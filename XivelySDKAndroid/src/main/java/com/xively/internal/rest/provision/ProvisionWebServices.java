package com.xively.internal.rest.provision;

import com.xively.internal.logger.LMILog;

import retrofit2.Callback;


public class ProvisionWebServices {
    private static final String TAG = "ProvisionWebServices";
    private static final LMILog log = new LMILog(TAG);

    static {
        log.getClass();
    }

    private String authorizationHeader;
    private final StartAssociationWithCode associationApi;

    public ProvisionWebServices() {
        ProvisioningApiFactory provisioningApi = ProvisioningApiFactory.getInstance();
        this.associationApi = provisioningApi.getAssociationApi();
    }

    // unit test purposes
    public ProvisionWebServices(StartAssociationWithCode associationApi) {
        this.associationApi = associationApi;
    }

    public void setBearerAuthorizationHeader(String authorization) {
        this.authorizationHeader = "Bearer " + authorization;
        log.d("Auth header set for provision ws." + this.authorizationHeader);
    }

    /**
     * @param associationCode xively device association code
     * @param endUserId       xively end user ID
     * @param callback        Error codes:
     *                        200 - OK
     *                        400 - The association code is missing or invalid
     *                        401 - Unauthorized
     *                        404 - No device found with the specified association code
     *                        422 - mqtt provisioning error
     *                        500 - Internal server error
     *                        503 - The service is temporarily unavailable. Please retry later.
     */
    public void associateIoTDevice(final String associationCode, String endUserId, final Callback<StartAssociationWithCode.Response> callback) {
        final StartAssociationWithCode.Request request = new StartAssociationWithCode.Request();
        request.endUserId = endUserId;
        request.associationCode = associationCode;

        this.associationApi.startAssociationWithCode(this.authorizationHeader, request).enqueue(callback);
    }
}
