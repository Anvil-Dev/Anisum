package dev.anvilcraft.resource.anisum.integration.jei;

import dev.anvilcraft.resource.anisum.Anisum;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

@JeiPlugin
public class AnisumJeiPlugin implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return Anisum.of("jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        for (Item item : BuiltInRegistries.ITEM) {
            registration.registerSubtypeInterpreter(
                item, (ingredient, context) -> {
                    Identifier modelData = ingredient.get(DataComponents.ITEM_MODEL);
                    if (modelData != null) return modelData;
                    return Identifier.withDefaultNamespace("item");
                }
            );
        }
    }
}
