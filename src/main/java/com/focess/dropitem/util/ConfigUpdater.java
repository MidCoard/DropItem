package com.focess.dropitem.util;

import com.focess.dropitem.DropItem;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigUpdater {

    public static void updateConfig(final DropItem drop) {
        final FileConfiguration config = drop.getConfig();
        config.set("Version", drop.getDescription().getVersion());
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
        drop.saveConfig();
        final File zhs = new File(drop.getDataFolder(), "language-zhs.yml");
        final File zht = new File(drop.getDataFolder(), "language-zht.yml");
        final File message = new File(drop.getDataFolder(),"message.yml");
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
    }
}
