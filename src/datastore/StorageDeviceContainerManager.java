/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.Key;

import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the StorageDeviceContainer class.
 * 
 */

public class StorageDeviceContainerManager {
	
	private static final Logger log = 
        Logger.getLogger(StorageDeviceContainerManager.class.getName());
	
	/**
     * Get a StorageDeviceContainer instance from the datastore given the StorageDeviceContainer key.
     * @param key
     * 			: the StorageDeviceContainer's key
     * @return StorageDeviceContainer instance, null if StorageDeviceContainer is not found
     */
	public static StorageDeviceContainer getStorageDeviceContainer(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		StorageDeviceContainer storageDeviceContainer;
		try  {
			storageDeviceContainer = pm.getObjectById(StorageDeviceContainer.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return storageDeviceContainer;
	}
	
	/**
     * Get all StorageDeviceContainer instances from the datastore that belong to
     * this Customer.
     * @param customerKey
     * @return All StorageDeviceContainer instances that belong to the given Customer
     * TODO: Inefficient touching of objects
     */
	public static List<StorageDeviceContainer> getAllStorageDeviceContainersFromCustomer(
			Key customerKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        Customer customer = 
        		pm.getObjectById(Customer.class, customerKey);
         
        List<StorageDeviceContainer> result = null;
        ArrayList<StorageDeviceContainer> finalResult = new ArrayList<StorageDeviceContainer>();
        try {
            result = customer.getStorageDeviceContainers();
            for (StorageDeviceContainer storageDeviceContainer : result) {
            	storageDeviceContainer.getStorageDeviceContainerComments();
            	finalResult.add(storageDeviceContainer);
            }
        }
        finally {
            pm.close();
        }

        return finalResult;
    }
	
    /**
     * Put StorageDeviceContainer into datastore.
     * Stores the given StorageDeviceContainer instance in the datastore for this
     * Customer.
     * @param customerKey
     *          : the key of the Customer where the storageDeviceContainer will be added
     * @param storageDeviceContainer
     *          : the StorageDeviceContainer instance to storageDeviceContainer
     */
    public static void putStorageDeviceContainer(Key customerKey, 
    		StorageDeviceContainer storageDeviceContainer) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            Customer customer = 
                    pm.getObjectById(Customer.class, customerKey);
            tx.begin();
            customer.addStorageDeviceContainer(storageDeviceContainer);
            customer.updateStorageDeviceContainerVersion();
            tx.commit();
            log.info("StorageDeviceContainer \"" + 
            		storageDeviceContainer.getStorageDeviceContainerLabel() + 
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
    * Delete StorageDeviceContainer from datastore.
    * Deletes the StorageDeviceContainer corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the StorageDeviceContainer instance to delete
    */
    public static void deleteStorageDeviceContainer(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            Customer customer = pm.getObjectById(Customer.class, key.getParent());
            StorageDeviceContainer storageDeviceContainer = pm.getObjectById(StorageDeviceContainer.class, key);
            String storageDeviceContainerContent = storageDeviceContainer.getStorageDeviceContainerLabel();
            tx.begin();
            customer.removeStorageDeviceContainer(storageDeviceContainer);
            customer.updateStorageDeviceContainerVersion();
            tx.commit();
            log.info("StorageDeviceContainer \"" + storageDeviceContainerContent + 
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
    * Update StorageDeviceContainer attributes.
    * Update's the given StorageDeviceContainer's attributes in the datastore.
    * @param key
    * 			: the key of the StorageDeviceContainer whose attributes will be updated
    * @param storageDevicePartition
    * 			: storageDevicePartition key
    * @param storageDeviceContainerParent
    * 			: storageDeviceContainer parent key
    * @param storageDeviceContainerModel
    * 			: storageDeviceContainerModel model key
    * @param storageDeviceContainerSerialNumber
    * 			: storageDeviceContainer number
    * @param storageDeviceContainerLabel
    * 			: storageDeviceContainer label
    * @param storageDeviceContainerDescription
    * 			: storageDeviceContainer description
    * @param storageDeviceContainerManufacturedDate
    * 			: storageDeviceContainer manufacturedDate
    * @param storageDeviceContainerShippingDate
    * 			: storageDeviceContainer shippingDate
    * @param storageDeviceContainerComments
    * 			: storageDeviceContainer comments
	* @throws MissingRequiredFieldsException 
    */
	public static void updateStorageDeviceContainerAttributes(
			Key key,
			Key storageDevicePartition,
    		Key storageDeviceContainerParent,
    		Long storageDeviceContainerModel, 
    		String storageDeviceContainerSerialNumber,
    		String storageDeviceContainerLabel,
    		String storageDeviceContainerDescription, 
    		Date storageDeviceContainerManufacturedDate, 
    		Date storageDeviceContainerShippingDate,
    		String storageDeviceContainerComments) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
        Customer customer = pm.getObjectById(Customer.class, key.getParent());
		
		Transaction tx = pm.currentTransaction();
		try {
			StorageDeviceContainer storageDeviceContainer = pm.getObjectById(StorageDeviceContainer.class, key);
			tx.begin();
			storageDeviceContainer.setStorageDevicePartition(
					storageDevicePartition);
			storageDeviceContainer.setStorageDeviceContainerParent(
					storageDeviceContainerParent);
			storageDeviceContainer.setStorageDeviceContainerSerialNumber(
					storageDeviceContainerSerialNumber);
			storageDeviceContainer.setStorageDeviceContainerLabel(
					storageDeviceContainerLabel);
			storageDeviceContainer.setStorageDeviceContainerDescription(
					storageDeviceContainerDescription);
			storageDeviceContainer.setStorageDeviceContainerManufacturedDate(
					storageDeviceContainerManufacturedDate);
			storageDeviceContainer.setStorageDeviceContainerShippingDate(
					storageDeviceContainerShippingDate);
			storageDeviceContainer.setStorageDeviceContainerComments(
					storageDeviceContainerComments);
			
			customer.updateStorageDeviceContainerVersion();
			tx.commit();
			log.info("StorageDeviceContainer \"" + storageDeviceContainerLabel + 
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
