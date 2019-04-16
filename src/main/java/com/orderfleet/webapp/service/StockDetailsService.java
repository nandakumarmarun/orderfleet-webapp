package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.web.rest.dto.StockDetailsDTO;

public interface StockDetailsService {

	List<StockDetailsDTO>  findOtherStockItems(User user);
}
