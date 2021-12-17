package com.orderfleet.webapp.web.rest.api;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@RestController
@RequestMapping("/api")
public class AccountWiseLastSellingDetailsController {
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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

	@SuppressWarnings("unchecked")
	@Timed
	@GetMapping("/last-selling-details")
	public  ResponseEntity<List <LastSellingDetailsDTO>> accountwiseLastSellingDetails(@RequestParam("accountPid")  String accountPid,
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
		String activityPid;
       if(activity.isPresent())
       {
		 activityPid = activity.get().getPid();
          }
       else {
    		return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("activity", "activityDoesn'texists","activity by name not present"))
					.body(null);
       }
		String documentPid = null;
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AD_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by activityPid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<ActivityDocument> activityDocument = activityDocumentRepository.findByActivityPid(activityPid);
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
		if (!activityDocument.isEmpty()) {
			Optional<ActivityDocument> opActivityDocs = activityDocument.stream()
					.filter(ad -> ad.getDocument().getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)).findAny();
			if (opActivityDocs.isPresent()) {
				 documentPid = opActivityDocs.get().getDocument().getPid();
			}
		}
List<LastSellingDetailsDTO >lastSellingDetailsDTO = inventoryVoucherHeaderService.findHeaderByAccountPidUserPidandDocPid(accountPid, userPid,documentPid,productPid);

     return new ResponseEntity<>(lastSellingDetailsDTO,HttpStatus.CREATED); 

	}
}
