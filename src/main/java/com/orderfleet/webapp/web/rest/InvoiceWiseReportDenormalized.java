package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.geolocation.model.TowerLocation;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.*;
import com.orderfleet.webapp.web.rest.dto.*;
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web")

public class InvoiceWiseReportDenormalized {

    private final Logger log = LoggerFactory.getLogger(InvoiceWiseReportDenormalized.class);

    private static final String INTERIM_SAVE = "interimSave";

    private static final String DISTANCE_TRAVELLED = "distanceTarvel";

    @Inject
    private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

    @Inject
    private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

    @Inject
    private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

    @Inject
    private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

    @Inject
    private AccountProfileService accountProfileService;

    @Inject
    private EmployeeHierarchyService employeeHierarchyService;

    @Inject
    private LocationAccountProfileService locationAccountProfileService;

    @Inject
    private UserActivityRepository userActivityRepository;

    @Inject
    private EmployeeProfileRepository employeeProfileRepository;

    @Inject
    private FilledFormRepository filledFormRepository;

    @Inject
    private DashboardUserRepository dashboardUserRepository;

    @Inject
    private AccountProfileRepository accountProfileRepository;

    @Inject
    private AccountTypeService accountTypeService;

    @Inject
    private PriceLevelService priceLevelService;

    @Inject
    private DocumentService documentService;

    @Inject
    private DocumentFormsService documentFormsService;

    @Inject
    private ExecutiveTaskExecutionService executiveTaskExecutionService;

    @Inject
    private CompanyConfigurationRepository companyConfigurationRepository;

    @Inject
    private DynamicDocumentHeaderService dynamicDocumentHeaderService;

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private LocationService locationService;

    @Inject
    private GeoLocationService geoLocationService;

    @Inject
    private UserActivityService userActivityService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private InvoiceDetailsDenormalizedRepository invoiceInventoryDetailsRepository;

    @Inject
    private DocumentRepository documentRepository;

    @RequestMapping(value = "/invoice-reports-denormalized", method = RequestMethod.GET)
    @Timed
    @Transactional(readOnly = true)
    public String getAllExecutiveTaskExecutions(Pageable pageable, Model model) {
        log.info("Web request to get the modal web page");
        // user under current user
        List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
        model.addAttribute("accountTypes", accountTypeService.findAllByCompany());
        model.addAttribute("priceLevels", priceLevelService.findAllByCompany());
        model.addAttribute("dynamicdocuments", documentService.findAllByDocumentType(DocumentType.DYNAMIC_DOCUMENT));
        List<DocumentDTO> documentDTOs = userDocumentService.findDocumentsByUserIsCurrentUser();
        model.addAttribute("documents", documentDTOs);

        List<LocationDTO> locationDTOs = locationService.findAllByCompanyAndLocationActivated(true);
        model.addAttribute("territories", locationDTOs);

        if (userIds.isEmpty()) {
            model.addAttribute("accounts", accountProfileService.findAllByCompanyAndActivated(true));
            model.addAttribute("activities", userActivityService.findAllDistinctByUserActivityByCompany());
        } else {
            model.addAttribute("accounts", locationAccountProfileService.findAccountProfilesByCurrentUserLocations());
            List<UserActivity> activities = userActivityRepository.findByUserIsCurrentUser();
            List<Activity> activitys = new ArrayList<>();
            for (UserActivity userActivity : activities) {
                activitys.add(userActivity.getActivity());
            }
            model.addAttribute("activities", activitys);
        }
        Optional<CompanyConfiguration> optionalInterim = companyConfigurationRepository
                .findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.INTERIM_SAVE);
        Optional<CompanyConfiguration> optionaldistanceTarvel = companyConfigurationRepository
                .findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.LOCATION_VARIANCE);
        if (optionalInterim.isPresent()) {
            if (optionalInterim.get().getValue().equals("true")) {
                model.addAttribute(INTERIM_SAVE, true);
            } else {
                model.addAttribute(INTERIM_SAVE, false);
            }
        } else {
            model.addAttribute(INTERIM_SAVE, false);
        }

        if (optionaldistanceTarvel.isPresent()) {
            if (optionaldistanceTarvel.get().getValue().equals("true")) {
                model.addAttribute(DISTANCE_TRAVELLED, true);
            } else {
                model.addAttribute(DISTANCE_TRAVELLED, false);
            }
        } else {
            model.addAttribute(DISTANCE_TRAVELLED, false);
        }

        return "company/invoiceWiseReportsDenormalized";
    }

    @RequestMapping(value = "/invoice-reports-denormalized/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<InvoiceReportList>> filterExecutiveTaskExecutionsDenormalized(
            @RequestParam("documentPid") String documentPid, @RequestParam("employeePid") String employeePid,
            @RequestParam("activityPid") String activityPid, @RequestParam("accountPid") String accountPid,
            @RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate,
            @RequestParam boolean inclSubordinate) {

        List<InvoiceReportList> invoiceReportLists = new ArrayList<>();
        if (filterBy.equals("TODAY")) {
            invoiceReportLists = getFilterData(employeePid, documentPid, activityPid, accountPid, LocalDate.now(),
                    LocalDate.now(), inclSubordinate);
        } else if (filterBy.equals("YESTERDAY")) {
            LocalDate yeasterday = LocalDate.now().minusDays(1);
            invoiceReportLists = getFilterData(employeePid, documentPid, activityPid, accountPid, yeasterday,
                    yeasterday, inclSubordinate);
        } else if (filterBy.equals("WTD")) {
            TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
            LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
            invoiceReportLists = getFilterData(employeePid, documentPid, activityPid, accountPid, weekStartDate,
                    LocalDate.now(), inclSubordinate);
        } else if (filterBy.equals("MTD")) {
            LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
            invoiceReportLists = getFilterData(employeePid, documentPid, activityPid, accountPid, monthStartDate,
                    LocalDate.now(), inclSubordinate);
        } else if (filterBy.equals("CUSTOM")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
            LocalDate toFateTime = LocalDate.parse(toDate, formatter);
            invoiceReportLists = getFilterData(employeePid, documentPid, activityPid, accountPid, fromDateTime,
                    toFateTime, inclSubordinate);
        } else if (filterBy.equals("SINGLE")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
            invoiceReportLists = getFilterData(employeePid, documentPid, activityPid, accountPid, fromDateTime,
                    fromDateTime, inclSubordinate);
        }
        return new ResponseEntity<>(invoiceReportLists, HttpStatus.OK);
    }

    private List<InvoiceReportList> getFilterData(String employeePid, String documentPid, String activityPid, String accountPid, LocalDate fDate, LocalDate tDate, boolean inclSubordinate) {
        LocalDateTime fromDate = fDate.atTime(0, 0);
        LocalDateTime toDate = tDate.atTime(23, 59);

        List<String> activityPids;
        List<String> accountProfilePids;

        List<Long> userIds = getUserIdsUnderCurrentUser(employeePid, inclSubordinate);
        log.info("User Ids :" + userIds);
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }
        if (activityPid.equalsIgnoreCase("no") || activityPid.equalsIgnoreCase("planed")
                || activityPid.equalsIgnoreCase("unPlaned")) {
            activityPids = getActivityPids(activityPid, userIds);
        } else {
            activityPids = Arrays.asList(activityPid);
        }
        List<String> docpids = new ArrayList<>();
        if(documentPid.equalsIgnoreCase("no"))
        {
             docpids = documentRepository.findAllByCompanyId().stream()
                     .map(doc->doc.getPid()).collect(Collectors.toList());
        }
        else{
            docpids.add(documentPid);
        }
        List<InvoiceDetailsDenormalized> invoiceDetailsDenormalized = new ArrayList<>();
        log.info("Finding executive Task execution from denormalized table");
        if (accountPid.equalsIgnoreCase("no")) {

            invoiceDetailsDenormalized = invoiceInventoryDetailsRepository
                    .getByCreatedDateBetweenAndActivityPidInAndUserIdInAndDocumentPidIn(fromDate, toDate, activityPids, userIds,docpids);
        } else {
            // if a specific account is selected load data based on that particular account
            accountProfilePids = Arrays.asList(accountPid);
            invoiceDetailsDenormalized = invoiceInventoryDetailsRepository
                    .getByCreatedDateBetweenAndActivityPidInAndUserIdInAndAccountPidInAndDocumentPidIn(fromDate, toDate, activityPids, userIds,
                            accountProfilePids,docpids);
        }
        log.info("SIze of data ********************:" + invoiceDetailsDenormalized.size());
        Set<BigInteger> filledForms = filledFormRepository.findfilledForms();

        List<InvoiceDetailsDenormalized> invoiceDetailsDenormalizedList = new ArrayList<>();
        if (invoiceDetailsDenormalized.size() > 0) {
            Map<String, InvoiceDetailsDenormalized> uniqueMap = invoiceDetailsDenormalized.stream()
                    .collect(Collectors.groupingBy(InvoiceDetailsDenormalized :: getDocumentNumberLocal,
                            Collectors.collectingAndThen(Collectors.toList(), list -> list.get(0))));

            invoiceDetailsDenormalizedList = new ArrayList<>(uniqueMap.values());
            log.info("Size of unique :" + invoiceDetailsDenormalizedList.size());
        }
        invoiceDetailsDenormalizedList.sort(Comparator.comparing(InvoiceDetailsDenormalized:: getCreatedDate).reversed());
        List<InvoiceReportList> invoiceReportLists = new ArrayList<>();
        for (InvoiceDetailsDenormalized invoices : invoiceDetailsDenormalizedList) {
            InvoiceReportList invoiceReport = new InvoiceReportList();
            invoiceReport.setExecutionPid(invoices.getExecutionPid());
            invoiceReport.setDynamicPid(invoices.getDynamicPid());
            invoiceReport.setPid(invoices.getPid());
            invoiceReport.setEmployeeName(invoices.getEmployeeName());
            invoiceReport.setAccountProfileName(invoices.getAccountProfileName());
            invoiceReport.setActivityName(invoices.getActivityName());
            invoiceReport.setPunchIn(invoices.getPunchInDate());
            invoiceReport.setClientDate(invoices.getSendDate());
            invoiceReport.setCreatedDate(invoices.getCreatedDate());
            invoiceReport.setTimeSpend(findTimeSpends(invoices.getSendDate(), invoices.getCreatedDate()));
            invoiceReport.setLocation(invoices.getLocation());
            invoiceReport.setLocationType(invoices.getLocationType());
            invoiceReport.setTowerLocation(invoices.getTowerLocation());
            invoiceReport.setWithCustomer(invoices.isWithCustomer());
            invoiceReport.setMockLocationStatus(invoices.isMockLocationStatus());
            invoiceReport.setSalesOrderTotalAmount(invoices.getSalesOrderTotalAmount());
            invoiceReport.setReceiptAmount(invoices.getReceiptAmount());
            invoiceReport.setVehicleNumber(invoices.getVehicleNumber());
            invoiceReport.setRemarks(invoices.getRemarks());
            invoiceReport.setDocumentName(invoices.getDocumentName());
            invoiceReport.setDocumentTotal(invoices.getDocumentTotal());
            invoiceReport.setDocumentType(invoices.getDocumentType());
            invoiceReport.setFilledformId(invoices.getFilledFormId());
            invoiceReport.setImagerefNo(invoices.getImageRefNo());
            invoiceReport.setDocumentVolume(invoices.getDocumentVolume());
            boolean imageFound = false;
            if(invoices.getFilledFormId()!= null) {
                Optional<BigInteger> opFilledForms = filledForms.stream()
                        .filter(ff -> ff.equals(BigInteger.valueOf(invoices.getFilledFormId()))).findAny();
                if (opFilledForms.isPresent()) {
                    System.out.println("Filled Form present");
                    imageFound = true;
                    invoiceReport.setImageFound(imageFound);
                }
            }
            invoiceReportLists.add(invoiceReport);
        }

        return invoiceReportLists;
    }

    public String findTimeSpends(LocalDateTime startTime, LocalDateTime endTime) {

        long hours = 00;
        long minutes = 00;
        long seconds = 00;
        long milliseconds = 00;
        if (startTime != null && endTime != null) {
            long years = startTime.until(endTime, ChronoUnit.YEARS);
            startTime = startTime.plusYears(years);

            long months = startTime.until(endTime, ChronoUnit.MONTHS);
            startTime = startTime.plusMonths(months);

            long days = startTime.until(endTime, ChronoUnit.DAYS);
            startTime = startTime.plusDays(days);

            hours = startTime.until(endTime, ChronoUnit.HOURS);
            startTime = startTime.plusHours(hours);

            minutes = startTime.until(endTime, ChronoUnit.MINUTES);
            startTime = startTime.plusMinutes(minutes);

            seconds = startTime.until(endTime, ChronoUnit.SECONDS);
            startTime = startTime.plusSeconds(seconds);

            milliseconds = startTime.until(endTime, ChronoUnit.MILLIS);
        }

        return hours + " : " + minutes + " : " + seconds + ":" + milliseconds;

    }

    private List<String> getActivityPids(String activityPid, List<Long> userIds) {
        List<String> activityPids;
        List<ActivityDTO> allActivityDTOs = new ArrayList<>();
        if (userIds.isEmpty()) {
            allActivityDTOs.addAll(userActivityService.findAllDistinctByUserActivityByCompany());
        } else {
            Set<UserActivity> userActivities = userActivityService
                    .findUserActivitiesByActivatedTrueAndUserIdIn(userIds);
            allActivityDTOs.addAll(userActivities.stream()
                    .map(usrActvity -> new ActivityDTO(usrActvity.getActivity(), usrActvity.getSaveActivityDuration(),
                            usrActvity.getPlanThrouchOnly(), usrActvity.getExcludeAccountsInPlan(),
                            usrActvity.getInterimSave()))
                    .collect(Collectors.toList()));
        }
        List<ActivityDTO> activityDTOs = new ArrayList<>();
        if (activityPid.equalsIgnoreCase("no")) {
            activityDTOs.addAll(allActivityDTOs);
        } else if (activityPid.equalsIgnoreCase("planed")) {
            allActivityDTOs.forEach(act -> {
                if (act.getPlanThrouchOnly()) {
                    activityDTOs.add(act);
                }
            });
        } else if (activityPid.equalsIgnoreCase("unPlaned")) {
            allActivityDTOs.forEach(act -> {
                if (!act.getPlanThrouchOnly()) {
                    activityDTOs.add(act);
                }
            });
        }
        activityPids = activityDTOs.stream().map(act -> act.getPid()).collect(Collectors.toList());
        return activityPids;

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

    @RequestMapping(value = "/invoice-reports-denormalized/updateLocation/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<InvoiceReportList> updateLocationExecutiveTaskExecutions(@PathVariable String pid) {
        Optional<ExecutiveTaskExecution> opExecutiveeExecution = executiveTaskExecutionRepository.findOneByPid(pid);

        InvoiceReportList executionView = new InvoiceReportList();
        if (opExecutiveeExecution.isPresent()) {
            ExecutiveTaskExecution execution = opExecutiveeExecution.get();
            if (execution.getLatitude() != BigDecimal.ZERO) {
                log.debug("lat != 0");
                String location = geoLocationService
                        .findAddressFromLatLng(execution.getLatitude() + "," + execution.getLongitude());
                log.debug("Location : " + location);
                execution.setLocation(location);
            } else {
                log.debug("No Location");
                execution.setLocation("No Location");
            }

            execution = executiveTaskExecutionRepository.save(execution);
            executionView = new InvoiceReportList(execution);
        }
        return new ResponseEntity<>(executionView, HttpStatus.OK);
    }

    @RequestMapping(value = "/invoice-reports-denormalized/updateTowerLocation/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<InvoiceReportList> updateTowerLocationExecutiveTaskExecutions(@PathVariable String pid) {
        Optional<ExecutiveTaskExecution> opExecutiveeExecution = executiveTaskExecutionRepository.findOneByPid(pid);
        InvoiceReportList executionView = new InvoiceReportList();
        if (opExecutiveeExecution.isPresent()) {
            ExecutiveTaskExecution execution = opExecutiveeExecution.get();
            TowerLocation location = geoLocationService.findAddressFromCellTower(execution.getMcc(), execution.getMnc(),
                    execution.getCellId(), execution.getLac());

            if (location.getLat() != null && location.getLat() != BigDecimal.ZERO) {
                execution.setTowerLatitude(location.getLat());
                execution.setTowerLongitude(location.getLan());

            }

            execution.setTowerLocation(location.getLocation());

            execution = executiveTaskExecutionRepository.save(execution);
            executionView = new InvoiceReportList(execution);
        }
        return new ResponseEntity<>(executionView, HttpStatus.OK);
    }

    @RequestMapping(value = "/invoice-reports-denormalized/getActivities/{typeName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ActivityDTO>> getActivities(@PathVariable String typeName) {
        log.debug("Web request to get activities by typeName : {}", typeName);

        List<ActivityDTO> allActivityDTOs = new ArrayList<>();
        // user under current user
        List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
        if (userIds.isEmpty()) {
            allActivityDTOs.addAll(userActivityService.findAllDistinctByUserActivityByCompany());
        } else {
            List<UserActivity> userActivities = userActivityRepository.findByUserIsCurrentUser();
            allActivityDTOs.addAll(userActivities.stream()
                    .map(usrActvity -> new ActivityDTO(usrActvity.getActivity(), usrActvity.getSaveActivityDuration(),
                            usrActvity.getPlanThrouchOnly(), usrActvity.getExcludeAccountsInPlan(),
                            usrActvity.getInterimSave()))
                    .collect(Collectors.toList()));
        }
        List<ActivityDTO> result = new ArrayList<>();
        if (typeName.equalsIgnoreCase("all")) {
            result.addAll(allActivityDTOs);
        } else if (typeName.equalsIgnoreCase("planed")) {
            allActivityDTOs.forEach(act -> {
                if (act.getPlanThrouchOnly()) {
                    result.add(act);
                }
            });
        } else if (typeName.equalsIgnoreCase("unPlaned")) {
            allActivityDTOs.forEach(act -> {
                if (!act.getPlanThrouchOnly()) {
                    result.add(act);
                }
            });
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/invoice-reports-denormalized/getInventoryDetails/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SalesOrderDTOs> getInventoryVouchers(@PathVariable String pid) {
        List<InvoiceDetailsDenormalized> invoiceDetailsDenormalized = invoiceInventoryDetailsRepository.findAllByExecutionPidAndDocumentType(pid, DocumentType.INVENTORY_VOUCHER);
        log.info("SIze of data :" + invoiceDetailsDenormalized.size());
        SalesOrderDTOs salesorder = new SalesOrderDTOs();
        for (InvoiceDetailsDenormalized invoice : invoiceDetailsDenormalized) {

            salesorder.setDocumentName(invoice.getDocumentName());
            salesorder.setUserName(invoice.getUserName());
            salesorder.setDocumentNumberServer(invoice.getDocumentNumberServer());
            salesorder.setDocumentDate(invoice.getCreatedDate());
            salesorder.setReceiverAccountName(invoice.getReceiverAccountName());
            salesorder.setSupplierAccountName(invoice.getSupplierAccountName());
            salesorder.setDocumentTotal(invoice.getDocumentTotal());
            salesorder.setDocumentVolume(invoice.getDocumentVolume());
            salesorder.setDocDiscountPercentage(invoice.getDocDiscountPercentage());
            salesorder.setDocDiscountAmount(invoice.getDocDiscountAmount());
        }
        List<InventoryData> inventoryData = new ArrayList<>();
        for (InvoiceDetailsDenormalized invoice : invoiceDetailsDenormalized) {
            InventoryData inventory = new InventoryData();
            inventory.setProductName(invoice.getProductName());
            inventory.setQuantity(invoice.getQuantity());
            inventory.setFreeQuantity(invoice.getFreeQuantity());
            inventory.setSellingRate(invoice.getSellingRate());
            inventory.setTaxPercentage(invoice.getTaxPercentage());
            inventory.setDiscountAmount(invoice.getDiscountAmount());
            inventory.setDiscountPercentage(invoice.getDiscountPercentage());
            inventory.setRowTotal(invoice.getQuantity()*invoice.getSellingRate());
            inventoryData.add(inventory);
            salesorder.setInventoryData(inventoryData);
        }
        return new ResponseEntity<>(salesorder, HttpStatus.OK);
    }

    @RequestMapping(value = "/invoice-reports-denormalized/accounting-details/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ReceiptDTOs> getAccountingVouchers(@PathVariable String pid) {
        List<InvoiceDetailsDenormalized> invoiceDetailsDenormalized = invoiceInventoryDetailsRepository.findAllByExecutionPidAndDocumentType(pid, DocumentType.ACCOUNTING_VOUCHER);

        ReceiptDTOs receiptDTOs = new ReceiptDTOs();
        for (InvoiceDetailsDenormalized invoiceDetails : invoiceDetailsDenormalized) {
            receiptDTOs.setDocumentNumberServer(invoiceDetails.getDocumentNumberServer());
            receiptDTOs.setUserName(invoiceDetails.getUserName());
            receiptDTOs.setAccountProfileName(invoiceDetails.getAccountProfileName());
            receiptDTOs.setDocumentName(invoiceDetails.getDocumentName());
            receiptDTOs.setCreatedDate(invoiceDetails.getCreatedDate());
            receiptDTOs.setTotalAmount(invoiceDetails.getTotalAmount());
            receiptDTOs.setOutstandingAmount(invoiceDetails.getOutstandingAmount());
            receiptDTOs.setRemarks(invoiceDetails.getRemarks());
            receiptDTOs.setDocumentTotal(invoiceDetails.getTotalAmount());
        }
        List<AccountingData> accountingData = new ArrayList<>();
        for (InvoiceDetailsDenormalized invoiceDetails : invoiceDetailsDenormalized) {
            AccountingData accounting = new AccountingData();
            accounting.setAmount(invoiceDetails.getAmount());
            accounting.setMode(invoiceDetails.getMode());
            accounting.setInstrumentNumber(invoiceDetails.getInstrumentNumber());
            accounting.setInstrumentDate(invoiceDetails.getInstrumentDate());
            accounting.setBankName(invoiceDetails.getBankName());
            accounting.setByAccountName(invoiceDetails.getByAccountName());
            accounting.setToAccountName(invoiceDetails.getToAccountName());
            if(invoiceDetails.getIncomeExpenseHead()!= null) {
                accounting.setIncomeExpenseHeadName(invoiceDetails.getIncomeExpenseHead().getName());
            }
            accounting.setVoucherNumber(invoiceDetails.getVoucherNumber());
            accounting.setVoucherDate(invoiceDetails.getVoucherDate());
            accounting.setReferenceNumber(invoiceDetails.getReferenceNumber());
            accounting.setProvisionalReceiptNo(invoiceDetails.getProvisionalReceiptNo());
            accounting.setRemarks(invoiceDetails.getReceiptRemarks());
            accountingData.add(accounting);
            receiptDTOs.setAccountingData(accountingData);
        }

        return new ResponseEntity<>(receiptDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/invoice-reports-denormalized/dynamic-documents-details/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DynamicDocumentDTO> getDynamicDocuments(@PathVariable String pid) {
        List<InvoiceDetailsDenormalized> invoiceDetailsDenormalized = invoiceInventoryDetailsRepository.findAllByExecutionPidAndDocumentType(pid, DocumentType.DYNAMIC_DOCUMENT);
             DynamicDocumentDTO documentDTO = new DynamicDocumentDTO();
        for(InvoiceDetailsDenormalized invoiceDetails :invoiceDetailsDenormalized)
        {
            documentDTO.setDocumentNumberServer(invoiceDetails.getDocumentNumberServer());
            documentDTO.setUserName(invoiceDetails.getUserName());
            documentDTO.setAccountName(invoiceDetails.getAccountProfileName());
            documentDTO.setAccountPhone(invoiceDetails.getAccountPhNo());
            documentDTO.setActivityname(invoiceDetails.getActivityName());
            documentDTO.setDocumentName(invoiceDetails.getDocumentName());
            documentDTO.setDocumentDate(invoiceDetails.getCreatedDate());
            documentDTO.setFormName(invoiceDetails.getFormName());
        }
        List<DynamicData> dynamicData = new ArrayList<>();
        for(InvoiceDetailsDenormalized invoiceDetails :invoiceDetailsDenormalized)
        {
            DynamicData dynamic = new DynamicData();
            dynamic.setFormElementName(invoiceDetails.getFormElementName());
            dynamic.setValue(invoiceDetails.getValue());
            dynamicData.add(dynamic);
            documentDTO.setDynamic(dynamicData);

        }
        return new ResponseEntity<>(documentDTO,HttpStatus.OK);
    }
}





