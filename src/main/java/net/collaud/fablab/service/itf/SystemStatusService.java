package net.collaud.fablab.service.itf;

import java.util.List;
import net.collaud.fablab.data.SystemStatusEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
public interface SystemStatusService {

	public SystemStatusEO getBySystemName(String name) throws FablabException;

	public List<SystemStatusEO> getAllSystemStatus() throws FablabException;
	
	public SystemStatusEO save(SystemStatusEO status) throws FablabException;

}
