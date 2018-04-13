package com.focess.dropitem.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class DropItemGottenEvent extends Event implements Cancellable {
	private static final HandlerList handlerList = new HandlerList();

	public static HandlerList getHandlerList() {
		return DropItemGottenEvent.handlerList;
	}

	private boolean cancel;

	private final ItemStack itemStack;

	public DropItemGottenEvent(final ItemStack itemStack) {
		this.itemStack = itemStack;
		this.cancel = false;
	}

	@Override
	public HandlerList getHandlers() {
		return DropItemGottenEvent.handlerList;
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