package com.orderfleet.webapp.repository.custom;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.orderfleet.webapp.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.SalesOrderStatus;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;

@Component
public class InventoryVoucherDetailCustomRepositoryImpl implements InventoryVoucherDetailCustomRepository {

	private final Logger log = LoggerFactory.getLogger(InventoryVoucherDetailCustomRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;
	
	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private ProductGroupProductRepository productGroupProductRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;
	


	@Override
	public List<InventoryVoucherDetailDTO> getInventoryDetailListBy(List<String> productCategoryPids,
			List<String> productGroupPids, List<String> productProfilePids, List<String> stockLocationPids,
			LocalDateTime fromDate, LocalDateTime toDate, List<String> documentPids, List<String> productTerritoryPids,
			List<String> employeePids, String status, List<String> accountPids) {

		Map<String, List<String>> queryParameters = new HashMap<>();
		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = new ArrayList<>();
		
		StringBuilder subQueryString = new StringBuilder("select " + "ivd.inventoryVoucherHeader.createdDate,"
				+ "ivd.inventoryVoucherHeader.employee.name," + "ivd.inventoryVoucherHeader.documentNumberServer,"
				+ "ivd.inventoryVoucherHeader.receiverAccount.name,"
				+ "ivd.inventoryVoucherHeader.supplierAccount.name," + "ivd.product.productCategory.name,"
				+ "ivd.product.name," + "ivd.quantity," + "ivd.sellingRate," + "ivd.rowTotal," + "ivd.product.pid,"
				+ "ivd.product.unitQty," + "ivd.volume," + "ivd.inventoryVoucherHeader.receiverAccount.location,"
				+ "ivd.inventoryVoucherHeader.supplierAccount.location,"
				+ "ivd.inventoryVoucherHeader.document.activityAccount," + "ivd.product.productDescription,"+"ivd.inventoryVoucherHeader.receiverAccount.pid,"+"ivd.inventoryVoucherHeader.deliveryDate,"+"ivd.inventoryVoucherHeader.salesOrderStatus");
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

		log.debug( "Enter : typedQuery.getResultList() " + LocalDateTime.now());
		List<Object[]> objectArray = typedQuery.getResultList();
		log.debug( "Exit : typedQuery.getResultList() " + LocalDateTime.now());

		log.debug("Enter : for(Object[] object : objectArray) { } ");
		for (Object[] object : objectArray) { 
			InventoryVoucherDetailDTO ivd = new InventoryVoucherDetailDTO();
			ivd.setCreatedDate((LocalDateTime) object[0]);
			ivd.setEmployeeName((String) object[1]);
			ivd.setOderID((String) object[2]);
			ivd.setAccountName((String) object[3]);
			ivd.setSupplierAccountName((String) object[4]);
			ivd.setProductCategory((String) object[5]);
			ivd.setProductName((String) object[6]);
			ivd.setQuantity((double) object[7]);
			ivd.setSellingRate((double) object[8]);
			ivd.setRowTotal((double) object[9]);
	
			ivd.setProductPid((String) object[10]);
			if (!stockLocationPids.isEmpty()) {
				ivd.setSourceStockLocationName((String) object[13]);
				ivd.setDestinationStockLocationName((String) object[14]);
			}
						
			ivd.setProductUnitQty(object[11] != null ? Double.valueOf(object[11].toString()) : 1);
			ivd.setVolume(Double.valueOf(object[12].toString()));
			ivd.setCustomerLocation(object[13] != null ? object[13].toString() : "");
			
			if (AccountTypeColumn.valueOf(object[15].toString()).equals(AccountTypeColumn.Supplier)) {
				ivd.setCustomerLocation(object[14] != null ? object[14].toString() : "");
			}
			ivd.setProductDescription((String) object[16]);
			ivd.setAccountPid((String) object[17]);
			
			if(object[18] != null){
				LocalDate deliveryDateLocal = (LocalDate) object[18];
				LocalDateTime deliveryDate = deliveryDateLocal.atTime(0, 0);
				ivd.setDeliveryDate(deliveryDate);
			}
			ivd.setSalesOrderStatus(SalesOrderStatus.valueOf(object[19].toString()));
			inventoryVoucherDetailDTOs.add(ivd);
		}
		log.debug("Exit : for(Object[] object : objectArray) { } - " + inventoryVoucherDetailDTOs.size());
		return inventoryVoucherDetailDTOs;
	}


	@Override
	public List<InventoryVoucherDetailDTO> getInventoryDetailListByItemWiseSalesJonarinOptmised(
			Long companyid,List<String> productCategoryPids, List<String> productGroupPids,
			List<String> productProfilePids, List<String> stockLocationPids, LocalDateTime fromDate,
			LocalDateTime toDate, List<String> documentPids, List<String> productTerritoryPids,
			List<String> employeePids, String status, List<String> accountPids) {

		log.debug("Enter : InventoryVoucherDetailCustomRepositoryImpl.getInventoryDetailListByItemWiseSaleResourceOptimised");

		List<Object[]> objectArray;
		Map<String, List<String>> queryParameters = new HashMap<>();
		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = new ArrayList<>();

		log.debug("Enter : productGroupProductRepository.findProductPidsByProductGroupPidIn : " +  productGroupPids.size());
		List<String>  ProductPids =
				productGroupProductRepository
						.findProductPidsByProductGroupPidIn(productGroupPids);
		log.debug("Exit  : productGroupProductRepository.findProductPidsByProductGroupPidIn : "+ ProductPids.size());

		log.debug("ProductPids : " + ProductPids.size());
		log.debug("documentPids : " + documentPids.size());
		log.debug("Territory-accountPids: " + productTerritoryPids.size());
		log.debug("employeePids : " + employeePids.size());
		log.debug("accountPids : " + accountPids.size());

		if(productTerritoryPids.get(0).equals("-1")){
			log.debug("Enter : inventoryVoucherDetailRepository.getInventoryDetailListFIlltered - first Query : All Data ");
			objectArray = inventoryVoucherDetailRepository
					.getInventoryDetailListFIlltered(
							companyid,fromDate,toDate,ProductPids, documentPids,
							employeePids);
			log.debug("Exit  : inventoryVoucherDetailRepository.getInventoryDetailListFIlltered " +  objectArray.size());
		}
		else{
			log.debug("Enter : inventoryVoucherDetailRepository.getInventoryDetailList - second Query : fetch Data using account pids ");
			objectArray = inventoryVoucherDetailRepository.getInventoryDetailList(
					companyid,fromDate,toDate,ProductPids, documentPids,
					employeePids,accountPids);
			log.debug("Exit  :  inventoryVoucherDetailRepository.getInventoryDetailList " + objectArray.size());
		}
		inventoryVoucherDetailDTOs = getFillterdData(objectArray);
		log.debug("Exit  : for(Object[] object : objectArray) { } - " + inventoryVoucherDetailDTOs.size());
		return inventoryVoucherDetailDTOs;
	}


	@Override
	public List<InventoryVoucherDetailDTO> getInventoryDetailListByItemSummaryEmployeeWiseResourceOptmised(
			Long companyid, List<String> productCategoryPids, List<String> productGroupPids,
			List<String> productProfilePids, List<String> stockLocationPids, LocalDateTime fromDate,
			LocalDateTime toDate, List<String> documentPids, List<String> productTerritoryPids,
			List<String> employeePids, String status, List<String> accountPids) {

		log.debug("Enter : InventoryVoucherDetailCustomRepositoryImpl.getInventoryDetailListBy()");

		Map<String, List<String>> queryParameters = new HashMap<>();
		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = new ArrayList<>();
		List<String>  ProductPids ;
		List<String> productaTerrotoryccountPids;

		log.debug( "Enter :  inventoryVoucherDetailRepository.getInventoryDetailListsummery" + LocalDateTime.now());
		List<Object[]> objectArray = inventoryVoucherDetailRepository.getInventoryDetailListsummery(
				companyid,fromDate,toDate,documentPids,
				employeePids,accountPids);
		log.debug( "Exit :  inventoryVoucherDetailRepository.getInventoryDetailListsummery" + LocalDateTime.now());

		log.debug("Enter : for(Object[] object : objectArray){}");
		for (Object[] object : objectArray) {
			InventoryVoucherDetailDTO ivd = new InventoryVoucherDetailDTO();
			ivd.setCreatedDate((LocalDateTime) object[0]);
			ivd.setEmployeeName((String) object[1]);
			ivd.setOderID((String) object[2]);
			ivd.setAccountName((String) object[3]);
			ivd.setSupplierAccountName((String) object[4]);
			ivd.setProductCategory((String) object[5]);
			ivd.setProductName((String) object[6]);
			ivd.setQuantity((double) object[7]);
			ivd.setSellingRate((double) object[8]);
			ivd.setRowTotal((double) object[9]);
			ivd.setProductPid((String) object[10]);

			if (!stockLocationPids.isEmpty()) {
				ivd.setSourceStockLocationName((String) object[13]);
				ivd.setDestinationStockLocationName((String) object[14]);
			}

			ivd.setProductUnitQty(object[11] != null ? Double.valueOf(object[11].toString()) : 1);

			double volume=Double.valueOf(object[12].toString());
			ivd.setVolume(volume);

			ivd.setCustomerLocation(object[13] != null ? object[13].toString() : "");

			if (AccountTypeColumn.valueOf(object[15].toString()).equals(AccountTypeColumn.Supplier)) {
				ivd.setCustomerLocation(object[14] != null ? object[14].toString() : "");
			}

			ivd.setProductDescription((String) object[16]);
			ivd.setAccountPid((String) object[17]);

			if(object[18] != null){
				LocalDate deliveryDateLocal = (LocalDate) object[18];
				LocalDateTime deliveryDate = deliveryDateLocal.atTime(0, 0);
				ivd.setDeliveryDate(deliveryDate);
			}
			ivd.setSalesOrderStatus(SalesOrderStatus.valueOf(object[19].toString()));

			inventoryVoucherDetailDTOs.add(ivd);
		}
		log.debug("Exit : for(Object[] object : objectArray) { } - " + inventoryVoucherDetailDTOs.size());
		return inventoryVoucherDetailDTOs;
	}


	@Override
	public List<InventoryVoucherDetailDTO> getInventoryDetailListByItemWiseSaleResourceOptmised(
			Long companyid, List<String> productCategoryPids, List<String> productGroupPids,
			List<String> productProfilePids, List<String> stockLocationPids, LocalDateTime fromDate,
			LocalDateTime toDate, List<String> documentPids, List<String> productTerritoryPids,
			List<String> employeePids, String status, List<String> accountPids) {
			log.debug("Enter : InventoryVoucherDetailCustomRepositoryImpl.getInventoryDetailListByItemWiseSaleResourceOptimised");

		  List<Object[]> objectArray;
			Map<String, List<String>> queryParameters = new HashMap<>();
			List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = new ArrayList<>();

			log.debug("Enter : productGroupProductRepository.findProductPidsByProductGroupPidIn : " +  productGroupPids.size());
			List<String>  ProductPids =
					productGroupProductRepository
							.findProductPidsByProductGroupPidIn(productGroupPids);
		log.debug("Exit : productGroupProductRepository.findProductPidsByProductGroupPidIn : "+ ProductPids.size());

		log.debug("ProductPids 		 : " + ProductPids.size());
		log.debug("documentPids 	 : " + documentPids.size());
		log.debug("Territory-accountPids : " + productTerritoryPids.size());
		log.debug("employeePids 	 : " + employeePids.size());
		log.debug("accountPids		 : " + accountPids.size());

		if(productTerritoryPids.get(0).equals("-1")){

			log.debug("Enter inventoryVoucherDetailRepository.getInventoryDetailListFIlltered - first Query : All Data ");
			objectArray = inventoryVoucherDetailRepository
					.getInventoryDetailListFIlltered(
							companyid,fromDate,toDate,ProductPids, documentPids,
							employeePids);
			log.debug("Exit : inventoryVoucherDetailRepository.getInventoryDetailListFIlltered " +  objectArray.size());
		}
		else{
			log.debug("Enter inventoryVoucherDetailRepository.getInventoryDetailList - second Query : fetch Data using account pids ");
			objectArray = inventoryVoucherDetailRepository.getInventoryDetailList(
					companyid,fromDate,toDate,ProductPids, documentPids,
					employeePids,accountPids);
			log.debug("Exit :  inventoryVoucherDetailRepository.getInventoryDetailList " + objectArray.size());
		}
		inventoryVoucherDetailDTOs = getFillterdData(objectArray);
		log.debug("Exit : for(Object[] object : objectArray) { } - " + inventoryVoucherDetailDTOs.size());
		return inventoryVoucherDetailDTOs;
	}


	private List<InventoryVoucherDetailDTO> getFillterdData(List<Object[]> objectArray) {
		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = new ArrayList<>();
		log.debug("Enter : for(Object[] object : objectArray){}");
		for (Object[] object : objectArray) {
			InventoryVoucherDetailDTO ivd = new InventoryVoucherDetailDTO();
			ivd.setCreatedDate((LocalDateTime) object[0]);
			ivd.setEmployeeName((String) object[1]);
			ivd.setOderID((String) object[2]);
			ivd.setAccountName((String) object[3]);
			ivd.setSupplierAccountName((String) object[4]);
			ivd.setProductCategory((String) object[5]);
			ivd.setProductName((String) object[6]);
			ivd.setQuantity((double) object[7]);
			ivd.setSellingRate((double) object[8]);
			ivd.setRowTotal((double) object[9]);
			ivd.setProductPid((String) object[10]);

			ivd.setProductUnitQty(object[11] != null ? Double.valueOf(object[11].toString()) : 1);

			double volume=Double.valueOf(object[12].toString());
			ivd.setVolume(volume);

			ivd.setCustomerLocation(object[13] != null ? object[13].toString() : "");

			if (AccountTypeColumn.valueOf(object[15].toString()).equals(AccountTypeColumn.Supplier)) {
				ivd.setCustomerLocation(object[14] != null ? object[14].toString() : "");
			}

			ivd.setProductDescription((String) object[16]);
			ivd.setAccountPid((String) object[17]);

			if(object[18] != null){
				LocalDate deliveryDateLocal = (LocalDate) object[18];
				LocalDateTime deliveryDate = deliveryDateLocal.atTime(0, 0);
				ivd.setDeliveryDate(deliveryDate);
			}
			ivd.setSalesOrderStatus(SalesOrderStatus.valueOf(object[19].toString()));

			inventoryVoucherDetailDTOs.add(ivd);
		}
		return inventoryVoucherDetailDTOs;
	}


	@Override
	public List<InventoryVoucherDetailDTO> getInventoryDetailListByItemWiseSummaryResourceOptmised(
			Long companyid, List<String> productCategoryPids, List<String> productGroupPids,
			List<String> productProfilePids, List<String> stockLocationPids, LocalDateTime fromDate,
			LocalDateTime toDate, List<String> documentPids, List<String> productTerritoryPids,
			List<String> employeePids, String status, List<String> accountPids) {

		log.debug("Enter : InventoryVoucherDetailCustomRepositoryImpl.getInventoryDetailListBy()");

		Map<String, List<String>> queryParameters = new HashMap<>();
		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = new ArrayList<>();

//		log.debug("Enter : ProductPids : " +  productGroupPids.size());
//		List<String>  ProductPids =  productGroupProductRepository
//				.findProductPidsByProductGroupPidIn(productGroupPids);
//		log.debug("Exit : ProductPids : "+ ProductPids.size());

//		log.debug("Enter : productaTerrotoryccountPids : " + productTerritoryPids.size());
//		List<String> productaTerrotoryccountPids = locationAccountProfileRepository
//				.findAccountProfilePidByLocationPidIn(productTerritoryPids);
//		log.debug("Exit : productaTerrotoryccountPids  : " + productaTerrotoryccountPids.size());

		log.debug( "Enter :  inventoryVoucherDetailRepository.getInventoryDetailList" + LocalDateTime.now());
		List<Object[]> objectArray = inventoryVoucherDetailRepository.getInventoryDetailListsummeryitemwise(
				companyid,fromDate,toDate,documentPids);
		log.debug( "Exit :  inventoryVoucherDetailRepository.getInventoryDetailList" + LocalDateTime.now());

		log.debug("Enter : for(Object[] object : objectArray){}");
		for (Object[] object : objectArray) {
			InventoryVoucherDetailDTO ivd = new InventoryVoucherDetailDTO();
			ivd.setCreatedDate((LocalDateTime) object[0]);
			ivd.setEmployeeName((String) object[1]);
			ivd.setOderID((String) object[2]);
			ivd.setAccountName((String) object[3]);
			ivd.setSupplierAccountName((String) object[4]);
			ivd.setProductCategory((String) object[5]);
			ivd.setProductName((String) object[6]);
			ivd.setQuantity((double) object[7]);
			ivd.setSellingRate((double) object[8]);
			ivd.setRowTotal((double) object[9]);
			ivd.setProductPid((String) object[10]);

			if (!stockLocationPids.isEmpty()) {
				ivd.setSourceStockLocationName((String) object[13]);
				ivd.setDestinationStockLocationName((String) object[14]);
			}

			ivd.setProductUnitQty(object[11] != null ? Double.valueOf(object[11].toString()) : 1);

			double volume=Double.valueOf(object[12].toString());
			ivd.setVolume(volume);

			ivd.setCustomerLocation(object[13] != null ? object[13].toString() : "");

			if (AccountTypeColumn.valueOf(object[15].toString()).equals(AccountTypeColumn.Supplier)) {
				ivd.setCustomerLocation(object[14] != null ? object[14].toString() : "");
			}

			ivd.setProductDescription((String) object[16]);
			ivd.setAccountPid((String) object[17]);

			if(object[18] != null){
				LocalDate deliveryDateLocal = (LocalDate) object[18];
				LocalDateTime deliveryDate = deliveryDateLocal.atTime(0, 0);
				ivd.setDeliveryDate(deliveryDate);
			}
			ivd.setSalesOrderStatus(SalesOrderStatus.valueOf(object[19].toString()));

			inventoryVoucherDetailDTOs.add(ivd);
		}
		log.debug("Exit : for(Object[] object : objectArray) { } : " + inventoryVoucherDetailDTOs.size() );
		return inventoryVoucherDetailDTOs;
	}

}
