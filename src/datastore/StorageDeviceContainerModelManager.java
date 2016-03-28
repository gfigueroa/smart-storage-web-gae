/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.logging.Logger;
import java.util.Date;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.jdo.Query;

import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the StorageDeviceContainerModel class.
 * 
 */

public class StorageDeviceContainerModelManager {
	
	private static final Logger log = 
        Logger.getLogger(StorageDeviceContainerModelManager.class.getName());
	
	/**
     * Get a StorageDeviceContainerModel instance from the datastore given the StorageDeviceContainerModel key.
     * @param key
     * 			: the StorageDeviceContainerModel's key
     * @return StorageDeviceContainerModel instance, null if StorageDeviceContainerModel is not found
     */
	public static StorageDeviceContainerModel getStorageDeviceContainerModel(Long key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		StorageDeviceContainerModel storageDeviceContainerModel;
		try  {
			storageDeviceContainerModel = pm.getObjectById(StorageDeviceContainerModel.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return storageDeviceContainerModel;
	}
	
	/**
     * Get all StorageDeviceContainerModel instances from the datastore.
     * @return All StorageDeviceContainerModel instances
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static List<StorageDeviceContainerModel> getAllStorageDeviceContainerModels() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(StorageDeviceContainerModel.class);

        List<StorageDeviceContainerModel> types = null;
        try {
        	types = (List<StorageDeviceContainerModel>) query.execute();
            // touch all elements
            for (StorageDeviceContainerModel t : types)
                t.getStorageDeviceContainerModelName();
        } 
        finally {
        	pm.close();
            query.closeAll();
        }

        return types;
    }
	
	/**
     * Put StorageDeviceContainerModel into datastore.
     * Stations the given StorageDeviceContainerModel instance in the datastore calling the 
     * PersistenceManager's makePersistent() method.
     * @param storageDeviceContainerModel
     * 			: the StorageDeviceContainerModel instance to store
     */
	public static void putStorageDeviceContainerModel(
			StorageDeviceContainerModel storageDeviceContainerModel) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistent(storageDeviceContainerModel);
			tx.commit();
			log.info("StorageDeviceContainerModel \"" + 
					storageDeviceContainerModel.getStorageDeviceContainerModelName() + 
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
    * Delete StorageDeviceContainerModel from datastore.
    * Deletes the StorageDeviceContainerModel corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    * 			: the key of the StorageDeviceContainerModel instance to delete
    */
	public static void deleteStorageDeviceContainerModel(Long key) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			StorageDeviceContainerModel storageDeviceContainerModel = 
					pm.getObjectById(StorageDeviceContainerModel.class, key);
			String StorageDeviceContainerModelName = 
					storageDeviceContainerModel.getStorageDeviceContainerModelName();
			tx.begin();
			pm.deletePersistent(storageDeviceContainerModel);
			tx.commit();
			log.info("StorageDeviceContainerModel \"" + StorageDeviceContainerModelName + 
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
    * Update StorageDeviceContainerModel attributes.
    * Update's the given StorageDeviceContainerModel's attributes in the datastore.
    * @param key
    * 			: the key of the StorageDeviceContainerModel whose attributes will be updated
    * @param storageDeviceContainerModelName
    * 			: the new name to give to the StorageDeviceContainerModel
    * @param storageDeviceContainerModelDescription
    * 			: the new description to give to the StorageDeviceContainerModel
    * @param storageDeviceContainerModelVersionNumber
    * 			: the new version number to give to the storageDeviceContainerModel
    * @param storageDeviceContainerModelDesignTime
    * 			: the new design time to give to the storageDeviceContainerModel
    * @param storageDeviceContainerModelComments
    * 			: the new comments to give to the StorageDeviceContainerModel
	* @throws MissingRequiredFieldsException 
    */
	public static void updateStorageDeviceContainerModelAttributes(
			Long key,
			String storageDeviceContainerModelName, 
			String storageDeviceContainerModelDescription,
			String storageDeviceContainerModelVersionNumber, 
			Date storageDeviceContainerModelDesignTime,
			String storageDeviceContainerModelComments) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			StorageDeviceContainerModel storageDeviceContainerModel = 
					pm.getObjectById(StorageDeviceContainerModel.class, key);
			tx.begin();
			storageDeviceContainerModel.setStorageDeviceContainerModelName(
					storageDeviceContainerModelName);
			storageDeviceContainerModel.setStorageDeviceContainerModelDescription(
					storageDeviceContainerModelDescription);
			storageDeviceContainerModel.setStorageDeviceContainerModelVersionNumber(
					storageDeviceContainerModelVersionNumber);
			storageDeviceContainerModel.setStorageDeviceContainerModelDesignTime(
					storageDeviceContainerModelDesignTime);
			storageDeviceContainerModel.setStorageDeviceContainerModelComments(
					storageDeviceContainerModelComments);
			tx.commit();
			log.info("StorageDeviceContainerModel \"" + storageDeviceContainerModelName + 
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
