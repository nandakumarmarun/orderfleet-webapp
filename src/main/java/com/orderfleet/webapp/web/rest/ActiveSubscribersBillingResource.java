package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderfleet.webapp.domain.BillingJSCode;
import com.orderfleet.webapp.domain.CompanyBilling;
import com.orderfleet.webapp.domain.Slab;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.BillingPeriod;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.impl.CompanyBillingServiceImpl;
import com.orderfleet.webapp.web.rest.dto.ActiveSubscribersBillingDTO;
import com.orderfleet.webapp.web.rest.dto.BillingDetail;
import com.orderfleet.webapp.web.rest.dto.BillingUserDTO;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web")
public class ActiveSubscribersBillingResource {

	private final Logger log = LoggerFactory.getLogger(ActiveSubscribersBillingResource.class);

	
	@Inject
	private CompanyBillingRepository companyBillingRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private SlabRepository slabRepository;

	@Inject
	private BillingJsCodeRepository billingJsCodeRepository;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private AttendanceRepository attendanceRepository;

	@RequestMapping(value = "/subscribers-billing", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	@Secured({ AuthoritiesConstants.SITE_ADMIN, AuthoritiesConstants.PARTNER })
	public String getAllCompanies(Model model) throws URISyntaxException {
		log.info("Web Request to get company billing Details");

		return "site_admin/activeSubscriberBilling";
	}

	@RequestMapping(value = "/subscribers-billing/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<ActiveSubscribersBillingDTO>> filterActiveSubscribersBillingList(
			@RequestParam("billingPeriod") String billingPeriod, @RequestParam LocalDate fromDate,
			@RequestParam LocalDate toDate)
			throws JsonParseException, JsonMappingException, ScriptException, IOException {

		return new ResponseEntity<>(createBillReport(billingPeriod, fromDate, toDate), HttpStatus.OK);
	}

	private List<ActiveSubscribersBillingDTO> createBillReport(String billingPeriod, LocalDate fromDate,
			LocalDate toDate) throws JsonParseException, JsonMappingException, ScriptException, IOException {
		log.info("web request to show the active subscribers billing details");

		log.info(fromDate + " " + toDate);
		List<CompanyBilling> billingSetting = new ArrayList<>();
		if (billingPeriod.equalsIgnoreCase("no")) {
			billingSetting = companyBillingRepository.findCompanyBillingByDateBetween(fromDate, toDate);
		} else {
			billingSetting = companyBillingRepository
					.findBybillingPeriodAndDateBetween(BillingPeriod.valueOf(billingPeriod), fromDate, toDate);
		}

		log.debug("billSeting Size :",String.valueOf(billingSetting.size()));
		List<Long> companyId = new ArrayList<>();
		if (billingSetting.size() > 0) {
			companyId = billingSetting.stream().map(bill -> bill.getCompany().getId()).collect(Collectors.toList());
		}

		List<User> user = new ArrayList<>();
		List<Slab> slabList = new ArrayList<>();
		if (companyId.size() > 0) {
			user = userRepository.findAllByCompanyIdInAndActivated(companyId);
			slabList = slabRepository.findAllByCompanyIdIn(companyId);
		}

		Map<String, Integer> slabTotal = new HashMap<String, Integer>();

		List<ActiveSubscribersBillingDTO> billingDTO = new ArrayList<>();
		for (CompanyBilling compBill : billingSetting) {
			
			ActiveSubscribersBillingDTO subscriberBill = new ActiveSubscribersBillingDTO();
			subscriberBill.setPid(compBill.getPid());
			subscriberBill.setCompanyPid(compBill.getCompany().getPid());
			subscriberBill.setCompanyName(compBill.getCompany().getLegalName());
			subscriberBill.setNoOfMonth(compBill.getNoOfMonths());
			subscriberBill.setFromDate(compBill.getLastBilledDate());
			subscriberBill.setToDate(compBill.getNext_bill_date());
			billingDTO.add(subscriberBill);

		}
		return billingDTO;
	}

	public Map<String, Integer> calculateBillAmount(int count, List<Slab> slabls, Long companyId)
			throws ScriptException, JsonParseException, JsonMappingException, IOException {
		log.debug("request to get js");
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("nashorn");

		System.out.println("slabs" + slabls);

		BillingJSCode billingJSCode = billingJsCodeRepository.findByCompanyId(companyId);

		Object salbrate = null;
		String slabDetail = null;
		if (billingJSCode != null) {
			engine.eval(billingJSCode.getJsCode());

			Invocable invocable = (Invocable) engine;
			try {
				salbrate = invocable.invokeFunction("calculateSlabRate", slabls, count);
			} catch (ScriptException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		 slabDetail = salbrate.toString();
		}
		else {
			Map<String, Integer> map = Collections.emptyMap();
			return  map;
		}

		ObjectMapper objectMapper = new ObjectMapper();

		Map<String, Integer> map = objectMapper.readValue(slabDetail, new TypeReference<Map<String, Integer>>() {
		});
	
		return map;
	}



	@RequestMapping(value = "/subscribers-billing/details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<ActiveSubscribersBillingDTO> detailsview(@RequestParam("companyPid") String companyPid,@RequestParam("billingSettingPid") String billingSettingPid) throws ScriptException, IOException {
		log.debug("request to get details...");
		List<BillingDetail> details = new ArrayList<>();
		Map<String, Integer> slabTotal = new HashMap<String, Integer>();
		List<Slab> slabList = new ArrayList<>();
		double totalAmount = 0;
		ActiveSubscribersBillingDTO billingDTO = new ActiveSubscribersBillingDTO();

		Optional<CompanyBilling> optBillingSetting = companyBillingRepository.findOneByPid(billingSettingPid);

		if(optBillingSetting.isPresent()){
			log.debug("billsetting present");
			CompanyBilling billingSetting = new CompanyBilling();
			billingSetting = optBillingSetting.get();
			Long companyId = billingSetting.getCompany().getId();
			System.out.println("billsetting present : "+companyId.toString());
			slabList = slabRepository.findAllByCompanyId(companyId);
			System.out.println("billsetting present : "+slabList.size());

			List<Slab> slabs = slabList.stream()
					.filter(slb -> slb.getCompany().getId().equals(companyId))
					.collect(Collectors.toList());


			if (slabs.size() > 0) {
				slabs.stream().sorted(Comparator.comparingInt(Slab::getMinimumUser)).collect(Collectors.toList());

				int usercount = getTransactionDetailsByCompany(billingSetting.getCompany().getPid(),billingSetting.getLastBilledDate(),billingSetting.getNext_bill_date());
				log.debug("billsetting slabList Size : ",usercount);
				billingDTO.setCountOfActiveUser(usercount);
				billingDTO.setCompanyName(billingSetting.getCompany().getLegalName());

				slabTotal = calculateBillAmount(usercount, slabs, billingSetting.getCompany().getId());
				System.out.println(slabTotal);
				for (Map.Entry<String, Integer> entry : slabTotal.entrySet()) {
					BillingDetail billDetail = new BillingDetail();
					String key = entry.getKey();
					Integer value = entry.getValue();
					System.out.println("Key: " + key + ", Value: " + value);
					if (!key.equalsIgnoreCase("total")) {
						Slab slabdata = slabs.stream().filter(sl -> sl.getId().equals(Long.valueOf(key))).findAny()
								.get();
						billDetail.setSlabName(slabdata.getMinimumUser() + "-" + slabdata.getMaximumUser());
						billDetail.setSlabRate(Double.valueOf(value));
						details.add(billDetail);
					} else {
						totalAmount = (Double.valueOf(value) * Double.valueOf(billingSetting.getNoOfMonths()));
						billDetail.setTotalAmount(totalAmount );
						billDetail.setSlabName("Total");
						billDetail.setSlabRate(totalAmount);
						details.add(billDetail);
					}
				}
				billingDTO.setBillingDetail(details);
			}

		}

		return new ResponseEntity<>(billingDTO, HttpStatus.OK);
	}

	public Integer getTransactionDetailsByCompany(String companyPid, LocalDate fromDate, LocalDate toDate)
	{
		log.debug("Web request to Get Users by companyPid: {}", companyPid);
		log.debug("Date format : {}", fromDate);
		log.debug("Date format : {}", toDate);
		int ExicutionCount = 0;

		LocalDateTime startDate = fromDate.atTime(0, 0);
		LocalDateTime endDate = toDate.atTime(23, 59);
		List<User> users = userRepository.findAllByCompanyPidSortedByName(companyPid);
		if (users.isEmpty()) {
			return null;
		}
		List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
		List<String> executiveTaskExecutions = executiveTaskExecutionRepository
				.findUserByCompanyPidAndUserIdInAndDateBetweenOrderByDateDesc(userIds, startDate, endDate);

		List<String> attentances = attendanceRepository.findByCompanyPidAndUserPidInAndDateBetween(userIds, startDate,
				endDate);
		Map<String, Long> executionMap = null;
		Map<String, Long> attendenceMap = null;

		if (!executiveTaskExecutions.isEmpty()) {
			executionMap = executiveTaskExecutions.stream()
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		}
		if (!attentances.isEmpty()) {
			attendenceMap = attentances.stream()
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		}

		List<BillingUserDTO> billingUserDtos = new ArrayList<>();
		for (User u : users) {
			BillingUserDTO billDTO = new BillingUserDTO();
			billDTO.setLogin(u.getLogin());
			if (executionMap != null) {
				billDTO.setExecutionCount(executionMap.getOrDefault(u.getLogin(), 0L));
			}
			if (attendenceMap != null) {
				billDTO.setAttendanceCount(attendenceMap.getOrDefault(u.getLogin(), 0L));
			}
			billingUserDtos.add(billDTO);
		}
		log.debug("usercount : "+billingUserDtos.size());
		for(BillingUserDTO billDTO : billingUserDtos){
			boolean countstatus = false;
			if(billDTO.getExecutionCount() != null ){
				if (billDTO.getExecutionCount() > 0) {
					countstatus = true;

				}
			}
			if(billDTO.getAttendanceCount() != null){
				if(billDTO.getAttendanceCount() > 0){
					countstatus = true;
				}
			}
			if(countstatus == true ){
				ExicutionCount += 1;
			}
		}
		return ExicutionCount;
	}
	
	@RequestMapping(value = "/subscribers-billing/download-xls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadBillingXls(@RequestParam("billingSettingPids") String[] billingSettingPids,
			HttpServletResponse response) throws JsonParseException, JsonMappingException, ScriptException, IOException {

		log.debug("request to get details...");
		List<ActiveSubscribersBillingDTO> subscriberBilling = new ArrayList<>();
		Map<String, Integer> slabTotal = new HashMap<String, Integer>();
		List<Slab> slabList = new ArrayList<>();
		double totalAmount = 0;

		List<CompanyBilling> optBillingSetting = companyBillingRepository.findOneByPidIn(billingSettingPids);

		for(CompanyBilling billingSetting: optBillingSetting){
			
			List<BillingDetail> details = new ArrayList<>();
			ActiveSubscribersBillingDTO billingDTO = new ActiveSubscribersBillingDTO();
			log.debug("billsetting present");
			
			Long companyId = billingSetting.getCompany().getId();
			System.out.println("billsetting company: "+billingSetting.getCompany().getLegalName());
			slabList = slabRepository.findAllByCompanyId(companyId);
			

			List<Slab> slabs = slabList.stream()
					.filter(slb -> slb.getCompany().getId().equals(companyId))
					.collect(Collectors.toList());
          System.out.println("slabSize:"+slabs.size());

          billingDTO.setCompanyName(billingSetting.getCompany().getLegalName());
          billingDTO.setFromDate(billingSetting.getLastBilledDate());
          billingDTO.setToDate(billingSetting.getNext_bill_date());
          billingDTO.setNoOfMonth(billingSetting.getNoOfMonths());
			if (slabs.size() > 0) {
				slabs.stream().sorted(Comparator.comparingInt(Slab::getMinimumUser)).collect(Collectors.toList());

				int usercount = getTransactionDetailsByCompany(billingSetting.getCompany().getPid(),billingSetting.getLastBilledDate(),billingSetting.getNext_bill_date());
				log.debug("active User count: ",+usercount);
				billingDTO.setCountOfActiveUser(usercount);
				slabTotal = calculateBillAmount(usercount, slabs, billingSetting.getCompany().getId());
				System.out.println("slabTotal :"+slabTotal);
				for (Map.Entry<String, Integer> entry : slabTotal.entrySet()) {
					System.out.println("Enterd to for loop:");
					BillingDetail billDetail = new BillingDetail();
					String key = entry.getKey();
					Integer value = entry.getValue();
					System.out.println("Key: " + key + ", Value: " + value);
					if (!key.equalsIgnoreCase("total")) {
						System.out.println("enter into the if loop");
						Slab slabdata = slabs.stream().filter(sl -> sl.getId().equals(Long.valueOf(key))).findAny()
								.get();
						billDetail.setSlabName(slabdata.getMinimumUser() + "-" + slabdata.getMaximumUser());
						billDetail.setSlabRate(Double.valueOf(value));
						totalAmount = (Double.valueOf(value) * Double.valueOf(billingSetting.getNoOfMonths()));
						billDetail.setTotalAmount(totalAmount );
						details.add(billDetail);
					
						billingDTO.setBillingDetail(details);
					}

				}
				
				System.out.println("SubscriberBilling :"+billingDTO.toString());
			}
			subscriberBilling.add(billingDTO);
		}

		
	
		buildExcelDocument(subscriberBilling, response);
	}

	private void buildExcelDocument(List<ActiveSubscribersBillingDTO> subscriberBilling, HttpServletResponse response) {

		log.debug("Downloading Excel report");
		String excelFileName = "Subscriber Billing" + ".xls";
		String sheetName = "Sheet1";
		String[] headerColumns = {"CompanyName","From Date","To Date","No of Month","Active Users","SlabName","Slab Rate","Total"};
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet worksheet = workbook.createSheet(sheetName);
			createHeaderRow(worksheet, headerColumns);
			createReportRows(worksheet, subscriberBilling);
			// Resize all columns to fit the content size
			for (int i = 0; i < headerColumns.length; i++) {
				worksheet.autoSizeColumn(i);
			}
			response.setHeader("Content-Disposition", "inline; filename=" + excelFileName);
			response.setContentType("application/vnd.ms-excel");
			// Writes the report to the output stream
			ServletOutputStream outputStream = response.getOutputStream();
			worksheet.getWorkbook().write(outputStream);
			outputStream.flush();
		} catch (IOException ex) {
			log.error("IOException on downloading Subscriber billing {}", ex.getMessage());
		}
	
		
	}

	private void createReportRows(HSSFSheet worksheet, List<ActiveSubscribersBillingDTO> subscriberBilling) {
		// TODO Auto-generated method stub
		HSSFCreationHelper createHelper = worksheet.getWorkbook().getCreationHelper();
		// Create Cell Style for formatting Date
		HSSFCellStyle dateCellStyle = worksheet.getWorkbook().createCellStyle();
		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy hh:mm:ss"));
		// Create Other rows and cells with Sales data
		int rowNum = 1;
		for(ActiveSubscribersBillingDTO billing:subscriberBilling)
		{
			
			System.out.println("Enter here first:"+billing.getCompanyName());
				HSSFRow row = worksheet.createRow(rowNum++);
				
				row.createCell(0).setCellValue(billing.getCompanyName());
				HSSFCell docDateCell = row.createCell(1);
				docDateCell.setCellValue(billing.getFromDate().toString());
				docDateCell.setCellStyle(dateCellStyle);
				HSSFCell docDateCell1 = row.createCell(2);
				docDateCell1.setCellValue(billing.getToDate().toString());
				docDateCell1.setCellStyle(dateCellStyle);
				row.createCell(3).setCellValue(billing.getNoOfMonth());
				if(billing.getCountOfActiveUser() == null)
				{
					row.createCell(4).setCellValue(0);	
				}
				else {
				row.createCell(4).setCellValue(billing.getCountOfActiveUser());
				}
				if(billing.getBillingDetail() != null)
				{
				for(BillingDetail detail: billing.getBillingDetail())
				{
					System.out.println("Enter here second ");
				row.createCell(5).setCellValue(detail.getSlabName());

				row.createCell(6).setCellValue(detail.getSlabRate());
				row.createCell(7).setCellValue(detail.getTotalAmount());
				
				}}
		}
		
	}

	private void createHeaderRow(HSSFSheet worksheet, String[] headerColumns) {
		// TODO Auto-generated method stub

		// Create a Font for styling header cells
		Font headerFont = worksheet.getWorkbook().createFont();
		headerFont.setFontName("Arial");
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.RED.getIndex());
		// Create a CellStyle with the font
		HSSFCellStyle headerCellStyle = worksheet.getWorkbook().createCellStyle();
		headerCellStyle.setFont(headerFont);
		// Create a Row
		HSSFRow headerRow = worksheet.createRow(0);
		// Create cells
		for (int i = 0; i < headerColumns.length; i++) {
			HSSFCell cell = headerRow.createCell(i);
			cell.setCellValue(headerColumns[i]);
			cell.setCellStyle(headerCellStyle);
		}
	
	}
	
}
