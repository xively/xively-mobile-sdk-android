/**
 * Base package for Xively Authentication APIs.
 *
 * The OpenID Authentication with the Xively SDK on Android requires the implementing application
 * to declare an intent-filter in the <code>AndroidManifest.xml</code> in order to enable the
 * transfer of the authorization data from the authentication service to the SDK.
 *
 * This intent-filter must have the following data:
 * <blockquote>
 * <pre>
 *     {@code
 *     <intent-filter>
 *          <action android:name="android.intent.action.VIEW"/>
 *           <category android:name="android.intent.category.DEFAULT"/>
 *          <category android:name="android.intent.category.BROWSABLE"/>
 *          <data android:scheme="xi123456789"
 *          android:pathPrefix="/"
 *      />
 *      }
 *</pre>
 *</blockquote>
 *
 * <b>Important!</b>
 * In the above declaration you must substitute the <i>XIVELY_ACCOUNT_ID</i> with your actual
 * account Id assigned to you in the Xively system.
 *
 */
package com.xively.auth;
