package dev.anvilcraft.resource.anisum.item;

import dev.anvilcraft.resource.anisum.annotations.Side;
import dev.anvilcraft.resource.anisum.utils.SideDist;
import net.minecraft.world.item.CreativeModeTab;

@Side(SideDist.CLIENT)
public class AnisumCreativeModeTab extends CreativeModeTab {
    protected AnisumCreativeModeTab(Builder builder) {
        super(builder);
    }
}
