package dev.anvilcraft.resource.anisum.client;

import dev.anvilcraft.resource.anisum.Anisum;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = Anisum.MOD_ID, dist = Dist.CLIENT)
public class AnisumClient {
    public AnisumClient(IEventBus modEventBus, ModContainer modContainer) {
    }
}
