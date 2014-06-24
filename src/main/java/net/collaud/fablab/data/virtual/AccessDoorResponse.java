package net.collaud.fablab.data.virtual;

import net.collaud.fablab.data.UserEO;

/**
 *
 * @author gaetan
 */
public class AccessDoorResponse {

	private final boolean granted;
	private final String rfid;
	private final UserEO user;

	public AccessDoorResponse(boolean granted, String rfid, UserEO user) {
		this.granted = granted;
		this.rfid = rfid;
		this.user = user;
	}

	public boolean isGranted() {
		return granted;
	}

	public String getRfid() {
		return rfid;
	}

	public UserEO getUser() {
		return user;
	}

}
