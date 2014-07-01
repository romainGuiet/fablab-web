package net.collaud.fablab.service.impl;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import net.collaud.fablab.dao.itf.PriceDAO;
import net.collaud.fablab.data.PriceMachineEO;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.security.RolesHelper;
import net.collaud.fablab.service.itf.PriceService;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
@RolesAllowed({RolesHelper.ROLE_ADMIN})
public class PriceServiceImpl extends AbstractServiceImpl implements PriceService {

	private static final Logger LOG = Logger.getLogger(PriceServiceImpl.class);

	@EJB
	private PriceDAO priceDao;

	@Override
@RolesAllowed({RolesHelper.ROLE_USE_MACHINES})
	public List<PriceMachineEO> getAllCurrentMachinePrices() throws FablabException {
		return priceDao.getAllCurrentMachinePrices();
	}


}
