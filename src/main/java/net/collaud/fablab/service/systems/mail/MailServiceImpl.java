package net.collaud.fablab.service.systems.mail;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import net.collaud.fablab.exceptions.FablabException;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class MailServiceImpl implements MailService {

	private static final Logger LOG = Logger.getLogger(MailServiceImpl.class);

//	@Override
//	public void sendMail(String recipient, String subject, String content) throws FablabException {
//
//		// Sender's email ID needs to be mentioned
//		String from = "no-reply@fablab-fribourg.ch";
//
//		// Assuming you are sending email from localhost
//		String host = "localhost";
//
//		// Get system properties
//		//FIXME fichier config
//		Properties properties = System.getProperties();
//
//		// Setup mail server 
//		//properties.put("mail.smtp.starttls.enable", "true");
//		properties.put("mail.smtp.host", "smtp.gmail.com");
//		properties.put("mail.smtp.user", "fablab.fribourg"); // User name
//		properties.put("mail.smtp.password", "#06fablab13#"); // password
//		properties.put("mail.smtp.port", "587");
//		properties.put("mail.smtp.auth", "true");
//
//		// Get the default Session object.
//		Session session = Session.getDefaultInstance(properties);
//
//		try {
//			// Create a default MimeMessage object.
//			MimeMessage message = new MimeMessage(session);
//
//			// Set From: header field of the header.
//			message.setFrom(new InternetAddress(from));
//
//			// Set To: header field of the header.
//			message.addRecipient(Message.RecipientType.TO,
//					new InternetAddress(recipient));
//
//			// Set Subject: header field
//			message.setSubject(subject);
//
//			// Now set the actual message
//			message.setText(content);
//
//			// Send message
//			Transport transport = session.getTransport("smtp");
//			transport.sendMessage(message, new Address[]{new InternetAddress(recipient)});
//			transport.connect();
//			transport.close();
//			LOG.info("Mail sent successfullty to " + recipient);
//		} catch (MessagingException ex) {
//			LOG.error("Unable to send mail to " + recipient, ex);
//		}
//	}
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
//		try {
//			Email email = new SimpleEmail();
//			email.setHostName("smtp.googlemail.com");
//			email.setSmtpPort(465);
//			email.setAuthenticator(new DefaultAuthenticator("fablab.fribourg", "#06fablab13#"));
//			email.setSSLOnConnect(true);
//			email.setFrom("fablab.fribourg@gmail.com");
//			email.setSubject(subject);
//			email.setMsg(content);
//			email.addTo(recipient);
//			email.send();
//		} catch (EmailException ex) {
//			LOG.error("Canont send mail ", ex);
//		}
	}

}
