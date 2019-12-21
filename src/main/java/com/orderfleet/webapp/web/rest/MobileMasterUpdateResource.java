package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.MobileMasterDetail;
import com.orderfleet.webapp.domain.MobileMasterUpdate;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.MobileMasterUpdateRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.UserDeviceService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.MobileMasterDetailDTO;
import com.orderfleet.webapp.web.rest.dto.UserDeviceDTO;

/**
 * Controller for MobileMasterUpdateResource
 *
 * @author Prashob
 * @since Dec 18,2019
 */
@RequestMapping("/web")
@Controller
public class MobileMasterUpdateResource {

	private final Logger log = LoggerFactory.getLogger(MobileMasterUpdateResource.class);

	@Inject
	private CompanyService companyService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private MobileMasterUpdateRepository mobileMasterUpdateRepository;

	@Inject
	private UserService userService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@GetMapping("/mobile-master-update")
	@Timed
	public String getMobileMasterUpdate(Model model) {
		model.addAttribute("companies", companyService.findAllCompanySortedByName());
		return "site_admin/mobile-master-update";
	}

	@RequestMapping(value = "/load-company-users/{companypid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<UserDTO> getAllCompanyUsersByCompany(@Valid @PathVariable("companypid") String companypid)
			throws URISyntaxException {
		log.debug("Web request to Get Users by companyPid: {}", companypid);
		List<UserDTO> users = userService.findAllByCompanyPid(companypid);
		return users;
	}

	@RequestMapping(value = "/mobile-master-update/getUsersMobileMasterUpdate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<MobileMasterDetailDTO> getUsersMobileMasterUpdate(@RequestParam String companyPid,
			@RequestParam String userPid) throws URISyntaxException {
		log.debug("Web request to Get Mobile Master Update: {}", userPid);

		List<MobileMasterDetailDTO> mobileMasterDetailDTOs = new ArrayList<>();
		if (companyPid != null && userPid != null) {
			List<MobileMasterUpdate> mobileMasterUpdates = mobileMasterUpdateRepository
					.findAllByCompanyPidAndUserPid(companyPid, userPid);
			if (mobileMasterUpdates.size() > 0) {
				mobileMasterDetailDTOs = convertMasterUpdateToView(mobileMasterUpdates, userPid);
			}
		}
		return mobileMasterDetailDTOs;
	}

	private List<MobileMasterDetailDTO> convertMasterUpdateToView(List<MobileMasterUpdate> mobileMasterUpdates,
			String userPid) {

		String employeeName = employeeProfileService.findByUserPid(userPid).get().getName();

		List<MobileMasterDetailDTO> mobileMasterDetailDTOs = new ArrayList<>();

		for (MobileMasterUpdate mobileMasterUpdate : mobileMasterUpdates) {

			if (mobileMasterUpdate.getMobileMasterDetails() != null
					&& mobileMasterUpdate.getMobileMasterDetails().size() > 0) {

				for (MobileMasterDetail mobileMasterDetail : mobileMasterUpdate.getMobileMasterDetails()) {
					MobileMasterDetailDTO mobileMasterDetailDTO = new MobileMasterDetailDTO();

					mobileMasterDetailDTO.setCount(mobileMasterDetail.getCount());
					mobileMasterDetailDTO.setCreatedDate(mobileMasterDetail.getCreatedDate());
					mobileMasterDetailDTO.setMasterItem(mobileMasterDetail.getMobileMasterItem().toString());
					mobileMasterDetailDTO.setUserName(mobileMasterUpdate.getUser().getLogin());
					mobileMasterDetailDTO.setEmployeeName(employeeName);
					mobileMasterDetailDTO.setUserBuildVersion(mobileMasterUpdate.getUserBuildVersion());

					double totalTimeMs = Double.parseDouble(
							mobileMasterUpdate.getUpdateTime() != null ? mobileMasterUpdate.getUpdateTime() : "0");
					double totalTimeSec = Math.round(totalTimeMs / 1000.0);
					mobileMasterDetailDTO.setTotalTime(String.valueOf(totalTimeSec) + " Sec");

					double totalOpTimeMs = Double.parseDouble(
							mobileMasterDetail.getOperationTime() != null ? mobileMasterDetail.getOperationTime()
									: "0");
					double totalOpTimeSec = Math.round(totalOpTimeMs / 1000.0);
					mobileMasterDetailDTO.setOperationTime(String.valueOf(totalOpTimeSec) + " Sec");

					mobileMasterDetailDTOs.add(mobileMasterDetailDTO);

				}
			}
		}

		mobileMasterDetailDTOs.sort((MobileMasterDetailDTO m1, MobileMasterDetailDTO m2)->m1.getMasterItem().compareTo(m2.getMasterItem()));
		return mobileMasterDetailDTOs;
	}

}
