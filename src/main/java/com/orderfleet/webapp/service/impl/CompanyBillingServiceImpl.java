package com.orderfleet.webapp.service.impl;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyBilling;
import com.orderfleet.webapp.repository.CompanyBillingRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.CompanyBillingDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CompanyBillingServiceImpl {
	private final Logger log = LoggerFactory.getLogger(CompanyBillingServiceImpl.class);

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private CompanyBillingRepository companyBillingRepository;

	public CompanyBilling save(CompanyBillingDTO companyDTO) {
		// TODO Auto-generated method stub

		log.debug("web request to save biling ");
		companyDTO.setPid("BILL-" + RandomUtil.generatePid());
		CompanyBilling billing = new CompanyBilling();
		Optional<Company> company = companyRepository.findOneByPid(companyDTO.getCompanyPid());
		billing.setPid(companyDTO.getPid());
		billing.setCompany(company.get());
		billing.setBillingPeriod(companyDTO.getBillingPeriod());
		billing.setNoOfMonths(companyDTO.getNoOfMonths());
		billing.setLastBilledDate(companyDTO.getLastBillDate());
		billing.setNext_bill_date(companyDTO.getDueBillDate());
		companyBillingRepository.save(billing);
		System.out.println("bill :" + billing);

		return billing;
	}

	public Optional<CompanyBilling> findByCompanyPid(String companyPid) {
		// TODO Auto-generated method stub
		Optional<Company> company = companyRepository.findOneByPid(companyPid);

		Optional<CompanyBilling> bill = companyBillingRepository.findOneByCompanyId(company.get().getId());

		return bill;
	}

	public CompanyBilling saveNewBillDate(String companyPid) {
		// TODO Auto-generated method stub

		Optional<CompanyBilling> billing = companyBillingRepository.findOneBycompanyPid(companyPid);
		CompanyBilling bill = null;
		if (billing.isPresent()) {
			System.out.println("Enter here");
			bill = billing.get();
			bill.setLastBilledDate(billing.get().getNext_bill_date());
			LocalDate dueDate = billing.get().getNext_bill_date()
					.plusMonths(Long.valueOf(billing.get().getNoOfMonths()));
			bill.setNext_bill_date(dueDate);
			companyBillingRepository.save(bill);

		}
		log.info("Saved new date :" + bill);
		return bill;
	}

	public Optional<CompanyBillingDTO> findOneByPid(String pid) {
		log.debug("Request to get Company by pid : {}", pid);
		return companyBillingRepository.findOneByPid(pid).map(company -> {
			CompanyBillingDTO companyDTO = new CompanyBillingDTO(company);
			return companyDTO;
		});
	}

	public CompanyBillingDTO update(CompanyBillingDTO companyDTO) {
		// TODO Auto-generated method stub
		log.debug("Request to Update Company : {}", companyDTO);

		Optional<Company> comp = companyRepository.findOneByPid(companyDTO.getCompanyPid());
		return companyBillingRepository.findOneByPid(companyDTO.getPid()).map(company -> {

			company.setCompany(comp.get());
			company.setBillingPeriod(companyDTO.getBillingPeriod());
			company.setNoOfMonths(companyDTO.getNoOfMonths());
			company.setLastBilledDate(companyDTO.getLastBillDate());
			company.setNext_bill_date(companyDTO.getDueBillDate());

			company = companyBillingRepository.save(company);
			CompanyBillingDTO result = new CompanyBillingDTO(company);
			return result;
		}).orElse(null);

	}
}
