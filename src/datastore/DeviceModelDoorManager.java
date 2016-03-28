/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.Key;

import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the DeviceModelDoor class.
 * 
 */

public class DeviceModelDoorManager {
	
	private static final Logger log = 
        Logger.getLogger(DeviceModelDoorManager.class.getName());
	
	/**
     * Get a DeviceModelDoor instance from the datastore given the DeviceModelDoor key.
     * @param key
     * 			: the DeviceModelDoor's key
     * @return DeviceModelDoor instance, null if DeviceModelDoor is not found
     */
	public static DeviceModelDoor getDeviceModelDoor(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		DeviceModelDoor deviceModelDoor;
		try  {
			deviceModelDoor = pm.getObjectById(DeviceModelDoor.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return deviceModelDoor;
	}
	
	/**
     * Get all DeviceModelDoor instances from the datastore that belong to
     * this DeviceModel.
     * @param deviceModelKey
     * @return All DeviceModelDoor instances that belong to the given DeviceModel
     * TODO: Inefficient touching of objects
     */
	public static List<DeviceModelDoor> getAllDeviceModelDoorsFromDeviceModel(
			Key deviceModelKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        DeviceModel deviceModel = pm.getObjectById(DeviceModel.class, deviceModelKey);
         
        List<DeviceModelDoor> result = null;
        ArrayList<DeviceModelDoor> finalResult = new ArrayList<DeviceModelDoor>();
        try {
            result = deviceModel.getDeviceModelDoors();
            for (DeviceModelDoor deviceModelDoor : result) {
            	deviceModelDoor.getDeviceModelDoorNumber();
            	finalResult.add(deviceModelDoor);
            }
        }
        finally {
            pm.close();
        }

        Collections.sort(finalResult);
        return finalResult;
    }
	
    /**
     * Put DeviceModelDoor into datastore.
     * Stores the given DeviceModelDoor instance in the datastore for this
     * deviceModel.
     * @param deviceModelKey
     *          : the key of the DeviceModel where the deviceModelDoor will be added
     * @param deviceModelDoor
     *          : the DeviceModelDoor instance to deviceModel
     */
    public static void putDeviceModelDoor(Key deviceModelKey, 
    		DeviceModelDoor deviceModelDoor) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            DeviceModel deviceModel = 
                    pm.getObjectById(DeviceModel.class, deviceModelKey);
            tx.begin();
            deviceModel.addDeviceModelDoor(deviceModelDoor);
            tx.commit();
            log.info("DeviceModelDoor \"" + deviceModelDoor.getDeviceModelDoorNumber() + 
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
    * Delete DeviceModelDoor from datastore.
    * Deletes the DeviceModelDoor corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the DeviceModelDoor instance to delete
    */
    public static void deleteDeviceModelDoor(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            DeviceModel deviceModel = 
            		pm.getObjectById(DeviceModel.class, key.getParent());
            DeviceModelDoor deviceModelDoor = 
            		pm.getObjectById(DeviceModelDoor.class, key);
            Integer deviceModelDoorContent = 
            		deviceModelDoor.getDeviceModelDoorNumber();
            tx.begin();
            deviceModel.removeDeviceModelDoor(deviceModelDoor);
            tx.commit();
            log.info("DeviceModelDoor \"" + deviceModelDoorContent + 
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
    * Update DeviceModelDoor attributes.
    * Update's the given DeviceModelDoor's attributes in the datastore.
    * @param key
    * 			: the key of the DeviceModelDoor whose attributes will be updated
    * @param deviceModelDoorNumber
    * 			: the new number to give to the DeviceModelDoor
    * @param deviceModelDoorComments
    * 			: the new comments to give to the DeviceModelDoor
	* @throws MissingRequiredFieldsException 
    */
	public static void updateDeviceModelDoorAttributes(Key key,
			Integer deviceModelDoorNumber,
			String deviceModelDoorComments) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			DeviceModelDoor deviceModelDoor = pm.getObjectById(DeviceModelDoor.class, key);
			tx.begin();
			deviceModelDoor.setDeviceModelDoorNumber(deviceModelDoorNumber);
			deviceModelDoor.setDeviceModelDoorComments(deviceModelDoorComments);
			tx.commit();
			log.info("DeviceModelDoor \"" + deviceModelDoorNumber + 
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
