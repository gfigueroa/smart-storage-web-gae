/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.EmailManager;
import util.RandomGenerator;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;

import datastore.Customer;
import datastore.Customer.Status;
import datastore.CustomerManager;
import datastore.SystemManager;
import datastore.User;
import datastore.User.UserType;
import datastore.UserManager;
import exceptions.InvalidFieldFormatException;
import exceptions.MissingRequiredFieldsException;
import exceptions.ObjectExistsInDatastoreException;
import exceptions.UnauthorizedUserOperationException;

/**
 * This servlet class is used to register users in the system (Customers)
 * 
 */

@SuppressWarnings("serial")
public class UserRegistrationServlet extends HttpServlet {

    private static final Logger log = 
        Logger.getLogger(UserRegistrationServlet.class.getName());
    
    private static final BlobstoreService blobstoreService = 
    	BlobstoreServiceFactory.getBlobstoreService();
    
    // JSP file locations
    private static final String confirmCustomerJSP = "/confirmCustomer.jsp";
    private static final String createCustomerJSP = "/createCustomer.jsp";
    private static final String loginJSP = "/login.jsp";

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
    	
    	HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("user");
        
        // Check that no user is logged in
	    if (user != null) {
	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        return;
	    }
    	
        // Lets check the action required by the jsp
        String action = req.getParameter("action");

        // Common parameters
        String successURL = "";
        
        // Confirm registration
        if (action.equals("confirmRegistration")) {
        	
        	try {
        	
	            // Retrieve the key of the user to confirm
	        	String keyString = req.getParameter("k");
	      		Key key = KeyFactory.stringToKey(keyString);
	
	      		// Retrieve the user type to confirm
	      		String type = req.getParameter("type");
	      		
	            // Confirm Customer
	      		if (type.equalsIgnoreCase("customer")) {
	                successURL = confirmCustomerJSP;
	      			
	      			confirmCustomer(req, key); 
	      		}
	      		// No more choices
	      		else {
	      			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
	      			return;
	      		}
	      		
	      		// Success URL
	      		resp.sendRedirect(successURL + "?msg=success&action=" + action);
        	}
        	catch (UnauthorizedUserOperationException ueoe) {
        		resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    	        return;
        	}
        }
        // No more choices
  		else {
  			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
  			return;
  		}
    }

    @SuppressWarnings("unused")
	public void doPost(HttpServletRequest req, HttpServletResponse resp) 
                throws IOException {
    	
    	// Get the current session
    	HttpSession session = req.getSession(true);
    	User user = (User) session.getAttribute("user");
    	
        // Lets check the action required by the jsp
        String action = req.getParameter("action");

        // Common parameters
        User.UserType userType;
        String type = req.getParameter("type");
        BlobKey blobKey = null;
        String successURL = "";
        String failURL = "";
        
        // ADD
        if (action.equals("add")) {

        	// Common parameters for ADD
            Email userEmail = new Email(req.getParameter("userEmail"));
            String userPassword = req.getParameter("userPassword");
            User neoUser;

            try {
	            // Add Customer
	            if (type.equalsIgnoreCase("customer")) {
	            	
	                // Check that no user is logged in
	        	    if (user != null) {
	        	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        	        return;
	        	    }
	            	
	            	successURL = confirmCustomerJSP;
	            	failURL = createCustomerJSP;
	            	
	            	userType = UserType.CUSTOMER;
	            	neoUser = new User(userEmail,
	                		userPassword,
	                        userType);
	            	
	            	blobKey = addCustomer(req, neoUser);
	            }
	            // No more choices
	            else {
	            	resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
	            	return;
	            }
	            
	            // Success URL
                resp.sendRedirect(successURL + "?msg=success&action=" + action);
	        }
            catch (MissingRequiredFieldsException mrfe) {
            	if (blobKey != null) {
            		blobstoreService.delete(blobKey);
            	}
                resp.sendRedirect(failURL  + "?etype=MissingInfo");
                return;
            }
            catch (InvalidFieldFormatException iffe) {
            	if (blobKey != null) {
            		blobstoreService.delete(blobKey);
            	}
                resp.sendRedirect(failURL + "?etype=InvalidFieldFormat");
                return;
            }
            catch (ObjectExistsInDatastoreException oede) {
            	if (blobKey != null) {
            		blobstoreService.delete(blobKey);
            	}
                resp.sendRedirect(failURL + "?etype=ObjectExists");
                return;
            }
            catch (MessagingException e) {
            	if (blobKey != null) {
            		blobstoreService.delete(blobKey);
            	}
                resp.sendRedirect(failURL + "?etype=InvalidEmail");
                return;
			}
          catch (Exception ex) {
        	if (blobKey != null) {
        		blobstoreService.delete(blobKey);
        	}
            log.log(Level.SEVERE, ex.toString());
            ex.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        } 
        // There are no more choices
        else {
    		resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
    		return;
    	}
    }
    
    /**
     * Confirm a Customer's registration
     * @param req: the HTTPServletRequest
     * @param key: the Customer key
     * @throws UnauthorizedUserOperationException 
     */
    private void confirmCustomer(HttpServletRequest req, Key key) 
    		throws UnauthorizedUserOperationException {
    	
    	Customer customer = CustomerManager.getCustomer(key);
    	
    	String confirmationKey = req.getParameter("confirmationKey");
    	if (customer.getConfirmationKey().equals(confirmationKey)) {
    		try {
				CustomerManager.updateCustomerStatus(key, Status.ACTIVE);
			} 
    		catch (MissingRequiredFieldsException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		throw new UnauthorizedUserOperationException(
    				customer.getUser(), "Whoops, can't do that.");
    	}
    }
    
    /**
     * Add Customer
     * @param req: the HTTPServletRequest
     * @param user: the new User (null if Update)
     * @throws MissingRequiredFieldsException 
     * @throws ObjectExistsInDatastoreException 
     * @throws MessagingException 
     * @throws UnsupportedEncodingException 
     */
    private BlobKey addCustomer(
    		HttpServletRequest req, 
    		User user) 
    		throws MissingRequiredFieldsException, 
    		ObjectExistsInDatastoreException, 
    		UnsupportedEncodingException, 
    		MessagingException {
		
		String customerName = req.getParameter("customerName");	
		String customerDescription = req.getParameter("customerDescription");
		
		String customerPhoneNumberString = req.getParameter("customerPhoneNumber");
		PhoneNumber customerPhoneNumber = null;
		if (!customerPhoneNumberString.isEmpty()) {
			customerPhoneNumber = new PhoneNumber(customerPhoneNumberString);
		}
		
		String regionIdString = req.getParameter("regionId");
		Key regionId = null;
	    if (!regionIdString.isEmpty()) {
			regionId = KeyFactory.stringToKey(regionIdString);
		}
		
        String customerAddress1 = req.getParameter("customerAddress1");
		String customerAddress2 = req.getParameter("customerAddress2");
		PostalAddress customerAddress = null;
		if (customerAddress2.trim().isEmpty()) {
			customerAddress = new PostalAddress(customerAddress1);
		}
		else {
			customerAddress = new PostalAddress(
					customerAddress1 + " " + customerAddress2);
		}
		
        Link customerWebsite = new Link(req.getParameter("customerWebsite"));
        //BlobKey customerLogoKey = BlobUtils.assignBlobKey(req, "customerLogo", 
        //		blobstoreService);
        BlobKey customerLogoKey = null; //TODO: Fix customerLogo upload
        String customerComments = req.getParameter("customerComments");
        
        String confirmationKey = RandomGenerator.generateRandomString(10, 20);
        
        Customer customer = new Customer(
        		confirmationKey,
        		user,
        		customerName,
        		customerDescription,
        		customerPhoneNumber,
                regionId,
                customerAddress,
                customerWebsite,
                customerLogoKey,
                Status.UNCONFIRMED,
                customerComments);
        
        // Check if user doesn't exist
		if (UserManager.userExists(user.getUserEmail())) {
			throw new ObjectExistsInDatastoreException(customer, "User \"" + 
					customer.getUser().getUserEmail().getEmail() + 
					"\" already exists in the datastore.");
		}
        
        final String CONFIRMATION_URL = 
        		getURLWithContextPath(req) + "/userRegistration";
        EmailManager.sendCustomerConfirmationEmail(
        		customer, confirmationKey, CONFIRMATION_URL);
        
        CustomerManager.putCustomer(customer);
        
        // Update the customerListVersion
        SystemManager.updateCustomerListVersion();
        
        return customerLogoKey;
    }
    
    public static String getURLWithContextPath(HttpServletRequest request) {
    	return request.getScheme() + 
    			"://" + request.getServerName() + 
    			":" + request.getServerPort() + 
    			request.getContextPath();
    }

}
