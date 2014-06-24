package net.collaud.fablab.dao.itf;

import java.util.List;
import javax.ejb.Local;
import net.collaud.fablab.data.PaymentEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Local
public interface PaymentDao {

	public List<PaymentEO> getByUser(UserEO user, int limit) throws FablabException;

	public PaymentEO add(PaymentEO current) throws FablabException;

	public List<PaymentEO> getByIds(List<Integer> ids);
	
}
