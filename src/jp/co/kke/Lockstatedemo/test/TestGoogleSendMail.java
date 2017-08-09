package jp.co.kke.Lockstatedemo.test;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import jp.co.kke.Lockstatedemo.mng.MsgException;
import jp.co.kke.Lockstatedemo.util.GoogleApiUtil;
import jp.co.kke.Lockstatedemo.util.MailUtil;

public class TestGoogleSendMail {
	public static void main(String[] args) throws MessagingException, IOException, MsgException {
		String access_token = "ya29.GluNBCtalopnUoQ61Hge5kv8Yu5lT5wiJTjkDj1ardmi9LQ9h8y8dZNGYdGTb_YsDRbrGlc1Q2i4dJ2cW3aeKWYbcCGBgzyUWEkynr34Z9HRZux-dAfJkHM_gllf";
		MimeMessage mimeMessage = MailUtil.makeMimeMessageWithEmail(
				"YQL06614@nifty.ne.jp",
				"タイトル題名",
				"薔薇の本文");
		String json = GoogleApiUtil.sendMailJson(mimeMessage, access_token);
		System.out.println("res:\n" + json);
	}
}