package net.collaud.fablab.service.impl;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import net.collaud.fablab.dao.itf.MachineDAO;
import net.collaud.fablab.dao.itf.MachineTypeDAO;
import net.collaud.fablab.data.MachineEO;
import net.collaud.fablab.data.MachineTypeEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.security.RolesHelper;
import net.collaud.fablab.service.itf.MachineService;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
@RolesAllowed({RolesHelper.ROLE_ADMIN})
public class MachineServiceImpl extends AbstractServiceImpl implements MachineService {

	private static final Logger LOG = Logger.getLogger(MachineServiceImpl.class);

	@EJB
	private MachineDAO machineDao;
	
	@EJB
	private MachineTypeDAO machineTypeDao;

	@Override
	@RolesAllowed({RolesHelper.ROLE_MANAGE_MACHINES})
	public MachineEO save(MachineEO reservation) throws FablabException {
		return machineDao.save(reservation);
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_MANAGE_MACHINES})
	public void remove(MachineEO reservation) throws FablabException {
		machineDao.remove(reservation);
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_MANAGE_MACHINES, RolesHelper.ROLE_USE_MACHINES})
	public List<MachineEO> getAllMachines() throws FablabException {
		return machineDao.findAll();
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_MANAGE_MACHINES, RolesHelper.ROLE_USE_MACHINES})
	public List<MachineEO> getMachinesForUser(UserEO user) throws FablabException {
		return machineDao.getMachinesForUser(user);
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_MANAGE_MACHINES, RolesHelper.ROLE_USE_MACHINES})
	public MachineEO find(long key) throws FablabException {
		return machineDao.find(key);
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_MANAGE_MACHINES, RolesHelper.ROLE_USE_MACHINES})
	public List<MachineTypeEO> getRestrictedMachineTypes() throws FablabException {
		return machineTypeDao.getRestrictedMachineTypes();
	}


}
