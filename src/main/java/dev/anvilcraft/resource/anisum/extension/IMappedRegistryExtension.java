package dev.anvilcraft.resource.anisum.extension;

import dev.anvilcraft.resource.anisum.network.RegistryItemHolder;

public interface IMappedRegistryExtension<T> {
    default void anisum$clear() {
        throw new AssertionError("Not implemented");
    }
}
