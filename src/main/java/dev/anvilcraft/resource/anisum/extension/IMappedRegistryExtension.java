package dev.anvilcraft.resource.anisum.extension;

public interface IMappedRegistryExtension<T> {
    default void anisum$clear() {
        throw new AssertionError("Not implemented");
    }
}
