package dev.anvilcraft.resource.anisum.item;

import dev.anvilcraft.resource.anisum.utils.AnisumItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class AnisumCreativeModeTab extends CreativeModeTab {
    private final Set<AnisumItem> items;
    private final Set<ItemStack> displayItems = new LinkedHashSet<>();

    protected AnisumCreativeModeTab(Builder builder, Set<AnisumItem> items) {
        super(builder);
        this.items = items;
    }

    @Override
    public boolean hasAnyItems() {
        return true;
    }

    @Override
    public Collection<ItemStack> getDisplayItems() {
        if (this.displayItems.isEmpty()) {
            this.items.forEach(item -> this.displayItems.add(item.itemStack()));
        }
        return this.displayItems;
    }
}
