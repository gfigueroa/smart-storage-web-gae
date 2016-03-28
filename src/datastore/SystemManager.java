/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the System class.
 * 
 */

public class SystemManager {
	
	private static final Logger log = Logger.getLogger(SystemManager.class.getName());
	
	/**
     * Get System instance from the datastore.
     * @return The only System instance there should be
     */
	public static System getSystem() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        System system = null;
			
        try {
        	List<System> systems = SystemManager.getAllSystems();
        	// Create system if it hasn't been created yet
        	if (systems == null || systems.isEmpty()) {
        		system = new System();
        		pm.makePersistent(system);
        	}
        	else {
    			system = systems.get(0);
    		}
        } 
        finally {
        	pm.close();
        }

        return system;
    }
	
	/**
     * Get all System instances from the datastore.
     * @return All System instances
     * TODO: Make "touching" of systems more efficient
     */
	@SuppressWarnings("unchecked")
	public static List<System> getAllSystems() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(System.class);

        List<System> systems = null;
        try {
        	systems = (List<System>) query.execute();
            // touch all elements
            for (System s : systems)
                s.getSystemTime();
        } 
        finally {
        	pm.close();
            query.closeAll();
        }

        return systems;
    }
	
	/**
     * Get Customer List Version from the system.
     * @return customer list version
     */
	public static int getCustomerListVersion() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        int customerListVersion = 0;
			
        try {
        	List<System> systems = SystemManager.getAllSystems();
    		if (systems != null && !systems.isEmpty()) {
    			customerListVersion = systems.get(0).getCustomerListVersion();
    		}
        } 
        finally {
        	pm.close();
        }

        return customerListVersion;
	}
	
	/**
     * Get DeviceServiceType List Version from the system.
     * @return DeviceServiceType list version
     */
	public static int getDeviceServiceTypeListVersion() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        int deviceServiceTypeListVersion = 0;
			
        try {
        	List<System> systems = SystemManager.getAllSystems();
    		if (systems != null && !systems.isEmpty()) {
    			deviceServiceTypeListVersion = 
    					systems.get(0).getDeviceServiceTypeListVersion();
    		}
        } 
        finally {
        	pm.close();
        }

        return deviceServiceTypeListVersion;
	}
	
	/**
     * Get DeviceModel List Version from the system.
     * @return DeviceModel list version
     */
	public static int getDeviceModelListVersion() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        int deviceModelListVersion = 0;
			
        try {
        	List<System> systems = SystemManager.getAllSystems();
    		if (systems != null && !systems.isEmpty()) {
    			deviceModelListVersion = 
    					systems.get(0).getDeviceModelListVersion();
    		}
        } 
        finally {
        	pm.close();
        }

        return deviceModelListVersion;
	}
	
	/**
     * Get StorageDeviceContainerModel List Version from the system.
     * @return StorageDeviceContainerModel list version
     */
	public static int getStorageDeviceContainerModelListVersion() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        int storageDeviceContainerModelListVersion = 0;
			
        try {
        	List<System> systems = SystemManager.getAllSystems();
    		if (systems != null && !systems.isEmpty()) {
    			storageDeviceContainerModelListVersion = 
    					systems.get(0).getStorageDeviceContainerModelListVersion();
    		}
        } 
        finally {
        	pm.close();
        }

        return storageDeviceContainerModelListVersion;
	}
	
	/**
     * Get SensorType List Version from the system.
     * @return SensorType list version
     */
	public static int getSensorTypeListVersion() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        int sensorTypeListVersion = 0;
			
        try {
        	List<System> systems = SystemManager.getAllSystems();
    		if (systems != null && !systems.isEmpty()) {
    			sensorTypeListVersion = 
    					systems.get(0).getSensorTypeListVersion();
    		}
        } 
        finally {
        	pm.close();
        }

        return sensorTypeListVersion;
	}
	
	/**
     * Get AlarmTrigger List Version from the system.
     * @return AlarmTrigger list version
     */
	public static int getAlarmTriggerListVersion() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        int alarmTriggerListVersion = 0;
			
        try {
        	List<System> systems = SystemManager.getAllSystems();
    		if (systems != null && !systems.isEmpty()) {
    			alarmTriggerListVersion = 
    					systems.get(0).getAlarmTriggerListVersion();
    		}
        } 
        finally {
        	pm.close();
        }

        return alarmTriggerListVersion;
	}
	
	/**
    * Update Customer List Version.
    * Updates the customer list version (add 1)
    */
	public static void updateCustomerListVersion() {	
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			List<System> systems = SystemManager.getAllSystems();
			System system = null;
			tx.begin();
			// Create system if it hasn't been created yet
			if (systems == null || systems.isEmpty()) {
				system = new System();
				pm.makePersistent(system);
			}
			else {
				system = pm.getObjectById(System.class, systems.get(0).getKey());
			}
			system.updateCustomerListVersion();
			tx.commit();
			log.info("System \"" + system.getKey() + 
					"\": Customer List Version updated to version " +
					system.getCustomerListVersion() + " in the datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Update DeviceServiceType List Version.
    * Updates the DeviceServiceType list version (add 1)
    */
	public static void updateDeviceServiceTypeListVersion() {	
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			List<System> systems = SystemManager.getAllSystems();
			System system = null;
			tx.begin();
			// Create system if it hasn't been created yet
			if (systems == null || systems.isEmpty()) {
				system = new System();
				pm.makePersistent(system);
			}
			else {
				system = pm.getObjectById(System.class, systems.get(0).getKey());
			}
			system.updateDeviceServiceTypeListVersion();
			tx.commit();
			log.info("System \"" + system.getKey() + 
					"\": DeviceServiceType List Version updated to version " +
					system.getDeviceServiceTypeListVersion() + " in the datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Update DeviceModel List Version.
    * Updates the DeviceModel list version (add 1)
    */
	public static void updateDeviceModelListVersion() {	
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			List<System> systems = SystemManager.getAllSystems();
			System system = null;
			tx.begin();
			// Create system if it hasn't been created yet
			if (systems == null || systems.isEmpty()) {
				system = new System();
				pm.makePersistent(system);
			}
			else {
				system = pm.getObjectById(System.class, systems.get(0).getKey());
			}
			system.updateDeviceModelListVersion();
			tx.commit();
			log.info("System \"" + system.getKey() + 
					"\": DeviceModel List Version updated to version " +
					system.getDeviceModelListVersion() + " in the datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Update StorageDeviceContainerModel List Version.
    * Updates the StorageDeviceContainerModel list version (add 1)
    */
	public static void updateStorageDeviceContainerModelListVersion() {	
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			List<System> systems = SystemManager.getAllSystems();
			System system = null;
			tx.begin();
			// Create system if it hasn't been created yet
			if (systems == null || systems.isEmpty()) {
				system = new System();
				pm.makePersistent(system);
			}
			else {
				system = pm.getObjectById(System.class, systems.get(0).getKey());
			}
			system.updateStorageDeviceContainerModelListVersion();
			tx.commit();
			log.info("System \"" + system.getKey() + 
					"\": StorageDeviceContainerModel List Version updated to version " +
					system.getStorageDeviceContainerModelListVersion() + " in the datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Update SensorType List Version.
    * Updates the SensorType list version (add 1)
    */
	public static void updateSensorTypeListVersion() {	
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			List<System> systems = SystemManager.getAllSystems();
			System system = null;
			tx.begin();
			// Create system if it hasn't been created yet
			if (systems == null || systems.isEmpty()) {
				system = new System();
				pm.makePersistent(system);
			}
			else {
				system = pm.getObjectById(System.class, systems.get(0).getKey());
			}
			system.updateSensorTypeListVersion();
			tx.commit();
			log.info("System \"" + system.getKey() + 
					"\": SensorType List Version updated to version " +
					system.getSensorTypeListVersion() + " in the datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Update AlarmTrigger List Version.
    * Updates the AlarmTrigger list version (add 1)
    */
	public static void updateAlarmTriggerListVersion() {	
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			List<System> systems = SystemManager.getAllSystems();
			System system = null;
			tx.begin();
			// Create system if it hasn't been created yet
			if (systems == null || systems.isEmpty()) {
				system = new System();
				pm.makePersistent(system);
			}
			else {
				system = pm.getObjectById(System.class, systems.get(0).getKey());
			}
			system.updateAlarmTriggerListVersion();
			tx.commit();
			log.info("System \"" + system.getKey() + 
					"\": AlarmTrigger List Version updated to version " +
					system.getAlarmTriggerListVersion() + " in the datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Update Department List Version.
    * Updates the Department list version (add 1)
    */
	public static void updateDepartmentListVersion() {	
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			List<System> systems = SystemManager.getAllSystems();
			System system = null;
			tx.begin();
			// Create system if it hasn't been created yet
			if (systems == null || systems.isEmpty()) {
				system = new System();
				pm.makePersistent(system);
			}
			else {
				system = pm.getObjectById(System.class, systems.get(0).getKey());
			}
			system.updateDepartmentListVersion();
			tx.commit();
			log.info("System \"" + system.getKey() + 
					"\": Department List Version updated to version " +
					system.getDepartmentListVersion() + " in the datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Update System attributes.
    * Update the different System variables in the datastore.
    * @param oldestAppVersionSupported
    * 			: the new oldestAppVersionSupported to be updated
	 * @throws MissingRequiredFieldsException 
    */
	public static void updateSystemAttributes(Integer oldestAppVersionSupported1,
			Integer oldestAppVersionSupported2, Integer oldestAppVersionSupported3) 
			throws MissingRequiredFieldsException {	
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			List<System> systems = SystemManager.getAllSystems();
			System system = null;
			tx.begin();
			// Create system if it hasn't been created yet
			if (systems == null || systems.isEmpty()) {
				system = new System();
				pm.makePersistent(system);
			}
			else {
				system = pm.getObjectById(System.class, systems.get(0).getKey());
			}
			system.setOldestAppVersionSupported1(oldestAppVersionSupported1);
			system.setOldestAppVersionSupported2(oldestAppVersionSupported2);
			system.setOldestAppVersionSupported3(oldestAppVersionSupported3);
			tx.commit();
			log.info("System \"" + system.getKey() + "\": oldestAppVersionSupported updated" +
					" in the datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
}

