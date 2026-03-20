package dev.anvilcraft.resource.anisum.feat;

import com.mojang.serialization.JsonOps;
import dev.anvilcraft.resource.anisum.Anisum;
import dev.anvilcraft.resource.anisum.AnisumConfig;
import dev.anvilcraft.resource.anisum.annotations.Side;
import dev.anvilcraft.resource.anisum.utils.SideDist;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@EventBusSubscriber
@Side(SideDist.SERVER)
@Slf4j
public class AnisumConfigManager extends SimplePreparableReloadListener<Map<Identifier, AnisumConfig>> {
    @Getter
    private Map<Identifier, AnisumConfig> configs = new HashMap<>();
    private final ReloadableServerResources serverResources;
    private final FileToIdConverter CONFIG_LISTER = FileToIdConverter.json("anisum");

    public AnisumConfigManager(ReloadableServerResources serverResources) {
        this.serverResources = serverResources;
    }

    @SubscribeEvent
    public static void addServerReloadListener(AddServerReloadListenersEvent event) {
        ReloadableServerResources serverResources = event.getServerResources();
        AnisumConfigManager manager = serverResources.anisum$getConfigManager();
        event.addListener(Anisum.of("config"), manager);
    }

    @Override
    protected Map<Identifier, AnisumConfig> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        SortedMap<Identifier, AnisumConfig> sortedmap = new TreeMap<>();
        SimpleJsonResourceReloadListener.scanDirectory(
            resourceManager,
            CONFIG_LISTER,
            new ConditionalOps<>(this.serverResources.getRegistryLookup().createSerializationContext(JsonOps.INSTANCE), this.getContext()),
            AnisumConfig.CODEC,
            sortedmap
        );
        return Collections.synchronizedMap(sortedmap);
    }

    @Override
    protected void apply(Map<Identifier, AnisumConfig> anisumConfig, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.configs = anisumConfig;
        log.info("Loaded {} anisum configs", anisumConfig.size());
    }
}
