/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.ArrayList;
import java.util.Date;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import util.DateManager;

import com.google.appengine.api.datastore.Key;

import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class represents the DeviceModel table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@PersistenceCapable
public class DeviceModel {
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    
    @Persistent
    private Long deviceServiceType;

    @Persistent
    private String deviceModelName;
    
    @Persistent
    private String deviceModelDescription;
    
    @Persistent
    private Date deviceModelDesignTime;
    
    @Persistent
    private Integer sensorDataUploadPeriod;

    @Persistent
    private String deviceModelComments;
    
    @Persistent
    private Boolean temp1Hum1;
    
    @Persistent
    private Boolean temp2Hum2;
    
    @Persistent
    private Boolean doorOpenClose;
    
    @Persistent
    private Boolean CO2;
    
    @Persistent
    private Boolean CO;
    
    @Persistent
    private Boolean flux;
    
    @Persistent
    private Boolean infrared;
    
    @Persistent
    private Boolean imageUpload;
    
    @Persistent
    private Boolean alcohol;
    
    @Persistent
    private Boolean electricCurrent;
    
    @Persistent
    private Boolean atmosphericPressure;
    
    @Persistent
    private Date deviceModelCreationDate;
    
    @Persistent
    private Date deviceModelModificationDate;
    
    @Persistent
    @Element(dependent = "true")
    private ArrayList<DeviceModelDoor> deviceModelDoors;
    
    @Persistent
    @Element(dependent = "true")
    private ArrayList<AlarmWarning> alarmWarnings;

    /**
     * DeviceModel constructor.
     * @param deviceServiceType
     * 			: deviceServiceType
     * @param deviceModelName
     * 			: DeviceModel name
     * @param deviceModelDescription
     * 			: DeviceModel description
     * @param deviceModelDesignTime
     * 			: DeviceModel design time
     * @param deviceModelComments
     * 			: DeviceModel comments
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
    public DeviceModel(Long deviceServiceType,
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
    		Boolean atmosphericPressure
    		) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (deviceServiceType == null || deviceModelName == null || 
    			deviceModelDesignTime == null || sensorDataUploadPeriod == null ||
    			temp1Hum1 == null || temp2Hum2 == null || doorOpenClose == null || 
    			CO2 == null || CO == null || flux == null || infrared == null || 
    			imageUpload == null || alcohol == null || electricCurrent == null || 
    			atmosphericPressure == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	if (deviceModelName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

    	this.deviceServiceType = deviceServiceType;
        this.deviceModelName = deviceModelName;
        this.deviceModelDescription = deviceModelDescription;
        this.deviceModelDesignTime = deviceModelDesignTime;
        this.sensorDataUploadPeriod = sensorDataUploadPeriod;
        this.deviceModelComments = deviceModelComments;
        this.temp1Hum1 = temp1Hum1;
        this.temp2Hum2 = temp2Hum2;
        this.doorOpenClose = doorOpenClose;
        this.CO2 = CO2;
        this.CO = CO;
        this.flux = flux;
        this.infrared = infrared;
        this.imageUpload = imageUpload;
        this.alcohol = alcohol;
        this.electricCurrent = electricCurrent;
        this.atmosphericPressure = atmosphericPressure;
        
        Date now = new Date();
        this.deviceModelCreationDate = now;
        this.deviceModelModificationDate = now;
        
        this.deviceModelDoors = new ArrayList<>();
        this.alarmWarnings = new ArrayList<>();
    }

    /**
     * Get DeviceModel key.
     * @return DeviceModel key
     */
    public Key getKey() {
        return key;
    }
    
    /**
	 * @return the deviceServiceType
	 */
	public Long getDeviceServiceType() {
		return deviceServiceType;
	}
    
    /**
     * Get DeviceModel name.
     * @return DeviceModel name
     */
    public String getDeviceModelName() {
        return deviceModelName;
    }

    /**
     * Get DeviceModel description.
     * @return DeviceModel description
     */
    public String getDeviceModelDescription() {
    	return deviceModelDescription;
    }

	/**
	 * @return the deviceModelDesignTime
	 */
	public Date getDeviceModelDesignTime() {
		return deviceModelDesignTime;
	}
	
	/**
	 * @return the sensorDataUploadPeriod
	 */
	public Integer getSensorDataUploadPeriod() {
		if (sensorDataUploadPeriod == null) {
			sensorDataUploadPeriod = 60; // Default of 60 seconds
		}
		return sensorDataUploadPeriod;
	}
	
	/**
	 * Whether the last SensorReading in a StorageDevice belonging to this
	 * DeviceModel is expired or not based on the sensorDataUploadPeriod
	 * @param now: the date to compare
	 * @param lastSensorReading: the SensorReading to check
	 * @return True if the last SensorReading time is less than
	 * the given date minus the sensorDataUploadPeriod in seconds,
	 * False otherwise
	 */
	public boolean lastSensorDataUploadIsExpired(Date now, SensorReading lastSensorReading) {
		
		if (lastSensorReading == null) {
			return true;
		}
		
		Date nowMinusSensorDataUploadPeriod =
				DateManager.subtractSecondsFromDate(now, getSensorDataUploadPeriod());
		Date lastSensorReadingTime = lastSensorReading.getSensorReadingTime();
		
		if (lastSensorReadingTime.compareTo(nowMinusSensorDataUploadPeriod) < 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
     * Get DeviceModel comments.
     * @return DeviceModel comments
     */
    public String getDeviceModelComments() {
    	return deviceModelComments;
    }

	/**
	 * @return the temp1Hum1
	 */
	public Boolean getTemp1Hum1() {
		return temp1Hum1;
	}

	/**
	 * @return the temp2Hum2
	 */
	public Boolean getTemp2Hum2() {
		return temp2Hum2;
	}

	/**
	 * @return the doorOpenClose
	 */
	public Boolean getDoorOpenClose() {
		return doorOpenClose;
	}

	/**
	 * @return the cO2
	 */
	public Boolean getCO2() {
		return CO2;
	}

	/**
	 * @return the cO
	 */
	public Boolean getCO() {
		return CO;
	}

	/**
	 * @return the flux
	 */
	public Boolean getFlux() {
		return flux;
	}

	/**
	 * @return the infrared
	 */
	public Boolean getInfrared() {
		return infrared;
	}

	/**
	 * @return the imageUpload
	 */
	public Boolean getImageUpload() {
		return imageUpload;
	}

	/**
	 * @return the alcohol
	 */
	public Boolean getAlcohol() {
		return alcohol;
	}

	/**
	 * @return the electricCurrent
	 */
	public Boolean getElectricCurrent() {
		return electricCurrent;
	}

	/**
	 * @return the atmosphericPressure
	 */
	public Boolean getAtmosphericPressure() {
		return atmosphericPressure;
	}

	/**
     * Get DeviceModel creation date.
     * @return DeviceModel creation date
     */
    public Date getDeviceModelCreationDate() {
    	return deviceModelCreationDate;
    }
    
    /**
     * Get DeviceModel modification date.
     * @return DeviceModel modification date
     */
    public Date getDeviceModelModificationDate() {
    	return deviceModelModificationDate;
    }
    
    /**
     * Get DeviceModelDoors
     * @return deviceModelDoors
     */
    public ArrayList<DeviceModelDoor> getDeviceModelDoors() {
    	return deviceModelDoors;
    }
    
    /**
     * Get AlarmWarnings
     * @return alarmWarnings
     */
    public ArrayList<AlarmWarning> getAlarmWarnings() {
    	return alarmWarnings;
    }
    
	/**
	 * @param deviceServiceType the deviceServiceType to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setDeviceServiceType(Long deviceServiceType) 
			throws MissingRequiredFieldsException {
		
		if (deviceServiceType == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"DeviceServiceType is missing.");
    	}
		this.deviceServiceType = deviceServiceType;
		this.deviceModelModificationDate = new Date();
	}
    
    /**
     * Set DeviceModel name.
     * @param deviceModelName
     * 			: DeviceModel name
     * @throws MissingRequiredFieldsException
     */
    public void setDeviceModelName(
    		String deviceModelName)
    		throws MissingRequiredFieldsException {
    	if (deviceModelName == null || 
    			deviceModelName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"DeviceModel name is missing.");
    	}
    	this.deviceModelName = deviceModelName;
    	this.deviceModelModificationDate = new Date();
    }
    
    /**
     * Set DeviceModel description.
     * @param deviceModelDescription
     * 			: DeviceModel description
     */
    public void setDeviceModelDescription(
    		String deviceModelDescription) {
    	this.deviceModelDescription = 
    			deviceModelDescription;
    	this.deviceModelModificationDate = new Date();
    }

	/**
	 * @param deviceModelDesignTime 
	 * 		the deviceModelDesignTime to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setDeviceModelDesignTime(
			Date deviceModelDesignTime) 
					throws MissingRequiredFieldsException {
		
		if (deviceModelDesignTime == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"DeviceModel design time is missing.");
    	}
		
		this.deviceModelDesignTime = 
				deviceModelDesignTime;
		this.deviceModelModificationDate = new Date();
	}
	
	/**
	 * @param sensorDataUploadPeriod
	 * @throws MissingRequiredFieldsException 
	 */
	public void setSensorDataUploadPeriod(Integer sensorDataUploadPeriod) 
			throws MissingRequiredFieldsException {
		
		if (sensorDataUploadPeriod == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"sensorDataUploadPeriod is missing.");
    	}
				
		this.sensorDataUploadPeriod = sensorDataUploadPeriod;
		this.deviceModelModificationDate = new Date();
	}
    
    /**
     * Set DeviceModel comments.
     * @param deviceModelComments
     * 			: DeviceModel comments
     */
    public void setDeviceModelComments(
    		String deviceModelComments) {
    	this.deviceModelComments = deviceModelComments;
    	this.deviceModelModificationDate = new Date();
    }

	/**
	 * @param temp1Hum1 the temp1Hum1 to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setTemp1Hum1(Boolean temp1Hum1) 
			throws MissingRequiredFieldsException {
		if (temp1Hum1 == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"temp1hum1 is missing.");
    	}
		this.temp1Hum1 = temp1Hum1;
		this.deviceModelModificationDate = new Date();
	}

	/**
	 * @param temp2Hum2 the temp2Hum2 to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setTemp2Hum2(Boolean temp2Hum2) 
			throws MissingRequiredFieldsException {
		if (temp2Hum2 == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"temp2hum2 is missing.");
    	}
		this.temp2Hum2 = temp2Hum2;
		this.deviceModelModificationDate = new Date();
	}

	/**
	 * @param doorOpenClose the doorOpenClose to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setDoorOpenClose(Boolean doorOpenClose) 
			throws MissingRequiredFieldsException {
		if (doorOpenClose == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"doorOpenClose is missing.");
    	}
		this.doorOpenClose = doorOpenClose;
		this.deviceModelModificationDate = new Date();
	}

	/**
	 * @param cO2 the cO2 to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setCO2(Boolean cO2) 
			throws MissingRequiredFieldsException {
		if (cO2 == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"CO2 is missing.");
    	}
		CO2 = cO2;
		this.deviceModelModificationDate = new Date();
	}

	/**
	 * @param cO the cO to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setCO(Boolean cO) 
			throws MissingRequiredFieldsException {
		if (cO == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"CO is missing.");
    	}
		CO = cO;
		this.deviceModelModificationDate = new Date();
	}

	/**
	 * @param flux the flux to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setFlux(Boolean flux) 
			throws MissingRequiredFieldsException {
		if (flux == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"flux is missing.");
    	}
		this.flux = flux;
		this.deviceModelModificationDate = new Date();
	}

	/**
	 * @param infrared the infrared to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setInfrared(Boolean infrared) 
			throws MissingRequiredFieldsException {
		if (infrared == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"infrared is missing.");
    	}
		this.infrared = infrared;
		this.deviceModelModificationDate = new Date();
	}

	/**
	 * @param imageUpload the imageUpload to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setImageUpload(Boolean imageUpload) 
			throws MissingRequiredFieldsException {
		if (imageUpload == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"imageUpload is missing.");
    	}
		this.imageUpload = imageUpload;
		this.deviceModelModificationDate = new Date();
	}

	/**
	 * @param alcohol the alcohol to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setAlcohol(Boolean alcohol) 
			throws MissingRequiredFieldsException {
		if (alcohol == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"alcohol is missing.");
    	}
		this.alcohol = alcohol;
		this.deviceModelModificationDate = new Date();
	}

	/**
	 * @param electricCurrent the electricCurrent to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setElectricCurrent(Boolean electricCurrent) 
			throws MissingRequiredFieldsException {
		if (electricCurrent == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"electricCurrent is missing.");
    	}
		this.electricCurrent = electricCurrent;
		this.deviceModelModificationDate = new Date();
	}

	/**
	 * @param atmosphericPressure the atmosphericPressure to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setAtmosphericPressure(Boolean atmosphericPressure) 
			throws MissingRequiredFieldsException {
		if (atmosphericPressure == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"atmosphericPressure is missing.");
    	}
		this.atmosphericPressure = atmosphericPressure;
		this.deviceModelModificationDate = new Date();
	}
	
	/**
	 * Add a deviceModelDoor to this DeviceModel
	 * @param deviceModelDoor
	 */
	public void addDeviceModelDoor(DeviceModelDoor deviceModelDoor) {
		deviceModelDoors.add(deviceModelDoor);
	}
	
    /**
     * Remove a DeviceModelDoor from the DeviceModel.
     * @param deviceModelDoor
     * 			: DeviceModelDoor to be removed
     * @throws InexistentObjectException
     */
    public void removeDeviceModelDoor(DeviceModelDoor deviceModelDoor) 
    		throws InexistentObjectException {
    	if (!deviceModelDoors.remove(deviceModelDoor)) {
    		throw new InexistentObjectException(
    				DeviceModelDoor.class, "DeviceModelDoor not found!");
    	}
    }
    
	/**
	 * Add a alarmWarning to this DeviceModel
	 * @param alarmWarning
	 */
	public void addAlarmWarning(AlarmWarning alarmWarning) {
		alarmWarnings.add(alarmWarning);
	}
	
    /**
     * Remove a AlarmWarning from the DeviceModel.
     * @param alarmWarning
     * 			: AlarmWarning to be removed
     * @throws InexistentObjectException
     */
    public void removeAlarmWarning(AlarmWarning alarmWarning) 
    		throws InexistentObjectException {
    	if (!alarmWarnings.remove(alarmWarning)) {
    		throw new InexistentObjectException(
    				AlarmWarning.class, "AlarmWarning not found!");
    	}
    }

}