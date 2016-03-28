/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package webservices.datastore_simple;

import java.io.Serializable;

/**
 * This class represents a simple version of the SensorReading table.
 * It is kept simple to return only some information to mobile apps.
 * 
 */

@SuppressWarnings("serial")
public class SensorReadingSimple implements Serializable {
    
	public String key;
	public String storageDeviceKey;
	public String sensorInstanceKey;
	public String sensorInstanceLabel;
	public String sensorReadingValue;
	public String sensorReadingTime;
    
    /**
     * ChannelSimple constructor.
     * @param key
     * 			: SensorReading key
     * @param storageDeviceKey
     * 			: StorageDevice key
     * @param sensorInstanceKey
     * 			: SensorInstance key
     * @param sensorInstanceLabel
     * 			: SensorInstance label
     * @param sensorReadingValue
     * 			: SensorReading value
     * @param sensorReadingTime
     * 			: SensorReading time
     */
    public SensorReadingSimple(
    		String key,
    		String storageDeviceKey,
    		String sensorInstanceKey,
    		String sensorInstanceLabel,
    		String sensorReadingValue,
    		String sensorReadingTime) {

    	this.key = key;
    	this.storageDeviceKey = storageDeviceKey;
    	this.sensorInstanceKey = sensorInstanceKey;
    	this.sensorInstanceLabel = sensorInstanceLabel;
    	this.sensorReadingValue = sensorReadingValue;
    	this.sensorReadingTime = sensorReadingTime;
    }
    
    /**
     * Compare this SensorReading with another SensorReading
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this SensorReading, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof SensorReadingSimple ) ) return false;
        SensorReadingSimple srs = (SensorReadingSimple) o;
        return this.key.equals(srs.key);
    }
    
}
