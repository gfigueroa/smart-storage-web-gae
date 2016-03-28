/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package webservices.external_resources;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import util.DateManager;
import webservices.datastore_simple.SystemSimple;
import datastore.System;
import datastore.SystemManager;

/**
 * This class represents the System
 * JDO Object which contains some version numbers
 * required by the mobile app.
 */

public class SystemResource extends ServerResource {

	/**
	 * Returns the System table instance as a JSON object.
	 * @return The instance of the System object in JSON format
	 */
    @Get("json")
    public SystemSimple toJson() {
        
    	
    	System system = SystemManager.getSystem();
    	
        SystemSimple systemSimple = new SystemSimple(
        		system.getKey(),
        		system.getCustomerListVersion(),
        		system.getDeviceServiceTypeListVersion(),
        		system.getDeviceModelListVersion(),
        		system.getStorageDeviceContainerModelListVersion(),
        		system.getSensorTypeListVersion(),
        		system.getAlarmTriggerListVersion(),
        		system.getOldestAppVersionSupportedString(),
        		DateManager.printDateAsString(system.getSystemTime())
        		);
        
        return systemSimple;
    }

}
