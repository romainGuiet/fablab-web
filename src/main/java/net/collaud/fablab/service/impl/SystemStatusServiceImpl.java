package net.collaud.fablab.service.impl;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import net.collaud.fablab.dao.itf.SystemStatusDao;
import net.collaud.fablab.data.SystemStatusEO;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.security.RolesHelper;
import net.collaud.fablab.service.itf.SystemStatusService;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
@RolesAllowed({RolesHelper.ROLE_ADMIN})
public class SystemStatusServiceImpl extends AbstractServiceImpl implements SystemStatusService {

	@EJB
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
