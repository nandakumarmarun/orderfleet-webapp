package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.UserDevice;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.UserDeviceService;
import com.orderfleet.webapp.service.WhiteListedDevicesService;
import com.orderfleet.webapp.web.rest.dto.WhiteListedDevicesDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class WhiteListedDevicesResource {

	private final Logger log = LoggerFactory.getLogger(WhiteListedDevicesResource.class);
	
	@Inject
	private WhiteListedDevicesService whiteListedDevicesService;
	
	@Inject
	private UserDeviceService userDeviceService;
	
	@RequestMapping(value = "/white-listed-devices", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public ResponseEntity<WhiteListedDevicesDTO> createWhiteListedDevices(@Valid @RequestBody WhiteListedDevicesDTO whiteListedDevicesDTO)
			throws URISyntaxException {
		log.debug("Web request to save WhiteListedDevices : {}", whiteListedDevicesDTO);
		if (whiteListedDevicesDTO.getId() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("whiteListedDevices", "idexists", "A new White Listed Devices cannot already have an ID"))
					.body(null);
		}
		if (whiteListedDevicesService.findByDeviceName(whiteListedDevicesDTO.getDeviceName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("whiteListedDevices", "nameexists", "White Listed Devices already in use"))
					.body(null);
		}
		WhiteListedDevicesDTO result = whiteListedDevicesService.save(whiteListedDevicesDTO);
		return ResponseEntity.created(new URI("/web/white-listed-devices/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("whiteListedDevices", result.getId().toString())).body(result);
	}
	
	@RequestMapping(value = "/white-listed-devices", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public ResponseEntity<WhiteListedDevicesDTO> updateWhiteListedDevices(@Valid @RequestBody WhiteListedDevicesDTO whiteListedDevicesDTO)
			throws URISyntaxException {
		log.debug("REST request to update WhiteListedDevices : {}", whiteListedDevicesDTO);
		if (whiteListedDevicesDTO.getId() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("whiteListedDevices", "idNotexists", "WhiteListedDevices must have an ID"))
					.body(null);
		}
		Optional<WhiteListedDevicesDTO> existingWhiteListedDevices = whiteListedDevicesService.findByDeviceName(whiteListedDevicesDTO.getDeviceName());
		if (existingWhiteListedDevices.isPresent() && (!existingWhiteListedDevices.get().getId().equals(whiteListedDevicesDTO.getId()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("whiteListedDevices", "nameexists", "WhiteListedDevices already in use"))
					.body(null);
		}
		WhiteListedDevicesDTO result = whiteListedDevicesService.update(whiteListedDevicesDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("whiteListedDevices", "idNotexists", "Invalid WhiteListedDevices ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("whiteListedDevices", whiteListedDevicesDTO.getId().toString())).body(result);
	}
	
	@RequestMapping(value = "/white-listed-devices", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getAllWhiteListedDevices(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of WhiteListedDevicess");
		List<UserDevice>userDevices=userDeviceService.findAll();
		model.addAttribute("userDevices", userDevices);
		model.addAttribute("whiteListedDevices", whiteListedDevicesService.findAll());
		return "site_admin/whiteListedDevices";
	}
	
	@RequestMapping(value = "/white-listed-devices/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public ResponseEntity<Void> deleteWhiteListedDevices(@PathVariable Long id) {
		log.debug("REST request to delete WhiteListedDevices : {}", id);
		whiteListedDevicesService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("whiteListedDevices", id.toString())).build();
	}
	
	@RequestMapping(value = "/white-listed-devices/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public ResponseEntity<WhiteListedDevicesDTO> getWhiteListedDevices(@PathVariable Long id) {
		log.debug("Web request to get White Listed Devices by id : {}", id);
		return new ResponseEntity<WhiteListedDevicesDTO>(whiteListedDevicesService.findOne(id), HttpStatus.OK) ;
	}
	
	@RequestMapping(value = "/white-listed-devices/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public ResponseEntity<WhiteListedDevicesDTO> changeDeviceVerificationNotRequired(@RequestParam Long id,@RequestParam boolean active) {
		log.debug("Web request to change White Listed Devices by id : {}", id);
		return new ResponseEntity<WhiteListedDevicesDTO>(whiteListedDevicesService.changeDeviceVerificationNotRequired(id, active), HttpStatus.OK) ;
	}
}
