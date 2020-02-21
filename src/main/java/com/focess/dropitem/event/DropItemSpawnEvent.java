package com.focess.dropitem.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.focess.dropitem.item.EntityDropItem;

public class DropItemSpawnEvent extends DropItemEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return DropItemSpawnEvent.handlerList;
    }

    private boolean cancel;

    public DropItemSpawnEvent(final EntityDropItem dropItem) {
        super(dropItem);
        this.cancel = false;
    }

    @Override
    public HandlerList getHandlers() {
        return DropItemSpawnEvent.handlerList;
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
