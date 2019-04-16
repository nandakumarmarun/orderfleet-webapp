package com.orderfleet.webapp.service;

import com.orderfleet.webapp.web.rest.dto.InventoryClosingHolder;

public interface InventoryClosingHeaderService {

	public void processInventoryClosing(InventoryClosingHolder inventoryClosingHolder);
}
