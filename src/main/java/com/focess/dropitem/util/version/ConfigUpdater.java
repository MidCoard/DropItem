package com.focess.dropitem.util.version;

import com.focess.dropitem.DropItem;
import com.focess.dropitem.util.DropItemUtil;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigUpdater {

    private static final Version _8_0VERSION = new Version("8.0");
    private static final Version _8_1VERSION = new Version("8.1");
    private static final Version _8_2VERSION = new Version("8.2");
    private static final Version _8_8VERSION = new Version("8.8");
    private static final Version _9_0VERSION = new Version("9.0");

    public static void updateConfig(final DropItem drop, final Version configVersion, final Version jarVersion) {
        final FileConfiguration config = drop.getConfig();
        config.set("Version", jarVersion.getVersion());
        if (!configVersion.isNew(_8_0VERSION)) {
            final File zhs = new File(drop.getDataFolder(), "language-zhs.yml");
            final File zht = new File(drop.getDataFolder(), "language-zht.yml");
            final File message = new File(drop.getDataFolder(), "message.yml");
            if (zhs.exists())
                zhs.delete();
            if (zht.exists())
                zht.delete();
            if (message.exists())
                message.delete();
            final File drops = new File(drop.getDataFolder(), "drops");
            if (drops.exists())
                DropItemUtil.forceDelete(drops);
            final File bugs = new File(drop.getDataFolder(), "bugs");
            if (bugs.exists())
                DropItemUtil.forceDelete(bugs);
            final String language;
            switch (config.getString("Language", "zhs")) {
                case "zhs": {
                    language = "zh_CN";
                    break;
                }
                case "zht": {
                    language = "zh_TW";
                    break;
                }
                default: {
                    language = "en_US";
                }
            }
            config.set("Language", language);
        }
        if (!configVersion.isNew(_8_1VERSION)) {
            drop.getConfig().set("VersionCheck",true);
            drop.getConfig().set("VersionCheckCycle",10800);
        }
        if (!configVersion.isNew(_8_2VERSION)) {
            drop.getConfig().set("VersionDownload",true);
        }
        if (!configVersion.isNew(_8_8VERSION)) {
            drop.getConfig().set("WaitingTime",20);
        }
        if (!configVersion.isNew(_9_0VERSION)) {
            drop.getConfig().set("EnableStats",true);
        }
        drop.saveConfig();
    }
}
