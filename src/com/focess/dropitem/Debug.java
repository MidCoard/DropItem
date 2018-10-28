package com.focess.dropitem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.focess.dropitem.commnad.DropItemCommand;
import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.event.DropItemGottenEvent;
import com.focess.dropitem.event.DropItemSpawnEvent;
import com.focess.dropitem.event.HopperGottenEvent;
import com.focess.dropitem.event.PlayerGottenEvent;
import com.focess.dropitem.item.CraftAIListener;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.DropItemInfo;
import com.focess.dropitem.item.EntityDropItem;
import com.focess.dropitem.listener.EntityDeathListener;
import com.focess.dropitem.listener.PlayerDropItemListener;
import com.focess.dropitem.listener.PlayerInteractListener;
import com.focess.dropitem.listener.PlayerJoinListener;
import com.focess.dropitem.listener.PlayerMoveListener;
import com.focess.dropitem.runnable.CallDropItemDeath;
import com.focess.dropitem.runnable.EmptyDropItemClean;
import com.focess.dropitem.runnable.PlayerStandDropItem;
import com.focess.dropitem.runnable.SpawnDropItem;
import com.focess.dropitem.runnable.VisibleDropItemName;
import com.focess.dropitem.test.Test;
import com.focess.dropitem.util.AnxiCode;
import com.focess.dropitem.util.DropItemUtil;

public class Debug {

	private static int anxiCode;
	private static boolean debug = false;
	private static DropItem drop;
	private static String filePath;
	private static List<String> ownerClass = new ArrayList<>();

	static {
		Debug.ownerClass.add(Debug.class.getName());
		Debug.ownerClass.add(DropItem.class.getName());
		Debug.ownerClass.add(DropItemCommand.class.getName());
		Debug.ownerClass.add(DropItemDeathEvent.class.getName());
		Debug.ownerClass.add(DropItemGottenEvent.class.getName());
		Debug.ownerClass.add(DropItemSpawnEvent.class.getName());
		Debug.ownerClass.add(HopperGottenEvent.class.getName());
		Debug.ownerClass.add(PlayerGottenEvent.class.getName());
		Debug.ownerClass.add(CraftAIListener.class.getName());
		Debug.ownerClass.add(CraftDropItem.class.getName());
		Debug.ownerClass.add(EntityDeathListener.class.getName());
		Debug.ownerClass.add(PlayerDropItemListener.class.getName());
		Debug.ownerClass.add(PlayerMoveListener.class.getName());
		Debug.ownerClass.add(CallDropItemDeath.class.getName());
		Debug.ownerClass.add(EmptyDropItemClean.class.getName());
		Debug.ownerClass.add(PlayerStandDropItem.class.getName());
		Debug.ownerClass.add(SpawnDropItem.class.getName());
		Debug.ownerClass.add(VisibleDropItemName.class.getName());
		Debug.ownerClass.add(AnxiCode.class.getName());
		Debug.ownerClass.add(DropItemInfo.class.getName());
		Debug.ownerClass.add(PlayerJoinListener.class.getName());
		Debug.ownerClass.add(Test.class.getName());
		Debug.ownerClass.add(EntityDropItem.class.getName());
		Debug.ownerClass.add(PlayerInteractListener.class.getName());
		Debug.ownerClass.add(DropItemUtil.class.getName());
	}

	protected static void debug(final DropItem drop, final int anxiCode) {
		Debug.anxiCode = AnxiCode.getCode(Debug.class, drop);
		if (Debug.anxiCode != anxiCode)
			AnxiCode.shut(Debug.class);
		if (Debug.debug) {
			System.err.println("DropItemDebug已经加载");
			AnxiCode.shut(Debug.class);
		}
		Debug.debug = true;
		Debug.drop = drop;
		Debug.filePath = Debug.drop.getDataFolder().getPath() + "/bugs/";
	}

	public static void debug(final Exception exception, final String message) {
		if (Debug.debug) {
			File bug = new File(Debug.filePath + new Date().toString().replace(" ", "_").replace(":", "_") + ".txt");
			try {
				if (bug.exists())
					bug = new File(Debug.filePath + new Date().toString().replace(" ", "_").replace(":", "_")
							+ Math.random() + ".txt");
				try {
					bug.createNewFile();
				} catch (final IOException e) {
					Debug.debug(e, "File Debug(Path = \"" + bug.getPath() + "\") cannot be created.");
				}
				final List<String> errinfos = new ArrayList<>();
				errinfos.add("Message: " + message);
				errinfos.add("Cause: " + exception.toString());
				final StackTraceElement[] errs = exception.getStackTrace();
				for (final StackTraceElement err : errs)
					if (Debug.ownerClass.contains(err.getClassName())) {
						errinfos.add("Class: " + err.getClassName());
						errinfos.add("Method: " + err.getMethodName());
						errinfos.add("Line: " + err.getLineNumber());
					}
				try {
					final FileOutputStream outputer = new FileOutputStream(bug);
					for (final String errinfo : errinfos) {
						final byte[] bytes = (errinfo + "\r\n").getBytes();
						for (final byte b : bytes)
							try {
								outputer.write(b);
							} catch (final IOException e) {
								Debug.debug(e,
										"Something wrong in writing in File Debug(Path = \"" + bug.getPath() + "\").");
							}
					}
					try {
						outputer.close();
					} catch (final IOException e) {
						Debug.debug(e, "Something wrong in closing FileOutputStream in File Debug(Path = \""
								+ bug.getPath() + "\").");
					}
				} catch (final FileNotFoundException e) {
					Debug.debug(e, "File Debug(Path = \"" + bug.getPath() + "\") cannot exist.");
				}
			} catch (final Exception e) {
				Debug.debug(e, "Something wrong in creating File Debug(Path = \"" + bug.getPath() + "\").");
			}
			exception.printStackTrace();
			System.err.println("错误/Warnning: " + exception.toString());
			System.err.println("DropItem: " + bug.getPath());
		} else
			exception.printStackTrace();
	}

	protected static void reload(final int anxiCode) {
		if (Debug.anxiCode == anxiCode)
			Debug.debug = false;
		else
			AnxiCode.shut(AnxiCode.class);
	}

}
