package com.xively.auth;

/**
 * This class encapsulates login services for Xively end user and OpenId authentication.
 * <p>
 * To sign in with Xively end user credentials you can use the <i>requestAuth</i> API
 * and pass the end user's credentials.
 * <p>
 * The OpenID Authentication with the Xively SDK requires the implementing application
 * to declare an intent-filter in the <code>AndroidManifest.xml</code> in order to enable the
 * transfer of the authorization data from the authentication service to the SDK.
 * <p>
 * This intent-filter must have the following data:
 * <p>
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
 * </pre>
 * <p>
 * <b>Important!</b>
 * In the above declaration you must substitute the <i>XIVELY_ACCOUNT_ID</i> with your actual
 * account Id assigned to you in the Xively system.
 */
public interface XiAuthentication {

    /**
     * Request authentication with Xively login credentials.
     * The results are passed back asynchronously via an {@link XiAuthenticationCallback} instance.
     * A successful login flow will provide an initialized {@link com.xively.XiSession} object
     * ready for all available operations.
     *
     * @param email     End user e-mail address.
     * @param password  End user password.
     * @param accountId The application builder organization's Xively account Id.
     * @param callback  callback for authentication results.
     */
    void requestAuth(String email, String password, String accountId, XiAuthenticationCallback callback);

    /**
     * Cancel any present and future authentication operation.
     * After calling cancel every auth operation will eventually make a failure callback and
     * will never return a session instance or other valid result.
     */
    void cancel();
}
