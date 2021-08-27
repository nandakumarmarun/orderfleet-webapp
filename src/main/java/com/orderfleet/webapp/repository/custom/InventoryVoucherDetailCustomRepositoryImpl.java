package com.orderfleet.webapp.repository.custom;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;

@Component
public class InventoryVoucherDetailCustomRepositoryImpl implements InventoryVoucherDetailCustomRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<InventoryVoucherDetailDTO> getInventoryDetailListBy(List<String> productCategoryPids,
			List<String> productGroupPids, List<String> productProfilePids, List<String> stockLocationPids,
			LocalDateTime fromDate, LocalDateTime toDate, List<String> documentPids, List<String> productTerritoryPids,
			List<String> employeePids, String status, List<String> accountPids) {

		Map<String, List<String>> queryParameters = new HashMap<>();
		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = new ArrayList<>();

		StringBuilder subQueryString = new StringBuilder("select " + "ivd.inventoryVoucherHeader.createdDate,"
				+ "ivd.inventoryVoucherHeader.employee.name," + "ivd.inventoryVoucherHeader.receiverAccount.name,"
				+ "ivd.inventoryVoucherHeader.supplierAccount.name," + "ivd.product.productCategory.name,"
				+ "ivd.product.name," + "ivd.quantity," + "ivd.sellingRate," + "ivd.rowTotal," + "ivd.product.pid,"
				+ "ivd.product.unitQty," + "ivd.volume ");
		if (!stockLocationPids.isEmpty()) {
			subQueryString.append(",ivd.sourceStockLocation.name," + "ivd.destinationStockLocation.name ");
		}
		subQueryString.append(" from InventoryVoucherDetail ivd where ");
		subQueryString.append("ivd.inventoryVoucherHeader.company.id = :companyId ");
		subQueryString.append("and ivd.inventoryVoucherHeader.createdDate between :fromDate and :toDate");

		if (productCategoryPids.size() != 0 || !productCategoryPids.isEmpty()) {
			subQueryString.append(" and ivd.product.productCategory.pid in :productCategoryPids ");
			queryParameters.put("productCategoryPids", productCategoryPids);
		}
		if (productGroupPids.size() != 0 || !productGroupPids.isEmpty()) {
			subQueryString.append(" and ivd.product.pid in "
					+ "(select pgp.product.pid from ProductGroupProduct pgp where pgp.productGroup.pid in :productGropPids "
					+ "and pgp.product.activated = true)");
			queryParameters.put("productGropPids", productGroupPids);
		}

		if (productProfilePids.size() != 0 || !productProfilePids.isEmpty()) {
			subQueryString.append(" and ivd.product.pid in :productProfilePids");
			queryParameters.put("productProfilePids", productProfilePids);
		}

		if (stockLocationPids.size() != 0 || !stockLocationPids.isEmpty()) {
			subQueryString.append(
					" and (ivd.sourceStockLocation.pid in :stockLocationPids or ivd.destinationStockLocation.pid in :stockLocationPids)");
			queryParameters.put("stockLocationPids", stockLocationPids);
		}

		if (documentPids.size() != 0 || !documentPids.isEmpty()) {
			subQueryString.append(" and ivd.inventoryVoucherHeader.document.pid in :documentPids");
			queryParameters.put("documentPids", documentPids);
		}

		if (productTerritoryPids.size() != 0 || !productTerritoryPids.isEmpty()) {
			subQueryString.append(
					" and ivd.inventoryVoucherHeader.receiverAccount in (select lap.accountProfile from LocationAccountProfile lap where lap.location.pid in :productTerritoryPids)");
			queryParameters.put("productTerritoryPids", productTerritoryPids);
		}

		if (employeePids.size() != 0 || !employeePids.isEmpty()) {
			subQueryString.append(" and ivd.inventoryVoucherHeader.employee.pid in :employeePids");
			queryParameters.put("employeePids", employeePids);
		}

		if (accountPids.size() != 0 || !accountPids.isEmpty()) {
			subQueryString.append(" and ivd.inventoryVoucherHeader.receiverAccount.pid in :accountPids");
			queryParameters.put("accountPids", accountPids);
		}

		if (status != null && !status.isEmpty()) {
			if (status.equals("PENDING")) {
				subQueryString.append(" and ivd.inventoryVoucherHeader.status = false");
			}
		}

		TypedQuery<Object[]> typedQuery = entityManager.createQuery(subQueryString.toString(), Object[].class);
		for (Map.Entry<String, List<String>> entry : queryParameters.entrySet()) {
			String key = entry.getKey();
			List<String> value = entry.getValue();
			typedQuery.setParameter(key, value);
		}
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		typedQuery.setParameter("companyId", companyId);
		typedQuery.setParameter("fromDate", fromDate);
		typedQuery.setParameter("toDate", toDate);
		List<Object[]> objectArray = typedQuery.getResultList();
		for (Object[] object : objectArray) {
			InventoryVoucherDetailDTO ivd = new InventoryVoucherDetailDTO();
			ivd.setCreatedDate((LocalDateTime) object[0]);
			ivd.setEmployeeName((String) object[1]);
			ivd.setAccountName((String) object[2]);
			ivd.setSupplierAccountName((String) object[3]);
			ivd.setProductCategory((String) object[4]);
			ivd.setProductName((String) object[5]);
			ivd.setQuantity((double) object[6]);
			ivd.setSellingRate((double) object[7]);
			ivd.setRowTotal((double) object[8]);
			ivd.setProductPid((String) object[9]);
			if (!stockLocationPids.isEmpty()) {
				ivd.setSourceStockLocationName((String) object[10]);
				ivd.setDestinationStockLocationName((String) object[11]);
			}
			ivd.setProductUnitQty(object[12] != null ? Double.valueOf(object[12].toString()) : 1);
			ivd.setVolume(Double.valueOf(object[13].toString()));
			inventoryVoucherDetailDTOs.add(ivd);
		}

		return inventoryVoucherDetailDTOs;
	}

}
