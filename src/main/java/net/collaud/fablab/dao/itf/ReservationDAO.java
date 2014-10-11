package net.collaud.fablab.dao.itf;

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
public interface ReservationDAO {

	public ReservationEO save(ReservationEO current) throws FablabException;

	public void remove(ReservationEO current) throws FablabException;

	public List<ReservationEO> findReservations(Date dateStart, Date dateEnd, List<Integer> machineIds) throws FablabException;

}
