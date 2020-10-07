package com.orderfleet.webapp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.PostDatedVoucherAllocation;
import com.orderfleet.webapp.web.rest.api.dto.PostDatedVoucherAllocationDTO;

public interface PostDatedVoucherAllocationService {

	String PID_PREFIX = "PDCALOC-";
	
	PostDatedVoucherAllocation savePDCAllocation(PostDatedVoucherAllocationDTO postDatedVoucherAllocation);
	List<PostDatedVoucherAllocation> saveAllPDCAllocation(List<PostDatedVoucherAllocationDTO> pdcAllocations);
	
}
