package dev.anvilcraft.resource.anisum.network.handler.client;

import dev.anvilcraft.resource.anisum.item.CreativeModeTabManager;
import dev.anvilcraft.resource.anisum.network.AnisumSyncStartPayload;
import dev.anvilcraft.resource.anisum.network.AnisumTabSyncPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class AnisumClientPayloadHandler {
    public static void handleTabSync(AnisumTabSyncPayload anisumTabSyncPayload, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            CreativeModeTabManager creativeModeTabManager = Minecraft.getInstance().anisum$getCreativeModeTabManager();
            creativeModeTabManager.addPayload(anisumTabSyncPayload);
            if (creativeModeTabManager.isSuccessful()) {
                creativeModeTabManager.end();
            }
        });
    }

    public static void handleSyncStart(AnisumSyncStartPayload anisumSyncStartPayload, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            CreativeModeTabManager creativeModeTabManager = Minecraft.getInstance().anisum$getCreativeModeTabManager();
            creativeModeTabManager.setCount(anisumSyncStartPayload.count());
            if (creativeModeTabManager.isSuccessful()) {
                creativeModeTabManager.end();
            }
        });
    }
}
