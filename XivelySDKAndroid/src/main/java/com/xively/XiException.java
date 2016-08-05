package com.xively;

/**
 * This <code>Exception</code> type is the common parent of exceptions thrown by the
 * APIs of the Xively SDK for Android.
 */
public class XiException extends Exception {

	private static final long serialVersionUID = 5435926374136242171L;

	XiException() {
		super();
	}
	
	XiException(String message){
		super(message);
	}
	
	XiException(Throwable wrappedThrowable){
		super(wrappedThrowable);
	}
	
	XiException(String message, Throwable wrappedThrowable){
		super(message, wrappedThrowable);
	}

    /**
     * <code>Exception</code> thrown when an operation requires an active network connection,
     * however the connection is not alive.
     */
	public static class NotConnectedException extends XiException {
		private static final long serialVersionUID = -569360052301518373L;
	}

    /**
     * <code>Exception</code> thrown by connection setup APIs.
     */
	public static class ConnectionException  extends XiException {
		private static final long serialVersionUID = -2446714659053678903L;
	}

    /**
     * <code>Exception</code> thrown by connection setup APIs when a timeout occurs.
     * Consult the API documentation for the timeout of each connection method.
     */
	public static class ConnectionTimeoutException  extends XiException {
		private static final long serialVersionUID = 6920570594190616578L;
	}

    /**
     * <code>Exception</code> thrown by messaging APIs when invalid or corrupt data has been found.
     */
	public static class XiMessageParserException extends XiException {
		private static final long serialVersionUID = 8405267450149501755L;
		
		public XiMessageParserException() {
			super();
		}

        @SuppressWarnings("SameParameterValue")
        public XiMessageParserException(String message) {
            super(message);
        }
		
		public XiMessageParserException(Throwable wrappedThrowable){
			super(wrappedThrowable);
		}
	}
}
