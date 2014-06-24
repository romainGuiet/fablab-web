package net.collaud.fablab.dao.itf;

import javax.ejb.Local;
import net.collaud.fablab.data.MembershipTypeEO;
import net.collaud.fablab.data.PriceCotisationEO;
import net.collaud.fablab.data.PriceRevisionEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Local
public interface PriceDAO {

	public PriceRevisionEO getLastPriceRevision() throws FablabException;
	
	public PriceCotisationEO getPriceCotisationForUser(MembershipTypeEO user) throws FablabException;
	
}
