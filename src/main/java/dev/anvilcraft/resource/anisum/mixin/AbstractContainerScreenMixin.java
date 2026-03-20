package dev.anvilcraft.resource.anisum.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Iterator;
import java.util.List;

@Mixin(AbstractContainerScreen.class)
abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen {
    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @WrapOperation(
        method = "renderTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;"
                     + "getTooltipFromContainerItem("
                     + "Lnet/minecraft/world/item/ItemStack;"
                     + ")Ljava/util/List;"
        )
    )
    private List<Component> renderTooltip(AbstractContainerScreen<T> instance, ItemStack stack, Operation<List<Component>> original) {
        List<Component> components = original.call(instance, stack);
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
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
            Component component1 = this.minecraft.options.advancedItemTooltips ? components.removeLast() : null;
            components.add(component);
            if (component1 != null) {
                components.add(component1);
            }
        }
        return components;
    }
}
