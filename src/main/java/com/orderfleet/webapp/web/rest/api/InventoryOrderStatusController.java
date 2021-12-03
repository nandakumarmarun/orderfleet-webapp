package com.orderfleet.webapp.web.rest.api;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;

@RestController
@RequestMapping("/api")
public class InventoryOrderStatusController {

	private final Logger log = LoggerFactory.getLogger(InventoryOrderStatusController.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private UserRepository userRepository;
	
	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@GetMapping("/inventory-order-status")
	@Timed
	public ResponseEntity<List<InventoryVoucherHeaderDTO>> getInventoryVoucherHeaders(
			@RequestParam(required = false, value = "fromDate") LocalDateTime fromDate,
			@RequestParam(required = false, value = "toDate") LocalDateTime toDate) {
		log.debug("request for get inventory-order-status ");
		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs = new ArrayList<>();
		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if (opUser.isPresent()) {
			User user = opUser.get();
			if (fromDate != null && toDate != null) {
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_143" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get by compId UserId date between";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findAllByCompanyIdUserIdAndDateBetweenOrderByDocumentDateDesc(user.getId(), fromDate, toDate);
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
			
				inventoryVoucherHeaderDTOs = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
						.collect(Collectors.toList());
			} else {
				 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id = "INV_QUERY_144" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description = "get Top3 by compId ExecutiveTaskExecutionUserId ";
					LocalDateTime startLCTime = LocalDateTime.now();
					String startTime = startLCTime.format(DATE_TIME_FORMAT);
					String startDate = startLCTime.format(DATE_FORMAT);
					logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findTop3ByCompanyIdAndExecutiveTaskExecutionUserPidOrderByDocumentDateDesc(
								user.getCompany().getId(), user.getPid());
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
				inventoryVoucherHeaderDTOs = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
						.collect(Collectors.toList());
			}
		}
		return new ResponseEntity<List<InventoryVoucherHeaderDTO>>(inventoryVoucherHeaderDTOs, HttpStatus.OK);

	}
	
	
	@GetMapping("/inventory-order-status-new")
	@Timed
	public ResponseEntity<List<InventoryVoucherHeaderDTO>> findInventoryVoucherHeaders(
			@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate) {
		log.debug("request for get inventory-order-status ");
		Set<String> ivhpid = new HashSet<>();
		List<InventoryVoucherDetail> ivhdlist = new ArrayList<>();
		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs = new ArrayList<>();
		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if (opUser.isPresent()) {
			User user = opUser.get();
			if (fromDate != null && toDate != null) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				LocalDate frdate = LocalDate.parse(fromDate, formatter);
				LocalDate todate = LocalDate.parse(toDate, formatter);
				LocalDateTime fromdate1 = frdate.atTime(0,0);
				LocalDateTime todate1 = todate.atTime(23,59);
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_143" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get by compId UserId date between";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findAllByCompanyIdUserIdAndDateBetweenOrderByDocumentDateDesc(user.getId(), fromdate1, todate1);
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
			
				inventoryVoucherHeaderDTOs = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
						.collect(Collectors.toList());
			} else {
				 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id = "INV_QUERY_144" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description = "get Top3 by compId ExecutiveTaskExecutionUserId ";
					LocalDateTime startLCTime = LocalDateTime.now();
					String startTime = startLCTime.format(DATE_TIME_FORMAT);
					String startDate = startLCTime.format(DATE_FORMAT);
					logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findTop3ByCompanyIdAndExecutiveTaskExecutionUserPidOrderByDocumentDateDesc(
								user.getCompany().getId(), user.getPid());
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
		                
		                 inventoryVoucherHeaders.forEach(data -> ivhpid.add(data.getPid()));

		               if (!ivhpid.isEmpty()) {
							ivhdlist=inventoryVoucherDetailRepository.findAllByInventoryVoucherHeaderPidIn(ivhpid);
							
						}
		                
		               for(InventoryVoucherHeader ivh1 : inventoryVoucherHeaders) {
		            	   List<InventoryVoucherDetail> ivhdetails = new ArrayList<>();
		            	   for(InventoryVoucherDetail ivhd : ivhdlist) {
		            		   if(ivh1.getId().equals(ivhd.getInventoryVoucherHeader().getId())) {
		            			  ivhdetails.add(ivhd);
		            			  ivh1.setInventoryVoucherDetails(ivhdetails);
		            		   }
		            	   }
		                	
		                }
		               
		                List<InventoryVoucherHeader> ivhfilter = inventoryVoucherHeaders.stream().filter(data -> {
		                	DateTimeFormatter DATE_FORMATnew = DateTimeFormatter.ofPattern("dd/MM/yyyy");
							String dataDate = data.getCreatedDate().format(DATE_FORMATnew);
							String todayDate = LocalDateTime.now().format(DATE_FORMATnew);
							if(dataDate.equals(todayDate)) {
								return true;
							}
							return false;
		                }).collect(Collectors.toList());
		                
		                
				inventoryVoucherHeaderDTOs = ivhfilter.stream().map(InventoryVoucherHeaderDTO::new)
						.collect(Collectors.toList());
		             
			}		
		}
		return new ResponseEntity<List<InventoryVoucherHeaderDTO>>(inventoryVoucherHeaderDTOs, HttpStatus.OK);

	}
}
