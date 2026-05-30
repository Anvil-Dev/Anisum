package dev.anvilcraft.resource.anisum.data;

import dev.anvilcraft.lib.v2.config.ConfigData;
import dev.anvilcraft.resource.anisum.Anisum;
import dev.anvilcraft.resource.anisum.client.AnisumClientConfig;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class AnisumLanguageProvider extends LanguageProvider {
    public AnisumLanguageProvider(PackOutput output) {
        super(output, Anisum.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        ConfigData.readConfigClass(this, AnisumClientConfig.class);
    }
}
