package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.CachedGauge;
import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.*;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.InvoiceWiseReportView;
import com.orderfleet.webapp.web.rest.dto.LeadStatusDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/web")
@Controller
public class LeadStatusAnalyticsResource {

    private final Logger log = LoggerFactory.getLogger(LeadStatusAnalyticsResource.class);

    @Inject
    private EmployeeHierarchyService employeeHierarchyService;
    @Inject
    private DocumentService documentService;
    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private EmployeeProfileService employeeProfileService;
    @Inject
    private AccountProfileService accountProfileService;

    @Inject
    private LocationAccountProfileService locationAccountProfileService;

    @Inject
    private EmployeeProfileRepository employeeProfileRepository;

    @Inject
    private InvoiceDetailsDenormalizedRepository invoiceDetailsDenormalizedRepository;
    @Inject
    private DocumentRepository documentRepository;

    @Inject
    private UserRepository userRepository;
    @Inject
    private DashboardUserRepository dashboardUserRepository;

    @Inject
    private  EmployeeProfileLocationRepository employeeProfileLocationRepository;
    @Inject
    private LocationAccountProfileRepository locationAccountProfileRepository;
    @Inject
    private AccountProfileRepository accountProfileRepository;

    @Inject
    private AccountProfileMapper accountProfileMapper;


    @RequestMapping(value = "/lead-status-analytics", method = RequestMethod.GET)
    @Timed
    @Transactional(readOnly = true)
    public String getAllLeadStatus(Pageable pageable, Model model) {
        // user under current user
        List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
        log.info("SIze ...:" + userIds.size());
        model.addAttribute("dynamicdocuments", documentService.findAllByDocumentType(DocumentType.DYNAMIC_DOCUMENT));

        if (userIds.isEmpty()) {
            model.addAttribute("accounts", accountProfileService.findAllByCompany());

        } else {
//            model.addAttribute("accounts", locationAccountProfileService.findAccountProfilesByCurrentUserLocations());
            model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));

            Long currentUserId = userRepository.getIdByLogin(SecurityUtils.getCurrentUserLogin());
            userIds.add(currentUserId);
            Set<Long> locationIds = employeeProfileLocationRepository.findLocationIdsByUserIdIn(userIds);
            Set<Long> accountProfileIds = locationAccountProfileRepository
                    .findAccountProfileIdsByUserLocationsOrderByAccountProfilesNameAndActivated(locationIds);
            List<AccountProfile> accountProfiles = accountProfileRepository
                    .findAllByCompanyIdAndIdsIn(accountProfileIds);
            List<AccountProfile> result = accountProfiles.parallelStream().distinct().collect(Collectors.toList());

            List<AccountProfileDTO> accountProfileDTOs = accountProfileMapper
                    .accountProfilesToAccountProfileDTOs(result);

            model.addAttribute("accounts", accountProfileDTOs);
        }
        return "company/LeadStatusAnalytics";
    }

    @RequestMapping(value = "/lead-status-analytics/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<LeadStatusDTO>> filterExecutiveTaskExecutions(
            @RequestParam("employeePid") String employeePid,
            @RequestParam("accountPid") String accountPid, @RequestParam("documentPid") String documentPid,
            @RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate,
            @RequestParam boolean inclSubordinate) {
        log.info("Web request to filter Lead Status");

        List<LeadStatusDTO> executiveTaskExecutions = new ArrayList<>();
        if (filterBy.equals("TODAY")) {
            executiveTaskExecutions = getFilterData(employeePid, documentPid, accountPid, LocalDate.now(),
                    LocalDate.now(), inclSubordinate);
        } else if (filterBy.equals("YESTERDAY")) {
            LocalDate yeasterday = LocalDate.now().minusDays(1);
            executiveTaskExecutions = getFilterData(employeePid, documentPid, accountPid, yeasterday,
                    yeasterday, inclSubordinate);
        } else if (filterBy.equals("WTD")) {
            TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
            LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
            executiveTaskExecutions = getFilterData(employeePid, documentPid, accountPid, weekStartDate,
                    LocalDate.now(), inclSubordinate);
        } else if (filterBy.equals("MTD")) {
            LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
            executiveTaskExecutions = getFilterData(employeePid, documentPid, accountPid, monthStartDate,
                    LocalDate.now(), inclSubordinate);
        } else if (filterBy.equals("CUSTOM")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
            LocalDate toFateTime = LocalDate.parse(toDate, formatter);
            executiveTaskExecutions = getFilterData(employeePid, documentPid, accountPid, fromDateTime,
                    toFateTime, inclSubordinate);
        } else if (filterBy.equals("SINGLE")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
            executiveTaskExecutions = getFilterData(employeePid, documentPid, accountPid, fromDateTime,
                    fromDateTime, inclSubordinate);
        }
        return new ResponseEntity<>(executiveTaskExecutions, HttpStatus.OK);
    }

    private List<LeadStatusDTO> getFilterData(String employeePid, String documentPid, String accountPid, LocalDate fDate, LocalDate tDate, boolean inclSubordinate) {

        LocalDateTime fromDate = fDate.atTime(0, 0);
        LocalDateTime toDate = tDate.atTime(23, 59);
        log.info("Employeepid :" + employeePid);
        List<Long> userIds = getUserIdsUnderCurrentUser(employeePid, inclSubordinate);

        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<InvoiceDetailsDenormalized> invoiceDetailsDenormalizedList = new ArrayList<>();
        if (accountPid.equalsIgnoreCase("no")) {
            invoiceDetailsDenormalizedList = invoiceDetailsDenormalizedRepository.getByCreatedDateDocumentPidAndUserIdsIn(fromDate, toDate, documentPid, userIds);
        } else {
            invoiceDetailsDenormalizedList = invoiceDetailsDenormalizedRepository.getByCreatedDateDocumentPidAndAccountPidAndUserIdsIn(fromDate, toDate, documentPid, accountPid, userIds);
        }
        log.info("Size of Invoice :" + invoiceDetailsDenormalizedList.size());

        Map<String, List<InvoiceDetailsDenormalized>> groupedByName = invoiceDetailsDenormalizedList.stream()
                .collect(Collectors.groupingBy(InvoiceDetailsDenormalized::getAccountProfilePid));

        // Get the persons with the greatest created date for each group
        Map<String, InvoiceDetailsDenormalized> lastCreatedByAccount = groupedByName.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .max(Comparator.comparing(InvoiceDetailsDenormalized::getCreatedDate))
                                .orElse(null)
                ));
        List<LeadStatusDTO> leadStatusDTOList = new ArrayList<>();
        List<InvoiceDetailsDenormalized> invoiceDetailsList = new ArrayList<>();
        for (Map.Entry<String, InvoiceDetailsDenormalized> entry : lastCreatedByAccount.entrySet()) {
            String key = entry.getKey();
            log.info("Name:" + key);
            InvoiceDetailsDenormalized value = entry.getValue();
            invoiceDetailsList = invoiceDetailsDenormalizedRepository.findAllByAccountProfilePidAndExecutionPidAndDocumentPid(key, value.getExecutionPid(), value.getDocumentPid());

            log.info("Size with date :" + invoiceDetailsList.size());
            LeadStatusDTO leadStatusDTO = new LeadStatusDTO();
            leadStatusDTO.setPid(value.getPid());
            leadStatusDTO.setEmployeeName(value.getEmployeeName());
            leadStatusDTO.setAccountName(value.getAccountProfileName());
            leadStatusDTO.setCreatedDate(value.getCreatedDate());
            leadStatusDTO.setDocumentName(value.getDocumentName());
            for (InvoiceDetailsDenormalized invoice : invoiceDetailsList) {
                System.out.println("Form name :" + invoice.getFormElementName());
                if (invoice.getFormElementName().equalsIgnoreCase("Lead Status")) {
                    leadStatusDTO.setLeadStatus(invoice.getValue() != null ? invoice.getValue() : "");
                }

                if (invoice.getFormElementName().equalsIgnoreCase("Deal Volume")) {
                    leadStatusDTO.setDealVolume(invoice.getValue() != null ? Double.parseDouble(invoice.getValue()) : 0);
                }

                if (invoice.getFormElementName().equalsIgnoreCase("Won volume")) {
                    leadStatusDTO.setWonVolume(invoice.getValue() != null ? Double.parseDouble(invoice.getValue()) : 0);
                }


                if (invoice.getFormElementName().equalsIgnoreCase("Lost volume")) {
                    leadStatusDTO.setLostVolume(invoice.getValue() != null ?Double.parseDouble( invoice.getValue()) : 0);
                }
                if (invoice.getFormElementName().equalsIgnoreCase("Balance Deal Volume")) {
                    leadStatusDTO.setBalanceDealVolume(invoice.getValue() != null ? Double.parseDouble(invoice.getValue()) : 0);
                }


            }
            leadStatusDTOList.add(leadStatusDTO);
        }
        leadStatusDTOList.sort(Comparator.comparing(LeadStatusDTO::getCreatedDate).reversed());
        return leadStatusDTOList;

    }

    @RequestMapping(value = "/lead-status-analytics/downloadRawData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Timed
    @Transactional(readOnly = true)
    public <documentPid> void downloadExecutiveTaskExecutions(
            @RequestParam("employeePid") String employeePid, @RequestParam("documentPid") String documentPid,
            @RequestParam("accountPid") String accountPid,
            @RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate,
            @RequestParam boolean inclSubordinate, HttpServletResponse response) {

        List<LeadStatusDTO> executiveTaskExecutions = new ArrayList<>();

        if (filterBy.equals("TODAY")) {
            executiveTaskExecutions = getFilterData(employeePid, documentPid, accountPid, LocalDate.now(),
                    LocalDate.now(), inclSubordinate);
        } else if (filterBy.equals("YESTERDAY")) {
            LocalDate yeasterday = LocalDate.now().minusDays(1);
            executiveTaskExecutions = getFilterData(employeePid, documentPid, accountPid, yeasterday,
                    yeasterday, inclSubordinate);
        } else if (filterBy.equals("WTD")) {
            TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
            LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
            executiveTaskExecutions = getFilterRawData(employeePid, documentPid, accountPid, weekStartDate,
                    LocalDate.now(), inclSubordinate);
        } else if (filterBy.equals("MTD")) {
            LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
            executiveTaskExecutions = getFilterRawData(employeePid, documentPid, accountPid, monthStartDate,
                    LocalDate.now(), inclSubordinate);
        } else if (filterBy.equals("CUSTOM")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
            LocalDate toFateTime = LocalDate.parse(toDate, formatter);
            executiveTaskExecutions = getFilterRawData(employeePid, documentPid, accountPid, fromDateTime,
                    toFateTime, inclSubordinate);
        } else if (filterBy.equals("SINGLE")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
            executiveTaskExecutions = getFilterRawData(employeePid, documentPid, accountPid, fromDateTime,
                    fromDateTime, inclSubordinate);
        }


        buildExcelDocument(executiveTaskExecutions, response);
    }

    private List<LeadStatusDTO> getFilterRawData(String employeePid, String documentPid, String accountPid, LocalDate fDate, LocalDate tDate, boolean inclSubordinate) {
        LocalDateTime fromDate = fDate.atTime(0, 0);
        LocalDateTime toDate = tDate.atTime(23, 59);

        List<Long> userIds = getUserIdsUnderCurrentUser(employeePid, inclSubordinate);

        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<InvoiceDetailsDenormalized> invoiceDetailsDenormalizedList = new ArrayList<>();
        if (accountPid.equalsIgnoreCase("no")) {
            invoiceDetailsDenormalizedList = invoiceDetailsDenormalizedRepository.getByCreatedDateDocumentPidAndUserIdsIn(fromDate, toDate, documentPid, userIds);
        } else {
            invoiceDetailsDenormalizedList = invoiceDetailsDenormalizedRepository.getByCreatedDateDocumentPidAndAccountPidAndUserIdsIn(fromDate, toDate, documentPid, accountPid, userIds);
        }
        log.info("Size of Invoice :" + invoiceDetailsDenormalizedList.size());

        // Get the persons with the greatest created date for each group
        List<LeadStatusDTO> leadStatusDTOList = new ArrayList<>();

        Map<String, InvoiceDetailsDenormalized> uniqueMap = invoiceDetailsDenormalizedList.stream()
                .collect(Collectors.groupingBy(InvoiceDetailsDenormalized::getDocumentNumberLocal,
                        Collectors.collectingAndThen(Collectors.toList(), list -> list.get(0))));
        for (Map.Entry<String, InvoiceDetailsDenormalized> entry : uniqueMap.entrySet()) {
            String key = entry.getKey();
            log.info("Name:" + key);
            InvoiceDetailsDenormalized invoices = entry.getValue();
            LeadStatusDTO leadStatusDTO = new LeadStatusDTO();
            leadStatusDTO.setPid(invoices.getPid());
            leadStatusDTO.setEmployeeName(invoices.getEmployeeName());
            leadStatusDTO.setAccountName(invoices.getAccountProfileName());
            leadStatusDTO.setCreatedDate(invoices.getCreatedDate());
            leadStatusDTO.setDocumentName(invoices.getDocumentName());
            List<InvoiceDetailsDenormalized> invoiceDetailsDenormalized = invoiceDetailsDenormalizedRepository.findAllByExecutionPid(invoices.getExecutionPid());
            for (InvoiceDetailsDenormalized invoice : invoiceDetailsDenormalized) {

                if (invoice.getFormElementName().equalsIgnoreCase("Lead Status")) {
                    leadStatusDTO.setLeadStatus(invoice.getValue() != null ? invoice.getValue() : "");
                }
                if (invoice.getFormElementName().equalsIgnoreCase("Deal Volume")) {
                    leadStatusDTO.setDealVolume(invoice.getValue() != null ? Double.parseDouble(invoice.getValue() ): 0);
                }
                if (invoice.getFormElementName().equalsIgnoreCase("Won volume")) {
                    leadStatusDTO.setWonVolume(invoice.getValue() != null ? Double.parseDouble(invoice.getValue() ): 0);
                }
                if (invoice.getFormElementName().equalsIgnoreCase("Lost volume")) {
                    leadStatusDTO.setLostVolume(invoice.getValue() != null ? Double.parseDouble(invoice.getValue()) : 0);
                }
                if (invoice.getFormElementName().equalsIgnoreCase("Balance Deal Volume")) {
                    leadStatusDTO.setBalanceDealVolume(invoice.getValue() != null ? Double.parseDouble(invoice.getValue()) : 0);
                }
            }
            leadStatusDTOList.add(leadStatusDTO);

        }
        leadStatusDTOList.sort(Comparator.comparing(LeadStatusDTO::getCreatedDate).reversed());
        return leadStatusDTOList;
    }
    private void buildExcelDocument(List<LeadStatusDTO> executiveTaskExecutions, HttpServletResponse response) {
        log.debug("Downloading Excel report");
        String excelFileName = "LeadStatusRawData" + ".xls";
        String sheetName = "Sheet1";

        String[] headerColumns = { "Employee","Account Profile","Document Name","Date"," Time","Lead Status","Deal Volume","Won Volume",
                "Lost Volume","Balance Deal Volume"};
        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            HSSFSheet worksheet = workbook.createSheet(sheetName);
            createHeaderRow(worksheet, headerColumns);
            createReportRows(worksheet, executiveTaskExecutions);
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
            log.error("IOException on downloading Lead Details{}", ex.getMessage());
        }

    }

    private void createReportRows(HSSFSheet worksheet, List<LeadStatusDTO> executiveTaskExecutions) {
        HSSFCreationHelper createHelper = worksheet.getWorkbook().getCreationHelper();
        // Create Cell Style for formatting Date
        HSSFCellStyle dateCellStyle = worksheet.getWorkbook().createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("DD/MM/YY"));

        HSSFCellStyle timeCellStyle = worksheet.getWorkbook().createCellStyle();
        timeCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("h:mm:ss"));
        // Create Other rows and cells with Sales data
        int rowNum = 1;

        for(LeadStatusDTO invoice:executiveTaskExecutions)
        {
            HSSFRow row = worksheet.createRow(rowNum++);

            row.createCell(0).setCellValue(invoice.getEmployeeName());
            row.createCell(1).setCellValue(invoice.getAccountName());
             row.createCell(2).setCellValue(invoice.getDocumentName());

            if(invoice.getCreatedDate()!=null)
            {
                LocalDateTime localDate = invoice.getCreatedDate();
                Instant i = localDate.atZone(ZoneId.systemDefault()).toInstant();
                Date date = Date.from(i);
                HSSFCell punchDateCell = row.createCell(3);
                punchDateCell.setCellValue(date);
                punchDateCell.setCellStyle(dateCellStyle);
            }

            if(invoice.getCreatedDate()!=null)
            {
                LocalDateTime localTime = invoice.getCreatedDate();
                Instant it = localTime.atZone(ZoneId.systemDefault()).toInstant();
                Date date = Date.from(it);
                HSSFCell punchTimeCell = row.createCell(4);
                punchTimeCell.setCellValue(date);
                punchTimeCell.setCellStyle(timeCellStyle);
            }

            row.createCell(5).setCellValue(invoice.getLeadStatus());
            row.createCell(6).setCellValue(invoice.getDealVolume() != null ? String.valueOf(invoice.getDealVolume()) : "");
            row.createCell(7).setCellValue(invoice.getWonVolume() != null ? String.valueOf(invoice.getWonVolume()) :"");
            row.createCell(8).setCellValue(invoice.getLostVolume() != null ?String.valueOf(invoice.getLostVolume()) : "");
            row.createCell(9).setCellValue(invoice.getBalanceDealVolume()!= null ? String.valueOf(invoice.getBalanceDealVolume()) : "");

        }
    }

    private void createHeaderRow(HSSFSheet worksheet, String[] headerColumns) {
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


    private List<Long> getUserIdsUnderCurrentUser(String employeePid, boolean inclSubordinate) {
        List<Long> userIds = Collections.emptyList();
        if (employeePid.equals("Dashboard Employee") || employeePid.equals("no")) {
            userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
            if (employeePid.equals("Dashboard Employee")) {
//				List<User> dashboardUsers = dashboardUserRepository.findUsersByCompanyId();
//				List<Long> dashboardUserIds = dashboardUsers.stream().map(User::getId).collect(Collectors.toList());
                Set<Long> dashboardUserIds = dashboardUserRepository.findUserIdsByCompanyId();
                Set<Long> uniqueIds = new HashSet<>();
                log.info("dashboard user ids empty: " + dashboardUserIds.isEmpty());
                if (!dashboardUserIds.isEmpty()) {
                    log.info(" user ids empty: " + userIds.isEmpty());
                    log.info("userids :" + userIds.toString());
                    if (!userIds.isEmpty()) {
                        for (Long uid : userIds) {
                            for (Long sid : dashboardUserIds) {
                                if (uid != null && uid.equals(sid)) {
                                    uniqueIds.add(sid);
                                }
                            }
                        }
                    } else {
                        userIds = new ArrayList<>(dashboardUserIds);
                    }
                }
                if (!uniqueIds.isEmpty()) {
                    userIds = new ArrayList<>(uniqueIds);
                }
            } else {
                if (userIds.isEmpty()) {
                    // List<User> users = userRepository.findAllByCompanyId();
                    // userIds = users.stream().map(User::getId).collect(Collectors.toList());
                    userIds = userRepository.findAllUserIdsByCompanyId();
                }
            }
        } else {
            if (inclSubordinate) {
                userIds = employeeHierarchyService.getEmployeeSubordinateIds(employeePid);
                System.out.println("Testing start for Activity Transaction");
                System.out.println("employeePid:" + employeePid);
                System.out.println("userIds:" + userIds.toString());
                System.out.println("Testing end for Activity Transaction");
            } else {
                Optional<EmployeeProfile> opEmployee = employeeProfileRepository.findOneByPid(employeePid);
                if (opEmployee.isPresent()) {
                    userIds = Arrays.asList(opEmployee.get().getUser().getId());
                }
                System.out.println("Testing start for Activity Transaction");
                System.out.println("--------------------------------------");
                System.out.println("employeePid:" + employeePid);
                System.out.println("UserIds:" + userIds.toString());
                System.out.println("Testing end for Activity Transaction");
            }
        }

        return userIds;
    }

}
