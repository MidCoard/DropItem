package com.focess.dropitem.listener;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.item.CraftDropItem;

public class EntityDeathListener implements Listener {
	public EntityDeathListener(final DropItem dropItem) {
	}

	@EventHandler
	public void onEntityDeath(final EntityDeathEvent event) {
		try {
			final Entity dropItem = event.getEntity();
			if (CraftDropItem.include(dropItem))
				CraftDropItem.remove(dropItem, DropItemDeathEvent.DeathCause.DEATH);
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in calling Evnet EntityDeathEvent.");
		}
	}
}
