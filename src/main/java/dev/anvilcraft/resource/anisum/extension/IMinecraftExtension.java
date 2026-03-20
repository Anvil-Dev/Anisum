package dev.anvilcraft.resource.anisum.extension;

import dev.anvilcraft.resource.anisum.item.CreativeModeTabManager;

public interface IMinecraftExtension {
    default CreativeModeTabManager anisum$getCreativeModeTabManager() {
        throw new AssertionError("Not implemented");
    }
}
