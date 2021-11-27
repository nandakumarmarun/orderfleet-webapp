package com.orderfleet.webapp.service.impl;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductNameTextSettings;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.ProductNameTextSettingsRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.web.rest.api.dto.LastSellingDetailsDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.StockDetailsDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductProfileMapper;
import com.orderfleet.webapp.web.vendor.excel.dto.SalesOrderExcelDTO;

/**
 * Service Implementation for managing InventoryVoucherHeader.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Service
@Transactional
public class InventoryVoucherHeaderServiceImpl implements InventoryVoucherHeaderService {

	private final Logger log = LoggerFactory.getLogger(InventoryVoucherHeaderServiceImpl.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private ProductProfileMapper productProfileMapper;

	@Inject
	private ProductNameTextSettingsRepository productNameTextSettingsRepository;

	@Inject
	InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	/**
	 * Save a inventoryVoucherHeader.
	 * 
	 * @param inventoryVoucherHeaderDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public InventoryVoucherHeaderDTO save(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {

		return null;
	}

	/**
	 * Get all the inventoryVoucherHeaders.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<InventoryVoucherHeader> findAll(Pageable pageable) {
		log.debug("Request to get all InventoryVoucherHeaders");
		Page<InventoryVoucherHeader> result = inventoryVoucherHeaderRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the inventoryVoucherHeaders.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<InventoryVoucherHeaderDTO> findAllByCompany() {
		log.debug("Request to get all InventoryVoucherHeaders");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		String id = "INV_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by companyId and order by create date in descending order";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		Long start = System.nanoTime();
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdOrderByCreatedDateDesc();
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
		logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);

		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the inventoryVoucherHeaders.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<InventoryVoucherHeaderDTO> findAllByCompany(Pageable pageable) {
		logger.debug("Request to get all InventoryVoucherHeaders");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		String id = "INV_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by companyId using page";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		Long start = System.nanoTime();
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Page<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdOrderByCreatedDateDesc(pageable);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}

		logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderList = inventoryVoucherHeaders.getContent().stream()
				.map(InventoryVoucherHeaderDTO::new).collect(Collectors.toList());
		Page<InventoryVoucherHeaderDTO> result = new PageImpl<InventoryVoucherHeaderDTO>(inventoryVoucherHeaderList,
				pageable, inventoryVoucherHeaders.getTotalElements());
		return result;
	}

	/**
	 * Get one inventoryVoucherHeader by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public InventoryVoucherHeaderDTO findOne(Long id) {
		log.debug("Request to get InventoryVoucherHeader : {}", id);
		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository.findOne(id);
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO(inventoryVoucherHeader);
		inventoryVoucherHeaderDTO.setInventoryVoucherDetails(inventoryVoucherHeader.getInventoryVoucherDetails()
				.stream().map(InventoryVoucherDetailDTO::new).collect(Collectors.toList()));
		return inventoryVoucherHeaderDTO;
	}

	/**
	 * Get one inventoryVoucherHeader by pid.
	 *
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<InventoryVoucherHeaderDTO> findOneByPid(String pid) {
		log.debug("Request to get InventoryVoucherHeader by pid : {}", pid);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "INV_QUERY_212" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one inv Voucher by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<InventoryVoucherHeaderDTO> ivDTO =inventoryVoucherHeaderRepository.findOneByPid(pid).map(InventoryVoucherHeaderDTO::new);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		return  ivDTO;
	}

	@Override
	@Transactional(readOnly = true)
	public List<InventoryVoucherHeaderDTO> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		String id = "INV_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by companyId and date between";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		Long start = System.nanoTime();
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(fromDate, toDate, documents);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}

		logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		return inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<InventoryVoucherHeaderDTO> findAllByCompanyIdUserPidAndDateBetween(String userPid,
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		String id = "INV_QUERY_106" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by companyIdUserPid and date between";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		Long start = System.nanoTime();
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdUserPidAndDateBetweenOrderByCreatedDateDesc(userPid, fromDate, toDate, documents);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
		logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<InventoryVoucherHeaderDTO> findAllByCompanyIdAccountPidAndDateBetween(String accountPid,
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		String id = "INV_QUERY_107" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by compIdAccountPid and Date Between";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		Long start = System.nanoTime();
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdAccountPidAndDateBetweenOrderByCreatedDateDesc(accountPid, fromDate, toDate,
						documents);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
		logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<InventoryVoucherHeaderDTO> findAllByCompanyIdUserPidAccountPidAndDateBetween(String userPid,
			String accountPid, LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		String id = "INV_QUERY_108" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by compId UserPidAccountPid and date between";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		Long start = System.nanoTime();
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdUserPidAccountPidAndDateBetweenOrderByCreatedDateDesc(userPid, accountPid, fromDate,
						toDate, documents);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
		logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<InventoryVoucherHeaderDTO> findAllByCompanyIdLocationPidAndDateBetween(String locationPid,
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents) {
		List<AccountProfileDTO> allAccounts = locationAccountProfileService
				.findAccountProfileByLocationPid(locationPid);
		List<InventoryVoucherHeader> AllAccountsInventoryVoucherHeaders = new ArrayList<>();
		for (AccountProfileDTO accountProfileDTO : allAccounts) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			String id = "INV_QUERY_107" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get all by compIdAccountPid and Date Between";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			Long start = System.nanoTime();
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdAccountPidAndDateBetweenOrderByCreatedDateDesc(accountProfileDTO.getPid(),
							fromDate, toDate, documents);
			String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
			logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
			AllAccountsInventoryVoucherHeaders.addAll(inventoryVoucherHeaders);
		}
		List<InventoryVoucherHeaderDTO> result = AllAccountsInventoryVoucherHeaders.stream()
				.map(InventoryVoucherHeaderDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<InventoryVoucherHeaderDTO> findAllByCompanyIdUserPidLocationPidAndDateBetween(String userPid,
			String locationPid, LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents) {
		List<AccountProfileDTO> allAccounts = locationAccountProfileService
				.findAccountProfileByLocationPid(locationPid);
		List<InventoryVoucherHeader> AllAccountsInventoryVoucherHeaders = new ArrayList<>();
		for (AccountProfileDTO accountProfileDTO : allAccounts) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			String id = "INV_QUERY_108" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get all by compId UserPidAccountPid and date between";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			Long start = System.nanoTime();
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdUserPidAccountPidAndDateBetweenOrderByCreatedDateDesc(userPid,
							accountProfileDTO.getPid(), fromDate, toDate, documents);
			String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
			logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
			AllAccountsInventoryVoucherHeaders.addAll(inventoryVoucherHeaders);
		}
		List<InventoryVoucherHeaderDTO> result = AllAccountsInventoryVoucherHeaders.stream()
				.map(InventoryVoucherHeaderDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public void updateInventoryVoucherHeaderStatus(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "INV_QUERY_212" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one inv Voucher by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findOneByPid(inventoryVoucherHeaderDTO.getPid()).get();
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

		inventoryVoucherHeader.setStatus(true);
		inventoryVoucherHeader.setTallyDownloadStatus(inventoryVoucherHeaderDTO.getTallyDownloadStatus());
		inventoryVoucherHeader.setSendSalesOrderEmailStatus(inventoryVoucherHeaderDTO.getSendSalesOrderEmailStatus());
		inventoryVoucherHeaderRepository.save(inventoryVoucherHeader);
	}

	@Override
	public void updateInventoryVoucherHeaderSalesManagementStatus(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "INV_QUERY_212" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one inv Voucher by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findOneByPid(inventoryVoucherHeaderDTO.getPid()).get();
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

		inventoryVoucherHeader.setSalesManagementStatus(inventoryVoucherHeaderDTO.getSalesManagementStatus());
		inventoryVoucherHeaderRepository.save(inventoryVoucherHeader);
	}

	@Override
	@Transactional(readOnly = true)
	public List<InventoryVoucherHeaderDTO> findAllByExecutiveTaskExecutionPid(String executiveTaskExecutionPid) {
		log.debug("Request to get InventoryVoucherHeader by executive task execution Pid : {}",
				executiveTaskExecutionPid);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		String id = "INV_QUERY_121" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by executive task execution Pid ";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByExecutiveTaskExecutionPid(executiveTaskExecutionPid);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
		logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public Set<Document> findDocumentsByUserIdIn(List<Long> userIds) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "INV_QUERY_126" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get doc by UserIdIn";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Set<Document> documents = inventoryVoucherHeaderRepository.findDocumentsByUserIdIn(userIds);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
		logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		return documents;
	}

	@Override
	public List<InventoryVoucherHeaderDTO> findAllByCompanyIdAndDateBetweenAndStatus(String status,
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents) {
		List<InventoryVoucherHeader> inventoryVoucherHeaders = new ArrayList<>();
		if (status.equals("processed")) {
			  DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_136" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get all by compId dateBetween and status";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdAndDateBetweenAndStatusOrderByCreatedDateDesc(fromDate, toDate, documents, true);
			  String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);

		} else {
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdAndDateBetweenAndStatusOrderByCreatedDateDesc(fromDate, toDate, documents,
							false);
		}
		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<InventoryVoucherHeaderDTO> findAllByCompanyIdUserPidAndDateBetweenAndStatus(String status,
			String userPid, LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents) {
		List<InventoryVoucherHeader> inventoryVoucherHeaders = new ArrayList<>();
		if (status.equals("processed")) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_137" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get all by compId UserPid dateBetween and status";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdUserPidAndDateBetweenAndStatusOrderByCreatedDateDesc(userPid, fromDate, toDate,
							documents, true);
			   String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);

		} else {
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdUserPidAndDateBetweenAndStatusOrderByCreatedDateDesc(userPid, fromDate, toDate,
							documents, false);
		}
		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<InventoryVoucherHeaderDTO> findAllByCompanyIdAccountPidAndDateBetweenAndStatus(String status,
			String accountPid, LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents) {
		List<InventoryVoucherHeader> inventoryVoucherHeaders = new ArrayList<>();
		if (status.equals("processed")) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_138" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get all by compIdAccPid dateBetween and status";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdAccountPidAndDateBetweenAndStatusOrderByCreatedDateDesc(accountPid, fromDate,
							toDate, documents, true);
			 String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);
		} else {
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdAccountPidAndDateBetweenAndStatusOrderByCreatedDateDesc(accountPid, fromDate,
							toDate, documents, false);
		}
		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<InventoryVoucherHeaderDTO> findAllByCompanyIdUserPidAccountPidAndDateBetweenAndStatus(String status,
			String userPid, String accountPid, LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents) {
		List<InventoryVoucherHeader> inventoryVoucherHeaders = new ArrayList<>();
		if (status.equals("processed")) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_139" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get all by compIdAccPid UserPid dateBetween and status";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdUserPidAccountPidAndDateBetweenAndStatusOrderByCreatedDateDesc(userPid,
							accountPid, fromDate, toDate, documents, true);
			 String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);
		} else {
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdUserPidAccountPidAndDateBetweenAndStatusOrderByCreatedDateDesc(userPid,
							accountPid, fromDate, toDate, documents, false);
		}
		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<InventoryVoucherHeaderDTO> findAllByDocumentPidAndDateBetweenOrderByCreatedDateDesc(
			LocalDateTime fromDate, LocalDateTime toDate, String documentPid) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_141" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get all by DocPid and date between order";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByDocumentPidAndDateBetweenOrderByCreatedDateDesc(fromDate, toDate, documentPid);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<InventoryVoucherHeaderDTO> findAllByDocumentPidInAndDateBetweenOrderByCreatedDateDesc(
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		String id = "INV_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by companyId and date between";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		Long start = System.nanoTime();
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(fromDate, toDate, documents);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
		logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<InventoryVoucherHeaderDTO> findAllByCompanyIdAndInventoryPidIn(List<String> inventoryPids) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_154" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get by CompId and InventoryPidIn";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdAndInventoryPidIn(inventoryPids);
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public void updateProcessStatus(String inventoryVoucherHeaderPid, String status) {
		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findOneByPid(inventoryVoucherHeaderPid).get();
		inventoryVoucherHeader.setProcessStatus(status);
		inventoryVoucherHeaderRepository.save(inventoryVoucherHeader);

	}

	@Override
	public void updateInventoryVoucherHeadersStatus(List<InventoryVoucherHeaderDTO> inventoryVoucherHeaders) {

		List<String> inventoryPids = inventoryVoucherHeaders.stream().map(inv -> inv.getPid())
				.collect(Collectors.toList());
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_155" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "Updating the inv Voucher header status using pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderStatusUsingPid(inventoryPids);

        String flag = "Normal";
LocalDateTime endLCTime = LocalDateTime.now();
String endTime = endLCTime.format(DATE_TIME_FORMAT);
String endDate = startLCTime.format(DATE_FORMAT);
Duration duration = Duration.between(startLCTime, endLCTime);
long minutes = duration.toMinutes();
if (minutes <= 1 && minutes >= 0) {
	flag = "Fast";
}
if (minutes > 1 && minutes <= 2) {
	flag = "Normal";
}
if (minutes > 2 && minutes <= 10) {
	flag = "Slow";
}
if (minutes > 10) {
	flag = "Dead Slow";
}
        logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
		+ description);
	}

	@Override
	public List<Object[]> findByCompanyIdAndInventoryPidIn(List<String> inventoryPids) {
		  DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_176" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get Iv header by PidIn";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Object[]> inventoryObjectArray = inventoryVoucherHeaderRepository
				.findInventoryVouchersByPidIn(inventoryPids);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		return inventoryObjectArray;

	}

	@Override
	public List<StockDetailsDTO> findAllStockDetails(Long companyId, Long userId, LocalDateTime fromDate,
			LocalDateTime toDate, Set<StockLocation> stockLocations) {

		List<Object[]> inventoryVoucherHeaders = new ArrayList<>();

		if (stockLocations != null && stockLocations.size() != 0) {

			Set<Long> stockLocationIds = new HashSet<>();
			stockLocationIds = stockLocations.stream().map(sl -> sl.getId()).collect(Collectors.toSet());
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_179" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="Getting all stock details by stockLocations";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository.getAllStockDetailsByStockLocations(companyId,
					userId, fromDate, toDate, stockLocationIds);
			 String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);

			log.info("in stocklocation based " + inventoryVoucherHeaders.size());
		} else {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_180" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="Get all stock details";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository.getAllStockDetails(companyId, userId, fromDate,
					toDate);
			 String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);

			log.info("in user  based");
		}
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyIdActivatedTrue();
		List<ProductProfileDTO> productProfileDtos = productProfileMapper
				.productProfilesToProductProfileDTOs(productProfiles);
		List<ProductNameTextSettings> productNameTextSettings = productNameTextSettingsRepository
				.findAllByCompanyIdAndEnabledTrue(companyId);

		List<StockDetailsDTO> stockDetailsDTOs = new ArrayList<>();
		for (Object[] obj : inventoryVoucherHeaders) {
			StockDetailsDTO stockDetailsDTO = new StockDetailsDTO();

			stockDetailsDTO.setProductName(obj[2] != null ? obj[2].toString() : "");

			double opStock = 0.0;
			double slStock = 0.0;
			double freeQuantity = 0.0;
			if (obj[5] != null) {
				opStock = Double.parseDouble(obj[5].toString());
//				log.info(obj[3] + " opStock : " + opStock);
			}

			if (obj[4] != null) {
				slStock = Double.parseDouble(obj[4].toString());
			}

			if (obj[10] != null) {
				freeQuantity = Double.parseDouble(obj[10].toString());
			}

			double saledQuantity = slStock + freeQuantity;

			stockDetailsDTO.setOpeningStock(opStock);
			stockDetailsDTO.setSaledQuantity(saledQuantity);

			stockDetailsDTOs.add(stockDetailsDTO);
		}

		Map<String, List<StockDetailsDTO>> groupedList = stockDetailsDTOs.stream()
				.collect(Collectors.groupingBy(StockDetailsDTO::getProductName));

		List<String> keySet = new ArrayList<String>(groupedList.keySet());

		List<StockDetailsDTO> proccessedStockDetailsDTOs = new ArrayList<>();

		log.info("Key set size = " + keySet.size() + "*******");

		for (String productName : keySet) {

			Optional<ProductProfileDTO> opProductProfile = productProfileDtos.stream()
					.filter(ppDto -> ppDto.getName().equalsIgnoreCase(productName)).findAny();

			List<StockDetailsDTO> valuesList = groupedList.get(productName);

			StockDetailsDTO stockDetailsDTO = new StockDetailsDTO();

			double openingStock = valuesList.get(0).getOpeningStock();
			double saledQuantity = valuesList.stream()
					.collect(Collectors.summingDouble((StockDetailsDTO::getSaledQuantity)));

			double closingStock = openingStock - saledQuantity;

			if (opProductProfile.isPresent()) {
				if (productNameTextSettings.size() > 0) {
					ProductProfileDTO productProfileDTO = opProductProfile.get();
					String name = " (";
					for (ProductNameTextSettings productNameText : productNameTextSettings) {
						if (productNameText.getName().equals("DESCRIPTION")) {
							if (productProfileDTO.getDescription() != null
									&& !productProfileDTO.getDescription().isEmpty())
								name += productProfileDTO.getDescription() + ",";
						} else if (productNameText.getName().equals("MRP")) {
							name += productProfileDTO.getMrp() + ",";
						} else if (productNameText.getName().equals("SELLING RATE")) {
							name += productProfileDTO.getPrice() + ",";
						} else if (productNameText.getName().equals("STOCK")) {
							name += openingStock + ",";
						} else if (productNameText.getName().equals("PRODUCT DESCRIPTION")) {
							if (productProfileDTO.getProductDescription() != null
									&& !productProfileDTO.getProductDescription().isEmpty())
								name += productProfileDTO.getProductDescription() + ",";
						} else if (productNameText.getName().equals("BARCODE")) {
							if (productProfileDTO.getBarcode() != null && !productProfileDTO.getBarcode().isEmpty())
								name += productProfileDTO.getBarcode() + ",";
						} else if (productNameText.getName().equals("REMARKS")) {
							if (productProfileDTO.getRemarks() != null && !productProfileDTO.getRemarks().isEmpty())
								name += productProfileDTO.getRemarks() + ",";
						}
					}
					name = name.substring(0, name.length() - 1);
					if (name.length() > 1) {
						name += ")";
					}
					stockDetailsDTO.setProductName(productName + name);

				} else {

					stockDetailsDTO.setProductName(productName);
				}
			} else {
				log.info("Optional Product Profile not present");
				stockDetailsDTO.setProductName(productName);
			}
			stockDetailsDTO.setOpeningStock(openingStock);
			stockDetailsDTO.setSaledQuantity(saledQuantity);
			stockDetailsDTO.setClosingStock(closingStock);

			proccessedStockDetailsDTOs.add(stockDetailsDTO);

		}

		return proccessedStockDetailsDTOs;
	}

	@Override
	public InventoryVoucherHeader updateInventoryVoucherHeader(InventoryVoucherHeaderDTO ivhDto) {
		InventoryVoucherHeader ivh = new InventoryVoucherHeader();
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_212" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one inv Voucher by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		Optional<InventoryVoucherHeader> opIvh = inventoryVoucherHeaderRepository.findOneByPid(ivhDto.getPid());
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
		if (opIvh.isPresent()) {
			ivh = opIvh.get();
			ivh.setDocumentTotalUpdated(ivhDto.getDocumentTotalUpdated());
			ivh.setDocumentVolumeUpdated(ivhDto.getDocumentVolumeUpdated());
			ivh.setUpdatedStatus(ivhDto.getUpdatedStatus());
			ivh = inventoryVoucherHeaderRepository.save(ivh);
		}

		return ivh;
	}

	@Override
	public void updateInventoryVoucherHeaderProcessFlowStatus(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_212" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one inv Voucher by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findOneByPid(inventoryVoucherHeaderDTO.getPid()).get();
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();

		inventoryVoucherHeader.setProcessFlowStatus(inventoryVoucherHeaderDTO.getProcessFlowStatus());
		inventoryVoucherHeader.setUpdatedDate(LocalDateTime.now());
		inventoryVoucherHeader.setUpdatedBy(user);
		inventoryVoucherHeaderRepository.save(inventoryVoucherHeader);
	}

	@Override
	public void updateInventoryVoucherHeaderPaymentReceived(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "INV_QUERY_212" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one inv Voucher by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findOneByPid(inventoryVoucherHeaderDTO.getPid()).get();
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
		inventoryVoucherHeader.setPaymentReceived(inventoryVoucherHeaderDTO.getPaymentReceived());
		inventoryVoucherHeaderRepository.save(inventoryVoucherHeader);
	}

	@Override
	public void updateInventoryVoucherHeaderBookingId(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {
		  DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_212" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one inv Voucher by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findOneByPid(inventoryVoucherHeaderDTO.getPid()).get();
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
		inventoryVoucherHeader.setBookingId(
				inventoryVoucherHeaderDTO.getBookingId() != null ? inventoryVoucherHeaderDTO.getBookingId() : "");
		inventoryVoucherHeaderRepository.save(inventoryVoucherHeader);
	}

	@Override
	public void updateInventoryVoucherHeaderRemarks(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {
		  DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_212" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one inv Voucher by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findOneByPid(inventoryVoucherHeaderDTO.getPid()).get();
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
		inventoryVoucherHeader.setRemarks(
				inventoryVoucherHeaderDTO.getRemarks() != null ? inventoryVoucherHeaderDTO.getRemarks() : "");
		inventoryVoucherHeaderRepository.save(inventoryVoucherHeader);
	}

	@Override
	public void updateInventoryVoucherHeaderRejectedStatus(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_212" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one inv Voucher by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findOneByPid(inventoryVoucherHeaderDTO.getPid()).get();
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
		inventoryVoucherHeader.setRejectedStatus(inventoryVoucherHeaderDTO.getRejectedStatus());
		inventoryVoucherHeaderRepository.save(inventoryVoucherHeader);
	}

	@Override
	public void updateInventoryVoucherHeaderDeliveryDate(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_212" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one inv Voucher by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findOneByPid(inventoryVoucherHeaderDTO.getPid()).get();
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
		inventoryVoucherHeader.setDeliveryDate(inventoryVoucherHeaderDTO.getDeliveryDate());
		inventoryVoucherHeaderRepository.save(inventoryVoucherHeader);
	}

	@Override
	public void updateInventoryVoucherHeaderBookingIdDeliveryDateAndPaymentReceived(
			InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {

		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findOneByPid(inventoryVoucherHeaderDTO.getPid()).get();

		inventoryVoucherHeader.setBookingId(
				inventoryVoucherHeaderDTO.getBookingId() != null ? inventoryVoucherHeaderDTO.getBookingId() : "");
		if (inventoryVoucherHeaderDTO.getDeliveryDate() != null) {
			inventoryVoucherHeader.setDeliveryDate(inventoryVoucherHeaderDTO.getDeliveryDate());
		}
		inventoryVoucherHeader.setPaymentReceived(inventoryVoucherHeaderDTO.getPaymentReceived());

		inventoryVoucherHeaderRepository.save(inventoryVoucherHeader);
	}

	@Override
	public List<LastSellingDetailsDTO> findHeaderByAccountPidUserPidandDocPid(String accountPid, String userPid,
			String documentPid, String productPid) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		String id = "INV_QUERY_213" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by userpid,compnay pid,document pid and accountPid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		Long start = System.nanoTime();
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Object[]> inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findAllByCompanyIdUserPidAccountPidAndDocumentPid(userPid, accountPid, documentPid);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
		logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		Set<String> ivhpids = new HashSet<>();

		for (Object[] obj : inventoryVoucherHeader) {
			ivhpids.add(obj[1] != null ? obj[1].toString() : " ");
		}
		if (ivhpids.size() == 0) {
			return null;
		}

		List<InventoryVoucherHeader> ivHeader = new ArrayList<>();
		List<InventoryVoucherDetail> ivhDetail = inventoryVoucherDetailRepository
				.findAllByInventoryVoucherHeaderPidIn(ivhpids);
		if (ivhDetail.size() == 0) {
			return null;
		}

		for (InventoryVoucherDetail voucherDetail : ivhDetail) {

			if (voucherDetail.getProduct().getPid().equals(productPid)) {

				ivHeader.add(voucherDetail.getInventoryVoucherHeader());
			}
		}
		if (ivHeader.isEmpty()) {
			return null;
		}

		List<InventoryVoucherHeader> sortedList = ivHeader.stream()
				.sorted(Comparator.comparing(InventoryVoucherHeader::getDocumentDate).reversed())
				.collect(Collectors.toList());
		if (sortedList.size() == 0) {
			return null;
		}

		List<InventoryVoucherHeader> lastProductInventoryVoucherHeader = new ArrayList<>();
		int i;
		for (i = 0; i <= 4; i++) {
			lastProductInventoryVoucherHeader.add(sortedList.get(i));
		}

		List<InventoryVoucherDetail> ivDetails = new ArrayList<>();
		for (InventoryVoucherHeader invHeader : lastProductInventoryVoucherHeader) {
			ivDetails.addAll(invHeader.getInventoryVoucherDetails());
		}

		List<LastSellingDetailsDTO> lsdDTO = new ArrayList<>();
		for (InventoryVoucherDetail ivds : ivDetails) {

			LastSellingDetailsDTO lastSellingDetailsDTO = new LastSellingDetailsDTO();
			if (ivds.getProduct().getPid().equals(productPid)) {
				lastSellingDetailsDTO.setQuantity(ivds.getQuantity());
				lastSellingDetailsDTO.setSellingRate(ivds.getSellingRate());
				lastSellingDetailsDTO.setTotalValue(ivds.getRowTotal());
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm a");
				String DateAndTime = ivds.getInventoryVoucherHeader().getDocumentDate().format(formatter);
				lastSellingDetailsDTO.setOrderDate(DateAndTime);
				lsdDTO.add(lastSellingDetailsDTO);
			}
		}
		return lsdDTO;
	}

}
