package net.collaud.fablab.service.systems.mail;

import javax.ejb.Local;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Local
public interface MailService {

	void sendMail(String recipient, String subject, String content) throws FablabException;
}
