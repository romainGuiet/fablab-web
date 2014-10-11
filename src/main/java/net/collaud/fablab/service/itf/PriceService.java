package net.collaud.fablab.service.itf;

import java.util.List;
import javax.ejb.Local;
import net.collaud.fablab.data.PriceMachineEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Local
public interface PriceService {

	List<PriceMachineEO> getAllCurrentMachinePrices() throws FablabException;
}
