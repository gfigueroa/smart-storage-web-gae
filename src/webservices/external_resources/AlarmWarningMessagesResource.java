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
import webservices.datastore_simple.AlarmWarningMessageSimple;

import com.google.appengine.api.datastore.KeyFactory;

import datastore.AlarmWarning;
import datastore.AlarmWarningManager;
import datastore.AlarmWarningMessage;
import datastore.AlarmWarningMessage.AlarmWarningMessageStatus;
import datastore.AlarmWarningMessageManager;
import datastore.StorageDevice;
import datastore.StorageDeviceManager;

/**
 * This class represents the AlarmWarningMessage JDO Object.
 */

public class AlarmWarningMessagesResource extends ServerResource {

	/**
	 * Returns the AlarmWarningMessage table instance as a JSON object.
	 * @return The instance of the AlarmWarningMessage object in JSON format
	 */
    @Get("json")
    public ArrayList<AlarmWarningMessageSimple> toJson() {
        
    	String queryInfo = (String) getRequest().getAttributes()
                .get("queryinfo");
    	String[] queryParameters = queryInfo.split("&");

    	AlarmWarningMessageStatus status = null;
    	for (String queryParameter : queryParameters) {
    		int indexOfEqualsSign = queryParameter.indexOf('=');
    		String parameterName = queryParameter.substring(0, indexOfEqualsSign);
    		String parameterValue = queryParameter.substring(indexOfEqualsSign + 1);
    		
	    	if (parameterName.equalsIgnoreCase("status")) {
	    		String statusString = parameterValue;
	    		status = AlarmWarningMessage.getAlarmWarningMessageStatusFromString(
	    				statusString);
	    	}
    	}
    	
    	ArrayList<AlarmWarningMessageSimple> simpleAlarmWarningMessages = new ArrayList<>();
    	List<StorageDevice> storageDevices = StorageDeviceManager.getAllStorageDevices();
    	for (StorageDevice storageDevice : storageDevices) {
        	List<AlarmWarningMessage> alarmWarningMessages = 
        			AlarmWarningMessageManager.getAllAlarmWarningMessagesFromStorageDevice(
        					storageDevice.getKey(), status);
        	
        	for (AlarmWarningMessage alarmWarningMessage : alarmWarningMessages) {
        		
        		AlarmWarning alarmWarning = 
        				AlarmWarningManager.getAlarmWarning(
        						alarmWarningMessage.getKey().getParent());
        		
        		AlarmWarningMessageSimple alarmWarningMessageSimple = 
        				new AlarmWarningMessageSimple(
		        	    		KeyFactory.keyToString(alarmWarningMessage.getKey()),
		        	    		storageDevice.getStorageDeviceSerialNumber(),
		        	    		alarmWarning.getAlarmWarningMessage(),
		        	    		alarmWarningMessage.getAlarmWarningMessageDehumidifierMachine(),
		        	    		DateManager.printDateAsString(
		        	    				alarmWarningMessage.getAlarmWarningMessageCreationDate()));
        		
        		simpleAlarmWarningMessages.add(alarmWarningMessageSimple);
        	}
    	}
    	
    	return simpleAlarmWarningMessages;
    }

}
