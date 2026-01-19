package dev.anvilcraft.resource.anisum.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.DimensionType;

import javax.annotation.Nonnull;

public interface VersionUtil {
    static ServerLevel overworld(@Nonnull MinecraftServer server) {
        return server.getLevel(DimensionType.OVERWORLD);
    }

    static @Nonnull ResourceLocation fromNamespaceAndPath(@Nonnull String namespace, @Nonnull String path) {
        return new ResourceLocation(namespace, path);
    }
}
