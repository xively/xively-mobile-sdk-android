package com.xively.messaging;

public interface XivelyDeviceAssociationCallback {

    /**
     * Enum of possible association error causes.
     */
    public enum AssociationError {UNAUTHORIZED, ASSOCIATION_ERROR, INVALID_CODE, SERVER_INTERNAL_ERROR, SERVICE_UNAVAILABLE}

    /**
     * Successful association event. The device has been associated with the end user's Xively account.
     */
    public void onAssociationSuccess();

    /**
     * Failed association event.
     * The nature of the error is signalled by the callback parameter {@param associationError}.
     *
     * @param associationError An {@link XivelyDeviceAssociationCallback.AssociationError} enum.
     */
    public void onAssociationFailure(AssociationError associationError);
}
