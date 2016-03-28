/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.DateManager;
import util.EmailManager;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import datastore.AlarmTrigger;
import datastore.AlarmTriggerManager;
import datastore.AlarmTriggerMessage;
import datastore.AlarmTriggerMessageManager;
import datastore.AlarmWarningMessageManager;
import datastore.SensorInstance;
import datastore.SensorInstanceManager;
import datastore.SensorReadingManager;
import datastore.StorageDevice;
import datastore.StorageDeviceManager;

/**
 * This servlet class is used for serving Cron requests.
 * 
 */

@SuppressWarnings("serial")
public class CronServlet extends HttpServlet {

    private static final Logger log = 
        Logger.getLogger(CronServlet.class.getName());
    
    // JSP file locations
    private static final String thisServlet = "/cron";
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
    	
	    // Lets check the action and parameters returned
	    String action = req.getParameter("action");
	    
	    try {
		    // Testing
		    if (action.equalsIgnoreCase("testing")) {
		    	log.log(Level.INFO, "Cron test - every 24 hours");
		    }
		    else if (action.equalsIgnoreCase("deleteSensorData")) {
		    	log.log(Level.INFO, "Daily maintenance. Deleting old sensor data...");
		    	deleteSensorData();
		    }
		    else if (action.equalsIgnoreCase("deleteAlarmMessages")) {
		    	log.log(Level.INFO, "Daily maintenance. Deleting old alarm messages...");
		    	deleteAlarmMessages(false);
		    }
		    else if (action.equalsIgnoreCase("aperiodicAlarmTriggers")) {
		    	log.log(Level.CONFIG, "Checking aperiodic AlarmTriggers...");
		    	checkAperiodicAlarmTriggers();
		    }
		    // No more choices
	  		else {
	  			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
	  			return;
	  		}
	    }
        catch (Exception ex) {
            log.log(Level.SEVERE, ex.toString());
            ex.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            		"Internal server error.");
            return;
        }
	}
    
    /**
     * Daily maintenance to delete old sensor data.
     * Deletes all sensor data from Storage Devices that have been expired
     * (older than today minus sensorDataEffectivePeriod)
     */
    public static void deleteSensorData() {
    	List<StorageDevice> storageDevices = StorageDeviceManager.getAllStorageDevices();
    	int count = 0;
    	for (StorageDevice storageDevice : storageDevices) {
    		SensorReadingManager.deleteAllExpiredSensorReadings(storageDevice.getKey());
    		count++;
    	}
		log.log(Level.INFO, "Deleted all expired sensor data from " + count + " storage devices");
    }
    
    /**
     * Daily maintenance to delete old sensor data.
     * Deletes all sensor data from the given Storage Devices that have been expired
     * (older than today minus sensorDataEffectivePeriod)
     * @param: storageDeviceKey:
     * 		The key of the StorageDevice whose expired data will be deleted
     */
    public static void deleteSensorData(Key storageDeviceKey) {

    	SensorReadingManager.deleteAllExpiredSensorReadings(storageDeviceKey);

		log.log(Level.INFO, "Deleted all expired sensor data from storage device " +
				KeyFactory.keyToString(storageDeviceKey));
    }
    
    /**
     * Daily maintenance to delete old alarm (warning/trigger) messages.
     * Deletes all alarm (warning/trigger) messages from Storage Devices that have been expired
     * (older than today)
     * @param all: whether to delete all AlarmWarningMessages or only expired ones
     */
    public static void deleteAlarmMessages(boolean all) {
    	List<StorageDevice> storageDevices = StorageDeviceManager.getAllStorageDevices();
    	int count = 0;
    	for (StorageDevice storageDevice : storageDevices) {
    		AlarmWarningMessageManager.deleteAllExpiredAlarmWarningMessages(
    				storageDevice.getKey(), all);
    		AlarmTriggerMessageManager.deleteAllExpiredAlarmTriggerMessages(
    				storageDevice.getKey(), all);
    		count++;
    	}
    	
    	if (all) {
    		log.log(Level.INFO, "Deleted all AlarmMessages from " + count + 
    				" storage devices");
    	}
    	else {
    		log.log(Level.INFO, "Deleted expired AlarmMessages from " + count + 
    				" storage devices");
    	}
    }
    
    /**
     * Checks aperiodic (door open/close) AlarmTriggers
     * If any AlarmTrigger is activated, then the corresponding 
     * AlarmTriggerMessages are created.
     * @throws MessagingException 
     * @throws UnsupportedEncodingException 
     */
    private void checkAperiodicAlarmTriggers() 
    		throws MessagingException, UnsupportedEncodingException {
    	
    	HashMap<AlarmTriggerMessage, Key> alarmTriggerMessages = 
    			AlarmTriggerManager.checkStorageDeviceDoorAlarmTriggers();
    	for (AlarmTriggerMessage alarmTriggerMessage : alarmTriggerMessages.keySet()) {
    		Key alarmTriggerKey = alarmTriggerMessages.get(alarmTriggerMessage);
    		AlarmTrigger alarmTrigger = AlarmTriggerMessageManager.putAlarmTriggerMessage(
    				alarmTriggerKey, alarmTriggerMessage);
    		
    		// Send AlarmTriggerMessage emails (only send once)
    		if (alarmTrigger.getAlarmTriggerCount() <= 1 && alarmTrigger.getAlarmTriggerMaxCount() > 1) {
    			
    			StorageDevice storageDevice = 
    					StorageDeviceManager.getStorageDevice(alarmTriggerKey.getParent().getParent());
    			SensorInstance sensorInstance = 
    					SensorInstanceManager.getSensorInstance(alarmTriggerKey.getParent());
    			
    			String subject = 
    					"Alarm triggered in Storage Device \"" + storageDevice.getStorageDeviceSerialNumber() + 
    					"\" (" + sensorInstance.getSensorInstanceLabel() + ")";
    			
    			String body = subject + "\n";
    			body += DateManager.printDateAsString(
    					alarmTriggerMessage.getAlarmTriggerMessageCreationDate()) + "\n";
    			body += AlarmTriggerManager.getAlarmTriggerString(alarmTriggerKey) + "\n";
    			
    			EmailManager.sendAlarmMessageEmails(subject, body);
    		}
    	}

    	log.log(Level.INFO, "Created " + alarmTriggerMessages.size() + " AlarmTriggerMessages");
    }
}