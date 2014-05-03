package edu.uci.ics.comon.eventprocessor.actions;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import edu.uci.ics.comon.eventprocessor.input.samples.Sample;

public class MailAction extends Action {

	public MailAction() {
	}

	@Override
	public void accept(Sample t) {
		// Recipient's email ID needs to be mentioned.
		String to = getConfig().getString("to");

		// Sender's email ID needs to be mentioned
		String from = getConfig().getString("from");

		final String password = getConfig().getString("password");

		Properties props = new Properties();
		props.put("mail.smtp.auth", getConfig().getString("properties.mailsmtpauth"));
		props.put("mail.smtp.starttls.enable", getConfig().getString("properties.mailsmtpstarttlsenable"));
		props.put("mail.smtp.host", getConfig().getString("properties.mailsmtphost"));
		props.put("mail.smtp.port", getConfig().getString("properties.mailsmtpport"));

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(getConfig().getString("subject"));
			message.setText(getConfig().getString("message"));

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

		System.exit(1);
	}

}
