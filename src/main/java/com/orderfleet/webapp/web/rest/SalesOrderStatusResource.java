package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.PrimarySecondaryDocumentService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;

@Controller
@RequestMapping("/web")
public class SalesOrderStatusResource {

	private final Logger log = LoggerFactory.getLogger(SalesOrderStatusResource.class);
	
	@Inject
	private PrimarySecondaryDocumentService primarySecondaryDocumentService;
	
	@Inject
	private InventoryVoucherHeaderService inventoryVoucherHeaderService;
	
	@Inject
	private DocumentRepository documentRepository;
	
	@RequestMapping(value = "/sales-order-status", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllItemWiseSales(Model model) throws URISyntaxException {
		log.debug("Web request to get All ItemWiseSales");
		List<DocumentDTO> documentDTOs = primarySecondaryDocumentService.findAllDocumentsByCompanyIdAndVoucherType(VoucherType.PRIMARY_SALES_ORDER);
		model.addAttribute("documents", documentDTOs);
		return "company/salesOrderStatus";
	}
	
	@RequestMapping(value = "/sales-order-status/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<InventoryVoucherHeaderDTO>> filterInventoryVoucherHeaders(@RequestParam("documentPid") String documentPid,
			@RequestParam("filterBy") String filterBy,
			@RequestParam String fromDate,
			@RequestParam String toDate) {
		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs=new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			inventoryVoucherHeaderDTOs = getFilterData(documentPid, LocalDate.now(), LocalDate.now());
		} 
		else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			inventoryVoucherHeaderDTOs = getFilterData(documentPid, yeasterday, yeasterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			inventoryVoucherHeaderDTOs = getFilterData(documentPid, weekStartDate, LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			inventoryVoucherHeaderDTOs = getFilterData(documentPid, monthStartDate, LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); 
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toDateTime = LocalDate.parse(toDate, formatter);
			inventoryVoucherHeaderDTOs = getFilterData(documentPid, fromDateTime, toDateTime);
		}
		return new ResponseEntity<>(inventoryVoucherHeaderDTOs, HttpStatus.OK);
	}
	
	private List<InventoryVoucherHeaderDTO> getFilterData(String documentPid, LocalDate fDate,
			LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
	
		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs = new ArrayList<InventoryVoucherHeaderDTO>();
		if(documentPid.equals("no")){
			List<DocumentDTO> documentDTOs = primarySecondaryDocumentService.findAllDocumentsByCompanyIdAndVoucherType(VoucherType.PRIMARY_SALES_ORDER);
			List<String> documentPids=new ArrayList<>();
			for(DocumentDTO documentDTO:documentDTOs){
				documentPids.add(documentDTO.getPid());
			}
			List<Document>documents=documentRepository.findOneByPidIn(documentPids);
			inventoryVoucherHeaderDTOs=inventoryVoucherHeaderService.findAllByDocumentPidInAndDateBetweenOrderByCreatedDateDesc(fromDate, toDate, documents);
		}else{
			inventoryVoucherHeaderDTOs=inventoryVoucherHeaderService.findAllByDocumentPidAndDateBetweenOrderByCreatedDateDesc(fromDate, toDate, documentPid);
			
		}
		return inventoryVoucherHeaderDTOs;
	}
	
	/**
	 * GET /sales-order-status/:id : get the "id" InventoryVoucher.
	 *
	 * @param id
	 *            the id of the InventoryVoucherDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         InventoryVoucherDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/sales-order-status/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> getInventoryVoucher(@PathVariable String pid) {
		log.debug("Web request to get inventoryVoucherDTO by pid : {}", pid);
		return inventoryVoucherHeaderService.findOneByPid(pid)
				.map(inventoryVoucherDTO -> new ResponseEntity<>(inventoryVoucherDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@RequestMapping(value = "/sales-order-status/updateProcessStatus", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<Void> updateProcessStatus(@RequestParam String salesOrderPids,@RequestParam String status) {
		String [] salesOrderStatus=salesOrderPids.split(",");
		for(String salesOrderPid:salesOrderStatus){
			inventoryVoucherHeaderService.updateProcessStatus(salesOrderPid, status);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
