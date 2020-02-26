package com.focess.dropitem.item;

import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.event.DropItemDeathEvent.DeathCause;
import com.focess.dropitem.event.DropItemSpawnEvent;
import com.focess.dropitem.runnable.SpawnDropItemRunnable;
import com.focess.dropitem.util.DropItemConfiguration;
import com.focess.dropitem.util.DropItemUtil;
import com.focess.dropitem.util.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CraftDropItem {
    private static final Map<UUID, EntityDropItem> droppedItems = new ConcurrentHashMap<>();
    private static final List<String> uuids = new ArrayList<>();
    private static DropItem drop;

    public static EntityDropItem getDropItem(final Entity entity) {
        return CraftDropItem.droppedItems.get(entity.getUniqueId());
    }

    public static Collection<EntityDropItem> getDropItems() {
        return CraftDropItem.droppedItems.values();
    }

    public static void hide(final EntityDropItem dropItem) {
        if (CraftDropItem.include(dropItem.getEntity())) {
            final File uuidFile = new File(
                    CraftDropItem.drop.getDataFolder() + "/drops/" + dropItem.getUniqueId().toString());
            uuidFile.delete();
            dropItem.remove();
            CraftDropItem.droppedItems.remove(dropItem.getUniqueId());
        }
    }

    public static boolean include(final Entity dropItem) {
        return CraftDropItem.getDropItem(dropItem) != null;
    }

    public static void loadItem(final DropItem dropItem) {
        CraftDropItem.drop = dropItem;
        final File drops = new File(CraftDropItem.drop.getDataFolder(), "drops");
        final File[] files = drops.listFiles();
        for (final File file : files)
            CraftDropItem.uuids.add(file.getName());
    }

    public static void loadItem(final Entity dropItem) {
        if (CraftDropItem.uuids.contains(dropItem.getUniqueId().toString())) {
            final EntityDropItem entityDropItem = EntityDropItem.createEntityDropItem((LivingEntity) dropItem);
            CraftDropItem.droppedItems.put(dropItem.getUniqueId(), entityDropItem);
            DropItemInfo.registerInfo(entityDropItem);
            CraftDropItem.uuids.remove(dropItem.getUniqueId().toString());
        }
    }

    public static void remove(final Entity dropItem, final DeathCause death) {
        CraftDropItem.remove(CraftDropItem.getDropItem(dropItem), death);
    }

    public static void remove(final EntityDropItem dropItem, final boolean isCalled) {
        if (CraftDropItem.include(dropItem.getEntity()) && !isCalled) {
            final File uuidFile = new File(
                    CraftDropItem.drop.getDataFolder() + "/drops/" + dropItem.getUniqueId().toString());
            DropItemInfo.remove(dropItem.getUniqueId());
            uuidFile.delete();
            if (!dropItem.isDead())
                dropItem.remove();
            CraftDropItem.droppedItems.remove(dropItem.getUniqueId());
        }
    }

    public static void remove(final EntityDropItem dropItem, final DropItemDeathEvent.DeathCause death) {
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
    }

    public static void spawnItem(final Item item) {
        if (DropItemConfiguration.checkDropForm("w-spawn")) {
            final ItemStack itemStack = item.getItemStack();
            final Location location = item.getLocation();
            item.remove();
            CraftDropItem.spawnItem(itemStack, location);
        } else
            SpawnDropItemRunnable.addItem(item);

    }

    public static EntityDropItem spawnItem(final ItemStack itemStack, final Location location) {
        return CraftDropItem.spawnItem(itemStack, location, true);
    }

    public static EntityDropItem spawnItem(final ItemStack itemStack, final Location location, final boolean isCalled) {
        return CraftDropItem.spawnItem(itemStack, location, isCalled, true);
    }

    public static EntityDropItem spawnItem(final ItemStack itemStack, final Location location, final boolean isCalled,
                                           final boolean isRegistered) {
        location.setY(location.getBlockY() - 1 + DropItemConfiguration.getHeight());
        final EntityDropItem dropItem = EntityDropItem.createEntityDropItem(
                (LivingEntity) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND), itemStack);
        dropItem.setUp();
        boolean flag = false;
        for (final DropItemConfiguration.ItemStackAngle itemStackAngle : DropItemConfiguration.getAngle())
            if (itemStackAngle.getMaterial().equals(itemStack.getType())) {
                flag = true;
                dropItem.setRightArmPose(itemStackAngle.getAngle());
            }
        if (!flag) {
            final EulerAngle eulerAngle = DropItemConfiguration.getDefaultAngle();
            dropItem.setRightArmPose(eulerAngle);
        }
        final String customName = DropItemUtil.formatName(itemStack) + " × " + itemStack.getAmount();
        dropItem.setCustomName(customName);
        CraftDropItem.droppedItems.put(dropItem.getUniqueId(), dropItem);
        if (isCalled) {
            final DropItemSpawnEvent event = new DropItemSpawnEvent(dropItem);
            CraftDropItem.drop.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                CraftDropItem.droppedItems.remove(dropItem.getUniqueId());
                dropItem.remove();
                return null;
            }
        }
        if (isRegistered)
            DropItemInfo.registerInfo(dropItem);
        return dropItem;
    }

    public static void uploadItems() {
        final Map<UUID, Pair<Location, ItemStack>> ais = CraftDropItem.drop.getCraftAIListener()
                .getAIs();
        for (final UUID uuid : ais.keySet()) {
            final Pair<Location, ItemStack> pair = ais.get(uuid);
            final Location location = pair.getKey();
            final ItemStack itemStack = pair.getValue();
            final Location temp = new Location(location.getWorld(), location.getBlockX(), location.getBlockY() + 1,
                    location.getBlockZ());
            if (itemStack != null)
                CraftDropItem.spawnItem(itemStack, temp, false);
        }
        for (final EntityDropItem dropItem : CraftDropItem.droppedItems.values()) {
            final File uuidFile = new File(
                    CraftDropItem.drop.getDataFolder() + "/drops/" + dropItem.getUniqueId().toString());
            try {
                uuidFile.createNewFile();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }


}
