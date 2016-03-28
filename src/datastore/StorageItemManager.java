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

import com.google.appengine.api.datastore.Key;

import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the StorageItem class.
 * 
 */

public class StorageItemManager {
	
	private static final Logger log = 
        Logger.getLogger(StorageItemManager.class.getName());
	
	/**
     * Get a StorageItem instance from the datastore given the StorageItem key.
     * @param key
     * 			: the StorageItem's key
     * @return StorageItem instance, null if StorageItem is not found
     */
	public static StorageItem getStorageItem(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		StorageItem storageItem;
		try  {
			storageItem = pm.getObjectById(StorageItem.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return storageItem;
	}
	
	/**
     * Get all StorageItem instances from the datastore that belong to
     * this Customer.
     * @param customerKey
     * @return All StorageItem instances that belong to the given Customer
     * TODO: Inefficient touching of objects
     */
	public static List<StorageItem> getAllStorageItemsFromCustomer(
			Key customerKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        Customer customer = 
        		pm.getObjectById(Customer.class, customerKey);
         
        List<StorageItem> result = null;
        ArrayList<StorageItem> finalResult = new ArrayList<StorageItem>();
        try {
            result = customer.getStorageItems();
            for (StorageItem storageItem : result) {
            	storageItem.getStorageItemComments();
            	finalResult.add(storageItem);
            }
        }
        finally {
            pm.close();
        }

        return finalResult;
    }
	
	/**
     * Search for StorageItems given the search string. The search works
     * with partial matching, and is case insensitive. It can be filtered
     * down to a specific Customer's StorageItems.
     * The search can be done according to any of the following fields:
     * storageItemPartNumber, storageItemName, storageItemDescription
     * @param customerKey (can be null)
     * @param searchString
     * @return All StorageItem instances that belong to the given Customer
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static List<StorageItem> searchStorageItems(
			Key customerKey, String searchString) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
         
        List<StorageItem> result = null;
        ArrayList<StorageItem> finalResult = new ArrayList<StorageItem>();
        try {
        	Query query = pm.newQuery(StorageItem.class);
        	query.setOrdering("storageItemName asc");
        	
        	if (customerKey != null) {
        		query.setFilter("customerKey == customerKeyParam && " +
        				"storageItemName == searchStringParam");
                query.declareParameters(Key.class.getName() + " customerKeyParam, " +
                		"String searchStringParam");
                result = (List<StorageItem>) query.execute(customerKey, searchString);
        	}
        	else {
        		query.setFilter("storageItemName.toLowerCase().matches(searchStringParam)");
                query.declareParameters("String searchStringParam");
                result = (List<StorageItem>) query.execute(searchString);
        	}
            
        	for (StorageItem storageItem : result) {
        		finalResult.add(storageItem);
        	}
        }
        finally {
            pm.close();
        }

        return finalResult;
    }
	
    /**
     * Put StorageItem into datastore.
     * Stores the given StorageItem instance in the datastore for this
     * Customer.
     * @param customerKey
     *          : the key of the Customer where the storageItem will be added
     * @param storageItem
     *          : the StorageItem instance to storageItem
     */
    public static void putStorageItem(Key customerKey, 
    		StorageItem storageItem) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            Customer customer = 
                    pm.getObjectById(Customer.class, customerKey);
            tx.begin();
            customer.addStorageItem(storageItem);
            customer.updateStorageItemVersion();
            tx.commit();
            log.info("StorageItem \"" + storageItem.getStorageItemPartNumber() + 
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
    * Delete StorageItem from datastore.
    * Deletes the StorageItem corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the StorageItem instance to delete
    */
    public static void deleteStorageItem(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            Customer customer = pm.getObjectById(Customer.class, key.getParent());
            StorageItem storageItem = pm.getObjectById(StorageItem.class, key);
            String storageItemContent = storageItem.getStorageItemPartNumber();
            tx.begin();
            customer.removeStorageItem(storageItem);
            customer.updateStorageItemVersion();
            tx.commit();
            log.info("StorageItem \"" + storageItemContent + 
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
    * Update StorageItem attributes.
    * Update's the given StorageItem's attributes in the datastore.
    * @param key
    * 			: the key of the StorageItem whose attributes will be updated
    * @param storageItemPartNumber
    * 			: storageItem part number
    * @param storageItemName
    * 			: storageItem name
    * @param storageItemDescription
    * 			: storageItem description
    * @param storageItemMSDLevel
    * 			: storageItem MSDLevel
    * @param storageItemComments
    * 			: storageItem comments
	* @throws MissingRequiredFieldsException 
    */
	public static void updateStorageItemAttributes(
			Key key,
    		String storageItemPartNumber,
    		String storageItemName, 
    		String storageItemDescription, 
    		Integer storageItemMSDLevel,
    		String storageItemComments) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
        Customer customer = pm.getObjectById(Customer.class, key.getParent());
		
		Transaction tx = pm.currentTransaction();
		try {
			StorageItem storageItem = pm.getObjectById(StorageItem.class, key);
			tx.begin();
			storageItem.setStorageItemPartNumber(storageItemPartNumber);
			storageItem.setStorageItemName(storageItemName);
			storageItem.setStorageItemDescription(storageItemDescription);
			storageItem.setStorageItemMSDLevel(storageItemMSDLevel);
			storageItem.setStorageItemComments(storageItemComments);
			
			customer.updateStorageItemVersion();
			tx.commit();
			log.info("StorageItem \"" + storageItemName + 
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
