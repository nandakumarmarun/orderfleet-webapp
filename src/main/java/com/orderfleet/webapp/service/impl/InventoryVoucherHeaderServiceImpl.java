package com.orderfleet.webapp.service.impl;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.StockDetailsDTO;
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

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	/**
	 * Save a inventoryVoucherHeader.
	 * 
	 * @param inventoryVoucherHeaderDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public InventoryVoucherHeaderDTO save(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {

		return null;
	}

	/**
	 * Get all the inventoryVoucherHeaders.
	 * 
	 * @param pageable
	 *            the pagination information
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
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdOrderByCreatedDateDesc();
		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the inventoryVoucherHeaders.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<InventoryVoucherHeaderDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all InventoryVoucherHeaders");
		Page<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdOrderByCreatedDateDesc(pageable);
		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderList = inventoryVoucherHeaders.getContent().stream()
				.map(InventoryVoucherHeaderDTO::new).collect(Collectors.toList());
		Page<InventoryVoucherHeaderDTO> result = new PageImpl<InventoryVoucherHeaderDTO>(inventoryVoucherHeaderList,
				pageable, inventoryVoucherHeaders.getTotalElements());
		return result;
	}

	/**
	 * Get one inventoryVoucherHeader by id.
	 *
	 * @param id
	 *            the id of the entity
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
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<InventoryVoucherHeaderDTO> findOneByPid(String pid) {
		log.debug("Request to get InventoryVoucherHeader by pid : {}", pid);
		return inventoryVoucherHeaderRepository.findOneByPid(pid).map(InventoryVoucherHeaderDTO::new);
	}

	@Override
	@Transactional(readOnly = true)
	public List<InventoryVoucherHeaderDTO> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents) {
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(fromDate, toDate, documents);
		return inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<InventoryVoucherHeaderDTO> findAllByCompanyIdUserPidAndDateBetween(String userPid,
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents) {
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdUserPidAndDateBetweenOrderByCreatedDateDesc(userPid, fromDate, toDate, documents);
		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<InventoryVoucherHeaderDTO> findAllByCompanyIdAccountPidAndDateBetween(String accountPid,
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents) {
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdAccountPidAndDateBetweenOrderByCreatedDateDesc(accountPid, fromDate, toDate,
						documents);
		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<InventoryVoucherHeaderDTO> findAllByCompanyIdUserPidAccountPidAndDateBetween(String userPid,
			String accountPid, LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents) {
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdUserPidAccountPidAndDateBetweenOrderByCreatedDateDesc(userPid, accountPid, fromDate,
						toDate, documents);
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
			List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdAccountPidAndDateBetweenOrderByCreatedDateDesc(accountProfileDTO.getPid(),
							fromDate, toDate, documents);
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
			List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdUserPidAccountPidAndDateBetweenOrderByCreatedDateDesc(userPid,
							accountProfileDTO.getPid(), fromDate, toDate, documents);
			AllAccountsInventoryVoucherHeaders.addAll(inventoryVoucherHeaders);
		}
		List<InventoryVoucherHeaderDTO> result = AllAccountsInventoryVoucherHeaders.stream()
				.map(InventoryVoucherHeaderDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public void updateInventoryVoucherHeaderStatus(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {
		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findOneByPid(inventoryVoucherHeaderDTO.getPid()).get();
		inventoryVoucherHeader.setStatus(true);
		inventoryVoucherHeader.setTallyDownloadStatus(inventoryVoucherHeaderDTO.getTallyDownloadStatus());
		inventoryVoucherHeaderRepository.save(inventoryVoucherHeader);
	}

	@Override
	public void updateInventoryVoucherHeaderSalesManagementStatus(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {
		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findOneByPid(inventoryVoucherHeaderDTO.getPid()).get();
		inventoryVoucherHeader.setSalesManagementStatus(inventoryVoucherHeaderDTO.getSalesManagementStatus());
		inventoryVoucherHeaderRepository.save(inventoryVoucherHeader);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<InventoryVoucherHeaderDTO> findAllByExecutiveTaskExecutionPid(String executiveTaskExecutionPid) {
		log.debug("Request to get InventoryVoucherHeader by executive task execution Pid : {}",
				executiveTaskExecutionPid);
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByExecutiveTaskExecutionPid(executiveTaskExecutionPid);
		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public Set<Document> findDocumentsByUserIdIn(List<Long> userIds) {
		return inventoryVoucherHeaderRepository.findDocumentsByUserIdIn(userIds);
	}

	@Override
	public List<InventoryVoucherHeaderDTO> findAllByCompanyIdAndDateBetweenAndStatus(String status,
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents) {
		List<InventoryVoucherHeader> inventoryVoucherHeaders = new ArrayList<>();
		if (status.equals("processed")) {
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdAndDateBetweenAndStatusOrderByCreatedDateDesc(fromDate, toDate, documents, true);
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
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdUserPidAndDateBetweenAndStatusOrderByCreatedDateDesc(userPid, fromDate, toDate,
							documents, true);
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
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdAccountPidAndDateBetweenAndStatusOrderByCreatedDateDesc(accountPid, fromDate,
							toDate, documents, true);
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
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdUserPidAccountPidAndDateBetweenAndStatusOrderByCreatedDateDesc(userPid,
							accountPid, fromDate, toDate, documents, true);
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
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByDocumentPidAndDateBetweenOrderByCreatedDateDesc(fromDate, toDate, documentPid);
		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<InventoryVoucherHeaderDTO> findAllByDocumentPidInAndDateBetweenOrderByCreatedDateDesc(
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents) {
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(fromDate, toDate, documents);
		List<InventoryVoucherHeaderDTO> result = inventoryVoucherHeaders.stream().map(InventoryVoucherHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<InventoryVoucherHeaderDTO> findAllByCompanyIdAndInventoryPidIn(List<String> inventoryPids) {
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdAndInventoryPidIn(inventoryPids);
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
		inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderStatusUsingPid(inventoryPids);
	}

	@Override
	public List<Object[]> findByCompanyIdAndInventoryPidIn(List<String> inventoryPids) {
		List<Object[]> inventoryObjectArray = inventoryVoucherHeaderRepository
				.findInventoryVouchersByPidIn(inventoryPids);

		return inventoryObjectArray;

	}

	@Override
	public List<StockDetailsDTO> findAllStockDetails(Long companyId, Long userId, LocalDateTime fromDate,
			LocalDateTime toDate) {

		List<Object[]> inventoryVoucherHeaders = inventoryVoucherHeaderRepository.getAllStockDetails(companyId, userId,
				fromDate, toDate);

		List<StockDetailsDTO> stockDetailsDTOs = new ArrayList<>();
		for (Object[] obj : inventoryVoucherHeaders) {
			StockDetailsDTO stockDetailsDTO = new StockDetailsDTO();

			stockDetailsDTO.setProductName(obj[2] != null ? obj[2].toString() : "");

			double opStock = 0.0;
			double slStock = 0.0;
			if (obj[5] != null) {
				opStock = Double.parseDouble(obj[5].toString());
			}

			if (obj[4] != null) {
				slStock = Double.parseDouble(obj[4].toString());
			}

			stockDetailsDTO.setOpeningStock(opStock);
			stockDetailsDTO.setSaledQuantity(slStock);

			stockDetailsDTOs.add(stockDetailsDTO);
		}

		Map<String, List<StockDetailsDTO>> groupedList = stockDetailsDTOs.stream()
				.collect(Collectors.groupingBy(StockDetailsDTO::getProductName));

		List<String> keySet = new ArrayList<String>(groupedList.keySet());

		List<StockDetailsDTO> proccessedStockDetailsDTOs = new ArrayList<>();

		for (String productName : keySet) {
			List<StockDetailsDTO> valuesList = groupedList.get(productName);

			StockDetailsDTO stockDetailsDTO = new StockDetailsDTO();

			double openingStock = valuesList.get(0).getOpeningStock();
			double saledQuantity = valuesList.stream()
					.collect(Collectors.summingDouble((StockDetailsDTO::getSaledQuantity)));

			double closingStock = openingStock - saledQuantity;

			stockDetailsDTO.setProductName(productName);
			stockDetailsDTO.setOpeningStock(openingStock);
			stockDetailsDTO.setSaledQuantity(saledQuantity);
			stockDetailsDTO.setClosingStock(closingStock);

			proccessedStockDetailsDTOs.add(stockDetailsDTO);

		}

		return proccessedStockDetailsDTOs;
	}

}
