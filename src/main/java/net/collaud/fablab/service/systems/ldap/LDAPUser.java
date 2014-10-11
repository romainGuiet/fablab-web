package net.collaud.fablab.service.systems.ldap;

/**
 *
 * @author gaetan
 */
public class LDAPUser {

	private String login;
	private String fullname;

	public LDAPUser(String login, String fullname) {
		this.login = login;
		this.fullname = fullname;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

}
