package com.focess.dropitem.listener;

import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.util.DropItemUtil;
import com.focess.dropitem.util.configuration.DropItemConfiguration;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        this.onPlayerPickUpItem(player);
    }

    private void onPlayerPickUpItem(final Player player) {
        if (player.getGameMode().compareTo(GameMode.SPECTATOR) == 0)
            return;
        final List<Entity> entities = player.getNearbyEntities(0.75D, 0.75D, 0.75D);
        for (final Entity entity : entities)
            if (CraftDropItem.include(entity) &&  (DropItemConfiguration.checkPickForm("w-move") || (player.isSneaking() &&DropItemConfiguration.checkPickForm("normal")))
                    && DropItemUtil.checkPlayerPermission(player))
                DropItemUtil.fillPlayerInventory(player, CraftDropItem.getDropItem(entity));

    }
}
