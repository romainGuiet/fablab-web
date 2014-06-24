package net.collaud.fablab.cron.system;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import net.collaud.fablab.common.ws.client.PingClient;
import net.collaud.fablab.common.ws.exception.WebServiceException;
import net.collaud.fablab.exceptions.FablabException;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
@XmlRootElement
public class Door extends AbstractSystem implements Serializable {

	private static final Logger LOG = Logger.getLogger(SystemStatusFactory.class);

	private int appPingPort;
	private boolean pingAppOk;
	private boolean doorOpen;
	private boolean alarmOn;

	public Door() {
	}

	public Door(String host, int appPingPort) {
		super(host);
		this.appPingPort = appPingPort;
	}

	public int getAppPingPort() {
		return appPingPort;
	}

	@XmlAttribute
	public void setAppPingPort(int appPingPort) {
		this.appPingPort = appPingPort;
	}

	public boolean isPingAppOk() {
		return pingAppOk;
	}

	@XmlAttribute
	public void setPingAppOk(boolean pingAppOk) {
		this.pingAppOk = pingAppOk;
	}

	public boolean isDoorOpen() {
		return doorOpen;
	}

	@XmlAttribute
	public void setDoorOpen(boolean doorOpen) {
		this.doorOpen = doorOpen;
	}

	public boolean isAlarmOn() {
		return alarmOn;
	}

	@XmlAttribute
	public void setAlarmOn(boolean alarmOn) {
		this.alarmOn = alarmOn;
	}

	public static Door unmarshal(String xml) throws FablabException {
		return AbstractSystem.unmarshal(xml, Door.class);
	}

	protected boolean executeCHeck() {
		boolean motherResult = super.executeCheck();
		boolean result = false;
		String content = "coucou";
		String url = "http://" + getHost() + ":" + appPingPort;
		PingClient client = new PingClient(url);
		try {
			result = client.ping(content).getContent().equals(content);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Client pint result for host " + getHost() + " : " + result);
			}
		} catch (WebServiceException ex) {
			LOG.warn("URL " + url + " is not responding to ping app because of " + ex.getMessage());
		}
		return motherResult && result;
	}
}
