package net.collaud.fablab.dao.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.collaud.fablab.dao.itf.MachineTypeDAO;
import net.collaud.fablab.data.MachineTypeEO;
import net.collaud.fablab.data.MachineTypeEO_;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
public class MachineTypeDAOImpl extends AbstractDAO<MachineTypeEO> implements MachineTypeDAO {

	public MachineTypeDAOImpl() {
		super(MachineTypeEO.class);
	}

	@Override
	public List<MachineTypeEO> getRestrictedMachineTypes() throws FablabException {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<MachineTypeEO> cq = cb.createQuery(MachineTypeEO.class);

		Root<MachineTypeEO> machineType = cq.from(MachineTypeEO.class);
		cq.where(cb.equal(machineType.get(MachineTypeEO_.restricted), true));
		cq.orderBy(cb.desc(machineType.get(MachineTypeEO_.name)));

		TypedQuery<MachineTypeEO> query = getEntityManager().createQuery(cq);
		return query.getResultList();
	}

	@Override
	public List<MachineTypeEO> getAllMachineTypes() throws FablabException {
		return findAll();
	}

}
