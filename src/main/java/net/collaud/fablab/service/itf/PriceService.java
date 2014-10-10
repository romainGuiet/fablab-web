package net.collaud.fablab.service.itf;

import java.util.List;
import net.collaud.fablab.data.PriceMachineEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
public interface PriceService {

	List<PriceMachineEO> getAllCurrentMachinePrices() throws FablabException;
}
