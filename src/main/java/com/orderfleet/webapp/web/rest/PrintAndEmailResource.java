package com.orderfleet.webapp.web.rest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.CompanyEmail.ModuleName;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.repository.CompanyEmailRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentPrintEmailService;
import com.orderfleet.webapp.service.async.MailService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentPrintEmailDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.FilledFormDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;

/**
 * Web controller for managing print and email.
 * 
 * @author Muhammed Riyas T
 * @since November 23, 2016
 */
@Controller
@RequestMapping("/web")
public class PrintAndEmailResource {
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	private final DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	private final MailService mailService;

	private final AccountProfileMapper accountProfileMapper;

	private final CompanyEmailRepository companyEmailRepository;

	private final DocumentPrintEmailService documentPrintEmailService;
	
	private final InternalResourceViewResolver viewResolver;

	public PrintAndEmailResource(DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository,
			MailService mailService, AccountProfileMapper accountProfileMapper,
			CompanyEmailRepository companyEmailRepository, DocumentPrintEmailService documentPrintEmailService,InternalResourceViewResolver viewResolver) {
		super();
		this.dynamicDocumentHeaderRepository = dynamicDocumentHeaderRepository;
		this.mailService = mailService;
		this.accountProfileMapper = accountProfileMapper;
		this.companyEmailRepository = companyEmailRepository;
		this.documentPrintEmailService = documentPrintEmailService;
		this.viewResolver = viewResolver;
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/print/{dynamicDocumentPid}", method = RequestMethod.GET)
	public String otherVoucherTransactionPrint(Model model,
			@PathVariable("dynamicDocumentPid") String dynamicDocumentPid, @RequestParam String name) {
		String docPid = addDocumentDetailsAndGetDocumentPid(dynamicDocumentPid, model);
		if(docPid != null) {
			Optional<DocumentPrintEmailDTO> documentPrintEmailDTO = documentPrintEmailService
					.findOneByDocumentPidAndNames(docPid, name);
			if (documentPrintEmailDTO.isPresent()) {
				return documentPrintEmailDTO.get().getPrintFilePath();
			}
		}
		return "print/default-voucher-print";
	}
	
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/email/{dynamicDocumentPid}", method = RequestMethod.GET)
	public @ResponseBody boolean otherVoucherTransactionEmail(Model model,
			@PathVariable("dynamicDocumentPid") String dynamicDocumentPid, @RequestParam("subject") String subject,
			@RequestParam("toEmails") String toEmails, @RequestParam("name") String name, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String docPid = addDocumentDetailsAndGetDocumentPid(dynamicDocumentPid, model);
		if(docPid != null) {
			Optional<DocumentPrintEmailDTO> documentPrintEmailDTO = documentPrintEmailService
					.findOneByDocumentPidAndNames(docPid, name);
			if (documentPrintEmailDTO.isPresent()) {
				View resolvedView = viewResolver.resolveViewName(documentPrintEmailDTO.get().getEmailFilePath(), Locale.US);
				MockHttpServletResponse mockResp = new MockHttpServletResponse();
				resolvedView.render(model.asMap(), request, mockResp);
				String responseString = mockResp.getContentAsString();
				companyEmailRepository.findFirstByModuleNameAndCompanyId(ModuleName.ESTIMATE, SecurityUtils.getCurrentUsersCompanyId()).ifPresent(ce -> {
					mailService.sendEmail(toEmails, null, subject, responseString , true, true, ce.getEmail(), ce.getPassword());	
				});
				return true;
			}
		}
		return false;
	}
	
	private String addDocumentDetailsAndGetDocumentPid(String dynamicDocumentPid, Model model) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "DYN_QUERY_146" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by Pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<DynamicDocumentHeader> optionalDynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findOneByPid(dynamicDocumentPid);
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
		if (optionalDynamicDocumentHeader.isPresent()) {
			DynamicDocumentHeader dynamicDocumentHeader = optionalDynamicDocumentHeader.get();
			List<FilledFormDTO> filledFormDTOs = dynamicDocumentHeader.getFilledForms().stream().map(FilledFormDTO::new)
					.collect(Collectors.toList());
			DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO = new DynamicDocumentHeaderDTO(dynamicDocumentHeader);
			dynamicDocumentHeaderDTO.setFilledForms(filledFormDTOs);
			// generate and set doc no
			String[] array = dynamicDocumentHeaderDTO.getDocumentNumberServer().split("_+");
			String result = "000" + array[3];
			int length = result.length();
			result = result.substring(length - 4, length);
			String documentNumberServer = array[2] + result;
			dynamicDocumentHeaderDTO.setDocumentNumberServer(documentNumberServer);
			// set model attribute
			model.addAttribute("dynamicDocument", dynamicDocumentHeaderDTO);
			AccountProfileDTO accountProfileDTO = accountProfileMapper.accountProfileToAccountProfileDTO(
					dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile());
			model.addAttribute("account", accountProfileDTO);
			
			return dynamicDocumentHeaderDTO.getDocumentPid();
		}
		
		return null;
	}
}
