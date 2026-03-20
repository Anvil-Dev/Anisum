package dev.anvilcraft.resource.anisum.extension;

import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

import java.util.List;

public interface ILootPoolExtension {
    default List<LootPoolEntryContainer> anisum$getEntries() {
        throw new AssertionError("Not implemented");
    }
}
