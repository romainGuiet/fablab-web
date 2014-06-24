package net.collaud.fablab.cron;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import net.collaud.fablab.cron.system.AbstractSystem;
import net.collaud.fablab.cron.system.Door;
import net.collaud.fablab.cron.system.SimpleHost;
import net.collaud.fablab.cron.system.SystemStatusFactory;
import net.collaud.fablab.data.SystemStatusEO;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.security.RolesHelper;
import net.collaud.fablab.service.itf.AuditService;
import net.collaud.fablab.service.itf.SystemStatusService;
import net.collaud.fablab.service.systems.mail.MailService;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
@Startup
@Singleton
@RunAs(RolesHelper.ROLE_SYSTEM)
public class SystemChecker {

	private static final Logger LOG = Logger.getLogger(SystemChecker.class);

	@EJB
	private SystemStatusService systemStatusService;

	@EJB
	private AuditService auditService;

	@EJB
	private MailService mailService;

	private final List<SystemStatusEO> defaultSystems;

	public SystemChecker() {
		defaultSystems = new ArrayList<>();
		addDefaultSystem("door", new Door("172.17.10.50", 8083), true);
		addDefaultSystem("server1", new SimpleHost("localhost"), true);
		//addDefaultSystem("CNC1", new SimpleHost("cnc1.fablab.local"), true);
	}
	
	private void addDefaultSystem(String name, AbstractSystem system, boolean notify){
		try {
			defaultSystems.add(new SystemStatusEO(0, name, system.getClass().getName(), system.marshal(), new Date(), notify));
		} catch (FablabException ex) {
			LOG.error("Cannot add default system "+name, ex);
		}
	}

	//@Schedule(second = "0, 10, 20, 30, 40, 50", minute = "*", hour = "*")
	@Schedule(second = "0", minute = "*", hour = "*")
	public void checkSystemStatus() {
		try {
			List<SystemStatusEO> listSystems = systemStatusService.getAllSystemStatus();
			checkListSystemStatus(listSystems);
			LOG.info("Check status started, " + listSystems.size() + " systems to check");

			for (SystemStatusEO system : listSystems) {
				AbstractSystem content = SystemStatusFactory.getSystemStatusObject(system);
				boolean result = content.executeCheck();
				LOG.info("Check of system " + system.getName() + " : " + result);
				system.setContent(content.marshal());
				//TODO save only if changes
				//TODO send notification if changes (and if system need notification)
				systemStatusService.save(system);
			}

//		try {
//			boolean changed = false;
//			SystemStatusEO status = systemStatusService.getBySystemName(SYSTEM_NAME);
//			if (status == null) {
//				status = new SystemStatusEO(SYSTEM_NAME, SYSTEM_ADDRESS, SYSTEM_PORT);
//				changed = true;
//			}
//			status.setName(SYSTEM_NAME);
//			status.setAddress(SYSTEM_ADDRESS);
//			status.setPort(SYSTEM_PORT);
//
//			boolean pingIcmp = pingIcmp(status.getAddress());
//			if (pingIcmp != status.getPingIcmp()) {
//				status.setPingIcmp(pingIcmp);
//				changed = true;
//			}
//
//			boolean pingApp = false;
//			if (pingIcmp) {
//				//we ping app, only, if icmp ping is ok
//				pingApp = pingApp(status.getAddress(), status.getPort());
//			}
//
//			if (pingApp != status.getPingApp()) {
//				status.setPingApp(pingApp);
//				changed = true;
//			}
//
//			status.setLastCheck(new Date());
//			systemStatusService.save(status);
//
//			if (changed) {
//				LOG.info("System status changed : " + status);
//				broadcastSystemStatusChanged(status);
//			} else {
//				LOG.info("System status still  unchanged : " + status);
//			}
//
//		} catch (FablabException ex) {
//			LOG.error("Cannot execute systems status check", ex);
//		} catch (Exception ex) {
//			LOG.error("A unexpected error appear during door check", ex);
//		}
		} catch (FablabException ex) {
			java.util.logging.Logger.getLogger(SystemChecker.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void broadcastSystemStatusChanged(SystemStatusEO status) throws FablabException {
//		String content = "System status changed : name=" + status.getName() + " pingIcmp=" + status.getPingIcmp() + " pingApp=" + status.getPingApp();
//		//insert int audit
//		AuditUtils.addAudit(auditService, AuditObject.SYSTEM_STATUS, AuditAction.UPDATE,
//				status.getPingIcmp() && status.getPingApp(),
//				content);
//	
//
//		//send mail
//		mailService.sendMail("gaetancollaud@gmail.com", "System status changed", content);
	}

	private void checkListSystemStatus(List<SystemStatusEO> list) {
		Set<String> systemName = new HashSet<>();
		for(SystemStatusEO system : list){
			systemName.add(system.getName());
		}
		
		boolean change = false;
		for(SystemStatusEO system : defaultSystems){
			if(!systemName.contains(system.getName())){
				try {
					LOG.info("Adding default system "+system.getName());
					systemStatusService.save(system);
					change = true;
				} catch (FablabException ex) {
					LOG.error("Cannot add system "+system.getName(), ex);
				}
			}
		}
		if(change){
			try {
				List<SystemStatusEO> newlist = systemStatusService.getAllSystemStatus();
				list.clear();
				list.addAll(newlist);
			} catch (FablabException ex) {
				java.util.logging.Logger.getLogger(SystemChecker.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

}
