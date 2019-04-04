package com.focess.dropitem.runnable;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.event.HopperGottenEvent;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.EntityDropItem;
import com.focess.dropitem.util.AnxiCode;
import com.focess.dropitem.util.DropItemUtil;
import com.focess.dropitem.util.NMSManager;

public class DropItemRunnable extends BukkitRunnable {
    private static int anxiCode;
    private final DropItem drop;

    public DropItemRunnable(final DropItem dropItem) {
        this.drop = dropItem;
        DropItemRunnable.anxiCode = AnxiCode.getCode(DropItemRunnable.class, this.drop);
    }

    @Override
    public void run() {
        try {
            for (final EntityDropItem dropItem : CraftDropItem.getDropItems(DropItemRunnable.anxiCode)) {
                final Location location = dropItem.getLocation();
                location.setY(location.getY() - DropItemUtil.getHeight());
                if (location.getBlock().getType().equals(Material.HOPPER)) {
                    final Hopper hopper = (Hopper) location.getBlock().getState();
                    final HopperGottenEvent event = new HopperGottenEvent(dropItem.getItemStack(), hopper);
                    this.drop.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        CraftDropItem.remove(dropItem, DropItemDeathEvent.DeathCause.HOPPER_GOTTEN);
                        hopper.getInventory().addItem(new ItemStack[] { dropItem.getItemStack() });
                        return;
                    }
                   
                }
                if (DropItemUtil.checkPickForm("w-move")) {
                    final List<Entity> entities = dropItem.getNearbyEntities(0.75, 0.75, 0.75);
                    for (final Entity entity : entities)
                        if (entity instanceof Player) {
                            final Player player = (Player) entity;
                            if (DropItemUtil.naturalSpawn() || DropItemUtil.allowedPlayer()
                                    || DropItemUtil.checkPlayerPermission(player))
                                DropItemUtil.fillPlayerInventory(player, CraftDropItem.getDropItem(entity));
                        }
                    if (dropItem.isDead())
                        return;
                }
                final Location loc = dropItem.getLocation();
                if (loc.getBlock().getType().equals(Material.AIR)) {
                    final Location loc2 = loc.clone();
                    loc2.setY(loc2.getY() - 1);
                    if (loc2.getBlock().getType().equals(Material.AIR))
                        NMSManager.setNBTBoolean(dropItem.getEntity(), "NoGravity", false);
                    else {
                        // NBT is slower than teleport
                        NMSManager.setNBTBoolean(dropItem.getEntity(), "NoGravity", true);
                        loc.setY((loc.getBlockY() - 1) + DropItemUtil.getHeight());
                        dropItem.teleport(loc);
                    }
                }
                final List<Entity> entities = dropItem.getNearbyEntities(2, 2, 2);
                boolean flag = true;
                for (final Entity entity : entities)
                    if ((entity instanceof Player) && DropItemUtil.showItemInfo()) {
                        dropItem.setCustomNameVisible(true);
                        flag = false;
                        break;
                    }
                if (flag)
                    dropItem.setCustomNameVisible(false);
            }
        } catch (final Exception e) {
            Debug.debug(e, "Something wrong in running Runnable DropItemRunnable.");
        }
    }
}
