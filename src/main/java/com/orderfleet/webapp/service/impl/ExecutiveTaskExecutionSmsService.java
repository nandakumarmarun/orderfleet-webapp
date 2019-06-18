package com.orderfleet.webapp.service.impl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.cache.LoadingCache;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.InventoryVoucherBatchDetail;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserDocument;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserDocumentService;
import com.orderfleet.webapp.service.async.TaskSubmissionPostSave;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.UserDocumentDTO;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

@Service
public class ExecutiveTaskExecutionSmsService {

	private final Logger log = LoggerFactory.getLogger(TaskSubmissionPostSave.class);

	@Inject
	private UserDocumentService userDocumentService;

	public ExecutiveTaskExecutionSmsService() {
		super();
	}

	public void sendSms(ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper) {
		ExecutiveTaskExecution executiveTaskExecution = tsTransactionWrapper.getExecutiveTaskExecution();
		List<InventoryVoucherHeader> inventoryVouchers = tsTransactionWrapper.getInventoryVouchers();
		List<AccountingVoucherHeader> accountingVouchers = tsTransactionWrapper.getAccountingVouchers();

		Document inventoryVocherDocument = new Document();
		Document accountingVocherDocument = new Document();
		String mobileNumber = executiveTaskExecution.getAccountProfile().getPhone1();
		String mobile = executiveTaskExecution.getAccountProfile().getPhone1();

		// String mobileNumber = "9387007657";

		if (mobile == null) {
			return;
		} else {
			if (mobile.isEmpty()) {
				return;
			}
			int mobilelength = mobile.length();
			log.info("Mobile Number Length =" + mobilelength);
			if (mobilelength > 10) {
				mobileNumber = mobile.substring(mobilelength - 10);
			}
			if (mobilelength < 10) {

				log.info("Failed !!!! Mobile Number Length < 10 -> " + mobilelength);
				return;
			}
			log.info("Mobile Number ->  " + mobileNumber);
		}

		if (inventoryVouchers != null && inventoryVouchers.size() > 0) {
			inventoryVocherDocument = inventoryVouchers.get(0).getDocument();
		}
		if (accountingVouchers != null && accountingVouchers.size() > 0) {
			accountingVocherDocument = accountingVouchers.get(0).getDocument();
		}
		User user = executiveTaskExecution.getUser();

		Company company = user.getCompany();

		List<UserDocumentDTO> userdocuments = userDocumentService.findByUserPid(user.getPid());

		if (inventoryVocherDocument != null) {
			for (UserDocumentDTO userDocumentDTO : userdocuments) {

				if (userDocumentDTO.getDocumentName().equalsIgnoreCase(inventoryVocherDocument.getName())) {

					if (userDocumentDTO.getSmsOption()) {
						if (sendInventoryVoucherMessage(inventoryVouchers, mobileNumber, company)) {
							log.info("Inventory Voucher SMS Sent to "
									+ executiveTaskExecution.getAccountProfile().getName() + " Successfully");
						} else {
							log.info("Inventory Voucher SMS Sent to "
									+ executiveTaskExecution.getAccountProfile().getName() + " Failed");
						}
					}

				}
			}
		}
		if (accountingVocherDocument != null) {
			for (UserDocumentDTO userDocumentDTO : userdocuments) {

				if (userDocumentDTO.getDocumentName().equalsIgnoreCase(accountingVocherDocument.getName())) {

					if (userDocumentDTO.getSmsOption()) {
						if (sendAccountingVoucherMessage(accountingVouchers, mobileNumber, company)) {
							log.info("Accounting Voucher SMS Sent to "
									+ executiveTaskExecution.getAccountProfile().getName() + " Successfully");
						} else {
							log.info("Accounting Voucher SMS Sent to "
									+ executiveTaskExecution.getAccountProfile().getName() + " Failed");
						}
					}

				}
			}
		}
	}

	public boolean sendInventoryVoucherMessage(List<InventoryVoucherHeader> inventoryVouchers, String mobileNumber,
			Company company) {
		try {
			// String authkey = "102303Axh5n6kDaubM5694f339";

			String authkey = company.getSmsApiKey();
			String sender = "SNRICH";

			String companyName = company.getAlias();

			if (!companyName.isEmpty() || companyName != null) {
				if (companyName.length() <= 6) {
					sender = companyName.toUpperCase();
				} else {
					sender = companyName.substring(0, 6).toUpperCase();
				}

			}

			StringBuilder stringBuilder = new StringBuilder();

			for (InventoryVoucherHeader inventoryVoucherHeader : inventoryVouchers) {

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				LocalDateTime documentDate = inventoryVoucherHeader.getDocumentDate();
				String date = documentDate.format(formatter);

				stringBuilder.append("Dear Customer,\n");
				stringBuilder.append("Our sales officer had visited your firm on " + date + ". ");
				stringBuilder.append("\nOrdered items are : ");

				for (InventoryVoucherDetail inventoryVoucherDetail : inventoryVoucherHeader
						.getInventoryVoucherDetails()) {

					stringBuilder.append("\n" + inventoryVoucherDetail.getProduct().getName() + "("
							+ inventoryVoucherDetail.getQuantity() + ")");

				}
				stringBuilder.append("\nTotal Items:" + inventoryVoucherHeader.getInventoryVoucherDetails().size()
						+ ",Total Amt:" + inventoryVoucherHeader.getDocumentTotal() + "\n\n");

				stringBuilder.append("Thank you for your time and consideration.\n" + company.getLegalName());

			}

			String message = stringBuilder.toString();

			log.info(sender + "\n \n" + message);

			String requestUrl = " https://api.msg91.com/api/sendhttp.php?mobiles=" + mobileNumber + "&authkey="
					+ authkey + "&route=4&sender=" + sender + "&message=" + URLEncoder.encode(message, "UTF-8")
					+ "&country=91";

			/*
			 * String requestUrl = "http://api.msg91.com/api/sendhttp.php?authkey=" +
			 * authkey + "&mobiles=+91" + mobileNumber + "&message=" +
			 * URLEncoder.encode(message, "UTF-8") + "&sender=" + sender + "&route=4";
			 */
			URL url = new URL(requestUrl);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			log.info("ResponseMessage :- " + uc.getResponseMessage());
			log.info("ResponseCode :- " + uc.getResponseCode());
			uc.disconnect();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return false;
	}

	public boolean sendAccountingVoucherMessage(List<AccountingVoucherHeader> accountingVouchers, String mobileNumber,
			Company company) {
		try {
			// String authkey = "102303Axh5n6kDaubM5694f339";

			String authkey = company.getSmsApiKey();
			String sender = "SNRICH";

			String companyName = company.getAlias();

			if (!companyName.isEmpty() || companyName != null) {
				if (companyName.length() <= 6) {
					sender = companyName.toUpperCase();
				} else {
					sender = companyName.substring(0, 6).toUpperCase();
				}

			}

			StringBuilder stringBuilder = new StringBuilder();

			/*
			 * String abc="Dear Customer, \n" +
			 * "Our sales officer had visited your firm on 24/11/2018. Cheque amount Rs 256.0. He has collected total 256.0."
			 */

			for (AccountingVoucherHeader accountingVoucherHeader : accountingVouchers) {

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				LocalDateTime documentDate = accountingVoucherHeader.getDocumentDate();
				String date = documentDate.format(formatter);

				stringBuilder.append("Dear Customer,\n");
				stringBuilder.append("Our sales officer had visited your firm on " + date + ". ");

				for (AccountingVoucherDetail accountingVoucherDetail : accountingVoucherHeader
						.getAccountingVoucherDetails()) {

					String type = "";
					if (PaymentMode.Bank.equals(accountingVoucherDetail.getMode())) {
						type = "Cheque";
					} else {
						type = accountingVoucherDetail.getMode().name();
					}

					stringBuilder.append("\n" + type + " amount Rs." + accountingVoucherDetail.getAmount());

				}

				stringBuilder.append(".\nHe has collected total " + accountingVoucherHeader.getTotalAmount() + ".\n\n");

				stringBuilder.append("Thank you for your time and consideration.\n" + company.getLegalName());

			}

			String message = stringBuilder.toString();

			log.info(sender + "\n \n" + message);

			String requestUrl = "http://api.msg91.com/api/sendhttp.php?authkey=" + authkey + "&mobiles=+91"
					+ mobileNumber + "&message=" + URLEncoder.encode(message, "UTF-8") + "&sender=" + sender
					+ "&route=4";
			URL url = new URL(requestUrl);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			log.info("ResponseMessage :- " + uc.getResponseMessage());
			log.info("ResponseCode :- " + uc.getResponseCode());
			uc.disconnect();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return false;
	}

}
