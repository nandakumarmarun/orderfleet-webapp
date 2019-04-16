package com.orderfleet.webapp.service;


public interface SetOrderStatusInventoryService {
	
	String PID_PREFIX = "IVOSH-";

	void saveSetOrderStatusInventoryHistory(String inventoryPid,String status);
}
