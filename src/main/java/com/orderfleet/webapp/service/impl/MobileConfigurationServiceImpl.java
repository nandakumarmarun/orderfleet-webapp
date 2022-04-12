package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.MobileConfiguration;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.MobileConfigurationRepository;
import com.orderfleet.webapp.service.MobileConfigurationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.MobileConfigurationDTO;

/**
 * Service Implementation for managing MobileConfiguration.
 *
 * @author Sarath
 * @since Jul 17, 2017
 *
 */
@Service
@Transactional
public class MobileConfigurationServiceImpl implements MobileConfigurationService {

	private final Logger log = LoggerFactory.getLogger(MobileConfigurationServiceImpl.class);

	@Inject
	private MobileConfigurationRepository mobileConfigurationRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public MobileConfigurationDTO saveMobileConfiguration(MobileConfigurationDTO mobileConfigurationDTO) {
		log.debug("Request to save MobileConfiguration : {}", mobileConfigurationDTO);
		MobileConfiguration configuration = new MobileConfiguration();

		configuration.setPid(MobileConfigurationService.PID_PREFIX + RandomUtil.generatePid()); // set
		configuration.setCompany(companyRepository.findOneByPid(mobileConfigurationDTO.getCompanyPid()).get());

		configuration.setAttendanceMarkingRequired(mobileConfigurationDTO.getAttendanceMarkingRequired());
		configuration.setBuildDueDetails(mobileConfigurationDTO.getBuildDueDetail());
		configuration.setDayPlanDownloadRequired(mobileConfigurationDTO.getDayPlanDownloadRequired());
		configuration.setIncludeAddressInAccountlist(mobileConfigurationDTO.getIncludeAddressInAccountList());
		configuration.setMasterDataUpdateRequired(mobileConfigurationDTO.getMasterDataUpdateRequired());
		configuration.setPromptAttendanceMarking(mobileConfigurationDTO.getPromptAttendanceMarkingvalue());
		configuration.setPromptDayPlanUpdate(mobileConfigurationDTO.getPromptDayPlanUpdate());
		configuration.setPromptMasterdataUpdate(mobileConfigurationDTO.getPromptMasterDataUpdate());
		configuration.setTaskExecutionOfflineSave(mobileConfigurationDTO.getTaskExecutionSaveOfflineValue());
		configuration.setShowAllActivityCount(mobileConfigurationDTO.getShowAllActivityCount());
		configuration.setCreateTerritory(mobileConfigurationDTO.getCreateTerritory());
		configuration.setRealTimeProductPriceEnabled(mobileConfigurationDTO.getRealTimeProductPriceEnabled());
		configuration.setHasGeoTag(mobileConfigurationDTO.getHasGeoTag());
		configuration.setHasPostDatedVoucherEnabled(mobileConfigurationDTO.getHasPostDatedVoucher());
		configuration.setPromptVehicleMaster(mobileConfigurationDTO.getPromptVehicleMaster());
		configuration.setAddNewCustomer(mobileConfigurationDTO.getAddNewCustomer());
		configuration.setPreventNegativeStock(mobileConfigurationDTO.getPreventNegativeStock());
		configuration.setVoucherNumberGenerationType(mobileConfigurationDTO.getVoucherNumberGenerationType());
		configuration.setInventoryVoucherUIType(mobileConfigurationDTO.getInventoryVoucherUIType());
		configuration.setCartType(mobileConfigurationDTO.getCartType());
		configuration.setAccountProfileDisplayName(mobileConfigurationDTO.getAccountProfileDisplayName());
		configuration.setAccountProfilePrintName(mobileConfigurationDTO.getAccountProfilePrintName());
		configuration.setProductProfileDisplayName(mobileConfigurationDTO.getProductProfileDisplayName());
		configuration.setProductProfilePrintName(mobileConfigurationDTO.getProductProfilePrintName());
		configuration.setKfcEnabled(mobileConfigurationDTO.getKfcEnabled());
		configuration.setGpsMandatory(mobileConfigurationDTO.getGpsMandatory());
		configuration.setEnableSecondarySales(mobileConfigurationDTO.getEnableSecondarySales());
		configuration.setEnableAttendanceImage(mobileConfigurationDTO.getEnableAttendanceImage());
		configuration.setSmartSearch(mobileConfigurationDTO.getSmartSearch());
		configuration.setSalesOrderDownloadPdf(mobileConfigurationDTO.getSalesOrderDownloadPdf());
		configuration.setFindLocation(mobileConfigurationDTO.getFindLocation());
		configuration.setEnableDynamicUnit(mobileConfigurationDTO.getEnableDynamicUnit());
		configuration.setEnableDiscountRoundOffColumn(mobileConfigurationDTO.getEnableDiscountRoundOffColumn());
		configuration.setStockLocationProducts(mobileConfigurationDTO.getStockLocationProducts());
		configuration.setSalesOrderAllocation(mobileConfigurationDTO.getSalesOrderAllocation());
		configuration.setRateWithoutCalculation(mobileConfigurationDTO.getRateWithoutCalculation());
		configuration.setShowBestPerformerUpload(mobileConfigurationDTO.isShowBestPerformerUpload());
		configuration.setBelowPriceLevel(mobileConfigurationDTO.isBelowPriceLevel());
		configuration.setAmountToThreeDecimal(mobileConfigurationDTO.getAmountToThreeDecimal());
		configuration.setEnableGeoFencing(mobileConfigurationDTO.getEnableGeoFencing());
		configuration.setReceiptAllocationMandatory(mobileConfigurationDTO.getReceiptAllocationMandatory());
		configuration.setBlockActivity(mobileConfigurationDTO.getBlockActivity());
		configuration.setSalesOrderMandatory(mobileConfigurationDTO.getSalesOrderMandatory());
		configuration.setRateWithTax(mobileConfigurationDTO.getRateWithTax());
		configuration.setWifiPrintEnabler(mobileConfigurationDTO.getWifiPrintEnabler());
		configuration.setBlockCustomerByCredits(mobileConfigurationDTO.isBlockCustomerByCredits());
		configuration.setMaxCartNos(mobileConfigurationDTO.isMaxCartNos());
		configuration.setCartMaxSize(mobileConfigurationDTO.getCartMaxSize());
		configuration.setRoundOffAutomation(mobileConfigurationDTO.isRoundOffAutomation());
		configuration = mobileConfigurationRepository.save(configuration);
		MobileConfigurationDTO result = new MobileConfigurationDTO(configuration);
		return result;
	}

	@Override
	public MobileConfigurationDTO updateMobileConfiguration(MobileConfigurationDTO mobileConfigurationDTO) {
		log.debug("Request to update MobileConfiguration : {}", mobileConfigurationDTO);
		return mobileConfigurationRepository.findOneByPid(mobileConfigurationDTO.getPid()).map(configuration -> {
			configuration.setAttendanceMarkingRequired(mobileConfigurationDTO.getAttendanceMarkingRequired());
			configuration.setBuildDueDetails(mobileConfigurationDTO.getBuildDueDetail());
			configuration.setDayPlanDownloadRequired(mobileConfigurationDTO.getDayPlanDownloadRequired());
			configuration.setIncludeAddressInAccountlist(mobileConfigurationDTO.getIncludeAddressInAccountList());
			configuration.setMasterDataUpdateRequired(mobileConfigurationDTO.getMasterDataUpdateRequired());
			configuration.setPromptAttendanceMarking(mobileConfigurationDTO.getPromptAttendanceMarkingvalue());
			configuration.setPromptDayPlanUpdate(mobileConfigurationDTO.getPromptDayPlanUpdate());
			configuration.setPromptMasterdataUpdate(mobileConfigurationDTO.getPromptMasterDataUpdate());
			configuration.setTaskExecutionOfflineSave(mobileConfigurationDTO.getTaskExecutionSaveOfflineValue());
			configuration.setShowAllActivityCount(mobileConfigurationDTO.getShowAllActivityCount());
			configuration.setCreateTerritory(mobileConfigurationDTO.getCreateTerritory());
			configuration.setRealTimeProductPriceEnabled(mobileConfigurationDTO.getRealTimeProductPriceEnabled());
			configuration.setHasGeoTag(mobileConfigurationDTO.getHasGeoTag());
			configuration.setHasPostDatedVoucherEnabled(mobileConfigurationDTO.getHasPostDatedVoucher());
			configuration.setPromptVehicleMaster(mobileConfigurationDTO.getPromptVehicleMaster());
			configuration.setAddNewCustomer(mobileConfigurationDTO.getAddNewCustomer());
			configuration.setPreventNegativeStock(mobileConfigurationDTO.getPreventNegativeStock());
			configuration.setVoucherNumberGenerationType(mobileConfigurationDTO.getVoucherNumberGenerationType());
			configuration.setInventoryVoucherUIType(mobileConfigurationDTO.getInventoryVoucherUIType());
			configuration.setCartType(mobileConfigurationDTO.getCartType());
			configuration.setAccountProfileDisplayName(mobileConfigurationDTO.getAccountProfileDisplayName());
			configuration.setAccountProfilePrintName(mobileConfigurationDTO.getAccountProfilePrintName());
			configuration.setProductProfileDisplayName(mobileConfigurationDTO.getProductProfileDisplayName());
			configuration.setProductProfilePrintName(mobileConfigurationDTO.getProductProfilePrintName());
			configuration.setKfcEnabled(mobileConfigurationDTO.getKfcEnabled());
			configuration.setGpsMandatory(mobileConfigurationDTO.getGpsMandatory());
			configuration.setEnableSecondarySales(mobileConfigurationDTO.getEnableSecondarySales());
			configuration.setEnableAttendanceImage(mobileConfigurationDTO.getEnableAttendanceImage());
			configuration.setSmartSearch(mobileConfigurationDTO.getSmartSearch());
			configuration.setSalesOrderDownloadPdf(mobileConfigurationDTO.getSalesOrderDownloadPdf());
			configuration.setFindLocation(mobileConfigurationDTO.getFindLocation());
			configuration.setEnableDynamicUnit(mobileConfigurationDTO.getEnableDynamicUnit());
			configuration.setEnableDiscountRoundOffColumn(mobileConfigurationDTO.getEnableDiscountRoundOffColumn());
			configuration.setStockLocationProducts(mobileConfigurationDTO.getStockLocationProducts());
			configuration.setSalesOrderAllocation(mobileConfigurationDTO.getSalesOrderAllocation());
			configuration.setRateWithoutCalculation(mobileConfigurationDTO.getRateWithoutCalculation());
			configuration.setShowBestPerformerUpload(mobileConfigurationDTO.isShowBestPerformerUpload());
			configuration.setBelowPriceLevel(mobileConfigurationDTO.isBelowPriceLevel());
			configuration.setAmountToThreeDecimal(mobileConfigurationDTO.getAmountToThreeDecimal());
			configuration.setEnableGeoFencing(mobileConfigurationDTO.getEnableGeoFencing());
			configuration.setReceiptAllocationMandatory(mobileConfigurationDTO.getReceiptAllocationMandatory());
			configuration.setBlockActivity(mobileConfigurationDTO.getBlockActivity());
			configuration.setSalesOrderMandatory(mobileConfigurationDTO.getSalesOrderMandatory());
			configuration.setRateWithTax(mobileConfigurationDTO.getRateWithTax());
			configuration.setWifiPrintEnabler(mobileConfigurationDTO.getWifiPrintEnabler());
			configuration.setBlockCustomerByCredits(mobileConfigurationDTO.isBlockCustomerByCredits());
			configuration.setMaxCartNos(mobileConfigurationDTO.isMaxCartNos());
			configuration.setCartMaxSize(mobileConfigurationDTO.getCartMaxSize());
			configuration.setRoundOffAutomation(mobileConfigurationDTO.isRoundOffAutomation());
			configuration = mobileConfigurationRepository.save(configuration);
			MobileConfigurationDTO result = new MobileConfigurationDTO(configuration);
			return result;
		}).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<MobileConfigurationDTO> findOneByPid(String pid) {
		log.debug("Request to get all MobileConfigurations");
		return mobileConfigurationRepository.findOneByPid(pid).map(config -> {
			MobileConfigurationDTO mobileConfigurationDTO = new MobileConfigurationDTO(config);
			return mobileConfigurationDTO;
		});
	}

	@Override
	@Transactional(readOnly = true)
	public MobileConfigurationDTO findByCompanyId(Long companyId) {
		log.debug("Request to get MobileConfigurations by company");
		MobileConfiguration configuration = mobileConfigurationRepository.findByCompanyId(companyId);
		MobileConfigurationDTO mobileConfigurationDTO = null;
		if (configuration != null) {
			mobileConfigurationDTO = new MobileConfigurationDTO(configuration);
		}
		return mobileConfigurationDTO;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MobileConfigurationDTO> findAll() {
		log.debug("Request to get all MobileConfigurations");
		return mobileConfigurationRepository.findAll().stream().map(MobileConfigurationDTO::new)
				.collect(Collectors.toList());
	}

	@Override
	public void deleteByPid(String pid) {
		log.debug("Request to delete MobileConfigurations");
		mobileConfigurationRepository.deleteByPid(pid);
	}

	@Override
	public Optional<MobileConfigurationDTO> findOneByCompanyId(Long companyId) {
		log.debug("Request to get all MobileConfigurations");
		return mobileConfigurationRepository.findOneByCompanyId(companyId).map(config -> {
			MobileConfigurationDTO mobileConfigurationDTO = new MobileConfigurationDTO(config);
			return mobileConfigurationDTO;
		});
	}

	@Override
	public Optional<MobileConfigurationDTO> findOneByCompanyId() {
		log.debug("Request to get all MobileConfigurations");
		return mobileConfigurationRepository.findOneByCompanyId().map(config -> {
			MobileConfigurationDTO mobileConfigurationDTO = new MobileConfigurationDTO(config);
			return mobileConfigurationDTO;
		});
	}

}
