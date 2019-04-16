package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

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
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InwardOutwardStockLocationRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.web.rest.dto.StockSummaryReportDTO;
import com.orderfleet.webapp.web.rest.dto.StockSummaryReportDTO.StockLocationSummary;

/**
 * Web controller for Stock summary report.
 * 
 * @author Shaheer
 * @since February 22, 2016
 */
@Controller
@RequestMapping("/web")
public class StockSummaryReportResource {

	private final Logger log = LoggerFactory.getLogger(StockSummaryReportResource.class);

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private InwardOutwardStockLocationRepository inwardOutwardStockLocationRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@Inject
	private StockLocationRepository stockLocationRepository;

	/**
	 * GET /stock-summary-report : get stockSummary.
	 *
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@Transactional
	@RequestMapping(value = "/stock-summary-report", method = RequestMethod.GET)
	public String getStockSummaryReport(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of stock summary Report");
		List<StockLocation> companyStockLocations = stockLocationRepository.findAllByCompanyId();
		model.addAttribute("stockLocations", companyStockLocations);
		return "company/stock-summary-report";
	}

	@Timed
	@RequestMapping(value = "/stock-summary-report/load", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<StockSummaryReportDTO>> getStockSummaryReport(
			@RequestParam(required = false, value = "stockLocationPids") String stockLocationPids) {
		log.debug("get stock summary Report");
		List<StockSummaryReportDTO> stockSummaryReportDTOs = new ArrayList<>();
		List<StockLocation> companyStockLocations = new ArrayList<>();

		if (stockLocationPids == null || stockLocationPids == "") {
			companyStockLocations = stockLocationRepository.findAllByCompanyId();
		} else {
			companyStockLocations = stockLocationRepository
					.findAllByStockLocationPidIn(Arrays.asList(stockLocationPids.split(",")));
		}
		List<StockLocation> stockLocations = inwardOutwardStockLocationRepository.findAllByCompanyId();
		if (!stockLocations.isEmpty()) {
			List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId();
			for (ProductProfile productProfile : productProfiles) {
				OpeningStock openingStock = openingStockRepository
						.findTop1ByProductProfilePidOrderByLastModifiedDateDesc(productProfile.getPid());
				LocalDateTime modifiedDate = openingStock == null ? LocalDateTime.now()
						: openingStock.getLastModifiedDate();

				Double inwardQty = inventoryVoucherDetailRepository
						.getClosingStockBySourceStockLocation(productProfile.getPid(), stockLocations, modifiedDate);
				Double outwardQty = inventoryVoucherDetailRepository.getClosingStockByDestinationStockLocation(
						productProfile.getPid(), stockLocations, modifiedDate);
				Double inHand = inwardQty - outwardQty;
				StockSummaryReportDTO ssrDto = new StockSummaryReportDTO(productProfile.getPid(),
						productProfile.getName(), inHand.doubleValue());
				List<InventoryVoucherDetail> sourceInventoryVoucherDetails = inventoryVoucherDetailRepository
						.findByProductPidAndSourceStockLocationNotIn(productProfile.getPid(), stockLocations,
								modifiedDate);
				Map<StockLocation, Double> sourceMap = sourceInventoryVoucherDetails.stream()
						.collect(Collectors.groupingBy(InventoryVoucherDetail::getSourceStockLocation,
								Collectors.summingDouble(InventoryVoucherDetail::getQuantity)));
				List<InventoryVoucherDetail> destinationInventoryVoucherDetails = inventoryVoucherDetailRepository
						.findByProductPidAndDestinationStockLocationNotIn(productProfile.getPid(), stockLocations,
								modifiedDate);
				Map<StockLocation, Double> destinationMap = destinationInventoryVoucherDetails.stream()
						.collect(Collectors.groupingBy(InventoryVoucherDetail::getDestinationStockLocation,
								Collectors.summingDouble(InventoryVoucherDetail::getQuantity)));

				// find all opening stock of a product
				List<StockLocationSummary> stockLocationSummarys = new ArrayList<>();
				List<OpeningStock> openingStocks = openingStockRepository
						.findByCompanyIdAndProductProfilePid(productProfile.getPid());
				Map<StockLocation, Double> productOpeningStock = openingStocks.stream().collect(Collectors.groupingBy(
						OpeningStock::getStockLocation, Collectors.summingDouble(OpeningStock::getQuantity)));

				if (stockLocationPids == null || stockLocationPids == "") {
					productOpeningStock.forEach((k, v) -> {
						// Add opening stock in inward-outward stock location
						ssrDto.setQuantity(ssrDto.getQuantity() + v);
						stockLocationSummarys.add(ssrDto.new StockLocationSummary(k.getPid(), k.getName(), v));
					});
				} else {
					List<String> companyStockLocationPids = Arrays.asList(stockLocationPids.split(","));
					productOpeningStock.forEach((k, v) -> {
						for (String companyStockLocationPid : companyStockLocationPids) {
							if (k.getPid().equals(companyStockLocationPid)) {
								// Add opening stock in inward-outward stock location
								ssrDto.setQuantity(ssrDto.getQuantity() + v);
								stockLocationSummarys.add(ssrDto.new StockLocationSummary(k.getPid(), k.getName(), v));
							}
						}
					});
				}
				for (StockLocation sl : companyStockLocations) {
					Double sourceQty = 0d;
					Double destinationQty = 0d;
					if (sourceMap.get(sl) != null) {
						sourceQty = sourceMap.get(sl);
					}
					if (destinationMap.get(sl) != null) {
						destinationQty = destinationMap.get(sl);
					}
					Double value = destinationQty - sourceQty;
					// if already added, then update qty, else add
					Optional<StockLocationSummary> optionalSls = stockLocationSummarys.stream()
							.filter(sls -> sls.getStockLocationPid().equals(sl.getPid())).findAny();
					if (optionalSls.isPresent()) {
						optionalSls.get().setQuantity(optionalSls.get().getQuantity() + value);
					} else {
						stockLocationSummarys.add(ssrDto.new StockLocationSummary(sl.getPid(), sl.getName(), value));
					}
				}
				if (stockLocationPids != "") {
					List<String> stockLocationList = Arrays.asList(stockLocationPids.split(","));
					double totalValue = 0d;
					for (int i = 0; i < stockLocationList.size(); i++) {
						totalValue = totalValue + stockLocationSummarys.get(i).getQuantity();
					}
					ssrDto.setQuantity(totalValue);
				}
				ssrDto.setStockLocationSummarys(stockLocationSummarys);
				stockSummaryReportDTOs.add(ssrDto);
			}
		}
		return new ResponseEntity<>(stockSummaryReportDTOs, HttpStatus.OK);
	}

}
