package dev.anvilcraft.resource.anisum;

import dev.anvilcraft.resource.anisum.utils.LootTablesUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

@Mod(Anisum.MOD_ID)
public class AnisumPlatform {
    @SuppressWarnings("removal")
    public AnisumPlatform() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void serverStarted(@Nonnull ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        AnisumPlatform.lootLoaded(server, server.getLootTables());
    }

    @SubscribeEvent
    public void addReloadListener(@Nonnull AddReloadListenerEvent event) {
        Anisum.LOGGER.info("Add anisum config reload listener...");
        event.addListener(new LootTablesUtil.PreparableAnisumConfigListener());
    }

    public static void endDataPackReload(
        @Nonnull MinecraftServer server,
        @Nonnull LootTables lootTables
    ) {
        AnisumPlatform.lootLoaded(server, lootTables);
        LootTablesUtil.applyTabsToForge();
    }

    @SubscribeEvent
    public void buildTabContents(@Nonnull CreativeModeTabEvent.BuildContents event) {
        LootTablesUtil.buildTabContents(event);
    }

    private static void lootLoaded(@Nonnull MinecraftServer server, @Nonnull LootTables lootTables) {
        LootTablesUtil.lootLoaded(server, lootTables);
        LootTablesUtil.createTabs((configLocation, icon, items) -> new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0)
            .icon(icon)
            .displayItems((parameters, output) -> {
                List<ItemStack> stacks = new ArrayList<>();
                items.accept(stacks);
                output.acceptAll(stacks);
            })
            .build()
        );
    }
}
