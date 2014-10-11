package net.collaud.fablab.service.impl;

import java.util.Date;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import net.collaud.fablab.dao.itf.AuditDAO;
import net.collaud.fablab.data.AuditEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.type.AuditObject;
import net.collaud.fablab.exceptions.BusinessException;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.security.RolesHelper;
import net.collaud.fablab.service.itf.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author gaetan
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = BusinessException.class)
public class AuditServiceImpl extends AbstractServiceImpl implements AuditService {

	@Autowired
	private AuditDAO auditDAO;

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
