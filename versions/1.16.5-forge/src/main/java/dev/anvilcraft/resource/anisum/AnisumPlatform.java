package dev.anvilcraft.resource.anisum;

import dev.anvilcraft.resource.anisum.utils.LootTablesUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nonnull;

@Mod(Anisum.MOD_ID)
public class AnisumPlatform {
    public AnisumPlatform() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
        modEventBus.addListener(this::addReloadListener);
    }

    @SubscribeEvent
    public void serverStarted(@Nonnull FMLServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        AnisumPlatform.lootLoaded(server, server.getLootTables());
    }

    @SubscribeEvent
    public void addReloadListener(@Nonnull AddReloadListenerEvent event) {
        event.addListener(new LootTablesUtil.PreparableAnisumConfigListener());
    }

    public static void endDataPackReload(
        @Nonnull MinecraftServer server,
        @Nonnull LootTables lootTables
    ) {
        AnisumPlatform.lootLoaded(server, lootTables);
    }

    private static void lootLoaded(@Nonnull MinecraftServer server, @Nonnull LootTables lootTables) {
        LootTablesUtil.lootLoaded(server, lootTables);
        LootTablesUtil.createTabs((id, icon, items) -> new CreativeModeTab(id.toString().replace(":", ".")) {
                @Override
                public ItemStack makeIcon() {
                    return icon.get();
                }

                @Override
                public void fillItemList(NonNullList<ItemStack> stacks) {
                    super.fillItemList(stacks);
                    items.accept(stacks);
                }
            }
        );
    }
}
