package dev.anvilcraft.resource.anisum.integration.jei;

import dev.anvilcraft.resource.anisum.Anisum;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@JeiPlugin
public class AnisumJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return Anisum.of("jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        for (Item item : BuiltInRegistries.ITEM) {
            registration.registerSubtypeInterpreter(item, new DatapackItemSubtypeInterpreter());
        }
    }

    static class DatapackItemSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
        @Override
        public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
            CustomModelData modelData = ingredient.get(DataComponents.CUSTOM_MODEL_DATA);
            if (modelData != null) return modelData;
            return ResourceLocation.withDefaultNamespace("item");
        }

        @Override
        public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
            return Objects.toString(this.getSubtypeData(ingredient, context));
        }
    }
}
