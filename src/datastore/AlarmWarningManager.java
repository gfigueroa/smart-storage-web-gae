/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.Key;

import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the AlarmWarning class.
 * 
 */

public class AlarmWarningManager {
	
	private static final Logger log = 
        Logger.getLogger(AlarmWarningManager.class.getName());
	
	/**
     * Get a AlarmWarning instance from the datastore given the AlarmWarning key.
     * @param key
     * 			: the AlarmWarning's key
     * @return AlarmWarning instance, null if AlarmWarning is not found
     */
	public static AlarmWarning getAlarmWarning(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		AlarmWarning alarmWarning;
		try  {
			alarmWarning = pm.getObjectById(AlarmWarning.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return alarmWarning;
	}
	
	/**
     * Get AlarmWarning instance from the datastore that belongs to
     * this DeviceModel with this code.
     * @param deviceModelKey
     * @param alarmWarningCode
     * @return All AlarmWarning instances that belong to the given DeviceModel
     * TODO: Inefficient touching of objects
     */
	public static AlarmWarning getAlarmWarningFromDeviceModelWithCode(
			Key deviceModelKey, Integer alarmWarningCode) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        DeviceModel deviceModel = pm.getObjectById(DeviceModel.class, deviceModelKey);
        
        List<AlarmWarning> result = null;
        AlarmWarning finalResult = null;
        try {
            result = deviceModel.getAlarmWarnings();
            for (AlarmWarning alarmWarning : result) {
            	if (alarmWarning.getAlarmWarningCode() == alarmWarningCode) {
            		finalResult = alarmWarning;
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
     * Get all AlarmWarning instances from the datastore that belong to
     * this DeviceModel.
     * @param deviceModelKey
     * @return All AlarmWarning instances that belong to the given DeviceModel
     * TODO: Inefficient touching of objects
     */
	public static List<AlarmWarning> getAllAlarmWarningsFromDeviceModel(
			Key deviceModelKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        DeviceModel deviceModel = pm.getObjectById(DeviceModel.class, deviceModelKey);
         
        List<AlarmWarning> result = null;
        ArrayList<AlarmWarning> finalResult = new ArrayList<AlarmWarning>();
        try {
            result = deviceModel.getAlarmWarnings();
            for (AlarmWarning alarmWarning : result) {
            	alarmWarning.getAlarmWarningCode();
            	finalResult.add(alarmWarning);
            }
        }
        finally {
            pm.close();
        }

        Collections.sort(finalResult);
        return finalResult;
    }
	
    /**
     * Put AlarmWarning into datastore.
     * Stores the given AlarmWarning instance in the datastore for this
     * deviceModel.
     * @param deviceModelKey
     *          : the key of the DeviceModel where the alarmWarning will be added
     * @param alarmWarning
     *          : the AlarmWarning instance to deviceModel
     */
    public static void putAlarmWarning(Key deviceModelKey, 
    		AlarmWarning alarmWarning) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            DeviceModel deviceModel = 
                    pm.getObjectById(DeviceModel.class, deviceModelKey);
            tx.begin();
            deviceModel.addAlarmWarning(alarmWarning);
            tx.commit();
            log.info("AlarmWarning \"" + alarmWarning.getAlarmWarningCode() + 
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
    * Delete AlarmWarning from datastore.
    * Deletes the AlarmWarning corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the AlarmWarning instance to delete
    */
    public static void deleteAlarmWarning(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            DeviceModel deviceModel = 
            		pm.getObjectById(DeviceModel.class, key.getParent());
            AlarmWarning alarmWarning = 
            		pm.getObjectById(AlarmWarning.class, key);
            Integer alarmWarningContent = 
            		alarmWarning.getAlarmWarningCode();
            tx.begin();
            deviceModel.removeAlarmWarning(alarmWarning);
            tx.commit();
            log.info("AlarmWarning \"" + alarmWarningContent + 
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
    * Update AlarmWarning attributes.
    * Update's the given AlarmWarning's attributes in the datastore.
    * @param key
    * 			: the key of the AlarmWarning whose attributes will be updated
    * @param alarmWarningCode
    * 			: the new code to give to the AlarmWarning
    * @param alarmWarningMessage
    * 			: the new message to give to the AlarmWarning
    * @param alarmWarningMaxCount
    * 			: AlarmWarning max count
	* @throws MissingRequiredFieldsException 
    */
	public static void updateAlarmWarningAttributes(Key key,
			Integer alarmWarningCode,
			String alarmWarningMessage,
    		Integer alarmWarningMaxCount) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			AlarmWarning alarmWarning = pm.getObjectById(AlarmWarning.class, key);
			tx.begin();
			alarmWarning.setAlarmWarningCode(alarmWarningCode);
			alarmWarning.setAlarmWarningMessage(alarmWarningMessage);
			alarmWarning.setAlarmWarningMaxCount(alarmWarningMaxCount);
			tx.commit();
			log.info("AlarmWarning \"" + alarmWarningCode + 
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
    * Reset AlarmWarning count.
    * Reset the given AlarmWarning's count in the datastore to 0.
    * @param key
    * 			: the key of the AlarmWarning whose attributes will be updated
    */
	public static void resetAlarmWarningCount(Key key) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();

		try {
			AlarmWarning alarmWarning = pm.getObjectById(AlarmWarning.class, key);
			tx.begin();
			alarmWarning.resetAlarmWarningCount();
			tx.commit();
			log.info("AlarmWarning \"" + alarmWarning.getAlarmWarningCode() + 
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
    * Update AlarmWarning count.
    * Update's the given AlarmWarning's count in the datastore by 1.
    * If the warning count is greater than or equal to the max count, then
	* the function returns false.
    * @param key
    * 			: the key of the AlarmWarning whose attributes will be updated
	* @return True if alarmWarningCount is less than alarmWarningMaxCount,
	* 			False otherwise
    */
	public static boolean updateAlarmWarningCount(Key key) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		
		boolean result = false;
		try {
			AlarmWarning alarmWarning = pm.getObjectById(AlarmWarning.class, key);
			tx.begin();
			result = alarmWarning.increaseAlarmWarningCount();
			tx.commit();
			log.info("AlarmWarning \"" + alarmWarning.getAlarmWarningCode() + 
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
