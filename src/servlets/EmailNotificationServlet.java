/*
 Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

//Copyright 2011, Google Inc. All Rights Reserved.
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

package servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Email;


/**
* This servlet generates emails.
* 
* @author SMASRV
*/
@SuppressWarnings("serial")
public class EmailNotificationServlet extends HttpServlet {
	
	private static final Logger log = 
	        Logger.getLogger(EmailNotificationServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	   throws ServletException, IOException {
	    
	    // Common fields
	    String type = req.getParameter("type");
	    try {
	    	// Check type
	    	if (type.equalsIgnoreCase("alarmMessage")) {
	    		sendAlarmMessageEmail(req);
	    	}
	    	else {
	    		resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
	  			return;
	    	}
	    }
	    catch (AddressException e) {
	    	log.log(Level.SEVERE, e.toString());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
	    } 
	    catch (MessagingException e) {
	    	log.log(Level.SEVERE, e.toString());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
	    }
	    catch (Exception ex) {
            log.log(Level.SEVERE, ex.toString());
            ex.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
	}
	
	/**
	 * Sends an AlarmWarningMessage or AlarmTriggerMessage to a list of
	 * recipients (comma-separated).
	 * @param req: the HTTP Servlet Request
	 * @throws MessagingException 
	 * @throws UnsupportedEncodingException 
	 */
	private void sendAlarmMessageEmail(HttpServletRequest req) 
			throws UnsupportedEncodingException, MessagingException {

		Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);
	    
	    // Recipients
	    HashMap<Email, String> recipients = getAlarmMessageRecipients();
	    
	    final String SENDER_EMAIL= "sysadmin@dr-storage.com";
	    final String SENDER_NAME = "Dr. Storage";
	   
	    String subject= req.getParameter("subject");
	    String msgBody = req.getParameter("body");

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME));
        
        // Add recipients
        for (Email recipientEmail : recipients.keySet()) {
        	String recipientName = recipients.get(recipientEmail);
        	msg.addRecipient(Message.RecipientType.TO,
        			new InternetAddress(recipientEmail.getEmail(), recipientName));
        }
        
        msg.setSubject(subject);
        msg.setText(msgBody);
        Transport.send(msg);
        
        log.log(Level.INFO, "Alarm message sent to " + recipients.size() + " recipients.");
        log.log(Level.INFO, "To: " + recipients.toString());
        log.log(Level.INFO, "Subject: " + subject);
        log.log(Level.INFO, "Body:\n" + msgBody);
	}
	
	/**
	 * Get a list of key-value pairs (email-name) for the email recipients of
	 * AlarmMessages
	 * @return a HashMap<Email, String> of email recipients
	 */
	private HashMap<Email, String> getAlarmMessageRecipients() {
		HashMap<Email, String> recipients = new HashMap<>();
		
		recipients.put(new Email("gfigueroa@smasrv.com"), "Gerardo Figueroa");
		recipients.put(new Email("pchang@smasrv.com"), "Peter Chang");
		recipients.put(new Email("gtchiou@dr-storage.com"), "GT Chiou");
		
		return recipients;
	}
}
