package net.collaud.fablab.cron.system;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import net.collaud.fablab.data.SystemStatusEO;
import net.collaud.fablab.exceptions.FablabException;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
abstract public class AbstractSystem implements Serializable {

	/**
	 * ICMP timeout in ms
	 */
	public static final int TIMEOUT_ICMP = 2000;

	private static final Logger LOG = Logger.getLogger(AbstractSystem.class);

	private String host;

	private boolean pingIcmpOk;

	public AbstractSystem() {
	}

	public AbstractSystem(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	@XmlAttribute
	public void setHost(String host) {
		this.host = host;
	}

	public boolean isPingIcmpOk() {
		return pingIcmpOk;
	}

	@XmlAttribute
	public void setPingIcmpOk(boolean pingIcmpOk) {
		this.pingIcmpOk = pingIcmpOk;
	}

	/**
	 * @return true if something has changed
	 */
	synchronized public boolean executeCheck() {
		boolean result = false;
		try {
			InetAddress ip = Inet4Address.getByName(host);
			result = ip.isReachable(TIMEOUT_ICMP);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Ping icmp for host " + host + " result is : " + result);
			}
		} catch (IOException ex) {
			LOG.error("Exception with host " + host, ex);
		}
		boolean changed = false;
		if (pingIcmpOk != result) {
			pingIcmpOk = result;
			changed = true;
		}
		return changed;
	}

	public String marshal() throws FablabException {
		try {
			JAXBContext context = JAXBContext.newInstance(getClass());
			Marshaller marshaller = context.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			StringWriter writer = new StringWriter();
			marshaller.marshal(this, writer);
			return writer.toString();
		} catch (JAXBException ex) {
			throw new FablabException("Unable to marshal object " + getClass(), ex);
		}
	}

	public static <T extends AbstractSystem> T unmarshal(String xml, Class<T> clazz) throws FablabException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			T obj = (T) jaxbUnmarshaller.unmarshal(new StringReader(xml));
			return obj;
		} catch (JAXBException ex) {
			throw new FablabException("Unable to unmarshal " + clazz, ex);
		}
	}

	public String getReadableStatus(SystemStatusEO status) {
		return "name=" + status.getName() + " pingIcmp=" + pingIcmpOk;
	}
}
