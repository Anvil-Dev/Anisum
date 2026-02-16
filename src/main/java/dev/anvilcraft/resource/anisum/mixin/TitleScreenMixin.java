package dev.anvilcraft.resource.anisum.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;

@Mixin(TitleScreen.class)
abstract class TitleScreenMixin {
    @Definition(id = "allowsMultiplayer", method = "Lnet/minecraft/client/Minecraft;allowsMultiplayer()Z")
    @Expression("? = ?.allowsMultiplayer()")
    @Inject(
        method = "createNormalMenuOptions",
        at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER)
    )
    void createNormalMenuOptions(int i, int j, CallbackInfo ci, @Local(name = "bl") @Nonnull LocalBooleanRef bl) {
        bl.set(true);
    }
}
