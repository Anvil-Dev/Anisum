package dev.anvilcraft.resource.anisum.client.screen;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.flag.FeatureFlagSet;

public class AnisumCreativeModeInventoryScreen extends CreativeModeInventoryScreen {
    public AnisumCreativeModeInventoryScreen(
        LocalPlayer player,
        FeatureFlagSet enabledFeatures,
        boolean displayOperatorCreativeTab
    ) {
        super(player, enabledFeatures, displayOperatorCreativeTab);
    }
}
