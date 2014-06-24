package net.collaud.fablab.service.impl;

import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
public class AbstractServiceImpl {

	private static final Logger LOG = Logger.getLogger(SecurityServiceImpl.class);

	protected String getCurrentUserLogin() {
		try {
			return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
		} catch (NullPointerException ex) {
			return null;
		}
	}

}
