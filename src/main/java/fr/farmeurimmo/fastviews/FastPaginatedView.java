package fr.farmeurimmo.fastviews;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.*;
import java.util.function.Consumer;

public class FastPaginatedView extends FastView {
    
    private final List<PaginatedItem> items;
    private final List<Integer> availableSlots;
    private final boolean maskEnabled;
    private final Set<Integer> maskSlots;
    private int currentPage;
    private ItemStack previousButton;
    private ItemStack nextButton;
    private int previousSlot = -1;
    private int nextSlot = -1;
    
    public FastPaginatedView(int rows, Component title) {
        this(rows, title, false);
    }
    
    public FastPaginatedView(int rows, Component title, boolean maskEnabled) {
        super(rows, title);
        this.items = new ArrayList<>();
        this.availableSlots = new ArrayList<>();
        this.maskEnabled = maskEnabled;
        this.maskSlots = new HashSet<>();
        this.currentPage = 0;
        
        initializeDefaultSlots();
        setupDefaultButtons();
    }
    
    public FastPaginatedView(InventoryType type, Component title) {
        this(type, title, false);
    }
    
    public FastPaginatedView(InventoryType type, Component title, boolean maskEnabled) {
        super(type, title);
        this.items = new ArrayList<>();
        this.availableSlots = new ArrayList<>();
        this.maskEnabled = maskEnabled;
        this.maskSlots = new HashSet<>();
        this.currentPage = 0;
        
        initializeDefaultSlots();
        setupDefaultButtons();
    }
    
    private void initializeDefaultSlots() {
        int size = getSize();
        
        if (maskEnabled) {
            for (int i = 10; i <= 16; i++) {
                if (i < size) availableSlots.add(i);
            }
            for (int i = 19; i <= 25; i++) {
                if (i < size) availableSlots.add(i);
            }
            for (int i = 28; i <= 34; i++) {
                if (i < size) availableSlots.add(i);
            }

            if (size >= 45) {
                previousSlot = 39;
                nextSlot = 41;
            }
        } else {
            for (int i = 0; i < size; i++) {
                availableSlots.add(i);
            }

            if (size >= 45) {
                previousSlot = size - 9;
                nextSlot = size - 1;
                availableSlots.remove(Integer.valueOf(previousSlot));
                availableSlots.remove(Integer.valueOf(nextSlot));
            }
        }
    }
    
    private void setupDefaultButtons() {
        this.previousButton = ItemStack.builder(Material.ARROW)
                .customName(Component.text("← Previous Page"))
                .build();
        
        this.nextButton = ItemStack.builder(Material.ARROW)
                .customName(Component.text("Next Page →"))
                .build();
    }
    
    public FastPaginatedView setMask(String pattern) {
        if (!maskEnabled) {
            throw new IllegalStateException("Mask is not enabled for this inventory");
        }

        availableSlots.clear();
        maskSlots.clear();

        String[] lines = pattern.split("\n");
        int rows = Math.min(lines.length, getSize() / 9);

        for (int row = 0; row < rows; row++) {
            String line = lines[row];
            int cols = Math.min(line.length(), 9);

            for (int col = 0; col < cols; col++) {
                char c = line.charAt(col);
                int slot = row * 9 + col;

                if (c == '#' && slot < getSize()) {
                    availableSlots.add(slot);
                    maskSlots.add(slot);
                }
            }
        }

        return this;
    }

    public FastPaginatedView setMask(int... slots) {
        if (!maskEnabled) {
            throw new IllegalStateException("Mask is not enabled for this inventory");
        }
        
        availableSlots.clear();
        maskSlots.clear();
        
        for (int slot : slots) {
            if (slot >= 0 && slot < getSize()) {
                availableSlots.add(slot);
                maskSlots.add(slot);
            }
        }
        
        return this;
    }
    
    public FastPaginatedView addPaginatedItem(ItemStack item) {
        return addPaginatedItem(item, null);
    }
    
    public FastPaginatedView addPaginatedItem(ItemStack item, Consumer<InventoryPreClickEvent> clickHandler) {
        items.add(new PaginatedItem(item, clickHandler));
        return this;
    }
    
    public FastPaginatedView setNavigationButtons(ItemStack previous, ItemStack next) {
        this.previousButton = previous;
        this.nextButton = next;
        return this;
    }
    
    public FastPaginatedView setNavigationSlots(int previousSlot, int nextSlot) {
        if (this.previousSlot != -1) {
            availableSlots.add(this.previousSlot);
        }
        if (this.nextSlot != -1) {
            availableSlots.add(this.nextSlot);
        }
        
        this.previousSlot = previousSlot;
        this.nextSlot = nextSlot;

        availableSlots.remove(Integer.valueOf(previousSlot));
        availableSlots.remove(Integer.valueOf(nextSlot));
        
        return this;
    }
    
    public void nextPage() {
        if (hasNextPage()) {
            currentPage++;
            update();
        }
    }
    
    public void previousPage() {
        if (hasPreviousPage()) {
            currentPage--;
            update();
        }
    }
    
    public FastPaginatedView setPage(int page) {
        if (page >= 0 && page < getMaxPages()) {
            currentPage = page;
            update();
        }
        return this;
    }
    
    private void update() {
        for (int slot : availableSlots) {
            removeItem(slot);
        }

        int itemsPerPage = availableSlots.size();
        int startIndex = currentPage * itemsPerPage;
        
        for (int i = 0; i < itemsPerPage && startIndex + i < items.size(); i++) {
            PaginatedItem paginatedItem = items.get(startIndex + i);
            int slot = availableSlots.get(i);
            setItem(slot, paginatedItem.item(), paginatedItem.clickHandler());
        }

        updateNavigationButtons();
    }
    
    private void updateNavigationButtons() {
        if (previousSlot != -1) {
            if (hasPreviousPage()) {
                setItem(previousSlot, previousButton, event -> previousPage());
            } else {
                setItem(previousSlot, ItemStack.AIR);
            }
        }
        
        if (nextSlot != -1) {
            if (hasNextPage()) {
                setItem(nextSlot, nextButton, event -> nextPage());
            } else {
                setItem(nextSlot, ItemStack.AIR);
            }
        }
    }
    
    @Override
    public void open(Player player) {
        update();
        super.open(player);
    }
    
    public boolean hasNextPage() {
        return currentPage < getMaxPages() - 1;
    }
    
    public boolean hasPreviousPage() {
        return currentPage > 0;
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public int getMaxPages() {
        if (availableSlots.isEmpty()) return 1;
        return (int) Math.ceil((double) items.size() / availableSlots.size());
    }
    
    public int getTotalItems() {
        return items.size();
    }
    
    public boolean isMaskEnabled() {
        return maskEnabled;
    }
    
    public Set<Integer> getMaskSlots() {
        return new HashSet<>(maskSlots);
    }
    
    public List<Integer> getAvailableSlots() {
        return new ArrayList<>(availableSlots);
    }
    
    private record PaginatedItem(ItemStack item, Consumer<InventoryPreClickEvent> clickHandler) {}
}
