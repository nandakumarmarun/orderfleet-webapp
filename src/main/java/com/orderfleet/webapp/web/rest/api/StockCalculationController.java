package com.orderfleet.webapp.web.rest.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.DocumentStockCalculation;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.enums.StockLocationType;
import com.orderfleet.webapp.repository.DocumentStockCalculationRepository;
import com.orderfleet.webapp.repository.DocumentStockLocationSourceRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.web.rest.api.dto.LiveStockDTO;
import com.orderfleet.webapp.web.rest.api.dto.StockLocationWiseStockDTO;

/**
 * REST controller for managing StockCalculation.
 * 
 * @author Muhammed Riyas T
 * @since Sep 30, 2016
 */
@RestController
@RequestMapping("/api")
public class StockCalculationController {

	private final DocumentStockLocationSourceRepository documentStockLocationSourceRepository;

	private final OpeningStockRepository openingStockRepository;

	private final InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	private final DocumentStockCalculationRepository documentStockCalculationRepository;
	
	private final UserStockLocationRepository userStockLocationRepository;

	public StockCalculationController(DocumentStockLocationSourceRepository documentStockLocationSourceRepository,
			OpeningStockRepository openingStockRepository,
			InventoryVoucherDetailRepository inventoryVoucherDetailRepository,
			DocumentStockCalculationRepository documentStockCalculationRepository,
			UserStockLocationRepository userStockLocationRepository) {
		super();
		this.documentStockLocationSourceRepository = documentStockLocationSourceRepository;
		this.openingStockRepository = openingStockRepository;
		this.inventoryVoucherDetailRepository = inventoryVoucherDetailRepository;
		this.documentStockCalculationRepository = documentStockCalculationRepository;
		this.userStockLocationRepository = userStockLocationRepository;
	}

	/**
	 * GET /stock : get opening stock.
	 * 
	 * @return
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of opening
	 *         stock in body
	 */
	@RequestMapping(value = "/stock", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Double> getStock(@RequestParam String documentPid, @RequestParam String productPid) {
		//user stock location
		List<StockLocation> userStockLocations = userStockLocationRepository.findStockLocationsByUserIsCurrentUser();
		if (userStockLocations.isEmpty()) {
			return ResponseEntity.ok().body(0.0);
		}
		List<StockLocation> stockLocations = documentStockLocationSourceRepository
				.findStockLocationByDocumentPidAndStockLocationIn(documentPid, userStockLocations);
		if (stockLocations.isEmpty()) {
			return ResponseEntity.ok().body(0.0);
		}
		// Group actual and logical stock locations
		Map<StockLocationType, List<StockLocation>> groupByLocationTypeMap = stockLocations.stream().parallel()
				.collect(Collectors.groupingBy(StockLocation::getStockLocationType));
		List<StockLocation> actualStockLocations = groupByLocationTypeMap.get(StockLocationType.ACTUAL);
		List<StockLocation> logicalStockLocations = groupByLocationTypeMap.get(StockLocationType.LOGICAL);
		// calculate stock
		Double stock = calculateStock(documentPid, productPid, stockLocations, actualStockLocations,
				logicalStockLocations);
		return ResponseEntity.ok().body(stock);
	}
	
	/**
	 * GET /stock : get opening stock.
	 * 
	 * @return
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of opening
	 *         stock in body
	 */
	@RequestMapping(value = "/ecom-stock", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Double> getEcomStock(@RequestParam String productPid) {
		//user opening stock for ecom
		List<OpeningStock> openingStocks = openingStockRepository.findByCompanyIdAndProductProfilePid(productPid);
		
		if (openingStocks.isEmpty()) {
			return ResponseEntity.ok().body(0.0);
		}
		Double stock = openingStocks.stream().mapToDouble(op -> op.getQuantity()).sum();
		return ResponseEntity.ok().body(stock);
	}
	
	
	/**
	 * GET /stock-list : get opening stock.
	 * 
	 * @return
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of opening
	 *         stock in body
	 */
	@RequestMapping(value = "/stock-list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<LiveStockDTO>> getStockList(@RequestParam String documentPid, @RequestParam List<String> productPids) {
		//user stock location
//		System.out.println("In /stock-list");
		List<StockLocation> userStockLocations = userStockLocationRepository.findStockLocationsByUserIsCurrentUser();
		if (userStockLocations.isEmpty()) {
			System.out.println("User Stock Location Empty");
			return ResponseEntity.ok().body(Collections.emptyList());
		}
		List<StockLocation> stockLocations = documentStockLocationSourceRepository
				.findStockLocationByDocumentPidAndStockLocationIn(documentPid, userStockLocations);
		if (stockLocations.isEmpty()) {
			System.out.println("Stock Location Empty");
			return ResponseEntity.ok().body(Collections.emptyList());
		}
		// Group actual and logical stock locations
		Map<StockLocationType, List<StockLocation>> groupByLocationTypeMap = stockLocations.stream().parallel()
				.collect(Collectors.groupingBy(StockLocation::getStockLocationType));
		List<StockLocation> actualStockLocations = groupByLocationTypeMap.get(StockLocationType.ACTUAL);
		List<StockLocation> logicalStockLocations = groupByLocationTypeMap.get(StockLocationType.LOGICAL);
		// calculate stock
		List<LiveStockDTO> liveStockList = calculateStockList(documentPid, productPids, stockLocations, actualStockLocations,
				logicalStockLocations);
		return ResponseEntity.ok().body(liveStockList);
	}
	
	/**
	 * GET /stock-location-stock : get location wise stock.
	 * 
	 * @return
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of opening
	 *         stock in body
	 */
	@RequestMapping(value = "/stock-location-stock", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<StockLocationWiseStockDTO>> getLocationsStock(@RequestParam String documentPid, @RequestParam String productPid) {
		//user stock location
		List<StockLocation> userStockLocations = userStockLocationRepository.findStockLocationsByUserIsCurrentUser();
		List<StockLocation> stockLocations = documentStockLocationSourceRepository
				.findStockLocationByDocumentPidAndStockLocationIn(documentPid, userStockLocations);
		if (stockLocations.isEmpty()) {
			return ResponseEntity.ok().body(null);
		}
		// Group actual and logical stock locations
		Map<StockLocationType, List<StockLocation>> groupByLocationTypeMap = stockLocations.stream().parallel()
				.collect(Collectors.groupingBy(StockLocation::getStockLocationType));
		List<StockLocation> actualStockLocations = groupByLocationTypeMap.get(StockLocationType.ACTUAL);
		// calculate location stock wise
		List<StockLocationWiseStockDTO> stockLocationWiseStockList = new ArrayList<>();
		for (StockLocation actualSL : actualStockLocations) {
			StockLocationWiseStockDTO stwsDto = new StockLocationWiseStockDTO();
			stwsDto.setName(actualSL.getName());
			stwsDto.setPid(actualSL.getPid());
			stwsDto.setStock(openingStockRepository.findOpeningStockByProductAndStockLocations(productPid,
					Arrays.asList(actualSL)));
			stockLocationWiseStockList.add(stwsDto);
		}
		return ResponseEntity.ok().body(stockLocationWiseStockList);
	}

	private Double calculateStock(String documentPid, String productPid, List<StockLocation> stockLocations,
			List<StockLocation> actualStockLocations, List<StockLocation> logicalStockLocations) {
		Double stock = 0.0;
		Optional<DocumentStockCalculation> optionalStockCalculation = documentStockCalculationRepository
				.findOneByDocumentPid(documentPid);
		if (optionalStockCalculation.isPresent()) {
			DocumentStockCalculation sc = optionalStockCalculation.get();
			Double openingStock = openingStockRepository.findOpeningStockByProductAndStockLocations(productPid,
					actualStockLocations);
			LocalDateTime from = openingStockRepository.findMaxDateByProductAndStockLocations(productPid,
					actualStockLocations);
			if(from == null){
				from = LocalDate.now().atTime(0, 0);
			}
			LocalDateTime currentDate = LocalDate.now().atTime(23, 59);
			if (sc.getOpening()) {
				stock = openingStock;
			} else if (sc.getClosingActual() && sc.getClosingLogical()) {
				stock = calculateClosingStock(openingStock, productPid, stockLocations, from, currentDate);
			} else if (!sc.getClosingActual() && sc.getClosingLogical()) {
				stock = calculateClosingStockLogical(openingStock, productPid, logicalStockLocations, from, currentDate);
			} else if (sc.getClosingActual() && !sc.getClosingLogical()) {
				stock = calculateClosingStockActual(openingStock, productPid, actualStockLocations, from, currentDate);
			}
		}
		return stock;

	}
	
	private List<LiveStockDTO> calculateStockList(String documentPid, List<String> productPids, List<StockLocation> stockLocations,
			List<StockLocation> actualStockLocations, List<StockLocation> logicalStockLocations) {
		Double stock = 0.0;
		List<LiveStockDTO> liveStockDtos = new ArrayList<>();
		
		Optional<DocumentStockCalculation> optionalStockCalculation = documentStockCalculationRepository
				.findOneByDocumentPid(documentPid);
		if (optionalStockCalculation.isPresent()) {
//			System.out.println("optional Stock calculation present");
			DocumentStockCalculation sc = optionalStockCalculation.get();
			
			Set<Long> stockLocationIds = stockLocations.stream().map(StockLocation::getId).collect(Collectors.toSet());
//			System.out.println("Size of stocklocationIds : "+stockLocationIds.size());
			stockLocationIds.forEach(System.out::println);
			List<OpeningStock> openingStocks = openingStockRepository.findOpeningStocksAndStockLocationIdIn(stockLocationIds);
			
			Map<String, List<OpeningStock>> productOpeningStockMap = 
					openingStocks.stream().collect(Collectors.groupingBy(op -> op.getProductProfile().getPid()));
//			System.out.println(productOpeningStockMap.size());
			for(String pid : productPids) {
				LiveStockDTO liveStock = new LiveStockDTO();
				List<OpeningStock> openingStockList = productOpeningStockMap.get(pid);
//				System.out.println("1 *******************"+openingStockList);
				if(openingStockList == null) {
//					System.out.println("2 *******************");
					continue;
				}
				Optional<OpeningStock> opOpeningStockDate = openingStockList.stream().max(Comparator.comparing(OpeningStock::getCreatedDate));
//				System.out.println("3 *******************");
				LocalDateTime from = null;
				if(opOpeningStockDate.isPresent()) {
					from = opOpeningStockDate.get().getCreatedDate();
				}else {
					from =  LocalDate.now().atTime(0, 0);
				}
//				System.out.println("4 *******************");
				Double openingStock = openingStockList.stream().collect(Collectors.summingDouble(OpeningStock::getQuantity));
//				System.out.println("5 *******************");
				LocalDateTime currentDate = LocalDate.now().atTime(23, 59);
				if (sc.getOpening()) {
					stock = openingStock;
				} else if (sc.getClosingActual() && sc.getClosingLogical()) {
					stock = calculateClosingStock(openingStock, pid, stockLocations, from, currentDate);
				} else if (!sc.getClosingActual() && sc.getClosingLogical()) {
					stock = calculateClosingStockLogical(openingStock, pid, logicalStockLocations, from, currentDate);
				} else if (sc.getClosingActual() && !sc.getClosingLogical()) {
					stock = calculateClosingStockActual(openingStock, pid, actualStockLocations, from, currentDate);
				}
//				System.out.println("6 *******************");
				liveStock.setProductPid(pid);
				liveStock.setStock(stock);
				liveStockDtos.add(liveStock);
			}

		}
		return liveStockDtos;

	}

	
	private Double calculateClosingStock(Double openingStock, String productPid, List<StockLocation> stockLocations, LocalDateTime from, LocalDateTime to) {
		Double stockLocationSourceSum = inventoryVoucherDetailRepository.getClosingStockBySourceStockLocationAndCreatedDateBetween(productPid,
				stockLocations,from,to);
		Double stockLocationDestinationSum = inventoryVoucherDetailRepository
				.getClosingStockByDestinationStockLocationAndCreatedDateBetween(productPid, stockLocations,from, to);
		return (openingStock - stockLocationSourceSum) + stockLocationDestinationSum;
	}

	private Double calculateClosingStockLogical(Double openingStock, String productPid,
			List<StockLocation> logicalStockLocations, LocalDateTime from, LocalDateTime to) {
		Double slSourceLogicalSum = inventoryVoucherDetailRepository.getClosingStockBySourceStockLocationAndCreatedDateBetween(productPid,
				logicalStockLocations,from, to);
		Double slDestinationLogicalSum = inventoryVoucherDetailRepository
				.getClosingStockByDestinationStockLocationAndCreatedDateBetween(productPid, logicalStockLocations,from , to);
		return (openingStock - slSourceLogicalSum) + slDestinationLogicalSum;
	}

	private Double calculateClosingStockActual(Double openingStock, String productPid,
			List<StockLocation> actualStockLocations, LocalDateTime from, LocalDateTime to) {
		Double slSourceActualSum = inventoryVoucherDetailRepository.getClosingStockBySourceStockLocationAndCreatedDateBetween(productPid,
				actualStockLocations, from, to);
		Double slDestinationActualSum = inventoryVoucherDetailRepository
				.getClosingStockByDestinationStockLocationAndCreatedDateBetween(productPid, actualStockLocations, from, to);
		return (openingStock - slSourceActualSum) + slDestinationActualSum;
	}

}
