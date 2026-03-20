package dev.anvilcraft.resource.anisum.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
abstract class CreativeModeInventoryScreenMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(GuiGraphics graphics, int p_281317_, int p_282770_, float p_281295_, CallbackInfo ci) {
        if (Minecraft.getInstance().anisum$getCreativeModeTabManager().isLoading()) {
            ci.cancel();
        }
    }
}
