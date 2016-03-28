/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package webservices.datastore_simple;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents a simple version of the StorageItem table.
 * It is kept simple to return only some information to mobile apps.
 * 
 */

@SuppressWarnings("serial")
public class StorageItemSimple implements Serializable {
    
	public static class StorageItemInstanceInformation {
		
		public String storageItemInstanceKey;
		public String storageItemInstanceOwner;
		public String storageItemInstanceSerialNumber;
		public String storageItemInstanceLabel;
		public String storageItemInstanceExpirationTime;
		public String storageItemInstanceWorksheet;
		public String transactionTime;
		public String transactionPerformedBy;
		public String transactionAction;
		public String storageDeviceSerialNumber;
		public Integer storageDeviceDoorNumber;
		public String storageDevicePartitionName;
		
		public StorageItemInstanceInformation(
				String storageItemInstanceKey,
				String storageItemInstanceOwner,
				String storageItemInstanceSerialNumber,
				String storageItemInstanceLabel,
				String storageItemInstanceExpirationTime,
				String storageItemInstanceWorksheet,
				String transactionTime,
				String transactionPerformedBy,
				String transactionAction,
				String storageDeviceSerialNumber,
				Integer storageDeviceDoorNumber,
				String storageDevicePartitionName
				) {
			
			this.storageItemInstanceKey = storageItemInstanceKey;
			this.storageItemInstanceOwner = storageItemInstanceOwner;
			this.storageItemInstanceSerialNumber = storageItemInstanceSerialNumber;
			this.storageItemInstanceLabel = storageItemInstanceLabel;
			this.storageItemInstanceExpirationTime = storageItemInstanceExpirationTime;
			this.storageItemInstanceWorksheet = storageItemInstanceWorksheet;
			this.transactionTime = transactionTime;
			this.transactionPerformedBy = transactionPerformedBy;
			this.transactionAction = transactionAction;
			this.storageDeviceSerialNumber = storageDeviceSerialNumber;
			this.storageDeviceDoorNumber = storageDeviceDoorNumber;
			this.storageDevicePartitionName = storageDevicePartitionName;
		}
	}
	
	public static class StorageItemInformation {
		
		public String storageItemKey;
		public String storageItemPartNumber;
		public String storageItemName;
		public String storageItemDescription;
		public Integer storageItemMSDLevel;
		public List<StorageItemInstanceInformation> storageItemInstances;
		
		public StorageItemInformation(
				String storageItemKey,
				String storageItemPartNumber,
				String storageItemName,
				String storageItemDescription,
				Integer storageItemMSDLevel,
				List<StorageItemInstanceInformation> storageItemInstances
				) {
			
			this.storageItemKey = storageItemKey;
			this.storageItemPartNumber = storageItemPartNumber;
			this.storageItemName = storageItemName;
			this.storageItemDescription = storageItemDescription;
			this.storageItemMSDLevel = storageItemMSDLevel;
			this.storageItemInstances = storageItemInstances;
		}
	}
	
	public String customerEmail;
	public List<StorageItemInformation> storageItems;
    
    public StorageItemSimple(
    		String customerEmail,
    		List<StorageItemInformation> storageItems
    		) {

    	this.customerEmail = customerEmail;
    	this.storageItems = storageItems;
    }
    
    /**
     * Compare this StorageItem with another StorageItem
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this Program, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof StorageItemSimple ) ) return false;
        StorageItemSimple d = (StorageItemSimple) o;
        return this.customerEmail.equalsIgnoreCase(d.customerEmail);
    }
}
