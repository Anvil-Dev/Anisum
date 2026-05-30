package dev.anvilcraft.resource.anisum.mixin.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(ItemStack.class)
abstract class ItemStackMixin implements DataComponentHolder {
    @Inject(
        method = "getTooltipLines",
        at = @At("RETURN"),
        cancellable = true
    )
    private void getTooltipLines(
        Item.TooltipContext context,
        @Nullable Player player,
        TooltipFlag tooltipFlag,
        CallbackInfoReturnable<List<Component>> cir
    ) {
        List<Component> components = cir.getReturnValue();
        CustomData customData = this.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("id")) {
            String id = customData.copyTag().getStringOr("id", "");
            Iterator<Component> iterator = components.iterator();
            while (iterator.hasNext()) {
                Component component = iterator.next();
                if (component.getString().equals(id)) {
                    iterator.remove();
                    break;
                }
            }
            MutableComponent component = Component.literal(id).withStyle(ChatFormatting.DARK_GRAY);
            Component component1 = Minecraft.getInstance().options.advancedItemTooltips ? components.removeLast() : null;
            components.add(component);
            if (component1 != null) {
                components.add(component1);
            }
        }
        cir.setReturnValue(components);
    }
}
