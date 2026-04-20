package dev.anvilcraft.resource.anisum.feat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.anvilcraft.resource.anisum.Anisum;
import dev.anvilcraft.resource.anisum.AnisumConfig;
import dev.anvilcraft.resource.anisum.extension.IReloadableServerResourcesExtension;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@EventBusSubscriber
public class AnisumConfigManager extends SimplePreparableReloadListener<Map<ResourceLocation, AnisumConfig>> {
    private static final Gson GSON = new GsonBuilder().create();
    @Getter
    private final AnisumLootTablesLoader lootTablesLoader = new AnisumLootTablesLoader();
    @Getter
    private Map<ResourceLocation, AnisumConfig> configs = new HashMap<>();

    public AnisumConfigManager() {
    }

    @SubscribeEvent
    public static void addServerReloadListener(AddReloadListenerEvent event) {
        ReloadableServerResources serverResources = event.getServerResources();
        AnisumConfigManager manager = ((IReloadableServerResourcesExtension) serverResources).anisum$getConfigManager();
        event.addListener(manager);
    }

    @Override
    protected Map<ResourceLocation, AnisumConfig> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        SortedMap<ResourceLocation, JsonElement> jsonElementSortedMap = new TreeMap<>();
        SimpleJsonResourceReloadListener.scanDirectory(
            resourceManager,
            Anisum.MOD_ID,
            AnisumConfigManager.GSON,
            jsonElementSortedMap
        );
        SortedMap<ResourceLocation, AnisumConfig> sortedmap = new TreeMap<>();
        jsonElementSortedMap.forEach((id, json) -> {
            DataResult<Pair<AnisumConfig, JsonElement>> result = AnisumConfig.CODEC.decode(JsonOps.INSTANCE, json);
            if (result.isError()) {
                return;
            }
            sortedmap.put(id, result.getOrThrow().getFirst());
        });
        return Collections.synchronizedMap(sortedmap);
    }

    @Override
    protected void apply(Map<ResourceLocation, AnisumConfig> anisumConfig, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.configs = anisumConfig;
        Anisum.LOGGER.info("Loaded {} anisum configs", anisumConfig.size());
    }
}
