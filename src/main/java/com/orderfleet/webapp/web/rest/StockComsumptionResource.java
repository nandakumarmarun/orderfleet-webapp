package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.web.rest.dto.StockConsumptionDTO;

/**
 * Web controller for Stock consumption report.
 * 
 * @author Shaheer
 * @since September 24, 2016
 */
@Controller
@RequestMapping("/web")
public class StockComsumptionResource {

	private final Logger log = LoggerFactory.getLogger(StockComsumptionResource.class);

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	/**
	 * GET /stock-consumption-report : get stockConsumption.
	 *
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/stock-consumption-report", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getStockConsumptionReport(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of stockConsumption Report");
		List<StockLocation> stockLocations = stockLocationRepository.findAllByCompanyId();
		model.addAttribute("stockLocations", stockLocations);
		model.addAttribute("selectedLocationPid", "-1");
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId();
		List<StockConsumptionDTO> stockConsumptions = new ArrayList<>();

		for (StockLocation stockLocation : stockLocations) {
			for (ProductProfile productProfile : productProfiles) {
				StockConsumptionDTO stockConsumptionDTO = new StockConsumptionDTO();
				stockConsumptionDTO.setProductName(productProfile.getName());
				stockConsumptionDTO.setProductPid(productProfile.getPid());
				stockConsumptionDTO.setProductId(productProfile.getId());
				stockConsumptionDTO.setOpeningStock(0);
				stockConsumptionDTO.setStockLocationId(stockLocation.getId());
				stockConsumptionDTO.setStockLocationName(stockLocation.getName());
				stockConsumptions.add(stockConsumptionDTO);
			}
		}

		List<OpeningStock> openingStocks = openingStockRepository.findByProductProfileIn(productProfiles);
		for (OpeningStock openingStock : openingStocks) {
			for (StockConsumptionDTO stockConsumptionDTO : stockConsumptions) {
				if (stockConsumptionDTO.getProductId() == openingStock.getProductProfile().getId()
						&& stockConsumptionDTO.getStockLocationId() == openingStock.getStockLocation().getId()) {
					stockConsumptionDTO.setOpeningStock(openingStock.getQuantity());
				}
			}
		}
		stockConsumptions.sort((sc1, sc2) -> sc1.getProductName().compareToIgnoreCase(sc2.getProductName()));
		for (StockConsumptionDTO sc : stockConsumptions) {
			if (sc.getStockLocationId() != null && sc.getStockLocationId() > 0) {
				Double inwardQty = inventoryVoucherDetailRepository
						.getClosingStockByDestinationStockLocationId(sc.getProductPid(), sc.getStockLocationId());
				Double outwardQty = inventoryVoucherDetailRepository
						.getClosingStockBySourceStockLocationId(sc.getProductPid(), sc.getStockLocationId());

				Double inHand = (sc.getOpeningStock() + inwardQty) - outwardQty;
				if (inwardQty != null) {
					sc.setIn(inwardQty);
					sc.setOut(outwardQty);
					sc.setClosingStock(inHand);
				}
			}
		}

		model.addAttribute("stockConsumptions", stockConsumptions);
		return "company/stockConsumptions";
	}

	@RequestMapping(value = "/stock-consumption-report/{locationPid}", method = RequestMethod.GET)
	@Timed
	public String getStockConsumptionReportByLocation(@PathVariable() String locationPid, Model model) {
		log.debug("Web request to get a page of stockConsumption Report with stock location id {}", locationPid);
		List<StockLocation> stockLocations = stockLocationRepository.findAllByCompanyId();
		model.addAttribute("stockLocations", stockLocations);
		model.addAttribute("selectedLocationPid", locationPid);
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId();
		List<StockConsumptionDTO> stockConsumptions = new ArrayList<>();

		StockLocation sc3 = stockLocationRepository.findOneByPid(locationPid).get();
		if (sc3.getPid() != null) {
			for (ProductProfile productProfile : productProfiles) {
				StockConsumptionDTO stockConsumptionDTO = new StockConsumptionDTO();
				stockConsumptionDTO.setProductName(productProfile.getName());
				stockConsumptionDTO.setProductPid(productProfile.getPid());
				stockConsumptionDTO.setProductId(productProfile.getId());
				stockConsumptionDTO.setOpeningStock(0);
				stockConsumptionDTO.setStockLocationId(sc3.getId());
				stockConsumptionDTO.setStockLocationName(sc3.getName());
				stockConsumptions.add(stockConsumptionDTO);
			}
		}

		List<OpeningStock> openingStocks = openingStockRepository.findByStockLocation(sc3);
		for (OpeningStock openingStock : openingStocks) {
			if (locationPid.equals(openingStock.getStockLocation().getPid())) {
				for (StockConsumptionDTO stockConsumptionDTO : stockConsumptions) {
					if (stockConsumptionDTO.getProductPid().equals(openingStock.getProductProfile().getPid())
							&& stockConsumptionDTO.getStockLocationId().equals(openingStock.getStockLocation().getId())) {
						stockConsumptionDTO.setOpeningStock(openingStock.getQuantity());
					}
				}
			}
		}
		stockConsumptions.sort((sc1, sc2) -> sc1.getProductName().compareToIgnoreCase(sc2.getProductName()));
		List<StockConsumptionDTO> newStockConsumptions = new ArrayList<>();
		for (StockConsumptionDTO sc : stockConsumptions) {
			if (sc.getStockLocationId() != null && sc.getStockLocationId() > 0) {
				Double inwardQty = inventoryVoucherDetailRepository
						.getClosingStockByDestinationStockLocationId(sc.getProductPid(), sc.getStockLocationId());
				Double outwardQty = inventoryVoucherDetailRepository
						.getClosingStockBySourceStockLocationId(sc.getProductPid(), sc.getStockLocationId());

				Double inHand = (sc.getOpeningStock() + inwardQty) - outwardQty;
				if (inwardQty != null) {
					sc.setIn(inwardQty);
					sc.setOut(outwardQty);
					sc.setClosingStock(inHand);
				}
				newStockConsumptions.add(sc);
			}
		}
		model.addAttribute("stockConsumptions", newStockConsumptions);
		return "company/stockConsumptions";
	}

	/*
	 * @RequestMapping(value = "/stock-consumption-report/{pid}", method =
	 * RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	 * 
	 * @Timed public List<StockConsumptionDTO> getAccountType(@PathVariable
	 * String pid) {
	 * log.debug("Web request to get stockConsumption by stocklocation pid : {}"
	 * , pid); List<ProductProfile> productProfiles =
	 * productProfileRepository.findAllByCompanyId(); List<StockConsumptionDTO>
	 * stockConsumptions = new ArrayList<>();
	 * stockLocationRepository.findOneByPid(pid).ifPresent(sl -> {
	 * List<Object[]> datas = inventoryVoucherHeaderRepository.
	 * getSumofProductProfileStockLocationNative(sl.getId()); datas.forEach(d ->
	 * { System.out.println("SL" + d[0]); System.out.println("SL" + d[1]);
	 * System.out.println("SL" + d[2]); }); });
	 * productProfiles.stream().parallel().forEach(p -> { StockConsumptionDTO
	 * stockConsumptionDTO = new StockConsumptionDTO();
	 * stockConsumptionDTO.setProductName(p.getName());
	 * stockConsumptionDTO.setProductId(p.getId());
	 * stockConsumptions.add(stockConsumptionDTO); });
	 * 
	 * stockConsumptions.forEach(sc -> {
	 * openingStockRepository.findByProductProfileIdOrderByCreatedDateDesc(sc.
	 * getProductId()).ifPresent(o -> { sc.setOpeningStock(o.getQuantity());
	 * sc.setStockLocationId(o.getStockLocation().getId());
	 * sc.setStockLocationName(o.getStockLocation().getName()); }); });
	 * stockConsumptions.forEach(sc -> { if (sc.getStockLocationId() != null &&
	 * sc.getStockLocationId() > 0) { Integer totalQty =
	 * inventoryVoucherHeaderRepository.
	 * getSumofProductProfileIdAndStockLocationId(sc.getProductId(),
	 * sc.getStockLocationId()); if (totalQty != null) {
	 * sc.setConsumption(totalQty); } } }); return stockConsumptions; }
	 */

}
