package dev.anvilcraft.resource.anisum.mixin;

import dev.anvilcraft.resource.anisum.utils.CreativeModeTabExtension;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CreativeModeTab.class)
abstract class CreativeModeTabMixin implements CreativeModeTabExtension {
    @Shadow
    public abstract String getName();

    @Unique
    private Component anisum$displayName = null;

    @Override
    public void anisum$setDisplayName(Component displayName) {
        this.anisum$displayName = displayName;
    }

    @Override
    public Component anisum$getDisplayName() {
        return this.anisum$displayName == null ? new TranslatableComponent(this.getName()) : this.anisum$displayName;
    }
}
