package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DayPlanPages;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DayPlanExecutionConfigService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.DayPlanExecutionConfigDTO;

/**
 * Web controller for managing DayPlanExecutionConfig.
 * 
 * @author Muhammed Riyas T
 * @since Jan 03, 2017
 */
@Controller
@RequestMapping("/web")
public class DayPlanExecutionConfigResource {

	private final Logger log = LoggerFactory.getLogger(DayPlanExecutionConfigResource.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private DayPlanExecutionConfigService dayPlanExecutionConfigService;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserService userService;

	/**
	 * GET /dayPlanExecutionConfig : get all the dayPlanExecutionConfigs.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         dayPlanExecutionConfigs in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/dayPlanExecutionConfig", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllDayPlanExecutionConfig(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of DayPlanExecutionConfigs");
		model.addAttribute("dayPlanExecutionConfigs", getDayPlanExecutionConfigs());
		model.addAttribute("users", userService.findAllByCompany());
		return "company/dayPlanExecutionConfig";
	}

	private List<DayPlanExecutionConfigDTO> getDayPlanExecutionConfigs() {
		List<DayPlanPages> dayPlanPages = Arrays.asList(DayPlanPages.values());
		List<DayPlanExecutionConfigDTO> dayPlanExecutionConfigDTOs = dayPlanExecutionConfigService.findAllByCompany();
		for (DayPlanPages dayPlanPage : dayPlanPages) {
			boolean exist = false;
			for (DayPlanExecutionConfigDTO dayPlanExecutionConfigDTO : dayPlanExecutionConfigDTOs) {
				if (dayPlanExecutionConfigDTO.getName().equals(dayPlanPage)) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				DayPlanExecutionConfigDTO dayPlanExecutionConfigDTO = new DayPlanExecutionConfigDTO();
				dayPlanExecutionConfigDTO.setName(dayPlanPage);
				dayPlanExecutionConfigDTOs.add(dayPlanExecutionConfigDTO);
			}
		}
		return dayPlanExecutionConfigDTOs;
	}

	/**
	 * POST /dayPlanExecutionConfig : Create a new dayPlanExecutionConfig.
	 *
	 * @param dayPlanExecutionConfigDTO
	 *            the dayPlanExecutionConfigDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new dayPlanExecutionConfigDTO, or with status 400 (Bad Request)
	 *         if the dayPlanExecutionConfig has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@Timed
	@RequestMapping(value = "/dayPlanExecutionConfig", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> createDayPlanExecutionConfig(
			@RequestBody List<DayPlanExecutionConfigDTO> dayPlanExecutionConfigDTOs) throws URISyntaxException {
		log.debug("Web request to save DayPlanExecutionConfigs ");
		dayPlanExecutionConfigService.save(dayPlanExecutionConfigDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/dayPlanExecutionConfig/account-purchase-history-duration", method = RequestMethod.GET)
	public @ResponseBody int getAccountPurchaseHistoryDuration() throws URISyntaxException {
		log.debug("Web request to get AccountPurchaseHistoryDuration");
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "COMP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 ="get by compId and name";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		Optional<CompanyConfiguration> optCompanyConfiguration = companyConfigurationRepository.findByCompanyIdAndName(
				SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.ACCOUNT_PURCHASE_HISTORY_DURATION);
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
		if (optCompanyConfiguration.isPresent()) {
			return Integer.valueOf(optCompanyConfiguration.get().getValue());
		}
		return 1;
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/dayPlanExecutionConfig/account-purchase-history-duration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> saveAccountPurchaseHistoryDuration(@RequestParam String month) throws URISyntaxException {
		log.debug("Web request to save AccountPurchaseHistoryDuration ");
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
        DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "COMP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 ="get by compId and name";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		Optional<CompanyConfiguration> optCompanyConfiguration = companyConfigurationRepository.findByCompanyIdAndName(
				SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.ACCOUNT_PURCHASE_HISTORY_DURATION);
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
		CompanyConfiguration companyConfiguration = null;
		if (optCompanyConfiguration.isPresent()) {
			companyConfiguration = optCompanyConfiguration.get();
			companyConfiguration.setValue(month);
		} else {
			companyConfiguration = new CompanyConfiguration();
			companyConfiguration.setName(CompanyConfig.ACCOUNT_PURCHASE_HISTORY_DURATION);
			companyConfiguration.setValue(month);
			companyConfiguration.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		}
		companyConfigurationRepository.save(companyConfiguration);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/dayPlanExecutionConfig/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Set<UserDTO> getDayPlanExecutionConfigDTO(@PathVariable Long id) {
		log.debug("Web request to get dayPlanExecutionConfig by id : {}", id);
		return dayPlanExecutionConfigService.findById(id).getUsers();
	}

	@Timed
	@RequestMapping(value = "/dayPlanExecutionConfig/assignUsers", method = RequestMethod.POST)
	public ResponseEntity<Void> assignDocuments(@RequestParam Long id, @RequestParam String assignedUsers) {
		log.debug("REST request to save assigned users: {}", id);
		dayPlanExecutionConfigService.saveAssignedUsers(id, assignedUsers);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
