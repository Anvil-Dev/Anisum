package dev.anvilcraft.resource.anisum.mixin.client;

import dev.anvilcraft.resource.anisum.client.screen.AnisumCreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin {

    /**
     * Prevent the INVENTORY (Survival Inventory) tab from being selected
     * in the side-by-side layout, since the player inventory is always visible on the right.
     */
    @Inject(method = "selectTab", at = @At("HEAD"), cancellable = true)
    private void anisum$blockInventoryTab(CreativeModeTab tab, CallbackInfo ci) {
        if ((Object) this instanceof AnisumCreativeModeInventoryScreen
            && tab.getType() == CreativeModeTab.Type.INVENTORY) {
            ci.cancel();
        }
    }
}
