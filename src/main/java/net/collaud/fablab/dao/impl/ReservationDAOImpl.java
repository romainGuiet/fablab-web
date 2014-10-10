package net.collaud.fablab.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import net.collaud.fablab.dao.itf.ReservationDAO;
import net.collaud.fablab.data.ReservationEO;
import net.collaud.fablab.data.ReservationEO_;
import net.collaud.fablab.exceptions.FablabException;
import org.springframework.stereotype.Repository;

/**
 *
 * @author gaetan
 */
@Repository
public class ReservationDAOImpl extends AbstractDAO<ReservationEO> implements ReservationDAO {

	public ReservationDAOImpl() {
		super(ReservationEO.class);
	}

	@Override
	public ReservationEO save(ReservationEO reservation) throws FablabException {
		//FIXME check if no reservation before, use transaction
		if (reservation.getReservationId() > 0) {
			return edit(reservation);
		} else {
			return create(reservation);
		}
	}

	@Override
	public List<ReservationEO> findReservations(Date dateStart, Date dateEnd, List<Integer> machineIds) {

		if (dateStart != null && dateEnd != null && dateStart.after(dateEnd)) {
			return new ArrayList<>();
		}

		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ReservationEO> cq = cb.createQuery(ReservationEO.class);
		Root<ReservationEO> reservation = cq.from(ReservationEO.class);

		List<Predicate> listPredicate = new ArrayList<>();

		listPredicate.add(cb.greaterThanOrEqualTo(reservation.get(ReservationEO_.dateStart), dateStart));
		listPredicate.add(cb.lessThanOrEqualTo(reservation.get(ReservationEO_.dateEnd), dateEnd));
		
		if (machineIds != null) {
			listPredicate.add(reservation.get(ReservationEO_.machine).in(machineIds));
		}

		cq.where(cb.and(listPredicate.toArray(new Predicate[]{})));

		cq.orderBy(cb.desc(reservation.get(ReservationEO_.machine)));
		TypedQuery<ReservationEO> query = getEntityManager().createQuery(cq);
		return query.getResultList();
	}

}
