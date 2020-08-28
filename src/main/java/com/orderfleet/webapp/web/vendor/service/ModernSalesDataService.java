package com.orderfleet.webapp.web.vendor.service;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.orderfleet.webapp.domain.EcomProductGroupEcomProduct;
import com.orderfleet.webapp.domain.EcomProductProfileProduct;
import com.orderfleet.webapp.domain.EmployeeProfileLocation;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.LocationHierarchy;
import com.orderfleet.webapp.domain.PriceLevelAccountEcomProductGroup;
import com.orderfleet.webapp.domain.PriceLevelAccountProductGroup;
import com.orderfleet.webapp.domain.PriceLevelList;
import com.orderfleet.webapp.domain.ProductGroupEcomProduct;
import com.orderfleet.webapp.domain.UserEcomProductGroup;
import com.orderfleet.webapp.domain.UserProductGroup;
import com.orderfleet.webapp.repository.EcomProductGroupEcomProductsRepository;
import com.orderfleet.webapp.repository.EcomProductGroupProductRepository;
import com.orderfleet.webapp.repository.EcomProductProfileProductRepository;
import com.orderfleet.webapp.repository.EcomProductProfileRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.PriceLevelAccountEcomProductGroupRepository;
import com.orderfleet.webapp.repository.PriceLevelAccountProductGroupRepository;
import com.orderfleet.webapp.repository.PriceLevelListRepository;
import com.orderfleet.webapp.repository.ProductGroupEcomProductsRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.UserEcomProductGroupRepository;
import com.orderfleet.webapp.repository.UserProductGroupRepository;
import com.orderfleet.webapp.service.LocationHierarchyService;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.orderfleet.webapp.web.tally.util.DateUtil;
import com.orderfleet.webapp.web.tally.util.KeyGeneratorUtil;

@Service
public class ModernSalesDataService {

	private final Logger log = LoggerFactory.getLogger(ModernSalesDataService.class);
	
	@Autowired
	LocationAccountProfileRepository locationAccountProfileRepository;
	
	@Autowired
	EmployeeProfileLocationRepository employeeProfileLocationRepository;
	
//..................Product Group Based
//	@Autowired
//	ProductGroupProductRepository productGroupProductRepository;
//	
//	@Autowired
//	UserProductGroupRepository userProductGroupRepository;
//	
//	@Autowired
//	ProductGroupEcomProductsRepository productGroupEcomProductRepo;
//	
//	@Autowired
//	PriceLevelAccountProductGroupRepository priceLevelAccountProductGroupRepository;
	
	

//..................Ecom Product Group Based
	@Autowired
	EcomProductGroupProductRepository productGroupProductRepository;
	
	@Autowired
	UserEcomProductGroupRepository userProductGroupRepository;
	
	@Autowired
	EcomProductGroupEcomProductsRepository productGroupEcomProductRepo;
	
	@Autowired
	PriceLevelAccountEcomProductGroupRepository priceLevelAccountProductGroupRepository;
	
	
	
	
	
	
	
	@Autowired
	EcomProductProfileRepository ecomProductProfileRepository;
	
	@Autowired
	EcomProductProfileProductRepository ecomProductProductProfileRepo;
	
	@Autowired
	LocationHierarchyRepository locationHierarchyRepository;
	
	@Autowired
	LocationHierarchyService locationHierarchyService;
	
	@Autowired
	LocationRepository locationRepository;
	
	@Autowired
	PriceLevelListRepository priceLevelListRepository;
	
	@Async
	@Transactional
	public void saveTransactions(ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper, Long companyId, String apiUrl) {
		if (tsTransactionWrapper != null) {
			log.info("posting data to server");
			postDataToServer( tsTransactionWrapper,  companyId,  apiUrl);
		}
	}
	
	
	
//New Ecom Product Group based 
	
	private  void postDataToServer(ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper, Long companyId, String apiUrl) {
		RestTemplate restTemplate = new RestTemplate();
		if (tsTransactionWrapper.getInventoryVouchers() != null
			&& !tsTransactionWrapper.getInventoryVouchers().isEmpty()) {
				List<InventoryVoucherHeader> ivhList = tsTransactionWrapper.getInventoryVouchers();
				List<String> accountProfilePids = ivhList.stream()
												.map(iv -> iv.getReceiverAccount().getPid())
												.collect(Collectors.toList());
				log.info("length account profile pids"+accountProfilePids.size());
				List<LocationAccountProfile> locationAccountProfiles = 
						locationAccountProfileRepository.findAllLocationByAccountProfilePids(accountProfilePids);
				
				
				
				log.info("length LocationAccountProfile "+locationAccountProfiles.size());
				List<EmployeeProfileLocation> employeeList = employeeProfileLocationRepository.
						findAllEmployeeByLocationPids(locationAccountProfiles.stream().map(
										la -> la.getLocation().getPid()).collect(Collectors.toList()));
				log.info("length EmployeeProfileLocation "+employeeList.size());
				List<InventoryVoucherDetail> ivdList = new ArrayList<InventoryVoucherDetail>();
				for(InventoryVoucherHeader iv : ivhList) {
					ivdList.addAll(iv.getInventoryVoucherDetails());
				}
				log.info("length InventoryVoucherDetail "+ivdList.size());
				List<PriceLevelList> priceLevelLists = priceLevelListRepository.findAllByCompanyAndProductProfilePidIn(
													companyId,ivdList.stream().map(ivd -> ivd.getProduct().getPid())
													.collect(Collectors.toList()));
				
				
				
				List<EcomProductProfileProduct> ecomProductProductProfile = 
						ecomProductProductProfileRepo.findByProductProfilePids(ivdList.stream()
																		.map(ivd -> ivd.getProduct().getPid())
																		.collect(Collectors.toList()));
				log.info("length EcomProductProfileProduct "+ecomProductProductProfile.size());
				List<EcomProductGroupEcomProduct> productGroupEcomProductList = productGroupEcomProductRepo.findProductGroupEcomProductByEcomProductPidIn(ecomProductProductProfile.stream()
																	.map(ecom -> ecom.getEcomProductProfile().getPid())
																	.collect(Collectors.toList()));
				List<String> productGroupPids = productGroupEcomProductList.stream()
						.map(pg -> pg.getEcomProductGroup().getPid()).collect(Collectors.toList());
				log.info("length ProductGroupEcom "+productGroupEcomProductList.size());
				List<UserEcomProductGroup> userProductGroupList = userProductGroupRepository.findByProductGroupPids(
						productGroupEcomProductList.stream().map(pgEcom -> pgEcom.getEcomProductGroup().getPid()).collect(Collectors.toList()));	
				log.info("length UserProductGroup "+userProductGroupList.size());
				
				List<LocationHierarchy> locationHierarchyList = locationHierarchyRepository.findByCompanyIdAndActivatedTrue(companyId);
				log.info("length LocationHierarchy "+locationHierarchyList.size());
				Optional<Long> northId = locationHierarchyList.stream().filter(lh ->
											lh.getLocation().getName().equalsIgnoreCase("NORTH"))
											.map(lh -> lh.getLocation().getId()).findFirst() ;
				Optional<Long> southId = locationHierarchyList.stream().filter(lh ->
											lh.getLocation().getName().equalsIgnoreCase("SOUTH"))
											.map(lh -> lh.getLocation().getId()).findFirst() ;
				log.info("northId :"+northId);
				log.info("southId :"+southId);
				List<Long> northChildren = new ArrayList<>();
				List<Long> southChildren = new ArrayList<>();
				if(northId.isPresent()) {
					northChildren = locationHierarchyService.getAllChildrenIdsByParentId(northId.get());
				}
				if(southId.isPresent()) {
					southChildren = locationHierarchyService.getAllChildrenIdsByParentId(southId.get());
				}
				log.info("north children size :"+northChildren.size());
				log.info("south children size :"+southChildren.size());
				
				List<PriceLevelAccountEcomProductGroup> priceLevelAccountProductGroups = priceLevelAccountProductGroupRepository
							.findByAccountPidsAndProductGroupPisds(companyId,accountProfilePids,productGroupPids);
				
			for(InventoryVoucherHeader ivh : tsTransactionWrapper.getInventoryVouchers()) {	 
					String xmlString = getSalesOrderXml(ivh,locationAccountProfiles
										,employeeList,userProductGroupList,locationHierarchyList,
										northChildren,southChildren,priceLevelAccountProductGroups,
										ecomProductProductProfile,productGroupEcomProductList,
										priceLevelLists).toString();
					try {
						FileWriter fw = new FileWriter("salesorderxml.txt",true);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(ivh.getCreatedDate() +"  ==== "+ ivh.getReceiverAccount().getName()+"\n"+xmlString+"\n\n\n");
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					log.info("-------------------");
					log.info(xmlString);
					log.info("-------------------");
					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_XML);
					HttpEntity<String> request = new HttpEntity<>(xmlString, headers);
//					final ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
//					if (response.getStatusCode().equals(HttpStatus.OK)) {
//						log.info("order successfully uploaded to **:modern ");
//					}
			}	
		}
		
	}

		private StringBuilder getSalesOrderXml(InventoryVoucherHeader inventoryVoucher, 
												List<LocationAccountProfile> locationAccountProfiles, 
												List<EmployeeProfileLocation> employeeLocationList,
												List<UserEcomProductGroup> userProductGroupList,
												List<LocationHierarchy> locationHierarchyList,
												List<Long> nChildren, List<Long> sChildren,
												List<PriceLevelAccountEcomProductGroup> priceLevelAccountProductGroups,
												List<EcomProductProfileProduct> ecomProductProductProfile,
												List<EcomProductGroupEcomProduct> productGroupEcomProductList, 
												List<PriceLevelList> priceLevelLists) {

		String key = KeyGeneratorUtil.getRandomAlphaNumericString(9);
		StringBuilder xmlRequest = new StringBuilder(
		"<ENVELOPE><VOUCHERS><ANDROIDID>1</ANDROIDID>");
		xmlRequest.append("<VOUCHERNO>");
		xmlRequest.append(inventoryVoucher.getDocumentNumberLocal()==null?key:inventoryVoucher.getDocumentNumberLocal());
		xmlRequest.append("</VOUCHERNO>");
		xmlRequest.append("<VOUCHERDATE>");
		xmlRequest.append(inventoryVoucher.getDocumentDate()==null?" ":DateUtil.convertLocalDateTimeToString(inventoryVoucher.getDocumentDate(), "dd-MMM-yyyy"));
		xmlRequest.append("</VOUCHERDATE>");
		xmlRequest.append("<CUSTOMER>");
		xmlRequest.append(inventoryVoucher.getReceiverAccount().getName()==null?" ":inventoryVoucher.getReceiverAccount().getName());
		xmlRequest.append("</CUSTOMER>");
		xmlRequest.append("<ORDERBRANCH>");
		
		 Optional<Location> location = locationAccountProfiles.stream().
				filter(la -> la.getAccountProfile().getPid().equals(inventoryVoucher.getReceiverAccount().getPid()))
				.map(la -> la.getLocation()).findFirst();
		if(location.isPresent()) {
			log.info("location Present"+location.get().getId());
			if(nChildren.stream().anyMatch(nc -> nc.longValue() == location.get().getId().longValue())){
				xmlRequest.append("Manjeri");
			}else if(sChildren.stream().anyMatch(sc -> sc.longValue() == location.get().getId().longValue())) {
				xmlRequest.append("Ernakulam");
			}
		}
		xmlRequest.append("</ORDERBRANCH>");
		xmlRequest.append("<ORDERTYPE>");
		xmlRequest.append(inventoryVoucher.getReferenceDocumentType()==null?"Android":inventoryVoucher.getReferenceDocumentType());
		xmlRequest.append("</ORDERTYPE>");
		xmlRequest.append("<DESTINATION>");
		//xmlRequest.append(inventoryVoucher.getReceiverAccount().getName()==null?" ":inventoryVoucher.getReceiverAccount().getName());
		xmlRequest.append("</DESTINATION>");
		xmlRequest.append("<ORDERREFNO>");
		xmlRequest.append(inventoryVoucher.getDocumentNumberLocal()==null?" ":inventoryVoucher.getDocumentNumberLocal());
		xmlRequest.append("</ORDERREFNO>");
		xmlRequest.append("<ORDERREGTIME>");
		xmlRequest.append(inventoryVoucher.getDocumentDate()==null?" ":DateUtil.convertLocalDateTimeToString(inventoryVoucher.getDocumentDate(), "HH:mm:ss a"));
		xmlRequest.append("</ORDERREGTIME>");
		xmlRequest.append("<EXECUTIVE>");
		Optional<LocationAccountProfile> locAcc = locationAccountProfiles.stream().
								filter(la -> 
								la.getAccountProfile().getPid()
								.equals(inventoryVoucher.getReceiverAccount().getPid())).findFirst();
		
		if(locAcc.isPresent()) {
			boolean employeeFound = false;
			List<EmployeeProfileLocation> epLocation = employeeLocationList.stream()
					.filter(el -> 
					el.getLocation().getPid().equals(locAcc.get().getLocation().getPid()))
					.collect(Collectors.toList());
			for(EmployeeProfileLocation el : epLocation) {
				for(UserEcomProductGroup upg : userProductGroupList) {
					if(upg.getUser().getId() == el.getEmployeeProfile().getUser().getId()) {
						log.info(el.getEmployeeProfile().getName()+"----*----"+upg.getUser().getFirstName());
//..................employee name to be used instead of Alias
						xmlRequest.append(el.getEmployeeProfile().getAlias());
						employeeFound  = true;
						break;
					}
				}
				if(employeeFound) {
					break;
				}
			}
		}
		xmlRequest.append("</EXECUTIVE>");
		xmlRequest.append("<REMARKS>");
		xmlRequest.append(inventoryVoucher.getExecutiveTaskExecution().getRemarks()==null?" ":inventoryVoucher.getExecutiveTaskExecution().getRemarks());
		xmlRequest.append("</REMARKS>");
		xmlRequest.append("<ORDERVALUE>");
		xmlRequest.append(inventoryVoucher.getDocumentTotal());
		xmlRequest.append("</ORDERVALUE>");
		for(InventoryVoucherDetail ivd : inventoryVoucher.getInventoryVoucherDetails()) {
			xmlRequest.append("<ITEMS><ANDROIDID>1</ANDROIDID>");
			xmlRequest.append("<GROUP>");
			Optional<EcomProductProfileProduct> ecomproduct = ecomProductProductProfile.stream()
					.filter(epp -> epp.getProduct().getPid().equals(ivd.getProduct().getPid())).findAny();
			
			Optional<EcomProductGroupEcomProduct> productGroup = productGroupEcomProductList.stream()
					.filter(pgEcom -> pgEcom.getEcomProduct().getPid().equals(ecomproduct.get().getEcomProductProfile().getPid())).findFirst();
			if(productGroup.isPresent()) {
//..................ProductGroup Name to be used instead of Alias
				xmlRequest.append(productGroup.get().getEcomProductGroup().getAlias());
			}
			
			xmlRequest.append("</GROUP>");
			xmlRequest.append("<ITEMNAME>");
//..................Item name  to be used instead of Alias
			xmlRequest.append(ivd.getProduct().getName()==null?" ":ivd.getProduct().getName());
			xmlRequest.append("</ITEMNAME>");
			xmlRequest.append("<QTY>");
			xmlRequest.append(ivd.getQuantity());
			xmlRequest.append("</QTY>");
			xmlRequest.append("<RATE>");
			
			Optional<PriceLevelAccountEcomProductGroup> priceLevelAccountProductGroup = priceLevelAccountProductGroups.stream()
					.filter(papg -> papg.getProductGroup().getPid()
					.equals(productGroup.get().getEcomProductGroup().getPid()) && 
					papg.getAccountProfile().getPid()
					.equals(inventoryVoucher.getReceiverAccount().getPid())).findAny();
			if(priceLevelAccountProductGroup.isPresent()) {
				Optional<PriceLevelList> priceLevelList = priceLevelLists.stream()
						.filter(pll -> 
							pll.getProductProfile().getPid().equals(ivd.getProduct().getPid()) &&
							pll.getPriceLevel().getPid().equals(priceLevelAccountProductGroup.get().getPriceLevel().getPid()))
							.findAny();
				xmlRequest.append(priceLevelList.get().getPrice());
			}
			xmlRequest.append("</RATE>");
			xmlRequest.append("<PRICETYPE>");
			if(priceLevelAccountProductGroup.isPresent()) {
				xmlRequest.append(priceLevelAccountProductGroup.get().getPriceLevel().getName());
			}
			xmlRequest.append("</PRICETYPE></ITEMS>");
		}
		xmlRequest.append("</VOUCHERS></ENVELOPE>");
		return xmlRequest;
		}
	
	
	
//Old Product Group based 	......................................
	
	
	
//	private  void postDataToServer(ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper, Long companyId, String apiUrl) {
//		RestTemplate restTemplate = new RestTemplate();
//		if (tsTransactionWrapper.getInventoryVouchers() != null
//			&& !tsTransactionWrapper.getInventoryVouchers().isEmpty()) {
//				List<InventoryVoucherHeader> ivhList = tsTransactionWrapper.getInventoryVouchers();
//				List<String> accountProfilePids = ivhList.stream()
//												.map(iv -> iv.getReceiverAccount().getPid())
//												.collect(Collectors.toList());
//				log.info("length account profile pids"+accountProfilePids.size());
//				List<LocationAccountProfile> locationAccountProfiles = 
//						locationAccountProfileRepository.findAllLocationByAccountProfilePids(accountProfilePids);
//				
//				
//				
//				log.info("length LocationAccountProfile "+locationAccountProfiles.size());
//				List<EmployeeProfileLocation> employeeList = employeeProfileLocationRepository.
//						findAllEmployeeByLocationPids(locationAccountProfiles.stream().map(
//										la -> la.getLocation().getPid()).collect(Collectors.toList()));
//				log.info("length EmployeeProfileLocation "+employeeList.size());
//				List<InventoryVoucherDetail> ivdList = new ArrayList<InventoryVoucherDetail>();
//				for(InventoryVoucherHeader iv : ivhList) {
//					ivdList.addAll(iv.getInventoryVoucherDetails());
//				}
//				log.info("length InventoryVoucherDetail "+ivdList.size());
//				List<PriceLevelList> priceLevelLists = priceLevelListRepository.findAllByCompanyAndProductProfilePidIn(
//													companyId,ivdList.stream().map(ivd -> ivd.getProduct().getPid())
//													.collect(Collectors.toList()));
//				
//				
//				
//				List<EcomProductProfileProduct> ecomProductProductProfile = 
//						ecomProductProductProfileRepo.findByProductProfilePids(ivdList.stream()
//																		.map(ivd -> ivd.getProduct().getPid())
//																		.collect(Collectors.toList()));
//				log.info("length EcomProductProfileProduct "+ecomProductProductProfile.size());
//				List<ProductGroupEcomProduct> productGroupEcomProductList = productGroupEcomProductRepo.findProductGroupEcomProductByEcomProductPidIn(ecomProductProductProfile.stream()
//																	.map(ecom -> ecom.getEcomProductProfile().getPid())
//																	.collect(Collectors.toList()));
//				List<String> productGroupPids = productGroupEcomProductList.stream()
//						.map(pg -> pg.getProductGroup().getPid()).collect(Collectors.toList());
//				log.info("length ProductGroupEcom "+productGroupEcomProductList.size());
//				List<UserProductGroup> userProductGroupList = userProductGroupRepository.findByProductGroupPids(
//						productGroupEcomProductList.stream().map(pgEcom -> pgEcom.getProductGroup().getPid()).collect(Collectors.toList()));	
//				log.info("length UserProductGroup "+userProductGroupList.size());
//				
//				List<LocationHierarchy> locationHierarchyList = locationHierarchyRepository.findByCompanyIdAndActivatedTrue(companyId);
//				log.info("length LocationHierarchy "+locationHierarchyList.size());
//				Optional<Long> northId = locationHierarchyList.stream().filter(lh ->
//											lh.getLocation().getName().equalsIgnoreCase("NORTH"))
//											.map(lh -> lh.getLocation().getId()).findFirst() ;
//				Optional<Long> southId = locationHierarchyList.stream().filter(lh ->
//											lh.getLocation().getName().equalsIgnoreCase("SOUTH"))
//											.map(lh -> lh.getLocation().getId()).findFirst() ;
//				log.info("northId :"+northId);
//				log.info("southId :"+southId);
//				List<Long> northChildren = new ArrayList<>();
//				List<Long> southChildren = new ArrayList<>();
//				if(northId.isPresent()) {
//					northChildren = locationHierarchyService.getAllChildrenIdsByParentId(northId.get());
//				}
//				if(southId.isPresent()) {
//					southChildren = locationHierarchyService.getAllChildrenIdsByParentId(southId.get());
//				}
//				log.info("north children size :"+northChildren.size());
//				log.info("south children size :"+southChildren.size());
//				
//				List<PriceLevelAccountProductGroup> priceLevelAccountProductGroups = priceLevelAccountProductGroupRepository
//							.findByAccountPidsAndProductGroupPisds(companyId,accountProfilePids,productGroupPids);
//				
//			for(InventoryVoucherHeader ivh : tsTransactionWrapper.getInventoryVouchers()) {	 
//					String xmlString = getSalesOrderXml(ivh,locationAccountProfiles
//										,employeeList,userProductGroupList,locationHierarchyList,
//										northChildren,southChildren,priceLevelAccountProductGroups,
//										ecomProductProductProfile,productGroupEcomProductList,
//										priceLevelLists).toString();
//					log.info("-------------------");
//					log.info(xmlString);
//					log.info("-------------------");
//					HttpHeaders headers = new HttpHeaders();
//					headers.setContentType(MediaType.APPLICATION_XML);
//					HttpEntity<String> request = new HttpEntity<>(xmlString, headers);
//					final ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
//					if (response.getStatusCode().equals(HttpStatus.OK)) {
//						log.info("order successfully uploaded to **:modern ");
//					}
//			}	
//		}
//		
//	}
//
//		private StringBuilder getSalesOrderXml(InventoryVoucherHeader inventoryVoucher, 
//												List<LocationAccountProfile> locationAccountProfiles, 
//												List<EmployeeProfileLocation> employeeLocationList,
//												List<UserProductGroup> userProductGroupList,
//												List<LocationHierarchy> locationHierarchyList,
//												List<Long> nChildren, List<Long> sChildren,
//												List<PriceLevelAccountProductGroup> priceLevelAccountProductGroups,
//												List<EcomProductProfileProduct> ecomProductProductProfile,
//												List<ProductGroupEcomProduct> productGroupEcomProductList, 
//												List<PriceLevelList> priceLevelLists) {
//
//		String key = KeyGeneratorUtil.getRandomAlphaNumericString(9);
//		StringBuilder xmlRequest = new StringBuilder(
//		"<ENVELOPE><VOUCHERS><ANDROIDID>1</ANDROIDID>");
//		xmlRequest.append("<VOUCHERNO>");
//		xmlRequest.append(inventoryVoucher.getDocumentNumberLocal()==null?key:inventoryVoucher.getDocumentNumberLocal());
//		xmlRequest.append("</VOUCHERNO>");
//		xmlRequest.append("<VOUCHERDATE>");
//		xmlRequest.append(inventoryVoucher.getDocumentDate()==null?" ":DateUtil.convertLocalDateTimeToString(inventoryVoucher.getDocumentDate(), "dd-MMM-yyyy"));
//		xmlRequest.append("</VOUCHERDATE>");
//		xmlRequest.append("<CUSTOMER>");
//		xmlRequest.append(inventoryVoucher.getReceiverAccount().getName()==null?" ":inventoryVoucher.getReceiverAccount().getName());
//		xmlRequest.append("</CUSTOMER>");
//		xmlRequest.append("<ORDERBRANCH>");
//		
//		 Optional<Location> location = locationAccountProfiles.stream().
//				filter(la -> la.getAccountProfile().getPid().equals(inventoryVoucher.getReceiverAccount().getPid()))
//				.map(la -> la.getLocation()).findFirst();
//		if(location.isPresent()) {
//			log.info("location Present"+location.get().getId());
//			if(nChildren.stream().anyMatch(nc -> nc.longValue() == location.get().getId().longValue())){
//				xmlRequest.append("Manjeri");
//			}else if(sChildren.stream().anyMatch(sc -> sc.longValue() == location.get().getId().longValue())) {
//				xmlRequest.append("Ernakulam");
//			}
//		}
//		xmlRequest.append("</ORDERBRANCH>");
//		xmlRequest.append("<ORDERTYPE>");
//		xmlRequest.append(inventoryVoucher.getReferenceDocumentType()==null?"Android":inventoryVoucher.getReferenceDocumentType());
//		xmlRequest.append("</ORDERTYPE>");
//		xmlRequest.append("<DESTINATION>");
//		//xmlRequest.append(inventoryVoucher.getReceiverAccount().getName()==null?" ":inventoryVoucher.getReceiverAccount().getName());
//		xmlRequest.append("</DESTINATION>");
//		xmlRequest.append("<ORDERREFNO>");
//		xmlRequest.append(inventoryVoucher.getDocumentNumberLocal()==null?" ":inventoryVoucher.getDocumentNumberLocal());
//		xmlRequest.append("</ORDERREFNO>");
//		xmlRequest.append("<ORDERREGTIME>");
//		xmlRequest.append(inventoryVoucher.getDocumentDate()==null?" ":DateUtil.convertLocalDateTimeToString(inventoryVoucher.getDocumentDate(), "HH:mm:ss a"));
//		xmlRequest.append("</ORDERREGTIME>");
//		xmlRequest.append("<EXECUTIVE>");
//		Optional<LocationAccountProfile> locAcc = locationAccountProfiles.stream().
//								filter(la -> 
//								la.getAccountProfile().getPid()
//								.equals(inventoryVoucher.getReceiverAccount().getPid())).findFirst();
//		
//		if(locAcc.isPresent()) {
//			boolean employeeFound = false;
//			List<EmployeeProfileLocation> epLocation = employeeLocationList.stream()
//					.filter(el -> 
//					el.getLocation().getPid().equals(locAcc.get().getLocation().getPid()))
//					.collect(Collectors.toList());
//			for(EmployeeProfileLocation el : epLocation) {
//				for(UserProductGroup upg : userProductGroupList) {
//					if(upg.getUser().getId() == el.getEmployeeProfile().getUser().getId()) {
//						log.info(el.getEmployeeProfile().getName()+"----*----"+upg.getUser().getFirstName());
//						xmlRequest.append(el.getEmployeeProfile().getAlias());
//						employeeFound  = true;
//						break;
//					}
//				}
//				if(employeeFound) {
//					break;
//				}
//			}
//		}
//		xmlRequest.append("</EXECUTIVE>");
//		xmlRequest.append("<REMARKS>");
//		xmlRequest.append(inventoryVoucher.getExecutiveTaskExecution().getRemarks()==null?" ":inventoryVoucher.getExecutiveTaskExecution().getRemarks());
//		xmlRequest.append("</REMARKS>");
//		xmlRequest.append("<ORDERVALUE>");
//		xmlRequest.append(inventoryVoucher.getDocumentTotal());
//		xmlRequest.append("</ORDERVALUE>");
//		for(InventoryVoucherDetail ivd : inventoryVoucher.getInventoryVoucherDetails()) {
//			xmlRequest.append("<ITEMS><ANDROIDID>1</ANDROIDID>");
//			xmlRequest.append("<GROUP>");
//			Optional<EcomProductProfileProduct> ecomproduct = ecomProductProductProfile.stream()
//					.filter(epp -> epp.getProduct().getPid().equals(ivd.getProduct().getPid())).findAny();
//			
//			Optional<ProductGroupEcomProduct> productGroup = productGroupEcomProductList.stream()
//					.filter(pgEcom -> pgEcom.getEcomProduct().getPid().equals(ecomproduct.get().getEcomProductProfile().getPid())).findFirst();
//			if(productGroup.isPresent()) {
//				xmlRequest.append(productGroup.get().getProductGroup().getAlias());
//			}
//			
//			xmlRequest.append("</GROUP>");
//			xmlRequest.append("<ITEMNAME>");
//			xmlRequest.append(ivd.getProduct().getAlias()==null?" ":ivd.getProduct().getAlias());
//			xmlRequest.append("</ITEMNAME>");
//			xmlRequest.append("<QTY>");
//			xmlRequest.append(ivd.getQuantity());
//			xmlRequest.append("</QTY>");
//			xmlRequest.append("<RATE>");
//			
//			Optional<PriceLevelAccountProductGroup> priceLevelAccountProductGroup = priceLevelAccountProductGroups.stream()
//					.filter(papg -> papg.getProductGroup().getPid()
//					.equals(productGroup.get().getProductGroup().getPid()) && 
//					papg.getAccountProfile().getPid()
//					.equals(inventoryVoucher.getReceiverAccount().getPid())).findAny();
//			if(priceLevelAccountProductGroup.isPresent()) {
//				Optional<PriceLevelList> priceLevelList = priceLevelLists.stream()
//						.filter(pll -> 
//							pll.getProductProfile().getPid().equals(ivd.getProduct().getPid()) &&
//							pll.getPriceLevel().getPid().equals(priceLevelAccountProductGroup.get().getPriceLevel().getPid()))
//							.findAny();
//				xmlRequest.append(priceLevelList.get().getPrice());
//			}
//			xmlRequest.append("</RATE>");
//			xmlRequest.append("<PRICETYPE>");
//			if(priceLevelAccountProductGroup.isPresent()) {
//				xmlRequest.append(priceLevelAccountProductGroup.get().getPriceLevel().getName());
//			}
//			xmlRequest.append("</PRICETYPE></ITEMS>");
//		}
//		xmlRequest.append("</VOUCHERS></ENVELOPE>");
//		return xmlRequest;
//		}

}
