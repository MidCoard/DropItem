package com.focess.dropitem.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    public static HttpResponse getFrom(final String url) throws Exception {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("GET");
//            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            httpURLConnection.setRequestProperty("Content-Language", "en-US");
            httpURLConnection.setDoOutput(true);
//            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
//            dataOutputStream.writeBytes(paramString);
//            dataOutputStream.flush();
//            dataOutputStream.close();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            final int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                final StringBuffer stringBuffer = new StringBuffer();
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    stringBuffer.append(str);
                    stringBuffer.append('\r');
                }
                bufferedReader.close();
                return new HttpResponse(responseCode,stringBuffer.toString());
            }
            else
                return new HttpResponse(responseCode,"");
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
