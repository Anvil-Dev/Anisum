package dev.anvilcraft.resource.anisum.feat;

import dev.anvilcraft.resource.anisum.Anisum;
import dev.anvilcraft.resource.anisum.annotations.Side;
import dev.anvilcraft.resource.anisum.utils.SideDist;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Side(SideDist.SERVER)
@EventBusSubscriber(modid = Anisum.MOD_ID)
@Slf4j
public class AnisumLootTablesLoader {
    public static final Map<Identifier, Set<ItemStack>> ITEMS = new HashMap<>();

    public static void lootLoaded(MinecraftServer server) {
        Optional<? extends HolderLookup.RegistryLookup<LootTable>> lookup = server.reloadableRegistries()
            .lookup()
            .lookup(Registries.LOOT_TABLE);
        if (lookup.isEmpty()) return;
        log.info("Processing loot tables");
        var overworld = server.overworld();
        LootParams params = new LootParams.Builder(overworld).create(ContextKeySet.EMPTY);
        LootContext context = new LootContext.Builder(params).create(Optional.empty());
        Map<Identifier, ItemStack> itemStackMap = new HashMap<>();
        lookup.get().listElements().forEach(reference -> {
            ResourceKey<LootTable> key = reference.getKey();
            if (key == null) return;
            Identifier identifier = key.identifier();
            LootTable lootTable = reference.value();
            List<LootPool> lootPools = lootTable.anisum$getPools();
            if (lootPools.size() != 1) return;
            LootPool lootPool = lootPools.getFirst();
            List<LootPoolEntryContainer> entries = lootPool.anisum$getEntries();
            if (entries.size() != 1) return;
            LootPoolEntryContainer entry = entries.getFirst();
            if (!(entry instanceof LootItem)) return;
            lootTable.getRandomItems(context, stack -> itemStackMap.put(identifier, stack));
        });
        log.info("loaded loot table with count {}", itemStackMap.size());
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        MinecraftServer server = event.getPlayerList().getServer();
        ServerPlayer player = event.getPlayer();
        if (player != null && !server.isSingleplayerOwner(player.nameAndId())) return;
        AnisumLootTablesLoader.lootLoaded(server);
    }
}
