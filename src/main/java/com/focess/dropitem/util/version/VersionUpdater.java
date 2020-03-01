package com.focess.dropitem.util.version;

import com.focess.dropitem.DropItem;
import com.focess.dropitem.util.DropItemUtil;
import com.focess.dropitem.util.HttpUtil;
import com.focess.dropitem.util.NMSManager;
import com.focess.dropitem.util.Section;
import com.focess.dropitem.util.configuration.DropItemConfiguration;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.TimerTask;

public class VersionUpdater {
    private static boolean needUpdated;
    private static String version;
    private static String url;
    private static boolean downloaded;

    public static String getVersion() {
        return version;
    }

    public static boolean isNeedUpdated() {
        return needUpdated;
    }

    public static void checkForUpdate(final DropItem drop, final TimerTask task) {
        if (needUpdated) {
            for (final OfflinePlayer player: Bukkit.getOperators())
                if (player.isOnline())
                    ((Player) player).sendMessage(DropItemConfiguration.getMessage("LowVersion",VersionUpdater.getVersion()));
            if (downloaded)
                DropItemUtil.sendNoColouredMessage(DropItemConfiguration.getMessage("HaveDownloaded"));
            else downloadNewVersion(drop,url,version,task);
            return;
        }
        DropItemUtil.sendNoColouredMessage(DropItemConfiguration.getMessage("VersionCheck"));
        final HttpUtil.HttpResponse response;
        try {
            response = HttpUtil.getFrom("https://api.github.com/repos/MIdCoard/DropItem/releases/latest");
        } catch (final Exception e) {
            //100 for network problem
            e.printStackTrace();
            DropItemUtil.sendNoColouredErrorMessage(DropItemConfiguration.getMessage("VersionFail2", 100));
            return;
        }
        if (response.getCode() != HttpURLConnection.HTTP_OK) {
            //200 for website problem
            DropItemUtil.sendNoColouredErrorMessage(DropItemConfiguration.getMessage("VersionFail", 200));
            return;
        }
        if (NMSManager.getVersionInt() == 8) {
            try {
                final JSONObject object = (JSONObject) new JSONParser().parse(response.getReturnString());
                if (object.containsKey("message") && object.get("message").equals("Not Found")) {
                    //300 for url problem or asset not found
                    DropItemUtil.sendNoColouredErrorMessage(DropItemConfiguration.getMessage("VersionFail", 300));
                    return;
                }
                final Version latestVersion = new Version((String) object.get("tag_name"));
                if (latestVersion.newerThan(drop.getVersion())) {
                    DropItemUtil.sendNoColouredMessage(DropItemConfiguration.getMessage("LowVersion", latestVersion.getVersion()));
                    needUpdated = true;
                    version = latestVersion.toString();
                    try {
                        url = DropItemUtil.getDownloadUrl(object);
                    }
                    catch (final NullPointerException e) {
                        //500 for fixing download url fail
                        e.printStackTrace();
                        DropItemUtil.sendNoColouredErrorMessage(DropItemConfiguration.getMessage("VersionFail",500));
                    }
                    downloadNewVersion(drop,url,version,task);
                } else DropItemUtil.sendNoColouredMessage(DropItemConfiguration.getMessage("LatestVersion"));
            } catch (final ParseException e) {
                e.printStackTrace();
                //400 for unknown error
                DropItemUtil.sendNoColouredErrorMessage(DropItemConfiguration.getMessage("VersionFail", 400));
            }
        } else {
            final JsonObject object = new GsonBuilder().create().fromJson(response.getReturnString(), JsonObject.class);
            if (object.has("message") && object.get("message").getAsString().equals("Not Found")) {
                //300 for url problem or asset not found
                DropItemUtil.sendNoColouredErrorMessage(DropItemConfiguration.getMessage("VersionFail", 300));
                return;
            }
            final Version latestVersion = new Version(object.get("tag_name").getAsString());
            if (latestVersion.newerThan(drop.getVersion())) {
                DropItemUtil.sendNoColouredMessage(DropItemConfiguration.getMessage("LowVersion", latestVersion.getVersion()));
                needUpdated = true;
                try {
                    url = DropItemUtil.getDownloadUrl(object);
                }
                catch (final NullPointerException e) {
                    //500 for fixing download url fail
                    e.printStackTrace();
                    DropItemUtil.sendNoColouredErrorMessage(DropItemConfiguration.getMessage("VersionFail",500));
                }
                downloadNewVersion(drop,url,version,task);
            } else DropItemUtil.sendNoColouredMessage(DropItemConfiguration.getMessage("LatestVersion"));
        }
    }

    public static boolean isDownloaded() {
        return downloaded;
    }

    private static void downloadNewVersion(final DropItem drop, final String url, final String version, final TimerTask task) {
        final File target = new File(drop.getDataFolder(), "DropItem-" + version + ".jar");
        if (target.exists()) {
            DropItemUtil.sendNoColouredErrorMessage(DropItemConfiguration.getMessage("ReplaceError",target.getPath()));
            drop.setVersionUpdateReplacement(new VersionUpdateReplacement(drop,DropItemConfiguration.getMessage("ReplaceError",target.getPath()),DropItemConfiguration.getMessage("VersionFail",600)));
            downloaded = true;
            return;
        }
        Section.getInstance().startSection("Download",task);
        DropItemUtil.sendNoColouredMessage(DropItemConfiguration.getMessage("DownloadStart",url));
        try {
            HttpUtil.downloadFile(url,target.getPath());
        }
        catch(final Exception e) {
            e.printStackTrace();
            DropItemUtil.sendNoColouredErrorMessage(DropItemConfiguration.getMessage("DownloadFail"));
            Section.getInstance().endSection("download");
            return;
        }
        final long time = Section.getInstance().endSection("Download");
        DropItemUtil.sendNoColouredMessage(DropItemConfiguration.getMessage("DownloadSucceed",Double.toString(time/(double)1000)));
        downloaded = true;
        drop.setVersionUpdateReplacement(new VersionUpdateReplacement(drop,DropItemConfiguration.getMessage("ReplaceError",target.getPath()),DropItemConfiguration.getMessage("VersionFail",600)));
    }


    public static void update(final DropItem drop) {
        if (drop.getVersionUpdateReplacement() != null)
            drop.getVersionUpdateReplacement().run();
    }
}
