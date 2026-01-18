package dev.anvilcraft.resource.anisum.compat;

import net.minecraft.resources.ResourceLocation;

public interface ResourceLocationCompat {
    static ResourceLocation fromNamespaceAndPath(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    static ResourceLocation parse(String location) {
        return ResourceLocation.parse(location);
    }

    static ResourceLocation withDefaultNamespace(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }
}
