package net.collaud.fablab.service.itf;

import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import net.collaud.fablab.data.ReservationEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Local
public interface ReservationService {

	ReservationEO save(ReservationEO reservation) throws FablabException;

	void remove(ReservationEO reservation) throws FablabException;

	List<ReservationEO> findReservations(Date dateStart, Date dateEnd, List<Integer> machineIds) throws FablabException;

	
}
