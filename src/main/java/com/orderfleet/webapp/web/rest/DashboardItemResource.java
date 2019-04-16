package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.SalesTargetBlock;
import com.orderfleet.webapp.domain.enums.DashboardItemConfigType;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.SalesTargetBlockSalesTargetGroupRepository;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.DashboardItemService;
import com.orderfleet.webapp.service.DashboardItemUserService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.SalesTargetGroupService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardItemDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetBlockDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing DashboardItem.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
@Controller
@RequestMapping("/web")
public class DashboardItemResource {

	private final Logger log = LoggerFactory.getLogger(DashboardItemResource.class);

	@Inject
	private DashboardItemService dashboardItemService;

	@Inject
	private ActivityService activityService;

	@Inject
	private DocumentService documentService;

	@Inject
	private UserService userService;

	@Inject
	private DashboardItemUserService dashboardItemUserService;
	
	@Inject
	private ProductGroupService productGroupService;
	
	@Inject
	private SalesTargetGroupService salesTargetGroupService;
	
	@Inject
	private SalesTargetBlockSalesTargetGroupRepository salesTargetBlockSalesTargetGroupRepository;

	/**
	 * GET /dashboardItems : get all the dashboardItems.
	 */
	@RequestMapping(value = "/dashboardItems", method = RequestMethod.GET)
	public String getAllDashboardItems(Model model) {
		model.addAttribute("users", userService.findAllByCompany());
		model.addAttribute("activities", activityService.findAllByCompany());
		model.addAttribute("dashboardItems", dashboardItemService.findAllByCompany());
		model.addAttribute("dashboardItemConfigTypes", Arrays.asList(DashboardItemConfigType.values()));
		model.addAttribute("productGroups", productGroupService.findAllByCompanyOrderByName());
		model.addAttribute("targetGroups", salesTargetGroupService.findAllByCompany());
		model.addAttribute("documents", documentService.findAllByDocumentType(DocumentType.INVENTORY_VOUCHER));
		return "company/dashboardItems";
	}

	/**
	 * POST /dashboardItems : Create a new dashboardItem.
	 *
	 * @param dashboardItemDTO
	 *            the dashboardItemDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new dashboardItemDTO, or with status 400 (Bad Request) if the
	 *         dashboardItem has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@Timed
	@RequestMapping(value = "/dashboardItems", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DashboardItemDTO> createDashboardItem(@Valid @RequestBody DashboardItemDTO dashboardItemDTO)
			throws URISyntaxException {
		log.debug("Web request to save DashboardItem : {}", dashboardItemDTO);
		if (dashboardItemDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dashboardItem", "idexists",
					"A new dashboardItem cannot already have an ID")).body(null);
		}
		if (dashboardItemService.findByName(dashboardItemDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("dashboardItem", "nameexists", "Dashboard Item already in use"))
					.body(null);
		}
		DashboardItemDTO result = dashboardItemService.save(dashboardItemDTO);
		return ResponseEntity.created(new URI("/web/dashboardItems/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("dashboardItem", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /dashboardItems : Updates an existing dashboardItem.
	 *
	 * @param dashboardItemDTO
	 *            the dashboardItemDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         dashboardItemDTO, or with status 400 (Bad Request) if the
	 *         dashboardItemDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the dashboardItemDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/dashboardItems", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DashboardItemDTO> updateDashboardItem(@Valid @RequestBody DashboardItemDTO dashboardItemDTO) {
		log.debug("Web request to update DashboardItem : {}", dashboardItemDTO);
		if (dashboardItemDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("dashboardItem", "idNotexists", "Dashboard Item must have an ID"))
					.body(null);
		}
		Optional<DashboardItemDTO> existingDashboardItem = dashboardItemService.findByName(dashboardItemDTO.getName());
		if (existingDashboardItem.isPresent()
				&& (!existingDashboardItem.get().getPid().equals(dashboardItemDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("dashboardItem", "nameexists", "Dashboard Item already in use"))
					.body(null);
		}
		DashboardItemDTO result = dashboardItemService.update(dashboardItemDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("dashboardItem", "idNotexists", "Invalid Dashboard Item ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("dashboardItem", dashboardItemDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /dashboardItems/:id : get the "id" dashboardItem.
	 *
	 * @param id
	 *            the id of the dashboardItemDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         dashboardItemDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/dashboardItems/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DashboardItemDTO> getDashboardItem(@PathVariable String pid) {
		log.debug("Web request to get DashboardItem by pid : {}", pid);
		return dashboardItemService.findOneByPid(pid)
				.map(dashboardItemDTO -> new ResponseEntity<>(dashboardItemDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@Timed
	@RequestMapping(value = "/dashboardItems/documents/{documentType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DocumentDTO>> getDocuments(@PathVariable DocumentType documentType) {
		log.debug("Web request to get documents by documentType : {}", documentType);
		return new ResponseEntity<>(documentService.findAllByDocumentType(documentType), HttpStatus.OK);

	}
	
	@Timed
	@RequestMapping(value = "/dashboardItems/targetGroup/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SalesTargetBlockDTO>> getTargetBlocks(@PathVariable String pid) {
		List<SalesTargetBlock> salesTargetBlocks = salesTargetBlockSalesTargetGroupRepository.findSalesTargetBlockBySalesTargetGroupPid(pid);
		List<SalesTargetBlockDTO> salesTargetBlockDtos = salesTargetBlocks.stream().map(SalesTargetBlockDTO::new).collect(Collectors.toList());
		return new ResponseEntity<>(salesTargetBlockDtos, HttpStatus.OK);
	}

	/**
	 * DELETE /dashboardItems/:id : delete the "id" dashboardItem.
	 *
	 * @param id
	 *            the id of the dashboardItemDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/dashboardItems/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteDashboardItem(@PathVariable String pid) {
		log.debug("REST request to delete DashboardItem : {}", pid);
		dashboardItemService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("dashboardItem", pid.toString()))
				.build();
	}

	@RequestMapping(value = "/dashboardItems/users/{pid}", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<List<UserDTO>> getAssignedUsers(@PathVariable String pid) {
		log.debug("REST request to assign users : {}", pid);
		List<UserDTO> userDTOs = dashboardItemUserService.findUserByDashboardItemPid(pid);
		return new ResponseEntity<>(userDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/dashboardItems/assign-users", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedPriceLevels(@RequestParam String pid, @RequestParam String assignedUsers) {
		log.debug("REST request to save assigned users : {}", pid);
		dashboardItemUserService.saveAssignedUsers(pid, assignedUsers);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
