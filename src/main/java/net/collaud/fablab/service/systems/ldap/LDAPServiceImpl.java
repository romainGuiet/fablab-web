package net.collaud.fablab.service.systems.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import net.collaud.fablab.file.ConfigFileHelper;
import net.collaud.fablab.file.FileHelperFactory;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.security.RolesHelper;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
@RolesAllowed({RolesHelper.ROLE_ADMIN})
public class LDAPServiceImpl implements LDAPService {

	private static final Logger LOG = Logger.getLogger(LDAPServiceImpl.class);

	@Override
	@RolesAllowed({RolesHelper.ROLE_MANAGE_USERS})
	public List<LDAPUser> getAllActiveUsers() throws FablabException {
		long time = System.currentTimeMillis();
		List<LDAPUser> users = new ArrayList<>();
		try {
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, FileHelperFactory.getConfig().get(ConfigFileHelper.LDAP_URL));
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			//env.put(Context.SECURITY_PRINCIPAL, "cn=admin,cn=users,dc=collaud,dc=net"); // specify the username
			//env.put(Context.SECURITY_CREDENTIALS, "emf123");           // specify the password
			DirContext ctx = new InitialDirContext(env);

			NamingEnumeration<NameClassPair> enm = ctx.list("cn=users,dc=fablab");

			while (enm.hasMore()) {
				NameClassPair user = enm.next();
				String login = user.getName().replace("uid=", "");
				if (!login.equals("admin")) {
					users.add(new LDAPUser(login, user.getNameInNamespace()));
				}
			}

			enm.close();
			ctx.close();

		} catch (NamingException ex) {
			throw new FablabException("Cannot retrieve LDAP users", ex);
		}
		LOG.debug("LDAP fetch time : " + (System.currentTimeMillis() - time) + "ms");

		return users;
	}

}
