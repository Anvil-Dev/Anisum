package dev.anvilcraft.resource.anisum.network;

import dev.anvilcraft.resource.anisum.Anisum;
import dev.anvilcraft.resource.anisum.network.handler.client.AnisumClientPayloadHandler;
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
        registrar.playToClient(
            AnisumSyncStartPayload.TYPE,
            AnisumSyncStartPayload.STREAM_CODEC,
            AnisumClientPayloadHandler::handleSyncStart
        );
        registrar.playToClient(
            AnisumTabSyncPayload.TYPE,
            AnisumTabSyncPayload.STREAM_CODEC,
            AnisumClientPayloadHandler::handleTabSync
        );
    }
}
