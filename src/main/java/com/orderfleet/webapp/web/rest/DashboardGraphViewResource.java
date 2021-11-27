package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.DashboardItem;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DashboardItemConfigType;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.DashboardItemRepository;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.custom.DashboardGraphRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.LocationHierarchyService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.web.rest.dto.DashboardGraphDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardLocationLineGraphDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;

/**
 * Web controller for managing Dash board territory wise.
 * 
 * @author Shaheer
 * @since June 05, 2017
 */
@Controller
@RequestMapping(value = "/web/dashboard/graph-view")
public class DashboardGraphViewResource {

	private final Logger log = LoggerFactory.getLogger(DashboardGraphViewResource.class);

	@Inject
	private AttendanceRepository attendanceRepository;

	@Inject
	private DashboardItemRepository dashboardItemRepository;

	@Inject
	private LocationHierarchyRepository locationHierarchyRepository;

	@Inject
	private LocationHierarchyService locationHierarchyService;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private LocationService locationService;
	
	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;
	
	@Inject
	private DashboardUserRepository dashboardUserRepository;
	
	@Inject
	private DashboardGraphRepositoryCustom dashboardGraphRepositoryCustom;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private ServletContext servletContext;

	@Timed
	@RequestMapping(value = { "/", "" }, method = RequestMethod.GET)
	public String dashboard(Model model) {
		log.info("Web request to get dashboard territory wise");
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		locationHierarchyRepository.findRootLocationByCompanyId(SecurityUtils.getCurrentUsersCompanyId())
				.ifPresent(lh -> {
					model.addAttribute("companyId", companyId);
					model.addAttribute("territory", lh.getLocation());
				});
		model.addAttribute("locations", locationService.findAllByCompany());
		// theme
		Optional<CompanyConfiguration> companyConfiguration = companyConfigurationRepository
				.findByCompanyIdAndName(companyId, CompanyConfig.THEME);
		if (companyConfiguration.isPresent()) {
			servletContext.setAttribute("currentcss", companyConfiguration.get().getValue());
		} else {
			servletContext.setAttribute("currentcss", "white.css");
		}
		return "company/dashboard-graphview";
	}

	@RequestMapping(value = "/child-territories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LocationDTO>> getChildTerritories(@RequestParam("territoryId") Long territoryId) {
		return new ResponseEntity<>(locationHierarchyService.findChildLocationsByParentId(territoryId), HttpStatus.OK);
	}

	@RequestMapping(value = "/parent-territory", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LocationDTO> getParentTerrity(@RequestParam("territoryId") Long territoryId) {
		return new ResponseEntity<>(locationHierarchyService.findParentLocation(territoryId), HttpStatus.OK);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/summary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DashboardGraphDTO> getDashboardSummaryData(
			@RequestParam(value = "date", required = false) LocalDate date,
			@RequestParam("territoryId") Long territoryId) {
		log.info("Web request to get graph dashboard  summary Day/Week/Month wise");
		return new ResponseEntity<>(loadDashboardHeaderSummary(date.atTime(0, 0).minusDays(30), date.atTime(23, 59), territoryId),HttpStatus.OK);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/territorytiles-summary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DashboardLocationLineGraphDTO>> getDashboardTilesAccountWiseData(
			@RequestParam(value = "date", required = false) LocalDate date,
			@RequestParam("territoryId") Long territoryId) {
		log.info("Web request to get territory tiles sepecific data");
		return new ResponseEntity<>(createTerritoryTilesData(date.atTime(0, 0).minusDays(30), date.atTime(23, 59), territoryId), HttpStatus.OK);
	}

	private DashboardGraphDTO loadDashboardHeaderSummary(LocalDateTime from, LocalDateTime to,
			Long territoryId) {
		List<Long> locationIds = locationHierarchyService.getAllChildrenIdsByParentId(territoryId);
		List<AccountProfile> accountProfiles = locationAccountProfileRepository
				.findAccountProfileByLocationIdIn(locationIds);
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		List<DashboardItem> dashboardChartItems = dashboardItemRepository.findByCompanyIdAndDashboardItemConfigType(DashboardItemConfigType.CHART);
		if(dashboardChartItems.isEmpty()) {
			return null;
		}
		return dashboardGraphRepositoryCustom.getPerformanceChartData(dashboardChartItems, accountProfiles, companyId, from, to);
	}

	private List<DashboardLocationLineGraphDTO> createTerritoryTilesData(LocalDateTime from, LocalDateTime to,
			Long parentLocationId) {
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		List<DashboardLocationLineGraphDTO> dashboardTerritoryDatas = new ArrayList<>();
		List<DashboardItem> dashboardChartItems = dashboardItemRepository.findByCompanyIdAndDashboardItemConfigType(DashboardItemConfigType.CHART);
		if(dashboardChartItems.isEmpty()) {
			return Collections.emptyList();
		}
		List<LocationDTO> dashboardTerritories;
		// get children
		List<Long> childLocationIds = locationHierarchyRepository.findIdsByParentIdAndActivatedTrue(parentLocationId);
		if (childLocationIds != null && !childLocationIds.isEmpty()) {
			dashboardTerritories = locationService.findAllByCompanyAndLocationIdIn(childLocationIds);
			dashboardTerritories.forEach(t -> {
				// get account profile in all hierarchical data under this
				// territory
				List<Long> locationIds = locationHierarchyService.getAllChildrenIdsByParentId(Long.valueOf(t.getId()));
				if (!locationIds.isEmpty()) {
					List<AccountProfile> accountProfiles = locationAccountProfileRepository
							.findAccountProfileByLocationIdIn(locationIds);
					DashboardLocationLineGraphDTO dashboardTerritoryData = new DashboardLocationLineGraphDTO();
					dashboardTerritoryData.setLocationId(Long.valueOf(t.getId()));
					dashboardTerritoryData.setLocationPid(t.getPid());
					dashboardTerritoryData.setLocationName(t.getName());
					dashboardTerritoryData.setAccountProfilePids(accountProfiles.stream().map(AccountProfile::getPid).collect(Collectors.toList()));
					// set attendance details
					Set<Long> uniqueUserIds = employeeProfileLocationRepository
							.findEmployeeUserIdsByLocationIdIn(locationIds);
					//dash board configured users
					Set<Long> userIds = dashboardUserRepository.findUserIdsByUserIdIn(uniqueUserIds);
					dashboardTerritoryData.setAttendenceTotalUsers(userIds.size());
					if(!userIds.isEmpty()) {
						String id="ATT_QUERY_108";
						String description="count attendance by userId in and date between";
						log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
						dashboardTerritoryData.setAttendencePresentUsers(attendanceRepository.countByUserIdInAndDateBetween(new ArrayList<>(userIds), from,
								to));	
					}
					// set account wise summary
					dashboardTerritoryData.setDashboardGraphDTO(dashboardGraphRepositoryCustom.getPerformanceChartData(dashboardChartItems, accountProfiles, companyId, from, to));
					dashboardTerritoryDatas.add(dashboardTerritoryData);
				}
			});
		}
		return dashboardTerritoryDatas;
	}
}
