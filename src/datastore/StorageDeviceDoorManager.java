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
 * made on the StorageDeviceDoor class.
 * 
 */

public class StorageDeviceDoorManager {
	
	private static final Logger log = 
        Logger.getLogger(StorageDeviceDoorManager.class.getName());
	
	/**
     * Get a StorageDeviceDoor instance from the datastore given the StorageDeviceDoor key.
     * @param key
     * 			: the StorageDeviceDoor's key
     * @return StorageDeviceDoor instance, null if StorageDeviceDoor is not found
     */
	public static StorageDeviceDoor getStorageDeviceDoor(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		StorageDeviceDoor storageDeviceDoor;
		try  {
			storageDeviceDoor = pm.getObjectById(StorageDeviceDoor.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return storageDeviceDoor;
	}
	
	/**
     * Get all StorageDeviceDoor instances from the datastore that belong to
     * this StorageDevice.
     * @param storageDeviceKey
     * @return All StorageDeviceDoor instances that belong to the given StorageDevice
     * TODO: Inefficient touching of objects
     */
	public static List<StorageDeviceDoor> getAllStorageDeviceDoorsFromStorageDevice(
			Key storageDeviceKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        StorageDevice storageDevice = pm.getObjectById(StorageDevice.class, storageDeviceKey);
         
        List<StorageDeviceDoor> result = null;
        ArrayList<StorageDeviceDoor> finalResult = new ArrayList<StorageDeviceDoor>();
        try {
            result = storageDevice.getStorageDeviceDoors();
            for (StorageDeviceDoor storageDeviceDoor : result) {
            	storageDeviceDoor.getKey();
            	finalResult.add(storageDeviceDoor);
            }
        }
        finally {
            pm.close();
        }

        return finalResult;
    }
	
    /**
     * Put StorageDeviceDoor into datastore.
     * Stores the given StorageDeviceDoor instance in the datastore for this
     * storageDevice.
     * @param storageDeviceKey
     *          : the key of the StorageDevice where the storageDeviceDoor will be added
     * @param storageDeviceDoor
     *          : the StorageDeviceDoor instance to storageDevice
     */
    public static void putStorageDeviceDoor(Key storageDeviceKey, 
    		StorageDeviceDoor storageDeviceDoor) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            StorageDevice storageDevice = 
                    pm.getObjectById(StorageDevice.class, storageDeviceKey);
            tx.begin();
            storageDevice.addStorageDeviceDoor(storageDeviceDoor);
            tx.commit();
            log.info("StorageDeviceDoor \"" + storageDeviceDoor.getKey() + 
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
    * Delete StorageDeviceDoor from datastore.
    * Deletes the StorageDeviceDoor corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the StorageDeviceDoor instance to delete
    */
    public static void deleteStorageDeviceDoor(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            StorageDevice storageDevice = 
            		pm.getObjectById(StorageDevice.class, key.getParent());
            StorageDeviceDoor storageDeviceDoor = 
            		pm.getObjectById(StorageDeviceDoor.class, key);
            Key storageDeviceDoorContent = 
            		storageDeviceDoor.getKey();
            tx.begin();
            storageDevice.removeStorageDeviceDoor(storageDeviceDoor);
            tx.commit();
            log.info("StorageDeviceDoor \"" + 
            		KeyFactory.keyToString(storageDeviceDoorContent) + 
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
    * Update StorageDeviceDoor attributes.
    * Update's the given StorageDeviceDoor's attributes in the datastore.
    * @param key
    * 			: the key of the StorageDeviceDoor whose attributes will be updated
    * @param deviceModelDoor
    * 			: the DeviceModelDoor key
    * @param storageDeviceDoorComments
    * 			: the new comments to give to the StorageDeviceDoor
	* @throws MissingRequiredFieldsException 
    */
	public static void updateStorageDeviceDoorAttributes(
			Key key,
			Key deviceModelDoor,
			String storageDeviceDoorComments) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			StorageDeviceDoor storageDeviceDoor = 
					pm.getObjectById(StorageDeviceDoor.class, key);
			tx.begin();
			storageDeviceDoor.setDeviceModelDoor(deviceModelDoor);
			storageDeviceDoor.setStorageDeviceDoorComments(storageDeviceDoorComments);
			tx.commit();
			log.info("StorageDeviceDoor \"" + 
					KeyFactory.keyToString(storageDeviceDoor.getKey()) + 
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
    * Open or close StorageDeviceDoor.
    * Open or close the given StorageDeviceDoor in the datastore.
    * @param key
    * 			: the key of the StorageDeviceDoor whose attributes will be updated
    * @param open
    * 			: True if opening door, False otherwise
	* @throws MissingRequiredFieldsException 
    */
	public static void openOrCloseStorageDeviceDoor(
			Key key,
			boolean open) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			StorageDeviceDoor storageDeviceDoor = 
					pm.getObjectById(StorageDeviceDoor.class, key);
			tx.begin();
			if (open) {
				storageDeviceDoor.openStorageDeviceDoor();
			}
			else {
				storageDeviceDoor.closeStorageDeviceDoor();
			}
			tx.commit();
			log.info("StorageDeviceDoor \"" + 
					KeyFactory.keyToString(storageDeviceDoor.getKey()) + 
                    (open ? " opened" : " closed") + " in datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
}
