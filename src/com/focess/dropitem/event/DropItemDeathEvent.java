package com.focess.dropitem.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.focess.dropitem.item.EntityDropItem;

public class DropItemDeathEvent extends DropItemEvent implements Cancellable {

    public static enum DeathCause {
        DEATH, FIRE_TICK, HOPPER_GOTTEN, PLAYER_GOTTEN, SYSTEM_CLEAN, UNKNOWN;
    }

    private static final HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return DropItemDeathEvent.handlerList;
    }

    private boolean cancel;

    private final DeathCause deathCause;

    public DropItemDeathEvent(final EntityDropItem dropItem, final DeathCause deathCause) {
        super(dropItem);
        this.cancel = false;
        this.deathCause = deathCause;
    }

    public DeathCause getDeathCause() {
        return this.deathCause;
    }

    @Override
    public HandlerList getHandlers() {
        return DropItemDeathEvent.handlerList;
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