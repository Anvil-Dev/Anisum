package dev.anvilcraft.resource.anisum;

import dev.anvilcraft.resource.anisum.utils.LootTablesUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.storage.loot.LootTables;

public class AnisumPlatform implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(AnisumPlatform::endDataPackReload);
        ServerLifecycleEvents.SERVER_STARTED.register(AnisumPlatform::serverStarted);
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new AnisumConfigListener());
    }

    private static void serverStarted(MinecraftServer server) {
        AnisumPlatform.lootLoaded(server, server.getLootTables());
    }


    private static void endDataPackReload(
        MinecraftServer server,
        ServerResources serverResourceManager,
        boolean success
    ) {
        LootTables lootTables = serverResourceManager.getLootTables();
        AnisumPlatform.lootLoaded(server, lootTables);
    }

    private static void lootLoaded(MinecraftServer server, LootTables lootTables) {
        LootTablesUtil.lootLoaded(server, lootTables);
        LootTablesUtil.createTabs((id, icon, items) -> FabricItemGroupBuilder.create(id).icon(icon).appendItems(items).build());
    }

    public static class AnisumConfigListener extends LootTablesUtil.PreparableAnisumConfigListener
        implements IdentifiableResourceReloadListener {
        @Override
        public ResourceLocation getFabricId() {
            return Anisum.location(Anisum.MOD_ID);
        }
    }
}
