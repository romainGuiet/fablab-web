package net.collaud.fablab.cron;

import java.util.List;
import java.util.logging.Level;
import javax.annotation.security.RunAs;
import net.collaud.fablab.audit.AuditUtils;
import net.collaud.fablab.cron.system.AbstractSystem;
import net.collaud.fablab.cron.system.SystemStatusFactory;
import net.collaud.fablab.data.SystemStatusEO;
import net.collaud.fablab.data.type.AuditAction;
import net.collaud.fablab.data.type.AuditObject;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.security.RolesHelper;
import net.collaud.fablab.service.itf.AuditService;
import net.collaud.fablab.service.itf.SystemStatusService;
import net.collaud.fablab.service.systems.mail.MailService;
import org.apache.log4j.Logger;
import org.primefaces.component.schedule.Schedule;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author gaetan
 */
//FIXME remettre cron
@RunAs(RolesHelper.ROLE_SYSTEM)
public class SystemChecker {

	private static final Logger LOG = Logger.getLogger(SystemChecker.class);

	@Autowired
	private SystemStatusService systemStatusService;

	@Autowired
	private AuditService auditService;

	@Autowired
	private MailService mailService;

	public SystemChecker() {
	}

	//@Schedule(second = "0, 10, 20, 30, 40, 50", minute = "*", hour = "*")
	//@Schedule(second = "0", minute = "*", hour = "*")
	public void checkSystemStatus() {
		try {
			List<SystemStatusEO> listSystems = systemStatusService.getAllSystemStatus();
			LOG.info("Check status started, " + listSystems.size() + " systems to check");

			for (SystemStatusEO system : listSystems) {
				AbstractSystem content = SystemStatusFactory.getSystemStatusObject(system);
				boolean changed = content.executeCheck();
				LOG.info("Check of system " + system.getName() + ", changed = " + changed);
				if (changed) {
					system.setContent(content.marshal());
					systemStatusService.save(system);
					broadcastSystemStatusChanged(system, content.getReadableStatus(system));
				}
			}

		} catch (FablabException ex) {
			java.util.logging.Logger.getLogger(SystemChecker.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void broadcastSystemStatusChanged(SystemStatusEO status, String content) throws FablabException {		
		//insert int audit
		AuditUtils.addAudit(auditService, AuditObject.SYSTEM_STATUS, AuditAction.UPDATE,
				true,
				content);

		if (status.isNotify()) {
			//send mail
			mailService.sendMail("gaetancollaud@gmail.com", "System status changed", "System status changed : "+content);
		}
	}

}
