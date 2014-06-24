package net.collaud.fablab.service.itf;

import javax.ejb.Local;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.virtual.AccessDoorResponse;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Local
public interface SecurityService {

	AccessDoorResponse canOpenDoor(String rfid) throws FablabException;

	UserEO getCurrentUser() throws FablabException;
	
}
