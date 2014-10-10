package net.collaud.fablab.service.itf;

import java.util.Date;
import java.util.List;
import net.collaud.fablab.data.AuditEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.type.AuditObject;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
public interface AuditService {

	AuditEO addEntry(AuditEO entry) throws FablabException;

	List<AuditEO> search(UserEO user, List<AuditObject> type, Date after, Date before, String content, int limit) throws FablabException;
	
}
