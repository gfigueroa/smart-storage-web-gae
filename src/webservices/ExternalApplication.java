/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package webservices;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import webservices.external_resources.AlarmTriggerMessagesResource;
import webservices.external_resources.AlarmWarningMessagesResource;
import webservices.external_resources.CustomerProfileResource;
import webservices.external_resources.DeviceModelsResource;
import webservices.external_resources.SensorDataResource;
import webservices.external_resources.CustomersResource;
import webservices.external_resources.StorageDevicesResource;
import webservices.external_resources.StorageItemsResource;
import webservices.external_resources.SystemResource;

/**
 * This class represents an instance of the Restlet Application
 */

public class ExternalApplication extends Application {

    /**
     * Creates a root Restlet that will receive all incoming calls
     * @return A router instance
     */
    @Override
    public Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a
        // new instance of each Resource
        Router router = new Router(getContext());
        
        // Define route to 1.1 SystemResource
        router.attach("/system", SystemResource.class);
        
        // Define route to 1.2 DeviceModelsResource
        router.attach("/deviceModels/{queryinfo}", DeviceModelsResource.class);
        
        // Define route to 1.4 StorageDevicesResource
        router.attach("/storageDevices/{queryinfo}", StorageDevicesResource.class);
        
        // Define route to CustomerProfileResource
        router.attach("/customerProfile/{queryinfo}", 
        		CustomerProfileResource.class);
        
        // Define route to CustomersResource
        router.attach("/customers/{queryinfo}", 
        		CustomersResource.class);
        
        // Define route to 1.5 SensorDataResource
        router.attach("/sensorData/{queryinfo}", 
        		SensorDataResource.class);
        
        // Define route to 1.6 AlarmWarningMessagesResource
        router.attach("/alarmWarningMessages/{queryinfo}", 
        		AlarmWarningMessagesResource.class);
        
        // Define route to 1.7 AlarmTriggerMessagesResource
        router.attach("/alarmTriggerMessages/{queryinfo}", 
        		AlarmTriggerMessagesResource.class);
        
        // Define route to 1.8 StorageItemsResource
        router.attach("/storageItems/{queryinfo}", 
        		StorageItemsResource.class);
        
        return router;
    }

}
