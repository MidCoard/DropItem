package com.focess.dropitem.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.focess.dropitem.Debug;
import com.focess.dropitem.DropItem;

public class DropItemUtil {
    
    private final static Array<Material> BanItems = new Array<>();
    
    @SuppressWarnings("unused")
    private static int anxiCode;
    
    public static void loadDefault(DropItem drop) {
        anxiCode = AnxiCode.getCode(DropItemUtil.class, drop);
        getBanItems(drop);
    }
    
    @SuppressWarnings("deprecation")
    private static void getBanItems(final DropItem drop) {
        try {
            final String banItems = drop.getConfig().getString("BanItem");
            for (final String banItem : banItems.split(",")){
                try {
                    int id = Integer.parseInt(banItem);
                    if (Material.getMaterial(id) == null)
                        continue;
                    BanItems.add(Material.getMaterial(id));
                }
                catch(Exception e) {
                    if (Material.getMaterial(banItem) == null)
                        continue;
                    BanItems.add(Material.getMaterial(banItem));
                }
            }
        } catch (final Exception e) {
            Debug.debug(e, "Something wrong in getting BanItems.");
        }
    }
    
    public static boolean checkBanItems(ItemStack itemStack) {
        if (BanItems.contains(itemStack.getType()))
            return false;
        return true;
    }

}
