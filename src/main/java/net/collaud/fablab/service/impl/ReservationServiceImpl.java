package net.collaud.fablab.service.impl;

import antlr.StringUtils;
import java.util.Date;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import net.collaud.fablab.dao.itf.ReservationDAO;
import net.collaud.fablab.data.ReservationEO;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.security.RolesHelper;
import net.collaud.fablab.service.itf.ReservationService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author gaetan
 */
@RolesAllowed({RolesHelper.ROLE_ADMIN})
@Service
public class ReservationServiceImpl extends AbstractServiceImpl implements ReservationService {

	private static final Logger LOG = Logger.getLogger(ReservationServiceImpl.class);

	@Autowired
	private ReservationDAO reservationDao;

	@Override
	@RolesAllowed({RolesHelper.ROLE_USE_MACHINES})
	public ReservationEO save(ReservationEO selectedUser) throws FablabException {
		//FIXME remove only reservation of itself
		return reservationDao.save(selectedUser);
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_USE_MACHINES})
	public void remove(ReservationEO reservation) throws FablabException {
		//FIXME remove only reservation of itself
		reservationDao.remove(reservation);
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_USE_MACHINES})
	public List<ReservationEO> findReservations(Date dateStart, Date dateEnd, List<Integer> machineIds) throws FablabException {
		List<ReservationEO> list = reservationDao.findReservations(dateStart, dateEnd, machineIds);
		if (LOG.isDebugEnabled()) {
			LOG.debug(list.size() + " reservations found between " + dateStart + " and " + dateEnd +" for machines "+machineIds);
		}
		return list;
	}

}
