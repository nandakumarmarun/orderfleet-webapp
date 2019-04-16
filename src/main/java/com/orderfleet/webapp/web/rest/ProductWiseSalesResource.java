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
import org.springframework.data.domain.Pageable;
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
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.service.ProductProfileService;

/**
 * Web controller for managing Product Wise Sales .
 * 
 * @author Muhammed Riyas T
 * @since October 14, 2016
 */
@Controller
@RequestMapping("/web")
public class ProductWiseSalesResource {

	private final Logger log = LoggerFactory.getLogger(ProductWiseSalesResource.class);

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	/**
	 * GET /product-wise-sales : get product wise sales.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK)
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/product-wise-sales", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllInventoryVouchers(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of inventory vouchers");
		model.addAttribute("products", productProfileService.findAllByCompany());
		return "company/productWiseSales";
	}

	@RequestMapping(value = "/product-wise-sales/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Object[]>> filterProductWiseSales(@RequestParam("productPid") String productPid,
			@RequestParam("filterBy") String filterBy,
			@RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to filter accounting vouchers");
		List<Object[]> inventoryVouchers = new ArrayList<Object[]>();
		if (filterBy.equals("TODAY")) {
			inventoryVouchers = getFilterData(productPid, LocalDate.now(), LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			inventoryVouchers = getFilterData(productPid, yeasterday, yeasterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			inventoryVouchers = getFilterData(productPid, weekStartDate, LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			inventoryVouchers = getFilterData(productPid, monthStartDate, LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); 
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toDateTime = LocalDate.parse(toDate, formatter);
			inventoryVouchers = getFilterData(productPid, fromDateTime, toDateTime);
		}
		return new ResponseEntity<>(inventoryVouchers, HttpStatus.OK);
	}

	private List<Object[]> getFilterData(String productPid, LocalDate fDate, LocalDate tDate) {
		List<Document> documents = primarySecondaryDocumentRepository.findDocumentsByVoucherTypeAndCompanyId(VoucherType.PRIMARY_SALES_ORDER);

		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<Object[]> inventoryVouchers = inventoryVoucherDetailRepository.findByProductPidAndDateBetweenAndDocumentIn(productPid,
				fromDate, toDate, documents);
		return inventoryVouchers;
	}

}
