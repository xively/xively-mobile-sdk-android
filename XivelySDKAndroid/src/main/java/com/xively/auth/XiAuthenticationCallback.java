package com.xively.auth;

import com.xively.XiSession;

public interface XiAuthenticationCallback {

    enum XiAuthenticationError { INVALID_CREDENTIALS, INTERNAL_ERROR, NETWORK_ERROR, UNEXPECTED_ERROR, CANCELED }

    /**
     * The authentication has been successful and the application now is authorized to use
     * any API calls which require the end user to be signed in.
     *
     * @param xiSession Xively Session ready for all operations.
     */
    void sessionCreated(XiSession xiSession);

    /**
     * The authentication has failed.
     */
    void authenticationFailed(XiAuthenticationError errorType);

}
