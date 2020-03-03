package com.focess.dropitem.runnable;

import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.EntityDropItem;
import com.focess.dropitem.util.DropItemUtil;
import com.focess.dropitem.util.NMSManager;
import com.focess.dropitem.util.Section;
import com.focess.dropitem.util.configuration.DropItemConfiguration;
import com.focess.dropitem.util.version.VersionUpdater;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class DropItemRunnable extends BukkitRunnable {
    private final DropItem drop;

    public DropItemRunnable(final DropItem dropItem) {
        this.drop = dropItem;
    }

    private void onCactusClean(final EntityDropItem dropItem) {
        if (dropItem.isDead())
            return;
        final Location location = dropItem.getFixLocation();
        if (location.getBlock().getType().equals(Material.CACTUS))
            CraftDropItem.remove(dropItem, DropItemDeathEvent.DeathCause.CACTUS_CLEAN);
    }

    private void onCheckCustomName(final EntityDropItem dropItem) {
        if (dropItem.isDead())
            return;
        if (DropItemConfiguration.isShowItemInfo()) {
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

    private int versionCheck;

    private void onCheckPosition(final EntityDropItem dropItem) {
        if (dropItem.isDead())
            return;
        final Location location = dropItem.getLocation().clone();
        if (location.getBlock().getType().equals(Material.AIR)) {
            location.add(0, -1, 0);
            if (location.getBlock().getType().equals(Material.AIR))
                NMSManager.setGravity(dropItem.getEntity(), true);
            else {
                NMSManager.setGravity(dropItem.getEntity(), false);
                location.setY(location.getBlockY() + DropItemConfiguration.getHeight());
                dropItem.teleport(location);
            }
        }
    }

    private void onHopperGotten(final EntityDropItem dropItem) {
        if (dropItem.isDead())
            return;
        final Location location = dropItem.getFixLocation();
        if (location.getBlock().getType().equals(Material.HOPPER))
            DropItemUtil.fillHopperInventory((Hopper) location.getBlock().getState(), dropItem);
    }

    @Override
    public void run() {
        if (DropItemConfiguration.isVersionCheck() && DropItemConfiguration.isCheckCycle()) {
            this.versionCheck++;
            if (this.versionCheck >= DropItemConfiguration.getVersionCheckCycle() * 2) {
                this.versionCheck = 0;
                this.drop.addThread(new Thread() {
                    public void run() {
                        VersionUpdater.checkForUpdate(DropItemRunnable.this.drop);
                        if (VersionUpdater.isNeedUpdated() && !VersionUpdater.isDownloaded() && DropItemConfiguration.isVersionDownload())
                            VersionUpdater.downloadNewVersion(DropItemRunnable.this.drop, this);
                    }
                }).start();
            }
        }
        Section.checkSection();
        for (final EntityDropItem dropItem : CraftDropItem.getDropItems()) {
            this.onCactusClean(dropItem);
            this.onFireClean(dropItem);
            this.onHopperGotten(dropItem);
            this.onCheckPosition(dropItem);
            this.onCheckCustomName(dropItem);
        }
    }

    private void onFireClean(final EntityDropItem dropItem) {
        if (dropItem.isDead())
            return;
        final Location location = dropItem.getFixLocation();
        if (location.getBlock().getType().equals(Material.FIRE))
            CraftDropItem.remove(dropItem, DropItemDeathEvent.DeathCause.FIRE_TICK);
    }
}
