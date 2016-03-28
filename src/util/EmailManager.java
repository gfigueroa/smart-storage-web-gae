/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import servlets.EmailNotificationServlet;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.KeyFactory;

import datastore.Customer;

/**
 * Utility class for sending emails.
 * 
 */

public class EmailManager {

	private static final Logger log = 
	        Logger.getLogger(EmailNotificationServlet.class.getName());
    public final static String SENDER_EMAIL= "sysadmin@smasrv.com";
    public final static String SENDER_NAME = "Dr. Storage";
	
	/**
	 * Sends an e-mail to the registering user (Customer) 
	 * with the confirmation key.
	 * @param customer: the Customer who is registering
	 * @param confirmationKey: the confirmation key to attach to the confirmation URL
	 * @param confirmationURL: the URL that leads to confirmation
	 * @throws MessagingException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void sendCustomerConfirmationEmail(Customer customer, 
			String confirmationKey, String confirmationURL) 
			throws UnsupportedEncodingException, MessagingException {

		Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);

	    // Sender
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME));
        
	    // Recipients
	    HashMap<Email, String> recipients = 
	    		getCustomerConfirmationRecipients(customer);
        for (Email recipientEmail : recipients.keySet()) {
        	String recipientName = recipients.get(recipientEmail);
        	msg.addRecipient(Message.RecipientType.TO,
        			new InternetAddress(recipientEmail.getEmail(), recipientName));
        }
        
        // Subject
        String subject = "Customer confirmation at Dr-Storage";
        msg.setSubject(subject);
        
        // Body
        String body = "Dear " + customer.getCustomerName() + ",\n\n";
        body += "You have requested registration at dr-storage.appspot.com.\n";
        body += "If you have not requested such registration, please delete this email.\n\n";
        body += "Please click on the following link to confirm your registration:\n";
        body += confirmationURL + "?action=confirmRegistration&k=" + 
        		KeyFactory.keyToString(customer.getKey()) + "&type=customer&confirmationKey=" +
        		confirmationKey;
        body += "\n\nBest regards,\nThe Dr-Storage Team";
        msg.setText(body);
        
        Transport.send(msg);
        
        log.log(Level.INFO, "Alarm message sent to " + recipients.size() + " recipients.");
        log.log(Level.INFO, "To: " + recipients.toString());
        log.log(Level.INFO, "Subject: " + subject);
        log.log(Level.INFO, "Body:\n" + body);
	}
	
	/**
	 * Sends an AlarmWarningMessage or AlarmTriggerMessage to a list of
	 * recipients (comma-separated).
	 * @param subject: the Subject of the email
	 * @param body: the Body of the email
	 * @throws MessagingException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void sendAlarmMessageEmails(String subject, String body) 
			throws UnsupportedEncodingException, MessagingException {

		Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);
	  
	    // Sender
	    final String SENDER_EMAIL= "sysadmin@smasrv.com";
	    final String SENDER_NAME = "Dr. Storage";

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME));
        
	    // Recipients
	    HashMap<Email, String> recipients = getAlarmMessageRecipients();
        for (Email recipientEmail : recipients.keySet()) {
        	String recipientName = recipients.get(recipientEmail);
        	msg.addRecipient(Message.RecipientType.TO,
        			new InternetAddress(recipientEmail.getEmail(), recipientName));
        }
        
        msg.setSubject(subject);
        msg.setText(body);
        Transport.send(msg);
        
        log.log(Level.INFO, "Alarm message sent to " + recipients.size() + " recipients.");
        log.log(Level.INFO, "To: " + recipients.toString());
        log.log(Level.INFO, "Subject: " + subject);
        log.log(Level.INFO, "Body:\n" + body);
	}
	
	/**
	 * Get a list of key-value pairs (email-name) for the email recipients of
	 * a Customer confirmation
	 * @return a HashMap<Email, String> of email recipients
	 */
	private static HashMap<Email, String> getCustomerConfirmationRecipients(
			Customer customer) {
		HashMap<Email, String> recipients = new HashMap<>();
		
		recipients.put(new Email(SENDER_EMAIL), SENDER_NAME);
		recipients.put(customer.getUser().getUserEmail(), customer.getCustomerName());
		
		return recipients;
	}
	
	/**
	 * Get a list of key-value pairs (email-name) for the email recipients of
	 * AlarmMessages
	 * @return a HashMap<Email, String> of email recipients
	 */
	private static HashMap<Email, String> getAlarmMessageRecipients() {
		HashMap<Email, String> recipients = new HashMap<>();
		
		recipients.put(new Email("gfigueroa@smasrv.com"), "Gerardo Figueroa");
		recipients.put(new Email("pchang@smasrv.com"), "Peter Chang");
		recipients.put(new Email("gtchiou@dr-storage.com"), "GT Chiou");
		
		return recipients;
	}
}
