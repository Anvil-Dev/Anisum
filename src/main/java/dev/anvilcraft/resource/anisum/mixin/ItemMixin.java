package dev.anvilcraft.resource.anisum.mixin;

import dev.anvilcraft.resource.anisum.utils.VersionUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import javax.annotation.Nonnull;

@Mixin(Item.class)
abstract class ItemMixin {
    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void appendHoverText(
        @Nonnull ItemStack itemStack,
        Level level,
        @Nonnull List<Component> list,
        TooltipFlag tooltipFlag,
        CallbackInfo ci
    ) {
        CompoundTag tag = itemStack.getTag();
        if (tag == null || !tag.contains("id")) return;
        list.add(VersionUtil.literal(tag.getString("id")).withStyle(ChatFormatting.DARK_GRAY));
    }
}
