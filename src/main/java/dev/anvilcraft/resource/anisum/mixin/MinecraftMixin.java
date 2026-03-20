package dev.anvilcraft.resource.anisum.mixin;

import dev.anvilcraft.resource.anisum.extension.IMinecraftExtension;
import dev.anvilcraft.resource.anisum.item.CreativeModeTabManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Minecraft.class)
abstract class MinecraftMixin implements IMinecraftExtension {
    @Unique
    public final CreativeModeTabManager anisum$creativeModeTabManager = new CreativeModeTabManager();

    @Override
    public CreativeModeTabManager anisum$getCreativeModeTabManager() {
        return this.anisum$creativeModeTabManager;
    }
}
