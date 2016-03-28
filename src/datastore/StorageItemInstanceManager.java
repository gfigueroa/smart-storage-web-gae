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

import datastore.StorageItemInstance.IndicationLightValue;

import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the StorageItemInstance class.
 * 
 */

public class StorageItemInstanceManager {
	
	private static final Logger log = 
        Logger.getLogger(StorageItemInstanceManager.class.getName());
	
	/**
     * Get a StorageItemInstance instance from the datastore given the StorageItemInstance key.
     * @param key
     * 			: the StorageItemInstance's key
     * @return StorageItemInstance instance, null if StorageItemInstance is not found
     */
	public static StorageItemInstance getStorageItemInstance(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		StorageItemInstance storageItemInstance;
		try  {
			storageItemInstance = pm.getObjectById(StorageItemInstance.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return storageItemInstance;
	}
	
	/**
     * Get all StorageItemInstance instances from the datastore that belong to
     * this StorageItem.
     * @param storageItemKey
     * @return All StorageItemInstance instances that belong to the given StorageItem
     * TODO: Inefficient touching of objects
     */
	public static List<StorageItemInstance> getAllStorageItemInstancesFromStorageItem(
			Key storageItemKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        StorageItem storageItem = 
        		pm.getObjectById(StorageItem.class, storageItemKey);
         
        List<StorageItemInstance> result = null;
        ArrayList<StorageItemInstance> finalResult = new ArrayList<StorageItemInstance>();
        try {
            result = storageItem.getStorageItemInstances();
            for (StorageItemInstance storageItemInstance : result) {
            	storageItemInstance.getStorageItemInstanceComments();
            	finalResult.add(storageItemInstance);
            }
        }
        finally {
            pm.close();
        }

        return finalResult;
    }
	
	/**
     * Get all StorageItemInstance instances from the datastore that are inside
     * this StorageDevice
     * @param storageDeviceKey
     * @return All StorageItemInstance instances that are inside the given StorageDevice
     * TODO: Inefficient touching of objects
     */
	public static List<StorageItemInstance> getStorageItemInstancesInStorageDevice(
			Key storageDeviceKey) {
		
		return null;
    }
	
	/**
     * Get all StorageItemInstance instances from the datastore that belong
     * to this StorageItemUser
     * @param storageItemUserKey
     * @return All StorageItemInstance instances that belong to the given StorageItemUser
     * TODO: Inefficient touching of objects
     */
	public static List<StorageItemInstance> getStorageItemInstancesFromStorageItemUser(
			Key storageItemUserKey) {
		
		return null;
    }
	
    /**
     * Put StorageItemInstance into datastore.
     * Stores the given StorageItemInstance instance in the datastore for this
     * StorageItem.
     * @param storageItemKey
     *          : the key of the StorageItem where the storageItemInstance will be added
     * @param storageItemInstance
     *          : the StorageItemInstance instance to storageItemInstance
     */
    public static void putStorageItemInstance(Key storageItemKey, 
    		StorageItemInstance storageItemInstance) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            StorageItem storageItem = 
                    pm.getObjectById(StorageItem.class, storageItemKey);
            tx.begin();
            storageItem.addStorageItemInstance(storageItemInstance);
            tx.commit();
            log.info("StorageItemInstance \"" + 
            		storageItemInstance.getStorageItemInstanceSerialNumber() + 
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
    * Delete StorageItemInstance from datastore.
    * Deletes the StorageItemInstance corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the StorageItemInstance instance to delete
    */
    public static void deleteStorageItemInstance(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            StorageItem storageItem = pm.getObjectById(StorageItem.class, key.getParent());
            StorageItemInstance storageItemInstance = 
            		pm.getObjectById(StorageItemInstance.class, key);
            String storageItemInstanceContent = 
            		storageItemInstance.getStorageItemInstanceSerialNumber();
            tx.begin();
            storageItem.removeStorageItemInstance(storageItemInstance);
            tx.commit();
            log.info("StorageItemInstance \"" + storageItemInstanceContent + 
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
    * Update StorageItemInstance attributes.
    * Update's the given StorageItemInstance's attributes in the datastore.
    * @param key
    * 			: the key of the StorageItemInstance whose attributes will be updated
    * @param storageItemInstanceOwner
    * 			: storageItemInstanceOwner model key
    * @param storageItemInstanceSerialNumber
    * 			: storageItemInstance number
    * @param storageItemInstanceLabel
    * 			: storageItemInstance label
    * @param storageItemInstanceExpirationTime
    * 			: storageItemInstance shippingDate
    * @param storageItemInstanceWorksheet
    * 			: storageItemInstance worksheet
    * @param storageItemInstanceIndicationLight
    * 			: storageItemInstance indicationLight
    * @param storageItemInstanceComments
    * 			: storageItemInstance comments
	* @throws MissingRequiredFieldsException 
    */
	public static void updateStorageItemInstanceAttributes(
			Key key,
			Key storageItemInstanceOwner, 
    		String storageItemInstanceSerialNumber,
    		String storageItemInstanceLabel, 
    		Date storageItemInstanceExpirationTime,
    		String storageItemInstanceWorksheet,
    		IndicationLightValue storageItemInstanceIndicationLight,
    		String storageItemInstanceComments) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			StorageItemInstance storageItemInstance = 
					pm.getObjectById(StorageItemInstance.class, key);
			tx.begin();
			storageItemInstance.setStorageItemInstanceOwner(
					storageItemInstanceOwner);
			storageItemInstance.setStorageItemInstanceSerialNumber(
					storageItemInstanceSerialNumber);
			storageItemInstance.setStorageItemInstanceLabel(
					storageItemInstanceLabel);
			storageItemInstance.setStorageItemInstanceExpirationTime(
					storageItemInstanceExpirationTime);
			storageItemInstance.setStorageItemInstanceWorksheet(
					storageItemInstanceWorksheet);
			storageItemInstance.setStorageItemInstanceIndicationLight(
					storageItemInstanceIndicationLight);
			storageItemInstance.setStorageItemInstanceComments(
					storageItemInstanceComments);
			tx.commit();
			log.info("StorageItemInstance \"" + storageItemInstanceLabel + 
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
    * Update StorageItemInstance lastStorageItemTransaction.
    * Update's the given StorageItemInstance's lastStorageItemTransaction in the datastore.
    * @param key
    * 			: the key of the StorageItemInstance whose attributes will be updated
    * @param lastStorageItemTransaction
    * 			: the key of the last StorageItemTransaction
    */
	public static void updateLastStorageItemTransaction(
			Key key,
			Key lastStorageItemTransactionKey) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			StorageItemInstance storageItemInstance = 
					pm.getObjectById(StorageItemInstance.class, key);
			tx.begin();
			storageItemInstance.setLastStorageItemTransaction(
					lastStorageItemTransactionKey);
			tx.commit();
			log.info("StorageItemInstance \"" + 
					storageItemInstance.getStorageItemInstanceLabel() + 
                     "\"'s lastStorageItemTransaction updated in datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
}
