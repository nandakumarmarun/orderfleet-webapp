package com.orderfleet.webapp.web.vendor.odoo.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.util.RestClientUtil;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.ParamsOdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.ParamsReceiptOdoo;
import com.orderfleet.webapp.web.vendor.odoo.dto.RequestBodyOdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.RequestBodyOdooReceipt;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdooInvoice;

@Service
public class SendReceiptOdooService {
	private final Logger log = LoggerFactory.getLogger(SendReceiptOdooService.class);	

	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private UserStockLocationRepository userStockLocationRepository;
	
	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;
	
	private static String SEND_RECEIPT_API_URL = "http://edappal.nellara.com:1214/web/api/create_payment";
	
	@Transactional
	public void sendReceipt() {
		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		String companyPid = company.getPid();
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		//List<ParamsReceiptOdoo> odooInvoices = new ArrayList<>();
		
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository.findByCompanyAndStatusOrderByCreatedDateDesc();
		log.info("Size of acc Voucher header size {}", accountingVoucherHeaders.size());
		
		if (accountingVoucherHeaders.size() > 0) {

			Set<Long> userIds = new HashSet<>();
			
			for (AccountingVoucherHeader obj : accountingVoucherHeaders) {
				userIds.add(obj.getCreatedBy().getId());
			}
			
			List<User> users = userRepository.findAllByCompanyIdAndIdsIn(userIds);

			List<UserStockLocation> userStockLocations = userStockLocationRepository.findAllByCompanyPid(companyPid);
			
			for (AccountingVoucherHeader obj : accountingVoucherHeaders) {
				Optional<User> opUser = users.stream().filter(u -> u.getId() == Long.parseLong(obj.getCreatedBy().getId().toString()))
						.findAny();
				
				Optional<UserStockLocation> opUserStockLocation = userStockLocations.stream()
						.filter(us -> us.getUser().getPid().equals(opUser.get().getPid())).findAny();
				ParamsReceiptOdoo odooInvoice = new ParamsReceiptOdoo();
				
				odooInvoice.setCreate(true);
				odooInvoice.setPayment_type("partner_payment");
				odooInvoice.setPartner_id(
						obj.getAccountProfile().getCustomerId() != null && !obj.getAccountProfile().getCustomerId().equals("")
								? Long.parseLong(obj.getAccountProfile().getCustomerId())
								: 0);
				odooInvoice.setPaid_amount(obj.getTotalAmount());
				odooInvoice.setTransaction_type(obj.getAccountingVoucherDetails() != null && obj.getAccountingVoucherDetails().size()>0 ?
						obj.getAccountingVoucherDetails().get(0).getMode().toString(): "");
				odooInvoice.setLocation_id(Long.parseLong(opUserStockLocation.get().getStockLocation().getAlias()));
				odooInvoice.setSalesman_id(0);
				odooInvoice.setReference(obj.getDocumentNumberServer());
				odooInvoice.setCheque_number(obj.getAccountingVoucherDetails() != null && obj.getAccountingVoucherDetails().size()>0 ?
						obj.getAccountingVoucherDetails().get(0).getInstrumentNumber().toString(): "");
				
				odooInvoice.setCheque_date(obj.getAccountingVoucherDetails() != null && obj.getAccountingVoucherDetails().size()>0 ?
						obj.getAccountingVoucherDetails().get(0).getInstrumentDate().format(formatter1): "");
				odooInvoice.setPdc_state(false);
				
				// odooInvoices.add(odooInvoice);
				sendToOdoo(odooInvoice);
			}
		}

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
	
		log.info("Sync completed in {} ms", elapsedTime);
	}
	
	private void sendToOdoo(ParamsReceiptOdoo odooParam) {
		
		log.info("Sending (" + odooParam.getReference() + ") Invoices to Odoo....");

		RequestBodyOdooReceipt request = new RequestBodyOdooReceipt();

		request.setJsonrpc("2.0");

		request.setParams(odooParam);

		HttpEntity<RequestBodyOdooReceipt> entity = new HttpEntity<>(request,
				RestClientUtil.createTokenAuthHeaders());

		log.info(entity.getBody().toString() + "");

		ObjectMapper Obj = new ObjectMapper();

		// get object as a json string
		String jsonStr;
		try {
			jsonStr = Obj.writeValueAsString(request);
			log.info(jsonStr);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Displaying JSON String

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get URL: " + SEND_RECEIPT_API_URL);

		try {

			ResponseBodyOdooInvoice responseBodyOdooInvoice = restTemplate.postForObject(SEND_RECEIPT_API_URL,
					entity, ResponseBodyOdooInvoice.class);
			log.info(responseBodyOdooInvoice + "");

			// get object as a json string
			String jsonStr1;
			try {
				jsonStr1 = Obj.writeValueAsString(responseBodyOdooInvoice);
				log.info(jsonStr1);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			log.info("Odoo Invoice Created Success Size= " + responseBodyOdooInvoice.getResult().getMessage().size()
					+ "------------");

			// changeServerDownloadStatus(responseBodyOdooInvoice.getResult().getMessage());

		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
				// throw new ServiceException(exception.getResponseBodyAsString());
				log.info(exception.getResponseBodyAsString());
				log.info("-------------------------");
				log.info(exception.getMessage());
				log.info("-------------------------");
				exception.printStackTrace();
			}
			log.info(exception.getMessage());
			// throw new ServiceException(exception.getMessage());
		} catch (Exception exception) {

			log.info(exception.getMessage());
			log.info("-------------------------");
			exception.printStackTrace();
			log.info("-------------------------");

			// throw new ServiceException(exception.getMessage());
		}
	}
}
