/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package webservices.external_resources;

import java.util.ArrayList;
import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import datastore.StorageDevice;
import datastore.RegionManager;
import datastore.Customer;
import datastore.CustomerManager;

/**
 * This class represents the list of Customers
 * as a Resource with only one representation
 */

public class CustomersResource extends ServerResource {
	
	/**
	 * Returns the simple station list as a JSON object.
	 * @return An ArrayList of StationSimple in JSON format
	 */
    @Get("json")
    public ArrayList<Customer> toJson() {
        
//        List<Customer> customers = CustomerManager.getAllCustomers();
//        
//        ArrayList<StationSimple> simpleStations = new ArrayList<StationSimple>();
//        for (Customer customer : customers) {
//        	
//        	// First, get and create the channels
//        	ArrayList<ChannelSimple> simpleChannels = new ArrayList<ChannelSimple>();
//        	List<StorageDevice> storageDevices = ChannelManager.getStationChannels(customer.getKey());
//        	for (StorageDevice storageDevice : storageDevices) {
//        		Key channelKey = storageDevice.getKey();
//        		BlobKey firstSlideBlobKey = 
//        				ChannelManager.getFirstSlideBlobKey(channelKey);
//        		ChannelSimple channelSimple = new ChannelSimple(
//        				KeyFactory.keyToString(storageDevice.getKey()),
//        				storageDevice.getChannelName(),
//        				storageDevice.getChannelNumber(),
//        				firstSlideBlobKey
//        				);
//        		simpleChannels.add(channelSimple);
//        	}
//        	
//        	// Then create the stations
//        	StationSimple stationSimple = new StationSimple(
//        			KeyFactory.keyToString(customer.getKey()),
//        			customer.getStationType(),
//        			customer.getStationPrivilegeLevel(),
//        			customer.getStationName(),
//        			customer.getStationNumber(),
//        			customer.getStationDescription(),
//        			RegionManager.getRegion(customer.getRegion()).getRegionName(),
//        			customer.getStationAddress() != null ? 
//        					customer.getStationAddress().getAddress() : "",
//        			customer.getStationWebsite() != null ? 
//        					customer.getStationWebsite().getValue() : "",
//        			customer.getUser().getUserEmail().getEmail(),
//        			customer.getStationLogo() != null ? 
//        					customer.getStationLogo() : new BlobKey(""),
//        			customer.getStationComments() != null ?
//        					customer.getStationComments() : "",
//        			simpleChannels);
//        	simpleStations.add(stationSimple);
//        }
        
        return null;
    }

}