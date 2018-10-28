package com.focess.dropitem.runnable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
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

public class DropItemRunnable extends BukkitRunnable {
    private static int anxiCode;
    private final DropItem drop;

    public DropItemRunnable(final DropItem dropItem) {
        this.drop = dropItem;
        anxiCode = AnxiCode.getCode(DropItemRunnable.class, this.drop);
    }

    @Override
    public void run() {
        try {
            for (EntityDropItem dropItem : CraftDropItem.getDropItems(anxiCode)) {
                final ItemStack head = dropItem.getHelmet();
                final ItemStack chest = dropItem.getChestplate();
                final ItemStack leg = dropItem.getLeggings();
                final ItemStack boots = dropItem.getBoots();
                final ItemStack hand = dropItem.getItemInHand();
                if ((head != null) && (head.getType().compareTo(Material.AIR) != 0)) {
                    if (DropItemUtil.checkBanItems(head))
                        CraftDropItem.spawnItem(head, dropItem.getLocation());
                    dropItem.setHelmet(new ItemStack(Material.AIR));
                }
                if ((chest != null) && (chest.getType().compareTo(Material.AIR) != 0)) {
                    if (DropItemUtil.checkBanItems(chest))
                        CraftDropItem.spawnItem(chest, dropItem.getLocation());
                    dropItem.setChestplate(new ItemStack(Material.AIR));
                }
                if ((leg != null) && (leg.getType().compareTo(Material.AIR) != 0)) {
                    if (DropItemUtil.checkBanItems(leg))
                        CraftDropItem.spawnItem(leg, dropItem.getLocation());
                    dropItem.setLeggings(new ItemStack(Material.AIR));
                }
                if ((boots != null) && (boots.getType().compareTo(Material.AIR) != 0)) {
                    if (DropItemUtil.checkBanItems(boots))
                        CraftDropItem.spawnItem(boots, dropItem.getLocation());
                    dropItem.setBoots(new ItemStack(Material.AIR));
                }
                if ((hand == null) || (hand.getType().compareTo(Material.AIR) == 0))
                    CraftDropItem.remove(dropItem, DropItemDeathEvent.DeathCause.PLAYER_GOTTEN);
                
                if (dropItem.isDead())
                    return;
                final Location location = dropItem.getLocation();
                location.setY(location.getY() - 1.0D);
                if (location.getBlock().getType().compareTo(Material.HOPPER) == 0) {
                    final Hopper hopper = (Hopper) location.getBlock().getState();
                    final HopperGottenEvent event = new HopperGottenEvent(dropItem.getItemInHand(), hopper);
                    this.drop.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        CraftDropItem.remove(dropItem, DropItemDeathEvent.DeathCause.HOPPER_GOTTEN);
                        hopper.getInventory().addItem(new ItemStack[] { dropItem.getItemInHand() });
                    }
                } else if (dropItem.getFireTicks() > 0)
                    CraftDropItem.remove(dropItem, DropItemDeathEvent.DeathCause.FIRE_TICK);
                
                if (dropItem.isDead())
                    return;
                final Block block = dropItem.getLocation().getBlock();
                if (block.getType().compareTo(Material.AIR) == 0)
                    dropItem.teleport(location);
                final Location loc = dropItem.getLocation();
                loc.setY(loc.getBlockY() + 1);
                if (loc.getBlock().getType().compareTo(Material.AIR) != 0)
                    dropItem.teleport(loc);
            }
        } catch (final Exception e) {
            Debug.debug(e, "Something wrong in running Runnable DropItemRunnable.");
        }
    }
}
