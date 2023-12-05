package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.formula.functions.T;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.geolocation.model.GTowerResponse;
import com.orderfleet.webapp.geolocation.model.TowerLocation;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.ExecutiveTaskExecutionService;
import com.orderfleet.webapp.service.LiveRoutingService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;
import com.orderfleet.webapp.web.rest.dto.LiveRoutingResponse;
import com.orderfleet.webapp.web.rest.dto.LiveTrackingDTO;
import com.orderfleet.webapp.web.rest.dto.LocationData;
import com.orderfleet.webapp.web.rest.util.RestUtil;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;

@Controller
@RequestMapping("/web")
public class LiveRoutingResourse {

	private final Logger log = LoggerFactory.getLogger(LiveRoutingResourse.class);

	private static final String AUTH_KEY = "AIzaSyB_KOvF4OXz0C6gM7kLE8BrIhgBjs2QLsg";

	private static final String FIREBASE_URL = "https://salesnrich-8ec69-default-rtdb.firebaseio.com/SalesNrich";

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private LiveRoutingService liveRoutingService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private GeoLocationService geoLocationService;

	@Inject
	private ExecutiveTaskExecutionService executiveTaskExecutionService;

	@RequestMapping("/live-routing")
	public String executives(Model model) {
		log.info("liveRouting.........................");
		model.addAttribute("companyId", SecurityUtils.getCurrentUsersCompanyId());
		return "company/liveRouting";
	}

	LocalTime start = LocalTime.now();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");
	String time1 = start.format(formatter);

	@RequestMapping(value = "/uploadUserLocations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public @ResponseBody List<LiveTrackingDTO> uploadUserLocations(
			@RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
			throws IOException, JSONException, ParseException, java.text.ParseException {

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String companyName = company.getLegalName();
		Long companyId = company.getId();

		List<EmployeeProfileDTO> dashboardEmployees;
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();

		List<Long> dashBoarduserId = dashboardUserRepository.findDashBoardUserIdsByUserIdInAndCompanyId(userIds);

		if (dashBoarduserId.isEmpty()) {
			dashboardEmployees = employeeProfileService.findAllByCompany();
		} else {
			dashboardEmployees = employeeProfileService.findAllEmployeeByUserIdsIn(dashBoarduserId);
		}

		List<LiveTrackingDTO> liveTrackingDTOs = new ArrayList<>();
		dashboardEmployees.forEach(employee -> {
			LiveTrackingDTO liveTrackingDTO = new LiveTrackingDTO();
			liveTrackingDTO.setEmployeeName(employee.getName());
			liveTrackingDTO.setUserPid(employee.getUserPid());
			liveTrackingDTO.setUserName(employee.getUserFirstName());
			System.out.println(employee.getUserLogin());
			String login = employee.getUserLogin();
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			HttpEntity<T> entity = new HttpEntity<>(createTokenAuthHeaders());

			ResponseEntity<Map<String, LocationData>> liveRoutingResponse = restTemplate.exchange(
					FIREBASE_URL + "/" + companyName.replaceAll("%20", " ") + "/" + login + ".json?orderBy=\"$key\"&limitToLast=1", HttpMethod.GET,
					entity, new ParameterizedTypeReference<Map<String, LocationData>>() {
					});

			List<ExecutiveTaskExecutionDTO> trackingPoints = new ArrayList<>();

			if (liveRoutingResponse.getBody() != null) {
				Map<String, LocationData> response = liveRoutingResponse.getBody();

				System.out.println("Size :"+response.toString());
				String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

				List<LiveRoutingResponse> routing = new ArrayList<>();
				for (Map.Entry<String, LocationData> entries : response.entrySet()) {
					List<LocationData> loc = new ArrayList<LocationData>();
					LocationData fbid = new LocationData();
					LiveRoutingResponse liveRouting = new LiveRoutingResponse();

					fbid.setKey(entries.getKey());
					fbid.setCompanyName(entries.getValue().getCompanyName());
					fbid.setCurrentDateTime(entries.getValue().getCurrentDateTime());
					fbid.setLatitude(entries.getValue().getLatitude());
					fbid.setLongitude(entries.getValue().getLongitude());
			    	fbid.setBattery_percentage(entries.getValue().getBattery_percentage());
					fbid.setAddress(entries.getValue().getAddress());
					loc.add(fbid);
					liveRouting.setLocations(loc);
					routing.add(liveRouting);
				}

				liveRoutingService.saveLocationDetails(routing,companyId);

				for (LiveRoutingResponse responses : routing) {

					for (LocationData locations : responses.getLocations())

					{
						ExecutiveTaskExecutionDTO executiveTaskExecutionDTO = new ExecutiveTaskExecutionDTO();
						String datetime = locations.getCurrentDateTime();
						String[] splitDate = datetime.split(" ");

						if (formattedDate.equals(splitDate[0])) {
							executiveTaskExecutionDTO.setLatitude(locations.getLatitude());
							executiveTaskExecutionDTO.setLongitude(locations.getLongitude());
//							String location = geoLocationService
//									.findAddressFromLatLng(locations.getLatitude() + "," + locations.getLongitude());
							executiveTaskExecutionDTO.setLocation(locations.getAddress());
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
							LocalDateTime createdDate = LocalDateTime.parse(locations.getCurrentDateTime(), formatter);
							executiveTaskExecutionDTO.setDate(createdDate);
							executiveTaskExecutionDTO.setBatteryPercentage(locations.getBattery_percentage());
							executiveTaskExecutionDTO.setLocationType(LocationType.GpsLocation);
							executiveTaskExecutionDTO.setAccountProfileName("Route");
							trackingPoints.add(executiveTaskExecutionDTO);
						}
					}

				}
			}

			liveTrackingDTO.setTrackingPoints(trackingPoints);
			liveTrackingDTOs.add(liveTrackingDTO);

		});

//		for (LiveTrackingDTO ltdto : liveTrackingDTOs) {
//
//			int last = ltdto.getTrackingPoints().size() - 1;
//			if (ltdto.getTrackingPoints().size() != 0) {
//				ExecutiveTaskExecutionDTO dtos = ltdto.getTrackingPoints().get(last);
//				ltdto.getTrackingPoints().remove(dtos);
//
//				LocalDateTime dates = dtos.getDate();
//				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//				String formattedDateTime = dates.format(formatter);
//
//				String[] parts = formattedDateTime.split(" ");
//
//				SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
//				Date date1 = format.parse(time1);
//				Date date2 = format.parse(parts[1]);
//				long difference = 0;
//				if(date2.getTime()>=date1.getTime())
//				{
//				difference = date2.getTime()- date1.getTime();
//				}
//				else {
//					difference = date1.getTime()- date2.getTime();
//				}
//				 long minutes = (difference / 1000) / 60;
//
//				if (minutes < 5) {
//					dtos.setAccountProfileName("CurrentLocation");
//
//					ltdto.getTrackingPoints().add(dtos);
//				}
//				else {
//					ltdto.getTrackingPoints().add(dtos);
//				}
//
//			}
//		}
		return liveTrackingDTOs;
	}

	public static MultiValueMap<String, String> createTokenAuthHeaders() {
		MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<String, String>();
		requestHeaders.add("Authorization", AUTH_KEY);
		requestHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		return requestHeaders;

	}

}
