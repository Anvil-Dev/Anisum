package dev.anvilcraft.resource.anisum.network;

import dev.anvilcraft.lib.v2.network.register.NetworkRegistrar;
import dev.anvilcraft.resource.anisum.Anisum;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Anisum.MOD_ID)
public class AnisumNetworks {
    public static final String VERSION = "1";

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(AnisumNetworks.VERSION);
        NetworkRegistrar.register(registrar.optional(), Anisum.MOD_ID);
    }
}
