/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the StorageDevicePartition class.
 * 
 */

public class StorageDevicePartitionManager {
	
	private static final Logger log = 
        Logger.getLogger(StorageDevicePartitionManager.class.getName());
	
	/**
     * Get a StorageDevicePartition instance from the datastore given the StorageDevicePartition key.
     * @param key
     * 			: the StorageDevicePartition's key
     * @return StorageDevicePartition instance, null if StorageDevicePartition is not found
     */
	public static StorageDevicePartition getStorageDevicePartition(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		StorageDevicePartition storageDevicePartition;
		try  {
			storageDevicePartition = pm.getObjectById(StorageDevicePartition.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return storageDevicePartition;
	}
	
	/**
     * Get all StorageDevicePartition instances from the datastore that belong to
     * this StorageDeviceDoor.
     * @param storageDeviceDoorKey
     * @return All StorageDevicePartition instances that belong to the given StorageDeviceDoor
     * TODO: Inefficient touching of objects
     */
	public static List<StorageDevicePartition> getAllStorageDevicePartitionsFromStorageDeviceDoor(
			Key storageDeviceDoorKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        StorageDeviceDoor storageDeviceDoor = 
        		pm.getObjectById(StorageDeviceDoor.class, storageDeviceDoorKey);
         
        List<StorageDevicePartition> result = null;
        ArrayList<StorageDevicePartition> finalResult = new ArrayList<StorageDevicePartition>();
        try {
            result = storageDeviceDoor.getStorageDevicePartitions();
            for (StorageDevicePartition storageDevicePartition : result) {
            	storageDevicePartition.getKey();
            	finalResult.add(storageDevicePartition);
            }
        }
        finally {
            pm.close();
        }

        return finalResult;
    }
	
    /**
     * Put StorageDevicePartition into datastore.
     * Stores the given StorageDevicePartition instance in the datastore for this
     * storageDeviceDoor.
     * @param storageDeviceDoorKey
     *          : the key of the StorageDeviceDoor where the storageDevicePartition will be added
     * @param storageDevicePartition
     *          : the StorageDevicePartition instance to deviceModel
     */
    public static void putStorageDevicePartition(Key storageDeviceDoorKey, 
    		StorageDevicePartition storageDevicePartition) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            StorageDeviceDoor storageDeviceDoor = 
                    pm.getObjectById(StorageDeviceDoor.class, storageDeviceDoorKey);
            tx.begin();
            storageDeviceDoor.addStorageDevicePartition(storageDevicePartition);
            tx.commit();
            log.info("StorageDevicePartition \"" + 
            		KeyFactory.keyToString(storageDevicePartition.getKey()) + 
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
    * Delete StorageDevicePartition from datastore.
    * Deletes the StorageDevicePartition corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the StorageDevicePartition instance to delete
    */
    public static void deleteStorageDevicePartition(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            StorageDeviceDoor storageDeviceDoor = 
            		pm.getObjectById(StorageDeviceDoor.class, key.getParent());
            StorageDevicePartition storageDevicePartition = 
            		pm.getObjectById(StorageDevicePartition.class, key);
            String storageDevicePartitionContent = 
            		KeyFactory.keyToString(storageDevicePartition.getKey());
            tx.begin();
            storageDeviceDoor.removeStorageDevicePartition(storageDevicePartition);
            tx.commit();
            log.info("StorageDevicePartition \"" + storageDevicePartitionContent + 
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
    * Update StorageDevicePartition attributes.
    * Update's the given StorageDevicePartition's attributes in the datastore.
    * @param key
    * 			: the key of the StorageDevicePartition whose attributes will be updated
    * @param deviceModelPartition
    * 			: the DeviceModelPartition key
    * @param storageDevicePartitionComments
    * 			: the new comments to give to the StorageDevicePartition
	* @throws MissingRequiredFieldsException 
    */
	public static void updateStorageDevicePartitionAttributes(
			Key key,
			Key deviceModelPartition,
			String storageDevicePartitionComments) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			StorageDevicePartition storageDevicePartition = 
					pm.getObjectById(StorageDevicePartition.class, key);
			tx.begin();
			storageDevicePartition.setDeviceModelPartition(deviceModelPartition);
			storageDevicePartition.setStorageDevicePartitionComments(
					storageDevicePartitionComments);
			tx.commit();
			log.info("StorageDevicePartition \"" + KeyFactory.keyToString(key) + 
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
