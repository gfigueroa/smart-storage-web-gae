/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PhoneNumber;

import datastore.CustomerUser.Gender;
import exceptions.InexistentObjectException;
import exceptions.InvalidFieldFormatException;
import exceptions.MissingRequiredFieldsException;
import exceptions.ObjectExistsInDatastoreException;
import exceptions.UserValidationException;

/**
 * This class is used to manage the GAE Datastore operations 
 * (get, put, delete, update) made on the CustomerUser class.
 */

public class CustomerUserManager {
	
	private static final Logger log = 
        Logger.getLogger(CustomerUserManager.class.getName());

	/**
     * Get a CustomerUser instance from the datastore given its user entity.
     * The method uses the user's email field to obtain the CustomerUser key.
     * @param user
     * 			: the user belonging to this customerUser
     * @return customerUser instance, null if customerUser is not found
     */
	public static CustomerUser getCustomerUser(User user) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key key = KeyFactory.createKey(
				user.getKey().getParent().getParent(),
				CustomerUser.class.getSimpleName(), 
                user.getUserEmail().getEmail());
		CustomerUser customerUser;
		try  {
			customerUser = pm.getObjectById(CustomerUser.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return customerUser;
	}
	
	/**
     * Get a CustomerUser instance from the datastore given the user's email.
     * The method uses this email to obtain the CustomerUser key.
     * @param email
     * 			: the customerUser's email address
     * @return customerUser instance, null if customerUser is not found
     */
	public static CustomerUser getCustomerUser(Email email) {		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key key = KeyFactory.createKey(CustomerUser.class.getSimpleName(), 
                                       email.getEmail());
		
		CustomerUser customerUser;
		try  {
			customerUser = pm.getObjectById(CustomerUser.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return customerUser;
	}
	
	/**
     * Get a CustomerUser instance from the datastore given the CustomerUser key.
     * @param key
     * 			: the customerUser's key
     * @return customerUser instance, null if customerUser is not found
     */
	public static CustomerUser getCustomerUser(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		CustomerUser customerUser;
		try  {
			customerUser = pm.getObjectById(CustomerUser.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return customerUser;
	}
	
	/**
     * Get ALL the CustomerUsers in the database and return them
     * in a List structure
     * @return all customerUsers in the datastore
     * TODO: Make more efficient "touching" of the users
     */
    @SuppressWarnings("unchecked")
	public static List<CustomerUser> getAllCustomerUsers() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(CustomerUser.class);

        try {
        	List<CustomerUser> customerUsers = (List<CustomerUser>) query.execute();
        	// Touch the user to keep in memory
        	for (CustomerUser customerUser : customerUsers) {
        		customerUser.getUser();
        	}
        	return customerUsers;
            // return (List<CustomerUser>) query.execute();
        } finally {
        	pm.close();
            query.closeAll();
        }
    }
    
    /**
     * Get all CustomerUser instances from the datastore that belong to
     * this Customer.
     * @param customerKey
     * @return All CustomerUser instances that belong to the given Customer
     * TODO: Inefficient touching of objects
     */
	public static List<CustomerUser> getAllCustomerUsersFromCustomer(
			Key customerKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        Customer customer = 
        		pm.getObjectById(Customer.class, customerKey);
         
        List<CustomerUser> result = null;
        ArrayList<CustomerUser> finalResult = 
        		new ArrayList<CustomerUser>();
        try {
            result = customer.getCustomerUsers();
            for (CustomerUser customerUser : result) {
            	customerUser.getKey();
            	finalResult.add(customerUser);
            }
        }
        finally {
            pm.close();
        }

        return finalResult;
    }
	
	/**
     * Put CustomerUser into datastore.
     * Stores the given CustomerUser instance in the datastore for this
     * Customer.
     * @param customerKey
     *          : the key of the Customer where the customerUser will be added
     * @param customerUser
     *          : the CustomerUser instance to customerUser
	 * @throws ObjectExistsInDatastoreException 
     */
    public static void putCustomerUser(Key customerKey, 
    		CustomerUser customerUser) throws ObjectExistsInDatastoreException {
    	
        PersistenceManager pm = PMF.get().getPersistenceManager();
        
		// Check if the user already exists in the datastore
		Email email = customerUser.getUser().getUserEmail();
		Key administratorKey = 
				KeyFactory.createKey(Administrator.class.getSimpleName(), email.getEmail());
		Key custKey = 
				KeyFactory.createKey(Customer.class.getSimpleName(), email.getEmail());
		Key customerUserKey =
				KeyFactory.createKey(CustomerUser.class.getSimpleName(), email.getEmail());
		
		if (DatastoreManager.entityExists(Administrator.class, administratorKey) ||
				DatastoreManager.entityExists(Customer.class, custKey) || 
				DatastoreManager.entityExists(CustomerUser.class, customerUserKey)) {
			
			throw new ObjectExistsInDatastoreException(customerUser, "User \"" + 
					customerUser.getUser().getUserEmail().getEmail() + 
					"\" already exists in the datastore.");
		}
         
        Transaction tx = pm.currentTransaction();
        try {
            Customer customer = 
                    pm.getObjectById(Customer.class, customerKey);
            tx.begin();
            customer.addCustomerUser(customerUser);
            customer.updateCustomerUserVersion();
            tx.commit();
            log.info("CustomerUser \"" + customerUser.getCustomerUserName() + 
                "\" stored successfully in datastore.");
        }
        finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }
     
    /**
    * Delete CustomerUser from datastore.
    * Deletes the CustomerUser corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the CustomerUser instance to delete
    */
    public static void deleteCustomerUser(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            Customer customer = pm.getObjectById(Customer.class, key.getParent());
            CustomerUser customerUser = pm.getObjectById(CustomerUser.class, key);
            String customerUserContent = customerUser.getCustomerUserName();
            tx.begin();
            customer.removeCustomerUser(customerUser);
            customer.updateCustomerUserVersion();
            tx.commit();
            log.info("CustomerUser \"" + customerUserContent + 
                     "\" deleted successfully from datastore.");
        }
        catch (InexistentObjectException e) {
            e.printStackTrace();
        }
        finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }
	
	/**
    * Update CustomerUser password in datastore.
    * Update's the customerUser's password in the datastore.
    * @param email
    * 			: the email of the customerUser whose password will be changed
    * @param currentPassword
    * 			: the current password of this customerUser
    * @param newPassword
    * 			: the new password for this customerUser
    * @throws UserValidationException 
	* @throws MissingRequiredFieldsException 
    */
	public static void updateCustomerUserPassword(Email email, String currentPassword,
			String newPassword) throws UserValidationException, MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			Key key = KeyFactory.createKey(CustomerUser.class.getSimpleName(), email.getEmail());
			CustomerUser customerUser = pm.getObjectById(CustomerUser.class, key);
			tx.begin();
			if (customerUser.getUser().validateUser(email, currentPassword)) {
				customerUser.getUser().setUserPassword(newPassword);
				tx.commit();
				log.info("CustomerUser \"" + email.getEmail() + "\"'s password updated in datastore.");
			}
			else {
				tx.rollback();
				throw new UserValidationException(customerUser.getUser(), 
						"User email and/or password are incorrect.");
			}
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Update CustomerUser password in datastore.
    * Update's the CustomerUser's password in the datastore.
    * @param key
    * 			: the key of the CustomerUser whose password will be changed
    * @param newPassword
    * 			: the new password for this customerUser
	* @throws MissingRequiredFieldsException 
    */
	public static void updateCustomerUserPassword(Key key, String newPassword) 
			throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			CustomerUser customerUser = pm.getObjectById(CustomerUser.class, key);
			tx.begin();
			customerUser.getUser().setUserPassword(newPassword);
			tx.commit();
			log.info("CustomerUser \"" + customerUser.getUser().getUserEmail().getEmail() + 
					"\"'s password updated in datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Update CustomerUser attributes.
    * Update's the given customerUser's attributes in the datastore.
    * @param key
    * 			: the key of the customerUser whose attributes will be updated
     * @param customerUserName
     * 			: customerUser name
     * @param customerUserDescription
     * 			: customerUser description
     * @param customerUserGender
     * 			: customerUser gender
     * @param customerUserTitle
     * 			: customerUser title
     * @param department
     * 			: customerUser Department key
     * @param customerUserPhoneNumber
     * 			: customerUser phone number
     * @param customerUserComments
     * 			: customerUser comments
	* @throws MissingRequiredFieldsException 
	* @throws InvalidFieldFormatException 
    */
	public static void updateCustomerUserAttributes(
			Key key, 
    		String customerUserName, 
    		String customerUserDescription, 
            Gender customerUserGender,
            String customerUserTitle,
            Long department, 
            PhoneNumber customerUserPhoneNumber, 
    		String customerUserComments) 
					throws MissingRequiredFieldsException, 
					InvalidFieldFormatException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			CustomerUser customerUser = pm.getObjectById(CustomerUser.class, key);
			Customer customer = pm.getObjectById(Customer.class, key.getParent());
			tx.begin();
			customerUser.setCustomerUserName(customerUserName);
			customerUser.setCustomerUserDescription(customerUserDescription);
			customerUser.setCustomerUserGender(customerUserGender);
			customerUser.setCustomerUserTitle(customerUserTitle);
			customerUser.setDepartment(department);
			customerUser.setCustomerUserPhoneNumber(customerUserPhoneNumber);
			customerUser.setCustomerUserComments(customerUserComments);
			customer.updateCustomerUserVersion();
			tx.commit();
			log.info("CustomerUser \"" + customerUser.getUser().getUserEmail().getEmail() + 
					"\"'s attributes updated in datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
}
