package dev.anvilcraft.resource.anisum.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.anvilcraft.resource.anisum.utils.CreativeModeTabExtension;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CreativeModeInventoryScreen.class)
abstract class CreativeModeInventoryScreenMixin {
    @WrapOperation(
        method = "renderLabels",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/language/I18n;get(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"
        )
    )
    String renderLabels(
        String format,
        Object[] s,
        Operation<String> original,
        @Local(name = "creativeModeTab") CreativeModeTab creativeModeTab
    ) {
        return ((CreativeModeTabExtension) creativeModeTab).anisum$getDisplayName().getColoredString();
    }

    @WrapOperation(
        method = "renderLabels",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/language/I18n;get(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"
        )
    )
    String renderTooltip(
        String format,
        Object[] s,
        Operation<String> original,
        @Local(name = "creativeModeTab") CreativeModeTab creativeModeTab
    ) {
        return ((CreativeModeTabExtension) creativeModeTab).anisum$getDisplayName().getColoredString();
    }

    @WrapOperation(
        method = "renderLabels",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/language/I18n;get(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"
        )
    )
    String checkTabHovering(
        String format,
        Object[] s,
        Operation<String> original,
        @Local(name = "creativeModeTab") CreativeModeTab creativeModeTab
    ) {
        return ((CreativeModeTabExtension) creativeModeTab).anisum$getDisplayName().getColoredString();
    }
}
