/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package webservices.external_resources;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import webservices.datastore_simple.StorageDeviceSimple;
import webservices.datastore_simple.StorageDeviceSimple.StorageDeviceInformation;

import com.google.appengine.api.datastore.KeyFactory;

import datastore.Country;
import datastore.CountryManager;
import datastore.Customer;
import datastore.CustomerManager;
import datastore.CustomerUser;
import datastore.CustomerUserManager;
import datastore.DeviceModel;
import datastore.DeviceModelManager;
import datastore.Region;
import datastore.RegionManager;
import datastore.SensorInstance;
import datastore.SensorInstanceManager;
import datastore.SensorReading;
import datastore.SensorReadingManager;
import datastore.StorageDevice;
import datastore.StorageDeviceManager;
import datastore.User;
import datastore.UserManager;

/**
 * This class represents the StorageDevice JDO Object.
 */

public class StorageDevicesResource extends ServerResource {

	/**
	 * Returns the StorageDevice table instance as a JSON object.
	 * @return The instance of the StorageDevice object in JSON format
	 */
    @Get("json")
    public ArrayList<StorageDeviceSimple> toJson() {
    	
    	Date now = new Date();
        
    	String queryInfo = (String) getRequest().getAttributes()
                .get("queryinfo");
    	String[] queryParameters = queryInfo.split("&");
    	
    	String userEmail = "";
    	for (String queryParameter : queryParameters) {
    		int indexOfEqualsSign = queryParameter.indexOf('=');
    		String parameterName = queryParameter.substring(0, indexOfEqualsSign);
    		String parameterValue = queryParameter.substring(indexOfEqualsSign + 1);
    		
	    	if (parameterName.equalsIgnoreCase("userEmail")) {
	    		userEmail = parameterValue;
	    	}
    	}

    	User user = UserManager.getUser(userEmail);
    	List<StorageDevice> storageDevices = null;
    	HashMap<Customer, List<StorageDevice>> storageDeviceHashMap = new HashMap<>();
    	Customer customer = null;
    	// Check User type
    	switch (user.getUserType()) {
    		case ADMINISTRATOR:
    			List<Customer> customers = CustomerManager.getAllCustomers();
    			for (Customer customer1 : customers) {
    				storageDevices = 
        					StorageDeviceManager.getAllStorageDevicesFromCustomer(customer1.getKey());
    				storageDeviceHashMap.put(customer1, storageDevices);
    			}
    			break;
    		case CUSTOMER:
    			customer = CustomerManager.getCustomer(user);
    			storageDevices = 
    					StorageDeviceManager.getAllStorageDevicesFromCustomer(customer.getKey());
    			break;
    		case CUSTOMER_USER:
    			CustomerUser customerUser = CustomerUserManager.getCustomerUser(user);
    			customer = CustomerManager.getCustomer(customerUser.getKey().getParent());
    			storageDevices = 
    					StorageDeviceManager.getAllStorageDevicesFromCustomerUser(customerUser.getKey());
    			break;
    	}

    	ArrayList<StorageDeviceSimple> simpleStorageDevices = new ArrayList<>();
		ArrayList<StorageDeviceInformation> storageDeviceInformationList =
				new ArrayList<>();
		if (user.getUserType() == User.UserType.ADMINISTRATOR) {
			for (Customer customer1 : storageDeviceHashMap.keySet()) {
				
				storageDevices = storageDeviceHashMap.get(customer1);
	    		for (StorageDevice storageDevice : storageDevices) {
	    			
	    			DeviceModel deviceModel = 
	    					DeviceModelManager.getDeviceModel(storageDevice.getDeviceModel());
	    			
	    			// Get SensorReadings
	    			List<SensorInstance> sensorInstances = 
	    					SensorInstanceManager.getAllSensorInstancesFromStorageDevice(
	    							storageDevice.getKey());
					SensorReading temp1Reading = null;
					SensorReading hum1Reading = null;
					SensorReading temp2Reading = null;
					SensorReading hum2Reading = null;
					SensorReading door1Reading = null;
					for (SensorInstance sensorInstance : sensorInstances) {
						if (sensorInstance.getSensorInstanceLabel().equals("Temperature_1")) {
							temp1Reading = 
									SensorReadingManager.getLastSensorReadingFromSensorInstance(
											sensorInstance.getKey(), true);
						}
						else if (sensorInstance.getSensorInstanceLabel().equals("Humidity_1")) {
							hum1Reading = 
									SensorReadingManager.getLastSensorReadingFromSensorInstance(
											sensorInstance.getKey(), true);
						}
						else if (sensorInstance.getSensorInstanceLabel().equals("Temperature_2")) {
							temp2Reading = 
									SensorReadingManager.getLastSensorReadingFromSensorInstance(
											sensorInstance.getKey(), true);
						}
						else if (sensorInstance.getSensorInstanceLabel().equals("Humidity_2")) {
							hum2Reading = 
									SensorReadingManager.getLastSensorReadingFromSensorInstance(
											sensorInstance.getKey(), true);
						}
						else if (sensorInstance.getSensorInstanceLabel().equals("Door_1")) {
							door1Reading = 
									SensorReadingManager.getLastSensorReadingFromSensorInstance(
											sensorInstance.getKey(), true);
						}
					}
					
					// Don't display null or expired SensorReadings
					String temp1ReadingValue = "-";
					String hum1ReadingValue = "-";
					String temp2ReadingValue = "-";
					String hum2ReadingValue = "-";
					String door1ReadingValue = "-";
		    		if (temp1Reading != null &&
		    				!deviceModel.lastSensorDataUploadIsExpired(now, temp1Reading)) {
		    			temp1ReadingValue = temp1Reading.getSensorReadingValue();
		    		}
		    		if (hum1Reading != null &&
		    				!deviceModel.lastSensorDataUploadIsExpired(now, hum1Reading)) {
		    			hum1ReadingValue = hum1Reading.getSensorReadingValue();
		    		}
		    		if (temp2Reading != null &&
		    				!deviceModel.lastSensorDataUploadIsExpired(now, temp2Reading)) {
		    			temp2ReadingValue = temp2Reading.getSensorReadingValue();
		    		}
		    		if (hum2Reading != null &&
		    				!deviceModel.lastSensorDataUploadIsExpired(now, hum2Reading)) {
		    			hum2ReadingValue = hum2Reading.getSensorReadingValue();
		    		}
		    		if (door1Reading != null) {
		    			door1ReadingValue = door1Reading.getSensorReadingValue();
		    		}
	    			
	    			// Get StorageDeviceInformation
	    			StorageDeviceInformation storageDeviceInformation = new StorageDeviceInformation(
	    					KeyFactory.keyToString(storageDevice.getDeviceModel()),
	    					KeyFactory.keyToString(storageDevice.getKey()),
	    					storageDevice.getStorageDeviceSerialNumber(),
	    					storageDevice.getStorageDeviceNickname(),
	    					temp1ReadingValue,
	    					hum1ReadingValue,
	    					temp2ReadingValue,
	    					hum2ReadingValue,
	    					door1ReadingValue
	    					);
	    			
	    			storageDeviceInformationList.add(storageDeviceInformation);
	    		}
	    		
	    		// Get Customer information
	    		Region region = RegionManager.getRegion(customer1.getRegion());
	    		Country country = CountryManager.getCountry(customer1.getRegion().getParent());
	    		
	    		StorageDeviceSimple storageDeviceSimple = new StorageDeviceSimple(
	    				customer1.getUser().getUserEmail().getEmail(),
	    				customer1.getCustomerName(),
	    				region.getRegionName(),
	    				country.getCountryName(),
	    				storageDeviceInformationList
	    				);
	    		simpleStorageDevices.add(storageDeviceSimple);
			}
		}
		else {
			for (StorageDevice storageDevice : storageDevices) {
    			
    			// Get SensorReadings
    			List<SensorInstance> sensorInstances = 
    					SensorInstanceManager.getAllSensorInstancesFromStorageDevice(
    							storageDevice.getKey());
				SensorReading temp1Reading = null;
				SensorReading hum1Reading = null;
				SensorReading temp2Reading = null;
				SensorReading hum2Reading = null;
				SensorReading door1Reading = null;
				for (SensorInstance sensorInstance : sensorInstances) {
					if (sensorInstance.getSensorInstanceLabel().equals("Temperature_1")) {
						temp1Reading = 
								SensorReadingManager.getLastSensorReadingFromSensorInstance(
										sensorInstance.getKey(), true);
					}
					else if (sensorInstance.getSensorInstanceLabel().equals("Humidity_1")) {
						hum1Reading = 
								SensorReadingManager.getLastSensorReadingFromSensorInstance(
										sensorInstance.getKey(), true);
					}
					else if (sensorInstance.getSensorInstanceLabel().equals("Temperature_2")) {
						temp2Reading = 
								SensorReadingManager.getLastSensorReadingFromSensorInstance(
										sensorInstance.getKey(), true);
					}
					else if (sensorInstance.getSensorInstanceLabel().equals("Humidity_2")) {
						hum2Reading = 
								SensorReadingManager.getLastSensorReadingFromSensorInstance(
										sensorInstance.getKey(), true);
					}
					else if (sensorInstance.getSensorInstanceLabel().equals("Door_1")) {
						door1Reading = 
								SensorReadingManager.getLastSensorReadingFromSensorInstance(
										sensorInstance.getKey(), true);
					}
				}
    			
    			// Get StorageDeviceInformation
    			StorageDeviceInformation storageDeviceInformation = new StorageDeviceInformation(
    					KeyFactory.keyToString(storageDevice.getDeviceModel()),
    					KeyFactory.keyToString(storageDevice.getKey()),
    					storageDevice.getStorageDeviceSerialNumber(),
    					storageDevice.getStorageDeviceNickname(),
    					temp1Reading != null ? temp1Reading.getSensorReadingValue() : "",
    					hum1Reading != null ? hum1Reading.getSensorReadingValue() : "",
    					temp2Reading != null ? temp2Reading.getSensorReadingValue() : "",
    					hum2Reading != null ? hum2Reading.getSensorReadingValue() : "",
    					door1Reading != null ? door1Reading.getSensorReadingValue() : ""
    					);
    			
    			storageDeviceInformationList.add(storageDeviceInformation);
    		}
    		
    		// Get Customer information
    		Region region = RegionManager.getRegion(customer.getRegion());
    		Country country = CountryManager.getCountry(customer.getRegion().getParent());
    		
    		StorageDeviceSimple storageDeviceSimple = new StorageDeviceSimple(
    				customer.getUser().getUserEmail().getEmail(),
    				customer.getCustomerName(),
    				region.getRegionName(),
    				country.getCountryName(),
    				storageDeviceInformationList
    				);
    		simpleStorageDevices.add(storageDeviceSimple);
		}
    	
    	
    	return simpleStorageDevices;
    }

}
