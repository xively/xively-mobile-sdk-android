package com.xively.auth;

/**
 * This class encapsulates login services for Xively end user and OpenId authentication.
 *
 * To sign in with Xively end user credentials you can use the <i>requestAuth</i> API
 * and pass the end user's credentials.
 *
 * The OpenID Authentication with the Xively SDK requires the implementing application
 * to declare an intent-filter in the <code>AndroidManifest.xml</code> in order to enable the
 * transfer of the authorization data from the authentication service to the SDK.
 *
 * This intent-filter must have the following data:
 *
 * <pre>
 * {@code
 *     <intent-filter>
 *          <action android:name="android.intent.action.VIEW"/>
 *           <category android:name="android.intent.category.DEFAULT"/>
 *          <category android:name="android.intent.category.BROWSABLE"/>
 *          <data
 *              android:scheme="xi123456789"
 *              android:pathPrefix="/"
 *          />
 *      </intent-filter>
 * }
 *</pre>
 *
 * <b>Important!</b>
 *  In the above declaration you must substitute the <i>XIVELY_ACCOUNT_ID</i> with your actual
 * account Id assigned to you in the Xively system.
 */
public interface XiAuthentication {

    /**
     * Request authentication with Xively login credentials.
     * The results are passed back asynchronously via an {@link XiAuthenticationCallback} instance.
     * A successful login flow will provide an initialized {@link com.xively.XiSession} object
     * ready for all available operations.
     *
     * @param email End user e-mail address.
     * @param password End user password.
     * @param accountId The application builder organization's Xively account Id.
     * @param callback callback for authentication results.
     */
    void requestAuth(String email, String password, String accountId, XiAuthenticationCallback callback);

    /**
     * Request the start of an OpenId Authentication flow within the Xively system.
     * The <code>oAuthUriReceived</code> XiOAuthCallback method will provide an URL which
     * should be opened in the mobile browser or a <code>WebView</code>. The page opened will forward
     * the end user to the third party OpenId login page of the specified login provider.
     *
     * The successful login flow will eventually forward the browser to a URI having
     * your application specific scheme. If the scheme's intent-filter is registered correctly
     * in the application <code>AndroidManifest.xml</code>, the Activity processing the Intent
     * is responsible to set the data contained in the Intent's scheme specific part.
     *
     * <pre>
     * {@code
     *      if (intent.getData() != null){
     *        Uri data = intent.getData();
     *
     *          if (data.getScheme().toLowerCase(Locale.US).startsWith("xi")){
     *              String value = data.getEncodedSchemeSpecificPart().substring(2);//skip leading //
     *              if (!value.equals("")){
     *                  DependencyInjector.get().interactionsWebServices().setBearerAuthorizationHeader(value);
     *              }
     *          }
     *      }
     * }
     *</pre>
     *
     * This call is asynchronous.
     *
     * @param providerId Your Xively provider Id.
     * @param callback Callback object for the OAuth flow.
     */
    //void requestOAuth(String providerId, XiOAuthCallback callback);

    /**
     * For use with OpenId authentication.
     * The OpenId authentication will pass its result to your application using a url-schema
     * callback. To continue the authentication flow the resulting token must be set in the
     * Xively SDK using this method.
     *
     * @param oAuthToken the OpenId authentication token.
     */
    //void setOauthToken(String oAuthToken, XiAuthenticationCallback callback);

    /**
     * Cancel any present and future authentication operation.
     * After calling cancel every auth operation will eventually make a failure callback and
     * will never return a session instance or other valid result.
     */
    void cancel();
}
