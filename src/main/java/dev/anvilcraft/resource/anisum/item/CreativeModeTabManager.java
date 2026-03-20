package dev.anvilcraft.resource.anisum.item;

import com.mojang.blaze3d.platform.Window;
import dev.anvilcraft.resource.anisum.network.AnisumTabSyncPayload;
import dev.anvilcraft.resource.anisum.network.RegistryItemHolder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.MappedRegistry;
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

public class CreativeModeTabManager {
    private final List<AnisumTabSyncPayload> payloads = new ArrayList<>();
    @Getter
    private boolean loading = false;
    @Setter
    private int count = -1;

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
            List<RegistryItemHolder<CreativeModeTab>> tabs = new ArrayList<>();
            for (Identifier identifier : mappedTabRegistry.keySet()) {
                CreativeModeTab value = mappedTabRegistry.getValue(identifier);
                if (value == null || value instanceof AnisumCreativeModeTab) {
                    continue;
                }
                tabs.add(new RegistryItemHolder<>(identifier, value));
            }
            mappedTabRegistry.anisum$clear();
            for (RegistryItemHolder<CreativeModeTab> tab : tabs) {
                Registry.register(mappedTabRegistry, ResourceKey.create(Registries.CREATIVE_MODE_TAB, tab.identifier()), tab.value());
            }
            Minecraft minecraft = Minecraft.getInstance();
            for (AnisumTabSyncPayload payload : this.payloads) {
                AnisumCreativeModeTab tab = new AnisumCreativeModeTab(
                    CreativeModeTab.builder()
                        .title(payload.name())
                        .icon(payload::icon)
                        .withTabsBefore(CreativeModeTabs.TOOLS_AND_UTILITIES),
                    payload.items()
                );
                Registry.register(
                    mappedTabRegistry,
                    ResourceKey.create(Registries.CREATIVE_MODE_TAB, payload.identifier()),
                    tab
                );
            }
            mappedTabRegistry.freeze();
            //noinspection UnstableApiUsage
            CreativeModeTabRegistry.sortTabs();
            if (minecraft.screen instanceof CreativeModeInventoryScreen screen) {
                Window window = minecraft.getWindow();
                screen.init(window.getGuiScaledWidth(), window.getGuiScaledHeight());
            }
        }
        this.count = -1;
        this.payloads.clear();
        this.loading = false;
    }
}
