package dev.anvilcraft.resource.anisum.integration.jei;

import dev.anvilcraft.resource.anisum.Anisum;
import dev.anvilcraft.resource.anisum.client.screen.AnisumCreativeModeInventoryScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

@JeiPlugin
public class AnisumJeiPlugin implements IModPlugin {

    /** Total width of the side-by-side layout: creative panel (195) + gap (8) + right panel (176). */
    private static final int SIDE_BY_SIDE_WIDTH = 195 + 8 + 176;

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

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        // Tell JEI the true width of the side-by-side layout so its item panel
        // is placed to the right of the right panel, avoiding overlap.
        registration.addGenericGuiScreenHandler(
            AnisumCreativeModeInventoryScreen.class,
            (AnisumCreativeModeInventoryScreen screen) -> new IGuiProperties() {
                @Override
                public Class<AnisumCreativeModeInventoryScreen> screenClass() {
                    return AnisumCreativeModeInventoryScreen.class;
                }

                @Override
                public int guiLeft() {
                    return screen.getLeftPos();
                }

                @Override
                public int guiTop() {
                    return screen.getTopPos();
                }

                @Override
                public int guiXSize() {
                    return SIDE_BY_SIDE_WIDTH;
                }

                @Override
                public int guiYSize() {
                    return screen.getImageHeight();
                }

                @Override
                public int screenWidth() {
                    return screen.width;
                }

                @Override
                public int screenHeight() {
                    return screen.height;
                }
            }
        );
    }
}
