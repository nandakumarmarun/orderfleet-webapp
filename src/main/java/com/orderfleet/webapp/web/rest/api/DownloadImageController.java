package com.orderfleet.webapp.web.rest.api;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.async.event.EventProducer;
import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.web.rest.api.dto.PunchingUserDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FileManagerService;

/**
 * REST controller for managing Attendance.
 * 
 * @author Muhammed Riyas T
 * @since October 18, 2016
 */
@RestController
@RequestMapping(value = "/api")
public class DownloadImageController {

	private final Logger log = LoggerFactory.getLogger(DownloadImageController.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private EcomProductProfileRepository ecomProductProfileRepository;

	@Inject
	private FileManagerService fileManagerService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private BestPerformerRepository bestPerformerRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;
	@Inject
	private  InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private AccountingVoucherDetailRepository accountingVoucherDetailRepository;

	@Inject
	private FilledFormRepository filledFormRepository;

	@Inject
	private FilledFormDetailRepository filledFormDetailRepository;

	@Inject
	private EventProducer eventProducer;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;


	@RequestMapping(value = "/product-group-image/{productGroupPid}", method = RequestMethod.GET)
	public @ResponseBody void getproductGroupImage(@PathVariable String productGroupPid, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.info("show ProductGroup image................." + productGroupPid);

		String errorMessage = "";
		Optional<ProductGroup> opProductGroup = productGroupRepository.findOneByPid(productGroupPid);
		if (opProductGroup.isPresent()) {
			ProductGroup productGroup = opProductGroup.get();
			if (productGroup.getImage() != null) {

				response.setContentType(productGroup.getImageContentType());
				response.setHeader("Content-Disposition",
						String.format("inline; filename=\"" + productGroup.getName() + "\""));

				InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(productGroup.getImage()));
				FileCopyUtils.copy(inputStream, response.getOutputStream());
				return;
			} else {
				errorMessage = "Sorry. image not available";
			}
		} else {
			errorMessage = "Sorry. The file you are looking for does not exist";
		}
		System.out.println(errorMessage);
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
		outputStream.close();
	}

	@RequestMapping(value = "/ecom-product-profile-image/{ecomProductPid}", method = RequestMethod.GET)
	public @ResponseBody void getEcomProductProfileImage(@PathVariable String ecomProductPid,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.info("show ecom-Product image................." + ecomProductPid);

		String errorMessage = "";
		Optional<EcomProductProfile> opEcomProductProfile = ecomProductProfileRepository.findOneByPid(ecomProductPid);
		if (opEcomProductProfile.isPresent()) {
			EcomProductProfile ecomProductProfile = opEcomProductProfile.get();
			if (ecomProductProfile.getImage() != null) {

				response.setContentType(ecomProductProfile.getImageContentType());
				response.setHeader("Content-Disposition",
						String.format("inline; filename=\"" + ecomProductProfile.getName() + "\""));

				InputStream inputStream = new BufferedInputStream(
						new ByteArrayInputStream(ecomProductProfile.getImage()));
				FileCopyUtils.copy(inputStream, response.getOutputStream());
				return;
			} else {
				errorMessage = "Sorry. image not available";
			}
		} else {
			errorMessage = "Sorry. The file you are looking for does not exist";
		}
		System.out.println(errorMessage);
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
		outputStream.close();
	}

	@RequestMapping(value = "/product-image/{filePid}", method = RequestMethod.GET)
	public @ResponseBody void getPdf(@PathVariable String filePid, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Optional<File> file = fileManagerService.findOneByPid(filePid);
		if (file.isPresent()) {
			File file2 = file.get();
			java.io.File physicalFile = this.fileManagerService.getPhysicalFileByFile(file2);
			if (physicalFile.exists()) {
				response.setContentType(file2.getMimeType());
				response.setHeader("Content-Disposition", "inline; filename=\"" + file2.getFileName() + "\"");
				response.setContentLength((int) physicalFile.length());
				try (InputStream inputStream = new BufferedInputStream(new FileInputStream(physicalFile));) {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				}
				return;
			}
		}
		String errorMessage = "Sorry. The file you are looking for does not exist";
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
		outputStream.close();
	}

	@RequestMapping(value = "/company-image", method = RequestMethod.GET)
	public @ResponseBody void getCompanyImageCurrentUser(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String errorMessage = "";
		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if (opUser.isPresent()) {
			User user = opUser.get();
			Company company = user.getCompany();
			if (company.getLogo() != null) {
				response.setContentType(company.getLogoContentType());
				response.setHeader("Content-Disposition",
						String.format("inline; filename=\"" + company.getLegalName() + "\""));

				InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(company.getLogo()));
				FileCopyUtils.copy(inputStream, response.getOutputStream());
				return;
			} else {
				errorMessage = "Sorry. image not available";
			}
		}
		errorMessage = "Sorry. The file you are looking for does not exist";
		System.out.println(errorMessage);
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
		outputStream.close();
	}

	@RequestMapping(value = "/best-performer-upload", method = RequestMethod.GET)
	public @ResponseBody void getBestPerformerImageCurrentUser(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String errorMessage = "";
		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "BP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by compId";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		BestPerformer cd = bestPerformerRepository.findOneByCompanyId();
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

		if (opUser.isPresent() && cd != null) {
			User user = opUser.get();
			Company company = user.getCompany();
			if (cd.getLogo() != null) {
				response.setContentType(cd.getLogoContentType());
				response.setHeader("Content-Disposition",
						String.format("inline; filename=\"" + company.getLegalName() + "\""));

				InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(cd.getLogo()));
				FileCopyUtils.copy(inputStream, response.getOutputStream());
				return;
			} else {
				errorMessage = "Sorry. image not available";
			}
		}
		errorMessage = "Sorry. The file you are looking for does not exist";
		System.out.println(errorMessage);
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
		outputStream.close();
	}
	@GetMapping("/inventory-sync")
	@Timed
	public @ResponseBody void findInventoryVoucherHeaders(
			@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate) {
		System.out.println("Staring job" +LocalDateTime.now());
		System.out.println("Date Range From" +fromDate+" To " + toDate );
		log.debug("request for get inventory ");
		Set<String> ivhpid = new HashSet<>();
		List<InventoryVoucherDetail> ivhdlist = new ArrayList<>();
		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs = new ArrayList<>();
			if (fromDate != null && toDate != null) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				LocalDate frdate = LocalDate.parse(fromDate, formatter);
				LocalDate todate = LocalDate.parse(toDate, formatter);
				LocalDateTime fromdate1 = frdate.atTime(0, 0);
				LocalDateTime todate1 = todate.atTime(23, 59);
				List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findAllByCompanyIdAndDateBetweenOrderByDocumentDateDesc(fromdate1, todate1);
				inventoryVoucherHeaders.forEach(data -> ivhpid.add(data.getPid()));
				if (!ivhpid.isEmpty()) {
					ivhdlist=inventoryVoucherDetailRepository.findAllByInventoryVoucherHeaderPidIn(ivhpid);

				}
				for(InventoryVoucherHeader ivh1 : inventoryVoucherHeaders) {
					List<InventoryVoucherDetail> ivhdetails = new ArrayList<>();
					for(InventoryVoucherDetail ivhd : ivhdlist) {
						if(ivh1.getId().equals(ivhd.getInventoryVoucherHeader().getId())) {
							ivhdetails.add(ivhd);
							ivh1.setInventoryVoucherDetails(ivhdetails);
						}
					}
				}
				List<InventoryVoucherHeaderDTO> ivhDTOs = inventoryVoucherHeaders.stream().map(data -> new InventoryVoucherHeaderDTO(data.getCompany(), data)).collect(Collectors.toList());
				System.out.println("list Size ==" +ivhDTOs.size());
				System.out.println("time Before streaming" +LocalDateTime.now());
				ivhDTOs.forEach(ivhDTO -> eventProducer.inventoryVoucherStreamPublish(ivhDTO));
				System.out.println("time After streaming" +LocalDateTime.now());
			}
			}
	@GetMapping("/dynamic-sync")
	@Timed
	public @ResponseBody void findDynamicVoucherHeaders(
			@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate) {
		log.debug("request for get dynamic ");
		System.out.println("Staring job" +LocalDateTime.now());
		Set<String> ddocPid = new HashSet<>();
		List<FilledForm> filledForms = new ArrayList<>();
		List<DynamicDocumentHeaderDTO> dynamicDocumentHeaderDTOS = new ArrayList<>();
		if (fromDate != null && toDate != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate frdate = LocalDate.parse(fromDate, formatter);
			LocalDate todate = LocalDate.parse(toDate, formatter);
			LocalDateTime fromdate1 = frdate.atTime(0, 0);
			LocalDateTime todate1 = todate.atTime(23, 59);
			System.out.println("Date Range From" +fromdate1+" To " + todate1);
			System.out.println("Staring job fetch db" +LocalDateTime.now());
			List<DynamicDocumentHeader> dynamicDocumentHeader = dynamicDocumentHeaderRepository.findAllByCompanyIdAndDateBetweenOrderByDocumentDateDesc(fromdate1,todate1);
			System.out.println("size of DynamicDocument"+ dynamicDocumentHeader.size());
			System.out.println("Complete job fetch db" +LocalDateTime.now());
			dynamicDocumentHeader.forEach(data -> ddocPid.add(data.getPid()));

			if (!ddocPid.isEmpty()) {
				System.out.println("Staring job fetch db filldform" +LocalDateTime.now());
				List<Object[]> fildformId = filledFormRepository.findByAndDynamicPidDynamicDocumentHeaderPidsIn(ddocPid);
				System.out.println("size of fildform"+ fildformId.size());
				System.out.println("Staring job fetch db filldformDetails" +LocalDateTime.now());
				Set<String> StringPids = fildformId.stream().map(data -> (String) data[0]).collect(Collectors.toSet());
				List<Object[]> fildFormDetails = filledFormDetailRepository.findAllByFormPidIn(StringPids);
				System.out.println("Complete job fetch db Details" +LocalDateTime.now());
				System.out.println("Staring job build  DDList" +LocalDateTime.now());
				List<DynamicDocumentHeader> dynamicdocumets = getDynamicDocument(dynamicDocumentHeader, fildformId, fildFormDetails);
				System.out.println("compleate job build  DDList" +LocalDateTime.now());
				System.out.println("Staring job convert object to DTO in header" +LocalDateTime.now());
				List<DynamicDocumentHeaderDTO> ddhDTOs = dynamicdocumets.stream().map(data -> new DynamicDocumentHeaderDTO(data,data.getCompany())).collect(Collectors.toList());
				System.out.println("Complete job convert object to DTO in header" +LocalDateTime.now());
				System.out.println("list Size ==" +ddhDTOs.size());
				System.out.println("time Before streaming" +LocalDateTime.now());
				System.out.println("time After streaming" +LocalDateTime.now());
				ddhDTOs.forEach(ivhDTO -> eventProducer.dynamicDocumentStreamingPublish(ivhDTO));
			}
		}
	}

	@GetMapping("/accounting-sync")
	@Timed
	public @ResponseBody void findAccountingVoucherHeaders(
			@RequestParam(required = false, value = "fromDate") String fromDate,
			@RequestParam(required = false, value = "toDate") String toDate) {
		log.debug("request for get accounting-order-status tert ");
		System.out.println("Staring job" +LocalDateTime.now());
		System.out.println("Date Range From" +fromDate+" To " + toDate );
		Set<String> accPid = new HashSet<>();
		List<AccountingVoucherDetail> accountingVoucherDetails = new ArrayList<>();
		List<DynamicDocumentHeaderDTO> dynamicDocumentHeaderDTOS = new ArrayList<>();
		if (fromDate != null && toDate != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate frdate = LocalDate.parse(fromDate, formatter);
			LocalDate todate = LocalDate.parse(toDate, formatter);
			LocalDateTime fromdate1 = frdate.atTime(0, 0);
			LocalDateTime todate1 = todate.atTime(23, 59);
			System.out.println("Staring job fetch db" +LocalDateTime.now());
			List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository.findAllByCompanyIdAndDateBetweenOrderByCreatedDateDes(fromdate1, todate1);
			System.out.println("Complete job fetch db" +LocalDateTime.now());
			accountingVoucherHeaders.forEach(data -> accPid.add(data.getPid()));

			if (!accPid.isEmpty()) {
				System.out.println("Staring job fetch db Details" +LocalDateTime.now());
				accountingVoucherDetails=accountingVoucherDetailRepository.findAllByAccountingVoucherHeaderPidIn(accPid);
				System.out.println("Complete job fetch db Details" +LocalDateTime.now());
			}
			System.out.println("Staring job set  Details in header" +LocalDateTime.now());
			for(AccountingVoucherHeader acc1 : accountingVoucherHeaders) {
				List<AccountingVoucherDetail> accountingVoucherDetails1 = new ArrayList<>();
				for(AccountingVoucherDetail accd : accountingVoucherDetails) {
					if(acc1.getId().equals(accd.getAccountingVoucherHeader().getId())) {
						accountingVoucherDetails1.add(accd);
						acc1.setAccountingVoucherDetails(accountingVoucherDetails1);
					}
				}
			}
			System.out.println("Complete job  set Details" +LocalDateTime.now());
			System.out.println("Staring job convert object to DTO in header" +LocalDateTime.now());
			List<AccountingVoucherHeaderDTO> accDTOs = accountingVoucherHeaders.stream().map(data -> new AccountingVoucherHeaderDTO(data.getCompany(), data)).collect(Collectors.toList());
			System.out.println("Complete job convert object to DTO in header" +LocalDateTime.now());
			System.out.println("list Size ==" +accDTOs.size());
			System.out.println("time Before streaming" +LocalDateTime.now());
			System.out.println("time After streaming" +LocalDateTime.now());
			accDTOs.forEach(ivhDTO -> eventProducer.accountingVoucherStreamingPublish(ivhDTO));
		}
	}
	@GetMapping("/employee-sync")
	@Timed
	public @ResponseBody void findCompanyEmployee() {
		log.debug("request for get All CompanyEmployee");
		System.out.println("Staring job" +LocalDateTime.now());

		List<EmployeeProfile> employees = employeeProfileRepository.findAllByCompanyId(true);
		System.out.println("Staring data size" +employees);
		List<PunchingUserDTO> punchingUserDTOS = new ArrayList<>();
		if (!(employees == null)){
			employees.stream().forEach(employee -> {
				PunchingUserDTO punchingUserDTO = new PunchingUserDTO();
				punchingUserDTO.setCompanyId(employee.getCompany().getId());
				punchingUserDTO.setUserPid(employee.getUser().getPid());
				punchingUserDTO.setUserName(employee.getName());
				punchingUserDTO.setEmployeeName(employee.getName());
				punchingUserDTO.setEmployeePid(employee.getPid());
				punchingUserDTOS.add(punchingUserDTO);
			});
		}
		System.out.println("time Before streaming" +LocalDateTime.now());
		if (!punchingUserDTOS.isEmpty()){
			punchingUserDTOS.forEach(Edto -> eventProducer.employeeStreamPublish(Edto));
		}
		System.out.println("list Size ==" +punchingUserDTOS.size());

		System.out.println("time After streaming" +LocalDateTime.now());
	}

	private List<DynamicDocumentHeader> getDynamicDocument(List<DynamicDocumentHeader> dynamicDocumentHeaders,List<Object[]> filledForm, List<Object[]> filledFormDetails){
		List<DynamicDocumentHeader> dynamicDocumentHeaders1 = new ArrayList<>();
		dynamicDocumentHeaders.stream().forEach(ddh -> {
			List<FilledForm> filledForms = new ArrayList<>();
			filledForm.stream().forEach(ff -> {
				if(ddh.getId().toString().equals(ff[1].toString())){
					FilledForm filledForm1 = new FilledForm();
					filledForm1.setPid(ff[0].toString());
					List<FilledFormDetail> filledFormDetails1 = new ArrayList<>();
					filledFormDetails.stream().forEach(ffd -> {
						if (ffd[2].toString().equals(filledForm1.getPid())){
							FilledFormDetail filledFormDetail = new FilledFormDetail();
							FormElement formElement = new FormElement();
							formElement.setName(ffd[3] == null ? null :ffd[3].toString());
							filledFormDetail.setFormElement(formElement);
							filledFormDetail.setValue(ffd[4] == null ? null : ffd[4].toString());
							filledFormDetails1.add(filledFormDetail);
						}
					});
					filledForm1.setFilledFormDetails(filledFormDetails1);
					filledForms.add(filledForm1);
				}
			});
			ddh.setFilledForms(filledForms);
			dynamicDocumentHeaders1.add(ddh);
		});
		return dynamicDocumentHeaders1;
	}
}
