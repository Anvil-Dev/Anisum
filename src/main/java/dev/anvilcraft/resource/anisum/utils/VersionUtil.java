package dev.anvilcraft.resource.anisum.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public interface VersionUtil {
    static ResourceLocation fromNamespaceAndPath(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    static MutableComponent literal(String key) {
        return Component.literal(key);
    }

    static MutableComponent translatable(String key, Object... args) {
        return Component.translatable(key, args);
    }

    @SafeVarargs
    @UnmodifiableView
    static <T> List<T> listOf(T... elements) {
        return List.of(elements);
    }
}
