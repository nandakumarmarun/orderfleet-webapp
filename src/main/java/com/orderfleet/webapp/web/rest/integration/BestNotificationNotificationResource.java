package com.orderfleet.webapp.web.rest.integration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.domain.enums.NotificationMessageType;
import com.orderfleet.webapp.domain.model.FirebaseData;
import com.orderfleet.webapp.repository.AccountingVoucherDetailRepository;
import com.orderfleet.webapp.repository.BestPerformanceConfigurationRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.UserDeviceService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.async.FirebaseService;
import com.orderfleet.webapp.web.rest.dto.BestPerformerDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.FirebaseRequest;
import com.orderfleet.webapp.web.rest.dto.FirebaseResponse;
import com.orderfleet.webapp.web.rest.dto.NotificationStatusDTO;
import com.orderfleet.webapp.web.rest.dto.TallyResponseDTO;
import com.orderfleet.webapp.web.rest.dto.UserDeviceDTO;

/**
 * REST controller for send best performer notifications.
 *
 * @author Sarath
 * @since Apr 10, 2018
 *
 */
@RestController
@RequestMapping(value = "/api/tp")
public class BestNotificationNotificationResource {

	private final Logger log = LoggerFactory.getLogger(BestNotificationNotificationResource.class);

	@Inject
	private BestPerformanceConfigurationRepository bestPerformanceConfigurationRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private AccountingVoucherDetailRepository accountingVoucherDetailRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private UserDeviceService userDeviceService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private FirebaseService firebaseService;

	@Inject
	private UserService userService;

	@RequestMapping(value = "/send-notification", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TallyResponseDTO> createAccountTypesJSON() {
		log.debug("REST request to send-notification ");

		BestPerformerDTO bestPerformerDTO = getFilterData(LocalDate.now(), LocalDate.now());

		String bestReceiptPerformer = "";
		String bestSalesPerformer = "";
		if (!bestPerformerDTO.getReceiptPerformer().isEmpty()) {
			Entry<String, Double> entry = bestPerformerDTO.getReceiptPerformer().entrySet().iterator().next();
			String key = entry.getKey();
			Double value = entry.getValue();
			bestReceiptPerformer = "Collection -  " + key + "\n" + "Volume -  " + value;
		}

		if (!bestPerformerDTO.getSalesPerformer().isEmpty()) {
			Entry<String, Double> entry = bestPerformerDTO.getSalesPerformer().entrySet().iterator().next();
			String key = entry.getKey();
			Double value = entry.getValue();
			bestSalesPerformer = "Sales Order - " + key + "\n" + "Volume -  " + value;
		}

		List<UserDeviceDTO> userDevices;
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			userDevices = userDeviceService.findAllByCompanyIdAndActivatedTrueConsistEmployee();
		} else {
			userDevices = userDeviceService.findByCompanyIdAndActivatedTrueAndUserPidInConsistEmployee(userIds);
		}

		if (!userDevices.isEmpty() && (!bestPerformerDTO.getSalesPerformer().isEmpty()
				|| !bestPerformerDTO.getReceiptPerformer().isEmpty())) {
			pushNotificationToFCM(userDevices, bestReceiptPerformer, bestSalesPerformer);
		}
		return new ResponseEntity<>(new TallyResponseDTO("OK", "The notification has been sent successfully", null),
				HttpStatus.CREATED);
	}

	private NotificationStatusDTO pushNotificationToFCM(List<UserDeviceDTO> userDeviceDTOs, String bestReceiptPerformer,
			String bestSalesPerformer) {
		// create fcm request object
		User createdUser = userService.getUserWithAuthorities();
		String[] usersFcmKeys = userDeviceDTOs.stream().map(UserDeviceDTO::getFcmKey).toArray(String[]::new);
		String msg = "\n\n" + "STARS OF THE DAY " + "\n\n" + bestSalesPerformer + "\n\n" + bestReceiptPerformer;

		FirebaseRequest firebaseRequest = new FirebaseRequest();
		firebaseRequest.setRegistrationIds(usersFcmKeys);
		FirebaseData data = new FirebaseData();
		data.setTitle("STARS OF THE DAY");
		data.setMessage(msg);
		data.setMessageType(NotificationMessageType.INFO);
		data.setPidUrl(createdUser.getPid());
		data.setNotificationPid("");
		data.setSentDate(LocalDateTime.now().toString());
		firebaseRequest.setData(data);
		try {
			FirebaseResponse response = firebaseService.sendSynchronousNotificationToUsers(firebaseRequest, createdUser.getLogin());
			NotificationStatusDTO notificationStatusDTO = new NotificationStatusDTO();
			notificationStatusDTO.setSuccess(response.getSuccess());
			notificationStatusDTO.setFailed(response.getFailure());
			notificationStatusDTO.setTotal(response.getSuccess() + response.getFailure());
			return notificationStatusDTO;
		} catch (Exception e) {
			log.warn("Notification could not be sent to devices '{}'",
					Arrays.toString(firebaseRequest.getRegistrationIds()), e);
		}
		return null;
	}

	private BestPerformerDTO getFilterData(LocalDate fDate, LocalDate tDate) {
		List<EmployeeProfileDTO> employeeProfileDTOs = employeeProfileService.findAllByCompany();
		List<Document> salesDocuments = bestPerformanceConfigurationRepository
				.findAllDocumentsByBestPerformanceType(BestPerformanceType.SALES);
		List<Document> receiptDocuments = bestPerformanceConfigurationRepository
				.findAllDocumentsByBestPerformanceType(BestPerformanceType.RECEIPT);

		BestPerformerDTO bestPerformerDTO = new BestPerformerDTO();
		if (!salesDocuments.isEmpty()) {
			Set<Long> docIds = salesDocuments.stream().map(Document::getId).collect(Collectors.toSet());
			List<Object[]> ivDetails = inventoryVoucherDetailRepository.findByDocumentIdInAndDateBetween(docIds,
					fDate.atTime(0, 0), tDate.atTime(23, 59));
			if (!ivDetails.isEmpty()) {
				for (EmployeeProfileDTO emp : employeeProfileDTOs) {
					bestPerformerDTO.getSalesPerformer().put(emp.getName(),
							getSumOfAmountOrQuantity(ivDetails, emp.getPid(), fDate, tDate));
				}
			}
		}
		if (!receiptDocuments.isEmpty()) {
			Set<Long> docIds = receiptDocuments.stream().map(Document::getId).collect(Collectors.toSet());
			List<Object[]> avDetails = accountingVoucherDetailRepository
					.findByDocumentIdInAndDateBetweenOrderByCreatedDateDesc(docIds, fDate.atTime(0, 0),
							tDate.atTime(23, 59));
			if (!avDetails.isEmpty()) {
				for (EmployeeProfileDTO emp : employeeProfileDTOs) {
					bestPerformerDTO.getReceiptPerformer().put(emp.getName(),
							getSumOfAmountOrQuantity(avDetails, emp.getPid(), fDate, tDate));
				}
			}
		}
		// sort
		bestPerformerDTO.setSalesPerformer(bestPerformerDTO.getSalesPerformer().entrySet().stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new)));
		bestPerformerDTO.setReceiptPerformer(bestPerformerDTO.getReceiptPerformer().entrySet().stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new)));
		return bestPerformerDTO;
	}

	private Double getSumOfAmountOrQuantity(List<Object[]> avDetails, String employeePid, LocalDate startDate,
			LocalDate endDate) {
		return avDetails.parallelStream().filter(avd -> employeePid.equals(avd[0].toString()))
				.filter(avd -> (((LocalDateTime) avd[2]).toLocalDate().compareTo(startDate) >= 0)
						&& (((LocalDateTime) avd[2]).toLocalDate().compareTo(endDate) <= 0))
				.collect(Collectors.summingDouble(avd -> (Double) avd[3]));
	}

}
