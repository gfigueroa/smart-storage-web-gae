/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package webservices.external_resources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.KeyFactory;

import util.DateManager;
import webservices.datastore_simple.SensorReadingSimple;
import datastore.DeviceModel;
import datastore.DeviceModelManager;
import datastore.SensorInstance;
import datastore.SensorInstanceManager;
import datastore.SensorReading;
import datastore.SensorReadingManager;
import datastore.StorageDevice;
import datastore.StorageDeviceManager;

/**
 * This class represents the SensorReading
 * JDO Object which contains some version numbers
 * required by the mobile app.
 */

public class SensorDataResource extends ServerResource {

	/**
	 * Returns the list of SensorReading table instances as a JSON object.
	 * @return The instances of the SensorReading object in JSON format
	 */
    @Get("json")
    public ArrayList<SensorReadingSimple> toJson() {
        
    	Date now = new Date();
    	
    	String queryInfo = (String) getRequest().getAttributes()
                .get("queryinfo");
    	String[] queryParameters = queryInfo.split("&");

    	String serialNumber = "";
    	boolean getLast = false;
    	long limit = 1000; // 1000 is limit (or if parameter is null)
    	Date dateFrom = null;
    	Date dateTo = null;
    	for (String queryParameter : queryParameters) {
    		int indexOfEqualsSign = queryParameter.indexOf('=');
    		String parameterName = queryParameter.substring(0, indexOfEqualsSign);
    		String parameterValue = queryParameter.substring(indexOfEqualsSign + 1);
    		
	    	if (parameterName.equalsIgnoreCase("serialNumber")) {
	    		serialNumber = parameterValue;
	    	}
	    	else if (parameterName.equalsIgnoreCase("getLast")) {
	    		String getLastString = parameterValue;
	    		getLast = Boolean.parseBoolean(getLastString);
	    	}
	    	else if (parameterName.equalsIgnoreCase("limit")) {
	    		String limitString = parameterValue;
	    		limit = Long.parseLong(limitString);
	    	}
	    	else if (parameterName.equalsIgnoreCase("dateFrom")) {
	    		String dateFromString = parameterValue;
	    		dateFrom = DateManager.getSimpleDateValueTaiwan(dateFromString);
	    	}
	    	else if (parameterName.equalsIgnoreCase("dateTo")) {
	    		String dateToString = parameterValue;
	    		dateTo = DateManager.getSimpleDateValueTaiwan(dateToString);
	    	}
    	}
    	
    	StorageDevice storageDevice = 
    			StorageDeviceManager.getStorageDevice(serialNumber);
    	
    	DeviceModel deviceModel =
    			DeviceModelManager.getDeviceModel(storageDevice.getDeviceModel());
    	
    	List<SensorReading> sensorReadings;
    	// Check whether to get last SensorReadings or all
    	if (getLast) {
    		sensorReadings = 
    				SensorReadingManager.getLastSensorReadingsFromStorageDevice(
    						storageDevice.getKey());
    	}
    	else {
    		if (dateFrom == null && dateTo == null) {
	    		sensorReadings = SensorReadingManager.getAllSensorReadingsFromStorageDevice(
	    				storageDevice.getKey(), limit);
    		}
    		else {
    			sensorReadings = SensorReadingManager.getSensorReadingsFromStorageDevice(
    					storageDevice.getKey(), limit, dateFrom, dateTo);
    		}
    	}
    	
    	ArrayList<SensorReadingSimple> sensorReadingsSimple =
    			new ArrayList<>();
    	for (SensorReading sensorReading : sensorReadings) {
    		
    		SensorInstance sensorInstance = 
    				SensorInstanceManager.getSensorInstance(
    						sensorReading.getKey().getParent());
    		
    		String sensorReadingValue = sensorReading.getSensorReadingValue();
    		// Don't display expired SensorReadings
    		if (getLast && !sensorInstance.getSensorInstanceLabel().startsWith("Door") &&
    				deviceModel.lastSensorDataUploadIsExpired(now, sensorReading)) {
    			sensorReadingValue = "-";
    		}
    		
    		SensorReadingSimple sensorReadingSimple =
    				new SensorReadingSimple(
    						KeyFactory.keyToString(sensorReading.getKey()),
    						KeyFactory.keyToString(storageDevice.getKey()),
    			    		KeyFactory.keyToString(sensorInstance.getKey()),
    			    		sensorInstance.getSensorInstanceLabel(),
    			    		sensorReadingValue,
    						DateManager.printDateAsString(
    								sensorReading.getSensorReadingTime())
    						);
    		sensorReadingsSimple.add(sensorReadingSimple);
    	}

    	return sensorReadingsSimple;
    }

}
