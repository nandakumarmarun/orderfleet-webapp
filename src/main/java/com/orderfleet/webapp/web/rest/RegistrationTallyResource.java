package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.GstLedger;
import com.orderfleet.webapp.domain.SnrichProductCompany;
import com.orderfleet.webapp.domain.TallyConfiguration;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.GstLedgerRepository;
import com.orderfleet.webapp.repository.SnrichProductCompanyRepository;
import com.orderfleet.webapp.repository.TallyConfigurationRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.TallyConfigurationService;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;
import com.orderfleet.webapp.web.tally.dto.TallyConfigurationDTO;

/**
 * WEB controller for managing the current user's account.
 * 
 * @author Shaheer
 * @since September 07, 2016
 */
@Controller
public class RegistrationTallyResource {

	private static final String VIEW_REGISTRATION_TALLY = "account/register-tally";
	private static final String VIEW_REGISTRATION_SUCCESS_TALLY = "account/register-success-company-tally";
	
	private final Logger log = LoggerFactory.getLogger(RegistrationTallyResource.class);
	
	@Inject
	private TallyConfigurationService tallyConfigurationService; 
	
	@Inject
	private SnrichProductCompanyRepository snrichProductCompanyRepository;
	
	@Inject
	private TallyConfigurationRepository tallyConfigurationRepository;
	
	@Inject 
	private GstLedgerRepository gstLedgerRepository;
	
	@Inject
	private CompanyRepository companyRepository;
	
	
	@RequestMapping(value = "/register-tally", method = RequestMethod.GET)
	public String registerAccountPage(Model model,@RequestParam(value="tSerial",required=true) String tallySerialNo,
													@RequestParam(value="tCompany",required=true) String tallyCompany,
													@RequestParam(value="key" ,required=true) String activationKey) {
		TallyConfigurationDTO tallyConfig = new TallyConfigurationDTO();
		 if((tallySerialNo !=null && !tallySerialNo.equals("")) &&
				(tallyCompany !=null && !tallyCompany.equals(""))) {
			Optional<SnrichProductCompany> snrichProduct = snrichProductCompanyRepository.findByTallyActivationKey(activationKey);  
			if(snrichProduct.isPresent()) {
				Optional<TallyConfiguration> opTallyConfig = tallyConfigurationRepository.findByProductKeyAndCompanyName(tallySerialNo, tallyCompany);
				tallyConfig.setTallyCompanyName(tallyCompany);
				tallyConfig.setTallyProductKey(tallySerialNo);
				tallyConfig.setCompanyName(snrichProduct.get().getCompany().getLegalName());
				tallyConfig.setCompanyPid(snrichProduct.get().getCompany().getPid());
				if(opTallyConfig.isPresent()){
					tallyConfig.setPid(opTallyConfig.get().getPid());
					tallyConfig.setBankName(opTallyConfig.get().getBankName());
					tallyConfig.setBankReceiptType(opTallyConfig.get().getBankReceiptType());
					tallyConfig.setCashReceiptType(opTallyConfig.get().getCashReceiptType());
					tallyConfig.setPdcVoucherType(opTallyConfig.get().getPdcVoucherType());
					tallyConfig.setReceiptVoucherType(opTallyConfig.get().getReceiptVoucherType());
					tallyConfig.setRoundOffLedgerName(opTallyConfig.get().getRoundOffLedgerName());
					tallyConfig.setSalesLedgerName(opTallyConfig.get().getSalesLedgerName());
					tallyConfig.setTransactionType(opTallyConfig.get().getTransactionType());
					tallyConfig.setActualBillStatus(opTallyConfig.get().getActualBillStatus());
					tallyConfig.setOrderNumberWithEmployee(opTallyConfig.get().getOrderNumberWithEmployee());
					tallyConfig.setItemRemarksEnabled(opTallyConfig.get().getItemRemarksEnabled());
				}
				model.addAttribute("tallyConfig",tallyConfig);
				return VIEW_REGISTRATION_TALLY;
			}else {
				return "/new-theme/index";
			}
		}
		return "/new-theme/index";
	}

	@PostMapping("/register-tally")
	public String registerTally(@Valid TallyConfigurationDTO tallyConfig, BindingResult result, Model model) {

//		tallyConfig.setDynamicDate("2019-02-01T00:00:00.000");
//		tallyConfig.setStaticSalesOrderDate("2019-02-01T00:00:00.000");
		TallyConfiguration tallyconfiguration = new TallyConfiguration();
		if(tallyConfig.getPid() == null || tallyConfig.getPid().isEmpty()){
			tallyconfiguration = tallyConfigurationService.save(tallyConfig);
		} else {
			tallyconfiguration = tallyConfigurationService.update(tallyConfig);
		}
		if(tallyconfiguration.getId()>0) {
			return VIEW_REGISTRATION_SUCCESS_TALLY;
		}else {
			model.addAttribute("msg","Tally registration failed");
			return VIEW_REGISTRATION_TALLY;
		}
		
	}
	
	@GetMapping("/tally-success")
	public String checkTallySuccessPage() {
		return VIEW_REGISTRATION_SUCCESS_TALLY;
	}
	
	
	@RequestMapping(value = "/gst-ledger-configure", method = RequestMethod.GET)
	public String gstLedgerConfigure(Model model,@RequestParam(value="tSerial",required=true) String tallySerialNo,
													@RequestParam(value="tCompany",required=true) String tallyCompany) {
		Optional<TallyConfiguration> tallyConfig = tallyConfigurationRepository
				.findByProductKeyAndCompanyName(tallySerialNo,tallyCompany);
		if(tallyConfig.isPresent()){
			List<GstLedger> gstLedgers = gstLedgerRepository.findAllByCompanyId(tallyConfig.get().getCompany().getId());
			model.addAttribute("gstLedgers",gstLedgers);
			return "company/gst-ledger-configure";
		}else{
			model.addAttribute("error","Invalid Credentials");
			model.addAttribute("message","Tally not configured yet!");
			return "error";
		}
	}
	
	@RequestMapping(value = "/gst-ledger-configure/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteGstLedger(@PathVariable Long id) {
		log.debug("REST request to delete gst-ledger : {}", id);
		gstLedgerRepository.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("gst-ledger", id.toString())).build();
	}
	
	@RequestMapping(value = "/gst-ledger-configure/activation", method = RequestMethod.POST)
	public ResponseEntity<Void> gstLedgerActivate(@RequestBody List<GstLedger> gstLedgers) {
		System.out.println(gstLedgers);
		List<Long> trues = new ArrayList<>();
		List<Long> falses = new ArrayList<>();
		for(GstLedger gstLedger : gstLedgers){
			if(gstLedger.isActivated()==true){
				trues.add(gstLedger.getId());
			}else{
				falses.add(gstLedger.getId());
			}
		}
		if(trues.size()!=0){
			gstLedgerRepository.updateByid(true, trues);
		}if(falses.size()!=0){
			gstLedgerRepository.updateByid(false, falses);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("gst-ledger", gstLedgers.toString())).build();
	}
	
	@RequestMapping(value = "/web/gst-ledger", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	@Secured({ AuthoritiesConstants.SITE_ADMIN})
	public String gstLedger(Model model)  throws URISyntaxException {
			model.addAttribute("companies",companyRepository.findAll());
			return "site_admin/gst-ledger";
	}
	
	@RequestMapping(value = "/web/gst-ledger/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<GstLedger>> getAllGstLedgersByCompany(@PathVariable String pid) {
		log.debug("Web request to get Company by pid : {}", pid);
		Optional<Company> company = companyRepository.findOneByPid(pid);
		List<GstLedger> gstLedgers = new ArrayList<>();
		if(company.isPresent()){
			gstLedgers = gstLedgerRepository.findAllByCompanyId(company.get().getId());
		}
		return new ResponseEntity<>(gstLedgers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/web/gst-ledger/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteWebGstLedger(@PathVariable Long id) {
		log.debug("REST request to delete gst-ledger : {}", id);
		gstLedgerRepository.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("gst-ledger", id.toString())).build();
	}
	
	@RequestMapping(value = "/web/gst-ledger/activation", method = RequestMethod.POST)
	public ResponseEntity<Void> gstWebLedgerActivate(@RequestBody List<GstLedger> gstLedgers) {
		System.out.println(gstLedgers);
		List<Long> trues = new ArrayList<>();
		List<Long> falses = new ArrayList<>();
		for(GstLedger gstLedger : gstLedgers){
			if(gstLedger.isActivated()==true){
				trues.add(gstLedger.getId());
			}else{
				falses.add(gstLedger.getId());
			}
		}
		if(trues.size()!=0){
			gstLedgerRepository.updateByid(true, trues);
		}if(falses.size()!=0){
			gstLedgerRepository.updateByid(false, falses);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("gst-ledger", gstLedgers.toString())).build();
	}
	
}