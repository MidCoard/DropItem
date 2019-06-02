package com.focess.dropitem.runnable;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
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
                NMSManager.setNBTBoolean(dropItem.getEntity(), "NoGravity", false);
            else {
                // NBT is slower than teleport
                NMSManager.setNBTBoolean(dropItem.getEntity(), "NoGravity", true);
                loc.setY((loc.getBlockY() - 1) + DropItemUtil.getHeight());
                dropItem.teleport(loc);
            }
        }
    }

    private void onHopperGotten(final EntityDropItem dropItem) {
        if (dropItem.isDead())
            return;
        final Location location = dropItem.getLocation();
        location.setY(location.getY() - DropItemUtil.getHeight());
        if (location.getBlock().getType().equals(Material.HOPPER)) {
            final Hopper hopper = (Hopper) location.getBlock().getState();
            if (!hopper.getInventory().containsAtLeast(dropItem.getItemStack(),1) && hopper.getInventory().firstEmpty() == -1)
                return;
            Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER);
            for (int i = 0;i<hopper.getInventory().getContents().length;i++){
                if (hopper.getInventory().getItem(i) == null)
                    continue;
                ItemStack itemStack = hopper.getInventory().getItem(i).clone();
                if (itemStack != null)
                    inventory.setItem(i, itemStack);
            }
            Map<Integer, ItemStack> itemStacks = inventory.addItem(dropItem.getItemStack().clone());
            ItemStack item = dropItem.getItemStack().clone();
            if (!itemStacks.isEmpty())
                item.setAmount(item.getAmount() - itemStacks.get(0).getAmount());
            if (item.getAmount() == 0)
                return;
            final HopperGottenEvent event = new HopperGottenEvent(item, hopper);
            this.drop.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                CraftDropItem.remove(dropItem, DropItemDeathEvent.DeathCause.HOPPER_GOTTEN);
                hopper.getInventory().addItem(event.getItemStack());
                if (item.getAmount() != dropItem.getItemStack().getAmount())
                    CraftDropItem.spawnItem(itemStacks.get(0), dropItem.getLocation().clone().add(0,1,0), false);
            }
        }
    }

    @Override
    public void run() {
        try {
            for (final EntityDropItem dropItem : CraftDropItem.getDropItems(DropItemRunnable.anxiCode)) {
                this.onHopperGotten(dropItem);
                this.onCheckPickForm(dropItem);
                this.onCheckPosition(dropItem);
                this.onCheckCustomName(dropItem);
            }
        } catch (final Exception e) {
            Debug.debug(e, "Something wrong in running Runnable DropItemRunnable.");
        }
    }
}
