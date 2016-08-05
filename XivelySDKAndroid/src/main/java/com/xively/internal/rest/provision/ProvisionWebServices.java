package com.xively.internal.rest.provision;

import com.xively.XiSdkConfig;
import com.xively.internal.Config;
import com.xively.internal.logger.LMILog;
import com.xively.sdk.BuildConfig;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class ProvisionWebServices {
    private static final String TAG = "ProvisionWebServices";
    private static final LMILog log = new LMILog(TAG);
    static {
        log.getClass();
    }

    private String authorizationHeader;
	private final RestAdapter restAdapter;

	public ProvisionWebServices() {
        String wsEndpoint;
		if (Config.CONN_USE_SSL){
            wsEndpoint = "https://";
        } else {
            wsEndpoint = "http://";
        }
        wsEndpoint += Config.provision_endpoint();
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                if (authorizationHeader != null){
                    request.addHeader("Authorization", authorizationHeader);
                }
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(wsEndpoint)
                .setRequestInterceptor(requestInterceptor)
                .build();

        if (BuildConfig.DEBUG) {
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        } else if (LMILog.getMinLogLevel() != XiSdkConfig.LogLevel.OFF){
            restAdapter.setLogLevel(RestAdapter.LogLevel.BASIC);
        } else {
            restAdapter.setLogLevel(RestAdapter.LogLevel.NONE);
        }
	}

    public void setBearerAuthorizationHeader(String authorization){
        this.authorizationHeader = "Bearer " + authorization;
        log.d("Auth header set for provision ws." + this.authorizationHeader );
    }

    //for unit testing
    public ProvisionWebServices(RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    /**
     *
     * @param associationCode xively device association code
     * @param endUserId xively end user ID
     * @param callback
     *      Error codes:
     *      200 - OK
     *      400 - The association code is missing or invalid
     *      401 - Unauthorized
     *      404 - No device found with the specified association code
     *      422 - mqtt provisioning error
     *      500 - Internal server error
     *      503 - The service is temporarily unavailable. Please retry later.
     */
    public void associateIoTDevice(final String associationCode, String endUserId, final Callback<StartAssociationWithCode.Response> callback){
        final StartAssociationWithCode.Request request = new StartAssociationWithCode.Request();
        request.endUserId = endUserId;
        request.associationCode = associationCode;

        final StartAssociationWithCode startAssociationWithCode = restAdapter.create(StartAssociationWithCode.class);

        new Thread(){
            @Override
            public void run() {
                startAssociationWithCode.startAssociationWithCode(request, callback);
            }
        }.start();
    }

}
