/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package webservices.datastore_simple;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents a simple version of the DeviceModel table.
 * It is kept simple to return only some information to mobile apps.
 * 
 */

@SuppressWarnings("serial")
public class DeviceModelSimple implements Serializable {
    
	public static class DeviceModelPartitionSimple {
		public String deviceModelPartitionKey;
		public String deviceModelPartitionName;
		
		public DeviceModelPartitionSimple(
				String deviceModelPartitionKey,
				String deviceModelPartitionName) {
			
			this.deviceModelPartitionKey = deviceModelPartitionKey;
			this.deviceModelPartitionName = deviceModelPartitionName;
		}
	}
	
	public static class DeviceModelDoorSimple {
		public String deviceModelDoorKey;
		public Integer deviceModelDoorNumber;
		public List<DeviceModelPartitionSimple> deviceModelPartitions;
		
		public DeviceModelDoorSimple(
				String deviceModelDoorKey,
				Integer deviceModelDoorNumber,
				List<DeviceModelPartitionSimple> deviceModelPartitions) {
			
			this.deviceModelDoorKey = deviceModelDoorKey;
			this.deviceModelDoorNumber = deviceModelDoorNumber;
			this.deviceModelPartitions = deviceModelPartitions;
		}
	}
	
	public String deviceModelKey;
	public String deviceServiceTypeName;
	public String deviceModelName;
	public String deviceModelDescription;
	public String deviceModelDesignTime;
	public Boolean temp1Hum1;
	public Boolean temp2Hum2;
	public Boolean doorOpenClose;
	public Boolean co2;
	public Boolean co;
	public Boolean flux;
	public Boolean infrared;
	public Boolean imageUpload;
	public Boolean alcohol;
	public Boolean electricCurrent;
	public Boolean atmosphericPressure;
	public String deviceModelCreationTime;
	public String deviceModelModificationTime;
	public List<DeviceModelDoorSimple> deviceModelDoors;
    
    public DeviceModelSimple(
    		String deviceModelKey,
    		String deviceServiceTypeName,
    		String deviceModelName, 
    		String deviceModelDescription,
    		String deviceModelDesignTime,
    		Boolean temp1Hum1,
    		Boolean temp2Hum2,
    		Boolean doorOpenClose,
    		Boolean co2,
    		Boolean co,
    		Boolean flux,
    		Boolean infrared,
    		Boolean imageUpload,
    		Boolean alcohol,
    		Boolean electricCurrent,
    		Boolean atmosphericPressure,
    		String deviceModelCreationTime,
    		String deviceModelModificationTime,
    		List<DeviceModelDoorSimple> deviceModelDoors
    		) {

    	this.deviceModelKey = deviceModelKey;
    	this.deviceServiceTypeName = deviceServiceTypeName;
    	this.deviceModelName = deviceModelName;
        this.deviceModelDescription = deviceModelDescription;
        this.deviceModelDesignTime = deviceModelDesignTime;
        this.temp1Hum1 = temp1Hum1;
        this.temp2Hum2 = temp2Hum2;
        this.doorOpenClose = doorOpenClose;
        this.co2 = co2;
        this.co = co;
        this.flux = flux;
        this.infrared = infrared;
        this.imageUpload = imageUpload;
        this.alcohol = alcohol;
        this.electricCurrent = electricCurrent;
        this.atmosphericPressure = atmosphericPressure;
        this.deviceModelCreationTime = deviceModelCreationTime;
        this.deviceModelModificationTime = deviceModelModificationTime;
        this.deviceModelDoors = deviceModelDoors;
    }
    
    /**
     * Compare this DeviceModel with another DeviceModel
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this DeviceModel, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof DeviceModelSimple ) ) return false;
        DeviceModelSimple d = (DeviceModelSimple) o;
        return this.deviceModelKey.equals(d.deviceModelKey);
    }
    
}
