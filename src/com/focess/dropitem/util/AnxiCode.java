package com.focess.dropitem.util;

import java.util.HashMap;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.commnad.DropItemCommand;
import com.focess.dropitem.item.CraftAIListener;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.DropItemInfo;
import com.focess.dropitem.listener.PlayerMoveListener;
import com.focess.dropitem.runnable.CallDropItemDeath;
import com.focess.dropitem.runnable.EmptyDropItemClean;

public class AnxiCode {
	private static final AnxiCode[] anxiCode = new AnxiCode[1];

	private static boolean flag = false;

	private static HashMap<Class<?>, Boolean> safeClass = new HashMap<>();
	static {
		AnxiCode.safeClass.put(CraftDropItem.class, false);
		AnxiCode.safeClass.put(DropItem.class, false);
		AnxiCode.safeClass.put(EmptyDropItemClean.class, false);
		AnxiCode.safeClass.put(CraftAIListener.class, false);
		AnxiCode.safeClass.put(CallDropItemDeath.class, false);
		AnxiCode.safeClass.put(DropItemCommand.class, false);
		AnxiCode.safeClass.put(Debug.class, false);
		AnxiCode.safeClass.put(DropItemInfo.class, false);
		AnxiCode.safeClass.put(PlayerMoveListener.class, false);
	}

	public static int getCode(final Class<?> c, final DropItem dropItem) {
		final Boolean b = AnxiCode.safeClass.get(c);
		if ((b != null) && !b.booleanValue() && (dropItem != null) && dropItem.getName().equals("DropItem")) {
			AnxiCode.safeClass.put(c, Boolean.valueOf(true));
			return AnxiCode.anxiCode[0].code;
		}
		System.err.println("Cause: " + c.getName());
		AnxiCode.shut(AnxiCode.class);
		return -1;
	}

	public static void reload(final int anxiCode) {
		if (AnxiCode.anxiCode[0].code == anxiCode) {
			for (final Class<?> c : AnxiCode.safeClass.keySet())
				AnxiCode.safeClass.put(c, false);
			AnxiCode.flag = false;
		} else
			AnxiCode.shut(AnxiCode.class);
	}

	public static void shut(final Class<?> c) {
		System.err.println("Cause: " + c.getName());
		System.err.println("针对DropItem的安全问题");
		System.err.println("DropItem即将载出");
		try {
			AnxiCode.anxiCode[0].drop.getPluginLoader().disablePlugin(AnxiCode.anxiCode[0].drop);
		} catch (final StackOverflowError e) {

		}
	}

	private final int code;

	private DropItem drop;

	public AnxiCode(final DropItem dropItem) {
		if ((dropItem != null) && dropItem.getName().equals("DropItem") && !AnxiCode.flag) {
			final int[] code = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
			final StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < 6; i++) {
				final int index = (int) (Math.random() * 10.0D);
				stringBuilder.append(code[index]);
			}
			this.code = stringBuilder.toString().hashCode();
			AnxiCode.anxiCode[0] = this;
			AnxiCode.flag = true;
			this.drop = dropItem;
		} else {
			this.code = -1;
			AnxiCode.shut(AnxiCode.class);
		}
	}
}