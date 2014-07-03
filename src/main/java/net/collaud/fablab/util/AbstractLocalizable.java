package net.collaud.fablab.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import static net.collaud.fablab.Constants.BUNDLE_NAME;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
abstract public class AbstractLocalizable {
	private static final Logger LOG = Logger.getLogger(AbstractLocalizable.class);

	private ResourceBundle bundle;

	public AbstractLocalizable() {
		bundle = ResourceBundle.getBundle(BUNDLE_NAME);
	}

	public String getString(String key) {
		return bundle.getString(key);
	}

	protected String getString(String key, Object... args) {
		String format = getString(key);
		return String.format(format, args);
	}

	public String getStringDefault(String key, String def) {
		String res = null;
		try {
			res = bundle.getString(key);
		} catch (NullPointerException | MissingResourceException ex) {
			LOG.warn("Cannot found key " + key + " in bundle " + BUNDLE_NAME);
		}
		if (res == null) {
			res = def;
		}
		return res;
	}
}
