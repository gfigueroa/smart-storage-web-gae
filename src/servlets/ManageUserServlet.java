/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package servlets;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;

import datastore.Administrator;
import datastore.AdministratorManager;
import datastore.Customer;
import datastore.Customer.Status;
import datastore.CustomerManager;
import datastore.SystemManager;
import datastore.User;
import datastore.User.UserType;
import exceptions.InvalidFieldFormatException;
import exceptions.MissingRequiredFieldsException;
import exceptions.ObjectExistsInDatastoreException;
import exceptions.UserValidationException;

/**
 * This servlet class is used to add, delete and update
 * global Users (Admin or Customer) in the system.
 * 
 */

@SuppressWarnings("serial")
public class ManageUserServlet extends HttpServlet {

    private static final Logger log = 
        Logger.getLogger(ManageUserServlet.class.getName());
    
    private static final BlobstoreService blobstoreService = 
    	BlobstoreServiceFactory.getBlobstoreService();
    
    // JSP file locations
    private static final String addAdminJSP = "/admin/addAdmin.jsp";
    private static final String editAdminJSP = "/admin/editAdmin.jsp";
    private static final String editAdminPasswordJSP = "/admin/editAdminPassword.jsp";
    private static final String listAdminJSP = "/admin/listAdmin.jsp";
    private static final String addCustomerJSP = "/admin/addCustomer.jsp";
    private static final String editCustomerJSPAdmin = "/admin/editCustomer.jsp";
    private static final String editCustomerJSPCustomer = 
    		"/admin/editCustomer.jsp";
    private static final String editCustomerPasswordJSPAdmin = 
    		"/admin/editCustomerPassword.jsp";
    private static final String editCustomerPasswordJSPCustomer = 
    		"/admin/editCustomerPassword.jsp";
    private static final String listCustomerJSP = "/admin/listCustomer.jsp";

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
    	
    	HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("user");
        
        // Check that an administrator is carrying out the action
	    if (user == null || user.getUserType() != User.UserType.ADMINISTRATOR) {
	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        return;
	    }
    	
        // Lets check the action required by the jsp
        String action = req.getParameter("action");

        // Common parameters
        String successURL = "";
        
        // DELETE
        if (action.equals("delete")) {
        	
            // retrieve the key     
        	String keyString = req.getParameter("k");
      		Key key = KeyFactory.stringToKey(keyString);

      		// retrieve the user type to delete
      		String type = req.getParameter("type");
      		
            // Delete Administrator
      		if (type.equalsIgnoreCase("administrator")) {
      			successURL = listAdminJSP;
      			
                Administrator admin = 
                    AdministratorManager.getAdministrator(key);
                AdministratorManager.deleteAdministrator(admin);
      		}
      		// Delete Customer
      		else if (type.equalsIgnoreCase("customer")) {
                successURL = listCustomerJSP;
      			
      			Customer customer = CustomerManager.getCustomer(key);
                CustomerManager.deleteCustomer(customer);
                
                SystemManager.updateCustomerListVersion();       
      		}
      		// No more choices
      		else {
      			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
      			return;
      		}
      		
      		// Success URL
      		resp.sendRedirect(successURL + "?msg=success&action=" + action);
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
            
            // Bypass security
            if (req.getParameter("bypass") != null && 
            		req.getParameter("bypass").equals("true")) {
            	log.info("Security bypassed");
            }
            else {
            	// Check that an administrator is carrying out the action
                if (user == null || user.getUserType() != User.UserType.ADMINISTRATOR) {
                	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }

            try {
            
	            // Add Administrator
	            if (type.equalsIgnoreCase("administrator")) {
	            	successURL = listAdminJSP;
	            	failURL = addAdminJSP;
	            	
	            	userType = UserType.ADMINISTRATOR;
	            	neoUser = new User(userEmail,
	                		userPassword,
	                        userType);
	            	
	            	addOrUpdateAdministrator(req, neoUser, null, true);
	            }
	            // Add Customer
	            else if (type.equalsIgnoreCase("customer")) {
	            	successURL = listCustomerJSP;
	            	failURL = addCustomerJSP;
	            	
	            	userType = UserType.CUSTOMER;
	            	neoUser = new User(userEmail,
	                		userPassword,
	                        userType);
	            	
	            	blobKey = addOrUpdateCustomer(req, neoUser, null, true);
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
        // UPDATE
        else if (action.equals("update")) {
        	
        	// Common parameters for UPDATE
            String updateType = req.getParameter("update_type");
            String keyString = req.getParameter("k");
            Key key = KeyFactory.stringToKey(keyString);
            boolean sameLogo = true;
            
            // Check that a user is carrying out the action
            if (user == null) {
            	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            
            try {
            	// Edit Administrator
            	if (type.equalsIgnoreCase("administrator")) {
                	
                	// Check that an administrator is carrying out the action
                    if (user.getUserType() != User.UserType.ADMINISTRATOR) {
                    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                	
                	// Edit Administrator Info
                	if (updateType.equalsIgnoreCase("info")) {
                        successURL = editAdminJSP;
                        failURL = editAdminJSP;
                		
                		addOrUpdateAdministrator(req, null, key, false);
                	}
                	// Edit Administrator Password
                	else if (updateType.equalsIgnoreCase("password")) {
                        successURL = editAdminPasswordJSP;
                        failURL = editAdminPasswordJSP;
                		
                		updateAdministratorPassword(req, key);
                	}
                	// There are no more choices
                	else {
                		resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
    	            	return;
                	}
            	}
            	// Edit Customer
            	else if (type.equalsIgnoreCase("customer")) {
            		
                    // Check that an Administrator or a Customer are carrying out the action
                    String successUpdateCustomerJSP;
                    String successUpdateCustomerPasswordJSP;
                    String failUpdateCustomerJSP;
                    String failUpdateCustomerPasswordJSP;
                    if (user.getUserType() == User.UserType.ADMINISTRATOR) {
                    	successUpdateCustomerJSP = editCustomerJSPAdmin;
                    	successUpdateCustomerPasswordJSP = editCustomerPasswordJSPAdmin;
                    	failUpdateCustomerJSP = editCustomerJSPAdmin;
                        failUpdateCustomerPasswordJSP = editCustomerPasswordJSPAdmin;
                    }
                    else if (user.getUserType() == User.UserType.CUSTOMER) {
                    	successUpdateCustomerJSP = editCustomerJSPCustomer;
                    	successUpdateCustomerPasswordJSP = editCustomerJSPCustomer;
                    	failUpdateCustomerJSP = editCustomerJSPCustomer;
                        failUpdateCustomerPasswordJSP = editCustomerPasswordJSPCustomer;
                    }
                    else {
                    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    	return;
                    }
            		
                    // Edit Customer Info
                	if (updateType.equalsIgnoreCase("info")) {
                        successURL = successUpdateCustomerJSP;
                        failURL = failUpdateCustomerJSP;
                		
	                    blobKey = addOrUpdateCustomer(req, null, key, false);
	                    
	                    Customer customer = CustomerManager.getCustomer(key);
	                    if (blobKey == null) {
	                    	blobKey = customer.getCustomerLogo();
	                    	sameLogo = true;
	                    	log.info("No logo uploaded in Customer \"" + customer.getCustomerName() + 
	                    			"\". Using previous logo.");
	                    }
                	}
                	// Edit Customer Password
                	else if (updateType.equalsIgnoreCase("password")) {
                        successURL = successUpdateCustomerPasswordJSP;
                        failURL = failUpdateCustomerPasswordJSP;
                        
                		updateCustomerPassword(req, key, user);
                	}
                	// There are no more choices
                	else {
                		resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
    	            	return;
                	}
            	}
            	// There are no more choices
            	else {
            		resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
	            	return;
            	}
            	
            	// Success URL
            	resp.sendRedirect(successURL + "?msg=success" +
            			"&action=" + action +
            			"&k=" + keyString +
            			"&update_type=" + updateType);
           	}
            catch (MissingRequiredFieldsException mrfe) {
            	if (!sameLogo) {
            		blobstoreService.delete(blobKey);
            	}
                resp.sendRedirect(failURL + "?etype=MissingInfo&k=" + keyString);
                return;
            }
            catch (UserValidationException uve) {
            	if (!sameLogo) {
            		blobstoreService.delete(blobKey);
            	}
                resp.sendRedirect(failURL + "?etype=InvalidInfo&k=" + keyString);
                return;
            }
            catch (ObjectExistsInDatastoreException oeide) {
            	if (!sameLogo) {
            		blobstoreService.delete(blobKey);
            	}
                resp.sendRedirect(failURL + "?etype=ObjectExists&k=" + keyString);
                return;
            }
            catch (Exception ex) {
            	if (!sameLogo) {
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
     * Add or Update Administrator
     * @param req: the HTTPServletRequest
     * @param user: the new User (null if Update)
     * @param key: the Administrator key (null if Add)
     * @param add: whether to Add object or not
     * @throws MissingRequiredFieldsException 
     * @throws ObjectExistsInDatastoreException 
     */
    private void addOrUpdateAdministrator(HttpServletRequest req, User user, Key key, 
    		boolean add) 
    		throws MissingRequiredFieldsException, 
    		ObjectExistsInDatastoreException {
        
    	String administratorName = req.getParameter("administratorName");
        String administratorComments = req.getParameter("administratorComments");

        if (add) {
	        Administrator administrator = new Administrator(user,
	                administratorName,
	                administratorComments);
	        AdministratorManager.putAdministrator(administrator);
        }
        else {
            AdministratorManager.updateAdministratorAttributes(
                    key,
                    administratorName,
                    administratorComments);
        }
    }
    
    /**
     * Update an Administrator's password
     * @param req: the HTTPServletRequest
     * @param key: the Administrator key
     * @throws MissingRequiredFieldsException 
     */
    private void updateAdministratorPassword(HttpServletRequest req, Key key) 
    		throws MissingRequiredFieldsException {
        
    	String newPassword = req.getParameter("userNewPassword");

        AdministratorManager.updateAdministratorPassword(
                key,
                newPassword
                );
    }
    
    /**
     * Add or Update Customer
     * @param req: the HTTPServletRequest
     * @param user: the new User (null if Update)
     * @param key: the Customer key (null if Add)
     * @param add: whether to Add object or not
     * @throws MissingRequiredFieldsException 
     * @throws ObjectExistsInDatastoreException 
     */
    private BlobKey addOrUpdateCustomer(HttpServletRequest req, User user,
    		Key key, boolean add) 
    		throws MissingRequiredFieldsException, 
    		ObjectExistsInDatastoreException {

    	Customer customerToUpdate = null;
    	if (key != null) {
    		customerToUpdate = CustomerManager.getCustomer(key);
    	}
		
		String customerName = req.getParameter("customerName");	
		String customerDescription = req.getParameter("customerDescription");
		
		String customerPhoneNumberString = req.getParameter("customerPhoneNumber");
		PhoneNumber customerPhoneNumber = null;
		if (!customerPhoneNumberString.isEmpty()) {
			customerPhoneNumber = new PhoneNumber(customerPhoneNumberString);
		}
		
		String regionIdString = req.getParameter("regionId");
		Key regionId = null;
		if (regionIdString != null) {
		    if (!regionIdString.isEmpty()) {
				regionId = KeyFactory.stringToKey(regionIdString);
			}
		}
		// regionIdString will be null if the Customer is updating
		else {
			regionId = customerToUpdate.getRegion();
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
        
        String customerStatusString = req.getParameter("customerStatus");
        Status customerStatus = null;
        if (customerStatusString != null && !customerStatusString.isEmpty()) {
        	customerStatus = Customer.getCustomerStatusFromString(customerStatusString);
        }
        // Status will be null if it is Customer log in
        // Only if Update (key != null)
        else if (key != null){
        	Customer customer = CustomerManager.getCustomer(key);
        	customerStatus = customer.getCustomerStatus();
        }
        
        String customerComments = req.getParameter("customerComments");
        
        if (add) {
	        Customer customer = new Customer(
	        		null,
	        		user,
	        		customerName,
	        		customerDescription,
	        		customerPhoneNumber,
	                regionId,
	                customerAddress,
	                customerWebsite,
	                customerLogoKey,
	                Status.ACTIVE,
	                customerComments);
	        CustomerManager.putCustomer(customer);
        }
        else {
            CustomerManager.updateCustomerAttributes(
            		key, 
            		customerName, 
            		customerDescription, 
            		customerPhoneNumber, 
            		regionId, 
            		customerAddress, 
            		customerWebsite, 
            		customerLogoKey, 
            		customerStatus,
            		customerComments);
        }
        
        // Update the customerListVersion
        SystemManager.updateCustomerListVersion();
        
        return customerLogoKey;
    }
    
    /**
     * Update a Customer's password
     * @param req: the HTTPServletRequest
     * @param key: the Customer key
     * @param user: the User that is logged in
     * @throws MissingRequiredFieldsException 
     * @throws UserValidationException 
     */
    private void updateCustomerPassword(HttpServletRequest req, Key key, 
    		User user) 
    		throws MissingRequiredFieldsException, UserValidationException {

    	// Check old password if Customer is logged in
    	if (user.getUserType() == User.UserType.CUSTOMER) {
    		String oldPassword = req.getParameter("userPassword");
    		boolean valid = user.validateUser(user.getUserEmail(), oldPassword);
    		if (!valid) {
    			throw new UserValidationException(user, "Incorrect password!");
    		}
    	}
    	
		String newPassword = req.getParameter("userNewPassword");

        CustomerManager.updateCustomerPassword(
                key,
                newPassword
                );
    }
}
