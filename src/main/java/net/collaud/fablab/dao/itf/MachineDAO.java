package net.collaud.fablab.dao.itf;

import java.util.List;
import net.collaud.fablab.data.MachineEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
public interface MachineDAO {

	List<MachineEO> findAll() throws FablabException;

	MachineEO save(MachineEO current) throws FablabException;

	void remove(MachineEO current) throws FablabException;

	MachineEO find(long key) throws FablabException;

	MachineEO getById(Integer number) throws FablabException;

	List<MachineEO> getMachinesForUser(UserEO user) throws FablabException;
}
