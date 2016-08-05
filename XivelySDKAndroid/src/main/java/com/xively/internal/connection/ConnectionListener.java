package com.xively.internal.connection;

/**
 * Listener for the messaging connection.
 */
public interface ConnectionListener {

    /**
     * Error types used in the <code>onError</code> event.
     */
	public enum ConnectionError {
		FAILED_TO_CONNECT, DISCONNECT_ERROR, CONNECTION_LOST, TIMED_OUT
	}

    /**
     * Event raised when the connection has been established and it is ready for send and receive.
     */
	public void onConnected();

    /**
     * Event raised when the connection has been reestablished after an unexpected connection loss.
     * To enable different workflows after reconnection the <code>onConnected</code> event is not raised.
     *
     * <code>onReconnecting</code> will always be raised before <code>onReconnected</code>.
     */
    public void onReconnected();

    /**
     * Event signalling an unexpected connection loss. If the connection can be reestablished,
     * <code>onReconnected</code> will be called, otherwise <code>onError</code> can be expected.
     */
    public void onReconnecting();

    /**
     * The connection is not receiving or sending messages any more. Cleanup may be still in progress
     * until <code>onClosed</code> is called.
     */
	public void onDisconnected();

    /**
     * The connection with the remote server has ended and the connection object is cleaned up.
     */
	public void onClosed();

    /**
     * If the connection has been unexpectedly lost, {@param error} will signal the probable cause.
     * For the different error types see {@link ConnectionListener.ConnectionError}.
     *
     * @param error The error details.
     */
	public void onError(ConnectionError error);
}
