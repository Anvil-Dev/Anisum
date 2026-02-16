package dev.anvilcraft.resource.anisum.network;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ModCreativeModeTabSyncPacket {
    private final ResourceLocation id;
    private final Component displayName;
    private final ItemStack icon;
    private final List<ItemStack> items;

    public ModCreativeModeTabSyncPacket(ResourceLocation id, Component displayName, ItemStack icon, List<ItemStack> items) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        this.items = items;
    }

    public static ModCreativeModeTabSyncPacket decode(FriendlyByteBuf friendlyByteBuf) {
        ResourceLocation id = friendlyByteBuf.readResourceLocation();
        Component displayName = friendlyByteBuf.readComponent();
        ItemStack icon = friendlyByteBuf.readItem();
        int count = friendlyByteBuf.readVarInt();
        List<ItemStack> items = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            items.add(friendlyByteBuf.readItem());
        }
        return new ModCreativeModeTabSyncPacket(id, displayName, icon, items);
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeResourceLocation(this.id);
        friendlyByteBuf.writeComponent(this.displayName);
        friendlyByteBuf.writeItem(this.icon);
        friendlyByteBuf.writeVarInt(this.items.size());
        for (ItemStack itemStack : this.items) {
            friendlyByteBuf.writeItem(itemStack);
        }
    }
}
