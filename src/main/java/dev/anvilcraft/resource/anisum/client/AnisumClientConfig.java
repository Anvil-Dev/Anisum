package dev.anvilcraft.resource.anisum.client;

import dev.anvilcraft.lib.v2.config.Comment;
import dev.anvilcraft.lib.v2.config.Config;
import dev.anvilcraft.resource.anisum.Anisum;
import net.neoforged.fml.config.ModConfig;

@Config(name = Anisum.MOD_ID, type = ModConfig.Type.CLIENT)
public class AnisumClientConfig {
    @Comment("§c[EXPERIMENTAL]§r Place Creative Mode Tabs and Inventory side by side.")
    public boolean placeSideBySideInventoryAndCreativeTabs = false;
}
