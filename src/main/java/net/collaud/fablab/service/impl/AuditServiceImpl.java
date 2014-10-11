package net.collaud.fablab.service.impl;

import java.util.Date;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import net.collaud.fablab.dao.itf.AuditDAO;
import net.collaud.fablab.dao.itf.PaymentDao;
import net.collaud.fablab.dao.itf.UsageDao;
import net.collaud.fablab.data.AuditEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.type.AuditObject;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.security.RolesHelper;
import net.collaud.fablab.service.itf.AuditService;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AuditServiceImpl extends AbstractServiceImpl implements AuditService {

	@EJB
	private AuditDAO auditDAO;

	@EJB
	private UsageDao usageDAO;

	@EJB
	private PaymentDao paymentDAO;

	@Override
	@PermitAll
	public AuditEO addEntry(AuditEO entry) throws FablabException {
		return auditDAO.addEntry(entry);
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_USE_AUDIT})
	public List<AuditEO> search(UserEO user, List<AuditObject> type, Date after, Date before, String content, int limit) throws FablabException {
		return auditDAO.search(user, type, after, before, content, limit);
	}
}
