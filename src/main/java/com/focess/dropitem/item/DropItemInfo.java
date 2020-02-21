package com.focess.dropitem.item;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.scheduler.BukkitRunnable;

import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent.DeathCause;
import com.focess.dropitem.util.AnxiCode;

public class DropItemInfo {

    private static class DropItemLive extends BukkitRunnable {

        @Override
        public void run() {
            for (final UUID uuid : DropItemInfo.dropItemInfos.keySet()) {
                final DropItemInfo dropItemInfo = DropItemInfo.dropItemInfos.get(uuid);
                dropItemInfo.time++;
                if (dropItemInfo.time > DropItemInfo.refreshTime) {
                    DropItemInfo.dropItemInfos.remove(uuid);
                    if (!dropItemInfo.alive)
                        CraftAIListener.remove(dropItemInfo.getUUID());
                    CraftDropItem.remove(dropItemInfo.dropItem, DeathCause.DEATH);
                }
            }
        }

    }

    private static int anxiCode;

    private static DropItem drop;

    private static Map<UUID, DropItemInfo> dropItemInfos = new ConcurrentHashMap<>();

    private static boolean isRefresh = true;

    private static int refreshTime = 300;

    public static void clear(final int anxiCode) {
        if (DropItemInfo.anxiCode == anxiCode)
            DropItemInfo.dropItemInfos.clear();
        else
            AnxiCode.shut(DropItemInfo.class);
    }

    protected static DropItemInfo getDropItemInfo(final UUID uuid) {
        return DropItemInfo.dropItemInfos.get(uuid);
    }

    public static void register(final DropItem drop, final int anxiCode) {
        DropItemInfo.anxiCode = AnxiCode.getCode(DropItemInfo.class, drop);
        if (DropItemInfo.anxiCode == anxiCode) {
            DropItemInfo.drop = drop;
            final String temp = drop.getConfig().getString("RefreshTime");
            try {
                DropItemInfo.refreshTime = Integer.parseInt(temp);
            } catch (final Exception e) {
                DropItemInfo.isRefresh = Boolean.getBoolean(temp);
            }
            if (DropItemInfo.isRefresh)
                DropItemInfo.drop.getServer().getScheduler().runTaskTimer(drop, (Runnable) new DropItemLive(), 0, 20);
        } else
            AnxiCode.shut(DropItemInfo.class);
    }

    protected static void registerInfo(final EntityDropItem dropItem) {
        new DropItemInfo(dropItem);
    }

    public static void remove(final UUID uuid) {
        DropItemInfo.dropItemInfos.remove(uuid);
    }

    private boolean alive = true;

    private EntityDropItem dropItem;

    private int time = 0;

    private UUID uuid;

    private DropItemInfo(final EntityDropItem dropItem) {
        this.dropItem = dropItem;
        this.uuid = dropItem.getUniqueId();
        DropItemInfo.dropItemInfos.put(dropItem.getUniqueId(), this);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        final DropItemInfo other = (DropItemInfo) obj;
        if (this.dropItem == null) {
            if (other.dropItem != null)
                return false;
        } else if (!this.dropItem.equals(other.dropItem))
            return false;
        return true;
    }

    public EntityDropItem getDropItem() {
        return this.dropItem;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void setAlive(final boolean alive) {
        this.alive = alive;
    }

    protected void setDropItem(final EntityDropItem dropItem) {
        this.dropItem = dropItem;
        this.uuid = dropItem.getUniqueId();
        DropItemInfo.dropItemInfos.put(dropItem.getUniqueId(), this);
    }

}
