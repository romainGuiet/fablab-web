package net.collaud.fablab.service.systems.ldap;

import java.util.List;
import javax.ejb.Local;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Local
public interface LDAPService {
	
	List<LDAPUser> getAllActiveUsers() throws FablabException;
}
