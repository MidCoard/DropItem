package com.focess.dropitem.util;

import com.focess.dropitem.DropItem;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.HttpURLConnection;

public class VersionUpdater {
    private static boolean needUpdated;
    private static String version;

    public static String getVersion() {
        return version;
    }

    public static boolean isNeedUpdated() {
        return needUpdated;
    }

    public static void checkForUpdate(final DropItem drop) {
        if (needUpdated) {
            for (final OfflinePlayer player: Bukkit.getOperators())
                if (player.isOnline())
                    ((Player) player).sendMessage(DropItemConfiguration.getMessage("LowVersion",VersionUpdater.getVersion()));
            return;
        }
        DropItemUtil.sendColouredMessageWithLabel(DropItemConfiguration.getMessage("VersionCheck"));
        final HttpUtil.HttpResponse response;
        try {
            response = HttpUtil.getFrom("https://api.github.com/repos/MIdCoard/DropItem/releases/latest");
        } catch (final Exception e) {
            //100 for network problem
            DropItemUtil.sendColouredErrorMessageWithLabel(DropItemConfiguration.getMessage("VersionCheckFail", 100));
            return;
        }
        if (response.getCode() != HttpURLConnection.HTTP_OK) {
            //200 for website problem
            DropItemUtil.sendColouredErrorMessageWithLabel(DropItemConfiguration.getMessage("VersionCheckFail", 200));
            return;
        }
        if (NMSManager.getVersionInt() == 8) {
            try {
                final JSONObject object = (JSONObject) new JSONParser().parse(response.getReturnString());
                if (object.containsKey("message") && object.get("message").equals("Not Found")) {
                    //300 for url problem or asset not found
                    DropItemUtil.sendColouredErrorMessageWithLabel(DropItemConfiguration.getMessage("VersionCheckFail", 300));
                    return;
                }
                final DropItemUtil.Version latestVersion = new DropItemUtil.Version((String) object.get("tag_name"));
                if (latestVersion.newerThan(drop.getVersion())) {
                    DropItemUtil.sendColouredMessageWithLabel(DropItemConfiguration.getMessage("LowVersion", latestVersion.getVersion()));
                    needUpdated = true;
                    version = latestVersion.toString();
                } else DropItemUtil.sendColouredMessageWithLabel(DropItemConfiguration.getMessage("LatestVersion"));
            } catch (final ParseException e) {
                e.printStackTrace();
                //400 for unknown error
                DropItemUtil.sendColouredErrorMessageWithLabel(DropItemConfiguration.getMessage("VersionCheckFail", 400));
            }
        } else {
            final JsonObject object = new GsonBuilder().create().fromJson(response.getReturnString(), JsonObject.class);
            if (object.has("message") && object.get("message").getAsString().equals("Not Found")) {
                //300 for url problem or asset not found
                DropItemUtil.sendColouredErrorMessageWithLabel(DropItemConfiguration.getMessage("VersionCheckFail", 300));
                return;
            }
            final DropItemUtil.Version latestVersion = new DropItemUtil.Version(object.get("tag_name").getAsString());
            if (latestVersion.newerThan(drop.getVersion())) {
                DropItemUtil.sendColouredMessageWithLabel(DropItemConfiguration.getMessage("LowVersion", latestVersion.getVersion()));
                needUpdated = true;
                version = latestVersion.toString();
            } else DropItemUtil.sendColouredMessageWithLabel(DropItemConfiguration.getMessage("LatestVersion"));
        }
    }
}
