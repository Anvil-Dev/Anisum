package dev.anvilcraft.resource.anisum.client.screen;

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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

    private final List<Slot> rightPanelSlots = new ArrayList<>();

    public AnisumCreativeModeInventoryScreen(LocalPlayer player, FeatureFlagSet enabledFeatures, boolean displayOperatorCreativeTab) {
        super(player, enabledFeatures, displayOperatorCreativeTab);
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

        addRightPanelSlots();
    }

    private void addRightPanelSlots() {
        // Remove previously added right panel slots (handles resize)
        this.menu.slots.removeAll(rightPanelSlots);
        rightPanelSlots.clear();
        if (this.minecraft.player == null) return;
        Inventory playerInv = this.minecraft.player.getInventory();

        // Remove vanilla hotbar slots (menu indices 45-53) on first init only.
        // Slot.x/y are final in 1.21.5, so we replace them entirely.
        // On resize, removeAll(rightPanelSlots) above already removed them.
        if (this.menu.slots.size() > 45) {
            for (int i = 0; i < 9; i++) {
                this.menu.slots.remove(45);
            }
        }

        // Hotbar (player inv 0-8) — matches vanilla INVENTORY tab at x=9+col*18, y=112
        for (int col = 0; col < 9; col++) {
            Slot slot = new Slot(playerInv, col, HOTBAR_X + col * 18, HOTBAR_Y);
            slot.index = this.menu.slots.size();
            this.menu.slots.add(slot);
            rightPanelSlots.add(slot);
        }

        // Main inventory (player inv 9-35, 3 rows × 9 cols) — matches vanilla at y=54+row*18
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int invIndex = 9 + row * 9 + col;
                Slot slot = new Slot(playerInv, invIndex, MAIN_INV_X + col * 18, MAIN_INV_Y + row * 18);
                slot.index = this.menu.slots.size();
                this.menu.slots.add(slot);
                rightPanelSlots.add(slot);
            }
        }

        // Armor 2×2 grid matching vanilla INVENTORY tab layout:
        //   (54,6) helmet  (108,6) leggings
        //   (54,33) chest  (108,33) boots
        Player player = this.minecraft.player;

        FilteredArmorSlot helmetSlot = new FilteredArmorSlot(playerInv, 39, EquipmentSlot.HEAD, player, ARMOR_HELMET_X, ARMOR_HELMET_Y);
        helmetSlot.setBackground(InventoryMenu.EMPTY_ARMOR_SLOT_HELMET);
        helmetSlot.index = this.menu.slots.size();
        this.menu.slots.add(helmetSlot);
        rightPanelSlots.add(helmetSlot);

        FilteredArmorSlot chestSlot = new FilteredArmorSlot(playerInv, 38, EquipmentSlot.CHEST, player, ARMOR_CHEST_X, ARMOR_CHEST_Y);
        chestSlot.setBackground(InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE);
        chestSlot.index = this.menu.slots.size();
        this.menu.slots.add(chestSlot);
        rightPanelSlots.add(chestSlot);

        FilteredArmorSlot legsSlot = new FilteredArmorSlot(playerInv, 37, EquipmentSlot.LEGS, player, ARMOR_LEGS_X, ARMOR_LEGS_Y);
        legsSlot.setBackground(InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS);
        legsSlot.index = this.menu.slots.size();
        this.menu.slots.add(legsSlot);
        rightPanelSlots.add(legsSlot);

        FilteredArmorSlot bootsSlot = new FilteredArmorSlot(playerInv, 36, EquipmentSlot.FEET, player, ARMOR_BOOTS_X, ARMOR_BOOTS_Y);
        bootsSlot.setBackground(InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS);
        bootsSlot.index = this.menu.slots.size();
        this.menu.slots.add(bootsSlot);
        rightPanelSlots.add(bootsSlot);

        // Offhand — matches vanilla INVENTORY tab at (35, 20)
        Slot offhandSlot = new Slot(playerInv, 40, OFFHAND_X, OFFHAND_Y);
        offhandSlot.setBackground(InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
        offhandSlot.index = this.menu.slots.size();
        this.menu.slots.add(offhandSlot);
        rightPanelSlots.add(offhandSlot);

        // Destroy slot — clears carried item / entire inventory
        destroySlot.index = this.menu.slots.size();
        this.menu.slots.add(destroySlot);
        rightPanelSlots.add(destroySlot);
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
    public void containerTick() {
        super.containerTick();
        if (this.minecraft.player != null) {
            this.minecraft.player.inventoryMenu.broadcastChanges();
        }
    }

    @Override
    protected void slotClicked(@Nullable Slot slot, int slotId, int buttonNum, ContainerInput containerInput) {
        if (slot == this.destroySlot && this.minecraft.player != null) {
            if (containerInput == ContainerInput.QUICK_MOVE) {
                // Shift-click: clear entire player inventory
                for (int i = 0; i < this.minecraft.player.inventoryMenu.getItems().size(); i++) {
                    this.minecraft.player.inventoryMenu.getSlot(i).set(ItemStack.EMPTY);
                    this.minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, i);
                }
            } else {
                // Normal click: clear carried item
                this.menu.setCarried(ItemStack.EMPTY);
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
     * Armor slot that filters items by equipment type — only helmets in helmet slot, etc.
     * Matches vanilla {@code InventoryMenu.ArmorSlot} behavior.
     */
    private static class FilteredArmorSlot extends Slot {
        private final Player player;
        private final EquipmentSlot equipmentSlot;

        FilteredArmorSlot(Inventory inventory, int slotIndex, EquipmentSlot equipmentSlot, Player player, int x, int y) {
            super(inventory, slotIndex, x, y);
            this.player = player;
            this.equipmentSlot = equipmentSlot;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return this.player.getEquipmentSlotForItem(stack) == this.equipmentSlot;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
