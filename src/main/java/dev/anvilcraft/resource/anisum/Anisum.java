package dev.anvilcraft.resource.anisum;

import dev.anvilcraft.resource.anisum.utils.VersionUtil;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

public class Anisum {
    public static final String MOD_ID = "anisum";
    public static final Logger LOGGER = LogManager.getLogger();

    public static @Nonnull ResourceLocation location(@Nonnull String path) {
        return VersionUtil.fromNamespaceAndPath(MOD_ID, path);
    }
}
