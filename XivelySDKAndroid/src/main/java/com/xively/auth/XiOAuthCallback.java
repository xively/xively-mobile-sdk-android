package com.xively.auth;

/**
 * Callback for Open Authentication request to provide the url to be opened for the end user.
 */
public interface XiOAuthCallback {

    /**
     * Provides the oauth url to the SDK user. This url should be opened either in an external
     * web browser or a <code>WebView</code>.
     *
     * @param oauthUrl the oauth url
     *                 (ex.: https://accounts.google.com/o/oauth2/auth?client_id=123456789.apps.googleusercontent.com&response_type=code&scope=openid).
     */
     void oAuthUriReceived(String oauthUrl);

    /**
     * The authentication has failed.
     */
    void authenticationFailed();
}
