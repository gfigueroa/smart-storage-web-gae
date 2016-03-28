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
import webservices.datastore_simple.DeviceModelSimple;
import webservices.datastore_simple.DeviceModelSimple.DeviceModelDoorSimple;
import webservices.datastore_simple.DeviceModelSimple.DeviceModelPartitionSimple;

import com.google.appengine.api.datastore.KeyFactory;

import datastore.Customer;
import datastore.CustomerManager;
import datastore.CustomerUser;
import datastore.CustomerUserManager;
import datastore.DeviceModel;
import datastore.DeviceModelDoor;
import datastore.DeviceModelDoorManager;
import datastore.DeviceModelManager;
import datastore.DeviceModelPartition;
import datastore.DeviceModelPartitionManager;
import datastore.DeviceServiceType;
import datastore.DeviceServiceTypeManager;
import datastore.User;
import datastore.UserManager;

/**
 * This class represents the DeviceModel JDO Object.
 */

public class DeviceModelsResource extends ServerResource {

	/**
	 * Returns the DeviceModel table instance as a JSON object.
	 * @return The instance of the DeviceModel object in JSON format
	 */
    @Get("json")
    public ArrayList<DeviceModelSimple> toJson() {
        
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
    	List<DeviceModel> deviceModels = null;
    	// Check User type
    	switch (user.getUserType()) {
    		case ADMINISTRATOR:
    			deviceModels = DeviceModelManager.getAllDeviceModels();
    			break;
    		case CUSTOMER:
    			Customer customer = CustomerManager.getCustomer(user);
    			deviceModels = DeviceModelManager.getAllDeviceModelsFromCustomer(customer.getKey());
    			break;
    		case CUSTOMER_USER:
    			CustomerUser customerUser = CustomerUserManager.getCustomerUser(user);
    			deviceModels = DeviceModelManager.getAllDeviceModelsFromCustomerUser(customerUser.getKey());
    			break;
    	}
    	
    	ArrayList<DeviceModelSimple> simpleDeviceModels = new ArrayList<>();
    	if (deviceModels != null) {
    		for (DeviceModel deviceModel : deviceModels) {
    			
    			// Get DeviceModelDoors
    			List<DeviceModelDoor> deviceModelDoors = 
    					DeviceModelDoorManager.getAllDeviceModelDoorsFromDeviceModel(deviceModel.getKey());
    			ArrayList<DeviceModelDoorSimple> simpleDeviceModelDoors = new ArrayList<>();
    			for (DeviceModelDoor deviceModelDoor : deviceModelDoors) {
    				
    				// Get DeviceModelPartitions
    				List<DeviceModelPartition> deviceModelPartitions =
    						DeviceModelPartitionManager.getAllDeviceModelPartitionsFromDeviceModelDoor(
    								deviceModelDoor.getKey());
    				ArrayList<DeviceModelPartitionSimple> simpleDeviceModelPartitions = new ArrayList<>();
    				for (DeviceModelPartition deviceModelPartition : deviceModelPartitions) {
    					DeviceModelPartitionSimple deviceModelPartitionSimple = 
    							new DeviceModelPartitionSimple(
    									KeyFactory.keyToString(deviceModelPartition.getKey()),
    									deviceModelPartition.getDeviceModelPartitionName()
    									);
    					simpleDeviceModelPartitions.add(deviceModelPartitionSimple);
    				}
    				
    				DeviceModelDoorSimple deviceModelDoorSimple =
    						new DeviceModelDoorSimple(
    								KeyFactory.keyToString(deviceModelDoor.getKey()),
    								deviceModelDoor.getDeviceModelDoorNumber(),
    								simpleDeviceModelPartitions
    								);
    				simpleDeviceModelDoors.add(deviceModelDoorSimple);
    			}
    			
    			// Get DeviceServiceType
    			DeviceServiceType deviceServiceType = 
    					DeviceServiceTypeManager.getDeviceServiceType(deviceModel.getDeviceServiceType());
    			
    			DeviceModelSimple deviceModelSimple =
    					new DeviceModelSimple(
    							KeyFactory.keyToString(deviceModel.getKey()),
    							deviceServiceType.getDeviceServiceTypeName(),
    							deviceModel.getDeviceModelName(),
    							deviceModel.getDeviceModelDescription() != null ?
    									deviceModel.getDeviceModelDescription() : "",
    							DateManager.printDateAsString(deviceModel.getDeviceModelDesignTime()),
    							deviceModel.getTemp1Hum1(),
    							deviceModel.getTemp2Hum2(),
    							deviceModel.getDoorOpenClose(),
    							deviceModel.getCO(),
    							deviceModel.getCO2(),
    							deviceModel.getFlux(),
    							deviceModel.getInfrared(),
    							deviceModel.getImageUpload(),
    							deviceModel.getAlcohol(),
    							deviceModel.getElectricCurrent(),
    							deviceModel.getAtmosphericPressure(),
    							DateManager.printDateAsString(deviceModel.getDeviceModelCreationDate()),
    							DateManager.printDateAsString(deviceModel.getDeviceModelModificationDate()),
    							simpleDeviceModelDoors
    							);
    			simpleDeviceModels.add(deviceModelSimple);
    		}
    	}
    	
    	
    	return simpleDeviceModels;
    }

}
