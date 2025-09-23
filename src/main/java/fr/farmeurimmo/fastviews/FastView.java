package fr.farmeurimmo.fastviews;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import java.util.*;

public class FastView {

    private final Inventory inventory;
    private final Map<Integer, Consumer<InventoryPreClickEvent>> clickHandlers;
    private Consumer<InventoryCloseEvent> closeHandler;
    private EventNode<@NotNull InventoryEvent> eventNode;

    public FastView(InventoryType type, Component title) {
        this.inventory = new Inventory(type, title);
        this.clickHandlers = new HashMap<>();
    }

    public FastView(int rows, Component title) {
        this.inventory = new Inventory(getTypeFromRows(rows), title);
        this.clickHandlers = new HashMap<>();
    }

    private static InventoryType getTypeFromRows(int rows) {
        return switch (rows) {
            case 1 -> InventoryType.CHEST_1_ROW;
            case 2 -> InventoryType.CHEST_2_ROW;
            case 3 -> InventoryType.CHEST_3_ROW;
            case 4 -> InventoryType.CHEST_4_ROW;
            case 5 -> InventoryType.CHEST_5_ROW;
            case 6 -> InventoryType.CHEST_6_ROW;
            default -> throw new IllegalArgumentException("Rows must be between 1 and 6");
        };
    }

    public FastView setItem(int slot, ItemStack item, Consumer<InventoryPreClickEvent> clickHandler) {
        inventory.setItemStack(slot, item);
        if (clickHandler != null) {
            clickHandlers.put(slot, clickHandler);
        }
        return this;
    }

    public FastView setItem(int slot, ItemStack item) {
        return setItem(slot, item, null);
    }

    public FastView addItem(ItemStack item, Consumer<InventoryPreClickEvent> clickHandler) {
        int slot = findNextEmptySlot();
        if (slot != -1) {
            setItem(slot, item, clickHandler);
        }
        return this;
    }

    public FastView addItem(ItemStack item) {
        return addItem(item, null);
    }

    public FastView fill(ItemStack item, Consumer<InventoryPreClickEvent> clickHandler) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItemStack(i).isAir()) {
                setItem(i, item, clickHandler);
            }
        }
        return this;
    }

    public FastView fill(ItemStack item) {
        return fill(item, null);
    }

    public FastView border(ItemStack item, Consumer<InventoryPreClickEvent> clickHandler) {
        int size = inventory.getSize();
        int rows = size / 9;

        for (int i = 0; i < 9; i++) {
            setItem(i, item, clickHandler);
        }
        for (int i = size - 9; i < size; i++) {
            setItem(i, item, clickHandler);
        }
        for (int row = 1; row < rows - 1; row++) {
            setItem(row * 9, item, clickHandler);
            setItem(row * 9 + 8, item, clickHandler);
        }
        return this;
    }

    public FastView border(ItemStack item) {
        return border(item, null);
    }

    public FastView fillBorder(ItemStack item, Consumer<InventoryPreClickEvent> clickHandler) {
        ItemStack glass = ItemStack.builder(Material.GRAY_STAINED_GLASS_PANE)
                .customName(Component.empty())
                .build();
        return border(glass, clickHandler);
    }

    public FastView fillBorder() {
        return fillBorder(null, null);
    }

    public FastView removeItem(int slot) {
        inventory.setItemStack(slot, ItemStack.AIR);
        clickHandlers.remove(slot);
        return this;
    }

    public FastView clear() {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItemStack(i, ItemStack.AIR);
        }
        clickHandlers.clear();
        return this;
    }

    public FastView onClose(Consumer<InventoryCloseEvent> closeHandler) {
        this.closeHandler = closeHandler;
        return this;
    }

    public void open(Player player) {
        eventNode = EventNode.type("fastview-" + inventory.getWindowId(),
                EventFilter.INVENTORY,
                (inventoryEvent, abstractInventory) -> inventory.getWindowId() == abstractInventory.getWindowId());

        eventNode.addListener(InventoryPreClickEvent.class, event -> {
            event.setCancelled(true);

            int slot = event.getSlot();
            Consumer<InventoryPreClickEvent> handler = clickHandlers.get(slot);

            if (handler != null) {
                handler.accept(event);
            }
        });

        eventNode.addListener(InventoryCloseEvent.class, event -> {
            MinecraftServer.getGlobalEventHandler().removeChild(eventNode);

            if (closeHandler != null) {
                closeHandler.accept(event);
            }
        });

        MinecraftServer.getGlobalEventHandler().addChild(eventNode);
        player.openInventory(inventory);
    }

    public void close() {
        inventory.getViewers().forEach(Player::closeInventory);
    }

    public void updateItem(int slot, ItemStack item) {
        inventory.setItemStack(slot, item);
    }

    private int findNextEmptySlot() {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItemStack(i).isAir()) {
                return i;
            }
        }
        return -1;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getSize() {
        return inventory.getSize();
    }

    public Set<Player> getViewers() {
        return inventory.getViewers();
    }

    public ItemStack getItem(int slot) {
        return inventory.getItemStack(slot);
    }

    public boolean hasClickHandler(int slot) {
        return clickHandlers.containsKey(slot);
    }

    public static FastPaginatedView paginated(int rows, Component title) {
        return new FastPaginatedView(rows, title);
    }

    public static FastPaginatedView paginated(int rows, Component title, boolean maskEnabled) {
        return new FastPaginatedView(rows, title, maskEnabled);
    }

    public static FastPaginatedView paginated(InventoryType type, Component title) {
        return new FastPaginatedView(type, title);
    }

    public static FastPaginatedView paginated(InventoryType type, Component title, boolean maskEnabled) {
        return new FastPaginatedView(type, title, maskEnabled);
    }
}
