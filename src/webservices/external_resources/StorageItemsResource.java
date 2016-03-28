/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package webservices.external_resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import util.DateManager;
import webservices.datastore_simple.StorageItemSimple;
import webservices.datastore_simple.StorageItemSimple.StorageItemInformation;
import webservices.datastore_simple.StorageItemSimple.StorageItemInstanceInformation;

import com.google.appengine.api.datastore.KeyFactory;

import datastore.Customer;
import datastore.CustomerManager;
import datastore.CustomerUser;
import datastore.CustomerUserManager;
import datastore.DeviceModelDoor;
import datastore.DeviceModelDoorManager;
import datastore.DeviceModelPartition;
import datastore.DeviceModelPartitionManager;
import datastore.StorageDevice;
import datastore.StorageDeviceDoor;
import datastore.StorageDeviceDoorManager;
import datastore.StorageDeviceManager;
import datastore.StorageDevicePartition;
import datastore.StorageDevicePartitionManager;
import datastore.StorageItem;
import datastore.StorageItemInstance;
import datastore.StorageItemInstanceManager;
import datastore.StorageItemManager;
import datastore.StorageItemTransaction;
import datastore.StorageItemTransactionManager;
import datastore.User;
import datastore.UserManager;

/**
 * This class represents the StorageItem JDO Object.
 */

public class StorageItemsResource extends ServerResource {

	/**
	 * Returns the StorageItem table instance as a JSON object.
	 * @return The instance of the StorageItem object in JSON format
	 */
    @Get("json")
    public ArrayList<StorageItemSimple> toJson() {
        
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
    	List<StorageItem> storageItems = null;
    	HashMap<Customer, List<StorageItem>> storageItemHashMap = new HashMap<>();
    	Customer customer = null;
    	// Check User type
    	switch (user.getUserType()) {
    		case ADMINISTRATOR:
    			List<Customer> customers = CustomerManager.getAllCustomers();
    			for (Customer customer1 : customers) {
    				storageItems = 
        					StorageItemManager.getAllStorageItemsFromCustomer(customer1.getKey());
    				storageItemHashMap.put(customer1, storageItems);
    			}
    			break;
    		case CUSTOMER:
    			customer = CustomerManager.getCustomer(user);
    			storageItems = 
    					StorageItemManager.getAllStorageItemsFromCustomer(customer.getKey());
    			storageItemHashMap.put(customer, storageItems);
    			break;
    		case CUSTOMER_USER:
    			CustomerUser customerUser = CustomerUserManager.getCustomerUser(user);
    			customer = CustomerManager.getCustomer(customerUser.getKey().getParent());
    			storageItems = 
    					StorageItemManager.getAllStorageItemsFromCustomer(customer.getKey());
    			storageItemHashMap.put(customer, storageItems);
    			break;
    	}

    	ArrayList<StorageItemSimple> simpleStorageItems = new ArrayList<>();
		ArrayList<StorageItemInformation> storageItemInformationList =
				new ArrayList<>();

		for (Customer customer1 : storageItemHashMap.keySet()) {
			
			storageItems = storageItemHashMap.get(customer1);
    		for (StorageItem storageItem : storageItems) {

    			// Get StorageItemInstances
    			List<StorageItemInstance> storageItemInstances =
    					StorageItemInstanceManager.getAllStorageItemInstancesFromStorageItem(
    							storageItem.getKey());
    			ArrayList<StorageItemInstanceInformation> storageItemInstanceInformationList = 
    					new ArrayList<>();
    			for (StorageItemInstance storageItemInstance : storageItemInstances) {
    				
    				// Get owner
    				String storageItemInstanceOwner = "";
    				if (storageItemInstance.getStorageItemInstanceOwner() != null) {
    					CustomerUser customerUser = 
    							CustomerUserManager.getCustomerUser(
    									storageItemInstance.getStorageItemInstanceOwner());
    					storageItemInstanceOwner = customerUser.getCustomerUserName();
    				}
    				
    				// Get last transaction information
    				StorageItemTransaction lastTransaction = 
    						StorageItemTransactionManager.getLastStorageItemTransactionFromStorageItemInstance(
    								storageItemInstance.getKey());
    				String transactionTime = "";
    				String transactionPerformedBy = "";
    				String transactionAction = "";
    				String storageDeviceSerialNumber = "";
    				Integer storageDeviceDoorNumber = 0;
    				String storageDevicePartitionName = "";
    				if (lastTransaction != null) {
    					transactionTime = DateManager.printDateAsString(
    							lastTransaction.getStorageItemTransactionCreationDate());
    					CustomerUser customerUser = 
    							CustomerUserManager.getCustomerUser(
    									lastTransaction.getStorageItemTransactionPerformedBy());
    					transactionPerformedBy = customerUser.getCustomerUserName();
    					transactionAction = lastTransaction.getStorageItemTransactionAction().toString();
    					
    					if (lastTransaction.getStorageDevicePartition() != null) {
    						StorageDevicePartition storageDevicePartition = 
    								StorageDevicePartitionManager.getStorageDevicePartition(
    										lastTransaction.getStorageDevicePartition());
    						DeviceModelPartition deviceModelPartition = 
    								DeviceModelPartitionManager.getDeviceModelPartition(
    										storageDevicePartition.getDeviceModelPartition());
    						storageDevicePartitionName = 
    								deviceModelPartition.getDeviceModelPartitionName();
    						
    						StorageDeviceDoor storageDeviceDoor = 
    								StorageDeviceDoorManager.getStorageDeviceDoor(
    										storageDevicePartition.getKey().getParent());
    						DeviceModelDoor deviceModelDoor =
    								DeviceModelDoorManager.getDeviceModelDoor(
    										storageDeviceDoor.getDeviceModelDoor());
    						storageDeviceDoorNumber = deviceModelDoor.getDeviceModelDoorNumber();
    						
    						StorageDevice storageDevice = 
    								StorageDeviceManager.getStorageDevice(
    										storageDeviceDoor.getKey().getParent());
    						storageDeviceSerialNumber = storageDevice.getStorageDeviceSerialNumber();
    					}
    				}

    				StorageItemInstanceInformation storageItemInstanceInformation =
    						new StorageItemInstanceInformation(
    								KeyFactory.keyToString(storageItemInstance.getKey()),
    								storageItemInstanceOwner,
    								storageItemInstance.getStorageItemInstanceSerialNumber(),
    								storageItemInstance.getStorageItemInstanceLabel(),
    								storageItemInstance.getStorageItemInstanceExpirationTime() != null ?
    										DateManager.printDateAsString(
    												storageItemInstance.getStorageItemInstanceExpirationTime()) : 
    														"",
    								storageItemInstance.getStorageItemInstanceWorksheet() != null ? 
    										storageItemInstance.getStorageItemInstanceWorksheet() : "",
    								transactionTime,
    								transactionPerformedBy,
    								transactionAction,
    								storageDeviceSerialNumber,
    								storageDeviceDoorNumber,
    								storageDevicePartitionName
    								);
    				storageItemInstanceInformationList.add(storageItemInstanceInformation);
    			}

    			// Get StorageItemInformation
    			StorageItemInformation storageItemInformation = 
    					new StorageItemInformation(
		    					KeyFactory.keyToString(storageItem.getKey()),
		    					storageItem.getStorageItemPartNumber(),
		    					storageItem.getStorageItemName(),
		    					storageItem.getStorageItemDescription() != null ?
		    							storageItem.getStorageItemDescription() : "",
		    					storageItem.getStorageItemMSDLevel() != null ?
		    							storageItem.getStorageItemMSDLevel() : 0,
		    					storageItemInstanceInformationList
		    					);
    			
    			storageItemInformationList.add(storageItemInformation);
    		}
    		
    		// Get Customer information
    		StorageItemSimple storageItemSimple = new StorageItemSimple(
    				customer1.getUser().getUserEmail().getEmail(),
    				storageItemInformationList
    				);
    		simpleStorageItems.add(storageItemSimple);
		}
		
    	return simpleStorageItems;
    }

}
