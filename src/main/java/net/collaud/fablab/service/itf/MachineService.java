package net.collaud.fablab.service.itf;

import java.util.List;
import javax.ejb.Local;
import net.collaud.fablab.data.MachineEO;
import net.collaud.fablab.data.MachineTypeEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Local
public interface MachineService {
	
	MachineEO find(long key) throws FablabException;

	MachineEO save(MachineEO reservation) throws FablabException;

	void remove(MachineEO reservation) throws FablabException;

	List<MachineEO> getAllMachines() throws FablabException;

	List<MachineEO> getMachinesForUser(UserEO user) throws FablabException;
	
	List<MachineTypeEO> getRestrictedMachineTypes() throws FablabException;

	
}
