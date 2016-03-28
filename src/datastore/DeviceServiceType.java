/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import exceptions.MissingRequiredFieldsException;

/**
 * This class represents the DeviceServiceType table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@PersistenceCapable
public class DeviceServiceType {
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long key;

    @Persistent
    private String deviceServiceTypeName;
    
    @Persistent
    private String deviceServiceTypeDescription;

    @Persistent
    private String deviceServiceTypeComments;
    
    @Persistent
    private Date deviceServiceTypeCreationDate;
    
    @Persistent
    private Date deviceServiceTypeModificationDate;

    /**
     * DeviceServiceType constructor.
     * @param deviceServiceTypeName
     * 			: DeviceServiceType name
     * @param deviceServiceTypeDescription
     * 			: DeviceServiceType description
     * @param deviceServiceTypeComments
     * 			: DeviceServiceType comments
     * @throws MissingRequiredFieldsException
     */
    public DeviceServiceType(String deviceServiceTypeName, 
    		String deviceServiceTypeDescription,
    		String deviceServiceTypeComments) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (deviceServiceTypeName == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	if (deviceServiceTypeName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

        this.deviceServiceTypeName = deviceServiceTypeName;
        this.deviceServiceTypeDescription = deviceServiceTypeDescription;
        this.deviceServiceTypeComments = deviceServiceTypeComments;
        
        Date now = new Date();
        this.deviceServiceTypeCreationDate = now;
        this.deviceServiceTypeModificationDate = now;
    }

    /**
     * Get DeviceServiceType key.
     * @return DeviceServiceType key
     */
    public Long getKey() {
        return key;
    }
    
    /**
     * Get DeviceServiceType name.
     * @return DeviceServiceType name
     */
    public String getDeviceServiceTypeName() {
        return deviceServiceTypeName;
    }

    /**
     * Get DeviceServiceType description.
     * @return DeviceServiceType description
     */
    public String getDeviceServiceTypeDescription() {
    	return deviceServiceTypeDescription;
    }
    
    /**
     * Get DeviceServiceType comments.
     * @return DeviceServiceType comments
     */
    public String getDeviceServiceTypeComments() {
    	return deviceServiceTypeComments;
    }
    
    /**
     * Get DeviceServiceType creation date.
     * @return DeviceServiceType creation date
     */
    public Date getDeviceServiceTypeCreationDate() {
    	return deviceServiceTypeCreationDate;
    }
    
    /**
     * Get DeviceServiceType modification date.
     * @return DeviceServiceType modification date
     */
    public Date getDeviceServiceTypeModificationDate() {
    	return deviceServiceTypeModificationDate;
    }
    
    /**
     * Set DeviceServiceType name.
     * @param deviceServiceTypeName
     * 			: DeviceServiceType name
     * @throws MissingRequiredFieldsException
     */
    public void setDeviceServiceTypeName(String deviceServiceTypeName)
    		throws MissingRequiredFieldsException {
    	if (deviceServiceTypeName == null || deviceServiceTypeName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"DeviceService type name is missing.");
    	}
    	this.deviceServiceTypeName = deviceServiceTypeName;
    	this.deviceServiceTypeModificationDate = new Date();
    }
    
    /**
     * Set DeviceServiceType description.
     * @param deviceServiceTypeDescription
     * 			: DeviceServiceType description
     */
    public void setDeviceServiceTypeDescription(String deviceServiceTypeDescription) {
    	this.deviceServiceTypeDescription = deviceServiceTypeDescription;
    	this.deviceServiceTypeModificationDate = new Date();
    }
    
    /**
     * Set DeviceServiceType comments.
     * @param deviceServiceTypeComments
     * 			: DeviceServiceType comments
     */
    public void setDeviceServiceTypeComments(String deviceServiceTypeComments) {
    	this.deviceServiceTypeComments = deviceServiceTypeComments;
    	this.deviceServiceTypeModificationDate = new Date();
    }
}