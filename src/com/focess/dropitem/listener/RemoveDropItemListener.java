package com.focess.dropitem.listener;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;

import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.event.DropItemDeathEvent.DeathCause;
import com.focess.dropitem.item.CraftDropItem;

public class RemoveDropItemListener implements Listener {

	@EventHandler
	public void onDropItemDamage(final EntityDamageEvent event) {
		if (CraftDropItem.include(event.getEntity()))
			if (event.getCause().equals(DamageCause.FIRE_TICK))
				CraftDropItem.remove(event.getEntity(), DeathCause.FIRE_TICK);
	}

	@EventHandler
	public void onDropItemDeath(final EntityDeathEvent event) {
		final Entity dropItem = event.getEntity();
		if (CraftDropItem.include(dropItem))
			CraftDropItem.remove(dropItem, DropItemDeathEvent.DeathCause.DEATH);
	}
}
