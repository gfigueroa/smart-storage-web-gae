/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import exceptions.MissingRequiredFieldsException;

/**
 * This class represents the System table.
 * The System table stores some version numbers required by the Mobile App to download
 * data from the web server.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class System implements Serializable {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long key;

    @Persistent
    private Integer customerListVersion;
    
    @Persistent
    private Date customerListTimestamp;
    
    @Persistent
    private Integer deviceServiceTypeListVersion;
    
    @Persistent
    private Date deviceServiceTypeListTimestamp;
    
    @Persistent
    private Integer deviceModelListVersion;
    
    @Persistent
    private Date deviceModelListTimestamp;
    
    @Persistent
    private Integer storageDeviceContainerModelListVersion;
    
    @Persistent
    private Date storageDeviceContainerModelListTimestamp;
    
    @Persistent
    private Integer sensorTypeListVersion;
    
    @Persistent
    private Date sensorTypeListTimestamp;
    
    @Persistent
    private Integer alarmTriggerListVersion;
    
    @Persistent
    private Date alarmTriggerListTimestamp;
    
    @Persistent
    private Integer departmentListVersion;
    
    @Persistent
    private Date departmentListTimestamp;
    
    @Persistent
    private Integer oldestAppVersionSupported1;
    
    @Persistent
    private Integer oldestAppVersionSupported2;
    
    @Persistent
    private Integer oldestAppVersionSupported3;
    
    @Persistent
    private Date systemTime;

    /**
     * System constructor.
     */
    public System() {
    	Date now = new Date();
    	
    	customerListVersion = 0;
    	customerListTimestamp = now;
    	deviceServiceTypeListVersion = 0;
    	deviceServiceTypeListTimestamp = now;
    	deviceModelListVersion = 0;
    	deviceModelListTimestamp = now;
    	storageDeviceContainerModelListVersion = 0;
    	storageDeviceContainerModelListTimestamp = now;
    	sensorTypeListVersion = 0;
    	sensorTypeListTimestamp = now;
    	alarmTriggerListVersion = 0;
    	alarmTriggerListTimestamp = now;
    	departmentListVersion = 0;
    	departmentListTimestamp = now;
    	
    	oldestAppVersionSupported1 = 1;
    	oldestAppVersionSupported2 = 0;
    	oldestAppVersionSupported3 = 0;

    	this.systemTime = now;
    }

    /**
     * Get System key.
     * @return system key
     */
    public Long getKey() {
        return key;
    }

    /**
     * Get Customer List Version.
     * @return customer list version
     */
    public Integer getCustomerListVersion() {
        return customerListVersion;
    }
    
    /**
     * Get Device Service Type List Version.
     * @return Device Service Type list version
     */
    public Integer getDeviceServiceTypeListVersion() {
        return deviceServiceTypeListVersion;
    }
    
    /**
     * Get Device Model List version.
     * @return Device Model version
     */
    public Integer getDeviceModelListVersion() {
    	return deviceModelListVersion;
    }
    
    /**
	 * @return the storageDeviceContainerModelListVersion
	 */
	public Integer getStorageDeviceContainerModelListVersion() {
		return storageDeviceContainerModelListVersion;
	}

	/**
	 * @return the sensorTypeListVersion
	 */
	public Integer getSensorTypeListVersion() {
		return sensorTypeListVersion;
	}

	/**
	 * @return the alarmTriggerListVersion
	 */
	public Integer getAlarmTriggerListVersion() {
		return alarmTriggerListVersion;
	}
	
	/**
	 * 
	 * @return the departmentListVersion
	 */
	public Integer getDepartmentListVersion() {
		if (departmentListVersion == null) {
			departmentListVersion = 0;
		}
		return departmentListVersion;
	}

	/**
     * Get oldest mobile app version supported by this
     * server version.
     * @return oldest app version supported
     */
    public String getOldestAppVersionSupportedString() {
        if (oldestAppVersionSupported1 == null ||
        		oldestAppVersionSupported2 == null ||
        		oldestAppVersionSupported3 == null) {
        	return "";
        }
        else {
	    	return oldestAppVersionSupported1 + "." + 
	        		oldestAppVersionSupported2 + "." + 
	        		oldestAppVersionSupported3;
        }
    }
    
    /**
     * Get first digit of oldest mobile app version supported 
     * by this server version.
     * @return first digit of oldest app version supported
     */
    public Integer getOldestAppVersionSupported1() {
        return oldestAppVersionSupported1;
    }
    
    /**
     * Get second digit of oldest mobile app version supported 
     * by this server version.
     * @return second digit of oldest app version supported
     */
    public Integer getOldestAppVersionSupported2() {
        return oldestAppVersionSupported2;
    }
    
    /**
     * Get third digit of oldest mobile app version supported 
     * by this server version.
     * @return third digit of oldest app version supported
     */
    public Integer getOldestAppVersionSupported3() {
        return oldestAppVersionSupported3;
    }
    
    /**
     * Get time when last update was made.
     * @return system time
     */
    public Date getSystemTime() {
        return systemTime;
    }
    
    /**
     * Compare this system instance with another syste,
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this System, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if (!(o instanceof System ) ) return false;
        System system = (System) o;
        return (this.getKey() == system.getKey());
    }
    
    /**
     * Update the Customer List Version number by 1.
     */
    public void updateCustomerListVersion() {
    	customerListVersion++;
    	systemTime = new Date();
    }
    
    /**
     * Update the Device Service Type List Version number by 1.
     */
    public void updateDeviceServiceTypeListVersion() {
    	deviceServiceTypeListVersion++;
    	systemTime = new Date();
    }
    
    /**
     * Update the Device Model List version number by 1.
     */
    public void updateDeviceModelListVersion() {
    	deviceModelListVersion++;
    	systemTime = new Date();
    }
    
    /**
     * Update the Storage Device Container Model List version number by 1.
     */
    public void updateStorageDeviceContainerModelListVersion() {
    	storageDeviceContainerModelListVersion++;
    	systemTime = new Date();
    }
    
    /**
     * Update the Sensor Type List version number by 1.
     */
    public void updateSensorTypeListVersion() {
    	sensorTypeListVersion++;
    	systemTime = new Date();
    }
    
    /**
     * Update the Alarm Trigger List version number by 1.
     */
    public void updateAlarmTriggerListVersion() {
    	alarmTriggerListVersion++;
    	systemTime = new Date();
    }
    
    /**
     * Update the Department List version number by 1.
     */
    public void updateDepartmentListVersion() {
    	if (departmentListVersion == null) {
    		departmentListVersion = 0;
    	}
    	departmentListVersion++;
    }
    
    /**
     * Set first digit of the 
     * oldest app version supported by this server version.
     * @param oldestAppVersionSupported1
     * 			: the first digit of the oldest app version 
     * 			  supported by this server
     * @throws MissingRequiredFieldsException 
     */
    public void setOldestAppVersionSupported1(Integer oldestAppVersionSupported1) 
    		throws MissingRequiredFieldsException {
    	// Check required field constraint
    	if (oldestAppVersionSupported1 == null) {
    		throw new MissingRequiredFieldsException(
    				this, "Missing oldest app version supported digit 1.");
    	}
    	this.oldestAppVersionSupported1 = oldestAppVersionSupported1;
    	systemTime = new Date();
    }
    
    /**
     * Set second digit of the 
     * oldest app version supported by this server version.
     * @param oldestAppVersionSupported2
     * 			: the second digit of the oldest app version 
     * 			  supported by this server
     * @throws MissingRequiredFieldsException 
     */
    public void setOldestAppVersionSupported2(Integer oldestAppVersionSupported2) 
    		throws MissingRequiredFieldsException {
    	// Check required field constraint
    	if (oldestAppVersionSupported2 == null) {
    		throw new MissingRequiredFieldsException(
    				this, "Missing oldest app version supported digit 2.");
    	}
    	this.oldestAppVersionSupported2 = oldestAppVersionSupported2;
    	systemTime = new Date();
    }
    
    /**
     * Set third digit of the 
     * oldest app version supported by this server version.
     * @param oldestAppVersionSupported3
     * 			: the third digit of the oldest app version 
     * 			  supported by this server
     * @throws MissingRequiredFieldsException 
     */
    public void setOldestAppVersionSupported3(Integer oldestAppVersionSupported3) 
    		throws MissingRequiredFieldsException {
    	// Check required field constraint
    	if (oldestAppVersionSupported3 == null) {
    		throw new MissingRequiredFieldsException(
    				this, "Missing oldest app version supported digit 3.");
    	}
    	this.oldestAppVersionSupported3 = oldestAppVersionSupported3;
    	systemTime = new Date();
    }
    
}