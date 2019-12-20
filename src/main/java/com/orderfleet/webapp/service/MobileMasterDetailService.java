package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.MobileMasterDetail;
import com.orderfleet.webapp.domain.MobileMasterUpdate;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.MobileMasterDetailDTO;
import com.orderfleet.webapp.web.rest.dto.StockDetailsDTO;


public interface MobileMasterDetailService {

	String PID_PREFIX = "MMD-";
	List<MobileMasterDetail> convertMobileMasterDetails(List<MobileMasterDetailDTO> mobileMasterDetailDtos,MobileMasterUpdate mobileMasterUpdate);
}
