package com.focess.dropitem.item;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.event.DropItemDeathEvent.DeathCause;
import com.focess.dropitem.event.DropItemSpawnEvent;
import com.focess.dropitem.runnable.SpawnDropItemRunnable;
import com.focess.dropitem.util.AnxiCode;
import com.focess.dropitem.util.DropItemUtil;
import com.focess.dropitem.util.Pair;

public class CraftDropItem {
	static class ItemStackAngle {
		private final Material material;
		private final int px;
		private final int py;
		private final int pz;

		ItemStackAngle(final Material material, final int px, final int py, final int pz) {
			this.material = material;
			this.px = px;
			this.py = py;
			this.pz = pz;
		}

		Material getMaterial() {
			return this.material;
		}

		int getPx() {
			return this.px;
		}

		int getPy() {
			return this.py;
		}

		int getPz() {
			return this.pz;
		}
	}

	private static int anxiCode;
	private static DropItem drop;
	private static Map<UUID, EntityDropItem> droppedItems = new ConcurrentHashMap<>();
	private static List<ItemStackAngle> isas = new ArrayList<>();
	private static int pitchX;
	private static int pitchY;
	private static int pitchZ;

	private static List<String> uuids = new ArrayList<>();

	public static EntityDropItem getDropItem(final Entity entity) {
		return CraftDropItem.droppedItems.get(entity.getUniqueId());
	}

	public static Collection<EntityDropItem> getDropItems(final int anxiCode) {
		try {
			if (CraftDropItem.anxiCode == anxiCode)
				return CraftDropItem.droppedItems.values();
			AnxiCode.shut(CraftDropItem.class);
			return null;
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in getting ArmorStands.");
			return null;
		}
	}

	public static void hide(final EntityDropItem dropItem) {
		try {
			if (CraftDropItem.include(dropItem.getEntity())) {
				final File uuidFile = new File(
						CraftDropItem.drop.getDataFolder() + "/drops/" + dropItem.getUniqueId().toString());
				uuidFile.delete();
				dropItem.remove();
				CraftDropItem.droppedItems.remove(dropItem.getUniqueId());
			}
		} catch (final Exception e) {
			Debug.debug(e,
					"Something wrong in hiding EntityDropItem(Name = " + dropItem.getCustomName() + ",Type = "
							+ dropItem.getItemStack().getType().name() + ",Count = "
							+ dropItem.getItemStack().getAmount() + ").");
		}
	}

	public static boolean include(final Entity dropItem) {
		return CraftDropItem.getDropItem(dropItem) != null;
	}

	@SuppressWarnings("deprecation")
	public static void loadItem(final DropItem dropItem) {
		try {
			CraftDropItem.anxiCode = AnxiCode.getCode(CraftDropItem.class, dropItem);
			CraftDropItem.drop = dropItem;
			CraftDropItem.pitchX = CraftDropItem.drop.getConfig().getInt("PitchX");
			CraftDropItem.pitchY = CraftDropItem.drop.getConfig().getInt("PitchY");
			CraftDropItem.pitchZ = CraftDropItem.drop.getConfig().getInt("PitchZ");
			final File drops = new File(CraftDropItem.drop.getDataFolder(), "drops");
			final File[] files = drops.listFiles();
			for (final File file : files)
				CraftDropItem.uuids.add(file.getName());
			final List<String> angles = CraftDropItem.drop.getConfig().getStringList("Angles");
			for (final String angle : angles) {
				final String[] temp = angle.trim().split(" ");
				if (temp.length != 4)
					continue;
				try {
					final int id = Integer.parseInt(temp[0]);
					if (Material.getMaterial(id) == null)
						continue;
					CraftDropItem.isas.add(new ItemStackAngle(Material.getMaterial(id), Integer.parseInt(temp[1]),
							Integer.parseInt(temp[2]), Integer.parseInt(temp[3])));
				} catch (final Exception e) {
					if (Material.getMaterial(temp[0]) == null)
						continue;
					CraftDropItem.isas.add(new ItemStackAngle(Material.getMaterial(temp[0]), Integer.parseInt(temp[1]),
							Integer.parseInt(temp[2]), Integer.parseInt(temp[3])));
				}

			}
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in loading config.");
		}
	}

	public static void loadItem(final Entity dropItem, final int anxiCode) {
		if (anxiCode == CraftDropItem.anxiCode) {
			if (CraftDropItem.uuids.contains(dropItem.getUniqueId().toString())) {
				final EntityDropItem entityDropItem = EntityDropItem.createEntityDropItem((LivingEntity) dropItem);
				CraftDropItem.droppedItems.put(dropItem.getUniqueId(), entityDropItem);
				DropItemInfo.registerInfo(entityDropItem);
				CraftDropItem.uuids.remove(dropItem.getUniqueId().toString());
			}
		} else
			AnxiCode.shut(CraftDropItem.class);
	}

	public static void remove(final Entity dropItem, final DeathCause death) {
		CraftDropItem.remove(CraftDropItem.getDropItem(dropItem), death);
	}

	public static void remove(final EntityDropItem dropItem, final boolean iscalled) {
		try {
			if (CraftDropItem.include(dropItem.getEntity()) && !iscalled) {
				final File uuidFile = new File(
						CraftDropItem.drop.getDataFolder() + "/drops/" + dropItem.getUniqueId().toString());
				DropItemInfo.remove(dropItem.getUniqueId());
				uuidFile.delete();
				if (!dropItem.isDead())
					dropItem.remove();
				CraftDropItem.droppedItems.remove(dropItem.getUniqueId());
			}
		} catch (final Exception e) {
			Debug.debug(e,
					"Something wrong in removing EntityDropItem(Name = " + dropItem.getCustomName() + ",Type = "
							+ dropItem.getItemStack().getType().name() + ",Count = "
							+ dropItem.getItemStack().getAmount() + ").");
		}
	}

	public static void remove(final EntityDropItem dropItem, final DropItemDeathEvent.DeathCause death) {
		try {
			if (CraftDropItem.include(dropItem.getEntity())) {
				final DropItemDeathEvent event = new DropItemDeathEvent(dropItem, death);
				CraftDropItem.drop.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled())
					return;
				final File uuidFile = new File(
						CraftDropItem.drop.getDataFolder() + "/drops/" + dropItem.getUniqueId().toString());
				DropItemInfo.remove(dropItem.getUniqueId());
				uuidFile.delete();
				if (!dropItem.isDead())
					dropItem.remove();
				CraftDropItem.droppedItems.remove(dropItem.getUniqueId());
			}
		} catch (final Exception e) {
			Debug.debug(e,
					"Something wrong in removing EntityDropItem(Name = " + dropItem.getCustomName() + ",Type = "
							+ dropItem.getItemStack().getType().name() + ",Count = "
							+ dropItem.getItemStack().getAmount() + ").");
		}
	}

	public static void spawnItem(final Item item) {
		try {
			if (DropItemUtil.checkDropForm("w-spawn")) {
				final ItemStack itemStack = item.getItemStack();
				final Location location = item.getLocation();
				item.remove();
				CraftDropItem.spawnItem(itemStack, location);
			} else
				SpawnDropItemRunnable.addItem(item);
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in spawning ItemStack(Type = " + item.getItemStack().getType().name()
					+ ",Count = " + item.getItemStack().getAmount() + ").");
		}
	}

	public static EntityDropItem spawnItem(final ItemStack itemStack, final Location location) {
		return CraftDropItem.spawnItem(itemStack, location, true);
	}

	public static EntityDropItem spawnItem(final ItemStack itemStack, final Location location, final boolean iscalled) {
		return CraftDropItem.spawnItem(itemStack, location, iscalled, true);
	}

	public static EntityDropItem spawnItem(final ItemStack itemStack, final Location location, final boolean iscalled,
			final boolean isregistered) {
		try {
			location.setY(location.getBlockY() - 1 + DropItemUtil.getHeight());
			EntityDropItem dropItem = EntityDropItem.createEntityDropItem(
					(LivingEntity) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND), itemStack);
			dropItem.setUp();
			boolean flag = false;
			for (final ItemStackAngle isa : CraftDropItem.isas)
				if (isa.getMaterial().equals(itemStack.getType())) {
					flag = true;
					final EulerAngle eulerAngle = new EulerAngle(isa.getPx(), isa.getPy(), isa.getPz());
					dropItem.setRightArmPose(eulerAngle);
				}
			if (!flag) {
				final EulerAngle eulerAngle = new EulerAngle(CraftDropItem.pitchX, CraftDropItem.pitchY,
						CraftDropItem.pitchZ);
				dropItem.setRightArmPose(eulerAngle);
			}
			String customName = itemStack.getType().name().toLowerCase() + " × " + itemStack.getAmount();
			if (DropItem.Slanguages.get(itemStack.getType().name()) == null)
				System.out.println("对不起，我们暂时还没有物品类型为：" + itemStack.getType().name() + "的中文译名");
			else if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName())
				customName = itemStack.getItemMeta().getDisplayName();
			else if (CraftDropItem.drop.getConfig().getString("Language", "zhs").equals("zhs"))
				customName = DropItem.Slanguages.get(itemStack.getType().name()) + " × " + itemStack.getAmount();
			else if (CraftDropItem.drop.getConfig().getString("Language", "zhs").equals("zht"))
				customName = DropItem.Tlanguages.get(itemStack.getType().name()) + " × " + itemStack.getAmount();
			dropItem.setCustomName(customName);
			CraftDropItem.droppedItems.put(dropItem.getUniqueId(), dropItem);
			if (iscalled) {
				final DropItemSpawnEvent event = new DropItemSpawnEvent(dropItem);
				CraftDropItem.drop.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					CraftDropItem.droppedItems.remove(dropItem.getUniqueId());
					dropItem.remove();
					return null;
				}
			}
			if (isregistered)
				DropItemInfo.registerInfo(dropItem);
			return dropItem;
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in spawning ItemStack(Type = " + itemStack.getType().name() + ",Count = "
					+ itemStack.getAmount() + ").");
			return null;
		}
	}

	public static void uploadItems(final int anxiCode) {
		try {
			if (CraftDropItem.anxiCode == anxiCode) {
				final Map<UUID, Pair<Location, ItemStack>> ais = CraftDropItem.drop.getCraftAIListener(anxiCode)
						.getAIs(anxiCode);
				for (final UUID uuid : ais.keySet()) {
					final Pair<Location, ItemStack> pair = ais.get(uuid);
					final Location location = pair.getKey();
					final ItemStack itemStack = pair.getValue();
					final Location temp = new Location(location.getWorld(), location.getBlockX(),
							location.getBlockY() + 1, location.getBlockZ());
					if (itemStack != null)
						CraftDropItem.spawnItem(itemStack, temp, false);
				}
				for (final EntityDropItem dropItem : CraftDropItem.droppedItems.values()) {
					final File uuidFile = new File(
							CraftDropItem.drop.getDataFolder() + "/drops/" + dropItem.getUniqueId().toString());
					uuidFile.createNewFile();
				}
			} else {
				System.err.println("某些程序试图重载DropItem信息");
				AnxiCode.shut(CraftDropItem.class);
			}
		} catch (final Exception e) {
			Debug.debug(e, "Something wrong in upload config.");
		}
	}

}
