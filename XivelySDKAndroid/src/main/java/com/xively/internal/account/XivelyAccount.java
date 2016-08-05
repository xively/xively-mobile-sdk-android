package com.xively.internal.account;

/**
 * Represents an internal Xively end user account.
 */
public class XivelyAccount {

	private final String clientId;
	private final String userName;
	private final String password;
	private final String displayName;

    /**
     *
     * @param clientId Xively Client's AccountId
     * @param userName End User Id
     * @param password End User Pass
     */
	public XivelyAccount(String clientId, String userName, String password) {
		this.clientId = clientId;
		this.userName = userName;
		this.password = password;
		this.displayName = "";
	}

	/**
	 *
	 * @param clientId Xively Client's AccountId
	 * @param userName End User Id
	 * @param password End User Pass
	 * @param displayName the display name of the end user.
	 */
	public XivelyAccount(String clientId, String userName, String password, String displayName) {
		this.clientId = clientId;
		this.userName = userName;
		this.password = password;
		this.displayName = displayName;
	}

	/**
	 * @return the clientId (Xively AccountId)
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the display name of the end user.
	 */
	public String getDisplayName(){
		return displayName;
	}
}
