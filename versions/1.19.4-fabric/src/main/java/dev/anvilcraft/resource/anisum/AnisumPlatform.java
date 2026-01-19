package dev.anvilcraft.resource.anisum;

import dev.anvilcraft.resource.anisum.utils.LootTablesUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTables;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

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
        @Nonnull MinecraftServer server,
        @Nonnull CloseableResourceManager serverResourceManager,
        boolean success
    ) {
        LootTables lootTables = server.getLootTables();
        AnisumPlatform.lootLoaded(server, lootTables);
    }

    private static void lootLoaded(@Nonnull MinecraftServer server, @Nonnull LootTables lootTables) {
        LootTablesUtil.lootLoaded(server, lootTables);
        LootTablesUtil.createTabs(
            (id, icon, items) -> {
                ItemGroupEvents.modifyEntriesEvent(id).register(content -> {
                    List<ItemStack> stacks = new ArrayList<>();
                    items.accept(stacks);
                    for (ItemStack stack : stacks) {
                        content.accept(stack);
                    }
                });
                return FabricItemGroup
                    .builder(id)
                    .icon(icon)
                    .build();
            }
        );
    }

    public static class AnisumConfigListener extends LootTablesUtil.PreparableAnisumConfigListener
        implements IdentifiableResourceReloadListener {
        @Override
        public ResourceLocation getFabricId() {
            return Anisum.location(Anisum.MOD_ID);
        }
    }
}
