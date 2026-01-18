package dev.anvilcraft.resource.anisum;

import dev.anvilcraft.resource.anisum.utils.LootTablesUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.world.level.storage.loot.LootTables;

import javax.annotation.Nonnull;

public class AnisumFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(AnisumFabric::endDataPackReload);
        ServerLifecycleEvents.SERVER_STARTED.register(AnisumFabric::serverStarted);
    }

    private static void serverStarted(MinecraftServer server) {
        AnisumFabric.lootLoaded(server, server.getLootTables());
    }


    private static void endDataPackReload(
        @Nonnull MinecraftServer server,
        @Nonnull ServerResources serverResourceManager,
        boolean success
    ) {
        LootTables lootTables = serverResourceManager.getLootTables();
        AnisumFabric.lootLoaded(server, lootTables);
    }

    private static void lootLoaded(@Nonnull MinecraftServer server, @Nonnull LootTables lootTables) {
        LootTablesUtil.lootLoaded(server, lootTables);
        LootTablesUtil.createTabs((id, icon, items) -> FabricItemGroupBuilder.create(id)
            .icon(icon)
            .appendItems(items)
            .build()
        );
    }
}
