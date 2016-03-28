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

import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the DeviceServiceType class.
 * 
 */

public class DeviceServiceTypeManager {
	
	private static final Logger log = 
        Logger.getLogger(DeviceServiceTypeManager.class.getName());
	
	/**
     * Get a DeviceServiceType instance from the datastore given the DeviceServiceType key.
     * @param key
     * 			: the DeviceServiceType's key
     * @return DeviceServiceType instance, null if DeviceServiceType is not found
     */
	public static DeviceServiceType getDeviceServiceType(Long key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		DeviceServiceType deviceServiceType;
		try  {
			deviceServiceType = pm.getObjectById(DeviceServiceType.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return deviceServiceType;
	}
	
	/**
     * Get all DeviceServiceType instances from the datastore.
     * @return All DeviceServiceType instances
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static List<DeviceServiceType> getAllDeviceServiceTypes() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(DeviceServiceType.class);

        List<DeviceServiceType> types = null;
        try {
        	types = (List<DeviceServiceType>) query.execute();
            // touch all elements
            for (DeviceServiceType t : types)
                t.getDeviceServiceTypeName();
        } 
        finally {
        	pm.close();
            query.closeAll();
        }

        return types;
    }
	
	/**
     * Put DeviceServiceType into datastore.
     * Stations the given DeviceServiceType instance in the datastore calling the 
     * PersistenceManager's makePersistent() method.
     * @param deviceServiceType
     * 			: the DeviceServiceType instance to store
     */
	public static void putDeviceServiceType(DeviceServiceType deviceServiceType) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistent(deviceServiceType);
			tx.commit();
			log.info("DeviceServiceType \"" + deviceServiceType.getDeviceServiceTypeName() + 
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
    * Delete DeviceServiceType from datastore.
    * Deletes the DeviceServiceType corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    * 			: the key of the DeviceServiceType instance to delete
    */
	public static void deleteDeviceServiceType(Long key) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			DeviceServiceType deviceServiceType = pm.getObjectById(DeviceServiceType.class, key);
			String DeviceServiceTypeName = deviceServiceType.getDeviceServiceTypeName();
			tx.begin();
			pm.deletePersistent(deviceServiceType);
			tx.commit();
			log.info("DeviceServiceType \"" + DeviceServiceTypeName + 
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
    * Update DeviceServiceType attributes.
    * Update's the given DeviceServiceType's attributes in the datastore.
    * @param key
    * 			: the key of the DeviceServiceType whose attributes will be updated
    * @param deviceServiceTypeName
    * 			: the new name to give to the DeviceServiceType
    * @param deviceServiceTypeDescription
    * 			: the new description to give to the DeviceServiceType
    * @param deviceServiceTypeComments
    * 			: the new comments to give to the DeviceServiceType
	* @throws MissingRequiredFieldsException 
    */
	public static void updateDeviceServiceTypeAttributes(Long key,
			String deviceServiceTypeName, String deviceServiceTypeDescription,
			String deviceServiceTypeComments) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			DeviceServiceType deviceServiceType = pm.getObjectById(DeviceServiceType.class, key);
			tx.begin();
			deviceServiceType.setDeviceServiceTypeName(deviceServiceTypeName);
			deviceServiceType.setDeviceServiceTypeDescription(deviceServiceTypeDescription);
			deviceServiceType.setDeviceServiceTypeComments(deviceServiceTypeComments);
			tx.commit();
			log.info("DeviceServiceType \"" + deviceServiceTypeName + 
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
