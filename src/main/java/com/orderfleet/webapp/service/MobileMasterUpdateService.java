package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.StockDetailsDTO;

/**
 * Service Interface for managing InventoryVoucherHeader.
 * 
 * @author Muhammed Riyas T
 * @since July 19, 2016
 */
public interface MobileMasterUpdateService {

	String PID_PREFIX = "MMU-";

}
