package com.focess.dropitem.util;

import com.focess.dropitem.event.DropItemDeathEvent;
import com.focess.dropitem.event.HopperGottenEvent;
import com.focess.dropitem.event.PlayerGottenEvent;
import com.focess.dropitem.item.CraftDropItem;
import com.focess.dropitem.item.EntityDropItem;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

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
        IntStream.range(0, player.getInventory().getContents().length).filter(i -> player.getInventory().getItem(i) != null).forEachOrdered(i -> {
            final ItemStack itemStack = player.getInventory().getItem(i).clone();
            if (itemStack != null)
                inventory.setItem(i, itemStack);
        });
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

    public static String getLanguageVersion() {
        if (NMSManager.getVersionInt() >= 13)
            return "1.15";
        return "1.12";
    }

    public static void forceDelete(final File file) {
        if (!file.isFile())
            for (final File f : file.listFiles())
                DropItemUtil.forceDelete(f);

        file.delete();
    }

    public static Map<String, String> JSONtoMap(final Reader reader) {
        if (NMSManager.getVersionInt() == 8) {
            final JSONParser parser = new JSONParser();
            final ContainerFactory containerFactory = new ContainerFactory() {
                public List creatArrayContainer() {
                    return new LinkedList();
                }

                public Map createObjectContainer() {
                    return new LinkedHashMap();
                }
            };
            try {
                return (Map<String, String>) parser.parse(reader, containerFactory);
            } catch (final Exception e) {
                e.printStackTrace();
            }
            return Maps.newHashMap();
        } else
            return new GsonBuilder().create().fromJson(reader, new TypeToken<Map<String, String>>() {
            }.getType());
    }

    public static void sendColouredMessageWithLabel(final String message) {
        sendColouredMessage("[DropItem]" + message);
    }

    public static void sendNoColouredMessageWithLabel(final String message) {
        sendColouredMessageWithLabel(ChatColor.stripColor(message));
    }

    public static void sendColouredMessage(final String message) {
        System.out.println(assembleColouredMessage(message));
    }

    private static String assembleColouredMessage(final String message) {
        return message;
    }

    public static void sendColouredErrorMessageWithLabel(final String message) {
        sendColouredErrorMessage("[DropItem]" + message);
    }

    public static void sendColouredErrorMessage(final String message) {
        System.err.println(assembleColouredMessage(message));
    }

    public static String getDownloadUrl(final JsonObject obj) {
        if (obj.has("assets")) {
            try {
                final JsonObject element = obj.getAsJsonArray("assets").get(0).getAsJsonObject();
                if (element.has("browser_download_url"))
                    return element.get("browser_download_url").getAsString();
                else throw new NullPointerException(obj.toString());
            } catch (final Exception e) {
                throw new NullPointerException(e.toString());
            }

        } else
            throw new NullPointerException(obj.toString());
    }

    public static String getDownloadUrl(final JSONObject object) {
        if (object.containsKey("assets")) {
            try {
                final JSONObject element = (JSONObject) ((JSONArray) object.get("assets")).get(0);
                if (element.containsKey("browser_download_url"))
                    return (String) element.get("browser_download_url");
                else throw new NullPointerException(object.toString());
            } catch (final Exception e) {
                throw new NullArgumentException(e.toString());
            }
        } else throw new NullPointerException(object.toString());
    }

    public static void sendNoColouredErrorMessageWithLabel(final String message) {
        sendColouredErrorMessageWithLabel(ChatColor.stripColor(message));
    }

    public static void sendNoColouredErrorMessage(final String message) {
        sendColouredMessage(ChatColor.stripColor(message));
    }

    public static void sendNoColouredMessage(final String message) {
        sendColouredMessage(ChatColor.stripColor(message));
    }

    public static File getPluginFile(final JavaPlugin plugin) {
        try {
            final Method method = JavaPlugin.class.getDeclaredMethod("getFile");
            method.setAccessible(true);
            return (File) method.invoke(plugin);
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        } catch (final InvocationTargetException e) {
            e.printStackTrace();
        } catch (final NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isChinese(final char c) {
        final Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    public static String decodeUnicode(final String unicode) {
        final StringBuffer string = new StringBuffer();
        final String[] hex = unicode.split("\\\\u");
        for (int i = 0; i < hex.length; i++) {
            try {
                if (hex[i].length() >= 4) {//取前四个，判断是否是汉字
                    final String chinese = hex[i].substring(0, 4);
                    try {
                        final int chr = Integer.parseInt(chinese, 16);
                        final boolean isChinese = isChinese((char) chr);
                        if (isChinese) {//在汉字范围内
                            string.append((char) chr);
                            final String behindString = hex[i].substring(4);
                            string.append(behindString);
                        } else {
                            string.append(hex[i]);
                        }
                    } catch (final NumberFormatException e1) {
                        string.append(hex[i]);
                    }
                } else {
                    string.append(hex[i]);
                }
            } catch (final NumberFormatException e) {
                string.append(hex[i]);
            }
        }
        return string.toString();
    }

}
