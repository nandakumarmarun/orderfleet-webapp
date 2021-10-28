package com.orderfleet.webapp.web.tally.api;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.GstLedger;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.TallyConfiguration;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.GstLedgerRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.TallyConfigurationRepository;
import com.orderfleet.webapp.web.tally.dto.GstLedgerDTO;
import com.orderfleet.webapp.web.tally.dto.SalesItemTally;
import com.orderfleet.webapp.web.tally.dto.SalesOrderTally;
import com.orderfleet.webapp.web.tally.dto.TallyConfigurationDTO;



@RestController
@RequestMapping(value = "/api/tally")
public class SalesOrderTallyController {

	private final Logger log = LoggerFactory.getLogger(SalesOrderTallyController.class);

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private TallyConfigurationRepository tallyConfigRepository;
	
	@Inject
	private GstLedgerRepository gstLedgerRepository;
	
	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	TallyConfiguration tallyConfig = null;

	@RequestMapping(value = "/get-sales-orders-tally", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<SalesOrderTally> getSalesOrderJSON(@RequestParam("key")String productKey,@RequestParam("company")String companyName) throws URISyntaxException {
		log.debug("REST request to download sales orders : {}");
		Optional<TallyConfiguration> tallyConfiguration = tallyConfigRepository.findByProductKeyAndCompanyName(productKey,companyName);
		List<SalesOrderTally> saleOrderTallyList = new ArrayList<>();
		Set<String> inventoryVoucherHeaderPids = new HashSet<>();
		
		if(!tallyConfiguration.isPresent()) {
			log.info("Configuration Not set");
			return saleOrderTallyList;
		}
		
		tallyConfig = tallyConfiguration.get();
		List<VoucherType> voucherTypes = Arrays.asList(VoucherType.PRIMARY_SALES, VoucherType.PRIMARY_SALES_ORDER);
		List<PrimarySecondaryDocument> psDocuments = primarySecondaryDocumentRepository.findByVoucherTypesAndCompany(voucherTypes, tallyConfig.getCompany().getId());
		Set<Long> documentIds = psDocuments.stream().map(psd -> psd.getDocument().getId()).collect(Collectors.toSet());
		String id="INV_QUERY_169";
		String description="Listing sales orders from tally";
		log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");

		List<Object[]> salesOrders = inventoryVoucherHeaderRepository.getSalesOrdersForTally(tallyConfig.getCompany().getId(), documentIds);
		List<GstLedger> gstLedgers = gstLedgerRepository.findAllByCompanyIdAndActivated(tallyConfig.getCompany().getId(), true);
		List<GstLedgerDTO> gstLedgerDTOs = gstLedgers.stream().map(GstLedgerDTO::new).collect(Collectors.toList());
		String previousPid = "";
		List<SalesItemTally> items = new ArrayList<>();
		
		SalesOrderTally sales = new SalesOrderTally();
		int size = salesOrders.size();
		int i = 0;
		for(Object[] o: salesOrders) {
			i++;
			String pid = o[15].toString();
			if( previousPid.equals(pid)) {
				SalesItemTally salesItem = new SalesItemTally();
				salesItem.setProductProfileName(o[5].toString());
				salesItem.setTaxRate(getTax(o));
				salesItem.setUnit(o[7].toString());
				salesItem.setSellingRate(o[8]==null?0:(double)o[8]);
				salesItem.setQuantity(o[9]==null?0:(double)o[9]);
				salesItem.setDiscountPercentage(o[10]==null?0:(double)o[10]);
				salesItem.setFreeQuantity(o[12]==null?0:(double)o[12]);
				salesItem.setStockLocationName(o[14]==null?"":o[14].toString());
				salesItem.setRemarks(o[13]==null?"":o[13].toString());
				items.add(salesItem);
				sales.setSalesItemList(items);
			}else {
				if(!previousPid.equals("")) {
					saleOrderTallyList.add(sales);
				}
				previousPid = pid;
				sales = new SalesOrderTally();
				items = new ArrayList<>();
				sales.setDate(o[0]==null?"":o[0].toString());
				sales.setTrimChar(o[1]==null?"":o[1].toString());
				sales.setLedgerName(o[2]==null?"":o[2].toString());
				sales.setLedgerAddress(o[3]==null?"":o[3].toString());
				sales.setPriceLevelName(o[4]==null?"":o[4].toString());
				sales.setDocumentTotal(o[11]==null?0:(double)o[11]);
				//sales.setRemarks(o[13]==null?"":o[13].toString());
				sales.setPid(o[15]==null?"":o[15].toString());
				sales.setEmployeeName(o[16]==null?"":o[16].toString());
				sales.setTallyConfig(new TallyConfigurationDTO(tallyConfig));
				sales.setGstLedgers(gstLedgerDTOs);
				sales.setVoucherType(psDocuments.stream().filter(psd -> psd.getDocument().getId() == Long.parseLong(o[17].toString())).findAny().get().getVoucherType());
				
				SalesItemTally salesItem = new SalesItemTally();
				salesItem.setProductProfileName(o[5]==null?"":o[5].toString());
				salesItem.setTaxRate(getTax(o));
				salesItem.setUnit(o[7]==null?"":o[7].toString());
				salesItem.setSellingRate(o[8]==null?0:(double)o[8]);
				salesItem.setQuantity(o[9]==null?0:(double)o[9]);
				salesItem.setDiscountPercentage(o[10]==null?0:(double)o[10]);
				salesItem.setFreeQuantity(o[12]==null?0:(double)o[12]);
				salesItem.setStockLocationName(o[14]==null?"":o[14].toString());
				salesItem.setRemarks(o[13]==null?"":o[13].toString());
				items.add(salesItem);
				sales.setSalesItemList(items);
			}
			if(i==size) {
				saleOrderTallyList.add(sales);
			}
			
		}
		
		if(!saleOrderTallyList.isEmpty()) {
			inventoryVoucherHeaderPids = saleOrderTallyList.stream().map(ivh -> ivh.getPid()).collect(Collectors.toSet());
			List<String> inventoryHeaderPid = new ArrayList<>(inventoryVoucherHeaderPids);
			log.info("SET SIZE : " + inventoryVoucherHeaderPids.size());
			log.info("LIST SIZE : " + inventoryHeaderPid.size());
			String id1="INV_QUERY_181";
			String description1="Updating inv Vou header TallydownloadStatus using pid and Companyid";
			log.info("{ Query Id:- "+id1+" Query Description:- "+description1+" }");
			int updated = inventoryVoucherHeaderRepository.
								updateInventoryVoucherHeaderTallyDownloadStatusUsingPidAndCompanyId(
										TallyDownloadStatus.PROCESSING,tallyConfig.getCompany().getId(), inventoryHeaderPid);
			log.debug("updated "+updated+" to PROCESSING");
		}
		
		System.out.println("=======================++++++++++++++++++++++++++++");
			for(SalesOrderTally sal : saleOrderTallyList) {
				System.out.println(sal.toString());
				System.out.println("===================");
				for(SalesItemTally is :sal.getSalesItemList()) {
					System.out.println(is.toString());
				}
				System.out.println("===================");
			}
		System.out.println("=======================++++++++++++++++++++++++++++");

		return saleOrderTallyList;
	}


	//method used for update order status based on tally response : updating variable TallyDownloadStatus enum
	@RequestMapping(value = "/update-salesorder-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public ResponseEntity<Void> UpdarteOrderStatusWithTallyResponse(@Valid @RequestHeader("key") String tallyKey,
																	@RequestHeader("company") String companyName,
																	@RequestBody List<String> inventoryVoucherHeaderPids)
			throws URISyntaxException {
		log.debug("REST request to update Inventory Voucher Header Status (tally) : {}", inventoryVoucherHeaderPids.size());
		Optional<TallyConfiguration> tallyConfiguration = tallyConfigRepository.findByProductKeyAndCompanyName(tallyKey,companyName);
		
		if(tallyConfiguration.isPresent()){
			if (!inventoryVoucherHeaderPids.isEmpty()) {
				String id="INV_QUERY_181";
				String description="Updating inv Vou header TallydownloadStatus using pid and Companyid";
				log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
				inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPidAndCompanyId(
						TallyDownloadStatus.COMPLETED,tallyConfiguration.get().getCompany().getId(),inventoryVoucherHeaderPids);
			}
			return new ResponseEntity<>(HttpStatus.CREATED);
		}else{
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	private double getTax(Object[] object){
		
		if(object[6]!=null){
			if((double)object[6] == 0){
				if(object[18]!=null){
					return (double)object[18];
				} else {
					return 0;
				}
			} else {
				return (double)object[6];
			}
		} else {
			return 0;
		}
	}
	
	
}
