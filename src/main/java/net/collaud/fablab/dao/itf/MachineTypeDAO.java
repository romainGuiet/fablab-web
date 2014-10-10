package net.collaud.fablab.dao.itf;

import java.util.List;
import net.collaud.fablab.data.MachineTypeEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
public interface MachineTypeDAO {

	List<MachineTypeEO> getRestrictedMachineTypes() throws FablabException;

	public List<MachineTypeEO> getAllMachineTypes() throws FablabException;
}
