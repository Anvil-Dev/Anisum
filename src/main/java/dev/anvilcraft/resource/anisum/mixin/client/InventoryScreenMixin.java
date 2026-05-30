package dev.anvilcraft.resource.anisum.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.anvilcraft.resource.anisum.client.AnisumClient;
import dev.anvilcraft.resource.anisum.client.screen.AnisumCreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InventoryScreen.class)
abstract class InventoryScreenMixin {
    @WrapOperation(
        method = "containerTick",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/flag/FeatureFlagSet;Z)"
                     + "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;"
        )
    )
    private CreativeModeInventoryScreen containerTick(
        LocalPlayer player,
        FeatureFlagSet enabledFeatures,
        boolean displayOperatorCreativeTab,
        Operation<CreativeModeInventoryScreen> original
    ) {
        if (!AnisumClient.CLIENT_CONFIG.placeSideBySideInventoryAndCreativeTabs) {
            return original.call(player, enabledFeatures, displayOperatorCreativeTab);
        }
        return new AnisumCreativeModeInventoryScreen(player, enabledFeatures, displayOperatorCreativeTab);
    }

    @WrapOperation(
        method = "init",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/flag/FeatureFlagSet;Z)"
                     + "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;"
        )
    )
    private CreativeModeInventoryScreen init(
        LocalPlayer player,
        FeatureFlagSet enabledFeatures,
        boolean displayOperatorCreativeTab,
        Operation<CreativeModeInventoryScreen> original
    ) {
        if (!AnisumClient.CLIENT_CONFIG.placeSideBySideInventoryAndCreativeTabs) {
            return original.call(player, enabledFeatures, displayOperatorCreativeTab);
        }
        return new AnisumCreativeModeInventoryScreen(player, enabledFeatures, displayOperatorCreativeTab);
    }
}
