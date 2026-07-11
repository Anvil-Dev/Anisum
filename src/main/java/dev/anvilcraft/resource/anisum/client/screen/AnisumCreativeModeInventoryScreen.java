package dev.anvilcraft.resource.anisum.client.screen;

import dev.anvilcraft.resource.anisum.mixin.client.AbstractContainerMenuAccessor;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;

@Slf4j
public class AnisumCreativeModeInventoryScreen extends CreativeModeInventoryScreen {
    public static final Identifier INVENTORY_LOCATION = Identifier.withDefaultNamespace(
        "textures/gui/container/creative_inventory/tab_inventory.png");
    private static final int RIGHT_PANEL_GAP = 8;
    private static final int RIGHT_PANEL_WIDTH = 195;
    private static final int RIGHT_PANEL_HEIGHT = 166;

    // Right panel origin X relative to leftPos
    private static final int RP_ORIGIN_X = 195 + RIGHT_PANEL_GAP; // 203

    // Slot positions matching vanilla CreativeModeInventoryScreen INVENTORY tab layout,
    // shifted to the right panel (+203 on X). See selectTab() lines 599-639.
    private static final int ARMOR_HELMET_X = RP_ORIGIN_X + 54; // 257, inv 39
    private static final int ARMOR_HELMET_Y = 6;
    private static final int ARMOR_CHEST_X = RP_ORIGIN_X + 54; // 257, inv 38
    private static final int ARMOR_CHEST_Y = 33;
    private static final int ARMOR_LEGS_X = RP_ORIGIN_X + 108; // 311, inv 37
    private static final int ARMOR_LEGS_Y = 6;
    private static final int ARMOR_BOOTS_X = RP_ORIGIN_X + 108; // 311, inv 36
    private static final int ARMOR_BOOTS_Y = 33;

    private static final int OFFHAND_X = RP_ORIGIN_X + 35; // 238, inv 40
    private static final int OFFHAND_Y = 20;

    // Main inventory: x = RP_ORIGIN_X + 9 + col*18 = 212 + col*18, y = 54 + row*18
    private static final int MAIN_INV_X = RP_ORIGIN_X + 9; // 212, inv 9-35
    private static final int MAIN_INV_Y = 54;

    // Hotbar: x = 212 + col*18, y = 112
    private static final int HOTBAR_X = RP_ORIGIN_X + 9; // 212, inv 0-8
    private static final int HOTBAR_Y = 112;

    // Destroy slot — matches vanilla INVENTORY tab at (173, 112)
    private static final int DESTROY_SLOT_X = RP_ORIGIN_X + 173; // 376
    private static final int DESTROY_SLOT_Y = HOTBAR_Y; // 112

    /**
     * Dummy container for the destroy slot (clears carried/player items).
     */
    private static final SimpleContainer DUMMY_CONTAINER = new SimpleContainer(1);
    private final Slot destroySlot = new Slot(DUMMY_CONTAINER, 0, DESTROY_SLOT_X, DESTROY_SLOT_Y);

    // Empty-slot background sprites for armor and offhand
    private static final Field HAS_CLICKED_OUTSIDE_FIELD;
    private static final Field SEARCH_BOX_FIELD;
    private static final Field EFFECTS;

    static {
        try {
            HAS_CLICKED_OUTSIDE_FIELD = CreativeModeInventoryScreen.class.getDeclaredField("hasClickedOutside");
            HAS_CLICKED_OUTSIDE_FIELD.setAccessible(true);

            SEARCH_BOX_FIELD = CreativeModeInventoryScreen.class.getDeclaredField("searchBox");
            SEARCH_BOX_FIELD.setAccessible(true);
            EFFECTS = CreativeModeInventoryScreen.class.getDeclaredField("effects");
            EFFECTS.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to access CreativeModeInventoryScreen fields", e);
        }
    }

    public AnisumCreativeModeInventoryScreen(LocalPlayer player, FeatureFlagSet enabledFeatures, boolean displayOperatorCreativeTab) {
        super(player, enabledFeatures, displayOperatorCreativeTab);
        this.addRightPanelSlots(player);
        try {
            EFFECTS.set(this, new AnisumEffectsInInventory(this));
        } catch (IllegalAccessException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    protected void init() {
        super.init();

        // Center the dividing line between creative panel and right panel at screen center.
        // super.init() set leftPos = (width - 195) / 2; reposition so gap center = width / 2.
        int newLeftPos = this.width / 2 - 195 - RIGHT_PANEL_GAP / 2;
        int delta = newLeftPos - this.leftPos;
        this.leftPos = newLeftPos;

        // Reposition page navigation buttons (prev/next) added in super.init()
        for (var renderable : this.renderables) {
            if (renderable instanceof AbstractWidget widget) {
                widget.setX(widget.getX() + delta);
            }
        }

        // Reposition search box added in super.init()
        try {
            EditBox searchBox = (EditBox) SEARCH_BOX_FIELD.get(this);
            if (searchBox != null) {
                searchBox.setX(searchBox.getX() + delta);
            }
        } catch (IllegalAccessException ignored) {
        }
    }

    private void addRightPanelSlots(Player player) {
        InventoryMenu inventoryMenu = player.inventoryMenu;

        // Hotbar (player inv 0-8) — matches vanilla INVENTORY tab at x=9+col*18, y=112
        for (int col = 0; col < 9; col++) {
            this.addRightPanelSlot(
                inventoryMenu.getSlot(InventoryMenu.USE_ROW_SLOT_START + col),
                HOTBAR_X + col * 18,
                HOTBAR_Y
            );
        }

        // Main inventory (player inv 9-35, 3 rows × 9 cols) — matches vanilla at y=54+row*18
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int menuSlot = InventoryMenu.INV_SLOT_START + row * 9 + col;
                this.addRightPanelSlot(
                    inventoryMenu.getSlot(menuSlot),
                    MAIN_INV_X + col * 18,
                    MAIN_INV_Y + row * 18
                );
            }
        }

        // Armor 2×2 grid matching vanilla INVENTORY tab layout:
        //   (54,6) helmet  (108,6) leggings
        //   (54,33) chest  (108,33) boots
        this.addRightPanelSlot(inventoryMenu.getSlot(InventoryMenu.ARMOR_SLOT_START), ARMOR_HELMET_X, ARMOR_HELMET_Y);
        this.addRightPanelSlot(inventoryMenu.getSlot(InventoryMenu.ARMOR_SLOT_START + 1), ARMOR_CHEST_X, ARMOR_CHEST_Y);
        this.addRightPanelSlot(inventoryMenu.getSlot(InventoryMenu.ARMOR_SLOT_START + 2), ARMOR_LEGS_X, ARMOR_LEGS_Y);
        this.addRightPanelSlot(inventoryMenu.getSlot(InventoryMenu.ARMOR_SLOT_START + 3), ARMOR_BOOTS_X, ARMOR_BOOTS_Y);

        // Offhand — matches vanilla INVENTORY tab at (35, 20)
        this.addRightPanelSlot(inventoryMenu.getSlot(InventoryMenu.SHIELD_SLOT), OFFHAND_X, OFFHAND_Y);

        // Destroy slot — clears carried item / entire inventory
        ((AbstractContainerMenuAccessor) this.menu).anisum$addSlot(this.destroySlot);
    }

    private void addRightPanelSlot(Slot target, int x, int y) {
        InventorySlotWrapper wrapper = new InventorySlotWrapper(target, x, y);
        ((AbstractContainerMenuAccessor) this.menu).anisum$addSlot(wrapper);
    }

    @Override
    protected boolean hasClickedOutside(double mx, double my, int xo, int yo) {
        boolean superResult = super.hasClickedOutside(mx, my, xo, yo);
        if (!superResult) {
            return false;
        }

        int rightPanelStart = xo + this.imageWidth + RIGHT_PANEL_GAP;
        int rightPanelEnd = rightPanelStart + RIGHT_PANEL_WIDTH;
        boolean inRightPanel = mx >= rightPanelStart && my >= yo && mx < rightPanelEnd && my < yo + RIGHT_PANEL_HEIGHT;

        if (inRightPanel) {
            try {
                HAS_CLICKED_OUTSIDE_FIELD.setBoolean(this, false);
            } catch (IllegalAccessException ignored) {
            }
            return false;
        }

        return true;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        int rightX = this.leftPos + this.imageWidth + RIGHT_PANEL_GAP;
        int rightY = this.topPos;

        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            INVENTORY_LOCATION,
            rightX,
            rightY,
            0.0F,
            0.0F,
            this.imageWidth,
            this.imageHeight,
            256,
            256
        );
        if (this.minecraft.player == null) return;
        InventoryScreen.extractEntityInInventoryFollowsMouse(
            graphics,
            rightX + 73,
            rightY + 6,
            rightX + 105,
            rightY + 49,
            20,
            0.0625F,
            mouseX,
            mouseY,
            this.minecraft.player
        );
    }

    @Override
    public boolean showsActiveEffects() {
        return false;
    }

    @Override
    protected void slotClicked(@Nullable Slot slot, int slotId, int buttonNum, ContainerInput containerInput) {
        if (slot == this.destroySlot && this.minecraft.player != null) {
            if (containerInput == ContainerInput.QUICK_MOVE) {
                // Shift-click: clear entire player inventory
                for (int i = 0; i < this.minecraft.player.inventoryMenu.getItems().size(); i++) {
                    this.minecraft.player.inventoryMenu.getSlot(i).set(ItemStack.EMPTY);
                    if (this.minecraft.gameMode != null) {
                        this.minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, i);
                    }
                }
            } else {
                // Normal click: clear carried item
                this.menu.setCarried(ItemStack.EMPTY);
            }
            return;
        }

        LocalPlayer player = this.minecraft.player;

        if (
            containerInput == ContainerInput.QUICK_CRAFT && player != null
            && this.quickCraftSlots.stream().anyMatch(InventorySlotWrapper.class::isInstance)
        ) {
            int targetIndex = -999;
            if (slot instanceof InventorySlotWrapper wrapper) {
                targetIndex = wrapper.target.index;
            } else if (
                slot != null && slot.container == player.getInventory()
                && Inventory.isHotbarSlot(slot.getContainerSlot())
            ) {
                targetIndex = InventoryMenu.USE_ROW_SLOT_START + slot.getContainerSlot();
            }

            player.inventoryMenu.clicked(targetIndex, buttonNum, containerInput, player);
            if (AbstractContainerMenu.getQuickcraftHeader(buttonNum) == AbstractContainerMenu.QUICKCRAFT_HEADER_END) {
                player.inventoryMenu.broadcastChanges();
            }
            return;
        }

        // Preserve the creative screen's shift-click behavior for both hotbar copies.
        if (
            containerInput == ContainerInput.QUICK_MOVE && slot != null
            && player != null
            && slot.container == player.getInventory()
            && Inventory.isHotbarSlot(slot.getContainerSlot())
        ) {
            slot.set(ItemStack.EMPTY);
            player.inventoryMenu.broadcastChanges();
            return;
        }

        if (slot instanceof InventorySlotWrapper wrapper && player != null) {
            Slot target = wrapper.target;
            if (!target.mayPickup(player)) {
                return;
            }

            if (containerInput == ContainerInput.THROW && target.hasItem()) {
                ItemStack toDrop = target.remove(buttonNum == 0 ? 1 : target.getItem().getMaxStackSize());
                ItemStack remaining = target.getItem();
                player.drop(toDrop, true);
                if (this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleCreativeModeItemDrop(toDrop);
                    this.minecraft.gameMode.handleCreativeModeItemAdd(remaining, target.index);
                }
            } else {
                player.inventoryMenu.clicked(target.index, buttonNum, containerInput, player);
                player.inventoryMenu.broadcastChanges();
            }
            return;
        }

        super.slotClicked(slot, slotId, buttonNum, containerInput);
    }

    public static class AnisumEffectsInInventory extends EffectsInInventory {
        public AnisumEffectsInInventory(AbstractContainerScreen<?> screen) {
            super(screen);
        }

        @Override
        public boolean canSeeEffects() {
            return false;
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        }
    }

    /**
     * Presents an InventoryMenu slot at another position without changing its server slot index or behavior.
     */
    private static class InventorySlotWrapper extends Slot {
        private final Slot target;

        InventorySlotWrapper(Slot target, int x, int y) {
            super(target.container, target.getContainerSlot(), x, y);
            this.target = target;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return this.target.mayPlace(stack);
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            this.target.onTake(player, stack);
        }

        @Override
        public ItemStack getItem() {
            return this.target.getItem();
        }

        @Override
        public boolean hasItem() {
            return this.target.hasItem();
        }

        @Override
        public void setByPlayer(ItemStack stack, ItemStack previous) {
            this.target.setByPlayer(stack, previous);
        }

        @Override
        public void set(ItemStack stack) {
            this.target.set(stack);
        }

        @Override
        public void setChanged() {
            this.target.setChanged();
        }

        @Override
        public int getMaxStackSize() {
            return this.target.getMaxStackSize();
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return this.target.getMaxStackSize(stack);
        }

        @Override
        public @Nullable Identifier getNoItemIcon() {
            return this.target.getNoItemIcon();
        }

        @Override
        public ItemStack remove(int amount) {
            return this.target.remove(amount);
        }

        @Override
        public boolean mayPickup(Player player) {
            return this.target.mayPickup(player);
        }

        @Override
        public boolean isActive() {
            return this.target.isActive();
        }

        @Override
        public boolean allowModification(Player player) {
            return this.target.allowModification(player);
        }

        @Override
        public boolean isHighlightable() {
            return this.target.isHighlightable();
        }

        @Override
        public boolean isFake() {
            return this.target.isFake();
        }
    }
}
