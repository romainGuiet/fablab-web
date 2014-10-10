package net.collaud.fablab.service.systems.mail;

import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
public interface MailService {

	void sendMail(String recipient, String subject, String content) throws FablabException;
}
