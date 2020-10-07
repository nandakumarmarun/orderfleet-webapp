
package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.PostDatedVoucher;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.PostDatedVoucherRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PostDatedVoucherService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.api.dto.PostDatedVoucherDTO;

/**
 * @author Anish
 * @since September 3, 2018
 *
 */
@Service
public class PostDatedVoucherServiceImpl implements PostDatedVoucherService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Inject
	private PostDatedVoucherRepository postDatedVoucherRepository;
	
	@Inject
	private AccountProfileRepository accountProfileRepository;
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Override
	public PostDatedVoucherDTO save(PostDatedVoucherDTO dto) {
			
			Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
			PostDatedVoucher postDatedVoucher = new PostDatedVoucher();
			log.debug("AccountProfile: "+dto.getAccountProfileName()+"--");
			Optional<AccountProfile> accountProfile = accountProfileRepository.findByCompanyIdAndNameIgnoreCase
															(company.getId() , dto.getAccountProfileName());
			postDatedVoucher.setAccountProfile(accountProfile.get());
			postDatedVoucher.setCompany(company);
			postDatedVoucher.setCreatedDate(LocalDateTime.now());
			postDatedVoucher.setLastModifiedDate(LocalDateTime.now());
			postDatedVoucher.setPid(PostDatedVoucherService.PID_PREFIX+RandomUtil.generatePid());
			postDatedVoucher.setReceivableBillNumber(dto.getReferenceVoucher());
			postDatedVoucher.setReferenceDocumentDate(convertStringToLocalDate(dto.getReferenceDocumentDate()));
			postDatedVoucher.setReferenceDocumentAmount(dto.getReferenceDocumentAmount());
			postDatedVoucher.setReferenceDocumentNumber(dto.getReferenceDocumentNumber());
			postDatedVoucher.setRemark(dto.getRemark());
			postDatedVoucher = postDatedVoucherRepository.save(postDatedVoucher);
			log.debug("Saved post Dated Cheques");
			return new PostDatedVoucherDTO(postDatedVoucher);
	}
	
	@Override
	public List<PostDatedVoucherDTO> saveAll(List<PostDatedVoucherDTO> postDatedVoucherDtos) {
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		List<String> names = postDatedVoucherDtos.stream().map(
							pdc -> pdc.getAccountProfileName()).collect(Collectors.toList());
		List<AccountProfile> accountProfiles = 
				accountProfileRepository.findByCompanyIdAndNameIgnoreCaseIn(company.getId(),names);
		List<PostDatedVoucher> postDatedVouchers = new ArrayList<>();
		
		for(PostDatedVoucherDTO dto : postDatedVoucherDtos){
				Optional<AccountProfile> accountProfile = accountProfiles.stream().filter(ap -> ap.getName().equals(dto.getAccountProfileName())).findAny();
				PostDatedVoucher postDatedVoucher = new PostDatedVoucher();
				postDatedVoucher.setAccountProfile(accountProfile.get());
				postDatedVoucher.setCompany(company);
				postDatedVoucher.setCreatedDate(LocalDateTime.now());
				postDatedVoucher.setLastModifiedDate(LocalDateTime.now());
				postDatedVoucher.setPid(PostDatedVoucherService.PID_PREFIX+RandomUtil.generatePid());
				postDatedVoucher.setReceivableBillNumber(dto.getReferenceVoucher());
				postDatedVoucher.setReferenceDocumentDate(convertStringToLocalDate(dto.getReferenceDocumentDate()));
				postDatedVoucher.setReferenceDocumentAmount(dto.getReferenceDocumentAmount());
				postDatedVoucher.setReferenceDocumentNumber(dto.getReferenceDocumentNumber());
				postDatedVoucher.setRemark(dto.getRemark());
				postDatedVouchers.add(postDatedVoucher);
		}
		
		
		
		postDatedVouchers = postDatedVoucherRepository.save(postDatedVouchers);
		
		log.debug("Saved post Dated Cheques");
		List<PostDatedVoucherDTO> pdcDto = postDatedVouchers.stream().map(pdc -> 
									new PostDatedVoucherDTO(pdc)).collect(Collectors.toList());
		return pdcDto;
	}
	@Override
	public List<PostDatedVoucherDTO> findAllPostDatedVoucherByAccountProfilePid(String accountPid) {
		
		return postDatedVoucherRepository.findAllByAccountProfilePid(accountPid)
										.stream()
										.map(pdc -> new PostDatedVoucherDTO(pdc))
										.collect(Collectors.toList());
	}
	
	@Override
	public List<PostDatedVoucherDTO> findAllPostDatedVouchers() {
		
		List<PostDatedVoucherDTO> result = postDatedVoucherRepository.findAllByCompanyId()
									.stream()
									.map(pdc -> new PostDatedVoucherDTO(pdc))
									.collect(Collectors.toList());
		
		
		return result;
	}

	
	
	
	
	private LocalDate convertStringToLocalDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(date, formatter);
		return localDate;
	}


	
}
