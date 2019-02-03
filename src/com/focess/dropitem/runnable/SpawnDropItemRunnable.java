package com.focess.dropitem.runnable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import com.focess.dropitem.item.CraftDropItem;

public class SpawnDropItemRunnable implements Runnable {

    private static List<Item> items = new CopyOnWriteArrayList<>();

    public static void addItem(final Item item) {
        SpawnDropItemRunnable.items.add(item);
    }

    @Override
    public void run() {
        for (final Item item : SpawnDropItemRunnable.items) {
            if (item.isDead()) {
                SpawnDropItemRunnable.items.remove(item);
                continue;
            }
            if (item.isOnGround()) {
                final ItemStack itemStack = item.getItemStack();
                final Location location = item.getLocation();
                item.remove();
                CraftDropItem.spawnItem(itemStack, location);
            }
        }
    }

}
