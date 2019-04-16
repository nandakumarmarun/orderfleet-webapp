package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.service.AccountingVoucherUISettingService;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherUISettingDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class AccountingVoucherUISettingResource {
	
	
	@Inject
	private DocumentService documentService;
	
	@Inject
	private AccountingVoucherUISettingService accountingVoucherUISettingService;
	
	@Inject
	private ActivityService activityService;
	
	/**
	 * GET /accounting-voucher-ui-setting : get all the accountingVoucherUISetting.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of accountingVoucherUISetting
	 *         in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/accounting-voucher-ui-setting", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountingVoucherUISetting(Model model) throws URISyntaxException {
		List<AccountingVoucherUISettingDTO> accountingVoucherUISettingDTOs = accountingVoucherUISettingService.findAllByCompany();
		model.addAttribute("accountingVoucherUISettings", accountingVoucherUISettingDTOs);
		List<ActivityDTO> activities = activityService.findAllByCompany();
		model.addAttribute("activities", activities);
		List<PaymentMode>paymentModes=Arrays.asList(PaymentMode.values());
		model.addAttribute("paymentModes", paymentModes);
		return "company/accountingVoucherUISetting";
	}
	
	/**
	 * POST /accounting-voucher-ui-setting : Create a new accountingVoucherUISetting.
	 *
	 * @param accountingVoucherUISettingDTO
	 *            the accountingVoucherUISettingDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new accountingVoucherUISettingDTO, or with status 400 (Bad Request) if the accountingVoucherUISetting
	 *         has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/accounting-voucher-ui-setting", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<AccountingVoucherUISettingDTO> createAccountingVoucherUISetting(@Valid @RequestBody AccountingVoucherUISettingDTO accountingVoucherUISettingDTO)
			throws URISyntaxException {
		if (accountingVoucherUISettingDTO.getId() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("accountingVoucherUISetting", "idexists", "A new accountingVoucherUISetting cannot already have an ID"))
					.body(null);
		}
//			if(accountingVoucherUISettingService.findByCompanyIdAndActivityPidAndDocumentTypeAndDocumentPid(accountingVoucherUISettingDTO.getActivityPid(), accountingVoucherUISettingDTO.getDocumentType(), accountingVoucherUISettingDTO.getDocumentPid()).isPresent()) {
//			return ResponseEntity.badRequest()
//					.headers(HeaderUtil.createFailureAlert("accountingVoucherUISetting", "accountingVoucherUISetting exists", "AccountingVoucherUISetting already in use"))
//					.body(null);
//		}

		AccountingVoucherUISettingDTO result = accountingVoucherUISettingService.save(accountingVoucherUISettingDTO);
		return ResponseEntity.created(new URI("/web/accounting-voucher-ui-setting"))
				.headers(HeaderUtil.createEntityCreationAlert("accountingVoucherUISetting", result.getId().toString())).body(result);
	}
	
	/**
	 * PUT /accounting-voucher-ui-setting : Updates an existing document.
	 *
	 * @param documentDTO
	 *            the documentDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         documentDTO, or with status 400 (Bad Request) if the documentDTO
	 *         is not valid, or with status 500 (Internal Server Error) if the
	 *         documentDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/accounting-voucher-ui-setting", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountingVoucherUISettingDTO> updateAccountingVoucherUISetting(@Valid @RequestBody AccountingVoucherUISettingDTO accountingVoucherUISettingDTO)
			throws URISyntaxException {
		if (accountingVoucherUISettingDTO.getId() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("accountingVoucherUISetting", "idNotexists", "AccountingVoucherUISetting must have an ID"))
					.body(null);
		}
		
//		Optional<AccountingVoucherUISettingDTO> existingAccountingVoucherUISetting = accountingVoucherUISettingService.findByCompanyIdAndActivityPidAndDocumentTypeAndDocumentPid(accountingVoucherUISettingDTO.getActivityPid(), accountingVoucherUISettingDTO.getDocumentType(), accountingVoucherUISettingDTO.getDocumentPid());
//		if (existingAccountingVoucherUISetting.isPresent() && (!existingAccountingVoucherUISetting.get().getId().equals(accountingVoucherUISettingDTO.getId()))) {
//			return ResponseEntity.badRequest()
//					.headers(HeaderUtil.createFailureAlert("accountingVoucherUISetting", "accountingVoucherUISettingexists", "AccountingVoucherUISetting already in use"))
//					.body(null);
//		}
		AccountingVoucherUISettingDTO result = accountingVoucherUISettingService.update(accountingVoucherUISettingDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("accountingVoucherUISetting", "idNotexists", "Invalid Document ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("accountingVoucherUISetting", accountingVoucherUISettingDTO.getId().toString())).body(result);
	}
	/**
	 * GET /accounting-voucher-ui-setting/:id : get the "id" accountingVoucherUISetting.
	 *
	 * @param id
	 *            the id of the accountingVoucherUISettingDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         documentDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/accounting-voucher-ui-setting/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountingVoucherUISettingDTO> getAccountingVoucherUISetting(@PathVariable Long id) {
		AccountingVoucherUISettingDTO accountingVoucherUISettingDTO=accountingVoucherUISettingService.findOne(id);
		return new ResponseEntity<AccountingVoucherUISettingDTO>(accountingVoucherUISettingDTO, HttpStatus.OK);
		
	}

	/**
	 * DELETE /accounting-voucher-ui-setting/:id : delete the "id" accountingVoucherUISetting.
	 *
	 * @param id
	 *            the id of the accountingVoucherUISettingDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/accounting-voucher-ui-setting/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteAccountingVoucherUISetting(@PathVariable Long id) {
		accountingVoucherUISettingService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("accountingVoucherUISetting", id.toString())).build();
	}
	
	@RequestMapping(value = "/accounting-voucher-ui-setting/loaddocument" , method = RequestMethod.GET)
	public @ResponseBody List<DocumentDTO> getDocumentsByUser(@RequestParam(value = "activityPid") String activityPid) {
		List<DocumentDTO>documentDTOs=documentService.findAllByActivityPid(activityPid);
		return documentDTOs;
	}
}
