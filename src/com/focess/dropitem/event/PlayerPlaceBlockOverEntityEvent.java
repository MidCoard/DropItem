package com.focess.dropitem.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerPlaceBlockOverEntityEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList handlerList = new HandlerList();

	public static HandlerList getHandlerList() {
		return PlayerPlaceBlockOverEntityEvent.handlerList;
	}

	private boolean cancel = false;

	private final ItemStack itemStack;

	public PlayerPlaceBlockOverEntityEvent(final Player player, final ItemStack itemStack) {
		super(player);
		this.itemStack = itemStack;
	}

	@Override
	public HandlerList getHandlers() {
		return PlayerPlaceBlockOverEntityEvent.handlerList;
	}

	public ItemStack getItemStack() {
		return this.itemStack;
	}

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public void setCancelled(final boolean cancel) {
		this.cancel = cancel;
	}

}
