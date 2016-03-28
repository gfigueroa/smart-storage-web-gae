/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.Key;

import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the DeviceModel class.
 * 
 */

public class DeviceModelManager {
	
	private static final Logger log = 
        Logger.getLogger(DeviceModelManager.class.getName());
	
	/**
     * Get a DeviceModel instance from the datastore given the DeviceModel key.
     * @param key
     * 			: the DeviceModel's key
     * @return DeviceModel instance, null if DeviceModel is not found
     */
	public static DeviceModel getDeviceModel(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		DeviceModel deviceModel;
		try  {
			deviceModel = pm.getObjectById(DeviceModel.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return deviceModel;
	}
	
	/**
     * Get all DeviceModel instances from the datastore.
     * @return All DeviceModel instances
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static List<DeviceModel> getAllDeviceModels() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(DeviceModel.class);

        List<DeviceModel> types = null;
        try {
        	types = (List<DeviceModel>) query.execute();
            // touch all elements
            for (DeviceModel t : types)
                t.getDeviceModelName();
        } 
        finally {
        	pm.close();
            query.closeAll();
        }

        return types;
    }
	
	/**
     * Get all DeviceModel instances from the datastore that belong to
     * this Customer.
     * @param customerKey
     * @return All DeviceModels that belong to the given Customer
     * TODO: Inefficient touching of objects
     */
	public static List<DeviceModel> getAllDeviceModelsFromCustomer(
			Key customerKey) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        Customer customer = 
        		pm.getObjectById(Customer.class, customerKey);
         
        List<StorageDevice> storageDevices = null;
        HashSet<Key> deviceModelKeys = new HashSet<Key>();
        ArrayList<DeviceModel> deviceModels = new ArrayList<DeviceModel>();
        try {
        	storageDevices = customer.getStorageDevices();
            for (StorageDevice storageDevice : storageDevices) {
            	deviceModelKeys.add(storageDevice.getDeviceModel());
            }
            
            for (Key deviceModelKey : deviceModelKeys) {
            	DeviceModel deviceModel = 
            			pm.getObjectById(DeviceModel.class, deviceModelKey);
            	deviceModels.add(deviceModel);
            }
        }
        finally {
            pm.close();
        }

        return deviceModels;
    }
	
	/**
     * Get all DeviceModel instances from the datastore that belong to
     * this CustomerUser.
     * @param customerUserKey
     * @return All DeviceModels that belong to the given CustomerUser
     * TODO: Inefficient touching of objects
     */
	public static List<DeviceModel> getAllDeviceModelsFromCustomerUser(
			Key customerUserKey) {
		
		List<StorageDeviceCustomerUser> storageDeviceCustomerUsers =
				StorageDeviceCustomerUserManager.
				getAllStorageDeviceCustomerUsersWithCustomerUser(customerUserKey);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
         
        ArrayList<StorageDevice> storageDevices = new ArrayList<>();
        HashSet<Key> deviceModelKeys = new HashSet<Key>();
        ArrayList<DeviceModel> deviceModels = new ArrayList<DeviceModel>();
        try {
        	// First, get StorageDevice list
    		for (StorageDeviceCustomerUser storageDeviceCustomerUser : 
    			storageDeviceCustomerUsers) {
    			StorageDevice storageDevice = 
    	        		pm.getObjectById(StorageDevice.class, 
    	        				storageDeviceCustomerUser.getStorageDevice());
    			// Touch storageDevice
    			storageDevice.getKey();
    			storageDevices.add(storageDevice);
    		}
        	
    		// Then, get unique keys
            for (StorageDevice storageDevice : storageDevices) {
            	deviceModelKeys.add(storageDevice.getDeviceModel());
            }
            
            // Finally, build DeviceModel list
            for (Key deviceModelKey : deviceModelKeys) {
            	DeviceModel deviceModel = 
            			pm.getObjectById(DeviceModel.class, deviceModelKey);
            	deviceModels.add(deviceModel);
            }
        }
        finally {
            pm.close();
        }

        return deviceModels;
    }
	
	/**
     * Get all DeviceModel instances from the datastore that belong
     * to the given DeviceServiceType.
     * @param deviceServiceTypeKey:
     * 			the key of the DeviceServiceType to filter
     * @return All DeviceModel instances that belong to this DeviceServiceType
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static List<DeviceModel> getDeviceModels(Long deviceServiceTypeKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(DeviceModel.class);
        query.setFilter("deviceServiceType == deviceServiceTypeParam");
        query.setOrdering("deviceModelName asc");
        query.declareParameters("Long deviceServiceType");

        List<DeviceModel> types = null;
        try {
        	types = (List<DeviceModel>) query.execute(deviceServiceTypeKey);
            // touch all elements
            for (DeviceModel t : types)
                t.getDeviceModelName();
        } 
        finally {
        	pm.close();
            query.closeAll();
        }

        return types;
    }	
	
	/**
     * Put DeviceModel into datastore.
     * Stations the given DeviceModel instance in the datastore calling the 
     * PersistenceManager's makePersistent() method.
     * @param deviceModel
     * 			: the DeviceModel instance to store
     */
	public static void putDeviceModel(DeviceModel deviceModel) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistent(deviceModel);
			tx.commit();
			log.info("DeviceModel \"" + deviceModel.getDeviceModelName() + 
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
    * Delete DeviceModel from datastore.
    * Deletes the DeviceModel corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    * 			: the key of the DeviceModel instance to delete
    */
	public static void deleteDeviceModel(Key key) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			DeviceModel deviceModel = pm.getObjectById(DeviceModel.class, key);
			String DeviceModelName = deviceModel.getDeviceModelName();
			tx.begin();
			pm.deletePersistent(deviceModel);
			tx.commit();
			log.info("DeviceModel \"" + DeviceModelName + 
                     "\" deleted successfully from datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}

	/**
    * Update DeviceModel attributes.
    * Update's the given DeviceModel's attributes in the datastore.
    * @param key
    * 			: the key of the DeviceModel whose attributes will be updated
    * @param deviceServiceType
    * 			: deviceServiceType
    * @param deviceModelName
    * 			: the new name to give to the DeviceModel
    * @param deviceModelDescription
    * 			: the new description to give to the DeviceModel
    * @param deviceModelVersionNumber
    * 			: the new version number to give to the deviceModel
    * @param deviceModelDesignTime
    * 			: the new design time to give to the deviceModel
    * @param sensorDataUploadPeriod
    * 			: the new sensorDataUploadPeriod to give to the deviceModel
    * @param deviceModelComments
    * 			: the new comments to give to the DeviceModel
    * @param temp1Hum1
    * 			: temp1Hum1 function
    * @param temp2Hum2
    * 			: temp2Hum2 function
    * @param doorOpenClose
    * 			: doorOpenClose function
    * @param CO2
    * 			: CO2 function
    * @param CO
    * 			: CO function
    * @param flux
    * 			: flux function
    * @param infrared
    * 			: infrared function
    * @param imageUpload
    * 			: imageUpload function
    * @param alcohol
    * 			: alcohol function
    * @param electricCurrent
    * 			: electricCurrent function
    * @param atmosphericPressure
    * 			: atmosphericPressure function
	* @throws MissingRequiredFieldsException 
    */
	public static void updateDeviceModelAttributes(
			Key key,
			Long deviceServiceType,
    		String deviceModelName, 
    		String deviceModelDescription,
    		Date deviceModelDesignTime,
    		Integer sensorDataUploadPeriod,
    		String deviceModelComments,
    		Boolean temp1Hum1,
    		Boolean temp2Hum2,
    		Boolean doorOpenClose,
    		Boolean CO2,
    		Boolean CO,
    		Boolean flux,
    		Boolean infrared,
    		Boolean imageUpload,
    		Boolean alcohol,
    		Boolean electricCurrent,
    		Boolean atmosphericPressure) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			DeviceModel deviceModel = pm.getObjectById(DeviceModel.class, key);
			tx.begin();
			deviceModel.setDeviceServiceType(deviceServiceType);
			deviceModel.setDeviceModelName(deviceModelName);
			deviceModel.setDeviceModelDescription(deviceModelDescription);
			deviceModel.setDeviceModelDesignTime(deviceModelDesignTime);
			deviceModel.setSensorDataUploadPeriod(sensorDataUploadPeriod);
			deviceModel.setDeviceModelComments(deviceModelComments);
			deviceModel.setTemp1Hum1(temp1Hum1);
			deviceModel.setTemp2Hum2(temp2Hum2);
			deviceModel.setDoorOpenClose(doorOpenClose);
			deviceModel.setCO2(CO2);
			deviceModel.setCO(CO);
			deviceModel.setFlux(flux);
			deviceModel.setInfrared(infrared);
			deviceModel.setImageUpload(imageUpload);
			deviceModel.setAlcohol(alcohol);
			deviceModel.setElectricCurrent(electricCurrent);
			deviceModel.setAtmosphericPressure(atmosphericPressure);
			tx.commit();
			log.info("DeviceModel \"" + deviceModelName + 
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
