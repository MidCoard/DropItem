package com.focess.dropitem.util.configuration;

import com.focess.dropitem.DropItem;
import com.focess.dropitem.util.DropItemUtil;
import com.focess.dropitem.util.NMSManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.EulerAngle;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DropItemConfiguration {

    private static final List<String> LANGUAGEMARK = Lists.newArrayList("zh_CN", "zh_TW");

    private static final List<Material> banItems = Lists.newArrayList();
    private static final List<ItemStackAngle> angles = Lists.newArrayList();
    private static double height;
    private static String pickForm;
    private static String dropForm;
    private static boolean showItemInfo;
    private static boolean naturalSpawn;
    private static boolean allowedPlayer;
    private static List<String> allowedPlayers;
    private static boolean enableCoverBlock;
    private static boolean enableAliases;
    private static boolean dropItemAI;

    private static Map<String, String> languages = Maps.newHashMap();

    private static String language;
    private static int pitchX;
    private static int pitchY;
    private static int pitchZ;
    private static int refreshTime;
    private static boolean refresh;
    private static Map<String, String> originalLanguages = Maps.newHashMap();
    private static int waitingTime;

    private static final Map<String, String> messages = Maps.newHashMap();

    private static boolean versionCheck;
    private static int versionCheckCycle;
    private static boolean checkCycle;

    private static boolean versionDownload;

    private static boolean enableStats;

    public static boolean isEnableStats() {
        return enableStats;
    }

    public static boolean isVersionDownload() {
        return versionDownload;
    }

    public static boolean isVersionCheck() {
        return versionCheck;
    }

    public static int getVersionCheckCycle() {
        return versionCheckCycle;
    }

    public static boolean isCheckCycle() {
        return checkCycle;
    }

    public static String getLanguage() {
        return language;
    }

    public static String getMessage(final String message, final Object... format) {
        final String ret = messages.get(message);
        if (ret == null)
            return message;
        else {
            if (format.length == 0)
                return ret;
            final String[] temp = ret.split("%f%");
            final StringBuilder sb = new StringBuilder(temp[0]);
            for (int i = 1;i<temp.length && i-1<format.length;i++)
                sb.append(format[i-1]).append(temp[i]);
            return sb.toString();
        }
    }

    public static void loadDefault(final DropItem drop) {
        versionCheck = drop.getConfig().getBoolean("VersionCheck",true);
        getVersionCheckCycle(drop);
        versionDownload = drop.getConfig().getBoolean("VersionDownload",true);
        dropForm = drop.getConfig().getString("DropForm", "normal");
        pickForm = drop.getConfig().getString("PickForm", "normal");
        pitchX = drop.getConfig().getInt("PitchX", 100);
        pitchY = drop.getConfig().getInt("PitchY", 135);
        pitchZ = drop.getConfig().getInt("PitchZ", 100);
        enableCoverBlock = drop.getConfig().getBoolean("EnableCoverBlock", false);
        enableAliases = drop.getConfig().getBoolean("EnableAliases", true);
        enableStats = drop.getConfig().getBoolean("EnableStats", true);
        showItemInfo = drop.getConfig().getBoolean("ShowItemInfo", true);
        dropItemAI = drop.getConfig().getBoolean("DropItemAI", true);
        waitingTime = drop.getConfig().getInt("WaitingTime",20);
        language = drop.getConfig().getString("Language", "zh_CN");
        getLanguage(drop);
        naturalSpawn = drop.getConfig().getBoolean("NaturalSpawn", true);
        getAllowedPlayer(drop);
        allowedPlayers = Lists.newArrayList(drop.getConfig().getString("AllowedPlayers").split(","));
        getBanItems(drop);
        getRefreshTime(drop);
        height = drop.getConfig().getDouble("Height", 0.3d);
        getItemStackAngles(drop);
        getMessage(drop);
    }

    private static void getVersionCheckCycle(final DropItem drop) {
        try {
            versionCheckCycle = Integer.parseInt(drop.getConfig().getString("VersionCheckCycle"));
            checkCycle = true;
        }
        catch(final Exception e) {
            versionCheckCycle = 10800;
            checkCycle = drop.getConfig().getBoolean("VersionCheckCycle");
        }
    }

    private static void getMessage(final DropItem drop) {
        try {
            final YamlConfiguration yml;
            if (checkLanguageMark(DropItemConfiguration.getLanguage()))
                yml = YamlConfiguration.loadConfiguration(new InputStreamReader(drop.getResource("message" + getLanguage() + ".yml"), StandardCharsets.UTF_8));
            else
                yml = YamlConfiguration.loadConfiguration(new InputStreamReader(drop.getResource("message.yml"), StandardCharsets.UTF_8));
            final Set<String> keys = yml.getKeys(false);
            for (final String key : keys)
                messages.put(key, yml.getString(key));
        }
        catch(final Exception e) {
            e.printStackTrace();
        }
    }

    private static void getLanguage(final DropItem drop) {
        if (checkLanguageMark(DropItemConfiguration.getLanguage())) {
            originalLanguages = DropItemUtil.JSONtoMap(new InputStreamReader(drop.getResource(DropItemUtil.getLanguageVersion() + ".json")));
            languages = DropItemUtil.JSONtoMap(new InputStreamReader(drop.getResource(DropItemUtil.getLanguageVersion() + DropItemConfiguration.getLanguage() + ".json")));
        }
    }

    private static boolean checkLanguageMark(final String language) {
        return LANGUAGEMARK.contains(language);
    }

    private static void getAllowedPlayer(final DropItem drop) {
        if (!naturalSpawn)
            allowedPlayer = drop.getConfig().getBoolean("AllowedPlayer", true);
    }

    private static void getRefreshTime(final DropItem drop) {
        try {
            refreshTime = Integer.parseInt(drop.getConfig().getString("RefreshTime"));
            refresh = true;
        } catch (final Exception e) {
            refresh = drop.getConfig().getBoolean("RefreshTime");
            refreshTime = 300;
        }
    }

    public static int getWaitingTime(){
        return waitingTime;
    }

    public static boolean checkPickForm(final String form) {
        return pickForm.equals(form);
    }

    public static boolean checkAllowedPlayers(final String name) {
        return allowedPlayers.contains(name);
    }

    public static boolean checkDropForm(final String form) {
        return dropForm.equals(form);
    }

    public static boolean checkBanItems(final Material material) {
        return !banItems.contains(material);
    }


    public static boolean isShowItemInfo() {
        return showItemInfo;
    }

    public static boolean isNaturalSpawn() {
        return naturalSpawn;
    }

    public static double getHeight() {
        return height;
    }

    public static boolean isAllowedPlayer() {
        return allowedPlayer;
    }

    public static boolean isEnableCoverBlock() {
        return enableCoverBlock;
    }

    public static boolean isEnableAliases() {
        return enableAliases;
    }

    public static boolean isDropItemAI() {
        return dropItemAI;
    }

    public static int getRefreshTime() {
        return refreshTime;
    }

    public static boolean isRefresh() {
        return refresh;
    }

    private static void getItemStackAngles(final DropItem drop) {
        final List<String> angle = drop.getConfig().getStringList("Angles");
        for (final String a : angle) {
            final String[] temp = a.trim().split(" ");
            if (temp.length != 4)
                continue;
            try {
                final int id = Integer.parseInt(temp[0]);
                if (Material.getMaterial(id) == null)
                    continue;
                angles.add(new ItemStackAngle(Material.getMaterial(id), Integer.parseInt(temp[1]),
                        Integer.parseInt(temp[2]), Integer.parseInt(temp[3])));
            } catch (final Exception e) {
                if (Material.getMaterial(temp[0]) == null)
                    continue;
                angles.add(new ItemStackAngle(Material.getMaterial(temp[0]), Integer.parseInt(temp[1]),
                        Integer.parseInt(temp[2]), Integer.parseInt(temp[3])));
            }

        }
    }

    private static void getBanItems(final DropItem drop) {
        final String banItem = drop.getConfig().getString("BanItem");
        for (final String item : banItem.split(","))
            try {
                final int id = Integer.parseInt(item);
                if (Material.getMaterial(id) == null)
                    continue;
                banItems.add(Material.getMaterial(id));
            } catch (final Exception e) {
                if (Material.getMaterial(item) == null)
                    continue;
                banItems.add(Material.getMaterial(item));
            }
    }

    public static List<ItemStackAngle> getAngle() {
        return angles;
    }

    public static EulerAngle getDefaultAngle() {
        return new EulerAngle(pitchX, pitchY, pitchZ);
    }

    public static String translate(final Object name, final boolean version) {
        if (version) {
            final String ret = languages.get(name);
            if (ret == null)
                return NMSManager.translateKey(name);
            return ret;
        } else {
            final String ret = languages.get(originalLanguages.get(name));
            if (ret == null)
                return (String) name;
            return ret;
        }
    }

    public static class ItemStackAngle {
        private final Material material;
        private final EulerAngle angle;

        public ItemStackAngle(final Material material, final int pitchX, final int pitchY, final int pitchZ) {
            this.material = material;
            this.angle = new EulerAngle(pitchX, pitchY, pitchZ);
        }

        public EulerAngle getAngle() {
            return this.angle;
        }

        public Material getMaterial() {
            return this.material;
        }
    }
}
