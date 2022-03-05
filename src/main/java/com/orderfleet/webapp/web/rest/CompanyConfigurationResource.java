package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.orderfleet.webapp.security.SecurityUtils;
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
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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
				if (cc.getName().equals(CompanyConfig.NEW_CUSTOMER_ALIAS)) {
					mcDto.setNewCustomerAlias(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.CHAT_REPLY)) {
					mcDto.setChatReply(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.SALES_PDF_DOWNLOAD)) {
					mcDto.setSalesPdfDownload(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.VISIT_BASED_TRANSACTION)) {
					mcDto.setVisitBasedTransaction(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.SALES_MANAGEMENT)) {
					mcDto.setSalesManagement(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.RECEIPT_MANAGEMENT)) {
					mcDto.setReceiptsManagement(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.SALES_EDIT_ENABLED)) {
					mcDto.setSalesEditEnabled(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.GPS_VARIANCE_QUERY)) {
					mcDto.setGpsVarianceQuery(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.SEND_SALES_ORDER_EMAIL)) {
					mcDto.setSendSalesOrderEmail(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.SEND_SALES_ORDER_SAP)) {
					mcDto.setSendSalesOrderSap(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.PIECES_TO_QUANTITY)) {
					mcDto.setPiecesToQuantity(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.SEND_SALES_ORDER_ODOO)) {
					mcDto.setSendSalesOrderOdoo(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.SEND_TRANSACTIONS_SAP_PRAVESH)) {
					mcDto.setSendTransactionsSapPravesh(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.ADD_COMPOUND_UNIT)) {
					mcDto.setAddCompoundUnit(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.UPDATE_STOCK_LOCATION)) {
					mcDto.setUpdateStockLocation(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.SEND_TO_ODOO)) {
					mcDto.setSendToOdoo(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.PRODUCT_GROUP_TAX)) {
					mcDto.setEnableProductGroupTax(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.ALIAS_TO_NAME)) {
					mcDto.setAliasToName(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				
				if (cc.getName().equals(CompanyConfig.DESCRIPTION_TO_NAME)) {
					mcDto.setDescriptionToName(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}
				if (cc.getName().equals(CompanyConfig.STOCK_API)) {
					mcDto.setStockApi(Boolean.valueOf(cc.getValue()));
					anyValueExist = true;
				}

				/*
				 * if (cc.getName().equals(CompanyConfig.FIND_LOCATION)) {
				 * mcDto.setSendSalesOrderEmail(Boolean.valueOf(cc.getValue())); anyValueExist =
				 * true; }
				 */

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
			@RequestParam String stageChangeAccountingVoucher, @RequestParam String newCustomerAlias,
			@RequestParam String chatReply, @RequestParam String salesPdfDownload,
			@RequestParam String visitBasedTransaction, @RequestParam String salesManagement,
			@RequestParam String receiptsManagement, @RequestParam String salesEditEnabled,
			@RequestParam String gpsVarianceQuery, @RequestParam String sendSalesOrderEmail,
			@RequestParam String sendSalesOrderSap, @RequestParam String piecesToQuantity,
			@RequestParam String sendSalesOrderOdoo, @RequestParam String sendTransactionsSapPravesh,
			@RequestParam String addCompoundUnit, @RequestParam String updateStockLocation,
			@RequestParam String sendToOdoo, @RequestParam String productGroupTax, @RequestParam String aliasToName, @RequestParam String descriptionToName,
			@RequestParam String stockApi)
			throws URISyntaxException {
		log.debug("Web request to save Company Configuration ");
		/* ,@RequestParam String findLocation */
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
		Optional<CompanyConfiguration> optNewCustomerAlias = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.NEW_CUSTOMER_ALIAS);
		Optional<CompanyConfiguration> optChatReply = companyConfigurationRepository.findByCompanyPidAndName(companyPid,
				CompanyConfig.CHAT_REPLY);
		Optional<CompanyConfiguration> optSalesPdfDownload = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_PDF_DOWNLOAD);
		Optional<CompanyConfiguration> optVisitBasedTransaction = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.VISIT_BASED_TRANSACTION);
		Optional<CompanyConfiguration> optSalesManagement = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_MANAGEMENT);
		Optional<CompanyConfiguration> optReceiptsManagement = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.RECEIPT_MANAGEMENT);
		Optional<CompanyConfiguration> optSalesEditEnabled = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_EDIT_ENABLED);
		Optional<CompanyConfiguration> optGpsVarianceQuery = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.GPS_VARIANCE_QUERY);
		Optional<CompanyConfiguration> optSendSalesOrderEmail = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SEND_SALES_ORDER_EMAIL);
		Optional<CompanyConfiguration> optSendSalesOrderSap = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SEND_SALES_ORDER_SAP);
		Optional<CompanyConfiguration> optPiecesToQuantity = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.PIECES_TO_QUANTITY);
		Optional<CompanyConfiguration> optSendSalesOrderOdoo = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SEND_SALES_ORDER_ODOO);
		Optional<CompanyConfiguration> optSendTransactionsSapPravesh = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SEND_TRANSACTIONS_SAP_PRAVESH);
		Optional<CompanyConfiguration> optAddCompoundUnitConfiguration = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.ADD_COMPOUND_UNIT);
		Optional<CompanyConfiguration> optUpdateStockLocation = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.UPDATE_STOCK_LOCATION);
		Optional<CompanyConfiguration> optSendToOdoo = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SEND_TO_ODOO);
		Optional<CompanyConfiguration> optProductGroupTax = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.PRODUCT_GROUP_TAX);
		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.ALIAS_TO_NAME);
		Optional<CompanyConfiguration> optdescriptionToName = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.DESCRIPTION_TO_NAME);
		Optional<CompanyConfiguration> optStockApi = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.STOCK_API);
		/*
		 * Optional<CompanyConfiguration> optFindLocation =
		 * companyConfigurationRepository .findByCompanyPidAndName(companyPid,
		 * CompanyConfig.FIND_LOCATION);
		 */

		CompanyConfiguration saveOfflineConfiguration = null;
		CompanyConfiguration promptAttendance = null;
		CompanyConfiguration interimSaveComapny = null;
		CompanyConfiguration refreshProductGroupProductCompany = null;
		CompanyConfiguration stageChangeAccountingVoucherCompany = null;
		CompanyConfiguration newCustomerAliasCompany = null;
		CompanyConfiguration chatReplyCompany = null;
		CompanyConfiguration salesPdfDownloadCompany = null;
		CompanyConfiguration visitBasedTransactionCompany = null;
		CompanyConfiguration salesManagementCompany = null;
		CompanyConfiguration receiptsManagementCompany = null;
		CompanyConfiguration salesEditEnabledCompany = null;
		CompanyConfiguration gpsVarianceQueryCompany = null;
		CompanyConfiguration sendSalesOrderEmailCompany = null;
		CompanyConfiguration sendSalesOrderSapCompany = null;
		CompanyConfiguration piecesToQuantityCompany = null;
		CompanyConfiguration sendSalesOrderOdooCompany = null;
		CompanyConfiguration sendTransactionSapPraveshCompany = null;
		CompanyConfiguration addCompoundUnitCompany = null;
		CompanyConfiguration updateStockLocationCompany = null;
		CompanyConfiguration sendToOdooCompany = null;
		CompanyConfiguration productGroupTaxCompany = null;
		CompanyConfiguration aliasToNameCompany = null;
		CompanyConfiguration descriptionToNameCompany = null;
		CompanyConfiguration stockApiCompany = null;
		/* CompanyConfiguration findLocationCompany = null; */

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

		if (optNewCustomerAlias.isPresent()) {
			newCustomerAliasCompany = optNewCustomerAlias.get();
			newCustomerAliasCompany.setValue(newCustomerAlias);
		} else {
			newCustomerAliasCompany = new CompanyConfiguration();
			newCustomerAliasCompany.setCompany(company);
			newCustomerAliasCompany.setName(CompanyConfig.NEW_CUSTOMER_ALIAS);
			newCustomerAliasCompany.setValue(newCustomerAlias);
		}
		companyConfigurationRepository.save(newCustomerAliasCompany);

		if (optChatReply.isPresent()) {
			chatReplyCompany = optChatReply.get();
			chatReplyCompany.setValue(chatReply);
		} else {
			chatReplyCompany = new CompanyConfiguration();
			chatReplyCompany.setCompany(company);
			chatReplyCompany.setName(CompanyConfig.CHAT_REPLY);
			chatReplyCompany.setValue(chatReply);
		}
		companyConfigurationRepository.save(chatReplyCompany);

		if (optSalesPdfDownload.isPresent()) {
			salesPdfDownloadCompany = optSalesPdfDownload.get();
			salesPdfDownloadCompany.setValue(salesPdfDownload);
		} else {
			salesPdfDownloadCompany = new CompanyConfiguration();
			salesPdfDownloadCompany.setCompany(company);
			salesPdfDownloadCompany.setName(CompanyConfig.SALES_PDF_DOWNLOAD);
			salesPdfDownloadCompany.setValue(salesPdfDownload);
		}
		companyConfigurationRepository.save(salesPdfDownloadCompany);

		if (optVisitBasedTransaction.isPresent()) {
			visitBasedTransactionCompany = optVisitBasedTransaction.get();
			visitBasedTransactionCompany.setValue(visitBasedTransaction);
		} else {
			visitBasedTransactionCompany = new CompanyConfiguration();
			visitBasedTransactionCompany.setCompany(company);
			visitBasedTransactionCompany.setName(CompanyConfig.VISIT_BASED_TRANSACTION);
			visitBasedTransactionCompany.setValue(visitBasedTransaction);
		}
		companyConfigurationRepository.save(visitBasedTransactionCompany);

		if (optSalesManagement.isPresent()) {
			salesManagementCompany = optSalesManagement.get();
			salesManagementCompany.setValue(salesManagement);
		} else {
			salesManagementCompany = new CompanyConfiguration();
			salesManagementCompany.setCompany(company);
			salesManagementCompany.setName(CompanyConfig.SALES_MANAGEMENT);
			salesManagementCompany.setValue(salesManagement);
		}
		companyConfigurationRepository.save(salesManagementCompany);

		if (optReceiptsManagement.isPresent()) {
			receiptsManagementCompany = optReceiptsManagement.get();
			receiptsManagementCompany.setValue(receiptsManagement);
		} else {
			receiptsManagementCompany = new CompanyConfiguration();
			receiptsManagementCompany.setCompany(company);
			receiptsManagementCompany.setName(CompanyConfig.RECEIPT_MANAGEMENT);
			receiptsManagementCompany.setValue(receiptsManagement);
		}
		companyConfigurationRepository.save(receiptsManagementCompany);

		if (optSalesEditEnabled.isPresent()) {
			salesEditEnabledCompany = optSalesEditEnabled.get();
			salesEditEnabledCompany.setValue(salesEditEnabled);
		} else {
			salesEditEnabledCompany = new CompanyConfiguration();
			salesEditEnabledCompany.setCompany(company);
			salesEditEnabledCompany.setName(CompanyConfig.SALES_EDIT_ENABLED);
			salesEditEnabledCompany.setValue(salesEditEnabled);
		}
		companyConfigurationRepository.save(salesEditEnabledCompany);

		if (optGpsVarianceQuery.isPresent()) {
			gpsVarianceQueryCompany = optGpsVarianceQuery.get();
			gpsVarianceQueryCompany.setValue(gpsVarianceQuery);
		} else {
			gpsVarianceQueryCompany = new CompanyConfiguration();
			gpsVarianceQueryCompany.setCompany(company);
			gpsVarianceQueryCompany.setName(CompanyConfig.GPS_VARIANCE_QUERY);
			gpsVarianceQueryCompany.setValue(gpsVarianceQuery);
		}
		companyConfigurationRepository.save(gpsVarianceQueryCompany);

		if (optSendSalesOrderEmail.isPresent()) {
			sendSalesOrderEmailCompany = optSendSalesOrderEmail.get();
			sendSalesOrderEmailCompany.setValue(sendSalesOrderEmail);
		} else {
			sendSalesOrderEmailCompany = new CompanyConfiguration();
			sendSalesOrderEmailCompany.setCompany(company);
			sendSalesOrderEmailCompany.setName(CompanyConfig.SEND_SALES_ORDER_EMAIL);
			sendSalesOrderEmailCompany.setValue(sendSalesOrderEmail);
		}
		companyConfigurationRepository.save(sendSalesOrderEmailCompany);

		if (optSendSalesOrderSap.isPresent()) {
			sendSalesOrderSapCompany = optSendSalesOrderSap.get();
			sendSalesOrderSapCompany.setValue(sendSalesOrderSap);
		} else {
			sendSalesOrderSapCompany = new CompanyConfiguration();
			sendSalesOrderSapCompany.setCompany(company);
			sendSalesOrderSapCompany.setName(CompanyConfig.SEND_SALES_ORDER_SAP);
			sendSalesOrderSapCompany.setValue(sendSalesOrderSap);
		}
		companyConfigurationRepository.save(sendSalesOrderSapCompany);

		if (optPiecesToQuantity.isPresent()) {
			piecesToQuantityCompany = optPiecesToQuantity.get();
			piecesToQuantityCompany.setValue(piecesToQuantity);
		} else {
			piecesToQuantityCompany = new CompanyConfiguration();
			piecesToQuantityCompany.setCompany(company);
			piecesToQuantityCompany.setName(CompanyConfig.PIECES_TO_QUANTITY);
			piecesToQuantityCompany.setValue(piecesToQuantity);
		}
		companyConfigurationRepository.save(piecesToQuantityCompany);

		if (optSendSalesOrderOdoo.isPresent()) {
			sendSalesOrderOdooCompany = optSendSalesOrderOdoo.get();
			sendSalesOrderOdooCompany.setValue(sendSalesOrderOdoo);
		} else {
			sendSalesOrderOdooCompany = new CompanyConfiguration();
			sendSalesOrderOdooCompany.setCompany(company);
			sendSalesOrderOdooCompany.setName(CompanyConfig.SEND_SALES_ORDER_ODOO);
			sendSalesOrderOdooCompany.setValue(sendSalesOrderOdoo);
		}
		companyConfigurationRepository.save(sendSalesOrderOdooCompany);

		if (optSendTransactionsSapPravesh.isPresent()) {
			sendTransactionSapPraveshCompany = optSendTransactionsSapPravesh.get();
			sendTransactionSapPraveshCompany.setValue(sendTransactionsSapPravesh);
		} else {
			sendTransactionSapPraveshCompany = new CompanyConfiguration();
			sendTransactionSapPraveshCompany.setCompany(company);
			sendTransactionSapPraveshCompany.setName(CompanyConfig.SEND_TRANSACTIONS_SAP_PRAVESH);
			sendTransactionSapPraveshCompany.setValue(sendTransactionsSapPravesh);
		}
		companyConfigurationRepository.save(sendTransactionSapPraveshCompany);

		if (optAddCompoundUnitConfiguration.isPresent()) {
			addCompoundUnitCompany = optAddCompoundUnitConfiguration.get();
			addCompoundUnitCompany.setValue(addCompoundUnit);
		} else {
			addCompoundUnitCompany = new CompanyConfiguration();
			addCompoundUnitCompany.setCompany(company);
			addCompoundUnitCompany.setName(CompanyConfig.ADD_COMPOUND_UNIT);
			addCompoundUnitCompany.setValue(addCompoundUnit);
		}
		companyConfigurationRepository.save(addCompoundUnitCompany);

		if (optUpdateStockLocation.isPresent()) {
			updateStockLocationCompany = optUpdateStockLocation.get();
			updateStockLocationCompany.setValue(updateStockLocation);
		} else {
			updateStockLocationCompany = new CompanyConfiguration();
			updateStockLocationCompany.setCompany(company);
			updateStockLocationCompany.setName(CompanyConfig.UPDATE_STOCK_LOCATION);
			updateStockLocationCompany.setValue(updateStockLocation);
		}
		companyConfigurationRepository.save(updateStockLocationCompany);

		if (optSendToOdoo.isPresent()) {
			sendToOdooCompany = optSendToOdoo.get();
			sendToOdooCompany.setValue(sendToOdoo);
		} else {
			sendToOdooCompany = new CompanyConfiguration();
			sendToOdooCompany.setCompany(company);
			sendToOdooCompany.setName(CompanyConfig.SEND_TO_ODOO);
			sendToOdooCompany.setValue(sendToOdoo);
		}
		companyConfigurationRepository.save(sendToOdooCompany);

		if (optProductGroupTax.isPresent()) {
			productGroupTaxCompany = optProductGroupTax.get();
			productGroupTaxCompany.setValue(productGroupTax);
		} else {
			productGroupTaxCompany = new CompanyConfiguration();
			productGroupTaxCompany.setCompany(company);
			productGroupTaxCompany.setName(CompanyConfig.PRODUCT_GROUP_TAX);
			productGroupTaxCompany.setValue(productGroupTax);
		}
		companyConfigurationRepository.save(productGroupTaxCompany);

		if (optAliasToName.isPresent()) {
			aliasToNameCompany = optAliasToName.get();
			aliasToNameCompany.setValue(aliasToName);
		} else {
			aliasToNameCompany = new CompanyConfiguration();
			aliasToNameCompany.setCompany(company);
			aliasToNameCompany.setName(CompanyConfig.ALIAS_TO_NAME);
			aliasToNameCompany.setValue(aliasToName);
		}
		companyConfigurationRepository.save(aliasToNameCompany);
		
		if (optdescriptionToName.isPresent()) {
			descriptionToNameCompany= optdescriptionToName.get();
			descriptionToNameCompany.setValue(descriptionToName);
		} else {
			descriptionToNameCompany = new CompanyConfiguration();
			descriptionToNameCompany.setCompany(company);
			descriptionToNameCompany.setName(CompanyConfig.DESCRIPTION_TO_NAME);
			descriptionToNameCompany.setValue(descriptionToName);
		}
		companyConfigurationRepository.save(descriptionToNameCompany);
		
		if (optStockApi.isPresent()) {
			stockApiCompany= optStockApi.get();
			stockApiCompany.setValue(stockApi);
		} else {
			stockApiCompany = new CompanyConfiguration();
			stockApiCompany.setCompany(company);
			stockApiCompany.setName(CompanyConfig.STOCK_API);
			stockApiCompany.setValue(stockApi);
		}
		companyConfigurationRepository.save(stockApiCompany);

		/*
		 * if (optFindLocation.isPresent()) { findLocationCompany =
		 * optFindLocation.get(); findLocationCompany.setValue(findLocation); } else {
		 * findLocationCompany = new CompanyConfiguration();
		 * findLocationCompany.setCompany(company);
		 * findLocationCompany.setName(CompanyConfig.FIND_LOCATION);
		 * findLocationCompany.setValue(findLocation); }
		 * companyConfigurationRepository.save(findLocationCompany);
		 */

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/company-configuration/{companyPid}", method = RequestMethod.GET)
	public @ResponseBody CompanyConfigDTO getCompanyConfiguration(@PathVariable String companyPid)
			throws URISyntaxException {
		log.debug("Web request to get Company Configuration");
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "COMP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by compPid and name";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<CompanyConfiguration> optDistanceTraveled = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.DISTANCE_TRAVELED);
		  String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

		Optional<CompanyConfiguration> optLocationVariance = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.LOCATION_VARIANCE);

		Optional<CompanyConfiguration> optinterimSave = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.INTERIM_SAVE);

		Optional<CompanyConfiguration> optRefreshProductGroupProduct = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.REFRESH_PRODUCT_GROUP_PRODUCT);

		Optional<CompanyConfiguration> optStageChangeAccountingVoucher = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.STAGE_CHANGES_FOR_ACCOUNTING_VOUCHER);

		Optional<CompanyConfiguration> optNewCustomerAlias = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.NEW_CUSTOMER_ALIAS);

		Optional<CompanyConfiguration> optChatReply = companyConfigurationRepository.findByCompanyPidAndName(companyPid,
				CompanyConfig.CHAT_REPLY);
		Optional<CompanyConfiguration> optSalesPdfDownload = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_PDF_DOWNLOAD);
		Optional<CompanyConfiguration> optVisitBasedTransaction = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.VISIT_BASED_TRANSACTION);
		Optional<CompanyConfiguration> optSalesManagement = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_MANAGEMENT);
		Optional<CompanyConfiguration> optReceiptsManagement = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.RECEIPT_MANAGEMENT);
		Optional<CompanyConfiguration> optSalesEditEnabled = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_EDIT_ENABLED);

		Optional<CompanyConfiguration> optGpsVarianceQuery = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.GPS_VARIANCE_QUERY);

		Optional<CompanyConfiguration> optSendSalesOrderEmail = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SEND_SALES_ORDER_EMAIL);

		Optional<CompanyConfiguration> optSendSalesOrderSap = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SEND_SALES_ORDER_SAP);

		Optional<CompanyConfiguration> optPiecesToQuantity = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.PIECES_TO_QUANTITY);

		Optional<CompanyConfiguration> optSendSalesOrderOdoo = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SEND_SALES_ORDER_ODOO);

		Optional<CompanyConfiguration> optSendTransactionsSapPravesh = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SEND_TRANSACTIONS_SAP_PRAVESH);

		Optional<CompanyConfiguration> optAddCompoundUnit = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.ADD_COMPOUND_UNIT);

		Optional<CompanyConfiguration> optUpdateStockLocation = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.UPDATE_STOCK_LOCATION);

		Optional<CompanyConfiguration> optSendToOdoo = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SEND_TO_ODOO);

		Optional<CompanyConfiguration> optProductGroupTax = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.PRODUCT_GROUP_TAX);

		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.ALIAS_TO_NAME);
		
		Optional<CompanyConfiguration> optdescriptionToName = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.DESCRIPTION_TO_NAME);

		Optional<CompanyConfiguration> optstoApi = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.STOCK_API);

		/*
		 * Optional<CompanyConfiguration> optFindLocation =
		 * companyConfigurationRepository .findByCompanyPidAndName(companyPid,
		 * CompanyConfig.FIND_LOCATION);
		 */
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
		if (optNewCustomerAlias.isPresent()) {
			companyConfigurationDTO.setNewCustomerAlias(Boolean.valueOf(optNewCustomerAlias.get().getValue()));
		}
		if (optChatReply.isPresent()) {
			companyConfigurationDTO.setChatReply(Boolean.valueOf(optChatReply.get().getValue()));
		}
		if (optSalesPdfDownload.isPresent()) {
			companyConfigurationDTO.setSalesPdfDownload(Boolean.valueOf(optSalesPdfDownload.get().getValue()));
		}
		if (optVisitBasedTransaction.isPresent()) {
			companyConfigurationDTO
					.setVisitBasedTransaction(Boolean.valueOf(optVisitBasedTransaction.get().getValue()));
		}
		if (optSalesManagement.isPresent()) {
			companyConfigurationDTO.setSalesManagement(Boolean.valueOf(optSalesManagement.get().getValue()));
		}
		if (optReceiptsManagement.isPresent()) {
			companyConfigurationDTO.setReceiptsManagement(Boolean.valueOf(optReceiptsManagement.get().getValue()));
		}
		if (optSalesEditEnabled.isPresent()) {
			companyConfigurationDTO.setSalesEditEnabled(Boolean.valueOf(optSalesEditEnabled.get().getValue()));
		}
		if (optGpsVarianceQuery.isPresent()) {
			companyConfigurationDTO.setGpsVarianceQuery(Boolean.valueOf(optGpsVarianceQuery.get().getValue()));
		}
		if (optSendSalesOrderEmail.isPresent()) {
			companyConfigurationDTO.setSendSalesOrderEmail(Boolean.valueOf(optSendSalesOrderEmail.get().getValue()));
		}
		if (optSendSalesOrderSap.isPresent()) {
			companyConfigurationDTO.setSendSalesOrderSap(Boolean.valueOf(optSendSalesOrderSap.get().getValue()));
		}
		if (optPiecesToQuantity.isPresent()) {
			companyConfigurationDTO.setPiecesToQuantity(Boolean.valueOf(optPiecesToQuantity.get().getValue()));
		}
		if (optSendSalesOrderOdoo.isPresent()) {
			companyConfigurationDTO.setSendSalesOrderOdoo(Boolean.valueOf(optSendSalesOrderOdoo.get().getValue()));
		}
		if (optSendTransactionsSapPravesh.isPresent()) {
			companyConfigurationDTO
					.setSendTransactionsSapPravesh(Boolean.valueOf(optSendTransactionsSapPravesh.get().getValue()));
		}
		if (optAddCompoundUnit.isPresent()) {
			companyConfigurationDTO.setAddCompoundUnit(Boolean.valueOf(optAddCompoundUnit.get().getValue()));
		}
		if (optUpdateStockLocation.isPresent()) {
			companyConfigurationDTO.setUpdateStockLocation(Boolean.valueOf(optUpdateStockLocation.get().getValue()));
		}
		if (optSendToOdoo.isPresent()) {
			companyConfigurationDTO.setSendToOdoo(Boolean.valueOf(optSendToOdoo.get().getValue()));
		}
		if (optProductGroupTax.isPresent()) {
			companyConfigurationDTO.setEnableProductGroupTax(Boolean.valueOf(optProductGroupTax.get().getValue()));
		}
		if (optAliasToName.isPresent()) {
			companyConfigurationDTO.setAliasToName(Boolean.valueOf(optAliasToName.get().getValue()));
		}
		if (optdescriptionToName.isPresent()) {
			companyConfigurationDTO.setDescriptionToName(Boolean.valueOf(optdescriptionToName.get().getValue()));
		}
		
		if (optstoApi.isPresent()) {
			companyConfigurationDTO.setStockApi(Boolean.valueOf(optstoApi.get().getValue()));
		}

		/*
		 * if (optFindLocation.isPresent()) {
		 * companyConfigurationDTO.setFindLocation(Boolean.valueOf(optFindLocation.get()
		 * .getValue())); }
		 */
		return companyConfigurationDTO;
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/company-configuration/delete/{companyPid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteCompanyConfiguration(@PathVariable String companyPid) throws URISyntaxException {
		log.debug("Web request to delete Company Configuration compantPid : {}", companyPid);
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "COMP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by compPid and name";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<CompanyConfiguration> optDistanceTraveled = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.DISTANCE_TRAVELED);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		Optional<CompanyConfiguration> optLocationVariance = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.LOCATION_VARIANCE);
		Optional<CompanyConfiguration> optinterimSave = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.INTERIM_SAVE);
		Optional<CompanyConfiguration> optRefreshProductGroupProduct = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.REFRESH_PRODUCT_GROUP_PRODUCT);
		Optional<CompanyConfiguration> optStageChangeAccountingVoucher = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.STAGE_CHANGES_FOR_ACCOUNTING_VOUCHER);
		Optional<CompanyConfiguration> optNewCustomerAlias = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.NEW_CUSTOMER_ALIAS);
		Optional<CompanyConfiguration> optChatReply = companyConfigurationRepository.findByCompanyPidAndName(companyPid,
				CompanyConfig.CHAT_REPLY);
		Optional<CompanyConfiguration> optSalesPdfDownload = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_PDF_DOWNLOAD);
		Optional<CompanyConfiguration> optVisitBasedTransaction = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.VISIT_BASED_TRANSACTION);
		Optional<CompanyConfiguration> optSalesManagement = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_MANAGEMENT);
		Optional<CompanyConfiguration> optReceiptsManagement = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.RECEIPT_MANAGEMENT);
		Optional<CompanyConfiguration> optSalesEditEnabled = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_EDIT_ENABLED);

		Optional<CompanyConfiguration> optGpsVarianceQuery = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.GPS_VARIANCE_QUERY);

		Optional<CompanyConfiguration> optSendSalesOrderEmail = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SEND_SALES_ORDER_EMAIL);

		Optional<CompanyConfiguration> optSendSalesOrderSap = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SEND_SALES_ORDER_SAP);

		Optional<CompanyConfiguration> optPiecesToQuantity = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.PIECES_TO_QUANTITY);

		Optional<CompanyConfiguration> optSendSalesOrderOdoo = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SEND_SALES_ORDER_ODOO);

		Optional<CompanyConfiguration> optSendTransactionsSapPravesh = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SEND_TRANSACTIONS_SAP_PRAVESH);

		Optional<CompanyConfiguration> optAddCompoundUnit = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.ADD_COMPOUND_UNIT);

		Optional<CompanyConfiguration> optUpdateStockLocation = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.UPDATE_STOCK_LOCATION);

		Optional<CompanyConfiguration> optSendToOdoo = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SEND_TO_ODOO);

		Optional<CompanyConfiguration> optProductGroupTax = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.PRODUCT_GROUP_TAX);

		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.ALIAS_TO_NAME);
		Optional<CompanyConfiguration> optdescriptionToName = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.DESCRIPTION_TO_NAME);
		Optional<CompanyConfiguration> optstockApi = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.STOCK_API);
		/*
		 * Optional<CompanyConfiguration> optFindLocation =
		 * companyConfigurationRepository .findByCompanyPidAndName(companyPid,
		 * CompanyConfig.FIND_LOCATION);
		 */

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
		if (optNewCustomerAlias.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optNewCustomerAlias.get().getCompany().getId(),
					CompanyConfig.NEW_CUSTOMER_ALIAS);
		}
		if (optChatReply.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optChatReply.get().getCompany().getId(),
					CompanyConfig.CHAT_REPLY);
		}
		if (optSalesPdfDownload.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optSalesPdfDownload.get().getCompany().getId(),
					CompanyConfig.SALES_PDF_DOWNLOAD);
		}
		if (optVisitBasedTransaction.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optVisitBasedTransaction.get().getCompany().getId(),
					CompanyConfig.VISIT_BASED_TRANSACTION);
		}
		if (optSalesManagement.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optSalesManagement.get().getCompany().getId(),
					CompanyConfig.SALES_MANAGEMENT);
		}
		if (optReceiptsManagement.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optSalesManagement.get().getCompany().getId(),
					CompanyConfig.RECEIPT_MANAGEMENT);
		}
		if (optSalesEditEnabled.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optSalesEditEnabled.get().getCompany().getId(),
					CompanyConfig.SALES_EDIT_ENABLED);
		}
		if (optGpsVarianceQuery.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optGpsVarianceQuery.get().getCompany().getId(),
					CompanyConfig.GPS_VARIANCE_QUERY);
		}
		if (optSendSalesOrderEmail.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optSendSalesOrderEmail.get().getCompany().getId(),
					CompanyConfig.SEND_SALES_ORDER_EMAIL);
		}
		if (optSendSalesOrderSap.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optSendSalesOrderSap.get().getCompany().getId(),
					CompanyConfig.SEND_SALES_ORDER_SAP);
		}
		if (optPiecesToQuantity.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optPiecesToQuantity.get().getCompany().getId(),
					CompanyConfig.PIECES_TO_QUANTITY);
		}
		if (optSendSalesOrderOdoo.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optSendSalesOrderOdoo.get().getCompany().getId(),
					CompanyConfig.SEND_SALES_ORDER_ODOO);
		}

		if (optSendTransactionsSapPravesh.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(
					optSendTransactionsSapPravesh.get().getCompany().getId(),
					CompanyConfig.SEND_TRANSACTIONS_SAP_PRAVESH);
		}

		if (optAddCompoundUnit.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optAddCompoundUnit.get().getCompany().getId(),
					CompanyConfig.ADD_COMPOUND_UNIT);
		}

		if (optUpdateStockLocation.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optUpdateStockLocation.get().getCompany().getId(),
					CompanyConfig.UPDATE_STOCK_LOCATION);
		}

		if (optSendToOdoo.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optSendToOdoo.get().getCompany().getId(),
					CompanyConfig.SEND_TO_ODOO);
		}

		if (optProductGroupTax.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optProductGroupTax.get().getCompany().getId(),
					CompanyConfig.PRODUCT_GROUP_TAX);
		}

		if (optAliasToName.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optAliasToName.get().getCompany().getId(),
					CompanyConfig.ALIAS_TO_NAME);
		}
		if (optdescriptionToName.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optdescriptionToName.get().getCompany().getId(),
					CompanyConfig.DESCRIPTION_TO_NAME);
		}
		
		if (optstockApi.isPresent()) {
			companyConfigurationRepository.deleteByCompanyIdAndName(optstockApi.get().getCompany().getId(),
					CompanyConfig.STOCK_API);
		}
		/*
		 * if (optFindLocation.isPresent()) {
		 * companyConfigurationRepository.deleteByCompanyIdAndName(optFindLocation.get()
		 * .getCompany().getId(), CompanyConfig.FIND_LOCATION); }
		 */

		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("companyConfiguration", companyPid.toString())).build();
	}
}
