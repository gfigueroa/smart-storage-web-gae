/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import util.DateManager;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import datastore.AlarmWarningStorageDevice.AlarmWarningStorageDeviceStatus;
import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the AlarmWarningStorageDevice class.
 * 
 */

public class AlarmWarningStorageDeviceManager {
	
	private static final Logger log = 
        Logger.getLogger(AlarmWarningStorageDeviceManager.class.getName());
	
	/**
     * Get a AlarmWarningStorageDevice instance from the datastore given the AlarmWarningStorageDevice key.
     * @param key
     * 			: the AlarmWarningStorageDevice's key
     * @return AlarmWarningStorageDevice instance, null if AlarmWarningStorageDevice is not found
     */
	public static AlarmWarningStorageDevice getAlarmWarningStorageDevice(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		AlarmWarningStorageDevice alarmWarningStorageDevice;
		try  {
			alarmWarningStorageDevice = pm.getObjectById(AlarmWarningStorageDevice.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return alarmWarningStorageDevice;
	}
	
	/**
     * Get all AlarmWarningStorageDevice instances from the datastore that belong to
     * this AlarmWarning.
     * @param alarmWarningKey
     * @return All AlarmWarningStorageDevice instances that belong to the given AlarmWarning
     * TODO: Inefficient touching of objects
     */
	public static List<AlarmWarningStorageDevice> getAllAlarmWarningStorageDevicesFromAlarmWarning(
			Key alarmWarningKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        AlarmWarning alarmWarning = 
        		pm.getObjectById(AlarmWarning.class, alarmWarningKey);
         
        List<AlarmWarningStorageDevice> result = null;
        ArrayList<AlarmWarningStorageDevice> finalResult = new ArrayList<AlarmWarningStorageDevice>();
        try {
            result = alarmWarning.getAlarmWarningStorageDevices();
            for (AlarmWarningStorageDevice alarmWarningStorageDevice : result) {
            	alarmWarningStorageDevice.getKey();
            	finalResult.add(alarmWarningStorageDevice);
            }
        }
        finally {
            pm.close();
        }

        return finalResult;
    }
	
	/**
     * Get all AlarmWarningStorageDevice instances from the datastore that belong to
     * this StorageDevice.
     * @param storageDeviceKey
     * @param status: the alarmWarningStorageDeviceStatus
     * @return All AlarmWarningStorageDevice instances that belong to the given StorageDevice
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static List<AlarmWarningStorageDevice> getAllAlarmWarningStorageDevicesFromStorageDevice(
			Key storageDeviceKey, AlarmWarningStorageDeviceStatus status) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
    	Query query = pm.newQuery(AlarmWarningStorageDevice.class);
        query.setFilter("storageDevice == storageDeviceKeyParam && " +
        		"alarmWarningStorageDeviceStatus == statusParam");
        query.declareParameters(Key.class.getName() + " storageDeviceKeyParam, " +
        		datastore.AlarmWarningStorageDevice.AlarmWarningStorageDeviceStatus.class.getName() +
        		" statusParam");
        query.setOrdering("alarmWarningStorageDeviceCreationDate asc");

        try {
            List<AlarmWarningStorageDevice> result = 
            		(List<AlarmWarningStorageDevice>) query.execute(storageDeviceKey, status);
            
            // Touch objects
            for (AlarmWarningStorageDevice alarmWarningStorageDevice : result) {
            	alarmWarningStorageDevice.getKey();
            }
            
            return result;
        }
        finally {
            pm.close();
        }
    }
	
    /**
     * Put AlarmWarningStorageDevice into datastore.
     * Stores the given AlarmWarningStorageDevice instance in the datastore for this
     * alarmWarning. If the alarmWarningCount exceeds the alarmWarningMaxCount, then
     * this AlarmWarningStorageDevice will not be stored in the datastore.
     * Returns the AlarmWarning instance where this message was stored.
     * @param alarmWarningKey
     *          : the key of the AlarmWarning where the alarmWarningStorageDevice will be added
     * @param alarmWarningStorageDevice
     *          : the AlarmWarningStorageDevice instance to deviceModel
     * @return AlarmWarning where this message was stored (for better performance)
     */
    public static AlarmWarning putAlarmWarningStorageDevice(Key alarmWarningKey, 
    		AlarmWarningStorageDevice alarmWarningStorageDevice) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            AlarmWarning alarmWarning = 
                    pm.getObjectById(AlarmWarning.class, alarmWarningKey);
            
            if (alarmWarning.getAlarmWarningCount() < 
            		alarmWarning.getAlarmWarningMaxCount()) {
            	
	            tx.begin();
	            alarmWarning.addAlarmWarningStorageDevice(alarmWarningStorageDevice);
	            alarmWarning.increaseAlarmWarningCount();
	            tx.commit();
	            
	            log.info("AlarmWarningStorageDevice \"" + alarmWarningStorageDevice.getKey() + 
	                "\" stored successfully in datastore.");
            }
	            
            return alarmWarning;
        }
        finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }
     
    /**
    * Delete AlarmWarningStorageDevice from datastore.
    * Deletes the AlarmWarningStorageDevice corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the AlarmWarningStorageDevice instance to delete
    */
    public static void deleteAlarmWarningStorageDevice(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            AlarmWarning alarmWarning = 
            		pm.getObjectById(AlarmWarning.class, key.getParent());
            AlarmWarningStorageDevice alarmWarningStorageDevice = 
            		pm.getObjectById(AlarmWarningStorageDevice.class, key);
            String alarmWarningStorageDeviceContent = 
            		KeyFactory.keyToString(alarmWarningStorageDevice.getKey());
            tx.begin();
            alarmWarning.removeAlarmWarningStorageDevice(alarmWarningStorageDevice);
            alarmWarning.decreaseAlarmWarningCount();
            tx.commit();
            log.info("AlarmWarningStorageDevice \"" + alarmWarningStorageDeviceContent + 
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
     * Deletes all AlarmWarningStorageDevices from this Storage Device that have been expired
     * (older than alarmMessageEffectivePeriod)
     * @param storageDeviceKey: the storageDevice key
     * @param all: whether to delete all AlarmWarningStorageDevices or only expired ones
     */
	@SuppressWarnings("unchecked")
	public static void deleteAllExpiredAlarmWarningStorageDevices(Key storageDeviceKey, 
			boolean all) {
    	
    	Date now = new Date();
    	
    	PersistenceManager pm = PMF.get().getPersistenceManager();
    	
        Query query = pm.newQuery(AlarmWarningStorageDevice.class);
        
        // Check whether to delete all or not
        if (all) {
	        query.setFilter("storageDevice == storageDeviceKeyParam");
        }
        else {
        	query.setFilter("alarmWarningStorageDeviceCreationDate < effectiveDateParam && " +
	        		"storageDevice == storageDeviceKeyParam");
        }
        
        query.declareParameters("java.util.Date effectiveDateParam, " +
        		Key.class.getName() + " storageDeviceKeyParam");

        try {
        	StorageDevice storageDevice = 
        			pm.getObjectById(StorageDevice.class, storageDeviceKey);
        	Integer alarmMessageEffectivePeriod = 
        			storageDevice.getAlarmMessageEffectivePeriod();
        	
        	Date effectiveDate = DateManager.subtractDaysFromDate(now, 
        			alarmMessageEffectivePeriod);
        	
        	List<AlarmWarningStorageDevice> alarmWarningStorageDevices = 
        			(List<AlarmWarningStorageDevice>) query.execute(
        					effectiveDate, storageDeviceKey);
        	for (AlarmWarningStorageDevice alarmWarningStorageDevice : alarmWarningStorageDevices) {
        		AlarmWarning alarmWarning = 
        				pm.getObjectById(AlarmWarning.class, 
        						alarmWarningStorageDevice.getKey().getParent());
        		pm.deletePersistent(alarmWarningStorageDevice);
        		alarmWarning.decreaseAlarmWarningCount();
        	}
//    		query.deletePersistentAll(effectiveDate, 
//    				storageDeviceKey);
        } 
        finally {
        	pm.close();
            query.closeAll();
        }
    }

	/**
    * Update AlarmWarningStorageDevice attributes.
    * Update's the given AlarmWarningStorageDevice's attributes in the datastore.
    * @param key
    * 			: the key of the AlarmWarningStorageDevice whose attributes will be updated
    * @param alarmWarningStorageDeviceStatus
    * 			: the new status to give to the AlarmWarningStorageDevice
    * @param alarmWarningStorageDeviceNote
    * 			: the new note to give to the AlarmWarningStorageDevice
	* @throws MissingRequiredFieldsException 
    */
	public static void updateAlarmWarningStorageDeviceAttributes(
			Key key,
			AlarmWarningStorageDeviceStatus alarmWarningStorageDeviceStatus,
			String alarmWarningStorageDeviceNote) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			AlarmWarningStorageDevice alarmWarningStorageDevice = 
					pm.getObjectById(AlarmWarningStorageDevice.class, key);
			AlarmWarning alarmWarning = 
					pm.getObjectById(AlarmWarning.class, key.getParent());
			
			tx.begin();
			alarmWarningStorageDevice.setAlarmWarningStorageDeviceStatus(alarmWarningStorageDeviceStatus);
			alarmWarningStorageDevice.setAlarmWarningStorageDeviceNote(alarmWarningStorageDeviceNote);
			
			if (alarmWarningStorageDeviceStatus == AlarmWarningStorageDeviceStatus.RESOLVED) {
				alarmWarning.decreaseAlarmWarningCount();
			}
			
			tx.commit();
			log.info("AlarmWarningStorageDevice \"" + alarmWarningStorageDevice.getKey() + 
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
