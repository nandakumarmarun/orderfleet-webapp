package com.orderfleet.webapp.web.rest;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.UserDeviceService;
import com.orderfleet.webapp.web.rest.dto.UserDeviceDTO;

/**
 * Web controller for managing UserDevice.
 * 
 * @author Shaheer
 * @since November 07, 2016
 */
@Controller
@RequestMapping("/web")
public class UserDeviceResource {

	private UserDeviceService userDeviceService;

	@Inject
	public UserDeviceResource(UserDeviceService userDeviceService) {
		super();
		this.userDeviceService = userDeviceService;
	}

	/**
	 * GET /user-devices : get all userDevice.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @param model
	 *            the model attributes
	 * @return the form with userDevices in model attribute
	 */
	@GetMapping("/user-devices")
	@Timed
	public String getUserDevicesFrom(Model model) {
		
		List<UserDeviceDTO>userDevices=userDeviceService.findAllByCompanyIdAndActivatedTrue();
		model.addAttribute("userDevices", userDevices);
		return "company/user-devices";
	}

	/**
	 * Update /user-devices/:id : delete the "id" label.
	 *
	 * @param id
	 *            the id of the userDevice to Update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@PutMapping("/user-devices/{pid}")
	@Timed
	public ResponseEntity<Void> updateUserDevicesFrom(@PathVariable("pid") String pid) {
		userDeviceService.deActivateUserDeviceByPid(pid);
		return ResponseEntity.ok().build();
	}

}
