package net.collaud.fablab.service.systems.mail;

import net.collaud.fablab.exceptions.FablabException;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author gaetan
 */
@Service
public class MailServiceImpl implements MailService {

	private static final Logger LOG = Logger.getLogger(MailServiceImpl.class);

	@Override
	public void sendMail(String recipient, String subject, String content) throws FablabException {
		try {
			Email email = new SimpleEmail();
			email.setHostName("localhost");
			email.setSmtpPort(25);
			email.setAuthenticator(new DefaultAuthenticator("test", "test"));
			email.setFrom("no-reply@fablab-fribourg.ch");
			email.setSubject(subject);
			email.setMsg(content);
			email.addTo(recipient);
			email.send();
		} catch (EmailException ex) {
			LOG.error("Canont send mail ", ex);
		}
	}

}
