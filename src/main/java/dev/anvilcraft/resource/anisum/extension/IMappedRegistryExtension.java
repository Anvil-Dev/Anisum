package dev.anvilcraft.resource.anisum.extension;

public interface IMappedRegistryExtension {
    default void anisum$clear() {
        throw new AssertionError("Not implemented");
    }
}
