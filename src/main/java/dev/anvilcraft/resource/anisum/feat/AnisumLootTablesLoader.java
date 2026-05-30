package dev.anvilcraft.resource.anisum.feat;

import dev.anvilcraft.resource.anisum.Anisum;
import dev.anvilcraft.resource.anisum.AnisumConfig;
import dev.anvilcraft.resource.anisum.network.pyload.AnisumSyncStartPayload;
import dev.anvilcraft.resource.anisum.network.pyload.AnisumTabSyncPayload;
import dev.anvilcraft.resource.anisum.utils.AnisumItem;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@EventBusSubscriber(modid = Anisum.MOD_ID)
public class AnisumLootTablesLoader {
    public final Map<AnisumConfig, Set<AnisumItem>> items = new HashMap<>();

    public AnisumLootTablesLoader() {
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        MinecraftServer server = event.getPlayerList().getServer();
        ServerPlayer player = event.getPlayer();
        AnisumLootTablesLoader loader = server.anisum$getConfigManager().getLootTablesLoader();
        if (player != null) {
            loader.syncLoots(player);
        } else {
            loader.lootLoaded(server);
        }
    }

    @SubscribeEvent
    public static void onDatapackLoaded(LevelEvent.Load event) {
        LevelAccessor level = event.getLevel();
        if (level.isClientSide()) {
            return;
        }
        MinecraftServer server = level.getServer();
        if (server == null) {
            return;
        }
        AnisumLootTablesLoader loader = server.anisum$getConfigManager().getLootTablesLoader();
        loader.lootLoaded(server);
    }

    public void lootLoaded(MinecraftServer server) {
        this.items.clear();
        Optional<? extends HolderLookup.RegistryLookup<LootTable>> lookup = server.reloadableRegistries()
            .lookup()
            .lookup(Registries.LOOT_TABLE);
        if (lookup.isEmpty()) return;
        var overworld = server.overworld();
        AnisumConfigManager manager = server.anisum$getConfigManager();
        LootParams params = new LootParams.Builder(overworld).create(ContextKeySet.EMPTY);
        LootContext context = new LootContext.Builder(params).create(Optional.empty());
        lookup.get().listElements().forEach(reference -> {
            try {
                ResourceKey<LootTable> key = reference.getKey();
                if (key == null) return;
                Identifier identifier = key.identifier();
                if (identifier.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) return;
                LootTable lootTable = reference.value();
                List<LootPool> lootPools = lootTable.anisum$getPools();
                if (lootPools.size() != 1) return;
                LootPool lootPool = lootPools.getFirst();
                List<LootPoolEntryContainer> entries = lootPool.anisum$getEntries();
                if (entries.size() != 1) return;
                LootPoolEntryContainer entry = entries.getFirst();
                if (!(entry instanceof LootItem)) return;
                //noinspection deprecation
                lootTable.getRandomItemsRaw(context,stack -> {
                    for (AnisumConfig config : manager.getConfigs().values()) {
                        if (!config.include(identifier)) {
                            continue;
                        }
                        this.items.computeIfAbsent(
                            config, _ -> new TreeSet<>(
                                (i1, i2) -> config.sort(i1.identifier(), i2.identifier())
                            )
                        ).add(
                            new AnisumItem(identifier, stack.copy())
                        );
                    }
                });
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
            }
        });
        PacketDistributor.sendToAllPlayers(new AnisumSyncStartPayload(this.items.size()));
        for (Map.Entry<AnisumConfig, Set<AnisumItem>> entry : this.items.entrySet()) {
            AnisumConfig config = entry.getKey();
            Identifier identifier = config.location();
            PacketDistributor.sendToAllPlayers(new AnisumTabSyncPayload(
                identifier,
                config.name(),
                config.icon(),
                entry.getValue()
            ));
        }
    }

    public void syncLoots(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new AnisumSyncStartPayload(this.items.size()));
        for (Map.Entry<AnisumConfig, Set<AnisumItem>> entry : this.items.entrySet()) {
            AnisumConfig config = entry.getKey();
            Identifier identifier = config.location();
            PacketDistributor.sendToPlayer(
                player,
                new AnisumTabSyncPayload(
                    identifier,
                    config.name(),
                    config.icon(),
                    entry.getValue()
                )
            );
        }
    }
}
