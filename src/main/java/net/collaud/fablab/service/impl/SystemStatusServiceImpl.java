package net.collaud.fablab.service.impl;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import net.collaud.fablab.dao.itf.SystemStatusDao;
import net.collaud.fablab.data.SystemStatusEO;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.security.RolesHelper;
import net.collaud.fablab.service.itf.SystemStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author gaetan
 */
@RolesAllowed({RolesHelper.ROLE_ADMIN})
@Service
public class SystemStatusServiceImpl extends AbstractServiceImpl implements SystemStatusService {

	@Autowired
	private SystemStatusDao systemStatusDao;

	@Override
	@RolesAllowed({RolesHelper.ROLE_SYSTEM, RolesHelper.ROLE_USE_SYSTEM_STATUS})
	public List<SystemStatusEO> getAllSystemStatus() throws FablabException {
		return systemStatusDao.getAllSystemStatus();
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_SYSTEM, RolesHelper.ROLE_USE_SYSTEM_STATUS})
	public SystemStatusEO getBySystemName(String name) throws FablabException {
		return systemStatusDao.getBySystemName(name);
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_SYSTEM, RolesHelper.ROLE_MANAGE_SYSTEM_STATUS})
	public SystemStatusEO save(SystemStatusEO status) throws FablabException {
		return systemStatusDao.save(status);
	}

}
