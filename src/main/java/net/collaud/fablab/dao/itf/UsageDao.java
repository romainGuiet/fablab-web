package net.collaud.fablab.dao.itf;

import java.util.Date;
import java.util.List;
import net.collaud.fablab.data.UsageDetailEO;
import net.collaud.fablab.data.UsageEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
public interface UsageDao {

	public List<UsageDetailEO> getByUser(UserEO user, int limit) throws FablabException;

	public UsageEO add(UsageEO current) throws FablabException;
	
	public List<UsageDetailEO> getByIds(List<Integer> ids) throws FablabException;
	
	public void removeById(int usageId) throws FablabException;

	public List<UsageDetailEO> getAllBetween(Date dateBefore, Date dateAfter);

}
