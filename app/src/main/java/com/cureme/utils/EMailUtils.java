package com.cureme.utils;

import android.content.Intent;

public class EMailUtils {

	public static Intent sendEmail() {
		String cc = "cure.me@live.com";
		String subject = "Take a look at \"Cure Me\"";
		String message = "Ever wished 'I was a doctor' or 'I had doctor friend' or get instant medical advise. 'Cure Me' is your medical assistant. This Application provides tips for preventing and curing many health related problems. These tips have been recommended by Doctors and medical experts. These suggestions are mainly home remedies which do not require any kind of medicines. These suggestions are 100% effective, but if problem persist then do not hesitate to get in contact with your family doctor or our medical experts. \n\n Take a look at \"Cure Me\" - https://play.google.com/store/apps/details?id=com.cureme \n\n\nThanks,\nCure Me Team";

		Intent email = new Intent(Intent.ACTION_SEND);
		email.setType("text/html");
		email.putExtra(Intent.EXTRA_CC, new String[] { cc });
		email.putExtra(Intent.EXTRA_SUBJECT, subject);
		email.putExtra(Intent.EXTRA_TEXT, message);

		// need this to prompts email client only
		email.setType("message/rfc822");

		return email;
	}
}
