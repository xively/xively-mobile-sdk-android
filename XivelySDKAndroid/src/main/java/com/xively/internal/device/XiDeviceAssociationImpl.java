package com.xively.internal.device;

import com.xively.internal.DependencyInjector;
import com.xively.internal.account.XivelyAccount;
import com.xively.internal.logger.LMILog;
import com.xively.internal.rest.provision.StartAssociationWithCode;
import com.xively.messaging.XivelyDeviceAssociationCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class XiDeviceAssociationImpl {
    private static final String TAG = "XiDeviceAssociationImpl";
    private static final LMILog log = new LMILog(TAG);
    private final XivelyAccount account;

    static {
        log.getClass();
    }

    public XiDeviceAssociationImpl(XivelyAccount xivelyEndUserAccount) {
        this.account = xivelyEndUserAccount;
    }

    public void startDeviceAssociation(final String associationCode, final XivelyDeviceAssociationCallback callback) {
        DependencyInjector.get().provisionWebServices().associateIoTDevice(
                associationCode,
                account.getUserName(),
                new Callback<StartAssociationWithCode.Response>() {

                    @Override
                    public void onResponse(
                            Call<StartAssociationWithCode.Response> call,
                            Response<StartAssociationWithCode.Response> response
                    ) {
                        StartAssociationWithCode.Response associationResponse = response.body();
                        if (associationResponse != null) {
                            log.d(associationResponse.toString());
                            switch (response.code()) {
                                case 401:
                                    callback.onAssociationFailure(
                                            XivelyDeviceAssociationCallback.AssociationError.UNAUTHORIZED
                                    );
                                    break;
                                case 400:
                                case 404:
                                    callback.onAssociationFailure(
                                            XivelyDeviceAssociationCallback.AssociationError.INVALID_CODE
                                    );
                                    break;
                                case 500:
                                    callback.onAssociationFailure(
                                            XivelyDeviceAssociationCallback.AssociationError.SERVER_INTERNAL_ERROR
                                    );
                                    break;
                                case 503:
                                    callback.onAssociationFailure(
                                            XivelyDeviceAssociationCallback.AssociationError.SERVICE_UNAVAILABLE
                                    );
                                    break;
                                default:
                                    callback.onAssociationFailure(
                                            XivelyDeviceAssociationCallback.AssociationError.ASSOCIATION_ERROR
                                    );
                                    break;
                            }

                            callback.onAssociationSuccess();
                        } else {
                            callback.onAssociationFailure(XivelyDeviceAssociationCallback.AssociationError.ASSOCIATION_ERROR);
                        }
                    }

                    @Override
                    public void onFailure(Call<StartAssociationWithCode.Response> call, Throwable t) {
                        callback.onAssociationFailure(XivelyDeviceAssociationCallback.AssociationError.ASSOCIATION_ERROR);
                    }
                });
    }
}
