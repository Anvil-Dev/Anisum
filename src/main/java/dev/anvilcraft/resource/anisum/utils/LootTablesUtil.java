package dev.anvilcraft.resource.anisum.utils;

import com.mojang.datafixers.util.Pair;
import dev.anvilcraft.resource.anisum.Anisum;
import dev.anvilcraft.resource.anisum.mixin.LootPoolAccessor;
import dev.anvilcraft.resource.anisum.mixin.LootTableAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class LootTablesUtil {
    private static final Map<String, List<Pair<ResourceLocation, ItemStack>>> LOOT_TABLE_RESULTS = new HashMap<>();
    private static final Map<ResourceLocation, CreativeModeTab> TABS = new HashMap<>();

    public static void lootLoaded(@Nonnull MinecraftServer server, @Nonnull LootTables lootTables) {
        LOOT_TABLE_RESULTS.clear();
        LootContext context = new LootContext.Builder(server.overworld()).create(new LootContextParamSet.Builder().build());
        Set<ResourceLocation> ids = lootTables.getIds();
        for (ResourceLocation id : ids) {
            try {
                if (id.getPath().contains("/")) continue;
                LootTable lootTable = lootTables.get(id);
                LootTableAccessor tableAccessor = (LootTableAccessor) lootTable;
                LootPool[] pools = tableAccessor.getPools();
                if (pools.length != 1) continue;
                LootPool pool = pools[0];
                LootPoolAccessor poolAccessor = (LootPoolAccessor) pool;
                LootPoolEntryContainer[] entries = poolAccessor.getEntries();
                if (entries.length != 1) continue;
                LootPoolEntryContainer entry = entries[0];
                if (!(entry instanceof LootItem)) continue;
                lootTable.getRandomItems(
                    context,
                    stack -> LOOT_TABLE_RESULTS.computeIfAbsent(id.getNamespace(), k -> new ArrayList<>()).add(Pair.of(id, stack))
                );
            } catch (Exception e) {
                Anisum.LOGGER.error("Error while processing loot table {}", id, e);
                throw e;
            }
        }
        LOOT_TABLE_RESULTS.values().forEach(pairs -> pairs.sort(Comparator.comparing(a -> a.getFirst().toString())));
    }

    public static void createTabs(CreativeModeTabFactory factory) {
        for (String key : LOOT_TABLE_RESULTS.keySet()) {
            ResourceLocation location = Anisum.location(key);
            if (TABS.get(location) == null) {
                CreativeModeTab tab = factory.create(location, () -> getIcon(key), items -> fillAllItems(key, items));
                TABS.put(location, tab);
            }
        }
    }

    public static ItemStack getIcon(String key) {
        List<Pair<ResourceLocation, ItemStack>> pairs = LOOT_TABLE_RESULTS.getOrDefault(key, new ArrayList<>());
        if (pairs.isEmpty()) return Items.BARREL.getDefaultInstance();
        return pairs.get(0).getSecond();
    }

    public static void fillAllItems(@Nonnull String key, @Nonnull List<ItemStack> items) {
        List<Pair<ResourceLocation, ItemStack>> pairs = LOOT_TABLE_RESULTS.getOrDefault(key, new ArrayList<>());
        items.addAll(pairs.stream().map(Pair::getSecond).collect(Collectors.toCollection(ArrayList::new)));
    }

    @FunctionalInterface
    public interface CreativeModeTabFactory {
        CreativeModeTab create(ResourceLocation id, Supplier<ItemStack> icon, Consumer<List<ItemStack>> items);
    }
}
