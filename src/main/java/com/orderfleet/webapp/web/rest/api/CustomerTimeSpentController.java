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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.CustomerTimeSpent;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.CustomerTimeSpentType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CustomerTimeSpentRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.DashboardWebSocketDataDTO;

/**
 * REST controller for managing CustomerTimeSpend.
 * 
 * @author fahad
 * @since jan 20, 2018
 */

@RestController
@RequestMapping("/api")
public class CustomerTimeSpentController {

	private final Logger log = LoggerFactory.getLogger(CustomerTimeSpentController.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private CustomerTimeSpentRepository customerTimeSpentRepository;

	@Inject
	private SimpMessagingTemplate simpMessagingTemplate;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Timed
	@RequestMapping(value = "/customer-time-spent", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> createCustomerTimeSpent(@RequestParam String accountProfilePid,
			@RequestParam String clientTransactionKey) {
		log.debug("Rest request to save Customer Time Spent : {}", accountProfilePid);
		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());

		if (opUser.isPresent()) {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "COMP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get by compPid and name";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			Optional<CompanyConfiguration> optinterimSave = companyConfigurationRepository
					.findByCompanyPidAndName(opUser.get().getCompany().getPid(), CompanyConfig.INTERIM_SAVE);
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
			if (optinterimSave.isPresent()) {
				List<CustomerTimeSpent> customerTimeSpents = customerTimeSpentRepository
						.findAllByUserIdAndActiveTrue(opUser.get().getId());
				EmployeeProfile employeeProfile = employeeProfileRepository.findEmployeeProfileByUser(opUser.get());
				DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id1 = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description1 ="get one by pid";
				LocalDateTime startLCTime1 = LocalDateTime.now();
				String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
				String startDate1 = startLCTime1.format(DATE_FORMAT1);
				logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
				Optional<AccountProfile> opaccountProfile = accountProfileRepository.findOneByPid(accountProfilePid);
				 String flag1 = "Normal";
					LocalDateTime endLCTime1 = LocalDateTime.now();
					String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
					String endDate1 = startLCTime1.format(DATE_FORMAT1);
					Duration duration1 = Duration.between(startLCTime1, endLCTime1);
					long minutes1 = duration1.toMinutes();
					if (minutes1 <= 1 && minutes1 >= 0) {
						flag1 = "Fast";
					}
					if (minutes1 > 1 && minutes1 <= 2) {
						flag1 = "Normal";
					}
					if (minutes1 > 2 && minutes1 <= 10) {
						flag1 = "Slow";
					}
					if (minutes1 > 10) {
						flag1 = "Dead Slow";
					}
			                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
							+ description1);

				if (opaccountProfile.isPresent()) {
					DashboardWebSocketDataDTO dashboardWebSocketDataDTO = new DashboardWebSocketDataDTO();
					CustomerTimeSpent customerTimeSpent = new CustomerTimeSpent();
					customerTimeSpent.setAccountProfile(opaccountProfile.get());
					customerTimeSpent.setEmployeeProfile(employeeProfile);
					customerTimeSpent.setUser(opUser.get());
					customerTimeSpent.setStartTime(LocalDateTime.now());
					customerTimeSpent.setEndTime(LocalDateTime.now());
					customerTimeSpent.setCompany(employeeProfile.getCompany());
					customerTimeSpent.setActive(true);
					customerTimeSpent.setClientTransactionKey(clientTransactionKey);
					customerTimeSpentRepository.save(customerTimeSpent);
					if (customerTimeSpents.isEmpty()) {
						dashboardWebSocketDataDTO.setUserPid(opUser.get().getPid());
						dashboardWebSocketDataDTO.setLastTime(LocalDateTime.now());
						dashboardWebSocketDataDTO.setCustomerTimeSpentType(CustomerTimeSpentType.RESET);
						simpMessagingTemplate.convertAndSend(
								"/live-tracking/dashboard-view/" + employeeProfile.getCompany().getId(),
								dashboardWebSocketDataDTO);
					}
				}
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);

	}

}
