package com.focess.dropitem.util;

import com.focess.dropitem.DropItem;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigUpdater {

    private static final DropItemUtil.Version _8_0VERSION = new DropItemUtil.Version("8.0");
    private static final DropItemUtil.Version _8_1VERSION = new DropItemUtil.Version("8.1");
    private static final DropItemUtil.Version _8_2VERSION = new DropItemUtil.Version("8.2");

    public static void updateConfig(final DropItem drop, final DropItemUtil.Version configVersion, final DropItemUtil.Version jarVersion) {
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
        drop.saveConfig();
    }
}
