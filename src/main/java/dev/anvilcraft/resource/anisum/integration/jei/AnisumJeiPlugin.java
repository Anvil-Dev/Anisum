package dev.anvilcraft.resource.anisum.integration.jei;

import dev.anvilcraft.resource.anisum.Anisum;
import dev.anvilcraft.resource.anisum.client.screen.AnisumCreativeModeInventoryScreen;
import dev.anvilcraft.resource.anisum.client.tab.AnisumCreativeModeTab;
import dev.anvilcraft.resource.anisum.event.AnisumTabClearEvent;
import dev.anvilcraft.resource.anisum.event.AnisumTabLoadedEvent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@JeiPlugin
public class AnisumJeiPlugin implements IModPlugin {

    /**
     * Total width of the side-by-side layout: creative panel (195) + gap (8) + right panel (195).
     */
    private static final int SIDE_BY_SIDE_WIDTH = 195 + 8 + 195;
    private static final Set<Item> JEI_VANILLA_SUBTYPE_ITEMS = Set.of(
        Items.TIPPED_ARROW,
        Items.POTION,
        Items.SPLASH_POTION,
        Items.LINGERING_POTION,
        Items.ENCHANTED_BOOK,
        Items.LIGHT,
        Items.PAINTING,
        Items.GOAT_HORN,
        Items.FIREWORK_ROCKET,
        Items.FIREWORK_STAR,
        Items.SUSPICIOUS_STEW,
        Items.OMINOUS_BOTTLE,
        Items.SHIELD,
        Items.DECORATED_POT
    );
    private @Nullable IIngredientManager ingredientManager;

    @Override
    public Identifier getPluginUid() {
        return Anisum.of("jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (JEI_VANILLA_SUBTYPE_ITEMS.contains(item)) {
                continue;
            }
            registration.registerFromDataComponentTypes(item, DataComponents.ITEM_MODEL);
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
                    return Math.max(2, screen.width);
                }

                @Override
                public int screenHeight() {
                    return Math.max(2, screen.height);
                }
            }
        );
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        this.ingredientManager = jeiRuntime.getIngredientManager();
        NeoForge.EVENT_BUS.register(this);
    }

    @Override
    public void onRuntimeUnavailable() {
        NeoForge.EVENT_BUS.unregister(this);
        this.ingredientManager = null;
    }

    @SubscribeEvent
    public void onAnisumTabsCleared(AnisumTabClearEvent event) {
        IIngredientManager manager = this.ingredientManager;
        if (manager == null) {
            return;
        }
        manager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, getDisplayItems(event.getCreativeModeTabs()));
    }

    @SubscribeEvent
    public void onAnisumTabsLoaded(AnisumTabLoadedEvent event) {
        Collection<ItemStack> stacks = getDisplayItems(event.getCreativeModeTabs());
        IIngredientManager manager = this.ingredientManager;
        if (manager == null) {
            return;
        }
        manager.addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, stacks);
    }

    private static Set<ItemStack> getDisplayItems(Collection<AnisumCreativeModeTab> tabs) {
        Set<ItemStack> stacks = new HashSet<>();
        for (AnisumCreativeModeTab tab : tabs) {
            stacks.addAll(tab.getDisplayItems());
        }
        return stacks;
    }
}
