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

public interface VersionUtil {
    static ServerLevel overworld(MinecraftServer server) {
        return server.overworld();
    }

    //#if MC>=11800 && FORGE
    //$$ @SuppressWarnings("removal")
    //#endif
    static ResourceLocation fromNamespaceAndPath(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }

    static void itemStackFromJson(JsonElement element, AtomicReference<ItemStack> icon) {
        ItemStack.CODEC.parse(JsonOps.INSTANCE, element)
            .result()
            .ifPresent(icon::set);
    }

    static MutableComponent literal(String key) {
        //#if MC>=11900
        //$$ return Component.literal(key);
        //#else
        return new TextComponent(key);
        //#endif
    }

    static MutableComponent translatable(String key, Object... args) {
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
    static Predicate<String> listResourcePredicate(String pathEnd) {
        return stringx -> stringx.endsWith(pathEnd);
    }
    //#endif
}
