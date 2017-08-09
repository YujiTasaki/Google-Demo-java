package jp.co.kke.Lockstatedemo.util;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtil {
	/**
	 * Gmail用メール構造生成
	 * @param to
	 * @param subject
	 * @param bodyText
	 * @return
	 * @throws MessagingException
	 */
    public static MimeMessage makeMimeMessageWithEmail(String to,
            String subject,
            String bodyText)
		throws MessagingException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage email = new MimeMessage(session);
		email.addRecipient(javax.mail.Message.RecipientType.TO,
		new InternetAddress(to));
		email.setSubject(subject);
		email.setText(bodyText);
		return email;
	}
}
