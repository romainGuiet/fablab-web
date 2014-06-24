package net.collaud.fablab.cron.system;

import javax.xml.bind.annotation.XmlRootElement;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@XmlRootElement
public class SimpleHost extends AbstractSystem {

	public SimpleHost() {
	}

	public SimpleHost(String host) {
		super(host);
	}

	public static SimpleHost unmarshal(String xml) throws FablabException{
		return AbstractSystem.unmarshal(xml, SimpleHost.class);
	}
	
}
