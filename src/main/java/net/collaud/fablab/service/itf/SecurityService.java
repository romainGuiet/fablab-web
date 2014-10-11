package net.collaud.fablab.service.itf;

import java.util.List;
import javax.ejb.Local;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Local
public interface SecurityService {

	List<UserEO> getUsersWithDoorAccess() throws FablabException;

	UserEO getCurrentUser() throws FablabException;
	
}
