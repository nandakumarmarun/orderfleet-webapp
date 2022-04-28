package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.projections.CustomInventoryVoucherDetail;
import com.orderfleet.webapp.service.InventoryVoucherDetailService;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;

@Service
@Transactional
public class InventoryVoucherDetailServiceImpl implements InventoryVoucherDetailService {

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Override
	@Transactional(readOnly = true)
	public List<InventoryVoucherDetailDTO> findAllByCompanyIdAndDocumentPidAndDateBetween(String sort, String order,
			LocalDateTime fromDate, LocalDateTime toDate, String documentPid, String employeePid) {
		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = new ArrayList<>();
		List<String> employeePids = new ArrayList<>();

		if (employeePid != null) {
			if (employeePid.equals("Dashboard Employee") || employeePid.equals("no")) {
				// checking all employees
				employeePids.addAll(employeeProfileRepository.findEmployeePidsByCompanyId());
			} else {
				// checking single employee
				employeePids.add(employeePid);
			}
		}

		List<Object[]> objects = inventoryVoucherDetailRepository
				.findAllByCompanyIdAndDocumentPidAndDateBetween(fromDate, toDate, documentPid, employeePids);

		for (Object[] object : objects) {
			InventoryVoucherDetailDTO ivd = new InventoryVoucherDetailDTO();
			ivd.setCreatedDate((LocalDateTime) object[0]);
			ivd.setEmployeeName((String) object[1]);
			ivd.setProductCategory((String) object[2]);
			ivd.setProductName((String) object[3]);
			ivd.setQuantity((double) object[4]);
			ivd.setSellingRate((double) object[5]);
			ivd.setRowTotal((double) object[6]);
			ivd.setProductPid((String) object[7]);
			inventoryVoucherDetailDTOs.add(ivd);
		}

		if (sort.equals("date")) {
			if (order.equals("desc")) {
				inventoryVoucherDetailDTOs
						.sort(Comparator.comparing(InventoryVoucherDetailDTO::getCreatedDate).reversed());
			} else {
				inventoryVoucherDetailDTOs.sort(Comparator.comparing(InventoryVoucherDetailDTO::getCreatedDate));
			}
		} else if (sort.equals("item")) {
			if (order.equals("desc")) {
				inventoryVoucherDetailDTOs
						.sort(Comparator.comparing(InventoryVoucherDetailDTO::getProductName).reversed());
			} else {
				inventoryVoucherDetailDTOs.sort(Comparator.comparing(InventoryVoucherDetailDTO::getProductName));
			}
		} else if (sort.equals("quantity")) {
			if (order.equals("desc")) {
				inventoryVoucherDetailDTOs
						.sort(Comparator.comparing(InventoryVoucherDetailDTO::getQuantity).reversed());
			} else {
				inventoryVoucherDetailDTOs.sort(Comparator.comparing(InventoryVoucherDetailDTO::getQuantity));
			}
		} else if (sort.equals("category")) {
			if (order.equals("desc")) {
				inventoryVoucherDetailDTOs
						.sort(Comparator.comparing(InventoryVoucherDetailDTO::getProductCategory).reversed());
			} else {
				inventoryVoucherDetailDTOs.sort(Comparator.comparing(InventoryVoucherDetailDTO::getProductCategory));
			}
		}
		return inventoryVoucherDetailDTOs;
	}

	@Override
	public List<InventoryVoucherDetailDTO> findAllByCompanyIdAndDateBetweenOrderBy(String sort, String order,
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents, String employeePid) {
		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = new ArrayList<>();
		List<String> employeePids = new ArrayList<>();

		if (employeePid != null) {
			if (employeePid.equals("Dashboard Employee") || employeePid.equals("no")) {
				// checking all employees
				employeePids.addAll(employeeProfileRepository.findEmployeePidsByCompanyId());
			} else {
				// checking single employee
				employeePids.add(employeePid);
			}
		}

		List<CustomInventoryVoucherDetail> customInventorys = inventoryVoucherDetailRepository
				.findAllByCompanyIdAndDateBetween(fromDate, toDate, documents, employeePids);

		for (CustomInventoryVoucherDetail customInventory : customInventorys) {
			InventoryVoucherDetailDTO ivd = new InventoryVoucherDetailDTO();
			ivd.setCreatedDate(customInventory.getCreatedDateTime());
			ivd.setEmployeeName(customInventory.getEmployeeName());
			ivd.setProductCategory(customInventory.getProductCategoryName());
			ivd.setProductName(customInventory.getProductName());
			ivd.setQuantity(customInventory.getQuantity());
			ivd.setSellingRate(customInventory.getSellingRate());
			ivd.setRowTotal(customInventory.getRowTotal());
			ivd.setProductPid(customInventory.getProductPid());
			inventoryVoucherDetailDTOs.add(ivd);
		}

		if (sort.equals("date")) {
			if (order.equals("desc")) {
				inventoryVoucherDetailDTOs
						.sort(Comparator.comparing(InventoryVoucherDetailDTO::getCreatedDate).reversed());
			} else {
				inventoryVoucherDetailDTOs.sort(Comparator.comparing(InventoryVoucherDetailDTO::getCreatedDate));
			}
		} else if (sort.equals("item")) {
			if (order.equals("desc")) {
				inventoryVoucherDetailDTOs
						.sort(Comparator.comparing(InventoryVoucherDetailDTO::getProductName).reversed());
			} else {
				inventoryVoucherDetailDTOs.sort(Comparator.comparing(InventoryVoucherDetailDTO::getProductName));
			}
		} else if (sort.equals("quantity")) {
			if (order.equals("desc")) {
				inventoryVoucherDetailDTOs
						.sort(Comparator.comparing(InventoryVoucherDetailDTO::getQuantity).reversed());
			} else {
				inventoryVoucherDetailDTOs.sort(Comparator.comparing(InventoryVoucherDetailDTO::getQuantity));
			}
		} else if (sort.equals("category")) {
			if (order.equals("desc")) {
				inventoryVoucherDetailDTOs
						.sort(Comparator.comparing(InventoryVoucherDetailDTO::getProductCategory).reversed());
			} else {
				inventoryVoucherDetailDTOs.sort(Comparator.comparing(InventoryVoucherDetailDTO::getProductCategory));
			}
		}

		return inventoryVoucherDetailDTOs;
	}

	@Override
	public InventoryVoucherDetail updateInventoryVoucherDetail(InventoryVoucherDetailDTO ivdDto) {
		InventoryVoucherDetail ivd = new InventoryVoucherDetail();
		ivd = inventoryVoucherDetailRepository.findOneById(ivdDto.getDetailId());
		ivd.setUpdatedQuantity(ivdDto.getUpdatedQty());
		ivd.setUpdatedRowTotal(ivdDto.getUpdatedRowTotal());
		ivd.setUpdatedStatus(true);
		ivd.setUpdatedsellingRate(ivdDto.getUpdatedsellingRate());
		ivd = inventoryVoucherDetailRepository.save(ivd);
		System.out.println("updatesellingrate"+ivd.getUpdatedsellingRate());
		return ivd;
		
	}

}
