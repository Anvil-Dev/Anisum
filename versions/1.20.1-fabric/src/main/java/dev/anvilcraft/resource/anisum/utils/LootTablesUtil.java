package dev.anvilcraft.resource.anisum.utils;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("SequencedCollectionMethodCanBeUsed")
public class LootTablesUtil {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Map<ResourceLocation, AnisumConfig> CONFIGS = new HashMap<>();
    private static final Map<ResourceLocation, List<Pair<ResourceLocation, ItemStack>>> LOOT_TABLE_RESULTS = new TreeMap<>();
    private static final Map<ResourceLocation, CreativeModeTab> TABS = new HashMap<>();

    public static void lootLoaded(MinecraftServer server, LootDataManager lootTables) {
        LOOT_TABLE_RESULTS.clear();
        Anisum.LOGGER.info("Processing loot tables");
        ServerLevel overworld = VersionUtil.overworld(server);
        LootContext context = new LootContext.Builder(new LootParams.Builder(overworld).create((new LootContextParamSet.Builder().build()))).create(null);
        Collection<ResourceLocation> keys = lootTables.getKeys(LootDataType.TABLE);
        for (ResourceLocation key : keys) {
            AnisumConfig config = CONFIGS.values()
                .stream()
                .sorted()
                .filter(config1 -> config1.includeNamespace(key))
                .findFirst()
                .orElseGet(() -> {
                    AnisumConfig anisumConfig = AnisumConfig.createInlineConfig(key);
                    CONFIGS.put(anisumConfig.location, anisumConfig);
                    return anisumConfig;
                });
            if (!config.include(key)) continue;
            if (config.inline && key.getPath().contains("/")) continue;
            try {
                LootTable lootTable = lootTables.getLootTable(key);
                LootTableAccessor tableAccessor = (LootTableAccessor) lootTable;
                List<LootPool> pools = ListArrayUtil.of(tableAccessor.getPools());
                if (pools.size() != 1) continue;
                LootPool pool = pools.get(0);
                LootPoolAccessor poolAccessor = (LootPoolAccessor) pool;
                List<LootPoolEntryContainer> entries = ListArrayUtil.of(poolAccessor.getEntries());
                if (entries.size() != 1) continue;
                LootPoolEntryContainer entry = entries.get(0);
                if (!(entry instanceof LootItem)) continue;
                lootTable.getRandomItems(
                    context,
                    stack -> LOOT_TABLE_RESULTS.computeIfAbsent(config.location, k -> new ArrayList<>()).add(Pair.of(key, stack))
                );
            } catch (Exception e) {
                Anisum.LOGGER.error("Error while processing loot table {}", key, e);
                throw e;
            }
        }
        LOOT_TABLE_RESULTS.forEach(
            (config, value) -> value
                .sort(
                    (pair1, pair2) -> CONFIGS.get(config)
                        .sort(pair1.getFirst(), pair2.getFirst())
                )
        );
    }

    public static void createTabs(CreativeModeTabFactory factory) {
        for (ResourceLocation configLocation : LOOT_TABLE_RESULTS.keySet()) {
            AnisumConfig config = CONFIGS.get(configLocation);
            if (TABS.get(configLocation) == null) {
                CreativeModeTab tab = factory.create(
                    configLocation,
                    () -> getIcon(configLocation),
                    items -> fillAllItems(config.location, items)
                );
                ((CreativeModeTabExtension) tab).anisum$setDisplayName(config.name);
                TABS.put(configLocation, tab);
            }
        }
    }

    public static ItemStack getIcon(ResourceLocation configLocation) {
        AnisumConfig config = CONFIGS.get(configLocation);
        if (config.icon != null) return config.icon;
        List<Pair<ResourceLocation, ItemStack>> pairs = LOOT_TABLE_RESULTS.getOrDefault(configLocation, new ArrayList<>());
        if (pairs.isEmpty()) return Items.BARREL.getDefaultInstance();
        return pairs.get(0).getSecond();
    }

    public static void fillAllItems(ResourceLocation configLocation, List<ItemStack> items) {
        List<Pair<ResourceLocation, ItemStack>> pairs = LOOT_TABLE_RESULTS.getOrDefault(configLocation, new ArrayList<>());
        items.addAll(
            pairs.stream()
                .map(Pair::getSecond)
                .collect(Collectors.toCollection(ArrayList::new))
        );
    }

    @FunctionalInterface
    public interface CreativeModeTabFactory {
        CreativeModeTab create(ResourceLocation id, Supplier<ItemStack> icon, Consumer<List<ItemStack>> items);
    }

    public static class PreparableAnisumConfigListener implements PreparableReloadListener {
        private final String directory = Anisum.MOD_ID;
        private static final int PATH_SUFFIX_LENGTH = ".json".length();

        @Override
        public CompletableFuture<Void> reload(
            PreparationBarrier preparationBarrier,
            ResourceManager resourceManager,
            ProfilerFiller profilerFiller,
            ProfilerFiller profilerFiller2,
            Executor executor,
            Executor executor2
        ) {
            return CompletableFuture.supplyAsync(
                () -> {
                    Map<ResourceLocation, AnisumConfig> map = Maps.newHashMap();

                    Anisum.LOGGER.info("Loading Anisum configs");
                    for (ResourceLocation resourceLocation : resourceManager.listResources(
                        Anisum.MOD_ID,
                        VersionUtil.listResourcePredicate(".json")
                    )
                    //#if MC>=11900
                    .keySet()
                    //#endif
                    ) {
                        Anisum.LOGGER.info("Loading Anisum config {}", resourceLocation);
                        String string = resourceLocation.getPath();
                        ResourceLocation resourceLocation2 = VersionUtil.fromNamespaceAndPath(
                            resourceLocation.getNamespace(),
                            string.substring(this.directory.length() + 1, string.length() - PATH_SUFFIX_LENGTH)
                        );

                        try {
                            for (Resource resource : resourceManager.getResourceStack(resourceLocation)) {
                                try {
                                    InputStream inputStream = resource.open();
                                    Exception throwable = null;

                                    try {
                                        Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                                        Exception throwable1 = null;

                                        try {
                                            JsonObject jsonObject = GsonHelper.fromJson(GSON, reader, JsonObject.class);
                                            map.computeIfAbsent(
                                                resourceLocation2, resourceLocationx -> {
                                                    // resource.getSourceName()
                                                    String[] split = resourceLocation2.getPath().split("/");
                                                    String fileName = split[split.length - 1];
                                                    Component name;
                                                    if (jsonObject.has("name")) {
                                                        name = Component.Serializer.fromJson(jsonObject.get("name"));
                                                    } else {
                                                        name = VersionUtil.translatable(String.format(
                                                            "itemGroup.anisum.%s",
                                                            fileName
                                                        ));
                                                    }
                                                    AtomicReference<ItemStack> icon = new AtomicReference<>(null);
                                                    if (jsonObject.has("icon")) {
                                                        VersionUtil.itemStackFromJson(jsonObject.get("icon"), icon);
                                                    }
                                                    List<String> include = new ArrayList<>();
                                                    if (jsonObject.has("include")) {
                                                        for (JsonElement element : jsonObject.getAsJsonArray("include")) {
                                                            include.add(element.getAsString());
                                                        }
                                                    }
                                                    List<String> sort = new ArrayList<>();
                                                    if (jsonObject.has("sort")) {
                                                        for (JsonElement element : jsonObject.getAsJsonArray("sort")) {
                                                            sort.add(element.getAsString());
                                                        }
                                                    }
                                                    return new AnisumConfig(
                                                        resourceLocation2,
                                                        name,
                                                        icon.get(),
                                                        Collections.unmodifiableList(include),
                                                        Collections.unmodifiableList(sort)
                                                    );
                                                }
                                            );
                                        } catch (Exception throwable2) {
                                            throwable1 = throwable2;
                                            // throw throwable2;
                                            Anisum.LOGGER.error(throwable2.getMessage(), throwable2);
                                        } finally {
                                            if (throwable1 != null) {
                                                try {
                                                    reader.close();
                                                } catch (Throwable throwable2) {
                                                    throwable1.addSuppressed(throwable2);
                                                }
                                            } else {
                                                reader.close();
                                            }
                                        }
                                    } catch (Exception throwable1) {
                                        throwable = throwable1;
                                        // throw throwable1;
                                        Anisum.LOGGER.error(throwable1.getMessage(), throwable1);
                                    } finally {
                                        if (throwable != null) {
                                            try {
                                                inputStream.close();
                                            } catch (Exception throwable1) {
                                                throwable.addSuppressed(throwable1);
                                            }
                                        } else {
                                            inputStream.close();
                                        }
                                    }
                                } catch (RuntimeException | IOException exception) {
                                    Anisum.LOGGER.error(
                                        "Couldn't read {} tag list {} from {} in data pack {}",
                                        this.directory,
                                        resourceLocation2,
                                        resourceLocation,
                                        resource.sourcePackId(),
                                        exception
                                    );
                                }
                                //#if MC<11900
                                //$$ finally {
                                //$$     IOUtils.closeQuietly(resource);
                                //$$ }
                                //#endif
                            }
                        } catch (Exception var59) {
                            Anisum.LOGGER.error(
                                "Couldn't read {} tag list {} from {}",
                                this.directory,
                                resourceLocation2,
                                resourceLocation,
                                var59
                            );
                        }
                    }
                    return map;
                }, executor
            ).thenCompose(preparationBarrier::wait).thenAcceptAsync(
                map -> {
                    LootTablesUtil.CONFIGS.clear();
                    LootTablesUtil.CONFIGS.putAll(map);
                    Anisum.LOGGER.info("Loaded {} Anisum configs", LootTablesUtil.CONFIGS.size());
                }, executor2
            );
        }
    }
}
