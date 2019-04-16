
package com.orderfleet.webapp.web.rest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.PostDatedVoucherService;
import com.orderfleet.webapp.web.rest.api.dto.PostDatedVoucherDTO;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;

/**
 * @author Anish
 * @since 14 September 2018
 *
 */
@Controller
@RequestMapping("/web")
public class PostDatedVoucherResources {

	private final Logger log = LoggerFactory.getLogger(PostDatedVoucherResources.class);
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Inject
	private PostDatedVoucherService postDatedVoucherService;
	
	@Timed
	@RequestMapping(value = "/post-dated-vouchers", method = RequestMethod.GET)
	public String getPostDatedVouchers(Model model) {
		log.debug("Web request to get a page of Post dated vouchers");
		model.addAttribute("accounts", accountProfileService.findAllByCompany());
		return "company/postDatedVouchers";
	}
	
	@Timed
	@GetMapping("/post-dated-vouchers/load")
	public ResponseEntity<Map<String , List<PostDatedVoucherDTO>>> postDatedVoucherReport(
			@RequestParam String accountPid) {
		log.debug("REST request to get all post dated vouchers");
		List<PostDatedVoucherDTO> postDatedVoucherDTOs = null;
		if (accountPid.equals("no")) {
			postDatedVoucherDTOs = postDatedVoucherService.findAllPostDatedVouchers();
		} else {
			postDatedVoucherDTOs = postDatedVoucherService.findAllPostDatedVoucherByAccountProfilePid(accountPid);
		}
		Map<String, List<PostDatedVoucherDTO>> result = postDatedVoucherDTOs.parallelStream()
				.collect(Collectors.groupingBy(PostDatedVoucherDTO::getAccountProfilePid));
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
}
