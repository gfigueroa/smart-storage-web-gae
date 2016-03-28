/*
 Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package servlets;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.http.*;

import util.Dictionary;
import util.Printer;

import datastore.Customer;
import datastore.Customer.Status;
import datastore.CustomerManager;
import datastore.User;
import datastore.UserManager;

/**
 * This servlet class is used to create and destroy sessions.
 * 
 */

@SuppressWarnings("serial")
public class CreateSessionServlet extends HttpServlet {
    
	private static final Logger log = 
        Logger.getLogger(CreateSessionServlet.class.getName());
	
	// JSP file locations
    private static final String loginJSP = "/login.jsp";
    private static final String listAdminJSP = "/admin/listAdmin.jsp";
    private static final String listCustomerJSP = "/admin/listCustomer.jsp";
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) 
    		throws IOException {
        
    	String action = req.getParameter("action");
    	
    	if (action == null) {
    		resp.sendRedirect(loginJSP);
    	}
    	
    	// are we destroying the session?
        if (action.equals("destroy")) {
            HttpSession session = req.getSession(true);
            assert(session != null);

            Printer printer = (Printer) session.getAttribute("printer");
            Dictionary.Language language;
            if (printer != null) {
            	language = printer.getLanguage();
            }
            else {
            	language = Dictionary.Language.ENGLISH;
            }
            
            session.setAttribute("user", null);
            session.setAttribute("printer", null);
            
            resp.sendRedirect(loginJSP + "?lang=" + 
            		Dictionary.getLanguageString(language) + 
            		"&msg=success&action=" + action);
        }
  		// No more choices
  		else {
  			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
  			return;
  		}
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) 
    		throws IOException {
        
        // Check the language
        Printer printer;
        String language = req.getParameter("language") != null ?
        		req.getParameter("language") : "CH";
	    if (language.equals("EN")) {
	    	printer = new Printer(Dictionary.Language.ENGLISH);
	    }
	    else {
	    	printer = new Printer(Dictionary.Language.CHINESE);
	    }

    	// Check the user and password fields
        String username = req.getParameter("userName");
        String hashedPass = req.getParameter("userPassword");
        User user = UserManager.getUser(username, hashedPass);
        if (user == null) {
        	resp.sendRedirect(loginJSP + "?etype=InvalidInfo" +
        			"&lang=" + language);
        	return;
        }
        else {
        	// We check the user type to send him to his/her own main page
        	String redirectPage = "";
            switch(user.getUserType()) {
                case ADMINISTRATOR:
                	log.info("User logged in as Administrator");
                    redirectPage = listAdminJSP;;
                    break;
                case CUSTOMER:
                	
                	// Check if Customer is active
                	Customer customer = CustomerManager.getCustomer(user);
                	if (customer.getCustomerStatus() != Status.ACTIVE) {
                    	resp.sendRedirect(loginJSP + "?etype=CustomerNotActive" +
                    			"&lang=" + language);
                    	return;
                	}
                	
                	log.info("User logged in as Customer");
                	redirectPage = listCustomerJSP;
                    break;
                case CUSTOMER_USER:
                	log.info("User logged in as Customer User");
                	redirectPage = listCustomerJSP;
                    break;
                default:
                    // There should be no other type of user
                    assert(false);
            }
        	
            // create session information
            HttpSession session = req.getSession(true);
            assert(session != null);

            session.setAttribute("user", user);
            session.setAttribute("printer", printer);
            
            resp.sendRedirect(redirectPage + 
            		"?msg=success&action=login");
        }
    }
    
}