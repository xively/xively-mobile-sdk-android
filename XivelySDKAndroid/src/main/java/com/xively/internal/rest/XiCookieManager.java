package com.xively.internal.rest;

import com.xively.internal.logger.LMILog;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class XiCookieManager extends CookieManager {
    private static final String TAG = "XiCookieManager";
    private static final LMILog log = new LMILog(TAG);
    static {
        log.getClass();
    }

    private final String xiAccessTokenLabel = "xively-access-token";

    private String accessToken = null;
    private AccessTokenUpdateListener accessTokenUpdateListener;

    public String getCurrentAccessToken(){
        return accessToken;
    }

    public void setAccessTokenListener(AccessTokenUpdateListener accessTokenUpdateListener){
        this.accessTokenUpdateListener = accessTokenUpdateListener;
    }

    @Override
    public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
        super.put(uri, responseHeaders);

        if (responseHeaders != null) {

            if (responseHeaders.get(xiAccessTokenLabel) != null) {
                for (String newAccessToken : responseHeaders.get(xiAccessTokenLabel)) {
                    if (!newAccessToken.equals(accessToken)) {
                        accessToken = newAccessToken;
                        if (accessTokenUpdateListener != null) accessTokenUpdateListener.onAccessTokenUpdated(accessToken);
                        log.d("New access token set.");
                    }
                }
            }

            if (responseHeaders.get("Set-Cookie") != null) {
                for (String string : responseHeaders.get("Set-Cookie")) {
                    if (string.startsWith(xiAccessTokenLabel)) {
                        String newAccessToken = string.substring(xiAccessTokenLabel.length() + 1);
                        int idx = newAccessToken.indexOf(";");
                        if (idx >= 0){
                            newAccessToken = newAccessToken.substring(0, idx);
                        }
                        if (!newAccessToken.equals(accessToken)) {
                            accessToken = newAccessToken;
                            if (accessTokenUpdateListener != null) accessTokenUpdateListener.onAccessTokenUpdated(accessToken);
                            log.d("New access token set.");
                        }
                    }
                }
            }
        }

    }

    public interface AccessTokenUpdateListener{
        void onAccessTokenUpdated(String newAccessToken);
    }
}
