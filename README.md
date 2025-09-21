# FastViews

A simple and powerful Minestom inventory GUI wrapper, inspired
by [FastInv by MrMicky-FR](https://github.com/MrMicky-FR/FastInv).

FastViews makes creating interactive inventories easy
with a fluent API and built-in event handling.

## Requirements

- Java 25+
- Minestom 2025.09.13-1.21.8+

## Features

- Simple fluent API
- Automatic event handling
- Built-in border and fill utilities
- Flexible item placement
- Click and close event handlers
- Type-safe with Java 25 support

## TODO

- [ ] Implement pagination
- [ ] Support for drag and drop events

## Ideas

- [ ] Add support for custom inventory types (hopper, dispenser, etc.)

## Installation

### Gradle

#### Add the repository

```groovy
maven {
    name "fr.farmeurimmo"
    url "https://repo.farmeurimmo.fr/<repository>"
}
```

#### Add the dependency

```groovy
implementation "fr.farmeurimmo:MinestomFastViews:1.0.0"
```

### Maven

#### Add the repository

```xml

<repository>
    <id>fr.farmeurimmo.repo</id>
    <name>Maven Repository | Farmeurimmo</name>
    <url>https://repo.farmeurimmo.fr/</url>
</repository>
```

#### Add the dependency

```xml

<dependency>
    <groupId>fr.farmeurimmo</groupId>
    <artifactId>MinestomFastViews</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start

### Basic Usage

```java
// Create a simple inventory
FastView fastView = new FastView(6, Component.text("Ranks"));

fastView.

setItem(0,ItemStack.builder(Material.PAPER)
        .

customName(Component.text("Rank 1").

color(NamedTextColor.GREEN))
        .

build());

        fastView.open(player);
```

### Advanced Usage with Fluent API

```java
new FastView(6,Component.text("Shop"))
        .

fillBorder()  // Add gray glass border
        .

setItem(22,ItemStack.builder(Material.DIAMOND_SWORD).

build(),event ->{
        player.

sendMessage(Component.text("You bought a diamond sword!"));
        })
        .

setItem(20,ItemStack.builder(Material.BOW).

build(),event ->{
        player.

sendMessage(Component.text("You bought a bow!"));
        })
        .

onClose(event ->{
        player.

sendMessage(Component.text("Thanks for visiting!"));
        })
        .open(player);
```

## API Reference

### Constructors

#### `FastView(int rows, Component title)`

Creates a new FastView with the specified number of rows (1-6).

```java
FastView gui = new FastView(3, Component.text("My GUI"));
```

#### `FastView(InventoryType type, Component title)`

Creates a new FastView with a specific inventory type.

```java
FastView gui = new FastView(InventoryType.CHEST_3_ROW, Component.text("My GUI"));
```

### Item Management

#### `setItem(int slot, ItemStack item)`

Sets an item in the specified slot without a click handler.

```java
fastView.setItem(0,ItemStack.builder(Material.DIAMOND).

build());
```

#### `setItem(int slot, ItemStack item, Consumer<InventoryPreClickEvent> clickHandler)`

Sets an item in the specified slot with a click handler.

```java
fastView.setItem(0,item, event ->{
Player player = (Player) event.getPlayer();
    player.

sendMessage(Component.text("Item clicked!"));
        });
```

#### `addItem(ItemStack item)`

Adds an item to the next available slot.

```java
fastView.addItem(ItemStack.builder(Material.GOLD_INGOT).

build());
```

#### `addItem(ItemStack item, Consumer<InventoryPreClickEvent> clickHandler)`

Adds an item to the next available slot with a click handler.

```java
fastView.addItem(item, event ->{
        // Handle click
        });
```

#### `removeItem(int slot)`

Removes an item from the specified slot.

```java
fastView.removeItem(10);
```

#### `updateItem(int slot, ItemStack item)`

Updates an item in the specified slot (for already opened inventories).

```java
fastView.updateItem(5,ItemStack.builder(Material.EMERALD).

build());
```

### Layout Utilities

#### `fill(ItemStack item)`

Fills all empty slots with the specified item.

```java
fastView.fill(ItemStack.builder(Material.BLACK_STAINED_GLASS_PANE)
        .

customName(Component.empty())
        .

build());
```

#### `fill(ItemStack item, Consumer<InventoryPreClickEvent> clickHandler)`

Fills all empty slots with the specified item and click handler.

```java
fastView.fill(glassPane, event ->event.

setCancelled(true));
```

#### `border(ItemStack item)`

Creates a border around the inventory with the specified item.

```java
fastView.border(ItemStack.builder(Material.GRAY_STAINED_GLASS_PANE)
        .

customName(Component.empty())
        .

build());
```

#### `border(ItemStack item, Consumer<InventoryPreClickEvent> clickHandler)`

Creates a border with the specified item and click handler.

#### `fillBorder()`

Creates a border with gray stained glass panes (default styling).

```java
fastView.fillBorder();
```

#### `fillBorder(ItemStack item, Consumer<InventoryPreClickEvent> clickHandler)`

Creates a border with custom item and click handler.

### Event Handling

#### `onClose(Consumer<InventoryCloseEvent> closeHandler)`

Sets a handler for when the inventory is closed.

```java
fastView.onClose(event ->{
Player player = (Player) event.getPlayer();
    player.

sendMessage(Component.text("Thanks for visiting!"));
        });
```

### Inventory Control

#### `open(Player player)`

Opens the inventory for the specified player.

```java
fastView.open(player);
```

#### `close()`

Closes the inventory for all viewers.

```java
fastView.close();
```

#### `clear()`

Removes all items and click handlers from the inventory.

```java
fastView.clear();
```

### Information Methods

#### `getInventory()`

Returns the underlying Minestom inventory.

```java
Inventory inventory = fastView.getInventory();
```

#### `getSize()`

Returns the size of the inventory.

```java
int size = fastView.getSize();
```

#### `getViewers()`

Returns a set of players currently viewing the inventory.

```java
Set<Player> viewers = fastView.getViewers();
```

#### `getItem(int slot)`

Returns the item in the specified slot.

```java
ItemStack item = fastView.getItem(10);
```

#### `hasClickHandler(int slot)`

Checks if a slot has a click handler.

```java
boolean hasHandler = fastView.hasClickHandler(5);
```

## Examples

### Shop GUI

```java
public void openShop(Player player) {
    new FastView(6, Component.text("Shop").color(NamedTextColor.GOLD))
            .fillBorder()
            .setItem(10, ItemStack.builder(Material.DIAMOND_SWORD)
                    .customName(Component.text("Diamond Sword").color(NamedTextColor.AQUA))
                    .lore(Component.text("Price: 100 coins").color(NamedTextColor.GRAY))
                    .build(), event -> {
                // Handle purchase logic
                player.sendMessage(Component.text("You bought a Diamond Sword!"));
                player.closeInventory();
            })
            .setItem(12, ItemStack.builder(Material.BOW)
                    .customName(Component.text("Bow").color(NamedTextColor.AQUA))
                    .lore(Component.text("Price: 50 coins").color(NamedTextColor.GRAY))
                    .build(), event -> {
                // Handle purchase logic
                player.sendMessage(Component.text("You bought a Bow!"));
            })
            .setItem(49, ItemStack.builder(Material.BARRIER)
                    .customName(Component.text("Close").color(NamedTextColor.RED))
                    .build(), event -> player.closeInventory())
            .onClose(event -> {
                player.sendMessage(Component.text("Thanks for visiting our shop!"));
            })
            .open(player);
}
```

### Player Profile GUI

```java
public void openProfile(Player player) {
    FastView profile = new FastView(3, Component.text("Profile: " + player.getUsername()));

    profile.fillBorder()
            .setItem(13, ItemStack.builder(Material.PLAYER_HEAD)
                    .customName(Component.text(player.getUsername()).color(NamedTextColor.YELLOW))
                    .build())
            .setItem(11, ItemStack.builder(Material.DIAMOND)
                    .customName(Component.text("Statistics").color(NamedTextColor.BLUE))
                    .build(), event -> {
                // Open statistics GUI
            })
            .setItem(15, ItemStack.builder(Material.CHEST)
                    .customName(Component.text("Inventory").color(NamedTextColor.GREEN))
                    .build(), event -> {
                // Show inventory
            });

    profile.open(player);
}
```

### Confirmation Dialog

```java
public void openConfirmation(Player player, String action, Runnable onConfirm) {
    new FastView(3, Component.text("Confirm " + action))
            .fill(ItemStack.builder(Material.BLACK_STAINED_GLASS_PANE)
                    .customName(Component.empty())
                    .build())
            .setItem(11, ItemStack.builder(Material.GREEN_WOOL)
                    .customName(Component.text("✓ Confirm").color(NamedTextColor.GREEN))
                    .build(), event -> {
                onConfirm.run();
                player.closeInventory();
            })
            .setItem(15, ItemStack.builder(Material.RED_WOOL)
                    .customName(Component.text("✗ Cancel").color(NamedTextColor.RED))
                    .build(), event -> player.closeInventory())
            .open(player);
}
```

## License

This project is licensed under the MIT License.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
