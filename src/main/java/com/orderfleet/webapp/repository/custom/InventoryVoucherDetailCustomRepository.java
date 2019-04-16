package com.orderfleet.webapp.repository.custom;


import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;


public interface InventoryVoucherDetailCustomRepository {

	List<InventoryVoucherDetailDTO> getInventoryDetailListBy(List<String> productCategoryPids ,List<String> productGroupPids,
															List<String> productProfilePids,List<String> stockLocationPids,
															LocalDateTime fromDate,LocalDateTime toDate,
															List<String> documentPids,List<String> productTerritoryPids, 
															List<String> employeePids,String status);
}
