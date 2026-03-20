package dev.anvilcraft.resource.anisum;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;

@Mod(Anisum.MOD_ID)
@EventBusSubscriber(modid = Anisum.MOD_ID)
public class Anisum {
    public static final String MOD_ID = "anisum";

    public Anisum(IEventBus modEventBus, ModContainer modContainer) {
    }

    public static Identifier of(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
