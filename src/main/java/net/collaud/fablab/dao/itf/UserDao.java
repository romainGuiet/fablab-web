package net.collaud.fablab.dao.itf;

import java.util.List;
import javax.ejb.Local;
import net.collaud.fablab.data.MachineTypeEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Local
public interface UserDao {

	UserEO getByLogin(String login) throws FablabException;
	
	UserEO getByRFID(String rfid) throws FablabException;

	UserEO getById(int userId) throws FablabException;

	List<UserEO> findAll() throws FablabException;
	
	UserEO save(UserEO current) throws FablabException;

	void remove(UserEO current) throws FablabException;

	List<UserEO> getByIds(List<Integer> value) throws FablabException;
	
	UserEO saveMachineAuthorized(UserEO user, List<MachineTypeEO> listTypes) throws FablabException ;
	
}
