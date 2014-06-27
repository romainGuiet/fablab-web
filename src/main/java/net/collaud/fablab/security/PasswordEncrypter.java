package net.collaud.fablab.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import net.collaud.fablab.file.ConfigFileHelper;
import net.collaud.fablab.file.FileHelperFactory;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
abstract public class PasswordEncrypter {

	private static final Logger LOG = Logger.getLogger(PasswordEncrypter.class);

	public static final String DEFAULT_HASH_SALT = "fasdfadsfadsf322fr24t2";
	public static final String HASH_ALGO = "SHA-256";
	
	
	public static String addPasswordSalt(String password){
		return password+FileHelperFactory.getConfig().get(ConfigFileHelper.PASSWORD_SALT, DEFAULT_HASH_SALT);
	}

	/**
	 * Crypte le mot de passe
	 *
	 * @param mdp le mot de passe en clair
	 * @return le mot de passe crypté
	 */
	public static String encryptMdp(String mdp) {
		mdp = addPasswordSalt(mdp);
		try {
			MessageDigest md = MessageDigest.getInstance(HASH_ALGO);
			byte[] digestResult = md.digest(mdp.getBytes());
			String result = "";
			for (int i = 0; i < digestResult.length; i++) {
				result += Integer.toString((digestResult[i] & 0xff) + 0x100, 16).substring(1);
			}
			return result;
		} catch (NoSuchAlgorithmException ex) {
			LOG.error("Cannot find algorithm "+HASH_ALGO, ex);
		}
		return null;
	}
}
