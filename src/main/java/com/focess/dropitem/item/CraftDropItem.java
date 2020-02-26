package com.focess.dropitem.item;

import com.focess.dropitem.DropItem;
import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.event.DropItemDeathEvent.DeathCause;
import com.focess.dropitem.event.DropItemSpawnEvent;
import com.focess.dropitem.runnable.SpawnDropItemRunnable;
import com.focess.dropitem.util.DropItemConfiguration;
import com.focess.dropitem.util.DropItemUtil;
import com.focess.dropitem.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CraftDropItem {
    private static final Map<UUID, EntityDropItem> droppedItems = new ConcurrentHashMap<>();
    private static DropItem drop;

    public static EntityDropItem getDropItem(final Entity entity) {
        return CraftDropItem.droppedItems.get(entity.getUniqueId());
    }

    public static Collection<EntityDropItem> getDropItems() {
        return CraftDropItem.droppedItems.values();
    }

    public static void hide(final EntityDropItem dropItem) {
        if (CraftDropItem.include(dropItem.getEntity())) {
            dropItem.remove();
            CraftDropItem.droppedItems.remove(dropItem.getUniqueId());
        }
    }

    public static boolean include(final Entity dropItem) {
        return CraftDropItem.getDropItem(dropItem) != null;
    }

    public static void loadItem(final DropItem dropItem) {
        CraftDropItem.drop = dropItem;
        for (final World world : Bukkit.getWorlds())
            for (final Item item : world.getEntitiesByClass(Item.class)) {
                CraftDropItem.spawnItem(item.getItemStack(), item.getLocation(), false);
                item.remove();
            }
    }

    public static void remove(final Entity dropItem, final DeathCause death) {
        CraftDropItem.remove(CraftDropItem.getDropItem(dropItem), death);
    }

    public static void remove(final EntityDropItem dropItem, final boolean isCalled) {
        if (CraftDropItem.include(dropItem.getEntity()) && !isCalled) {
            DropItemInfo.remove(dropItem.getUniqueId());
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
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName())
            dropItem.setCustomName(itemStack.getItemMeta().getDisplayName());
        else
            dropItem.setCustomName(DropItemUtil.formatName(itemStack) + " × " + itemStack.getAmount());
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
        if (DropItemConfiguration.isDropItemAI()) {
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
        }
        for (final EntityDropItem dropItem : CraftDropItem.getDropItems()) {
            CraftDropItem.remove(dropItem, false);
            dropItem.getLocation().getWorld().dropItem(dropItem.getLocation().add(0, 1 - DropItemConfiguration.getHeight(), 0), dropItem.getItemStack());
        }
    }


}
