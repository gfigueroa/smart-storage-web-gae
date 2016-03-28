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

import datastore.AlarmWarningMessage.AlarmWarningMessageStatus;
import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the AlarmWarningMessage class.
 * 
 */

public class AlarmWarningMessageManager {
	
	private static final Logger log = 
        Logger.getLogger(AlarmWarningMessageManager.class.getName());
	
	/**
     * Get a AlarmWarningMessage instance from the datastore given the AlarmWarningMessage key.
     * @param key
     * 			: the AlarmWarningMessage's key
     * @return AlarmWarningMessage instance, null if AlarmWarningMessage is not found
     */
	public static AlarmWarningMessage getAlarmWarningMessage(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		AlarmWarningMessage alarmWarningMessage;
		try  {
			alarmWarningMessage = pm.getObjectById(AlarmWarningMessage.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return alarmWarningMessage;
	}
	
	/**
     * Get all AlarmWarningMessage instances from the datastore that belong to
     * this AlarmWarning.
     * @param alarmWarningKey
     * @return All AlarmWarningMessage instances that belong to the given AlarmWarning
     * TODO: Inefficient touching of objects
     */
	public static List<AlarmWarningMessage> getAllAlarmWarningMessagesFromAlarmWarning(
			Key alarmWarningKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        AlarmWarning alarmWarning = 
        		pm.getObjectById(AlarmWarning.class, alarmWarningKey);
         
        List<AlarmWarningMessage> result = null;
        ArrayList<AlarmWarningMessage> finalResult = new ArrayList<AlarmWarningMessage>();
        try {
            result = alarmWarning.getAlarmWarningMessages();
            for (AlarmWarningMessage alarmWarningMessage : result) {
            	alarmWarningMessage.getKey();
            	finalResult.add(alarmWarningMessage);
            }
        }
        finally {
            pm.close();
        }

        return finalResult;
    }
	
	/**
     * Get all AlarmWarningMessage instances from the datastore that belong to
     * this StorageDevice.
     * @param storageDeviceKey
     * @param status: the alarmWarningMessageStatus
     * @return All AlarmWarningMessage instances that belong to the given StorageDevice
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static List<AlarmWarningMessage> getAllAlarmWarningMessagesFromStorageDevice(
			Key storageDeviceKey, AlarmWarningMessageStatus status) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
    	Query query = pm.newQuery(AlarmWarningMessage.class);
        query.setFilter("storageDevice == storageDeviceKeyParam && " +
        		"alarmWarningMessageStatus == statusParam");
        query.declareParameters(Key.class.getName() + " storageDeviceKeyParam, " +
        		datastore.AlarmWarningMessage.AlarmWarningMessageStatus.class.getName() +
        		" statusParam");
        query.setOrdering("alarmWarningMessageCreationDate asc");

        try {
            List<AlarmWarningMessage> result = 
            		(List<AlarmWarningMessage>) query.execute(storageDeviceKey, status);
            
            // Touch objects
            for (AlarmWarningMessage alarmWarningMessage : result) {
            	alarmWarningMessage.getKey();
            }
            
            return result;
        }
        finally {
            pm.close();
        }
    }
	
    /**
     * Put AlarmWarningMessage into datastore.
     * Stores the given AlarmWarningMessage instance in the datastore for this
     * alarmWarning. If the alarmWarningCount exceeds the alarmWarningMaxCount, then
     * this AlarmWarningMessage will not be stored in the datastore.
     * Returns the AlarmWarning instance where this message was stored.
     * @param alarmWarningKey
     *          : the key of the AlarmWarning where the alarmWarningMessage will be added
     * @param alarmWarningMessage
     *          : the AlarmWarningMessage instance to deviceModel
     * @return AlarmWarning where this message was stored (for better performance)
     */
    public static AlarmWarning putAlarmWarningMessage(Key alarmWarningKey, 
    		AlarmWarningMessage alarmWarningMessage) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            AlarmWarning alarmWarning = 
                    pm.getObjectById(AlarmWarning.class, alarmWarningKey);
            
            if (alarmWarning.getAlarmWarningCount() < 
            		alarmWarning.getAlarmWarningMaxCount()) {
            	
	            tx.begin();
	            alarmWarning.addAlarmWarningMessage(alarmWarningMessage);
	            alarmWarning.increaseAlarmWarningCount();
	            tx.commit();
	            
	            log.info("AlarmWarningMessage \"" + alarmWarningMessage.getKey() + 
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
    * Delete AlarmWarningMessage from datastore.
    * Deletes the AlarmWarningMessage corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the AlarmWarningMessage instance to delete
    */
    public static void deleteAlarmWarningMessage(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            AlarmWarning alarmWarning = 
            		pm.getObjectById(AlarmWarning.class, key.getParent());
            AlarmWarningMessage alarmWarningMessage = 
            		pm.getObjectById(AlarmWarningMessage.class, key);
            String alarmWarningMessageContent = 
            		KeyFactory.keyToString(alarmWarningMessage.getKey());
            tx.begin();
            alarmWarning.removeAlarmWarningMessage(alarmWarningMessage);
            alarmWarning.decreaseAlarmWarningCount();
            tx.commit();
            log.info("AlarmWarningMessage \"" + alarmWarningMessageContent + 
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
     * Deletes all AlarmWarningMessages from this Storage Device that have been expired
     * (older than alarmMessageEffectivePeriod)
     * @param storageDeviceKey: the storageDevice key
     * @param all: whether to delete all AlarmWarningMessages or only expired ones
     */
	@SuppressWarnings("unchecked")
	public static void deleteAllExpiredAlarmWarningMessages(Key storageDeviceKey, 
			boolean all) {
    	
    	Date now = new Date();
    	
    	PersistenceManager pm = PMF.get().getPersistenceManager();
    	
        Query query = pm.newQuery(AlarmWarningMessage.class);
        
        // Check whether to delete all or not
        if (all) {
	        query.setFilter("storageDevice == storageDeviceKeyParam");
        }
        else {
        	query.setFilter("alarmWarningMessageCreationDate < effectiveDateParam && " +
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
        	
        	List<AlarmWarningMessage> alarmWarningMessages = 
        			(List<AlarmWarningMessage>) query.execute(
        					effectiveDate, storageDeviceKey);
        	for (AlarmWarningMessage alarmWarningMessage : alarmWarningMessages) {
        		AlarmWarning alarmWarning = 
        				pm.getObjectById(AlarmWarning.class, 
        						alarmWarningMessage.getKey().getParent());
        		pm.deletePersistent(alarmWarningMessage);
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
    * Update AlarmWarningMessage attributes.
    * Update's the given AlarmWarningMessage's attributes in the datastore.
    * @param key
    * 			: the key of the AlarmWarningMessage whose attributes will be updated
    * @param alarmWarningMessageStatus
    * 			: the new status to give to the AlarmWarningMessage
    * @param alarmWarningMessageNote
    * 			: the new note to give to the AlarmWarningMessage
	* @throws MissingRequiredFieldsException 
    */
	public static void updateAlarmWarningMessageAttributes(
			Key key,
			AlarmWarningMessageStatus alarmWarningMessageStatus,
			String alarmWarningMessageNote) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			AlarmWarningMessage alarmWarningMessage = 
					pm.getObjectById(AlarmWarningMessage.class, key);
			AlarmWarning alarmWarning = 
					pm.getObjectById(AlarmWarning.class, key.getParent());
			
			tx.begin();
			alarmWarningMessage.setAlarmWarningMessageStatus(alarmWarningMessageStatus);
			alarmWarningMessage.setAlarmWarningMessageNote(alarmWarningMessageNote);
			
			if (alarmWarningMessageStatus == AlarmWarningMessageStatus.RESOLVED) {
				alarmWarning.decreaseAlarmWarningCount();
			}
			
			tx.commit();
			log.info("AlarmWarningMessage \"" + alarmWarningMessage.getKey() + 
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
