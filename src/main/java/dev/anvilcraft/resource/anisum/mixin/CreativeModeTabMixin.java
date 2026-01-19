package dev.anvilcraft.resource.anisum.mixin;

import dev.anvilcraft.resource.anisum.utils.CreativeModeTabExtension;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin implements CreativeModeTabExtension {
    @Mutable
    @Shadow
    @Final
    private Component displayName;

    @Override
    public void anisum$setDisplayName(Component displayName) {
        this.displayName = displayName;
    }
}
