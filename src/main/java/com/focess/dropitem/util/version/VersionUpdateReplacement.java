package com.focess.dropitem.util.version;


import com.focess.dropitem.DropItem;
import com.focess.dropitem.util.DropItemUtil;
import com.focess.dropitem.util.configuration.DropItemConfiguration;
import org.bukkit.util.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


public class VersionUpdateReplacement {

    private final String err;
    private final DropItem drop;
    private final File latest;
    private final String fail;
    private final File now;

    public VersionUpdateReplacement(final DropItem drop, final String err, final String fail) {
        this.err = err;
        this.fail = fail;
        this.drop = drop;
        this.latest = new File(this.drop.getDataFolder(), "DropItem-" + VersionUpdater.getVersion() + ".jar");
        this.now = DropItemUtil.getPluginFile(drop);

    }

    public void run() {
        try {
            final File file = new File("plugins/DropItem/update.txt");
            if (file.exists())
                file.delete();
            final FileOutputStream fos = new FileOutputStream(file);
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            // 'if' is not necessary
            if (VersionUpdater.isNeedUpdated() && DropItemConfiguration.isVersionDownload() && VersionUpdater.isDownloaded()) {
                try {
                    if (!this.latest.exists()) {
                        writer.write("File is not exists...\r\n");
                        return;
                    }
                    int t = 0;
                    boolean flag = false;
                    while (!flag && t++<10) {
                        writer.write("System gc\r\n");
                        System.gc();
                        writer.write("Force delete old version...\r\n");
                        flag = this.now.delete();
                    }
                    if (flag) {
                        writer.write("Copy latest version...\r\n");
                        FileUtil.copy(this.latest, new File(this.drop.getDataFolder().getParentFile(), "DropItem-" + VersionUpdater.getVersion() + ".jar"));
                        writer.write("Delete latest version temp file...\r\n");
                        this.latest.delete();
                    }
                    else writer.write(this.err + "\r\n");
                } catch (final Exception e) {
                    e.printStackTrace();
                    //600 for replace error
                    writer.write(this.fail);
                }
            }
            writer.close();
            fos.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public File getUpdatedFile() {
        return new File("plugins",this.latest.getName());
    }
}
