package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.web.rest.dto.CompanyConfigDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing CompanyConfiguration.
 *
 * @author Sarath
 * @since Jul 28, 2017
 *
 */

@Controller
@RequestMapping("/web")
public class CompanyConfigurationResource {

	private final Logger log = LoggerFactory.getLogger(CompanyConfigurationResource.class);

	@Inject
	private CompanyService companyService;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private CompanyRepository companyRepository;

	@RequestMapping(value = "/company-configuration", method = RequestMethod.GET)
	@Timed
	public String getInventoryCreationForm(Model model) {
		log.debug("Web request to get a company configurations");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		List<CompanyConfiguration> companyConfigurations = companyConfigurationRepository.findAll();
		Map<Company, List<CompanyConfiguration>> groupByCompany = companyConfigurations.stream()
				.collect(Collectors.groupingBy(CompanyConfiguration::getCompany));
		model.addAttribute("companyConfigurations", convertCompanyConfigurationToCompanyConfiguration(groupByCompany));
		return "site_admin/company-configuration";
	}

	private List<CompanyConfigDTO> convertCompanyConfigurationToCompanyConfiguration(
			Map<Company, List<CompanyConfiguration>> groupedCompanyConfigurations) {
		List<CompanyConfigDTO> companyConfigurationDtos = new ArrayList<>();
		groupedCompanyConfigurations.forEach((k, v) -> {
			CompanyConfigDTO mcDto = new CompanyConfigDTO();
			mcDto.setCompanyPid(k.getPid());
			mcDto.setCompanyName(k.getLegalName());
			boolean anyValueExist = false;
			for (CompanyConfiguration cc : v) {
				if (cc.getName().equals(CompanyConfig.DISTANCE_TRAVELED)) {
					mcDto.setDistanceTraveled(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.LOCATION_VARIANCE)) {
					mcDto.setLocationVariance(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.INTERIM_SAVE)) {
					mcDto.setInterimSave(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.REFRESH_PRODUCT_GROUP_PRODUCT)) {
					mcDto.setRefreshProductGroupProduct(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.STAGE_CHANGES_FOR_ACCOUNTING_VOUCHER)) {
					mcDto.setStageChangeAccountingVoucher(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}

			}
			if (anyValueExist) {
				companyConfigurationDtos.add(mcDto);
			}
		});
		return companyConfigurationDtos;
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/company-configuration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> saveCompanyConfiguration(@RequestParam String companyPid,
			@RequestParam String distanceTraveled, @RequestParam String locationVariance,
			@RequestParam String interimSave, @RequestParam String refreshProductGroupProduct,
			@RequestParam String stageChangeAccountingVoucher) throws URISyntaxException {
		log.debug("Web request to save Company Configuration ");
		Company company = null;
		Optional<Company> optionalCompany = companyRepository.findOneByPid(companyPid);
		if (optionalCompany.isPresent()) {
			company = optionalCompany.get();
		} else {
			return new ResponseEntity<>("Invalid Company", HttpStatus.BAD_REQUEST);
		}
		Optional<CompanyConfiguration> optDistanceTraveled = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.DISTANCE_TRAVELED);
		Optional<CompanyConfiguration> optLocationVariance = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.LOCATION_VARIANCE);
		Optional<CompanyConfiguration> optinterimSave = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.INTERIM_SAVE);
		Optional<CompanyConfiguration> optRefreshProductGroupProduct = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.REFRESH_PRODUCT_GROUP_PRODUCT);
		Optional<CompanyConfiguration> optStageChangeAccountingVoucher = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.STAGE_CHANGES_FOR_ACCOUNTING_VOUCHER);

		CompanyConfiguration saveOfflineConfiguration = null;
		CompanyConfiguration promptAttendance = null;
		CompanyConfiguration interimSaveComapny = null;
		CompanyConfiguration refreshProductGroupProductCompany = null;
		CompanyConfiguration stageChangeAccountingVoucherCompany = null;

		if (optDistanceTraveled.isPresent()) {
			saveOfflineConfiguration = optDistanceTraveled.get();
			saveOfflineConfiguration.setValue(distanceTraveled);
		} else {
			saveOfflineConfiguration = new CompanyConfiguration();
			saveOfflineConfiguration.setCompany(company);
			saveOfflineConfiguration.setName(CompanyConfig.DISTANCE_TRAVELED);
			saveOfflineConfiguration.setValue(distanceTraveled);
		}
		companyConfigurationRepository.save(saveOfflineConfiguration);

		if (optLocationVariance.isPresent()) {
			promptAttendance = optLocationVariance.get();
			promptAttendance.setValue(locationVariance);
		} else {
			promptAttendance = new CompanyConfiguration();
			promptAttendance.setCompany(company);
			promptAttendance.setName(CompanyConfig.LOCATION_VARIANCE);
			promptAttendance.setValue(locationVariance);
		}
		companyConfigurationRepository.save(promptAttendance);

		if (optinterimSave.isPresent()) {
			interimSaveComapny = optinterimSave.get();
			interimSaveComapny.setValue(interimSave);
		} else {
			interimSaveComapny = new CompanyConfiguration();
			interimSaveComapny.setCompany(company);
			interimSaveComapny.setName(CompanyConfig.INTERIM_SAVE);
			interimSaveComapny.setValue(interimSave);
		}
		companyConfigurationRepository.save(interimSaveComapny);

		if (optRefreshProductGroupProduct.isPresent()) {
			refreshProductGroupProductCompany = optRefreshProductGroupProduct.get();
			refreshProductGroupProductCompany.setValue(refreshProductGroupProduct);
		} else {
			refreshProductGroupProductCompany = new CompanyConfiguration();
			refreshProductGroupProductCompany.setCompany(company);
			refreshProductGroupProductCompany.setName(CompanyConfig.REFRESH_PRODUCT_GROUP_PRODUCT);
			refreshProductGroupProductCompany.setValue(refreshProductGroupProduct);
		}
		companyConfigurationRepository.save(refreshProductGroupProductCompany);

		if (optStageChangeAccountingVoucher.isPresent()) {
			stageChangeAccountingVoucherCompany = optStageChangeAccountingVoucher.get();
			stageChangeAccountingVoucherCompany.setValue(stageChangeAccountingVoucher);
		} else {
			stageChangeAccountingVoucherCompany = new CompanyConfiguration();
			stageChangeAccountingVoucherCompany.setCompany(company);
			stageChangeAccountingVoucherCompany.setName(CompanyConfig.STAGE_CHANGES_FOR_ACCOUNTING_VOUCHER);
			stageChangeAccountingVoucherCompany.setValue(stageChangeAccountingVoucher);
		}
		companyConfigurationRepository.save(stageChangeAccountingVoucherCompany);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/company-configuration/{companyPid}", method = RequestMethod.GET)
	public @ResponseBody CompanyConfigDTO getCompanyConfiguration(@PathVariable String companyPid)
			throws URISyntaxException {
		log.debug("Web request to get Company Configuration");

		Optional<CompanyConfiguration> optDistanceTraveled = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.DISTANCE_TRAVELED);

		Optional<CompanyConfiguration> optLocationVariance = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.LOCATION_VARIANCE);

		Optional<CompanyConfiguration> optinterimSave = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.INTERIM_SAVE);

		Optional<CompanyConfiguration> optRefreshProductGroupProduct = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.REFRESH_PRODUCT_GROUP_PRODUCT);

		Optional<CompanyConfiguration> optStageChangeAccountingVoucher = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.STAGE_CHANGES_FOR_ACCOUNTING_VOUCHER);

		CompanyConfigDTO companyConfigurationDTO = new CompanyConfigDTO();

		if (optDistanceTraveled.isPresent()) {
			companyConfigurationDTO.setDistanceTraveled(Boolean.valueOf(optDistanceTraveled.get().getValue()));
		}
		if (optLocationVariance.isPresent()) {
			companyConfigurationDTO.setLocationVariance(Boolean.valueOf(optLocationVariance.get().getValue()));
		}
		if (optinterimSave.isPresent()) {
			companyConfigurationDTO.setInterimSave(Boolean.valueOf(optinterimSave.get().getValue()));
		}
		if (optRefreshProductGroupProduct.isPresent()) {
			companyConfigurationDTO
					.setRefreshProductGroupProduct(Boolean.valueOf(optRefreshProductGroupProduct.get().getValue()));
		}
		if (optStageChangeAccountingVoucher.isPresent()) {
			companyConfigurationDTO
					.setStageChangeAccountingVoucher(Boolean.valueOf(optStageChangeAccountingVoucher.get().getValue()));
		}

		return companyConfigurationDTO;
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/company-configuration/delete/{companyPid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteCompanyConfiguration(@PathVariable String companyPid) throws URISyntaxException {
		log.debug("Web request to delete Company Configuration compantPid : {}", companyPid);

		Optional<CompanyConfiguration> optDistanceTraveled = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.DISTANCE_TRAVELED);
		Optional<CompanyConfiguration> optLocationVariance = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.LOCATION_VARIANCE);
		Optional<CompanyConfiguration> optinterimSave = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.INTERIM_SAVE);
		Optional<CompanyConfiguration> optRefreshProductGroupProduct = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.REFRESH_PRODUCT_GROUP_PRODUCT);
		Optional<CompanyConfiguration> optStageChangeAccountingVoucher = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.STAGE_CHANGES_FOR_ACCOUNTING_VOUCHER);

		if (optDistanceTraveled.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optDistanceTraveled.get().getCompany().getId(),
					CompanyConfig.DISTANCE_TRAVELED);
		}
		if (optLocationVariance.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optLocationVariance.get().getCompany().getId(),
					CompanyConfig.LOCATION_VARIANCE);
		}
		if (optinterimSave.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optinterimSave.get().getCompany().getId(),
					CompanyConfig.INTERIM_SAVE);
		}
		if (optRefreshProductGroupProduct.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(
					optRefreshProductGroupProduct.get().getCompany().getId(),
					CompanyConfig.REFRESH_PRODUCT_GROUP_PRODUCT);
		}
		if (optStageChangeAccountingVoucher.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(
					optStageChangeAccountingVoucher.get().getCompany().getId(),
					CompanyConfig.STAGE_CHANGES_FOR_ACCOUNTING_VOUCHER);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("companyConfiguration", companyPid.toString())).build();
	}
}
