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

import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the DeviceModelPartition class.
 * 
 */

public class DeviceModelPartitionManager {
	
	private static final Logger log = 
        Logger.getLogger(DeviceModelPartitionManager.class.getName());
	
	/**
     * Get a DeviceModelPartition instance from the datastore given the DeviceModelPartition key.
     * @param key
     * 			: the DeviceModelPartition's key
     * @return DeviceModelPartition instance, null if DeviceModelPartition is not found
     */
	public static DeviceModelPartition getDeviceModelPartition(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		DeviceModelPartition deviceModelPartition;
		try  {
			deviceModelPartition = pm.getObjectById(DeviceModelPartition.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return deviceModelPartition;
	}
	
	/**
     * Get all DeviceModelPartition instances from the datastore that belong to
     * this DeviceModelDoor.
     * @param deviceModelDoorKey
     * @return All DeviceModelPartition instances that belong to the given DeviceModelDoor
     * TODO: Inefficient touching of objects
     */
	public static List<DeviceModelPartition> getAllDeviceModelPartitionsFromDeviceModelDoor(
			Key deviceModelDoorKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        DeviceModelDoor deviceModelDoor = 
        		pm.getObjectById(DeviceModelDoor.class, deviceModelDoorKey);
         
        List<DeviceModelPartition> result = null;
        ArrayList<DeviceModelPartition> finalResult = new ArrayList<DeviceModelPartition>();
        try {
            result = deviceModelDoor.getDeviceModelPartitions();
            for (DeviceModelPartition deviceModelPartition : result) {
            	deviceModelPartition.getDeviceModelPartitionName();
            	finalResult.add(deviceModelPartition);
            }
        }
        finally {
            pm.close();
        }

        return finalResult;
    }
	
    /**
     * Put DeviceModelPartition into datastore.
     * Stores the given DeviceModelPartition instance in the datastore for this
     * deviceModelDoor.
     * @param deviceModelDoorKey
     *          : the key of the DeviceModelDoor where the deviceModelPartition will be added
     * @param deviceModelPartition
     *          : the DeviceModelPartition instance to deviceModel
     */
    public static void putDeviceModelPartition(Key deviceModelDoorKey, 
    		DeviceModelPartition deviceModelPartition) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            DeviceModelDoor deviceModelDoor = 
                    pm.getObjectById(DeviceModelDoor.class, deviceModelDoorKey);
            tx.begin();
            deviceModelDoor.addDeviceModelPartition(deviceModelPartition);
            tx.commit();
            log.info("DeviceModelPartition \"" + deviceModelPartition.getDeviceModelPartitionName() + 
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
    * Delete DeviceModelPartition from datastore.
    * Deletes the DeviceModelPartition corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the DeviceModelPartition instance to delete
    */
    public static void deleteDeviceModelPartition(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            DeviceModelDoor deviceModelDoor = 
            		pm.getObjectById(DeviceModelDoor.class, key.getParent());
            DeviceModelPartition deviceModelPartition = 
            		pm.getObjectById(DeviceModelPartition.class, key);
            String deviceModelPartitionContent = 
            		deviceModelPartition.getDeviceModelPartitionName();
            tx.begin();
            deviceModelDoor.removeDeviceModelPartition(deviceModelPartition);
            tx.commit();
            log.info("DeviceModelPartition \"" + deviceModelPartitionContent + 
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
    * Update DeviceModelPartition attributes.
    * Update's the given DeviceModelPartition's attributes in the datastore.
    * @param key
    * 			: the key of the DeviceModelPartition whose attributes will be updated
    * @param deviceModelPartitionName
    * 			: the new name to give to the DeviceModelPartition
    * @param deviceModelPartitionComments
    * 			: the new comments to give to the DeviceModelPartition
	* @throws MissingRequiredFieldsException 
    */
	public static void updateDeviceModelPartitionAttributes(
			Key key,
			String deviceModelPartitionName,
			String deviceModelPartitionComments) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			DeviceModelPartition deviceModelPartition = 
					pm.getObjectById(DeviceModelPartition.class, key);
			tx.begin();
			deviceModelPartition.setDeviceModelPartitionName(deviceModelPartitionName);
			deviceModelPartition.setDeviceModelPartitionComments(deviceModelPartitionComments);
			tx.commit();
			log.info("DeviceModelPartition \"" + deviceModelPartitionName + 
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
