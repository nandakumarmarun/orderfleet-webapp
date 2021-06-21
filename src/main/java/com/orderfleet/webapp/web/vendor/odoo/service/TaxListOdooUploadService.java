package com.orderfleet.webapp.web.vendor.odoo.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.TaxMaster;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.TaxMasterRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.TaxMasterService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooTaxList;

@Service
public class TaxListOdooUploadService {
	private final Logger log = LoggerFactory.getLogger(TaxListOdooUploadService.class);

	private final CompanyRepository companyRepository;

	private final TaxMasterRepository taxMasterRepository;

	private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	public TaxListOdooUploadService(CompanyRepository companyRepository, TaxMasterRepository taxMasterRepository,
			BulkOperationRepositoryCustom bulkOperationRepositoryCustom) {
		super();
		this.companyRepository = companyRepository;
		this.taxMasterRepository = taxMasterRepository;
		this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
	}

	@Transactional
	public void saveTaxList(final List<OdooTaxList> list) {

		log.info("Saving Tax List...");
		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		List<TaxMaster> taxMasters = taxMasterRepository.findAllByCompanyId(company.getId());

		Set<TaxMaster> saveTaxMasters = new HashSet<>();

		for (OdooTaxList taxDto : list) {
			// check exist by name, only one exist with a name
			Optional<TaxMaster> optionalTM = taxMasters.stream()
					.filter(pc -> pc.getVatName().equalsIgnoreCase(taxDto.getName())).findAny();

			TaxMaster taxMaster;
			if (optionalTM.isPresent()) {
				taxMaster = optionalTM.get();
				if (optionalTM.get().getTaxId().equals(String.valueOf(taxDto.getId()))) {
					taxMaster.setVatName(taxDto.getName() + ".");
				}
			} else {
				taxMaster = new TaxMaster();
				taxMaster.setPid(TaxMasterService.PID_PREFIX + RandomUtil.generatePid());
				taxMaster.setVatName(taxDto.getName());
			}
			double vatPercentage = taxDto.getAmount() * 100;
			taxMaster.setVatPercentage(vatPercentage);
			taxMaster.setVatClass(taxDto.getType());
			taxMaster.setCompany(company);
			taxMaster.setDescription("" + taxDto.getId());
			taxMaster.setTaxId("" + taxDto.getId());
			saveTaxMasters.add(taxMaster);
		}
		bulkOperationRepositoryCustom.bulkSaveTaxMasters(saveTaxMasters);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}
}
