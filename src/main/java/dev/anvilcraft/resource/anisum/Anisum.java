package dev.anvilcraft.resource.anisum;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Anisum.MOD_ID)
@EventBusSubscriber(modid = Anisum.MOD_ID)
public class Anisum {
    public static final String MOD_ID = "anisum";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Anisum(IEventBus modEventBus, ModContainer modContainer) {
    }

    public static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
