package dev.anvilcraft.resource.anisum.utils;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.chat.MutableComponent;
//#if MC<11900
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
//#else
//$$ import net.minecraft.network.chat.Component;
//#endif
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
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

    static @Nonnull MutableComponent literal(@Nonnull String key) {
        //#if MC>=11900
        //$$ return Component.literal(key);
        //#else
        return new TextComponent(key);
        //#endif
    }

    static @Nonnull MutableComponent translatable(@Nonnull String key, Object... args) {
        //#if MC>=11900
        //$$ return Component.translatable(key, args);
        //#else
        return new TranslatableComponent(key, args);
        //#endif
    }

    //#if MC>=11900
    //$$ static @Nonnull Predicate<ResourceLocation> listResourcePredicate(@Nonnull String pathEnd) {
    //$$     return location -> location.getPath().endsWith(pathEnd);
    //$$ }
    //#else
    static @Nonnull Predicate<String> listResourcePredicate(@Nonnull String pathEnd) {
        return stringx -> stringx.endsWith(pathEnd);
    }
    //#endif
}
