package com.focess.dropitem.runnable;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.event.PlayerGottenEvent;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.EntityDropItem;
import com.focess.dropitem.util.AnxiCode;

public class PlayerAroundDropItemRunnable extends BukkitRunnable {
    private boolean allowedPlayer = false;
    private final DropItem drop;
    private final boolean naturalSpawn;
    private final String PickForm;
    private final int anxiCode;
    private boolean showItemInfo;

    public PlayerAroundDropItemRunnable(final DropItem dropItem) {
        this.anxiCode = AnxiCode.getCode(PlayerAroundDropItemRunnable.class, dropItem);
        this.PickForm = dropItem.getConfig().getString("PickForm");
        this.showItemInfo = dropItem.getConfig().getBoolean("ShowItemInfo");
        this.drop = dropItem;
        if (!dropItem.getConfig().getBoolean("NaturalSpawn"))
            this.allowedPlayer = dropItem.getConfig().getBoolean("AllowedPlayer", false);
        this.naturalSpawn = dropItem.getConfig().getBoolean("NaturalSpawn", true);
    }

    @Override
    public void run() {
        try {
            for (EntityDropItem dropItem:CraftDropItem.getDropItems(anxiCode)) {
                List<Entity> entities = dropItem.getNearbyEntities(0.75, 0.75, 0.75);
                for (Entity entity:entities) 
                    if (entity instanceof Player){  
                        Player player = (Player) entity;
                        if (this.PickForm.equals("w-move")&& (this.naturalSpawn || this.allowedPlayer || player.hasPermission("dropitem.use"))) {
                            final EntityDropItem entityDropItem = CraftDropItem.getDropItem(entity);
                            if (player.getInventory().firstEmpty() != -1) {
                                final PlayerGottenEvent event_gotten = new PlayerGottenEvent(
                                        entityDropItem.getItemInHand(), player);
                                this.drop.getServer().getPluginManager().callEvent(event_gotten);
                                if (!event_gotten.isCancelled()) {
                                    player.getInventory().addItem(new ItemStack[] { entityDropItem.getItemInHand() });
                                    CraftDropItem.remove(entity, DropItemDeathEvent.DeathCause.PLAYER_GOTTEN);
                                }
                            } else if (!player.getInventory().contains(entityDropItem.getItemInHand()))
                                continue;
                            else {
                                int count = 0;
                                final ItemStack is = entityDropItem.getItemInHand();
                                int amount = 0;
                                for (final ItemStack itemStack : player.getInventory())
                                    if (itemStack != null)
                                        if (itemStack.isSimilar(is)) {
                                            count += itemStack.getAmount();
                                            amount++;
                                        }
                                if (((amount * entityDropItem.getItemInHand().getMaxStackSize()) - count) != 0) {
                                    final PlayerGottenEvent event_gotten = new PlayerGottenEvent(
                                            entityDropItem.getItemInHand(), player);
                                    this.drop.getServer().getPluginManager().callEvent(event_gotten);
                                    if (!event_gotten.isCancelled()) {
                                        player.getInventory()
                                                .addItem(new ItemStack[] { entityDropItem.getItemInHand() });
                                        CraftDropItem.remove(entity, DropItemDeathEvent.DeathCause.PLAYER_GOTTEN);
                                    }
                                } else
                                    continue;
                            }
                        }
                    }
                List<Entity> entities2 = dropItem.getNearbyEntities(2, 2, 2);
                boolean flag = true;
                for (Entity entity:entities2) 
                    if (entity instanceof Player&&this.showItemInfo){
                        dropItem.setCustomNameVisible(true);
                        flag = false;
                        break;
                    }
                if (flag)
                    dropItem.setCustomNameVisible(false);
            }
        } catch (final Exception e) {
            Debug.debug(e, "Something wrong in running Runnable PlayerAroundDropItemRunnable.");
        }
    }
}