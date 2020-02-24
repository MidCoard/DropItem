package com.focess.dropitem.runnable;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent;
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

    private void onCactusClean(final EntityDropItem dropItem) {
        if (dropItem.isDead())
            return;
        final Location location = dropItem.getLocation();
        location.setY(location.getY() - DropItemUtil.getHeight());
        if (location.getBlock().getType().equals(Material.CACTUS))
            CraftDropItem.remove(dropItem, DropItemDeathEvent.DeathCause.CACTUS_CLEAN);
    }

    private void onCheckCustomName(final EntityDropItem dropItem) {
        if (dropItem.isDead())
            return;
        if (DropItemUtil.showItemInfo()) {
            final List<Entity> entities = dropItem.getNearbyEntities(2, 2, 2);
            boolean flag = true;
            for (final Entity entity : entities)
                if (entity instanceof Player) {
                    dropItem.setCustomNameVisible(true);
                    flag = false;
                    break;
                }
            if (flag)
                dropItem.setCustomNameVisible(false);
        }
    }

    private void onCheckPickForm(final EntityDropItem dropItem) {
        if (dropItem.isDead())
            return;
        if (DropItemUtil.checkPickForm("w-move")) {
            final List<Entity> entities = dropItem.getNearbyEntities(0.75, 0.75, 0.75);
            for (final Entity entity : entities)
                if (entity instanceof Player) {
                    final Player player = (Player) entity;
                    if (DropItemUtil.naturalSpawn() || DropItemUtil.allowedPlayer()
                            || DropItemUtil.checkPlayerPermission(player))
                        DropItemUtil.fillPlayerInventory(player, dropItem);
                }
        }
    }

    private void onCheckPosition(final EntityDropItem dropItem) {
        if (dropItem.isDead())
            return;
        final Location loc = dropItem.getLocation();
        if (loc.getBlock().getType().equals(Material.AIR)) {
            final Location loc2 = loc.clone();
            loc2.setY(loc2.getY() - 1);
            if (loc2.getBlock().getType().equals(Material.AIR))
                ((ArmorStand)dropItem.getEntity()).setGravity(true);
            else {
                ((ArmorStand)dropItem.getEntity()).setGravity(false);
                loc.setY(loc.getBlockY() - 1 + DropItemUtil.getHeight());
                dropItem.teleport(loc);
            }
        }
    }

    private void onHopperGotten(final EntityDropItem dropItem) {
        if (dropItem.isDead())
            return;
        final Location location = dropItem.getLocation();
        location.setY(location.getY() - DropItemUtil.getHeight());
        if (location.getBlock().getType().equals(Material.HOPPER))
            DropItemUtil.fillHopperInventory((Hopper) location.getBlock().getState(), dropItem);
    }

    @Override
    public void run() {
        for (final EntityDropItem dropItem : CraftDropItem.getDropItems(DropItemRunnable.anxiCode)) {
            this.onCactusClean(dropItem);
            this.onHopperGotten(dropItem);
            this.onCheckPickForm(dropItem);
            this.onCheckPosition(dropItem);
            this.onCheckCustomName(dropItem);
        }
    }
}
