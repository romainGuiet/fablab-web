package net.collaud.fablab.dao.itf;

import java.util.List;
import net.collaud.fablab.data.MembershipTypeEO;
import net.collaud.fablab.data.PriceCotisationEO;
import net.collaud.fablab.data.PriceMachineEO;
import net.collaud.fablab.data.PriceRevisionEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
public interface PriceDAO {

	public PriceRevisionEO getLastPriceRevision() throws FablabException;
	
	public PriceCotisationEO getPriceCotisationForUser(MembershipTypeEO user) throws FablabException;

	public List<PriceMachineEO> getAllCurrentMachinePrices() throws FablabException;
	
}
