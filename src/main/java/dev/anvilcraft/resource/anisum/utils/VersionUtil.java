package dev.anvilcraft.resource.anisum.utils;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class VersionUtil {
    public static @Nonnull ResourceLocation fromNamespaceAndPath(@Nonnull String namespace, @Nonnull String path) {
        return new ResourceLocation(namespace, path);
    }
}
