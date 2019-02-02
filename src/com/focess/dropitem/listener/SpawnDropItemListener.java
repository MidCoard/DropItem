package com.focess.dropitem.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import com.focess.dropitem.Debug;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.util.DropItemUtil;

public class SpawnDropItemListener implements Listener {
    
    
    @EventHandler
    public void onSpawnDropItem(ItemSpawnEvent event) {
        try {
            ItemStack itemStack = event.getEntity().getItemStack();
            if (DropItemUtil.naturalSpawn()&&DropItemUtil.checkNull(itemStack.getItemMeta().getDisplayName())&&DropItemUtil.checkBanItems(itemStack))
                CraftDropItem.spawnItem(event.getEntity());
        }
        catch (final Exception e) {
            Debug.debug(e, "Something wrong in calling Event ItemSpawnEvent.");
        }
    }
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerDropItem(final PlayerDropItemEvent event) {
		try {
			if (DropItemUtil.checkNull(event.getItemDrop().getItemStack().getItemMeta().getDisplayName())
					&& DropItemUtil.checkBanItems(event.getItemDrop().getItemStack())
					&& (DropItemUtil.checkPlayerPermission(event.getPlayer()) ||DropItemUtil.allowedPlayer()) && !DropItemUtil.naturalSpawn())
				CraftDropItem.spawnItem(event.getItemDrop());
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in calling Event PlayerDropItemEvent.");
		}
	}
}
