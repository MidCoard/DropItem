package com.focess.dropitem.util.version;

import com.focess.dropitem.DropItem;
import com.focess.dropitem.util.DropItemUtil;
import com.focess.dropitem.util.HttpUtil;
import com.focess.dropitem.util.NMSManager;
import com.focess.dropitem.util.Section;
import com.focess.dropitem.util.configuration.DropItemConfiguration;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.net.HttpURLConnection;

public class VersionUpdater {
    private static boolean needUpdated;
    private static String version;
    private static String url;
    private static boolean downloaded;
    private static String body;

    public static String getVersion() {
        return version;
    }

    public static boolean isNeedUpdated() {
        return needUpdated;
    }

    public static void checkForUpdate(final DropItem drop) {
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
                final Version latestVersion = new Version( object.get("tag_name").toString());
                final String name = object.get("name").toString();
                if (latestVersion.newerThan(drop.getVersion()) && !name.equals("Test")) {
                    DropItemUtil.sendNoColouredMessage(DropItemConfiguration.getMessage("LowVersion", latestVersion.getVersion()));
                    DropItemUtil.sendNoColouredMessage(DropItemConfiguration.getMessage("UpdateMessage"));
                    body = DropItemUtil.decodeUnicode(object.get("body").toString());
                    DropItemUtil.sendNoColouredMessage(body);
                    needUpdated = true;
                    version = latestVersion.toString();
                    try {
                        url = DropItemUtil.getDownloadUrl(object);
                    } catch (final NullPointerException e) {
                        //500 for fixing download url fail
                        e.printStackTrace();
                        DropItemUtil.sendNoColouredErrorMessage(DropItemConfiguration.getMessage("VersionFail", 500));
                    }
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
            final String name = object.get("name").getAsString();
            if (latestVersion.newerThan(drop.getVersion()) && !name.equals("Test")) {
                DropItemUtil.sendNoColouredMessage(DropItemConfiguration.getMessage("LowVersion", latestVersion.getVersion()));
                DropItemUtil.sendNoColouredMessage(DropItemConfiguration.getMessage("UpdateMessage"));
                body = DropItemUtil.decodeUnicode(object.get("body").getAsString());
                DropItemUtil.sendNoColouredMessage(body);
                needUpdated = true;
                version = latestVersion.toString();
                try {
                    url = DropItemUtil.getDownloadUrl(object);
                } catch (final NullPointerException e) {
                    //500 for fixing download url fail
                    e.printStackTrace();
                    DropItemUtil.sendNoColouredErrorMessage(DropItemConfiguration.getMessage("VersionFail", 500));
                }
            } else DropItemUtil.sendNoColouredMessage(DropItemConfiguration.getMessage("LatestVersion"));
        }
    }

    public static boolean isDownloaded() {
        return downloaded;
    }

    public static void downloadNewVersion(final DropItem drop, final Thread task) {
        downloadNewVersion(drop,task,false);
    }

    public static void downloadNewVersion(final DropItem drop, final Thread task, final boolean flag) {
        final File target = new File(drop.getDataFolder(), "DropItem-" + version + ".jar");
        if (target.exists() && !flag) {
            DropItemUtil.sendNoColouredErrorMessage(DropItemConfiguration.getMessage("ReplaceError", target.getPath()));
            drop.setVersionUpdateReplacement(new VersionUpdateReplacement(drop, DropItemConfiguration.getMessage("ReplaceError", target.getPath()), DropItemConfiguration.getMessage("VersionFail", 600)));
            downloaded = true;
            return;
        }
        Section.getInstance().startSection("Download", task,()->target.delete());
        DropItemUtil.sendNoColouredMessage(DropItemConfiguration.getMessage("DownloadStart", url));
        try {
            HttpUtil.downloadFile(url, target.getPath());
        } catch (final Exception e) {
            e.printStackTrace();
            DropItemUtil.sendNoColouredErrorMessage(DropItemConfiguration.getMessage("DownloadFail"));
            Section.getInstance().endSection("Download");
            downloaded = false;
            return;
        }
        final long time = Section.getInstance().endSection("Download");
        DropItemUtil.sendNoColouredMessage(DropItemConfiguration.getMessage("DownloadSucceed", Double.toString(time / (double) 1000)));
        downloaded = true;
        drop.setVersionUpdateReplacement(new VersionUpdateReplacement(drop, DropItemConfiguration.getMessage("ReplaceError", target.getPath()), DropItemConfiguration.getMessage("VersionFail", 600)));
    }


    public static void update(final DropItem drop) {
        if (drop.getVersionUpdateReplacement() != null)
            drop.getVersionUpdateReplacement().run();
    }

    public static File getUpdatedFile(final DropItem drop) {
        return drop.getVersionUpdateReplacement().getUpdatedFile();
    }
}
