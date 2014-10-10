package net.collaud.fablab.dao.itf;

import java.util.Date;
import java.util.List;
import net.collaud.fablab.data.SubscriptionEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */

public interface SubscriptionDao {

	public List<SubscriptionEO> getByUser(UserEO user, int limit) throws FablabException;

	public SubscriptionEO add(SubscriptionEO current) throws FablabException;

	public List<SubscriptionEO> getAllBetween(Date dateBefore, Date dateAfter);
	
}
