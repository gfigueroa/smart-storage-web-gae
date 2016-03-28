/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.DateManager;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PhoneNumber;

import datastore.AlarmTrigger;
import datastore.AlarmTrigger.AlarmTriggerConditionOperator;
import datastore.AlarmTrigger.AlarmTriggerLevel;
import datastore.AlarmTriggerManager;
import datastore.AlarmTriggerMessage.AlarmTriggerMessageStatus;
import datastore.AlarmWarningMessage.AlarmWarningMessageStatus;
import datastore.AlarmTriggerMessageManager;
import datastore.Customer;
import datastore.CustomerManager;
import datastore.CustomerUser;
import datastore.CustomerUser.Gender;
import datastore.CustomerUserManager;
import datastore.DeviceModel;
import datastore.DeviceModelDoor;
import datastore.DeviceModelDoorManager;
import datastore.DeviceModelManager;
import datastore.DeviceModelPartition;
import datastore.DeviceModelPartitionManager;
import datastore.SensorInstance;
import datastore.SensorInstance.SensorStatus;
import datastore.AlarmTriggerMessage;
import datastore.AlarmWarningMessage;
import datastore.AlarmWarningMessageManager;
import datastore.SensorInstanceManager;
import datastore.SensorType;
import datastore.SensorTypeManager;
import datastore.StorageDevice;
import datastore.StorageDeviceContainerManager;
import datastore.StorageDeviceCustomerUserManager;
import datastore.StorageDeviceDoor;
import datastore.StorageDeviceDoorManager;
import datastore.StorageDeviceManager;
import datastore.StorageDevicePartition;
import datastore.StorageDevicePartitionManager;
import datastore.StorageItem;
import datastore.StorageItemInstance;
import datastore.StorageItemInstance.IndicationLightValue;
import datastore.StorageItemInstanceManager;
import datastore.StorageItemManager;
import datastore.StorageItemTransaction;
import datastore.StorageItemTransaction.StorageItemTransactionAction;
import datastore.StorageItemTransactionManager;
import datastore.User;
import datastore.User.UserType;
import exceptions.InexistentObjectException;
import exceptions.InvalidFieldFormatException;
import exceptions.MissingRequiredFieldsException;
import exceptions.ObjectExistsInDatastoreException;
import exceptions.UserValidationException;

/**
 * This servlet class is used to add, delete and update
 * Customer-owned objects in the system.
 * 
 */

@SuppressWarnings("serial")
public class ManageCustomerServlet extends HttpServlet {

    private static final Logger log = 
        Logger.getLogger(ManageCustomerServlet.class.getName());
    
    // JSP file locations
    private static final String addStorageDeviceJSP = 
    		"/admin/addEquipmentStorage.jsp";
    private static final String editStorageDeviceJSP = 
    		"/admin/editEquipmentStorage.jsp";
    private static final String listStorageDeviceJSP = 
    		"/admin/listEquipmentStorage.jsp";
    
    private static final String addStorageDeviceContainerJSP = 
    		"/admin/addStorageDeviceContainer.jsp";
    private static final String editStorageDeviceContainerJSP = 
    		"/admin/editStorageDeviceContainer.jsp";
    private static final String listStorageDeviceContainerJSP = 
    		"/admin/listStorageDeviceContainer.jsp";
    
    private static final String addStorageItemJSP = 
    		"/admin/addItem.jsp";
    private static final String editStorageItemJSP = 
    		"/admin/editItem.jsp";
    private static final String listStorageItemJSP = 
    		"/admin/listItem.jsp";
    
    private static final String addStorageItemInstanceJSP = 
    		"/admin/addItemInstance.jsp";
    private static final String editStorageItemInstanceJSP = 
    		"/admin/editItemInstance.jsp";
    private static final String listStorageItemInstanceJSP = 
    		"/admin/listItemInstance.jsp";
    
    private static final String addStorageItemTransactionItemsJSP = 
    		"/admin/listItemInstance.jsp";
    private static final String addStorageItemTransactionStorageJSP = 
    		"/admin/editListItemDetail.jsp";
    private static final String listStorageItemTransactionJSP = 
    		"/admin/listStorageItemTransaction.jsp";
    
    private static final String addCustomerUserJSP = 
    		"/admin/addEmployee.jsp";
    private static final String editCustomerUserJSP = 
    		"/admin/editEmployee.jsp";
    private static final String editCustomerUserPasswordJSP = 
    		"/admin/editEmployeePassword.jsp";
    private static final String listCustomerUserJSP =
    		"/admin/listEmployee.jsp";
    
    private static final String addStorageDeviceCustomerUserJSP = 
    		"/admin/addEmployee.jsp";
    private static final String listStorageDeviceCustomerUserJSP =
    		"/admin/listEmployee.jsp";
    
    private static final String addAlarmTriggerJSP = 
    		"/admin/addAlarmRule.jsp";
    private static final String editAlarmTriggerJSP = 
    		"/admin/editAlarmRule.jsp";
    private static final String listAlarmTriggerJSP =
    		"/admin/listAlarmRule.jsp";

    private static final String editAlarmTriggerMessageJSP = 
    		"/admin/editAlarmTriggerStatus.jsp";
    private static final String listAlarmTriggerMessageJSP =
    		"/admin/listAlarmMessage.jsp";
    
    private static final String editAlarmWarningMessageJSP = 
    		"/admin/editAlarmWarningStatus.jsp";
    private static final String listAlarmWarningMessageJSP = 
    		"/admin/listAlarmMessage.jsp";

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
        
    	HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("user");
        
        // Check that a User is carrying out the action
	    if (user == null) {
	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        return;
	    }
    	
    	// Lets check the action required by the jsp
        String action = req.getParameter("action");
        
        // Common parameters
        String successURL = "";
        String failURL = "";
        String additionalParameters = "";

        try {
	        // DELETE
	        if (action.equals("delete")) {
	            // Retrieve the key     
	        	String keyString = req.getParameter("k");
	
	        	// Retrieve the object type to delete
	        	String type = req.getParameter("type");
	
	        	// Delete StorageDevice
	        	if (type.equalsIgnoreCase("storageDevice")) {
	        		
	                // Check that an Administrator is carrying out the action
	        	    if (user.getUserType() != User.UserType.ADMINISTRATOR) {
	        	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        	        return;
	        	    }
	        		
	        		Key key = KeyFactory.stringToKey(keyString);
	        		
	            	successURL = listStorageDeviceJSP;
	            	failURL = successURL;
	            	additionalParameters = 
	            			"&c_key=" + KeyFactory.keyToString(key.getParent());
	            	
	        		StorageDeviceManager.deleteStorageDevice(key);
	        	}
	        	// Delete StorageDeviceContainer
	        	else if (type.equalsIgnoreCase("storageDeviceContainer")) {
	        		
	                // Check that an Administrator is carrying out the action
	        	    if (user.getUserType() != User.UserType.ADMINISTRATOR) {
	        	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        	        return;
	        	    }
	        		
	        		Key key = KeyFactory.stringToKey(keyString);
	        		
	            	successURL = listStorageDeviceContainerJSP;
	            	failURL = successURL;
	            	additionalParameters = 
	            			"&c_key=" + KeyFactory.keyToString(key.getParent());
	        		
	        		StorageDeviceContainerManager.deleteStorageDeviceContainer(key);
	        	}
	        	// Delete StorageItem
	        	else if (type.equalsIgnoreCase("storageItem")) {
	        		
	                // Check that a Customer or Admin is carrying out the action
	        	    if (user.getUserType() != User.UserType.CUSTOMER &&
	        	    		user.getUserType() != User.UserType.ADMINISTRATOR) {
	        	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        	        return;
	        	    }
	        	    
	            	successURL = listStorageItemJSP;
	            	failURL = successURL;
	        		
	        		Key key = KeyFactory.stringToKey(keyString);
	        		StorageItemManager.deleteStorageItem(key);
	        	}
	        	// Delete StorageItemInstance
	        	else if (type.equalsIgnoreCase("storageItemInstance")) {
	        		
	                // Check that a Customer or Admin is carrying out the action
	        	    if (user.getUserType() != User.UserType.CUSTOMER &&
	        	    		user.getUserType() != User.UserType.ADMINISTRATOR) {
	        	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        	        return;
	        	    }
	        	    
	            	successURL = listStorageItemInstanceJSP;
	            	failURL = successURL;
	            	additionalParameters = 
	            			"&storageItemId=" + req.getParameter("storageItemId");
	        		
	        		Key key = KeyFactory.stringToKey(keyString);
	        		StorageItemInstanceManager.deleteStorageItemInstance(key);
	        	}
	        	// Delete StorageItemTransaction
	        	else if (type.equalsIgnoreCase("storageItemTransaction")) {
	        		
	                // Check that a Customer or Admin is carrying out the action
	        	    if (user.getUserType() != User.UserType.CUSTOMER &&
	        	    		user.getUserType() != User.UserType.ADMINISTRATOR) {
	        	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        	        return;
	        	    }
	        		
	            	successURL = listStorageItemTransactionJSP;
	            	failURL = successURL;
	        	    
	            	Key key = KeyFactory.stringToKey(keyString);
	        		StorageItemTransactionManager.deleteStorageItemTransaction(key);
	        	}
	        	// Delete CustomerUser
	        	else if (type.equalsIgnoreCase("customerUser")) {
	        		
	                // Check that a Customer or Admin is carrying out the action
	        	    if (user.getUserType() != User.UserType.CUSTOMER &&
	        	    		user.getUserType() != User.UserType.ADMINISTRATOR) {
	        	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        	        return;
	        	    }
	        		
	        		successURL = listCustomerUserJSP;
	        		failURL = successURL;
	        	    
	        		Key key = KeyFactory.stringToKey(keyString);
	        		CustomerUserManager.deleteCustomerUser(key);
	        	}
	        	// Delete StorageDeviceCustomerUser
	        	else if (type.equalsIgnoreCase("storageDeviceCustomerUser")) {
	        		
	                // Check that a Customer or Admin is carrying out the action
	        	    if (user.getUserType() != User.UserType.CUSTOMER &&
	        	    		user.getUserType() != User.UserType.ADMINISTRATOR) {
	        	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        	        return;
	        	    }
	        		
	        		successURL = listStorageDeviceCustomerUserJSP + additionalParameters;
	        		failURL = successURL;
	        	    
	        		Long key = Long.parseLong(keyString);
	        		StorageDeviceCustomerUserManager.deleteStorageDeviceCustomerUser(key);
	        	}
	        	// Delete AlarmTrigger
	        	else if (type.equalsIgnoreCase("alarmTrigger")) {
	        		
	                // Check that an Administrator is carrying out the action
	        	    if (user.getUserType() != User.UserType.ADMINISTRATOR) {
	        	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        	        return;
	        	    }
	        		
	        		Key key = KeyFactory.stringToKey(keyString);
	        		
	            	successURL = listAlarmTriggerJSP;
	            	failURL = successURL;
	            	
	        		AlarmTriggerManager.deleteAlarmTrigger(key);
	        	}
	        	// Delete AlarmTriggerMessage
	        	else if (type.equalsIgnoreCase("alarmTriggerMessage")) {
	        		
	                // Check that an Administrator or Customer is carrying out the action
	        	    if (user.getUserType() != User.UserType.ADMINISTRATOR &&
	        	    		user.getUserType() != User.UserType.CUSTOMER) {
	        	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        	        return;
	        	    }
	        		
	        		Key key = KeyFactory.stringToKey(keyString);
	        		
	            	successURL = listAlarmTriggerMessageJSP;
	            	failURL = successURL;
	            	
	        		AlarmTriggerMessageManager.deleteAlarmTriggerMessage(key);
	        	}
	        	// Delete AlarmWarningMessage
	        	else if (type.equalsIgnoreCase("alarmWarningMessage")) {
	        		
	                // Check that an Administrator or Customer is carrying out the action
	        	    if (user.getUserType() != User.UserType.ADMINISTRATOR &&
	        	    		user.getUserType() != User.UserType.CUSTOMER) {
	        	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        	        return;
	        	    }
	        		
	        		successURL = listAlarmWarningMessageJSP;
	        		failURL = successURL;
	        		
	        		Key key = KeyFactory.stringToKey(keyString);
	        		AlarmWarningMessageManager.deleteAlarmWarningMessage(key);
	        	}
	        	// No more choices
	      		else {
	      			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
	      			return;
	      		}
	
	        	// Success URL
	        	resp.sendRedirect(successURL + "?msg=success&action=" + action +
	        			additionalParameters);
	        }
	        // No more choices
	  		else {
	  			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
	  			return;
	  		}
        }
        catch (InexistentObjectException ioe) {
        	resp.sendRedirect(failURL + "?etype=InexistentObject" + additionalParameters);
            return;
        }
        catch (Exception ex) {
            log.log(Level.SEVERE, ex.toString());
            ex.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) 
                throws IOException {
    	
    	HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("user");
        
        // Check that a User is carrying out the action
	    if (user == null) {
	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        return;
	    }
	    
	    // Check the Customer involved
	    Customer customer = null;
	    if (user.getUserType() == UserType.CUSTOMER) {
	    	customer = CustomerManager.getCustomer(user);
	    }
	    else if (user.getUserType() == UserType.ADMINISTRATOR) {
	    	String customerKeyString = req.getParameter("c_key");
	    	if (customerKeyString != null && !customerKeyString.isEmpty()) {
	    		Key customerKey = KeyFactory.stringToKey(customerKeyString);
		    	customer = CustomerManager.getCustomer(customerKey);
	    	}
	    }
	    else if (user.getUserType() == UserType.CUSTOMER_USER) {
	    	CustomerUser customerUser = 
	    			CustomerUserManager.getCustomerUser(user);
	    	customer = CustomerManager.getCustomer(customerUser.getKey().getParent());
	    }
    	
        // Lets check the action required by the jsp
        String action = req.getParameter("action");
        
        // Common parameters
        String type = req.getParameter("type");
        String successURL = "";
        String failURL = "";
        String additionalParameters = "";
        
        // ADD
        if (action.equals("add")) {
            try {

                // Add StorageDevice
                if (type.equalsIgnoreCase("storageDevice")) {
                	
                    // Check that an Administrator is carrying out the action
            	    if (user.getUserType() != UserType.ADMINISTRATOR) {
            	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	        return;
            	    }
                	
	                successURL = listStorageDeviceJSP;
	                failURL = addStorageDeviceJSP;
	                additionalParameters = "&c_key=" + 
	                		KeyFactory.keyToString(customer.getKey());
            	    
                	addOrUpdateStorageDevice(req, null, true, customer);
	            }
                // Add StorageDeviceContainer
                else if (type.equalsIgnoreCase("storageDeviceContainer")) {
                	
                    // Check that an Administrator is carrying out the action
            	    if (user.getUserType() != UserType.ADMINISTRATOR) {
            	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	        return;
            	    }
                	
	                successURL = listStorageDeviceContainerJSP;
	                failURL = addStorageDeviceContainerJSP;
	                additionalParameters = "&c_key=" + 
	                		KeyFactory.keyToString(customer.getKey());
            	    
                	addOrUpdateStorageDeviceContainer(req, null, true, customer);
	            }
                // Add StorageItem
                else if (type.equalsIgnoreCase("storageItem")) {
                	
                    // Check that an Administrator or Customer is carrying out the action
            	    if (user.getUserType() != User.UserType.ADMINISTRATOR &&
            	    		user.getUserType() != User.UserType.CUSTOMER) {
            	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	        return;
            	    }
                	
	                successURL = listStorageItemJSP;
	                failURL = addStorageItemJSP;
            	    
                	addOrUpdateStorageItem(req, null, true, customer);
	            }
                // Add StorageItemInstance
                else if (type.equalsIgnoreCase("storageItemInstance")) {
                	
                    // Check that an Administrator or Customer is carrying out the action
            	    if (user.getUserType() != User.UserType.ADMINISTRATOR &&
            	    		user.getUserType() != User.UserType.CUSTOMER) {
            	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	        return;
            	    }
            	    
                	String storageItemKeyString = req.getParameter("storageItemId");
                	additionalParameters = "&storageItemId=" + storageItemKeyString;
                	Key storageItemKey = KeyFactory.stringToKey(storageItemKeyString);
                	
	                successURL = listStorageItemInstanceJSP;
	                failURL = addStorageItemInstanceJSP;
            	    
                	addOrUpdateStorageItemInstance(req, null, storageItemKey, true);
	            }
                // Add StorageItemTransaction
                else if (type.equalsIgnoreCase("storageItemTransaction")) {
                	
                    // Check that an Administrator, Customer, or 
                	// CustomerUser is carrying out the action
            	    if (user.getUserType() != User.UserType.ADMINISTRATOR &&
            	    		user.getUserType() != User.UserType.CUSTOMER &&
            	    		user.getUserType() != User.UserType.CUSTOMER_USER) {
            	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	        return;
            	    }
            	    
            	    // Common additional parameters
                	String storageItemInstanceKeyString = 
                			req.getParameter("storageItemInstanceId");
                	Key storageItemInstanceKey = 
                			KeyFactory.stringToKey(storageItemInstanceKeyString);
                	String storageItemKeyString = 
                			KeyFactory.keyToString(storageItemInstanceKey.getParent());
                	additionalParameters = "&storageItemId=" + storageItemKeyString;
                	
                	String pageFrom = req.getParameter("pageFrom");
                	
                	// Additional parameters from editListItemDetail.jsp
                	String storageDevicePartitionKeyString = 
                			req.getParameter("storageDevicePartitionId");
                	if (storageDevicePartitionKeyString != null) {
                		additionalParameters += "&storageDevicePartitionId=" +
                				storageDevicePartitionKeyString;
                	}
                	
                	if (pageFrom.equals("listItemInstance")) {
		                successURL = addStorageItemTransactionItemsJSP;
		                failURL = addStorageItemTransactionItemsJSP;
                	}
                	else if (pageFrom.equals("editListItemDetail")) {
		                successURL = addStorageItemTransactionStorageJSP;
		                failURL = addStorageItemTransactionStorageJSP;
                	}
            	    
                	addStorageItemTransaction(req);
	            }
	            // Add CustomerUser
                else if (type.equalsIgnoreCase("customerUser")) {
                	
                    // Check that an Administrator or Customer is carrying out the action
            	    if (user.getUserType() != User.UserType.ADMINISTRATOR &&
            	    		user.getUserType() != User.UserType.CUSTOMER) {
            	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	        return;
            	    }
            	    
                	successURL = listCustomerUserJSP;
                	failURL = addCustomerUserJSP;
            	    
                	addOrUpdateCustomerUser(req, null, true, customer);
                }
	            // Add StorageDeviceCustomerUser
                else if (type.equalsIgnoreCase("storageDeviceCustomerUser")) {
                	
                    // Check that an Administrator or Customer is carrying out the action
            	    if (user.getUserType() != User.UserType.ADMINISTRATOR &&
            	    		user.getUserType() != User.UserType.CUSTOMER) {
            	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	        return;
            	    }
                	
                	successURL = listStorageDeviceCustomerUserJSP;
                	failURL = addStorageDeviceCustomerUserJSP;
            	    
                	addStorageDeviceCustomerUser(req, customer);
                }
                // Add AlarmTrigger
                else if (type.equalsIgnoreCase("alarmTrigger")) {
                	
                    // Check that an Administrator or Customer is carrying out the action
            	    if (user.getUserType() != UserType.ADMINISTRATOR) {
            	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	        return;
            	    }
                	
	                successURL = listAlarmTriggerJSP;
	                failURL = addAlarmTriggerJSP;
            	    
                	addOrUpdateAlarmTrigger(req, null, true);
	            }
                // No more choices
          		else {
          			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
          			return;
          		}
	        	
	        	// Success URL
	        	resp.sendRedirect(successURL + "?msg=success&action=" + action +
	        			additionalParameters);
            }
            catch (MissingRequiredFieldsException mrfe) {
                resp.sendRedirect(failURL + "?etype=MissingInfo" + additionalParameters);
                return;
            }
            catch (InvalidFieldFormatException iffe) {
                resp.sendRedirect(failURL + "?etype=InvalidFieldFormat" + additionalParameters);
                return;
            }
            catch (ObjectExistsInDatastoreException oeide) {
            	resp.sendRedirect(failURL + "?etype=ObjectExists" + additionalParameters);
                return;
            }
            catch (Exception ex) {
                log.log(Level.SEVERE, ex.toString());
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
	    }
        // UPDATE
	    else if (action.equals("update")) {
	    	
	    	// Common parameters
	    	String keyString = req.getParameter("k");
	    	Key key = null;
	    	if (keyString != null) {
	    		key = KeyFactory.stringToKey(keyString);
	    	}
	    	
	    	try {
	    		
            	// Update StorageDevice
            	if (type.equalsIgnoreCase("storageDevice")) {
            		
                    // Check that a Administrator is carrying out the action
            	    if (user.getUserType() != User.UserType.ADMINISTRATOR) {
            	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	        return;
            	    }

                	successURL = editStorageDeviceJSP;
                	failURL = editStorageDeviceJSP;
            		
            		addOrUpdateStorageDevice(req, key, false, null);
            	}
            	// Update StorageDeviceContainer
            	else if (type.equalsIgnoreCase("storageDeviceContainer")) {
            		
                    // Check that a Administrator is carrying out the action
            	    if (user.getUserType() != User.UserType.ADMINISTRATOR) {
            	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	        return;
            	    }
            		
                	successURL = editStorageDeviceContainerJSP;
                	failURL = editStorageDeviceContainerJSP;
            		
            		addOrUpdateStorageDeviceContainer(req, key, false, null);
            	}
            	// Update StorageItem
            	else if (type.equalsIgnoreCase("storageItem")) {
            		
                    // Check that an Administrator or Customer is carrying out the action
            	    if (user.getUserType() != User.UserType.ADMINISTRATOR &&
            	    		user.getUserType() != User.UserType.CUSTOMER) {
            	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	        return;
            	    }
            		
                	successURL = editStorageItemJSP;
                	failURL = editStorageItemJSP;
                	
                	if (req.getParameter("pageFrom") != null) {
	                	additionalParameters = 
	                			"&pageFrom=" + req.getParameter("pageFrom");
                	}
            		
            		addOrUpdateStorageItem(req, key, false, null);
            	}
            	// Update StorageItemInstance
            	else if (type.equalsIgnoreCase("storageItemInstance")) {
            		
                    // Check that an Administrator or Customer is carrying out the action
            	    if (user.getUserType() != User.UserType.ADMINISTRATOR &&
            	    		user.getUserType() != User.UserType.CUSTOMER) {
            	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	        return;
            	    }
            		
                	successURL = editStorageItemInstanceJSP;
                	failURL = editStorageItemInstanceJSP;
            		
            		addOrUpdateStorageItemInstance(req, key, null, false);
            	}
	            // Update CustomerUser
	    		else if (type.equalsIgnoreCase("customerUser")) {
	    			
	    			String updateType = req.getParameter("update_type");
	    			
	    			// Check that an Administrator, Customer or CustomerUser is carrying out the action
            	    if (user.getUserType() != User.UserType.ADMINISTRATOR &&
            	    		user.getUserType() != User.UserType.CUSTOMER) {
            	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	        return;
            	    }
	    			
            	    // Update info
            	    if (updateType.equalsIgnoreCase("info")) {
	                	successURL = editCustomerUserJSP;
	                	failURL = editCustomerUserJSP;
		    			
		    			addOrUpdateCustomerUser(req, key, false, null);
            	    }
            	    // Update password
            	    else if (updateType.equalsIgnoreCase("password")) {
            	    	successURL = editCustomerUserPasswordJSP;
	                	failURL = editCustomerUserPasswordJSP;
		    			
		    			updateCustomerUserPassword(req, key, user);
            	    }
            	    // There are no more choices
                	else {
                		resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
    	            	return;
                	}
            	}
            	// Update AlarmTrigger
	    		else if (type.equalsIgnoreCase("alarmTrigger")) {
            		
                    // Check that a Administrator or Customer is carrying out the action
            	    if (user.getUserType() != User.UserType.ADMINISTRATOR &&
            	    		user.getUserType() != User.UserType.CUSTOMER) {
            	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	        return;
            	    }

                	successURL = editAlarmTriggerJSP;
                	failURL = editAlarmTriggerJSP;
            		
            		addOrUpdateAlarmTrigger(req, key, false);
            	}
            	// Update AlarmTriggerMessage
	    		else if (type.equalsIgnoreCase("alarmTriggerMessage")) {
            		
                    // Check that a Administrator or Customer is carrying out the action
            	    if (user.getUserType() != User.UserType.ADMINISTRATOR &&
            	    		user.getUserType() != User.UserType.CUSTOMER) {
            	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	        return;
            	    }

                	successURL = editAlarmTriggerMessageJSP;
                	failURL = editAlarmTriggerMessageJSP;
            		
            		updateAlarmTriggerMessage(req, key);
            	}
            	// Update AlarmWarningMessage
	    		else if (type.equalsIgnoreCase("alarmWarningMessage")) {
	    			
                    // Check that a Administrator or Customer is carrying out the action
            	    if (user.getUserType() != User.UserType.ADMINISTRATOR &&
            	    		user.getUserType() != User.UserType.CUSTOMER) {
            	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	        return;
            	    }
	    			
                	successURL = editAlarmWarningMessageJSP;
                	failURL = editAlarmWarningMessageJSP;

	    			updateAlarmWarningMessage(req, key);
            	}
            	// No more choices
          		else {
          			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
          			return;
          		}
            	
    	    	// If success
                resp.sendRedirect(successURL + "?k=" + keyString + 
                		"&msg=success&action=" + action +
                		additionalParameters);
            }
            catch (MissingRequiredFieldsException mrfe) {
                resp.sendRedirect(failURL + "?etype=MissingInfo&k="
                        + keyString + additionalParameters);
                return;
            }
	    	catch (InvalidFieldFormatException iffe) {
                resp.sendRedirect(failURL + "?etype=InvalidFieldFormat&k="
                        + keyString + additionalParameters);
                return;
            }
	    	catch (ObjectExistsInDatastoreException oeide) {
	    		resp.sendRedirect(failURL + "?etype=ObjectExists&k="
                        + keyString + additionalParameters);
                return;
	    	}
            catch (UserValidationException uve) {
                resp.sendRedirect(failURL + "?etype=InvalidInfo&k=" + 
                		keyString + additionalParameters);
                return;
            }
            catch (Exception ex) {
                log.log(Level.SEVERE, ex.toString());
                ex.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        }
        // No more choices
  		else {
  			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
  			return;
  		}
    }
    
    /**
     * Add or Update StorageDevice
     * @param req: the HTTPServletRequest
     * @param key: the StorageDevice key (null if Add)
     * @param add: whether to Add object or not
     * @param customer: the Customer involved (null if Edit)
     * @throws MissingRequiredFieldsException
     */
    private void addOrUpdateStorageDevice(HttpServletRequest req, 
    		Key key, boolean add, Customer customer) 
    		throws MissingRequiredFieldsException {
        
    	// StorageDevice data
    	
    	String deviceModelKeyString =
    			req.getParameter("deviceModelId");
    	Key deviceModelKey = null;
    	if (deviceModelKeyString != null && !deviceModelKeyString.isEmpty()) {
    		deviceModelKey = KeyFactory.stringToKey(deviceModelKeyString);
    	}
    	
    	String storageDeviceSerialNumber = 
        		req.getParameter("storageDeviceSerialNumber");
    	String storageDeviceNickname = 
        		req.getParameter("storageDeviceNickname");
        String storageDeviceDescription = 
        		req.getParameter("storageDeviceDescription");
        String storageDevicePrivateEncryptionKey = "0";
        
        String storageDeviceManufacturedDateString = 
        		req.getParameter("storageDeviceManufacturedDate");
        Date storageDeviceManufacturedDate = null;
        if (!storageDeviceManufacturedDateString.isEmpty()) {
        	storageDeviceManufacturedDate = 
        			DateManager.getSimpleDateValueTaiwan(
        					storageDeviceManufacturedDateString);
        }
        
        String storageDeviceShippingDateString = 
        		req.getParameter("storageDeviceShippingDate");
        Date storageDeviceShippingDate = null;
        if (!storageDeviceShippingDateString.isEmpty()) {
        	storageDeviceShippingDate = 
        			DateManager.getSimpleDateValueTaiwan(
        					storageDeviceShippingDateString);
        }
        
        String sensorDataEffectivePeriodString =
        		req.getParameter("sensorDataEffectivePeriod");
        Integer sensorDataEffectivePeriod = null;
        if (!sensorDataEffectivePeriodString.isEmpty()) {
        	sensorDataEffectivePeriod = 
        			Integer.parseInt(sensorDataEffectivePeriodString);
        }
        
        String alarmMessageEffectivePeriodString =
        		req.getParameter("alarmMessageEffectivePeriod");
        Integer alarmMessageEffectivePeriod = null;
        if (!alarmMessageEffectivePeriodString.isEmpty()) {
        	alarmMessageEffectivePeriod = 
        			Integer.parseInt(alarmMessageEffectivePeriodString);
        }
        
        String enableSensorDataUploadString = 
        		req.getParameter("enableSensorDataUpload");
        Boolean enableSensorDataUpload = false;
        if (enableSensorDataUploadString != null) {
        	enableSensorDataUpload = true;
        }
        
        String sensorReadingCacheSizeString =
        		req.getParameter("sensorReadingCacheSize");
        Integer sensorReadingCacheSize = null;
        if (!sensorReadingCacheSizeString.isEmpty()) {
        	sensorReadingCacheSize =
        			Integer.parseInt(sensorReadingCacheSizeString);
        }
        
        String storageDeviceComments =
        		req.getParameter("storageDeviceComments");
        
        if (add) {
        	
        	// StorageDeviceDoors
            List<DeviceModelDoor> deviceModelDoors = 
            		DeviceModelDoorManager.getAllDeviceModelDoorsFromDeviceModel(
            				deviceModelKey);
            HashMap<StorageDeviceDoor, ArrayList<StorageDevicePartition>> storageDeviceDoorPartitions =
            		new HashMap<>();
            for (DeviceModelDoor deviceModelDoor : deviceModelDoors) {
            	
            	String storageDeviceDoorComments = null;
            	
            	StorageDeviceDoor storageDeviceDoor = 
            			new StorageDeviceDoor(deviceModelDoor.getKey(), 
            					storageDeviceDoorComments);
            	
            	// StorageDevicePartitions
            	List<DeviceModelPartition> deviceModelPartitions = 
            			DeviceModelPartitionManager.getAllDeviceModelPartitionsFromDeviceModelDoor(
            					deviceModelDoor.getKey());
            	ArrayList<StorageDevicePartition> storageDevicePartitions =
            			new ArrayList<>();
            	for (DeviceModelPartition deviceModelPartition : deviceModelPartitions) {
            		
            		String storageDevicePartitionComments = null;
            		
            		StorageDevicePartition storageDevicePartition = 
            				new StorageDevicePartition(deviceModelPartition.getKey(),
            						storageDevicePartitionComments);
            		storageDevicePartitions.add(storageDevicePartition);
            	}
            	
            	storageDeviceDoorPartitions.put(storageDeviceDoor, 
            			storageDevicePartitions);
            }
        	
	        StorageDevice storageDevice = 
	        		new StorageDevice(
	        				deviceModelKey, 
	        	    		storageDeviceSerialNumber,
	        	    		storageDeviceNickname,
	        	    		storageDeviceDescription, 
	        	    		storageDevicePrivateEncryptionKey,
	        	    		storageDeviceManufacturedDate, 
	        	    		storageDeviceShippingDate,
	        	    		sensorDataEffectivePeriod,
	        	    		alarmMessageEffectivePeriod,
	        	    		enableSensorDataUpload,
	        	    		sensorReadingCacheSize,
	        	    		storageDeviceComments
	        	    		);
	        StorageDeviceManager.putStorageDevice(customer.getKey(), 
	        		storageDevice);
	        
	        // Add StorageDeviceDoors and StorageDevicePartitions
	        for (StorageDeviceDoor storageDeviceDoor : 
	        		storageDeviceDoorPartitions.keySet()) {
	        	
	        	StorageDeviceDoorManager.putStorageDeviceDoor(
	        			storageDevice.getKey(), storageDeviceDoor);
	        	
	        	for (StorageDevicePartition storageDevicePartition : 
	        			storageDeviceDoorPartitions.get(storageDeviceDoor)) {
	        		
	        		StorageDevicePartitionManager.putStorageDevicePartition(
	        				storageDeviceDoor.getKey(), storageDevicePartition);
	        	}
	        }
	        
	        // SensorInstances
	        DeviceModel deviceModel = DeviceModelManager.getDeviceModel(deviceModelKey);
	    	SensorType tempSensorType = 
	    			SensorTypeManager.getSensorType("Temperature");
	    	SensorType humSensorType =
	    			SensorTypeManager.getSensorType("Humidity");
	    	SensorType doorSensorType =
	    			SensorTypeManager.getSensorType("Door Open/Close");
	        
	    	SensorInstance temp1 = null;
	    	SensorInstance hum1 = null;
	    	SensorInstance temp2 = null;
	    	SensorInstance hum2 = null;
	    	ArrayList<SensorInstance> doorSensorInstances = new ArrayList<>();
	    	if (deviceModel.getTemp1Hum1()) {
	        	temp1 = new SensorInstance(tempSensorType.getKey(),
	        		    		null,
	        		    		"Temperature_1",
	        		    		SensorStatus.OK,
	        		    		null);
	        	hum1 = new SensorInstance(humSensorType.getKey(),
	        		    		null,
	        		    		"Humidity_1",
	        		    		SensorStatus.OK,
	        		    		null);
	        	
	        }
	    	if (deviceModel.getTemp2Hum2()) {
	        	temp2 = new SensorInstance(tempSensorType.getKey(),
	        		    		null,
	        		    		"Temperature_2",
	        		    		SensorStatus.OK,
	        		    		null);
	        	hum2 = new SensorInstance(humSensorType.getKey(),
	        		    		null,
	        		    		"Humidity_2",
	        		    		SensorStatus.OK,
	        		    		null);
	        }
	    	if (deviceModel.getDoorOpenClose()) {
	    		for (StorageDeviceDoor storageDeviceDoor : 
	        		storageDeviceDoorPartitions.keySet()) {
	    			
	    			DeviceModelDoor deviceModelDoor  = 
	    					DeviceModelDoorManager.getDeviceModelDoor(
	    							storageDeviceDoor.getDeviceModelDoor());
	    			
	    			SensorInstance doorSensorInstance = 
	    					new SensorInstance(doorSensorType.getKey(),
	            		    		storageDeviceDoor.getKey(),
	            		    		"Door_" + deviceModelDoor.getDeviceModelDoorNumber(),
	            		    		SensorStatus.OK,
	            		    		null);
	    			doorSensorInstances.add(doorSensorInstance);
	    		}
	    	}
	        
	        // Add SensorInstances
	        if (temp1 != null) {
	        	SensorInstanceManager.putSensorInstance(
	        			storageDevice.getKey(), temp1);
	        }
	        if (hum1 != null) {
	        	SensorInstanceManager.putSensorInstance(
	        			storageDevice.getKey(), hum1);
	        }
	        if (temp2 != null) {
	        	SensorInstanceManager.putSensorInstance(
	        			storageDevice.getKey(), temp2);
	        }
	        if (hum2 != null) {
	        	SensorInstanceManager.putSensorInstance(
	        			storageDevice.getKey(), hum2);
	        }
	        for (SensorInstance doorSensorInstance : doorSensorInstances) {
	        	SensorInstanceManager.putSensorInstance(
	        			storageDevice.getKey(), doorSensorInstance);
	        }
        }
        else {
            StorageDeviceManager.updateStorageDeviceAttributes(
            		key,
            		storageDeviceSerialNumber, 
            		storageDeviceNickname,
            		storageDeviceDescription, 
            		storageDeviceManufacturedDate, 
            		storageDeviceShippingDate, 
            		sensorDataEffectivePeriod,
            		alarmMessageEffectivePeriod,
            		enableSensorDataUpload,
            		sensorReadingCacheSize,
            		storageDeviceComments);
        }
    }
    
    /**
     * Add or Update StorageDeviceContainer
     * @param req: the HTTPServletRequest
     * @param key: the StorageDeviceContainer key (null if Add)
     * @param add: whether to Add object or not
     * @param customer: the Customer involved (null if Edit)
     * @throws MissingRequiredFieldsException 
     */
    private void addOrUpdateStorageDeviceContainer(HttpServletRequest req, 
    		Key key, boolean add, Customer customer) 
    		throws MissingRequiredFieldsException {
    	
//    	String storageDeviceContainerName = 
//        		req.getParameter("storageDeviceContainerName");
//        String storageDeviceContainerDescription = 
//        		req.getParameter("storageDeviceContainerDescription");
//        String storageDeviceContainerVersionNumber = 
//        		req.getParameter("storageDeviceContainerVersionNumber");
//        
//        String storageDeviceContainerDesignTimeString = 
//        		req.getParameter("storageDeviceContainerDesignTime");
//        Date storageDeviceContainerDesignTime = null;
//        if (!storageDeviceContainerDesignTimeString.isEmpty()) {
//        	storageDeviceContainerDesignTime = 
//        			DateManager.getDateValueISO8601(storageDeviceContainerDesignTimeString);
//        }
//        
//        String storageDeviceContainerComments =
//        		req.getParameter("storageDeviceContainerComments");
//        
//        if (add) {
//	        StorageDeviceContainer storageDeviceContainer = 
//	        		new StorageDeviceContainer(
//	        	    		storageDeviceContainerName, 
//	        	    		storageDeviceContainerDescription,
//	        	    		storageDeviceContainerVersionNumber,
//	        	    		storageDeviceContainerDesignTime,
//	        	    		storageDeviceContainerComments
//	        	    		) ;
//	        StorageDeviceContainerManager.putStorageDeviceContainer(storageDeviceContainer);
//        }
//        else {
//            StorageDeviceContainerManager.updateStorageDeviceContainerAttributes(
//            		key, 
//            		storageDeviceContainerName, 
//            		storageDeviceContainerDescription, 
//            		storageDeviceContainerVersionNumber, 
//            		storageDeviceContainerDesignTime, 
//            		storageDeviceContainerComments
//            		);
//        }
//        
//        // Update deviceServiceTypeListVersion
//        SystemManager.updateStorageDeviceContainerListVersion();
    }
    
    /**
     * Add or Update StorageItem
     * @param req: the HTTPServletRequest
     * @param key: the StorageItem key (null if Add)
     * @param add: whether to Add object or not
     * @param customer: the Customer involved (null if Edit)
     * @throws MissingRequiredFieldsException 
     */
    private void addOrUpdateStorageItem(HttpServletRequest req, 
    		Key key, boolean add, Customer customer) 
    		throws MissingRequiredFieldsException {
    	
    	String storageItemPartNumber = 
        		req.getParameter("storageItemPartNumber");
    	String storageItemName = 
        		req.getParameter("storageItemName");
        String storageItemDescription = 
        		req.getParameter("storageItemDescription");
        
        String storageItemMSDLevelString = 
        		req.getParameter("storageItemMSDLevel");
        Integer storageItemMSDLevel = null;
        if (!storageItemMSDLevelString.isEmpty()) {
        	storageItemMSDLevel = Integer.parseInt(storageItemMSDLevelString);
        }

        String storageItemComments =
        		req.getParameter("storageItemComments");
        
        String addStorageItemInstanceString = 
        		req.getParameter("addStorageItemInstance");
        boolean addStorageItemInstance = false;
        if (addStorageItemInstanceString != null && 
        		!addStorageItemInstanceString.isEmpty()) {
        	addStorageItemInstance = true;
        }
        
        if (add) {
	        StorageItem storageItem = 
	        		new StorageItem(
	        				customer.getKey(),
	        				storageItemPartNumber,
	        	    		storageItemName, 
	        	    		storageItemDescription,
	        	    		storageItemMSDLevel,
	        	    		storageItemComments
	        	    		);
	        StorageItemManager.putStorageItem(customer.getKey(), storageItem);
	        
	        // Add StorageItemInstance
	        if (addStorageItemInstance) {
	        	this.addOrUpdateStorageItemInstance(
	        			req, null, storageItem.getKey(), true);
	        }
        }
        else {
            StorageItemManager.updateStorageItemAttributes(
            		key, 
            		storageItemPartNumber, 
            		storageItemName, 
            		storageItemDescription, 
            		storageItemMSDLevel,
            		storageItemComments);
        }
    }
    
    /**
     * Add or Update StorageItemInstance
     * @param req: the HTTPServletRequest
     * @param key: the StorageItemInstance key (null if Add)
     * @param storageItemKey: the StorageItem key where this
     * 			StorageItemInstance will be added (null if Update)
     * @param add: whether to Add object or not
     * @throws MissingRequiredFieldsException 
     */
    private void addOrUpdateStorageItemInstance(HttpServletRequest req, 
    		Key key, Key storageItemKey, boolean add) 
    		throws MissingRequiredFieldsException {

    	String storageItemInstanceOwnerKeyString =
    			req.getParameter("storageItemInstanceOwner");
    	Key storageItemInstanceOwnerKey = null;
    	if (!storageItemInstanceOwnerKeyString.isEmpty()) {
    		storageItemInstanceOwnerKey = KeyFactory.stringToKey(
    				storageItemInstanceOwnerKeyString);
    	}

    	String storageItemInstanceSerialNumber = 
        		req.getParameter("storageItemInstanceSerialNumber");
    	String storageItemInstanceLabel = 
        		req.getParameter("storageItemInstanceLabel");
        
        String storageItemInstanceExpirationTimeString =
        		req.getParameter("storageItemInstanceExpirationTime");
        Date storageItemInstanceExpirationTime = null;
        if (!storageItemInstanceExpirationTimeString.isEmpty()) {
        	storageItemInstanceExpirationTime = DateManager.getSimpleDateValueTaiwan(
        			storageItemInstanceExpirationTimeString);
        }

        String storageItemInstanceWorksheet = 
        		req.getParameter("storageItemInstanceWorksheet");
        
        String storageItemInstanceIndicationLightString =
        		req.getParameter("storageItemInstanceIndicationLight");
        IndicationLightValue storageItemInstanceIndicationLight = null;
        if (storageItemInstanceIndicationLightString != null && 
        		!storageItemInstanceIndicationLightString.isEmpty()) {
        	storageItemInstanceIndicationLight = 
        			StorageItemInstance.getIndicationLightValueFromString(
        					storageItemInstanceIndicationLightString);
        }
        
        String storageItemInstanceComments =
        		req.getParameter("storageItemInstanceComments");
        
        if (add) {
	        StorageItemInstance storageItemInstance = 
	        		new StorageItemInstance(
	        				storageItemInstanceOwnerKey,
	        				storageItemInstanceSerialNumber,
	        	    		storageItemInstanceLabel, 
	        	    		storageItemInstanceExpirationTime,
	        	    		storageItemInstanceWorksheet,
	        	    		storageItemInstanceIndicationLight,
	        	    		storageItemInstanceComments
	        	    		);
	        StorageItemInstanceManager.putStorageItemInstance(storageItemKey, 
	        		storageItemInstance);
        }
        else {
            StorageItemInstanceManager.updateStorageItemInstanceAttributes(
            		key, 
            		storageItemInstanceOwnerKey, 
            		storageItemInstanceSerialNumber, 
            		storageItemInstanceLabel, 
            		storageItemInstanceExpirationTime, 
            		storageItemInstanceWorksheet,
            		storageItemInstanceIndicationLight,
            		storageItemInstanceComments);
        }
    }
    
    /**
     * Add StorageItemTransaction
     * @param req: the HTTPServletRequest
     * @throws MissingRequiredFieldsException 
     */
    private void addStorageItemTransaction(HttpServletRequest req) 
    		throws MissingRequiredFieldsException {
    	
    	String storageItemInstanceKeyString = 
        		req.getParameter("storageItemInstanceId");
    	Key storageItemInstanceKey = null;
    	if (!storageItemInstanceKeyString.isEmpty()) {
    		storageItemInstanceKey = 
    				KeyFactory.stringToKey(storageItemInstanceKeyString);
    	}
    	
        String storageItemTransactionPerformedByString = 
        		req.getParameter("storageItemTransactionPerformedBy");
        Key storageItemTransactionPerformedBy = null;
        if (!storageItemTransactionPerformedByString.isEmpty()) {
        	storageItemTransactionPerformedBy = 
        			KeyFactory.stringToKey(storageItemTransactionPerformedByString);
        }
        
        String storageDevicePartitionKeyString = 
        		req.getParameter("storageDevicePartitionId");
        Key storageDevicePartitionKey = null;
        if (storageDevicePartitionKeyString != null && 
        		!storageDevicePartitionKeyString.isEmpty()) {
        	storageDevicePartitionKey = 
        			KeyFactory.stringToKey(storageDevicePartitionKeyString);
        }
        
        StorageItemTransactionAction storageItemTransactionAction = 
        		StorageItemTransaction.getStorageItemTransactionActionFromString(
        				req.getParameter("storageItemTransactionAction"));

        StorageItemTransaction storageItemTransaction = 
        		new StorageItemTransaction(
        				storageItemInstanceKey,
        	    		storageItemTransactionPerformedBy,
        	    		storageDevicePartitionKey,
        	    		storageItemTransactionAction
        	    		) ;

        StorageItemTransactionManager.putStorageItemTransaction(
        		storageItemInstanceKey,
        		storageItemTransaction);
    }
    
    /**
     * Add or Update CustomerUser
     * @param req: the HTTPServletRequest
     * @param key: the CustomerUser key (null if Add)
     * @param add: whether to Add object or not
     * @param customer: the Customer involved (null if Edit)
     * @throws MissingRequiredFieldsException 
     * @throws ObjectExistsInDatastoreException 
     * @throws InvalidFieldFormatException 
     */
    private void addOrUpdateCustomerUser(HttpServletRequest req, 
    		Key key, boolean add, Customer customer) 
    		throws MissingRequiredFieldsException, 
    		ObjectExistsInDatastoreException, 
    		InvalidFieldFormatException {
    	
    	String customerUserName = req.getParameter("customerUserName");
    	String customerUserDescription = req.getParameter("customerUserDescription");
    	Gender customerUserGender = 
    			CustomerUser.getGenderFromString(req.getParameter("customerUserGender"));
    	String customerUserTitle = req.getParameter("customerUserTitle");
    	
    	String departmentKeyString = req.getParameter("departmentId");
    	Long departmentKey = null;
    	if (departmentKeyString != null && !departmentKeyString.isEmpty()) {
    		departmentKey = Long.parseLong(departmentKeyString);
    	}
    	
    	String customerUserPhoneNumberString = req.getParameter("customerUserPhoneNumber");
		PhoneNumber customerUserPhoneNumber = null;
		if (!customerUserPhoneNumberString.isEmpty()) {
			customerUserPhoneNumber = new PhoneNumber(customerUserPhoneNumberString);
		}
    	
        String customerUserComments = req.getParameter("customerUserComments");

        if (add) {
        	String userEmailString = req.getParameter("userEmail");
        	Email userEmail = null;
        	if (!userEmailString.isEmpty()) {
        		userEmail = new Email(userEmailString);
        	}
        	
        	String userPassword = req.getParameter("userPassword");
        	
        	User neoUser = new User(userEmail,
            		userPassword,
                    UserType.CUSTOMER_USER);
        	
	        CustomerUser customerUser = new CustomerUser(
	        		neoUser,
	        		customerUserName,
	        		customerUserDescription,
	        		customerUserGender,
	        		customerUserTitle,
	        		departmentKey,
	        		customerUserPhoneNumber,
	        		customerUserComments);
	        CustomerUserManager.putCustomerUser(customer.getKey(), customerUser);
        }
        else {
            CustomerUserManager.updateCustomerUserAttributes(
            		key, 
            		customerUserName, 
            		customerUserDescription, 
            		customerUserGender, 
            		customerUserTitle, 
            		departmentKey, 
            		customerUserPhoneNumber, 
            		customerUserComments);
        }
    }
    
    /**
     * Update a Customer User's password
     * @param req: the HTTPServletRequest
     * @param key: the CustomerUser key
     * @param user: the User that is logged in
     * @throws MissingRequiredFieldsException 
     * @throws UserValidationException 
     */
    private void updateCustomerUserPassword(HttpServletRequest req, Key key, 
    		User user) 
    		throws MissingRequiredFieldsException, UserValidationException {

    	// Check old password if CustomerUser is logged in
    	if (user.getUserType() == User.UserType.CUSTOMER_USER) {
    		String oldPassword = req.getParameter("userPassword");
    		boolean valid = user.validateUser(user.getUserEmail(), oldPassword);
    		if (!valid) {
    			throw new UserValidationException(user, "Incorrect password!");
    		}
    	}
    	
		String newPassword = req.getParameter("userNewPassword");

        CustomerUserManager.updateCustomerUserPassword(
                key,
                newPassword
                );
    }
    
    /**
     * Add StorageDeviceCustomerUser
     * @param req: the HTTPServletRequest
     * @throws MissingRequiredFieldsException 
     */
    private void addStorageDeviceCustomerUser(HttpServletRequest req, 
    		Customer customer) 
    		throws MissingRequiredFieldsException {
    	

    }
    
    /**
     * Add or Update AlarmTrigger
     * @param req: the HTTPServletRequest
     * @param key: the AlarmTrigger key (null if add)
     * @param add: whether to Add object or not
     * @throws MissingRequiredFieldsException
     */
    private void addOrUpdateAlarmTrigger(HttpServletRequest req, 
    		Key key, boolean add) 
    		throws MissingRequiredFieldsException {
        
    	// AlarmTrigger data
    	String storageDeviceKeyString = req.getParameter("storageDeviceId");
    	Key storageDeviceKey = null;
    	if (storageDeviceKeyString != null && !storageDeviceKeyString.isEmpty()) {
    		storageDeviceKey = KeyFactory.stringToKey(storageDeviceKeyString);
    	}
    	
    	String sensorTypeKeyString = req.getParameter("sensorTypeId");
    	Long sensorTypeKey = null;
    	if (sensorTypeKeyString!= null && !sensorTypeKeyString.isEmpty()) {
    		sensorTypeKey = Long.parseLong(sensorTypeKeyString);
    	}
    	
    	List<SensorInstance> sensorInstances = null;
    	if (storageDeviceKey != null && sensorTypeKey != null) {
	    	 sensorInstances = SensorInstanceManager.getSensorInstancesFromStorageDevice(
	    					storageDeviceKey, sensorTypeKey);
    	}
    	
    	String alarmTriggerCodeString = req.getParameter("alarmTriggerCode");
    	Integer alarmTriggerCode = Integer.parseInt(alarmTriggerCodeString);
    	if (!alarmTriggerCodeString.isEmpty()) {
    		alarmTriggerCode = Integer.parseInt(alarmTriggerCodeString);
    	}
    	
    	String alarmTriggerConditionOperatorString = 
    			req.getParameter("alarmTriggerConditionOperator");
    	AlarmTriggerConditionOperator alarmTriggerConditionOperator = null;
		alarmTriggerConditionOperator = 
				AlarmTrigger.getAlarmTriggerConditionOperatorFromString(
						alarmTriggerConditionOperatorString);
		
		String alarmTriggerConditionValueString = req.getParameter("alarmTriggerConditionValue");
		Double alarmTriggerConditionValue = null;
		if (!alarmTriggerConditionValueString.isEmpty()) {
			alarmTriggerConditionValue = Double.parseDouble(alarmTriggerConditionValueString);
		}
		
		String alarmTriggerMaxCountString = req.getParameter("alarmTriggerMaxCount");
		Integer alarmTriggerMaxCount = null;
		if (!alarmTriggerMaxCountString.isEmpty()) {
			alarmTriggerMaxCount = Integer.parseInt(alarmTriggerMaxCountString);
		}
		
		String alarmTriggerLevelString = req.getParameter("alarmTriggerLevel");
		AlarmTriggerLevel alarmTriggerLevel = 
				AlarmTrigger.getAlarmTriggerLevelFromString(alarmTriggerLevelString);
		
		String alarmTriggerComments = req.getParameter("alarmTriggerComments");
		
        if (add) {
        	for (SensorInstance sensorInstance : sensorInstances) {
		        AlarmTrigger alarmTrigger = new AlarmTrigger(
		        		sensorInstance.getStorageDeviceDoor(),
		        		alarmTriggerCode, 
		        		alarmTriggerConditionOperator,
		        		alarmTriggerConditionValue,
		        		alarmTriggerMaxCount,
		        		alarmTriggerLevel,
		        		alarmTriggerComments);
		        
		        AlarmTriggerManager.putAlarmTrigger(
		        		sensorInstance.getKey(), alarmTrigger);
        	}
        }
        else {
        	AlarmTriggerManager.updateAlarmTriggerAttributes(
        			key, 
        			alarmTriggerCode, 
        			alarmTriggerConditionOperator, 
        			alarmTriggerConditionValue, 
        			alarmTriggerMaxCount, 
        			alarmTriggerLevel, 
        			alarmTriggerComments);
        }
    }
    
    /**
     * Update AlarmTriggerMessage
     * @param req: the HTTPServletRequest
     * @param key: the key of AlarmTriggerMessage to modify
     * @throws MissingRequiredFieldsException
     */
    private void updateAlarmTriggerMessage(HttpServletRequest req, 
    		Key key) 
    		throws MissingRequiredFieldsException {
        
    	// AlarmTriggerMessage data
    	String alarmTriggerMessageStatusString = 
    			req.getParameter("alarmTriggerMessageStatus");
    	AlarmTriggerMessageStatus alarmTriggerMessageStatus = 
    			AlarmTriggerMessage.getAlarmTriggerMessageStatusFromString(
    					alarmTriggerMessageStatusString);
    	
    	String alarmTriggerMessageNote =
    			req.getParameter("alarmTriggerMessageNote");
    	
    	AlarmTriggerMessageManager.updateAlarmTriggerMessageAttributes(
    			key, 
    			alarmTriggerMessageStatus,
    			alarmTriggerMessageNote);
    }
    
    /**
     * Update AlarmWarningMessage
     * @param req: the HTTPServletRequest
     * @param key: the AlarmWarningMessage key
     * @throws MissingRequiredFieldsException 
     */
    private void updateAlarmWarningMessage(HttpServletRequest req, Key key) 
    		throws MissingRequiredFieldsException {

    	String alarmWarningMessageStatusString = 
    			req.getParameter("alarmWarningMessageStatus");
    	AlarmWarningMessageStatus alarmWarningMessageStatus = null;
    	if (!alarmWarningMessageStatusString.isEmpty()) {
    		alarmWarningMessageStatus = 
    				AlarmWarningMessage.getAlarmWarningMessageStatusFromString(
    						alarmWarningMessageStatusString);
    	}
    	
    	String alarmWarningMessageNote = req.getParameter("alarmWarningMessageNote");

        AlarmWarningMessageManager.updateAlarmWarningMessageAttributes(
        		key, 
        		alarmWarningMessageStatus,
        		alarmWarningMessageNote);
    }
}