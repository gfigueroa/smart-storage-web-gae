/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.Key;

import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the StorageDevice class.
 * 
 */

public class StorageDeviceManager {
	
	private static final Logger log = 
        Logger.getLogger(StorageDeviceManager.class.getName());
	
	/**
     * Get a StorageDevice instance from the datastore given the StorageDevice key.
     * @param key
     * 			: the StorageDevice's key
     * @return StorageDevice instance, null if StorageDevice is not found
     */
	public static StorageDevice getStorageDevice(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		StorageDevice storageDevice;
		try  {
			storageDevice = pm.getObjectById(StorageDevice.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return storageDevice;
	}
	
	/**
     * Get a StorageDevice instance from the datastore given the StorageDevice serialNumber.
     * @param storageDeviceSerialNumber
     * 			: the StorageDevice's serialNumber
     * @return StorageDevice instance, null if StorageDevice is not found
     */
	@SuppressWarnings("unchecked")
	public static StorageDevice getStorageDevice(String storageDeviceSerialNumber) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(StorageDevice.class);
        query.setFilter("storageDeviceSerialNumber == storageDeviceSerialNumberParam");
        query.declareParameters(Key.class.getName() + " storageDeviceSerialNumberParam");
        query.setOrdering("storageDeviceSerialNumber asc");

        List<StorageDevice> storageDevices = null;
        try {
        	StorageDevice storageDevice = null;
        	storageDevices = 
        			(List<StorageDevice>) query.execute(storageDeviceSerialNumber);
            
            if (!storageDevices.isEmpty()) {
            	storageDevice = storageDevices.get(0);
            	storageDevice.getKey(); // Touch element
            }
            return storageDevice;
        }
        finally {
        	pm.close();
            query.closeAll();
        }
	}
	
	/**
     * Get all StorageDevice instances from the datastore.
     * @return All StorageDevice instances
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static List<StorageDevice> getAllStorageDevices() {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(StorageDevice.class);

        List<StorageDevice> types = null;
        try {
        	types = (List<StorageDevice>) query.execute();
            // touch all elements
            for (StorageDevice t : types)
                t.getStorageDeviceNickname();
        } 
        finally {
        	pm.close();
            query.closeAll();
        }

        Collections.sort(types);
        return types;
    }
	
	/**
     * Get all StorageDevice instances from the datastore that belong to
     * this DeviceModel.
     * @param	deviceModelKey: the DeviceModel key
     * @return All StorageDevice instances that belong to the given DeviceModel
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static List<StorageDevice> getAllStorageDevicesFromDeviceModel(
			Key deviceModelKey) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(StorageDevice.class);
        query.setFilter("deviceModel == deviceModelParam");
        query.declareParameters(Key.class.getName() + " deviceModelParam");
        query.setOrdering("storageDeviceNickname asc");

        List<StorageDevice> types = null;
        try {
        	types = (List<StorageDevice>) query.execute(deviceModelKey);
            // touch all elements
            for (StorageDevice t : types)
                t.getStorageDeviceNickname();
        } 
        finally {
        	pm.close();
            query.closeAll();
        }

        Collections.sort(types);
        return types;
    }
	
	/**
     * Get all StorageDevice instances from the datastore that belong to
     * this Customer.
     * @param customerKey
     * @return All StorageDevice instances that belong to the given Customer
     * TODO: Inefficient touching of objects
     */
	public static List<StorageDevice> getAllStorageDevicesFromCustomer(
			Key customerKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        Customer customer = 
        		pm.getObjectById(Customer.class, customerKey);
         
        List<StorageDevice> result = null;
        ArrayList<StorageDevice> finalResult = new ArrayList<StorageDevice>();
        try {
            result = customer.getStorageDevices();
            for (StorageDevice storageDevice : result) {
            	storageDevice.getStorageDeviceComments();
            	finalResult.add(storageDevice);
            }
        }
        finally {
            pm.close();
        }

        Collections.sort(finalResult);
        return finalResult;
    }
	
	/**
     * Get all StorageDevice instances from the datastore that belong to
     * this Customer and that are of this DeviceModel.
     * @param customerKey
     * @param deviceModelKey
     * @return All StorageDevice instances that belong to the given Customer
     * and that are of this DeviceModel
     */
	public static List<StorageDevice> getAllStorageDevicesFromCustomerAndDeviceModel(
			Key customerKey, Key deviceModelKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        Customer customer = 
        		pm.getObjectById(Customer.class, customerKey);
         
        List<StorageDevice> result = null;
        ArrayList<StorageDevice> finalResult = new ArrayList<StorageDevice>();
        try {
            result = customer.getStorageDevices();
            for (StorageDevice storageDevice : result) {
            	if (storageDevice.getDeviceModel().equals(deviceModelKey)) {
            		finalResult.add(storageDevice);
            	}
            }
        }
        finally {
            pm.close();
        }

        Collections.sort(finalResult);
        return finalResult;
    }
	
	/**
     * Get all StorageDevice instances from the datastore that
     * this CustomerUser can access.
     * @param customerUserKey
     * @return All StorageDevice instances that this CustomerUser can view
     * TODO: Inefficient touching of objects
     */
	public static List<StorageDevice> getAllStorageDevicesFromCustomerUser(
			Key customerUserKey) {
		
		List<StorageDeviceCustomerUser> storageDeviceCustomerUsers =
				StorageDeviceCustomerUserManager.
				getAllStorageDeviceCustomerUsersWithCustomerUser(customerUserKey);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

		ArrayList<StorageDevice> storageDevices = new ArrayList<>();
		try {
			for (StorageDeviceCustomerUser storageDeviceCustomerUser : 
					storageDeviceCustomerUsers) {
				StorageDevice storageDevice = 
		        		pm.getObjectById(StorageDevice.class, 
		        				storageDeviceCustomerUser.getStorageDevice());
				// Touch storageDevice
				storageDevice.getKey();
				storageDevices.add(storageDevice);
			}
		}
        finally {
            pm.close();
        }

		Collections.sort(storageDevices);
        return storageDevices;
    }
	
	/**
     * Get all StorageDevice instances from the datastore that
     * this CustomerUser can access and that are of this DeviceModel.
     * @param customerUserKey
     * @param deviceModelKey
     * @return All StorageDevice instances that this CustomerUser can view
     * and that are of this DeviceModel
     */
	public static List<StorageDevice> getAllStorageDevicesFromCustomerUserAndDeviceModel(
			Key customerUserKey, Key deviceModelKey) {
		
		List<StorageDeviceCustomerUser> storageDeviceCustomerUsers =
				StorageDeviceCustomerUserManager.
				getAllStorageDeviceCustomerUsersWithCustomerUser(customerUserKey);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

		ArrayList<StorageDevice> storageDevices = new ArrayList<>();
		try {
			for (StorageDeviceCustomerUser storageDeviceCustomerUser : 
					storageDeviceCustomerUsers) {
				StorageDevice storageDevice = 
		        		pm.getObjectById(StorageDevice.class, 
		        				storageDeviceCustomerUser.getStorageDevice());
				
				if (storageDevice.getDeviceModel().equals(deviceModelKey)) {
					storageDevices.add(storageDevice);
				}
			}
		}
        finally {
            pm.close();
        }

		Collections.sort(storageDevices);
        return storageDevices;
    }
	
    /**
     * Put StorageDevice into datastore.
     * Stores the given StorageDevice instance in the datastore for this
     * Customer.
     * @param customerKey
     *          : the key of the Customer where the storageDevice will be added
     * @param storageDevice
     *          : the StorageDevice instance to storageDevice
     */
    public static void putStorageDevice(Key customerKey, 
    		StorageDevice storageDevice) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            Customer customer = 
                    pm.getObjectById(Customer.class, customerKey);
            tx.begin();
            customer.addStorageDevice(storageDevice);
            customer.updateStorageDeviceVersion();
            tx.commit();
            log.info("StorageDevice \"" + storageDevice.getStorageDeviceSerialNumber() + 
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
    * Delete StorageDevice from datastore.
    * Deletes the StorageDevice corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the StorageDevice instance to delete
    */
    public static void deleteStorageDevice(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            Customer customer = pm.getObjectById(Customer.class, key.getParent());
            StorageDevice storageDevice = pm.getObjectById(StorageDevice.class, key);
            String storageDeviceContent = storageDevice.getStorageDeviceSerialNumber();
            tx.begin();
            customer.removeStorageDevice(storageDevice);
            customer.updateStorageDeviceVersion();
            tx.commit();
            log.info("StorageDevice \"" + storageDeviceContent + 
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
    * Update StorageDevice attributes.
    * Update's the given StorageDevice's attributes in the datastore.
    * @param key
    * 			: the key of the StorageDevice whose attributes will be updated
    * @param storageDeviceSerialNumber
    * 			: storageDevice number
    * @param storageDeviceNickname
    * 			: storageDevice nickname
    * @param storageDeviceDescription
    * 			: storageDevice description
    * @param storageDeviceManufacturedDate
    * 			: storageDevice manufacturedDate
    * @param storageDeviceShippingDate
    * 			: storageDevice shippingDate
    * @param sensorDataEffectivePeriod
    * 			: sensorDataEffectivePeriod
    * @param alarmMessageEffectivePeriod
    * 			: alarmMessageEffectivePeriod
    * @param enableSensorDataUpload
    * 			: whether to enable the sensor data upload or not
    * @param sensorReadingCacheSize
    * 			: SensorReadingCache size
    * @param storageDeviceComments
    * 			: storageDevice comments
	* @throws MissingRequiredFieldsException 
    */
	public static void updateStorageDeviceAttributes(
			Key key,
			String storageDeviceSerialNumber,
			String storageDeviceNickname,
    		String storageDeviceDescription, 
    		Date storageDeviceManufacturedDate, 
    		Date storageDeviceShippingDate,
    		Integer sensorDataEffectivePeriod,
    		Integer alarmMessageEffectivePeriod,
    		Boolean enableSensorDataUpload,
    		Integer sensorReadingCacheSize,
    		String storageDeviceComments) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
        Customer customer = pm.getObjectById(Customer.class, key.getParent());

		Transaction tx = pm.currentTransaction();
		try {
			StorageDevice storageDevice = pm.getObjectById(StorageDevice.class, key);
			tx.begin();
			storageDevice.setStorageDeviceSerialNumber(storageDeviceSerialNumber);
			storageDevice.setStorageDeviceNickname(storageDeviceNickname);
			storageDevice.setStorageDeviceDescription(storageDeviceDescription);
			storageDevice.setStorageDeviceManufacturedDate(storageDeviceManufacturedDate);
			storageDevice.setStorageDeviceShippingDate(storageDeviceShippingDate);
			storageDevice.setSensorDataEffectivePeriod(sensorDataEffectivePeriod);
			storageDevice.setAlarmMessageEffectivePeriod(alarmMessageEffectivePeriod);
			storageDevice.setEnableSensorDataUpload(enableSensorDataUpload);
			storageDevice.setSensorReadingCacheSize(sensorReadingCacheSize);
			storageDevice.setStorageDeviceComments(storageDeviceComments);
			
			customer.updateStorageDeviceVersion();
			tx.commit();
			log.info("StorageDevice \"" + storageDeviceNickname + 
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
