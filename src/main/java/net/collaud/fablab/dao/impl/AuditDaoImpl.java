package net.collaud.fablab.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import net.collaud.fablab.dao.itf.AuditDAO;
import net.collaud.fablab.data.AuditEO;
import net.collaud.fablab.data.AuditEO_;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.type.AuditObject;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
public class AuditDaoImpl extends AbstractDAO<AuditEO> implements AuditDAO {
	
	public AuditDaoImpl() {
		super(AuditEO.class);
	}
	
	@Override
	public AuditEO addEntry(AuditEO lua) {
		return create(lua);
	}
	
	@Override
	public List<AuditEO> search(UserEO user, List<AuditObject> type, Date after, Date before, int limit) {
		if (after != null && before != null && after.after(before)) {
			return new ArrayList<>();
		}
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<AuditEO> cq = cb.createQuery(AuditEO.class);
		Root<AuditEO> audit = cq.from(AuditEO.class);
		
		List<Predicate> listPredicate = new ArrayList<>();
		if (user != null) {
			listPredicate.add(cb.equal(audit.get(AuditEO_.who), user));
		}
		
		if (type != null && !type.isEmpty()) {
			listPredicate.add(audit.get(AuditEO_.object).in(type));
		}
		
		if (after != null) {
			listPredicate.add(cb.greaterThanOrEqualTo(audit.get(AuditEO_.when), after));
		}
		if (before != null) {
			listPredicate.add(cb.lessThanOrEqualTo(audit.get(AuditEO_.when), before));
		}
		
		cq.where(cb.and(listPredicate.toArray(new Predicate[]{})));
		
		cq.orderBy(cb.desc(audit.get(AuditEO_.when)));
		TypedQuery<AuditEO> query = getEntityManager().createQuery(cq);
		query.setMaxResults(limit);
		return query.getResultList();
	}
	
}
