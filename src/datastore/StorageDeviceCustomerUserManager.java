/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.logging.Logger;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the StorageDeviceCustomerUser class.
 * 
 */

public class StorageDeviceCustomerUserManager {
	
	private static final Logger log = 
        Logger.getLogger(StorageDeviceCustomerUserManager.class.getName());
	
	/**
     * Get a StorageDeviceCustomerUser instance from the datastore given the StorageDeviceCustomerUser key.
     * @param key
     * 			: the StorageDeviceCustomerUser's key
     * @return StorageDeviceCustomerUser instance, null if StorageDeviceCustomerUser is not found
     */
	public static StorageDeviceCustomerUser getStorageDeviceCustomerUser(Long key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		StorageDeviceCustomerUser storageDeviceCustomerUser;
		try  {
			storageDeviceCustomerUser = pm.getObjectById(StorageDeviceCustomerUser.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return storageDeviceCustomerUser;
	}
	
	/**
     * Get all StorageDeviceCustomerUser instances from the datastore that
     * contain this Customer User
     * @return All StorageDeviceCustomerUsers that contain the given 
     * 			customer user
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static List<StorageDeviceCustomerUser> getAllStorageDeviceCustomerUsersWithCustomerUser(
			Key customerUserKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(StorageDeviceCustomerUser.class);
        query.setFilter("customerUser == customerUserParam");
        query.setOrdering("storageDevice desc");
        query.declareParameters(Key.class.getName() + 
        		" customerUserParam");
        
        List<StorageDeviceCustomerUser> types = null;
        try {
        	types = (List<StorageDeviceCustomerUser>) query.execute(customerUserKey);
            // touch all elements
            for (StorageDeviceCustomerUser t : types)
                t.getKey();
        } 
        finally {
        	pm.close();
            query.closeAll();
        }

        return types;
    }
	
	/**
     * Get all StorageDeviceCustomerUser instances from the datastore that
     * contain this StorageDevice
     * @return All StorageDeviceCustomerUsers that contain the given 
     * 			StorageDevice
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static List<StorageDeviceCustomerUser> getAllStorageDeviceCustomerUsersWithStorageDevice(
			Key storageDeviceKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(StorageDeviceCustomerUser.class);
        query.setFilter("storageDevice == storageDeviceParam");
        query.setOrdering("customerUser desc");
        query.declareParameters(Key.class.getName() + 
        		" storageDeviceParam");
        
        List<StorageDeviceCustomerUser> types = null;
        try {
        	types = (List<StorageDeviceCustomerUser>) query.execute(storageDeviceKey);
            // touch all elements
            for (StorageDeviceCustomerUser t : types)
                t.getKey();
        } 
        finally {
        	pm.close();
            query.closeAll();
        }

        return types;
    }
	
	/**
     * Put StorageDeviceCustomerUser into datastore.
     * Stations the given StorageDeviceCustomerUser instance in the datastore calling the 
     * PersistenceManager's makePersistent() method.
     * @param storageDeviceCustomerUser
     * 			: the StorageDeviceCustomerUser instance to store
     */
	public static void putStorageDeviceCustomerUser(
			StorageDeviceCustomerUser storageDeviceCustomerUser) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistent(storageDeviceCustomerUser);
			tx.commit();
			log.info("StorageDeviceCustomerUser \"" + storageDeviceCustomerUser.getKey() + 
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
    * Delete StorageDeviceCustomerUser from datastore.
    * Deletes the StorageDeviceCustomerUser corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    * 			: the key of the StorageDeviceCustomerUser instance to delete
    */
	public static void deleteStorageDeviceCustomerUser(Long key) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			StorageDeviceCustomerUser storageDeviceCustomerUser = 
					pm.getObjectById(StorageDeviceCustomerUser.class, key);
			String StorageDeviceCustomerUserName = 
					String.valueOf(storageDeviceCustomerUser.getKey());
			tx.begin();
			pm.deletePersistent(storageDeviceCustomerUser);
			tx.commit();
			log.info("StorageDeviceCustomerUser \"" + StorageDeviceCustomerUserName + 
                     "\" deleted successfully from datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
}
