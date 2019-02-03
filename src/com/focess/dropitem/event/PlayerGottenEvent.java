package com.focess.dropitem.event;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerGottenEvent extends DropItemGottenEvent {
    private final Player player;

    public PlayerGottenEvent(final ItemStack itemStack, final Player player) {
        super(itemStack);
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}
