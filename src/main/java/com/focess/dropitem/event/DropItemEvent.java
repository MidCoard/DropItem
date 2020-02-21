package com.focess.dropitem.event;

import org.bukkit.event.Event;

import com.focess.dropitem.item.EntityDropItem;

public abstract class DropItemEvent extends Event {

    private final EntityDropItem dropItem;

    public DropItemEvent(final EntityDropItem dropItem) {
        this.dropItem = dropItem;
    }

    public EntityDropItem getDropItem() {
        return this.dropItem;
    }

}
