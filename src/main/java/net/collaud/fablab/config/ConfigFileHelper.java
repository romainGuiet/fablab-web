package net.collaud.fablab.config;

import net.collaud.fablab.common.file.KeyEnum;

/**
 *
 * @author gaetan
 */
public enum ConfigFileHelper implements KeyEnum {

	DEV_MODE("DEV_MODE"),
	ITEM_PER_PAGE("ITEM_PER_PAGE"),
	LDAP_URL("LDAP_URL"),
	DEFAULT_MEMBERSHIP_TYPE("DEFAULT_MEMBERSHIP_TYPE"),
	PASSWORD_SALT("PASSWORD_SALT");

	private ConfigFileHelper(String key) {
		this.key = key;
	}

	private final String key;

	@Override
	public String getKey() {
		return key;
	}

}
