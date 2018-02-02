package com.xively.internal.device;

import com.xively.internal.DependencyInjector;
import com.xively.internal.account.XivelyAccount;
import com.xively.internal.rest.provision.StartAssociationWithCode;
import com.xively.messaging.XivelyDeviceAssociationCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class XiDeviceAssociationImpl {
    private final XivelyAccount account;

    public XiDeviceAssociationImpl(XivelyAccount xivelyEndUserAccount) {
        this.account = xivelyEndUserAccount;
    }

    public void startDeviceAssociation(String associationCode, final XivelyDeviceAssociationCallback callback) {
        DependencyInjector.get().provisionWebServices().associateIoTDevice(
                associationCode,
                account.getUserName(),
                new Callback<StartAssociationWithCode.Response>() {

                    @Override
                    public void onResponse(
                            Call<StartAssociationWithCode.Response> call,
                            Response<StartAssociationWithCode.Response> response
                    ) {
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
                    }

                    @Override
                    public void onFailure(Call<StartAssociationWithCode.Response> call, Throwable t) {
                        XivelyDeviceAssociationCallback.AssociationError error;
                        error = XivelyDeviceAssociationCallback.AssociationError.ASSOCIATION_ERROR;

                        callback.onAssociationFailure(error);
                    }
                });
    }
}
