/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;

import datastore.Customer.Status;
import exceptions.MissingRequiredFieldsException;
import exceptions.ObjectExistsInDatastoreException;
import exceptions.UserValidationException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the Customer class.
 * 
 */

public class CustomerManager {
	
    private static final BlobstoreService blobstoreService = 
        	BlobstoreServiceFactory.getBlobstoreService();
	private static final Logger log = 
	        Logger.getLogger(CustomerManager.class.getName());

	/**
     * Get a Customer instance from the datastore given its user entity.
     * The method uses the user's email field to obtain the Customer key.
     * @param user
     * 			: the user belonging to this Customer
     * @return Customer instance, null if Customer is not found
     */
	public static Customer getCustomer(User user) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key key = KeyFactory.createKey(Customer.class.getSimpleName(), 
                                       user.getUserEmail().getEmail());
		Customer customer;
		try  {
			customer = pm.getObjectById(Customer.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return customer;
	}
	
	/**
     * Get a Customer instance from the datastore given the user's email.
     * The method uses this email to obtain the Customer key.
     * @param email
     * 			: the Customer's email address
     * @return Customer instance, null if Customer is not found
     */
	public static Customer getCustomer(Email email) {		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key key = KeyFactory.createKey(Customer.class.getSimpleName(), 
                                       email.getEmail());
		
		Customer customer;
		try  {
			customer = pm.getObjectById(Customer.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return customer;
	}
	
	/**
     * Get a Customer instance from the datastore given the Customer key.
     * @param key
     * 			: the Customer's key
     * @return Customer instance, null if Customer is not found
     */
	public static Customer getCustomer(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Customer customer;
		try  {
			customer = pm.getObjectById(Customer.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return customer;
	}
	
	/**
     * Get ALL the Customers in the database and return them
     * in a List structure
     * @return all customers in the datastore
     * TODO: Make more efficient "touching" of the users
     */
    @SuppressWarnings("unchecked")
	public static List<Customer> getAllCustomers() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(Customer.class);

        try {
        	List<Customer> customers = (List<Customer>) query.execute();
        	// Touch the user to keep in memory
        	for (Customer customer : customers) {
        		customer.getUser();
        	}
        	return customers;
            //return (List<Customer>) query.execute();
        } finally {
        	pm.close();
            query.closeAll();
        }
    }

	/**
     * Put Customer into datastore.
     * Customers the given Customer instance in the datastore calling the 
     * PersistenceManager's makePersistent() method.
     * @param CustomerSimple
     * 			: the Customer instance to customer
     * @throws ObjectExistsInDatastoreException
     */
	public static void putCustomer(Customer customer) 
           throws ObjectExistsInDatastoreException {
		
		// Check if the user already exists in the datastore
		Email email = customer.getUser().getUserEmail();
		if (UserManager.userExists(email)) {
			throw new ObjectExistsInDatastoreException(customer, "User \"" + 
					customer.getUser().getUserEmail().getEmail() + 
					"\" already exists in the datastore.");
		}
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistent(customer);
			tx.commit();
			log.info("Customer \"" + 
					customer.getUser().getUserEmail().getEmail() + 
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
    * Delete Customer from datastore.
    * Deletes the given Customer from the datastore calling the PersistenceManager's
    * deletePersistent() method.
    * @param CustomerSimple
    * 			: the Customer instance to delete
    */
	public static void deleteCustomer(Customer customer) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			customer = pm.getObjectById(Customer.class, 
					customer.getKey());
			String email = customer.getUser().getUserEmail().getEmail();
			BlobKey customerLogo = customer.getCustomerLogo();
			
			tx.begin();
			pm.deletePersistent(customer);
			tx.commit();
			
			// Delete blob
			if (customerLogo != null) {
				blobstoreService.delete(customerLogo);
			}
			
			log.info("Customer \"" + email +
					"\" deleted successfully from datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Update Customer password in datastore.
    * Update's the Customer's password in the datastore.
    * @param key
    * 			: the key of the Customer whose password will be changed
    * @param currentPassword
    * 			: the current password of this Customer
    * @param newPassword
    * 			: the new password for this Customer
    * @throws UserValidationException
	* @throws MissingRequiredFieldsException 
    */
	public static void updateCustomerPassword(Key key, 
			String currentPassword, String newPassword) 
			throws UserValidationException, MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			Customer customer = pm.getObjectById(Customer.class, key);
			tx.begin();
			if (customer.getUser().validateUser(customer.getUser().getUserEmail(), 
					currentPassword)) {
				customer.getUser().setUserPassword(newPassword);
				tx.commit();
				log.info("Customer \"" + customer.getCustomerName() + 
						"\"'s password updated in datastore.");
			}
			else {
				tx.rollback();
				throw new UserValidationException(customer.getUser(), 
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
    * Update Customer password in datastore.
    * Update's the Customer's password in the datastore.
    * @param key
    * 			: the key of the Customer whose password will be changed
    * @param newPassword
    * 			: the new password for this Customer
	* @throws MissingRequiredFieldsException 
    */
	public static void updateCustomerPassword(Key key, String newPassword) 
			throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			Customer customer = pm.getObjectById(Customer.class, key);
			tx.begin();
			customer.getUser().setUserPassword(newPassword);
			tx.commit();
			log.info("Customer \"" + customer.getCustomerName() + 
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
    * Update Customer attributes.
    * Update's the given Customer's attributes in the datastore.
    * @param key
    * 			: the key of the Customer whose attributes will be updated
    * @param customerName
    * 			: the new name to give to the Customer
    * @param customerDescription
    * 			: the new description to give to the Customer
    * @param customerPhoneNumber
    * 			: the new phone number to give to the Customer
    * @param region
    * 			: the new region of the Customer
    * @param customerAddress
    * 			: the new address of the Customer
    * @param customerWebsite
    * 			: Customer website
    * @param customerLogo
    * 			: Customer logo blob key
    * @param customerStatus
    * 			: Customer Status
    * @param customerComments
    * 			: the new comments to give to the Customer
	* @throws MissingRequiredFieldsException 
    */
	public static void updateCustomerAttributes(
			Key key,
            String customerName,
            String customerDescription,
            PhoneNumber customerPhoneNumber,
            Key region,
            PostalAddress customerAddress,
            Link customerWebsite, 
            BlobKey customerLogo,
            Status customerStatus,
            String customerComments) 
            		throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			Customer customer = pm.getObjectById(Customer.class, key);
			
			BlobKey oldCustomerLogo = customer.getCustomerLogo();
			
			tx.begin();
			customer.setCustomerName(customerName);
			customer.setCustomerDescription(customerDescription);
			customer.setCustomerPhoneNumber(customerPhoneNumber);
			customer.setRegion(region);
			customer.setCustomerAddress(customerAddress);
			customer.setCustomerWebsite(customerWebsite);
			customer.setCustomerLogo(customerLogo);
			customer.setCustomerStatus(customerStatus);
			customer.setCustomerComments(customerComments);
			tx.commit();
			
			if (oldCustomerLogo != null &&
					!oldCustomerLogo.equals(customerLogo)) {
				blobstoreService.delete(oldCustomerLogo);
			}
			
			log.info("Customer \"" + customer.getCustomerName() + 
					"\"'s attributes updated in datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Update Customer Status.
    * Updates the given Customer's Status in the datastore.
    * @param key
    * 			: the key of the Customer whose attributes will be updated
	 * @throws MissingRequiredFieldsException 
    */
	public static void updateCustomerStatus(Key key, Status customerStatus) 
			throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			Customer customer = pm.getObjectById(Customer.class, key);
			tx.begin();
			customer.setCustomerStatus(customerStatus);
			tx.commit();
			log.info("Customer \"" + customer.getUser().getUserEmail().getEmail() + 
					"\"'s Customer Status version updated in datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Update Device Model Version.
    * Updates the given Customer's Device Model Version by 1 in the datastore.
    * @param email
    * 			: the email of the Customer whose attributes will be updated
    */
	public static void updateDeviceModelVersion(Email email) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			Key key = KeyFactory.createKey(Customer.class.getSimpleName(), 
					email.getEmail());
			Customer customer = pm.getObjectById(Customer.class, key);
			tx.begin();
			customer.updateDeviceModelVersion();
			tx.commit();
			log.info("Customer \"" + email.getEmail() + 
					"\"'s DeviceModel version updated in datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Update Customer Storage Device Version.
    * Updates the given Customer's Storage Device Version by 1 in the datastore.
    * @param email
    * 			: the email of the Customer whose attributes will be updated
    */
	public static void updateStorageDeviceVersion(Email email) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			Key key = KeyFactory.createKey(Customer.class.getSimpleName(), 
					email.getEmail());
			Customer customer = pm.getObjectById(Customer.class, key);
			tx.begin();
			customer.updateStorageDeviceVersion();
			tx.commit();
			log.info("Customer \"" + email.getEmail() + 
					"\"'s StorageDevice version updated in datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Update Storage Device Container Model Version.
    * Updates the given Customer's Storage Device Container Model Version by 1 in the datastore.
    * @param email
    * 			: the email of the Customer whose attributes will be updated
    */
	public static void updateStorageDeviceContainerModelVersion(Email email) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			Key key = KeyFactory.createKey(Customer.class.getSimpleName(), 
					email.getEmail());
			Customer customer = pm.getObjectById(Customer.class, key);
			tx.begin();
			customer.updateStorageDeviceContainerModelVersion();
			tx.commit();
			log.info("Customer \"" + email.getEmail() + 
					"\"'s StorageDeviceContainerModel version updated in datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Update Customer Storage Device Container Version.
    * Updates the given Customer's Storage Device Container Version by 1 in the datastore.
    * @param email
    * 			: the email of the Customer whose attributes will be updated
    */
	public static void updateStorageDeviceContainerVersion(Email email) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			Key key = KeyFactory.createKey(Customer.class.getSimpleName(), 
					email.getEmail());
			Customer customer = pm.getObjectById(Customer.class, key);
			tx.begin();
			customer.updateStorageDeviceContainerVersion();
			tx.commit();
			log.info("Customer \"" + email.getEmail() + 
					"\"'s StorageDeviceContainer version updated in datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Update Customer Storage Item Version.
    * Updates the given Customer's Storage Item Version by 1 in the datastore.
    * @param email
    * 			: the email of the Customer whose attributes will be updated
    */
	public static void updateStorageItemVersion(Email email) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			Key key = KeyFactory.createKey(Customer.class.getSimpleName(), 
					email.getEmail());
			Customer customer = pm.getObjectById(Customer.class, key);
			tx.begin();
			customer.updateStorageItemVersion();
			tx.commit();
			log.info("Customer \"" + email.getEmail() + 
					"\"'s StorageItem version updated in datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Update Customer User Version.
    * Updates the given Customer's CustomerUser Version by 1 in the datastore.
    * @param email
    * 			: the email of the Customer whose attributes will be updated
    */
	public static void updateCustomerUserVersion(Email email) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			Key key = KeyFactory.createKey(Customer.class.getSimpleName(), 
					email.getEmail());
			Customer customer = pm.getObjectById(Customer.class, key);
			tx.begin();
			customer.updateCustomerUserVersion();
			tx.commit();
			log.info("Customer \"" + email.getEmail() + 
					"\"'s CustomerUser version updated in datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
}
