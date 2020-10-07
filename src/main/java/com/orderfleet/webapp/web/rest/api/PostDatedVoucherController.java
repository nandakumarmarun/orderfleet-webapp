
package com.orderfleet.webapp.web.rest.api;


import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.PostDatedVoucher;
import com.orderfleet.webapp.domain.PostDatedVoucherAllocation;
import com.orderfleet.webapp.repository.PostDatedVoucherRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PostDatedVoucherService;
import com.orderfleet.webapp.web.rest.api.dto.PostDatedVoucherDTO;


/**
 * @author Anish
 * @since September 3, 2018
 *
 */
@RestController
@RequestMapping("/api")
public class PostDatedVoucherController {

	private final Logger log = LoggerFactory.getLogger(PostDatedVoucherController.class);
	
	
	@Inject
	PostDatedVoucherService postDatedVoucherService;
	
	@Inject
	PostDatedVoucherRepository postDatedVoucherRepository;
	
	
//	@PostMapping(path = "/tp/v1/post-dated-vouchers", produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<?> saveAllPostDatedVouchers(@Valid @RequestBody List<PostDatedVoucherDTO> postDatedVoucherDtos)
//	{
//		log.debug("API request for post dated voucher -- List size -- {}",postDatedVoucherDtos.size());
//		
//		postDatedVoucherRepository.deleteByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
//		
//		List<PostDatedVoucherDTO> result = new ArrayList<PostDatedVoucherDTO>();
//
//		
//		for(PostDatedVoucherDTO dto : postDatedVoucherDtos)
//		{
//			result.add(postDatedVoucherService.save(dto));	
//		}
//
//		
//		if(result.size() == postDatedVoucherDtos.size())
//		{
//			return new ResponseEntity<>(HttpStatus.OK);
//		}
//		else {
//			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
	
	@GetMapping(path = "/post-dated-vouchers", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> findAllPostDatedVoucherByCompany()
	{
		List<PostDatedVoucher> postDatedVoucherList = postDatedVoucherRepository.findAllByCompanyId();
//		for(PostDatedVoucher pdc : postDatedVoucherList){
//			System.out.println(pdc.getAccountProfile() +" "+pdc.getReferenceDocumentNumber());
//			for(PostDatedVoucherAllocation pdcAlloc : pdc.getPostDatedVoucherAllocation()){
//				System.out.println(pdcAlloc.getAllocReferenceVoucher()+" "+pdcAlloc.getVoucherNumber());
//			}
//		}
		
		List<PostDatedVoucherDTO> postDatedVoucherDtos = postDatedVoucherList.stream().map(
															postDatedVoucher -> new PostDatedVoucherDTO(postDatedVoucher))
															.collect(Collectors.toList());
		return new ResponseEntity<>( postDatedVoucherDtos,HttpStatus.OK);
	}
}
