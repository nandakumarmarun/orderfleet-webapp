package com.orderfleet.webapp.web.rest.api;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityDocument;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.ActivityDocumentRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;

import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.web.rest.api.dto.LastSellingDetailsDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;

@RestController
@RequestMapping("/api")
public class AccountWiseLastSellingDetailsController {

	@Inject
	UserRepository userRepository;

	@Inject
	DocumentService documentService;

	@Inject
	InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	InventoryVoucherHeaderService inventoryVoucherHeaderService;

	@Inject
	CompanyRepository companyRepository;

	@Inject
	ActivityRepository activityRepository;

	@Inject
	ActivityDocumentRepository activityDocumentRepository;

	@Timed
	@GetMapping("/last-selling-details")
	public List<LastSellingDetailsDTO> accountwiseLastSellingDetails(@RequestParam("accountPid")  String accountPid,
			@RequestParam("productPid")  String productPid) {

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String pid = company.getPid();
         String userPid = null;
		Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if (user.isPresent()) {
		userPid = user.get().getPid();
		}

		String name = "Sales Data Transfer";

		Optional<Activity> activity = activityRepository.findByCompanyPidAndNameIgnoreCase(pid, name);

		String activityPid = activity.get().getPid();

		String documentPid = null;
		List<ActivityDocument> activityDocument = activityDocumentRepository.findByActivityPid(activityPid);
		if (!activityDocument.isEmpty()) {
			Optional<ActivityDocument> opActivityDocs = activityDocument.stream()
					.filter(ad -> ad.getDocument().getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)).findAny();
			if (opActivityDocs.isPresent()) {
				 documentPid = opActivityDocs.get().getDocument().getPid();
			}
		}
List<LastSellingDetailsDTO >lastSellingDetailsDTO = inventoryVoucherHeaderService.findHeaderByAccountPidUserPidandDocPid(accountPid, userPid,documentPid,productPid);

		return lastSellingDetailsDTO ;

	}
}
