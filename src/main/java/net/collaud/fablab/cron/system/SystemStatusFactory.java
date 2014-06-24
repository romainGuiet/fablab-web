package net.collaud.fablab.cron.system;

import net.collaud.fablab.data.SystemStatusEO;
import net.collaud.fablab.exceptions.FablabException;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
public class SystemStatusFactory {

	private static final Logger LOG = Logger.getLogger(SystemStatusFactory.class);

	public static AbstractSystem getSystemStatusObject(SystemStatusEO eo) {
		try {
			Class<AbstractSystem> clazz = (Class<AbstractSystem>) Class.forName(eo.getType());
			return AbstractSystem.unmarshal(eo.getContent(), clazz);
		} catch (ClassNotFoundException ex) {
			LOG.error("Class not found "+eo.getType(), ex);
		} catch (FablabException ex) {
			LOG.error("Unable to unmarshall "+eo, ex);
		}
		return null;
	}
}
