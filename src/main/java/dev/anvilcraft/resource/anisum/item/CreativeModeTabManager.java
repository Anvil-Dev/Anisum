package dev.anvilcraft.resource.anisum.item;

import com.mojang.blaze3d.platform.Window;
import dev.anvilcraft.resource.anisum.annotations.Side;
import dev.anvilcraft.resource.anisum.network.AnisumTabSyncPayload;
import dev.anvilcraft.resource.anisum.network.RegistryItemHolder;
import dev.anvilcraft.resource.anisum.utils.AnisumItem;
import dev.anvilcraft.resource.anisum.utils.SideDist;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.common.CreativeModeTabRegistry;

import java.util.ArrayList;
import java.util.List;

@Side(SideDist.CLIENT)
@Slf4j
public class CreativeModeTabManager {
    @Getter
    private boolean loading = false;
    @Setter
    private int count = -1;
    private final List<AnisumTabSyncPayload> payloads = new ArrayList<>();

    public void addPayload(AnisumTabSyncPayload payload) {
        this.payloads.add(payload);
    }

    public boolean isSuccessful() {
        if (this.count < 0) return false;
        return this.payloads.size() >= this.count;
    }

    public void end() {
        this.loading = true;
        Registry<CreativeModeTab> tabRegistry = BuiltInRegistries.CREATIVE_MODE_TAB;
        if (tabRegistry instanceof MappedRegistry<CreativeModeTab> mappedTabRegistry) {
            //noinspection deprecation
            mappedTabRegistry.unfreeze(true);
            List<RegistryItemHolder<CreativeModeTab>> removed = new ArrayList<>();
            for (Identifier identifier : mappedTabRegistry.keySet()) {
                CreativeModeTab value = mappedTabRegistry.getValue(identifier);
                if (value instanceof AnisumCreativeModeTab tab) {
                    removed.add(new RegistryItemHolder<>(identifier, tab));
                }
            }
            for (RegistryItemHolder<CreativeModeTab> tab : removed) {
                mappedTabRegistry.anisum$remove(tab);
            }
            for (AnisumTabSyncPayload payload : this.payloads) {
                mappedTabRegistry.register(
                    ResourceKey.create(Registries.CREATIVE_MODE_TAB, payload.identifier()),
                    new AnisumCreativeModeTab(
                        CreativeModeTab.builder()
                            .title(payload.name())
                            .icon(payload::icon)
                            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                            .displayItems((itemDisplayParameters, output) -> {
                                for (AnisumItem item : payload.items()) {
                                    output.accept(item.itemStack());
                                }
                            })
                    ),
                    RegistrationInfo.BUILT_IN
                );
            }
            mappedTabRegistry.freeze();
            //noinspection UnstableApiUsage
            CreativeModeTabRegistry.sortTabs();
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.screen instanceof CreativeModeInventoryScreen screen) {
                Window window = minecraft.getWindow();
                screen.init(window.getGuiScaledWidth(), window.getGuiScaledHeight());
            }
        }
        this.count = -1;
        this.payloads.clear();
        this.loading = false;
        log.info("Finished syncing creative mode tabs");
    }
}
