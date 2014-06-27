package net.collaud.fablab.file;

import net.collaud.fablab.common.file.KeyEnum;

/**
 *
 * @author gaetan
 */
public enum ConfigFileHelper implements KeyEnum {

	DEV_MODE,
	ITEM_PER_PAGE,
	LDAP_URL,
	DEFAULT_MEMBERSHIP_TYPE,
	PASSWORD_SALT,
	WEBSERVICE_TOKEN;

	@Override
	public String getKey() {
		return toString();
	}

}
