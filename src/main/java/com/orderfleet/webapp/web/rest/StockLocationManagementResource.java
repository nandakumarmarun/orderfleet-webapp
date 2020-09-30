package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.TemporaryOpeningStock;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.TemporaryOpeningStockRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.OpeningStockService;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.StockLocationManagementDTO;

/**
 * Web controller for managing AccountType.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */

@Controller
@RequestMapping("/web")
public class StockLocationManagementResource {

	private final Logger log = LoggerFactory.getLogger(StockLocationManagementResource.class);

	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@Inject
	private TemporaryOpeningStockRepository temporaryOpeningStockRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private StockLocationService stockLocationService;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserRepository userRepository;

	@RequestMapping(value = "/stock-location-management", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getStockLocationManagement(Model model) throws URISyntaxException {

		return "company/stockLocationManagement";
	}

	@RequestMapping(value = "/stock-location-management/loadStockLocationDetails", method = RequestMethod.GET)
	public ResponseEntity<List<StockLocationManagementDTO>> loadStockLocationDetails() {
		log.debug("Web request to get stock location details");

		List<StockLocationManagementDTO> stockLocationManagementDTOs = new ArrayList<>();

		List<StockLocation> stockLocations = stockLocationRepository
				.findAllByCompanyIdAndDeactivatedStockLocation(true);

		List<OpeningStock> openingStocks = openingStockRepository.findAllByCompanyIdAndDeactivatedOpeningStock(true);

		List<TemporaryOpeningStock> temporaryOpeningStocks = temporaryOpeningStockRepository
				.findAllByCompanyIdAndDeactivatedTemporaryOpeningStock(true);

		List<EmployeeProfile> employeeProfiles = employeeProfileRepository.findAllByCompanyIdAndUser(true);

		if (stockLocations.size() > 0) {

			for (StockLocation stockLocation : stockLocations) {

				StockLocationManagementDTO stockLocationManagementDTO = new StockLocationManagementDTO();

				stockLocationManagementDTO.setStockLocationName(stockLocation.getName());
				stockLocationManagementDTO.setStockLocationPid(stockLocation.getPid());

				if (temporaryOpeningStocks.size() > 0) {
					Optional<TemporaryOpeningStock> tempOsOptional = temporaryOpeningStocks.stream()
							.filter(tOs -> tOs.getStockLocation().getPid().equalsIgnoreCase(stockLocation.getPid()))
							.findAny();
					if (tempOsOptional.isPresent()) {
						stockLocationManagementDTO
								.setTemporaryStockLocationDate(tempOsOptional.get().getOpeningStockDate());
					} else {
						stockLocationManagementDTO.setTemporaryStockLocationDate(null);
					}
				} else {
					stockLocationManagementDTO.setTemporaryStockLocationDate(null);
				}

				if (openingStocks.size() > 0) {
					Optional<OpeningStock> osOptional = openingStocks.stream()
							.filter(os -> os.getStockLocation().getPid().equalsIgnoreCase(stockLocation.getPid()))
							.findAny();

					if (osOptional.isPresent()) {
						stockLocationManagementDTO.setLiveStockLocationDate(osOptional.get().getOpeningStockDate());

						if (osOptional.get().getUser() != null) {
							Optional<EmployeeProfile> osEmployee = employeeProfiles.stream().filter(
									emp -> emp.getUser().getPid().equalsIgnoreCase(osOptional.get().getUser().getPid()))
									.findAny();
							if (osEmployee.isPresent()) {
								stockLocationManagementDTO.setUserName(osEmployee.get().getName());
							} else {
								stockLocationManagementDTO.setUserName("-");
							}
						} else {
							stockLocationManagementDTO.setUserName("-");
						}
					} else {
						stockLocationManagementDTO.setLiveStockLocationDate(null);
						stockLocationManagementDTO.setUserName("-");
					}
				} else {
					stockLocationManagementDTO.setLiveStockLocationDate(null);
					stockLocationManagementDTO.setUserName("-");
				}
				stockLocationManagementDTOs.add(stockLocationManagementDTO);
			}

			return new ResponseEntity<>(stockLocationManagementDTOs, HttpStatus.OK);

		}

		return null;
	}

	@RequestMapping(value = "/stock-location-management/temporaryStockLocation/{stockLoctionPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<OpeningStockDTO>> getTemporaryOpeningStock(@PathVariable String stockLoctionPid) {
		log.debug("Web request to get all temporary opening Stocks");

		Optional<StockLocation> opStockLocation = stockLocationRepository.findOneByPid(stockLoctionPid);

		StockLocation stockLocation = new StockLocation();

		if (opStockLocation.isPresent()) {
			stockLocation = opStockLocation.get();
		}

		List<TemporaryOpeningStock> temporaryOpeningStocks = temporaryOpeningStockRepository
				.findTemporaryOpeningStocksAndStockLocationId(stockLocation.getId());

		List<OpeningStockDTO> openingStocks = convertTemporaryOpeningStockToOpeningStockDto(temporaryOpeningStocks);

		return new ResponseEntity<>(openingStocks, HttpStatus.OK);

	}

	@RequestMapping(value = "/stock-location-management/liveStockLocation/{stockLoctionPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<OpeningStockDTO>> getLiveOpeningStock(@PathVariable String stockLoctionPid) {
		log.debug("Web request to get all live opening Stocks");

		Optional<StockLocation> opStockLocation = stockLocationRepository.findOneByPid(stockLoctionPid);

		StockLocation stockLocation = new StockLocation();

		if (opStockLocation.isPresent()) {
			stockLocation = opStockLocation.get();
		}

		List<OpeningStock> liveOpeningStocks = openingStockRepository
				.findOpeningStocksAndStockLocationId(stockLocation.getId());

		List<OpeningStockDTO> openingStocks = convertLiveOpeningStockToOpeningStockDto(liveOpeningStocks);

		return new ResponseEntity<>(openingStocks, HttpStatus.OK);

	}

	@RequestMapping(value = "/stock-location-management/updateStocks/{selectedStockLocation}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<OpeningStockDTO>> updateLiveOpeningStock(@PathVariable String selectedStockLocation) {
		log.debug("Web request to update temporary opening stocks to live opening Stocks");

		List<String> stockLocationPids = new ArrayList<>();

		String[] selectStockLocations = selectedStockLocation.split(",");

		for (String str : selectStockLocations) {
			stockLocationPids.add(str);
		}

		List<TemporaryOpeningStock> temporaryOpeningStocks = temporaryOpeningStockRepository
				.findTemporaryOpeningStocksAndStockLocationPIdIn(stockLocationPids);

		List<OpeningStockDTO> openingStockDtos = convertTemporaryOpeningStockToOpeningStockDto(temporaryOpeningStocks);

		saveOpeningStocks(openingStockDtos, stockLocationPids);

		return new ResponseEntity<>(openingStockDtos, HttpStatus.OK);

	}

	private void saveOpeningStocks(List<OpeningStockDTO> openingStockDTOs, List<String> stockLocationPids) {
		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();

		Company company = companyRepository.findOne(companyId);
		String userLogin = SecurityUtils.getCurrentUserLogin();
		User user = userRepository.findByCompanyIdAndLogin(companyId, userLogin);
		Set<OpeningStock> saveOpeningStocks = new HashSet<>();
		// All opening-stock must have a stock-location, if not, set a default
		// one
		StockLocation defaultStockLocation = stockLocationRepository.findFirstByCompanyId(companyId);
		// find all exist product profiles
		Set<String> ppNames = openingStockDTOs.stream().map(os -> os.getProductProfileName())
				.collect(Collectors.toSet());

		List<StockLocation> StockLocations = stockLocationService.findAllStockLocationByCompanyId(companyId);

		List<ProductProfile> productProfiles = productProfileRepository
				.findByCompanyIdAndNameIgnoreCaseIn(company.getId(), ppNames);
		// delete all opening stock
		openingStockRepository.deleteByCompanyIdAndStockLocationPidIn(company.getId(), stockLocationPids);
		for (OpeningStockDTO osDto : openingStockDTOs) {
			// only save if account profile exist
			productProfiles.stream().filter(pp -> pp.getName().equals(osDto.getProductProfileName())).findAny()
					.ifPresent(pp -> {
						OpeningStock openingStock = new OpeningStock();
						openingStock.setPid(OpeningStockService.PID_PREFIX + RandomUtil.generatePid()); // set
						openingStock.setOpeningStockDate(LocalDateTime.now());
						openingStock.setCreatedDate(LocalDateTime.now());
						openingStock.setCompany(company);
						openingStock.setProductProfile(pp);
						openingStock.setUser(user);

						if (osDto.getStockLocationName() == null) {
							openingStock.setStockLocation(defaultStockLocation);
						} else {
							// stock location
							Optional<StockLocation> optionalStockLocation = StockLocations.stream()
									.filter(pl -> osDto.getStockLocationName().equals(pl.getName())).findAny();
							if (optionalStockLocation.isPresent()) {
								openingStock.setStockLocation(optionalStockLocation.get());
							} else {
								openingStock.setStockLocation(defaultStockLocation);
							}
						}
						openingStock.setBatchNumber(osDto.getBatchNumber());
						openingStock.setQuantity(osDto.getQuantity());
						saveOpeningStocks.add(openingStock);
					});
		}
		bulkOperationRepositoryCustom.bulkSaveOpeningStocks(saveOpeningStocks);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);

	}

	private List<OpeningStockDTO> convertTemporaryOpeningStockToOpeningStockDto(
			List<TemporaryOpeningStock> temporaryOpeningStocks) {

		List<OpeningStockDTO> openingStockDTOs = new ArrayList<>();
		for (TemporaryOpeningStock openingStock : temporaryOpeningStocks) {
			OpeningStockDTO openingStockDTO = new OpeningStockDTO();

			openingStockDTO.setProductProfilePid(openingStock.getProductProfile().getPid());
			openingStockDTO.setStockLocationName(openingStock.getStockLocation().getName());
			openingStockDTO.setStockLocationPid(openingStock.getStockLocation().getPid());
			openingStockDTO.setProductProfileName(openingStock.getProductProfile().getName());
			openingStockDTO.setActivated(openingStock.getActivated());
			openingStockDTO.setPid(openingStock.getPid());
			openingStockDTO.setBatchNumber(openingStock.getBatchNumber());
			openingStockDTO.setQuantity(openingStock.getQuantity());
			openingStockDTO.setCreatedDate(openingStock.getCreatedDate());
			openingStockDTO.setOpeningStockDate(openingStock.getOpeningStockDate());
			openingStockDTO.setLastModifiedDate(openingStock.getLastModifiedDate());

			openingStockDTOs.add(openingStockDTO);
		}

		return openingStockDTOs;
	}

	private List<OpeningStockDTO> convertLiveOpeningStockToOpeningStockDto(List<OpeningStock> liveOpeningStocks) {

		List<OpeningStockDTO> openingStockDTOs = new ArrayList<>();
		for (OpeningStock openingStock : liveOpeningStocks) {
			OpeningStockDTO openingStockDTO = new OpeningStockDTO();

			openingStockDTO.setProductProfilePid(openingStock.getProductProfile().getPid());
			openingStockDTO.setStockLocationName(openingStock.getStockLocation().getName());
			openingStockDTO.setStockLocationPid(openingStock.getStockLocation().getPid());
			openingStockDTO.setProductProfileName(openingStock.getProductProfile().getName());
			openingStockDTO.setActivated(openingStock.getActivated());
			openingStockDTO.setPid(openingStock.getPid());
			openingStockDTO.setBatchNumber(openingStock.getBatchNumber());
			openingStockDTO.setQuantity(openingStock.getQuantity());
			openingStockDTO.setCreatedDate(openingStock.getCreatedDate());
			openingStockDTO.setOpeningStockDate(openingStock.getOpeningStockDate());
			openingStockDTO.setLastModifiedDate(openingStock.getLastModifiedDate());

			openingStockDTOs.add(openingStockDTO);
		}

		return openingStockDTOs;
	}

}
