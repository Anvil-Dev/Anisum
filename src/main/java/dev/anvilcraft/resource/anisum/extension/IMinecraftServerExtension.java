package dev.anvilcraft.resource.anisum.extension;

import dev.anvilcraft.resource.anisum.feat.AnisumConfigManager;

public interface IMinecraftServerExtension {
    default AnisumConfigManager anisum$getConfigManager() {
        throw new AssertionError("Not implemented");
    }
}
