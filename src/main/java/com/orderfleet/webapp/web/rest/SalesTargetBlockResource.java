package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.service.SalesTargetBlockSalesTargetGroupService;
import com.orderfleet.webapp.service.SalesTargetBlockService;
import com.orderfleet.webapp.service.SalesTargetGroupService;
import com.orderfleet.webapp.web.rest.dto.SalesTargetBlockDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing SalesTargetBlock.
 *
 * @author Sarath
 * @since Feb 17, 2017
 */

@Controller
@RequestMapping("/web")
public class SalesTargetBlockResource {

	private final Logger log = LoggerFactory.getLogger(SalesTargetBlockResource.class);

	@Inject
	private SalesTargetBlockService salesTargetBlockService;

	@Inject
	private SalesTargetGroupService salesTargetGroupService;

	@Inject
	private SalesTargetBlockSalesTargetGroupService salesTargetBlockSalesTargetGroupService;

	/**
	 * GET /salesTargetBlocks : get all the salesTargetBlocks.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         salesTargetBlocks in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/salesTargetBlocks", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllSalesTargetBlocks(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of SalesTargetBlocks");
		model.addAttribute("pageSalesTargetBlocks", salesTargetBlockService.findAllByCompany());
		model.addAttribute("targetSettingTypes", Arrays.asList(BestPerformanceType.values()));
		return "company/salesTargetBlocks";
	}

	/**
	 * POST /salesTargetBlocks : Create a new salesTargetBlock.
	 *
	 * @param salesTargetBlockDTO
	 *            the salesTargetBlockDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new salesTargetBlockDTO, or with status 400 (Bad Request) if the
	 *         salesTargetBlock has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/salesTargetBlocks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<SalesTargetBlockDTO> createSalesTargetBlock(
			@Valid @RequestBody SalesTargetBlockDTO salesTargetBlockDTO) throws URISyntaxException {
		log.debug("Web request to save SalesTargetBlock : {}", salesTargetBlockDTO);
		if (salesTargetBlockDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("salesTargetBlock", "idexists",
					"A new salesTargetBlock cannot already have an ID")).body(null);
		}
		if (salesTargetBlockService.findByName(salesTargetBlockDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("salesTargetBlock", "nameexists", "Account type already in use"))
					.body(null);
		}
		SalesTargetBlockDTO result = salesTargetBlockService.save(salesTargetBlockDTO);
		return ResponseEntity.created(new URI("/web/salesTargetBlocks/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("salesTargetBlock", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /salesTargetBlocks : Updates an existing salesTargetBlock.
	 *
	 * @param salesTargetBlockDTO
	 *            the salesTargetBlockDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         salesTargetBlockDTO, or with status 400 (Bad Request) if the
	 *         salesTargetBlockDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the salesTargetBlockDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/salesTargetBlocks", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<SalesTargetBlockDTO> updateSalesTargetBlock(
			@Valid @RequestBody SalesTargetBlockDTO salesTargetBlockDTO) throws URISyntaxException {
		log.debug("Web request to update SalesTargetBlock : {}", salesTargetBlockDTO);
		if (salesTargetBlockDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("salesTargetBlock", "idNotexists", "Account Type must have an ID"))
					.body(null);
		}
		Optional<SalesTargetBlockDTO> existingSalesTargetBlock = salesTargetBlockService
				.findByName(salesTargetBlockDTO.getName());
		if (existingSalesTargetBlock.isPresent()
				&& (!existingSalesTargetBlock.get().getPid().equals(salesTargetBlockDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("salesTargetBlock", "nameexists", "Account type already in use"))
					.body(null);
		}
		SalesTargetBlockDTO result = salesTargetBlockService.update(salesTargetBlockDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("salesTargetBlock", "idNotexists", "Invalid account type ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(
						HeaderUtil.createEntityUpdateAlert("salesTargetBlock", salesTargetBlockDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /salesTargetBlocks/:id : get the "id" salesTargetBlock.
	 *
	 * @param id
	 *            the id of the salesTargetBlockDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         salesTargetBlockDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/salesTargetBlocks/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<SalesTargetBlockDTO> getSalesTargetBlock(@PathVariable String pid) {
		log.debug("Web request to get SalesTargetBlock by pid : {}", pid);
		return salesTargetBlockService.findOneByPid(pid)
				.map(salesTargetBlockDTO -> new ResponseEntity<>(salesTargetBlockDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /salesTargetBlocks/:id : delete the "id" salesTargetBlock.
	 *
	 * @param id
	 *            the id of the salesTargetBlockDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/salesTargetBlocks/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteSalesTargetBlock(@PathVariable String pid) {
		log.debug("REST request to delete SalesTargetBlock : {}", pid);
		salesTargetBlockService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("salesTargetBlock", pid.toString()))
				.build();
	}

	@RequestMapping(value = "/salesTargetBlocks/saveAssignSalesTargetgroups", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedAccountTypes(@RequestParam String pid,
			@RequestParam String assignedSalesTargetgroups) {
		log.debug("REST request to save assigned account type : {}", pid);
		salesTargetBlockSalesTargetGroupService.save(pid, assignedSalesTargetgroups);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/salesTargetBlocks/findSalesTargetGroups/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<SalesTargetGroupDTO>> getSalesTargetGroups(@PathVariable String pid,@RequestParam BestPerformanceType targetSettingType) {
		log.debug("Web request to get SalesTargetGroup by pid : {}", pid);
		
		List<SalesTargetGroupDTO>result=new ArrayList<>();
		List<SalesTargetGroupDTO> allSalesTargetGroups = salesTargetGroupService.findAllByCompanyAndTargetSettingType(targetSettingType);
		List<SalesTargetGroupDTO> salesTargetGroupDTOs = salesTargetBlockSalesTargetGroupService.findSalesTargetGroupsBySalesTargetBlockPid(pid);
		
		allSalesTargetGroups.forEach(allSTG->{
			Optional<SalesTargetGroupDTO> stgDto = salesTargetGroupDTOs.stream().filter(td -> td.getPid().equals(allSTG.getPid())).findAny();
			if (stgDto.isPresent()) {
				allSTG.setAlias("TRUE");
			}
			result.add(allSTG);
		});
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
