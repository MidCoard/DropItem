package com.focess.dropitem.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class HttpUtil {
    public static HttpResponse getFrom(final String url) throws Exception {
        final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("accept", "application/vnd.github.v3+json");
        httpURLConnection.setRequestProperty("User-Agent", "DropItem Updater");
//        httpURLConnection.setRequestProperty("Content-Type", "application/json");
//            httpURLConnection.setRequestProperty("Content-Language", "en-US");
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDefaultUseCaches(true);
        httpURLConnection.setConnectTimeout(20000);
        httpURLConnection.setReadTimeout(20000);
//            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
//            dataOutputStream.writeBytes(paramString);
//            dataOutputStream.flush();
//            dataOutputStream.close();
        final BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8));
        final int responseCode = httpURLConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            final StringBuffer stringBuffer = new StringBuffer();
            String str;
            while ((str = bufferedReader.readLine()) != null) {
            stringBuffer.append(str);
            stringBuffer.append('\r');
        }
        bufferedReader.close();
        return new HttpResponse(responseCode, stringBuffer.toString());
        } else
            return new HttpResponse(responseCode, "");
    }

    public static void downloadFile(final String urlPath, final String targetPath) throws Exception {
        final File file = new File(targetPath);
        file.delete();
        int byteRead;
        final URL url = new URL(urlPath);
        final URLConnection connection = url.openConnection();
        final InputStream inStream = connection.getInputStream();
        final FileOutputStream fs = new FileOutputStream(file);
        final byte[] buffer = new byte[1204];
        while ((byteRead = inStream.read(buffer)) != -1)
            fs.write(buffer, 0, byteRead);
    }

    public static class HttpResponse {

        private final int code;
        private final String ret;

        private HttpResponse(final int code, final String ret) {
            this.code = code;
            this.ret = ret;
        }

        public int getCode() {
            return this.code;
        }

        public String getReturnString() {
            return this.ret;
        }

    }

}
