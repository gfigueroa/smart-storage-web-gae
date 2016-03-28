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

import datastore.SensorInstance.SensorStatus;
import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the SensorInstance class.
 * 
 */

public class SensorInstanceManager {
	
	private static final Logger log = 
        Logger.getLogger(SensorInstanceManager.class.getName());
	
	/**
     * Get a SensorInstance instance from the datastore given the SensorInstance key.
     * @param key
     * 			: the SensorInstance's key
     * @return SensorInstance instance, null if SensorInstance is not found
     */
	public static SensorInstance getSensorInstance(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		SensorInstance sensorInstance;
		try  {
			sensorInstance = pm.getObjectById(SensorInstance.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return sensorInstance;
	}
	
	/**
     * Get all SensorInstance instances from the datastore that belong to
     * this StorageDevice.
     * @param storageDeviceKey
     * @return All SensorInstance instances that belong to the given StorageDevice
     * TODO: Inefficient touching of objects
     */
	public static List<SensorInstance> getAllSensorInstancesFromStorageDevice(
			Key storageDeviceKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        StorageDevice storageDevice = pm.getObjectById(StorageDevice.class, storageDeviceKey);
         
        List<SensorInstance> result = null;
        ArrayList<SensorInstance> finalResult = new ArrayList<SensorInstance>();
        try {
            result = storageDevice.getSensorInstances();
            for (SensorInstance sensorInstance : result) {
            	sensorInstance.getSensorInstanceComments();
            	finalResult.add(sensorInstance);
            }
        }
        finally {
            pm.close();
        }

        return finalResult;
    }
	
	/**
     * Get all SensorInstance instances from the datastore that belong to
     * this StorageDevice and belong to this SensorType.
     * @param storageDeviceKey
     * @param sensorTypeKey
     * @return All SensorInstance instances that belong to the given StorageDevice
     * and belong to this SensorType
     * TODO: Inefficient touching of objects
     */
	public static List<SensorInstance> getSensorInstancesFromStorageDevice(
			Key storageDeviceKey, Long sensorTypeKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        StorageDevice storageDevice = pm.getObjectById(StorageDevice.class, storageDeviceKey);
         
        List<SensorInstance> result = null;
        ArrayList<SensorInstance> finalResult = new ArrayList<SensorInstance>();
        try {
            result = storageDevice.getSensorInstances();
            for (SensorInstance sensorInstance : result) {
            	if (sensorInstance.getSensorType().equals(sensorTypeKey)) {
            		finalResult.add(sensorInstance);
            	}
            }
        }
        finally {
            pm.close();
        }

        return finalResult;
    }
	
    /**
     * Put SensorInstance into datastore.
     * Stores the given SensorInstance instance in the datastore for this
     * storageDevice.
     * @param storageDeviceKey
     *          : the key of the StorageDevice where the sensorInstance will be added
     * @param sensorInstance
     *          : the SensorInstance instance to storageDevice
     */
    public static void putSensorInstance(Key storageDeviceKey, 
    		SensorInstance sensorInstance) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            StorageDevice storageDevice = 
                    pm.getObjectById(StorageDevice.class, storageDeviceKey);
            tx.begin();
            storageDevice.addSensorInstance(sensorInstance);
            tx.commit();
            log.info("SensorInstance \"" + sensorInstance.getSensorInstanceLabel() + 
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
    * Delete SensorInstance from datastore.
    * Deletes the SensorInstance corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the SensorInstance instance to delete
    */
    public static void deleteSensorInstance(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            StorageDevice storageDevice = pm.getObjectById(StorageDevice.class, key.getParent());
            SensorInstance sensorInstance = pm.getObjectById(SensorInstance.class, key);
            String sensorInstanceContent = sensorInstance.getSensorInstanceLabel();
            tx.begin();
            storageDevice.removeSensorInstance(sensorInstance);
            tx.commit();
            log.info("SensorInstance \"" + sensorInstanceContent + 
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
    * Update SensorInstance attributes.
    * Update's the given SensorInstance's attributes in the datastore.
    * @param key
    * 			: the key of the SensorInstance whose attributes will be updated
    * @param sensorType
    * 			: sensorType key
    * @param storageDeviceDoor
    * 			: StorageDeviceDoor key
    * @param sensorInstanceLabel
    * 			: sensorInstanceLabel
    * @param sensorStatus
    * 			: sensorStatus
    * @param sensorInstanceComments
    * 			: SensorInstance comments
	* @throws MissingRequiredFieldsException 
    */
	public static void updateSensorInstanceAttributes(Key key,
			Long sensorType,
			Key storageDeviceDoor,
    		String sensorInstanceLabel,
    		SensorStatus sensorStatus,
    		String sensorInstanceComments) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			SensorInstance sensorInstance = pm.getObjectById(SensorInstance.class, key);
			tx.begin();
			sensorInstance.setSensorType(sensorType);
			sensorInstance.setStorageDeviceDoor(storageDeviceDoor);
			sensorInstance.setSensorInstanceLabel(sensorInstanceLabel);
			sensorInstance.setSensorStatus(sensorStatus);
			sensorInstance.setSensorInstanceComments(sensorInstanceComments);
			tx.commit();
			log.info("SensorInstance \"" + sensorInstanceComments + 
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
	 * Update the StorageDeviceDoor key of this SensorInstance
	 * @param key: the key of the SensorInstance to update
	 * @param storageDeviceDoorKey: the key of the StorageDeviceDoor
	 */
	public static void updateSensorInstanceStorageDeviceDoor(
			Key key,
			Key storageDeviceDoorKey) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			SensorInstance sensorInstance = pm.getObjectById(SensorInstance.class, key);
			tx.begin();
			sensorInstance.setStorageDeviceDoor(storageDeviceDoorKey);
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
	 * Update the last SensorReading key of this SensorInstance
	 * @param key: the key of the SensorInstance to update
	 * @param lastSensorReadingKey: the key of the last SensorReading
	 */
	public static void updateSensorInstanceLastSensorReading(
			Key key,
			Key lastSensorReadingKey) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			SensorInstance sensorInstance = pm.getObjectById(SensorInstance.class, key);
			tx.begin();
			sensorInstance.setLastSensorReading(lastSensorReadingKey); // Update last sensor reading
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
	 * Increment this SensorInstance's SensorReadingCache count by 1
	 * @param key: the key of the SensorInstance to update
	 */
	public static void incrementSensorReadingCacheCount(
			Key key) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			SensorInstance sensorInstance = pm.getObjectById(SensorInstance.class, key);
			tx.begin();
			sensorInstance.incrementSensorReadingCacheCount();
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
	 * Reset this SensorInstance's SensorReadingCache count to 0.
	 * @param key: the key of the SensorInstance to update
	 */
	public static void resetSensorReadingCacheCount(
			Key key) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			SensorInstance sensorInstance = pm.getObjectById(SensorInstance.class, key);
			tx.begin();
			sensorInstance.resetSensorReadingCacheCount();
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
}
