/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package webservices.datastore_simple;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents a simple version of the StorageDevice table.
 * It is kept simple to return only some information to mobile apps.
 * 
 */

@SuppressWarnings("serial")
public class StorageDeviceSimple implements Serializable {
    
	public static class StorageDeviceInformation {
		public String deviceModelKey;
		public String storageDeviceKey;
		public String storageDeviceSerialNumber;
		public String storageDeviceNickname;
		public String temp1;
		public String hum1;
		public String temp2;
		public String hum2;
		public String doorOpenClose;
		
		public StorageDeviceInformation(
				String deviceModelKey,
				String storageDeviceKey,
				String storageDeviceSerialNumber,
				String storageDeviceNickname,
				String temp1,
				String hum1,
				String temp2,
				String hum2,
				String doorOpenClose
				) {
			
			this.deviceModelKey = deviceModelKey;
			this.storageDeviceKey = storageDeviceKey;
			this.storageDeviceSerialNumber = storageDeviceSerialNumber;
			this.storageDeviceNickname = storageDeviceNickname;
			this.temp1 = temp1;
			this.hum1 = hum1;
			this.temp2 = temp2;
			this.hum2 = hum2;
			this.doorOpenClose = doorOpenClose;
		}
	}
	
	public String customerEmail;
	public String customerName;
	public String regionName;
	public String countryName;
	public List<StorageDeviceInformation> storageDevices;
    
    public StorageDeviceSimple(
    		String customerEmail,
    		String customerName,
    		String regionName,
    		String countryName,
    		List<StorageDeviceInformation> storageDevices
    		) {

    	this.customerEmail = customerEmail;
    	this.customerName = customerName;
    	this.regionName = regionName;
    	this.countryName = countryName;
    	this.storageDevices = storageDevices;
    }
    
    /**
     * Compare this StorageDevice with another StorageDevice
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this Program, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof StorageDeviceSimple ) ) return false;
        StorageDeviceSimple d = (StorageDeviceSimple) o;
        return this.customerEmail.equalsIgnoreCase(d.customerEmail);
    }
    
}
