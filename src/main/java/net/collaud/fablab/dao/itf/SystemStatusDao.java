package net.collaud.fablab.dao.itf;

import java.util.List;
import javax.ejb.Local;
import net.collaud.fablab.data.SystemStatusEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Local
public interface SystemStatusDao {

	public SystemStatusEO getBySystemName(String name) throws FablabException;

	public List<SystemStatusEO> getAllSystemStatus() throws FablabException;
	
	public SystemStatusEO save(SystemStatusEO status) throws FablabException;

}
