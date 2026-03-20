package dev.anvilcraft.resource.anisum.extension;

import net.minecraft.world.level.storage.loot.LootPool;

import java.util.List;

public interface ILootTableExtension {
    default List<LootPool> anisum$getPools() {
        throw new AssertionError("Not implemented");
    }
}
