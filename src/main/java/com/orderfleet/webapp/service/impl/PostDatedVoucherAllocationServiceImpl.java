package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.PostDatedVoucher;
import com.orderfleet.webapp.domain.PostDatedVoucherAllocation;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.PostDatedVoucherAllocationRepository;
import com.orderfleet.webapp.repository.PostDatedVoucherRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PostDatedVoucherAllocationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.api.dto.PostDatedVoucherAllocationDTO;

@Service
public class PostDatedVoucherAllocationServiceImpl implements PostDatedVoucherAllocationService {

	private final Logger log = LoggerFactory.getLogger(PostDatedVoucherAllocationServiceImpl.class);
	
	@Inject
	PostDatedVoucherAllocationRepository postDatedVoucherAllocationRepository;
	
	@Inject
	PostDatedVoucherRepository postDatedVoucherRepository;
	
	@Inject
	CompanyRepository companyRepository;
	
	@Override
	public PostDatedVoucherAllocation savePDCAllocation(PostDatedVoucherAllocationDTO postDatedVoucherAllocationDtos) {
		
		return null;
	}

	@Override
	public List<PostDatedVoucherAllocation> saveAllPDCAllocation(List<PostDatedVoucherAllocationDTO> pdcAllocationDtos) {
		List<PostDatedVoucherAllocation> postDatedVoucherAllocations =  new ArrayList<>();
		 Optional<Company> company =companyRepository.findById(SecurityUtils.getCurrentUsersCompanyId());
		 List<String> voucherNumbers = pdcAllocationDtos.stream()
				.map(pdcAlloc -> pdcAlloc.getVoucherNumber()).collect(Collectors.toList());
		log.info("PostdatedVoucher - ref document numbers found :"+voucherNumbers.size());
		log.info(company.get().getId()+"--\n"+voucherNumbers.toString());
		List<PostDatedVoucher> postDatedVouchers = 
				postDatedVoucherRepository.findAllByCompanyIdAndReferenceDocumentNumberIn(company.get().getId(),voucherNumbers);
		log.info("PostdatedVoucherAllocDtos - "+pdcAllocationDtos.size());
		log.info("PostDatedVouchers : "+postDatedVouchers.size());
		for(PostDatedVoucherAllocationDTO dto : pdcAllocationDtos){
			PostDatedVoucherAllocation postDatedVoucherAllocation = new PostDatedVoucherAllocation();
			postDatedVoucherAllocation.setPid(PID_PREFIX+RandomUtil.generatePid());
			postDatedVoucherAllocation.setCreatedDate(LocalDateTime.now());
			postDatedVoucherAllocation.setLastModifiedDate(LocalDateTime.now());
			postDatedVoucherAllocation.setCompany(company.get());
			postDatedVoucherAllocation.setAllocReferenceVoucher(dto.getAllocReferenceVoucher());
			postDatedVoucherAllocation.setAllocReferenceVoucherAmount(dto.getAllocReferenceVoucherAmount());
			postDatedVoucherAllocation.setVoucherNumber(dto.getVoucherNumber());
			Optional<PostDatedVoucher> opPostDatedVoucher = postDatedVouchers.stream()
						.filter(pdc -> pdc.getReferenceDocumentNumber().equals(dto.getVoucherNumber())).findAny();
			if(opPostDatedVoucher.isPresent()){
				postDatedVoucherAllocation.setPostDatedVoucher(opPostDatedVoucher.get());
				postDatedVoucherAllocations.add(postDatedVoucherAllocation);
			}else{
				log.info("------"+postDatedVoucherAllocation.toString());
			}
			
		}
		log.info("Saving post dated voucher allocations: " +postDatedVoucherAllocations.size());
		return postDatedVoucherAllocationRepository.save(postDatedVoucherAllocations);
	}

}
