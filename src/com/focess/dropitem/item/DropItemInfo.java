package com.focess.dropitem.item;

import org.bukkit.scheduler.BukkitRunnable;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent.DeathCause;
import com.focess.dropitem.util.AnxiCode;
import com.focess.dropitem.util.Array;

public class DropItemInfo {

	private class DropItemLive extends BukkitRunnable {

		@Override
		public void run() {
			try {
				final Array<DropItemInfo> temp = new Array<>();
				for (final DropItemInfo dropItemInfo : DropItemInfo.dropItemInfos) {
					dropItemInfo.time++;
					if (DropItemInfo.isRefresh)
						if (dropItemInfo.time > DropItemInfo.refreshTime) {
							temp.add(dropItemInfo);
							CraftAIListener.remove(dropItemInfo);
							CraftDropItem.remove(dropItemInfo.dropItem, DeathCause.DEATH);
						}
				}
				for (final DropItemInfo dropItemInfo : temp)
					DropItemInfo.dropItemInfos.remove(dropItemInfo);
			} catch (final Exception e) {
				Debug.debug(e, "Something wrong in running Runnable DropItemLive.");
			}
		}

	}

	private static int refreshTime = 300;

	private static boolean isRefresh = true;

	private static int anxiCode;

	private static DropItem drop;

	private static Array<DropItemInfo> dropItemInfos = new Array<>();

	protected static DropItemInfo getDropItemInfo(final EntityDropItem d) {
		try {
			for (final DropItemInfo dropItemInfo : DropItemInfo.dropItemInfos)
				if (dropItemInfo.dropItem.getUniqueId().toString().equals(d.getUniqueId().toString()))
					return dropItemInfo;
			return null;
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in getting DropItemInfo.");
			return null;
		}
	}

	public static void register(final DropItem drop, final int anxiCode) {
		try {
			DropItemInfo.anxiCode = AnxiCode.getCode(DropItemInfo.class, drop);
			if (DropItemInfo.anxiCode == anxiCode) {
				DropItemInfo.drop = drop;
				final String temp = drop.getConfig().getString("RefreshTime");
				try {
					DropItemInfo.refreshTime = Integer.parseInt(temp);
				} catch (final Exception e) {
					DropItemInfo.isRefresh = Boolean.getBoolean(temp);
				}
				DropItemInfo.drop.getServer().getScheduler().runTaskTimer(drop,
						(Runnable) new DropItemInfo().new DropItemLive(), 0, 20);
			} else
				AnxiCode.shut(DropItemInfo.class);
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in starting to check DropItem living time.");
		}
	}

	protected static void registerInfo(final EntityDropItem dropItem) {
		new DropItemInfo(dropItem);
	}

	private EntityDropItem dropItem;

	private int time = 0;

	private DropItemInfo() {
	}

	private DropItemInfo(final EntityDropItem dropItem) {
		this.dropItem = dropItem;
		DropItemInfo.dropItemInfos.add(this);
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

	protected DropItemInfo setDropItem(final EntityDropItem dropItem) {
		this.dropItem = dropItem;
		return this;
	}

}
