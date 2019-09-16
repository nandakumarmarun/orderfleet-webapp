package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;

public interface InventoryVoucherDetailService {
	List<InventoryVoucherDetailDTO> findAllByCompanyIdAndDocumentPidAndDateBetween(String sort,String order ,LocalDateTime fromDate, LocalDateTime toDate,
			String documentPid,String employeePid);
	
	List<InventoryVoucherDetailDTO> findAllByCompanyIdAndDateBetweenOrderBy(String sort,String order, LocalDateTime fromDate, LocalDateTime toDate,
			List<Document> documents,String employeePid);
	
	InventoryVoucherDetail updateInventoryVoucherDetail(InventoryVoucherDetailDTO ivdDto);

}
