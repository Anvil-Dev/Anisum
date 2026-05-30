package dev.anvilcraft.resource.anisum.client.extension;

import dev.anvilcraft.resource.anisum.client.tab.CreativeModeTabManager;

public interface IMinecraftExtension {
    default CreativeModeTabManager anisum$getCreativeModeTabManager() {
        throw new AssertionError("Not implemented");
    }
}
