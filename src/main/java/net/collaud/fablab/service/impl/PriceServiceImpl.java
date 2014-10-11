package net.collaud.fablab.service.impl;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import net.collaud.fablab.dao.itf.PriceDAO;
import net.collaud.fablab.data.PriceMachineEO;
import net.collaud.fablab.exceptions.BusinessException;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.security.RolesHelper;
import net.collaud.fablab.service.itf.PriceService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author gaetan
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = BusinessException.class)
public class PriceServiceImpl extends AbstractServiceImpl implements PriceService {

	private static final Logger LOG = Logger.getLogger(PriceServiceImpl.class);

	@Autowired
	private PriceDAO priceDao;

	@Override
	@RolesAllowed({RolesHelper.ROLE_USE_MACHINES})
	public List<PriceMachineEO> getAllCurrentMachinePrices() throws FablabException {
		return priceDao.getAllCurrentMachinePrices();
	}

}
