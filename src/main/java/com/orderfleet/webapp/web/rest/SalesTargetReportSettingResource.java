package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

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
import com.orderfleet.webapp.domain.SalesTargetReportSettingSalesTargetBlock;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.repository.SalesTargetReportSettingSalesTargetBlockRepository;
import com.orderfleet.webapp.service.SalesTargetBlockService;
import com.orderfleet.webapp.service.SalesTargetReportSettingService;
import com.orderfleet.webapp.web.rest.dto.SalesTargetBlockDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetReportSettingDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetReportSettingSalesTargetBlockDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing SalesTargetReportSetting.
 *
 * @author Sarath
 * @since Feb 17, 2017
 */

@Controller
@RequestMapping("/web")
public class SalesTargetReportSettingResource {

	private final Logger log = LoggerFactory.getLogger(SalesTargetReportSettingResource.class);

	@Inject
	private SalesTargetReportSettingService salesTargetReportSettingService;

	@Inject
	private SalesTargetBlockService salesTargetBlockService;

	@Inject
	private SalesTargetReportSettingSalesTargetBlockRepository salesTargetReportSettingSalesTargetBlockRepository;

	/**
	 * GET /salesTargetReportSettings : get all the salesTargetReportSettings.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         salesTargetReportSettings in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/salesTargetReportSettings", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllSalesTargetReportSettings(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of SalesTargetReportSettings");
		model.addAttribute("pageSalesTargetReportSettings", salesTargetReportSettingService.findAllByCompany());
		model.addAttribute("targetSettingTypes", Arrays.asList(BestPerformanceType.values()));
		return "company/salesTargetReportSetting";
	}

	/**
	 * POST /salesTargetReportSettings : Create a new salesTargetReportSetting.
	 *
	 * @param salesTargetReportSettingDTO
	 *            the salesTargetReportSettingDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         salesTargetReportSettingDTO, or with status 400 (Bad Request) if the
	 *         salesTargetReportSetting has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/salesTargetReportSettings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<SalesTargetReportSettingDTO> createSalesTargetReportSetting(
			@Valid @RequestBody SalesTargetReportSettingDTO salesTargetReportSettingDTO) throws URISyntaxException {
		log.debug("Web request to save SalesTargetReportSetting : {}", salesTargetReportSettingDTO);
		if (salesTargetReportSettingDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("salesTargetReportSetting",
					"idexists", "A new salesTargetReportSetting cannot already have an ID")).body(null);
		}
		if (salesTargetReportSettingService.findByName(salesTargetReportSettingDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("salesTargetReportSetting",
					"nameexists", "Account type already in use")).body(null);
		}
		SalesTargetReportSettingDTO result = salesTargetReportSettingService.save(salesTargetReportSettingDTO);
		return ResponseEntity.created(new URI("/web/salesTargetReportSettings/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("salesTargetReportSetting", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /salesTargetReportSettings : Updates an existing
	 * salesTargetReportSetting.
	 *
	 * @param salesTargetReportSettingDTO
	 *            the salesTargetReportSettingDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         salesTargetReportSettingDTO, or with status 400 (Bad Request) if the
	 *         salesTargetReportSettingDTO is not valid, or with status 500
	 *         (Internal Server Error) if the salesTargetReportSettingDTO couldnt be
	 *         updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/salesTargetReportSettings", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<SalesTargetReportSettingDTO> updateSalesTargetReportSetting(
			@Valid @RequestBody SalesTargetReportSettingDTO salesTargetReportSettingDTO) throws URISyntaxException {
		log.debug("Web request to update SalesTargetReportSetting : {}", salesTargetReportSettingDTO);
		if (salesTargetReportSettingDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("salesTargetReportSetting",
					"idNotexists", "Account Type must have an ID")).body(null);
		}
		Optional<SalesTargetReportSettingDTO> existingSalesTargetReportSetting = salesTargetReportSettingService
				.findByName(salesTargetReportSettingDTO.getName());
		if (existingSalesTargetReportSetting.isPresent()
				&& (!existingSalesTargetReportSetting.get().getPid().equals(salesTargetReportSettingDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("salesTargetReportSetting",
					"nameexists", "Account type already in use")).body(null);
		}
		SalesTargetReportSettingDTO result = salesTargetReportSettingService.update(salesTargetReportSettingDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("salesTargetReportSetting", "idNotexists", "Invalid account type ID"))
					.body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("salesTargetReportSetting",
				salesTargetReportSettingDTO.getPid().toString())).body(result);
	}

	/**
	 * GET /salesTargetReportSettings/:id : get the "id" salesTargetReportSetting.
	 *
	 * @param id
	 *            the id of the salesTargetReportSettingDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         salesTargetReportSettingDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/salesTargetReportSettings/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<SalesTargetReportSettingDTO> getSalesTargetReportSetting(@PathVariable String pid) {
		log.debug("Web request to get SalesTargetReportSetting by pid : {}", pid);
		return salesTargetReportSettingService.findOneByPid(pid)
				.map(salesTargetReportSettingDTO -> new ResponseEntity<>(salesTargetReportSettingDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /salesTargetReportSettings/:id : delete the "id"
	 * salesTargetReportSetting.
	 *
	 * @param id
	 *            the id of the salesTargetReportSettingDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/salesTargetReportSettings/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteSalesTargetReportSetting(@PathVariable String pid) {
		log.debug("REST request to delete SalesTargetReportSetting : {}", pid);
		salesTargetReportSettingService.delete(pid);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("salesTargetReportSetting", pid.toString())).build();
	}

	@Timed
	@RequestMapping(value = "/salesTargetReportSettings/targetBlocks/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SalesTargetBlockDTO>> getTargetBlocks(@PathVariable String pid,
			@RequestParam BestPerformanceType targetSettingType) {
		log.debug("Web request to get get targetBlocks : {}", pid);
		List<SalesTargetReportSettingSalesTargetBlock> list = salesTargetReportSettingSalesTargetBlockRepository
				.findBySalesTargetReportSettingPidOrderBySortOrder(pid);

		List<SalesTargetReportSettingSalesTargetBlockDTO> reportSettingSalesTargetBlockDTOs = list.stream()
				.map(SalesTargetReportSettingSalesTargetBlockDTO::new).collect(Collectors.toList());

		List<SalesTargetBlockDTO> allSalesTargetBlockDTOs = new ArrayList<>();
		if (targetSettingType.equals(BestPerformanceType.SALES)) {
			allSalesTargetBlockDTOs = salesTargetBlockService
					.findAllByCompanyIdAndtargetSettingType(BestPerformanceType.SALES);
		} else if (targetSettingType.equals(BestPerformanceType.RECEIPT)) {
			allSalesTargetBlockDTOs = salesTargetBlockService
					.findAllByCompanyIdAndtargetSettingType(BestPerformanceType.RECEIPT);
		}

		List<SalesTargetBlockDTO> result = new ArrayList<>();
		allSalesTargetBlockDTOs.forEach(allSTB -> {
			Optional<SalesTargetReportSettingSalesTargetBlockDTO> stbDTO = reportSettingSalesTargetBlockDTOs.stream()
					.filter(td -> td.getSalesTargetBlockPid().equals(allSTB.getPid())).findAny();
			if (stbDTO.isPresent()) {
				allSTB.setDescription("TRUE");
				allSTB.setSortOrder(stbDTO.get().getSortOrder());
			}
			result.add(allSTB);
		});
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/salesTargetReportSettings/assignTargetBlocks/{pid}", method = RequestMethod.POST)
	public ResponseEntity<Void> saveAssignedAccountTypes(@PathVariable String pid,
			@RequestBody List<SalesTargetReportSettingSalesTargetBlockDTO> assignedTargetBlocks) {
		log.debug("REST request to save assigned target blocks : {}", pid);
		salesTargetReportSettingService.saveAssignedTargetBlocks(pid, assignedTargetBlocks);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
