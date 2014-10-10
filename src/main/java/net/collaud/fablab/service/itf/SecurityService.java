package net.collaud.fablab.service.itf;

import java.util.List;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
public interface SecurityService {

	List<UserEO> getUsersWithDoorAccess() throws FablabException;

	UserEO getCurrentUser() throws FablabException;
	
}
