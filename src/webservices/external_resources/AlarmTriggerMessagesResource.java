/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package webservices.external_resources;

import java.util.ArrayList;
import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import util.DateManager;
import webservices.datastore_simple.AlarmTriggerMessageSimple;
import webservices.datastore_simple.SensorReadingSimple;

import com.google.appengine.api.datastore.KeyFactory;

import datastore.AlarmTrigger;
import datastore.AlarmTriggerManager;
import datastore.AlarmTriggerMessage;
import datastore.AlarmTriggerMessage.AlarmTriggerMessageStatus;
import datastore.AlarmTriggerMessageManager;
import datastore.SensorInstance;
import datastore.SensorInstanceManager;
import datastore.SensorReading;
import datastore.SensorReadingManager;
import datastore.StorageDevice;
import datastore.StorageDeviceManager;

/**
 * This class represents the AlarmTriggerMessage JDO Object.
 */

public class AlarmTriggerMessagesResource extends ServerResource {

	/**
	 * Returns the AlarmTriggerMessage table instance as a JSON object.
	 * @return The instance of the AlarmTriggerMessage object in JSON format
	 */
    @Get("json")
    public ArrayList<AlarmTriggerMessageSimple> toJson() {
        
    	String queryInfo = (String) getRequest().getAttributes()
                .get("queryinfo");
    	String[] queryParameters = queryInfo.split("&");

    	AlarmTriggerMessageStatus status = null;
    	for (String queryParameter : queryParameters) {
    		int indexOfEqualsSign = queryParameter.indexOf('=');
    		String parameterName = queryParameter.substring(0, indexOfEqualsSign);
    		String parameterValue = queryParameter.substring(indexOfEqualsSign + 1);
    		
	    	if (parameterName.equalsIgnoreCase("status")) {
	    		String statusString = parameterValue;
	    		status = AlarmTriggerMessage.getAlarmTriggerMessageStatusFromString(
	    				statusString);
	    	}
    	}
    	
    	ArrayList<AlarmTriggerMessageSimple> simpleAlarmTriggerMessages = new ArrayList<>();
    	List<StorageDevice> storageDevices = StorageDeviceManager.getAllStorageDevices();
    	for (StorageDevice storageDevice : storageDevices) {
        	List<AlarmTriggerMessage> alarmTriggerMessages = 
        			AlarmTriggerMessageManager.getAllAlarmTriggerMessagesFromStorageDevice(
        					storageDevice.getKey(), status);
        	
        	for (AlarmTriggerMessage alarmTriggerMessage : alarmTriggerMessages) {
        		
        		AlarmTrigger alarmTrigger = 
        				AlarmTriggerManager.getAlarmTrigger(
        						alarmTriggerMessage.getKey().getParent());
        		
        		// SensorReading
        		SensorReadingSimple sensorReadingSimple = null;
        		if (alarmTriggerMessage.getSensorReading() != null) {
            		SensorReading sensorReading = 
            				SensorReadingManager.getSensorReading(alarmTriggerMessage.getSensorReading());
        			
        			SensorInstance sensorInstance = 
            				SensorInstanceManager.getSensorInstance(
            						sensorReading.getKey().getParent());
        			
        			sensorReadingSimple =
            				new SensorReadingSimple(
            						KeyFactory.keyToString(sensorReading.getKey()),
            						KeyFactory.keyToString(storageDevice.getKey()),
            			    		KeyFactory.keyToString(sensorInstance.getKey()),
            			    		sensorInstance.getSensorInstanceLabel(),
            			    		sensorReading.getSensorReadingValue(),
            						DateManager.printDateAsString(
            								sensorReading.getSensorReadingTime())
            						);
        		}
        		
        		AlarmTriggerMessageSimple alarmTriggerMessageSimple = 
        				new AlarmTriggerMessageSimple(
		        	    		KeyFactory.keyToString(alarmTriggerMessage.getKey()),
		        	    		storageDevice.getStorageDeviceSerialNumber(),
		        	    		AlarmTriggerManager.getAlarmTriggerString(alarmTrigger.getKey()),
		        	    		DateManager.printDateAsString(
		        	    				alarmTriggerMessage.getAlarmTriggerMessageCreationDate()),
		        	    		sensorReadingSimple		
        						);
        		
        		simpleAlarmTriggerMessages.add(alarmTriggerMessageSimple);
        	}
    	}
    	
    	return simpleAlarmTriggerMessages;
    }

}
