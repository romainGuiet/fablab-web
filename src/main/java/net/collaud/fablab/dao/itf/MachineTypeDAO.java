package net.collaud.fablab.dao.itf;

import java.util.List;
import javax.ejb.Local;
import net.collaud.fablab.data.MachineTypeEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Local
public interface MachineTypeDAO {

	List<MachineTypeEO> getRestrictedMachineTypes() throws FablabException;
}
