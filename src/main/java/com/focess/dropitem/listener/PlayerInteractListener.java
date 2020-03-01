package com.focess.dropitem.listener;

import com.focess.dropitem.event.DropItemDeathEvent.DeathCause;
import com.focess.dropitem.event.PlayerGottenEvent;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.EntityDropItem;
import com.focess.dropitem.util.DropItemUtil;
import com.focess.dropitem.util.NMSManager;
import com.focess.dropitem.util.configuration.DropItemConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

public class PlayerInteractListener implements Listener {
    private boolean buildBlock(final Player player, final ItemStack itemStack) {
        final BlockIterator i;
        if (player.getGameMode().equals(GameMode.SURVIVAL))
            i = new BlockIterator(player, 4);
        else
            i = new BlockIterator(player, 5);
        Block last = null;
        Block now;
        while (i.hasNext())
            if (!(now = i.next()).getType().equals(Material.AIR))
                break;
            else
                last = now;
        if (last == null)
            return false;
        else
            this.placeBlock(last, player, itemStack);
        return true;
    }

    @SuppressWarnings("deprecation")
    private boolean buildBlock2(final Player player, final ItemStack itemStack) {
        final BlockIterator i;
        if (player.getGameMode().equals(GameMode.SURVIVAL))
            i = new BlockIterator(player, 4);
        else
            i = new BlockIterator(player, 5);
        Block last = null;
        Block now;
        while (i.hasNext())
            if (!(now = i.next()).getType().equals(Material.AIR))
                break;
            else
                last = now;
        if (last == null)
            return false;
        boolean flag = false;
        for (final EntityDropItem entityDropItem : CraftDropItem.getDropItems())
            if (entityDropItem.getLocation().distance(last.getLocation()) < 1.0) {
                flag = true;
                break;
            }
        if (!flag)
            return false;
        last.setType(itemStack.getType());
        if (!player.getGameMode().equals(GameMode.CREATIVE))
            if (itemStack.getAmount() == 1)
                itemStack.setType(Material.AIR);
            else
                itemStack.setAmount(itemStack.getAmount() - 1);
        player.setItemInHand(itemStack);
        player.updateInventory();
        NMSManager.playSound(player, last);
        return true;
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && event.getPlayer().getItemInHand() != null &&! event.getPlayer().getItemInHand().getType().equals(Material.AIR) && event.getPlayer().getItemInHand().getType().isBlock()
                && DropItemConfiguration.isEnableCoverBlock())
            if (this.buildBlock2(event.getPlayer(), event.getPlayer().getItemInHand()))
                event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(final PlayerInteractAtEntityEvent event) {
        if (CraftDropItem.include(event.getRightClicked())) {
                event.setCancelled(true);
            if (DropItemUtil.checkPlayerPermission(event.getPlayer()) && (DropItemConfiguration.checkPickForm("normal") || DropItemConfiguration.checkPickForm("right-click"))) {
                final EntityDropItem dropItem = CraftDropItem.getDropItem(event.getRightClicked());
                final PlayerGottenEvent e = new PlayerGottenEvent(dropItem.getItemStack(), event.getPlayer());
                Bukkit.getServer().getPluginManager().callEvent(e);
                if (e.isCancelled())
                    return;
                DropItemUtil.fillPlayerInventory(event.getPlayer(), dropItem);
                CraftDropItem.remove(dropItem, DeathCause.PLAYER_GOTTEN);
            }
            }
    }

    @SuppressWarnings("deprecation")
    private void placeBlock(final Block block, final Player player, final ItemStack itemStack) {
        block.setType(itemStack.getType());
        if (!player.getGameMode().equals(GameMode.CREATIVE))
            if (itemStack.getAmount() == 1)
                itemStack.setType(Material.AIR);
            else
                itemStack.setAmount(itemStack.getAmount() - 1);
        player.setItemInHand(itemStack);
        player.updateInventory();
        NMSManager.playSound(player, block);
    }


}
