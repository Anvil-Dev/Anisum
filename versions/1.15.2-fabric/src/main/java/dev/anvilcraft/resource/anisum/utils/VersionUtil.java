package dev.anvilcraft.resource.anisum.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.anvilcraft.resource.anisum.Anisum;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public interface VersionUtil {
    static ServerLevel overworld(@Nonnull MinecraftServer server) {
        return server.getLevel(DimensionType.OVERWORLD);
    }

    static @Nonnull ResourceLocation fromNamespaceAndPath(@Nonnull String namespace, @Nonnull String path) {
        return new ResourceLocation(namespace, path);
    }

    static void itemStackFromJson(@Nonnull JsonElement element, @Nonnull AtomicReference<ItemStack> icon) {
        if (!element.isJsonObject()) return;
        JsonObject object = element.getAsJsonObject();
        ItemStack stack = new ItemStack(Registry.ITEM.get(new ResourceLocation(object.get("name").getAsString())));
        if (object.has("tag")) {
            String tagString = object.get("tag").getAsString();
            try {
                CompoundTag compoundTag = TagParser.parseTag(tagString);
                stack.setTag(compoundTag);
            } catch (CommandSyntaxException e) {
                Anisum.LOGGER.error(e.getMessage(), e);
            }
        }
        icon.set(stack);
    }

    static Component literal(String key) {
        return new TextComponent(key);
    }

    static Component translatable(String key, Object... args) {
        return new TranslatableComponent(key, args);
    }

    static Predicate<String> listResourcePredicate(String pathEnd) {
        return stringx -> stringx.endsWith(pathEnd);
    }
}
