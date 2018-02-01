package com.xively.internal.auth;

import android.os.Build;

import com.xively.XiSession;
import com.xively.auth.XiAuthentication;
import com.xively.auth.XiAuthenticationCallback;
import com.xively.internal.DependencyInjector;
import com.xively.internal.XiSessionImpl;
import com.xively.internal.account.XivelyAccount;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.auth.LoginUser;
import com.xively.internal.rest.blueprint.BlueprintWebServices;
import com.xively.sdk.BuildConfig;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class XiAuthenticationImpl implements XiAuthentication {
    private static final String TAG = "XiAuthentication";
    private static final LMILog log = new LMILog(TAG);

    static {
        log.getClass();
    }

    private boolean canceled = false;
    private final Object cancelSync = new Object();

    public XiAuthenticationImpl() {
        startupLog();
    }

    @Override
    public void requestAuth(String email, String password, String accountId,
                            final XiAuthenticationCallback callback) {

        DependencyInjector.get().authWebServices()
                .loginUser(email, password, accountId, new Callback<LoginUser.Response>() {
                    @Override
                    public void onResponse(Call<LoginUser.Response> call, Response<LoginUser.Response> response) {
                        LoginUser.Response loginResponse = response.body();

                        if (loginResponse != null &&
                                loginResponse.jwt != null &&
                                !loginResponse.jwt.equals("")) {
                            log.i("Authentication success. Acquiring credentials...");
                            setAuthorizationHeaders(loginResponse.jwt);
                            acquireCredentials(callback, loginResponse.jwt);
                        } else {
                            log.w("Invalid auth response.");
                            callback.authenticationFailed(
                                    XiAuthenticationCallback.XiAuthenticationError.INTERNAL_ERROR);
                        }
                    }

                    // TODO
                    @Override
                    public void onFailure(Call<LoginUser.Response> call, Throwable t) {
                        log.i("Authentication failed: " + t);
                        XiAuthenticationCallback.XiAuthenticationError error =
                                XiAuthenticationCallback.XiAuthenticationError.UNEXPECTED_ERROR;

                        if (t != null) {
                            if (t.getCause() instanceof IOException) {
                                error = XiAuthenticationCallback.XiAuthenticationError.NETWORK_ERROR;
                            } /*  if (retrofitError.getResponse() != null) {
                                switch (retrofitError.getResponse().getStatus()) {
                                    case 401:
                                        error = XiAuthenticationCallback.XiAuthenticationError.INVALID_CREDENTIALS;
                                        break;
                                    case 500:
                                    case 503:
                                        error = XiAuthenticationCallback.XiAuthenticationError.INTERNAL_ERROR;
                                        break;
                                }
                            } */
                        }

                        callback.authenticationFailed(error);
                    }
                });
    }

    public void setOauthToken(String oAuthToken, XiAuthenticationCallback callback) {
        //FIXME: acquireCredentials(callback, oAuthToken);
        setAuthorizationHeaders(oAuthToken);
        createSessionObject(callback, null);
    }

    @Override
    public void cancel() {
        canceled = true;
    }

    private void startupLog() {
        log.i("Xively Mobile SDK startup");
        log.i("SDK Version: " + BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE);
        log.i("Build type: " + BuildConfig.BUILD_TYPE);
        log.i("Application ID: " + BuildConfig.APPLICATION_ID);

        log.i("Device information: \n" +
                (Build.BRAND == null ? "unknown" :
                        Build.BRAND.toUpperCase(Locale.US).charAt(0) +
                                Build.BRAND.substring(1, Build.BRAND.length()) + " " + Build.MODEL + "\n")
        );

        log.i("Manufacturer: " +
                (Build.MANUFACTURER == null ? "unknown" : Build.MANUFACTURER));

        log.i("OS version: " +
                (Build.VERSION.RELEASE == null ? "unknown" : Build.VERSION.RELEASE));
    }

    private void acquireCredentials(final XiAuthenticationCallback callback, final String jwt) {
        synchronized (cancelSync) {
            if (!canceled) {
                BlueprintWebServices blueprint = DependencyInjector.get().blueprintWebServices();
                blueprint.queryXivelyAccount(jwt,
                        new Callback<XivelyAccount>() {
                            @Override
                            public void onResponse(Call<XivelyAccount> call, Response<XivelyAccount> response) {
                                XivelyAccount accoutnResponse = response.body();
                                createSessionObject(callback, accoutnResponse);
                            }

                            @Override
                            public void onFailure(Call<XivelyAccount> call, Throwable t) {
                                log.i("Failed to acquire credentials.");
                                callback.authenticationFailed(
                                        XiAuthenticationCallback.XiAuthenticationError.INTERNAL_ERROR);
                            }
                        });
            } else {
                log.i("Authentication flow canceled.");
                callback.authenticationFailed(XiAuthenticationCallback.XiAuthenticationError.CANCELED);
            }
        }
    }

    private void createSessionObject(XiAuthenticationCallback callback, XivelyAccount xivelyAccount) {
        synchronized (cancelSync) {
            if (!canceled) {
                XiSession session = new XiSessionImpl();
                if (xivelyAccount != null) {
                    ((XiSessionImpl) session).setXivelyAccount(xivelyAccount);
                }
                callback.sessionCreated(session);
            } else {
                log.i("Authentication flow canceled.");
                callback.authenticationFailed(XiAuthenticationCallback.XiAuthenticationError.CANCELED);
            }
        }
    }

    private void setAuthorizationHeaders(String jwt) {
        DependencyInjector.get().provisionWebServices().setBearerAuthorizationHeader(jwt);
        DependencyInjector.get().timeSeriesWebServices().setBearerAuthorizationHeader(jwt);
        DependencyInjector.get().blueprintWebServices().setBearerAuthorizationHeader(jwt);
        DependencyInjector.get().setAuthorizationHeader(jwt);
    }
}
