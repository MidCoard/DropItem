package com.focess.dropitem.util;

import com.focess.dropitem.DropItem;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.net.HttpURLConnection;

public class VersionUpdater {
    public static void checkForUpdate(final DropItem drop) {
        drop.getLogger().info(DropItemConfiguration.getMessage("VersionCheck"));
        final HttpUtil.HttpResponse response;
        try {
            response = HttpUtil.getFrom("https://api.github.com/repos/MIdCoard/DropItem/releases/latest");
        } catch (final Exception e) {
            e.printStackTrace();
            //100 for code error
            drop.getLogger().severe(DropItemConfiguration.getMessage("VersionCheckFail",100));
            return;
        }
        if (response.getCode() != HttpURLConnection.HTTP_OK) {
            //200 for website problem
            drop.getLogger().severe(DropItemConfiguration.getMessage("VersionCheckFail",200));
            return;
        }
        final JsonObject object = new GsonBuilder().create().fromJson(response.getReturnString(), JsonObject.class);
        if (object.has("message") && object.get("message").getAsString().equals("Not Found")) {
            //300 for url problem or asset not found
            drop.getLogger().severe(DropItemConfiguration.getMessage("VersionCheckFail",300));
            return;
        }
        final DropItemUtil.Version latestVersion = new DropItemUtil.Version(object.get("tag_name").getAsString());
        if (latestVersion.newerThan(new DropItemUtil.Version(drop.getDescription().getVersion())))
            drop.getLogger().info(DropItemConfiguration.getMessage("LatestVersion"));
        else drop.getLogger().info(DropItemConfiguration.getMessage("LowVersion",latestVersion.getVersion()));
    }
}
