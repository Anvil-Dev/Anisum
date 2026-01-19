package dev.anvilcraft.resource.anisum.utils;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

public interface VersionUtil {
    static ServerLevel overworld(@Nonnull MinecraftServer server) {
        return server.overworld();
    }

    //#if MC>=11800 && FORGE
    //$$ @SuppressWarnings("removal")
    //#endif
    static @Nonnull ResourceLocation fromNamespaceAndPath(@Nonnull String namespace, @Nonnull String path) {
        return new ResourceLocation(namespace, path);
    }

    static void itemStackFromJson(@Nonnull JsonElement element, @Nonnull AtomicReference<ItemStack> icon) {
        ItemStack.CODEC.parse(JsonOps.INSTANCE, element)
            .result()
            .ifPresent(icon::set);
    }
}
