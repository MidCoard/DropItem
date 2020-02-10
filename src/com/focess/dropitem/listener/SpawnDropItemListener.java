package com.focess.dropitem.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.util.DropItemUtil;

public class SpawnDropItemListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDropItem(final PlayerDropItemEvent event) {
		if (DropItemUtil.checkNull(event.getItemDrop().getItemStack().getItemMeta().getDisplayName())
				&& DropItemUtil.checkBanItems(event.getItemDrop().getItemStack())
				&& (DropItemUtil.checkPlayerPermission(event.getPlayer()) || DropItemUtil.allowedPlayer())
				&& !DropItemUtil.naturalSpawn())
			CraftDropItem.spawnItem(event.getItemDrop());
	}

	@EventHandler
	public void onSpawnDropItem(final ItemSpawnEvent event) {
		final ItemStack itemStack = event.getEntity().getItemStack();
		if (DropItemUtil.naturalSpawn() && DropItemUtil.checkNull(itemStack.getItemMeta().getDisplayName())
				&& DropItemUtil.checkBanItems(itemStack))
			CraftDropItem.spawnItem(event.getEntity());
	}
}
