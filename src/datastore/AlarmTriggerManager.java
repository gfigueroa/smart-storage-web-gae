/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.Key;

import datastore.AlarmTrigger.AlarmTriggerConditionOperator;
import datastore.AlarmTrigger.AlarmTriggerLevel;
import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the AlarmTrigger class.
 * 
 */

public class AlarmTriggerManager {
	
	private static final Logger log = 
        Logger.getLogger(AlarmTriggerManager.class.getName());
	
	/**
     * Get a AlarmTrigger instance from the datastore given the AlarmTrigger key.
     * @param key
     * 			: the AlarmTrigger's key
     * @return AlarmTrigger instance, null if AlarmTrigger is not found
     */
	public static AlarmTrigger getAlarmTrigger(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		AlarmTrigger alarmTrigger;
		try  {
			alarmTrigger = pm.getObjectById(AlarmTrigger.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return alarmTrigger;
	}
	
	/**
     * Get AlarmTrigger string representation
     * @param alarmTriggerKey
     * 			: the AlarmTrigger's key
     * @return AlarmTrigger string representation, null if AlarmTrigger is not found
     */
	public static String getAlarmTriggerString(Key alarmTriggerKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		String alarmTriggerString = "";
		try  {
			AlarmTrigger alarmTrigger = pm.getObjectById(AlarmTrigger.class, alarmTriggerKey);
			SensorInstance sensorInstance = 
					pm.getObjectById(SensorInstance.class, alarmTriggerKey.getParent());
			
			String unit = "";
		    if (sensorInstance.getSensorInstanceLabel().startsWith("Door")) {
		    	unit = "Seconds";
		    }
		    else {
		    	SensorType sensorType = 
		    			pm.getObjectById(SensorType.class, sensorInstance.getSensorType());
		    	unit = sensorType.getSensorTypeUnit();
		    }
			
			alarmTriggerString += sensorInstance.getSensorInstanceLabel() + " " + 
					alarmTrigger.getAlarmTriggerConditionOperatorString() + " " + 
					alarmTrigger.getAlarmTriggerConditionValue() + " " + unit;
			
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return alarmTriggerString;
	}
	
	/**
     * Get AlarmTrigger instance from the datastore that belongs to
     * this SensorInstance with this code.
     * @param sensorInstanceKey
     * @param alarmTriggerCode
     * @return All AlarmTrigger instances that belong to the given SensorInstance
     * TODO: Inefficient touching of objects
     */
	public static AlarmTrigger getAlarmTriggerFromSensorInstanceWithCode(
			Key sensorInstanceKey, Integer alarmTriggerCode) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        SensorInstance sensorInstance = pm.getObjectById(SensorInstance.class, sensorInstanceKey);
        
        List<AlarmTrigger> result = null;
        AlarmTrigger finalResult = null;
        try {
            result = sensorInstance.getAlarmTriggers();
            for (AlarmTrigger alarmTrigger : result) {
            	if (alarmTrigger.getAlarmTriggerCode() == alarmTriggerCode) {
            		finalResult = alarmTrigger;
            		break;
            	}
            }
        }
        finally {
            pm.close();
        }

        return finalResult;
    }
	
	/**
     * Get all AlarmTrigger instances from the datastore that belong to
     * this SensorInstance.
     * @param sensorInstanceKey
     * @return All AlarmTrigger instances that belong to the given SensorInstance
     * TODO: Inefficient touching of objects
     */
	public static List<AlarmTrigger> getAllAlarmTriggersFromSensorInstance(
			Key sensorInstanceKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        SensorInstance sensorInstance = pm.getObjectById(SensorInstance.class, sensorInstanceKey);
         
        List<AlarmTrigger> result = null;
        ArrayList<AlarmTrigger> finalResult = new ArrayList<AlarmTrigger>();
        try {
            result = sensorInstance.getAlarmTriggers();
            for (AlarmTrigger alarmTrigger : result) {
            	alarmTrigger.getAlarmTriggerCode();
            	finalResult.add(alarmTrigger);
            }
        }
        finally {
            pm.close();
        }

        Collections.sort(finalResult);
        return finalResult;
    }
	
	/**
     * Get all AlarmTrigger instances from the datastore that belong to
     * this StorageDevice.
     * @param storageDeviceKey
     * @return All AlarmTrigger instances that belong to the given StorageDevice
     * TODO: Inefficient touching of objects
     */
	public static List<AlarmTrigger> getAllAlarmTriggersFromStorageDevice(
			Key storageDeviceKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        StorageDevice storageDevice = pm.getObjectById(StorageDevice.class, storageDeviceKey);
        List<SensorInstance> sensorInstances = storageDevice.getSensorInstances();
        
        ArrayList<AlarmTrigger> finalResult = new ArrayList<AlarmTrigger>();
        try {
        	
            for (SensorInstance sensorInstance : sensorInstances) {
            	List<AlarmTrigger> alarmTriggers = sensorInstance.getAlarmTriggers();
            	for (AlarmTrigger alarmTrigger : alarmTriggers) {
            		alarmTrigger.getAlarmTriggerCode(); // Touch
            		finalResult.add(alarmTrigger);
            	}
            }
        }
        finally {
            pm.close();
        }

        Collections.sort(finalResult);
        return finalResult;
    }
	
	/**
     * Check AlarmTriggers for StorageDeviceDoors
     * Checks all StorageDeviceDoors and generate AlarmTriggerMessages
     * if the corresponding AlarmTriggers check.
	 * @return a HashMap of the AlarmTriggerMessages, the key is the AlarmTriggerMessage, the
	 * value is the Key of the AlarmTrigger
     */
    @SuppressWarnings({ "unchecked" })
	public static HashMap<AlarmTriggerMessage, Key> checkStorageDeviceDoorAlarmTriggers() {
    	
    	Date now = new Date();
    	
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Query query = pm.newQuery(AlarmTrigger.class);
        query.setFilter("storageDeviceDoor != null");
        query.setOrdering("storageDeviceDoor asc");

        HashMap<AlarmTriggerMessage, Key> alarmTriggerMessages = new HashMap<>();
        try {
        	List<AlarmTrigger> alarmTriggers = 
            		(List<AlarmTrigger>) query.execute();
        	for (AlarmTrigger alarmTrigger : alarmTriggers) {
        		StorageDeviceDoor storageDeviceDoor = 
        				pm.getObjectById(StorageDeviceDoor.class, 
        						alarmTrigger.getStorageDeviceDoor());
        		
        		if (storageDeviceDoor.getStorageDeviceDoorIsOpen()) {
        			Date doorLastOpenTime = 
        					storageDeviceDoor.getStorageDeviceDoorLastOpenTime();
        			// Get the number of seconds that the door has been opened
        			long doorTimeOpened = (now.getTime() - doorLastOpenTime.getTime()) / 1000;
        			
        			// Check if AlarmTrigger is triggered with the given doorTimeOpened
        			if (alarmTrigger.alarmTriggered(doorTimeOpened)) {
						AlarmTriggerMessage alarmTriggerMessage = 
								new AlarmTriggerMessage(
										storageDeviceDoor.getKey().getParent(),
										null
										);
						alarmTriggerMessages.put(alarmTriggerMessage, alarmTrigger.getKey());
        			}
        		}
        	}
        }
        catch (Exception ex) {
            log.log(Level.SEVERE, ex.toString());
            ex.printStackTrace();
        }
        finally {
            pm.close();
        }
        
        return alarmTriggerMessages;
    }
	
    /**
     * Put AlarmTrigger into datastore.
     * Stores the given AlarmTrigger instance in the datastore for this
     * sensorInstance.
     * @param sensorInstanceKey
     *          : the key of the SensorInstance where the alarmTrigger will be added
     * @param alarmTrigger
     *          : the AlarmTrigger instance to sensorInstance
     */
    public static void putAlarmTrigger(Key sensorInstanceKey, 
    		AlarmTrigger alarmTrigger) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            SensorInstance sensorInstance = 
                    pm.getObjectById(SensorInstance.class, sensorInstanceKey);
            tx.begin();
            sensorInstance.addAlarmTrigger(alarmTrigger);
            tx.commit();
            log.info("AlarmTrigger \"" + alarmTrigger.getAlarmTriggerCode() + 
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
    * Delete AlarmTrigger from datastore.
    * Deletes the AlarmTrigger corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the AlarmTrigger instance to delete
    */
    public static void deleteAlarmTrigger(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            SensorInstance sensorInstance = 
            		pm.getObjectById(SensorInstance.class, key.getParent());
            AlarmTrigger alarmTrigger = 
            		pm.getObjectById(AlarmTrigger.class, key);
            Integer alarmTriggerContent = 
            		alarmTrigger.getAlarmTriggerCode();
            tx.begin();
            sensorInstance.removeAlarmTrigger(alarmTrigger);
            tx.commit();
            log.info("AlarmTrigger \"" + alarmTriggerContent + 
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
    * Update AlarmTrigger attributes.
    * Update's the given AlarmTrigger's attributes in the datastore.
    * @param key
    * 			: the key of the AlarmTrigger whose attributes will be updated
     * @param alarmTriggerCode
     * 			: AlarmTrigger code
     * @param alarmTriggerConditionOperator
     * 			: AlarmTrigger condition operator
     * @param alarmTriggerConditionValue
     * 			: AlarmTrigger condition value
     * @param alarmTriggerMaxCount
     * 			: AlarmTrigger max count
     * @param alarmTriggerLevel
     * 			: AlarmTrigger level
     * @param alarmTriggerComments
     * 			: AlarmTrigger comments
	* @throws MissingRequiredFieldsException 
    */
	public static void updateAlarmTriggerAttributes(Key key,
			Integer alarmTriggerCode, 
    		AlarmTriggerConditionOperator alarmTriggerConditionOperator,
    		Double alarmTriggerConditionValue,
    		Integer alarmTriggerMaxCount,
    		AlarmTriggerLevel alarmTriggerLevel,
    		String alarmTriggerComments) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			AlarmTrigger alarmTrigger = pm.getObjectById(AlarmTrigger.class, key);
			tx.begin();
			alarmTrigger.setAlarmTriggerCode(alarmTriggerCode);
			alarmTrigger.setAlarmTriggerConditionOperator(
					alarmTriggerConditionOperator);
			alarmTrigger.setAlarmTriggerConditionValue(alarmTriggerConditionValue);
			alarmTrigger.setAlarmTriggerMaxCount(alarmTriggerMaxCount);
			alarmTrigger.setAlarmTriggerLevel(alarmTriggerLevel);
			alarmTrigger.setAlarmTriggerComments(alarmTriggerComments);
			tx.commit();
			log.info("AlarmTrigger \"" + alarmTriggerCode + 
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
    * Reset AlarmTrigger count.
    * Reset the given AlarmTrigger's count in the datastore to 0.
    * @param key
    * 			: the key of the AlarmTrigger whose attributes will be updated
    */
	public static void resetAlarmTriggerCount(Key key) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();

		try {
			AlarmTrigger alarmTrigger = pm.getObjectById(AlarmTrigger.class, key);
			tx.begin();
			alarmTrigger.resetAlarmTriggerCount();
			tx.commit();
			log.info("AlarmTrigger \"" + alarmTrigger.getAlarmTriggerCode() + 
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
    * Update AlarmTrigger count.
    * Update's the given AlarmTrigger's count in the datastore by 1.
    * If the trigger count is greater than or equal to the max count, then
	* the function returns false.
    * @param key
    * 			: the key of the AlarmTrigger whose attributes will be updated
	* @return True if alarmTriggerCount is less than alarmTriggerMaxCount,
	* 			False otherwise
    */
	public static boolean updateAlarmTriggerCount(Key key) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		
		boolean result = false;
		try {
			AlarmTrigger alarmTrigger = pm.getObjectById(AlarmTrigger.class, key);
			tx.begin();
			result = alarmTrigger.increaseAlarmTriggerCount();
			tx.commit();
			log.info("AlarmTrigger \"" + alarmTrigger.getAlarmTriggerCode() + 
                     "\"'s attributes updated in datastore.");
			return result;
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
}
