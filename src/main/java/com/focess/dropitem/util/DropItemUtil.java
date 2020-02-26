package com.focess.dropitem.util;

import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.event.HopperGottenEvent;
import com.focess.dropitem.event.PlayerGottenEvent;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.EntityDropItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

public class DropItemUtil {

    public static boolean checkNull(final String name) {
        if (name != null)
            return !name.startsWith(ChatColor.RED + "QuickShop") && !name.contains("GSCompleXMoneyFromMobzzzzzzzzz")
                    && !name.contains("XXXPlayer777MoneyXXX");
        return true;
    }


    public static boolean checkPlayerPermission(final Player player) {
        return player.hasPermission("dropitem.use");
    }

    public static void fillHopperInventory(final Hopper hopper, final EntityDropItem entityDropItem) {
        if (!hopper.getInventory().containsAtLeast(entityDropItem.getItemStack(), 1)
                && hopper.getInventory().firstEmpty() == -1)
            return;
        final Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER);
        for (int i = 0; i < hopper.getInventory().getContents().length; i++) {
            if (hopper.getInventory().getItem(i) == null)
                continue;
            final ItemStack itemStack = hopper.getInventory().getItem(i).clone();
            if (itemStack != null)
                inventory.setItem(i, itemStack);
        }
        final Map<Integer, ItemStack> itemStacks = inventory.addItem(entityDropItem.getItemStack().clone());
        final ItemStack item = entityDropItem.getItemStack().clone();
        if (!itemStacks.isEmpty())
            item.setAmount(item.getAmount() - itemStacks.get(0).getAmount());
        if (item.getAmount() == 0)
            return;
        final HopperGottenEvent event = new HopperGottenEvent(item, hopper);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            CraftDropItem.remove(entityDropItem, DropItemDeathEvent.DeathCause.HOPPER_GOTTEN);
            hopper.getInventory().addItem(event.getItemStack());
            if (item.getAmount() != entityDropItem.getItemStack().getAmount())
                CraftDropItem.spawnItem(itemStacks.get(0), entityDropItem.getLocation().clone().add(0, 1, 0), false);
        }
    }

    public static void fillPlayerInventory(final Player player, final EntityDropItem entityDropItem) {
        if (!player.getInventory().containsAtLeast(entityDropItem.getItemStack(), 1)
                && player.getInventory().firstEmpty() == -1)
            return;
        final Inventory inventory = Bukkit.createInventory(null, InventoryType.PLAYER);
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            if (player.getInventory().getItem(i) == null)
                continue;
            final ItemStack itemStack = player.getInventory().getItem(i).clone();
            if (itemStack != null)
                inventory.setItem(i, itemStack);
        }
        final Map<Integer, ItemStack> itemStacks = inventory.addItem(entityDropItem.getItemStack().clone());
        final ItemStack item = entityDropItem.getItemStack().clone();
        if (!itemStacks.isEmpty())
            item.setAmount(item.getAmount() - itemStacks.get(0).getAmount());
        if (item.getAmount() == 0)
            return;
        final PlayerGottenEvent event = new PlayerGottenEvent(item, player);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            CraftDropItem.remove(entityDropItem, DropItemDeathEvent.DeathCause.PLAYER_GOTTEN);
            player.getInventory().addItem(event.getItemStack());
            if (item.getAmount() != entityDropItem.getItemStack().getAmount())
                CraftDropItem.spawnItem(itemStacks.get(0), entityDropItem.getLocation().clone().add(0, 1, 0), false);
        }
    }

    public static void forceDelete(final File file) {
        if (file.isFile())
            file.delete();
        else
            for (final File f : file.listFiles())
                DropItemUtil.forceDelete(f);
    }

    public static void playSound(final Player player, final Block block) {
        try {
            final Object nmsblock = NMSManager.getMethod(NMSManager.getCraftClass("util.CraftMagicNumbers"), "getBlock",
                    new Class[]{Material.class}).invoke(null, block.getType());
            final Field stepSound = NMSManager.getField(NMSManager.getNMSClass("Block"), "stepSound");
            final Object sound = stepSound.get(nmsblock);
            final int version = NMSManager.getVersionInt();
            final Object nmsWorld = NMSManager.getMethod(NMSManager.CraftWorld, "getHandle", new Class[]{})
                    .invoke(block.getWorld());
            if (version == 8) {
                final String sound_str = (String) NMSManager
                        .getMethod(sound.getClass(), "getPlaceSound", new Class[]{}).invoke(sound, new Object[]{});
                NMSManager.getMethod(NMSManager.World, "makeSound",
                        new Class[]{double.class, double.class, double.class, String.class, float.class,
                                float.class})
                        .invoke(nmsWorld, block.getLocation().getX(), block.getLocation().getY(),
                                block.getLocation().getZ(), sound_str, 1f, 0.8f);
            } else {
                final Object block_position = NMSManager
                        .getConstructor(NMSManager.getNMSClass("BlockPosition"),
                                new Class[]{double.class, double.class, double.class})
                        .newInstance(block.getLocation().getX(), block.getLocation().getY(),
                                block.getLocation().getZ());
                final Object sound_effect = NMSManager
                        .getMethod(NMSManager.getNMSClass("SoundEffectType"), "e", new Class[]{})
                        .invoke(sound);
                Object category = null;
                for (final Object e : NMSManager.getNMSClass("SoundCategory").getEnumConstants())
                    if (e.toString().equalsIgnoreCase("BLOCKS"))
                        category = e;
                NMSManager
                        .getMethod(NMSManager.World, "a",
                                new Class[]{NMSManager.getNMSClass("EntityHuman"),
                                        NMSManager.getNMSClass("BlockPosition"), NMSManager.getNMSClass("SoundEffect"),
                                        NMSManager.getNMSClass("SoundCategory"), float.class, float.class})
                        .invoke(nmsWorld, null, block_position, sound_effect, category, 1.0f, 0.8f);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static String getLanguageVersion() {
        if (NMSManager.getVersionInt() >= 13)
            return "1.15";
        return "1.12";
    }

    public static String formatName(final ItemStack itemStack) {
        try {
            final Object nmsItemStack = NMSManager.getMethod(NMSManager.getCraftClass("inventory.CraftItemStack"), "asNMSCopy",
                    new Class[]{ItemStack.class}).invoke(null, itemStack);
            final Object name = NMSManager.getMethod(NMSManager.getNMSClass("ItemStack"), "getName").invoke(nmsItemStack);
            if (NMSManager.getVersionInt() >= 13) {
                /*if (name.getClass().getName().equals(NMSManager.getNMSClass("ChatComponentText").getName()))
                    //itemStack.getItemMeta().getDisplayName();
                    return (String) NMSManager.getMethod(NMSManager.getNMSClass("ChatComponentText"), "getText").invoke(name);
                else*/
                if (NMSManager.getVersionInt() == 13)
                    return DropItemConfiguration.translate(NMSManager.getField(NMSManager.getNMSClass("ChatMessage"), "f").get(name), true);
                else
                    return DropItemConfiguration.translate(NMSManager.getField(NMSManager.getNMSClass("ChatMessage"), "key").get(name), true);
            } else {
                //no
                final String str = (String) name;
                if (str.startsWith("Spawn")) {
                    final String temp = str.substring(6);
                    return DropItemConfiguration.translate("Spawn", false) + " " + DropItemConfiguration.translate(temp, false);
                }
                return DropItemConfiguration.translate(name, false);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            return "THIS IS AN ERROR.";
        }
    }
}
