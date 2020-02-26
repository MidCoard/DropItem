package com.focess.dropitem.listener;

import com.focess.dropitem.event.DropItemDeathEvent.DeathCause;
import com.focess.dropitem.item.CraftDropItem;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;

public class RemoveDropItemListener implements Listener {

    @EventHandler
    public void onDropItemDamage(final EntityDamageEvent event) {
        if (CraftDropItem.include(event.getEntity())) {
            System.out.println(event.getCause());
            if (event.getCause().equals(DamageCause.FIRE_TICK))
                CraftDropItem.remove(event.getEntity(), DeathCause.FIRE_TICK);
            else if (event.getCause().equals(DamageCause.VOID))
                CraftDropItem.remove(event.getEntity(), DeathCause.VOID);
            else if (event.getCause().equals(DamageCause.BLOCK_EXPLOSION) || event.getCause().equals(DamageCause.ENTITY_EXPLOSION))
                CraftDropItem.remove(event.getEntity(), DeathCause.EXPLOSION);
            else event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropItemDeath(final EntityDeathEvent event) {
        final Entity dropItem = event.getEntity();
        if (CraftDropItem.include(dropItem))
            CraftDropItem.remove(dropItem, DeathCause.DEATH);
    }
}
