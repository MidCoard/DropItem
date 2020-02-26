package com.focess.dropitem.listener;

import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.util.DropItemConfiguration;
import com.focess.dropitem.util.DropItemUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnDropItemListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        final ItemStack itemStack = event.getItemDrop().getItemStack();
        if (DropItemUtil.checkNull(itemStack.getItemMeta().getDisplayName())
                && DropItemConfiguration.checkBanItems(itemStack.getType()) && DropItemUtil.checkPlayerPermission(event.getPlayer()))
            CraftDropItem.spawnItem(event.getItemDrop());
    }

    @EventHandler
    public void onSpawnDropItem(final ItemSpawnEvent event) {
        final ItemStack itemStack = event.getEntity().getItemStack();
        if (DropItemConfiguration.isNaturalSpawn() && DropItemUtil.checkNull(itemStack.getItemMeta().getDisplayName())
                && DropItemConfiguration.checkBanItems(itemStack.getType()))
            CraftDropItem.spawnItem(event.getEntity());
    }
}
