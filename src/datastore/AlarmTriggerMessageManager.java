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

import datastore.AlarmTriggerMessage.AlarmTriggerMessageStatus;
import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the AlarmTriggerMessage class.
 * 
 */

public class AlarmTriggerMessageManager {
	
	private static final Logger log = 
        Logger.getLogger(AlarmTriggerMessageManager.class.getName());
	
	/**
     * Get a AlarmTriggerMessage instance from the datastore given the AlarmTriggerMessage key.
     * @param key
     * 			: the AlarmTriggerMessage's key
     * @return AlarmTriggerMessage instance, null if AlarmTriggerMessage is not found
     */
	public static AlarmTriggerMessage getAlarmTriggerMessage(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		AlarmTriggerMessage alarmTriggerMessage;
		try  {
			alarmTriggerMessage = pm.getObjectById(AlarmTriggerMessage.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return alarmTriggerMessage;
	}
	
	/**
     * Get all AlarmTriggerMessage instances from the datastore that belong to
     * this AlarmTrigger.
     * @param alarmTriggerKey
     * @return All AlarmTriggerMessage instances that belong to the given AlarmTrigger
     * TODO: Inefficient touching of objects
     */
	public static List<AlarmTriggerMessage> getAllAlarmTriggerMessagesFromAlarmTrigger(
			Key alarmTriggerKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        AlarmTrigger alarmTrigger = 
        		pm.getObjectById(AlarmTrigger.class, alarmTriggerKey);
         
        List<AlarmTriggerMessage> result = null;
        ArrayList<AlarmTriggerMessage> finalResult = new ArrayList<AlarmTriggerMessage>();
        try {
            result = alarmTrigger.getAlarmTriggerMessages();
            for (AlarmTriggerMessage alarmTriggerMessage : result) {
            	alarmTriggerMessage.getKey();
            	finalResult.add(alarmTriggerMessage);
            }
        }
        finally {
            pm.close();
        }

        return finalResult;
    }
	
	/**
     * Get all AlarmTriggerMessage instances from the datastore that belong to
     * this StorageDevice.
     * @param storageDeviceKey
     * @param status: the alarmTriggerMessageStatus
     * @return All AlarmTriggerMessage instances that belong to the given StorageDevice
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static List<AlarmTriggerMessage> getAllAlarmTriggerMessagesFromStorageDevice(
			Key storageDeviceKey, AlarmTriggerMessageStatus status) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
    	Query query = pm.newQuery(AlarmTriggerMessage.class);
        query.setFilter("storageDevice == storageDeviceKeyParam && " +
        		"alarmTriggerMessageStatus == statusParam");
        query.declareParameters(Key.class.getName() + " storageDeviceKeyParam, " +
        		datastore.AlarmTriggerMessage.AlarmTriggerMessageStatus.class.getName() +
        		" statusParam");
        query.setOrdering("alarmTriggerMessageCreationDate asc");

        try {
            List<AlarmTriggerMessage> result = 
            		(List<AlarmTriggerMessage>) query.execute(storageDeviceKey, status);
            
            // Touch objects
            for (AlarmTriggerMessage alarmTriggerMessage : result) {
            	alarmTriggerMessage.getKey();
            }
            
            return result;
        }
        finally {
            pm.close();
        }
    }
	
    /**
     * Put AlarmTriggerMessage into datastore.
     * Stores the given AlarmTriggerMessage instance in the datastore for this
     * alarmTrigger. If the alarmTriggerCount exceeds the alarmTriggerMaxCount, then
     * this AlarmTriggerMessage will not be stored in the datastore.
     * Also returns the AlarmTrigger instance where this message was stored.
     * @param alarmTriggerKey
     *          : the key of the AlarmTrigger where the alarmTriggerMessage will be added
     * @param alarmTriggerMessage
     *          : the AlarmTriggerMessage instance to deviceModel
     * @return alarmTrigger
     * 			: the AlarmTrigger where this AlarmTriggerMessage was stored (for better performance)
     */
    public static AlarmTrigger putAlarmTriggerMessage(Key alarmTriggerKey, 
    		AlarmTriggerMessage alarmTriggerMessage) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            AlarmTrigger alarmTrigger = 
                    pm.getObjectById(AlarmTrigger.class, alarmTriggerKey);
            
            if (alarmTrigger.getAlarmTriggerCount() < alarmTrigger.getAlarmTriggerMaxCount()) {
	            tx.begin();
	            alarmTrigger.addAlarmTriggerMessage(alarmTriggerMessage);
	            alarmTrigger.increaseAlarmTriggerCount();
	            tx.commit();
	            
	            log.info("AlarmTriggerMessage \"" + alarmTriggerMessage.getKey() + 
	                    "\" stored successfully in datastore.");
            }
            
            return alarmTrigger;
        }
        finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }
     
    /**
    * Delete AlarmTriggerMessage from datastore.
    * Deletes the AlarmTriggerMessage corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the AlarmTriggerMessage instance to delete
    */
    public static void deleteAlarmTriggerMessage(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
        
        Transaction tx = pm.currentTransaction();
        try {
            AlarmTrigger alarmTrigger = 
            		pm.getObjectById(AlarmTrigger.class, key.getParent());
            AlarmTriggerMessage alarmTriggerMessage = 
            		pm.getObjectById(AlarmTriggerMessage.class, key);
            String alarmTriggerMessageContent = 
            		KeyFactory.keyToString(alarmTriggerMessage.getKey());
            tx.begin();
            alarmTrigger.removeAlarmTriggerMessage(alarmTriggerMessage);
            alarmTrigger.decreaseAlarmTriggerCount();
            tx.commit();
            log.info("AlarmTriggerMessage \"" + alarmTriggerMessageContent + 
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
     * Deletes all AlarmTriggerMessages from this Storage Device that have been expired
     * (older than alarmMessageEffectivePeriod)
     * @param storageDeviceKey: the storageDevice key
     * @param all: whether to delete all AlarmTriggerMessages or only expired ones
     */
	@SuppressWarnings("unchecked")
	public static void deleteAllExpiredAlarmTriggerMessages(Key storageDeviceKey, 
			boolean all) {
    	
    	Date now = new Date();
    	
    	PersistenceManager pm = PMF.get().getPersistenceManager();
    	
        Query query = pm.newQuery(AlarmTriggerMessage.class);
        
        // Check whether to delete all or not
        if (all) {
	        query.setFilter("storageDevice == storageDeviceKeyParam");
        }
        else {
        	query.setFilter("alarmTriggerMessageCreationDate < effectiveDateParam && " +
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
        	
        	List<AlarmTriggerMessage> alarmTriggerMessages = 
        			(List<AlarmTriggerMessage>) query.execute(
        					effectiveDate, storageDeviceKey);
        	for (AlarmTriggerMessage alarmTriggerMessage : alarmTriggerMessages) {
        		AlarmTrigger alarmTrigger = 
        				pm.getObjectById(AlarmTrigger.class, 
        						alarmTriggerMessage.getKey().getParent());
        		pm.deletePersistent(alarmTriggerMessage);
        		alarmTrigger.decreaseAlarmTriggerCount();
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
    * Update AlarmTriggerMessage attributes.
    * Update's the given AlarmTriggerMessage's attributes in the datastore.
    * @param key
    * 			: the key of the AlarmTriggerMessage whose attributes will be updated
    * @param alarmTriggerMessageStatus
    * 			: the new status to give to the AlarmTriggerMessage
    * @param alarmTriggerMessageNote
    * 			: the new note to give to the AlarmTriggerMessage
	* @throws MissingRequiredFieldsException 
    */
	public static void updateAlarmTriggerMessageAttributes(
			Key key,
			AlarmTriggerMessageStatus alarmTriggerMessageStatus,
			String alarmTriggerMessageNote) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			AlarmTriggerMessage alarmTriggerMessage = 
					pm.getObjectById(AlarmTriggerMessage.class, key);
			
			AlarmTrigger alarmTrigger = 
					pm.getObjectById(AlarmTrigger.class, key.getParent());
			
			tx.begin();
			
			alarmTriggerMessage.setAlarmTriggerMessageStatus(alarmTriggerMessageStatus);
			alarmTriggerMessage.setAlarmTriggerMessageNote(alarmTriggerMessageNote);
			
			if (alarmTriggerMessageStatus == AlarmTriggerMessageStatus.RESOLVED) {
				alarmTrigger.decreaseAlarmTriggerCount();
			}
			
			tx.commit();
			log.info("AlarmTriggerMessage \"" + alarmTriggerMessage.getKey() + 
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
