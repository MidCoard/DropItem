# DropItem - Let Your Dropped Items Real
## Description
DropItem is a plugin for Minecraft Servers which let the game world real.As you are using it, it makes your dropped item real like a small thing that really exists.Also it allows you to pick up items by right-clicking or sneaking.

DropItem requires Java 8 or higher. On older versions, the plugin won't work.

DropItem supports Minecraft versions which are higher than 1.8. On lower versions,the plugin won't work.

## For Developers
If you are developers and want to interact with the items in DropItem.You can find the following events and use them like any other events.

**DropItemDeathEvent**
It called when an item disappear(or we called it death).

**DropItemGottenEvent**
It called when an item was gotten by a player or a hopper.
It is different from DropItemDeathEvent.
Just like when a player got an item,first the DropItemGottenEvent called and next the DropItemDeathEvent called.

**DropItemSpawnEvent**
It called when an item was spawned.

**HopperGottenEvent**
Notice the DropItemGottenEvent.It called when a hopper got an item.

**PlayerGottenEvent**
Notice the DropItemGottenEvent.It called when a player got an item.
