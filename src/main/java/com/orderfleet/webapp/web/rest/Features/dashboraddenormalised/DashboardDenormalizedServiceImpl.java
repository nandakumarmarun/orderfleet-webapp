package com.orderfleet.webapp.web.rest.Features.dashboraddenormalised;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.*;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DashboardUserService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.LocationHierarchyService;
import com.orderfleet.webapp.web.rest.Features.dashboraddenormalised.DashboardDenormalizedService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.Features.dashboraddenormalised.DashboardNew;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.chart.dto.BarChartDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardChartDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardHeaderSummaryDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardSummaryDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardUserDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class DashboardDenormalizedServiceImpl implements DashboardDenormalizedService {

    public static final String DASHBOARD_NEW_PREFIX = DashboardDenormalizedService.PID_PREFIX;
    private final Logger log = LoggerFactory.getLogger(DashboardDenormalizedService.class);
    @Inject
    private EmployeeProfileRepository employeeProfileRepository;

    @Inject
    private DocumentRepository documentRepository;

    @Inject
    private LocationAccountProfileRepository locationAccountProfileRepository;

    @Inject
    private LocationHierarchyRepository locationHierarchyRepository;

    @Inject
    private EmployeeProfileLocationRepository employeeProfileLocationRepository;

    @Inject
    private DashboardUserService dashboardUserService;

    @Inject
    private DashboardDenormalizedRepository dashboardDenormalizedRepository;

    @Inject
    private ExecutiveTaskPlanRepository executiveTaskPlanRepository;

    @Inject
    private DashboardItemUserRepository dashboardItemUserRepository;

    @Inject
    private DashboardItemRepository dashboardItemRepository;

    @Inject
    private DashboardChartItemRepository dashboardChartItemRepository;

    @Inject
    private EmployeeHierarchyService employeeHierarchyService;

    @Inject
    private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

    @Inject
    private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

    @Inject
    private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

    @Inject
    private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

    @Inject
    private LocationHierarchyService locationHierarchyService;

    @Inject
    private AttendanceRepository attendanceRepository;

    @Inject
    private InventoryVoucherHeaderService inventoryVoucherHeaderService;

    @Inject
    private DashboardAttendanceUserRepository dashboardAttendanceUserRepository;

    @Inject
    private SalesTargetGroupProductRepository salesTargetGroupProductRepository;

    @Inject
    private SalesTargetGroupDocumentRepository salesTargetGroupDocumentRepository;

    @Inject
    private SalesTargetGroupUserTargetRepository salesTargetGroupUserTargetRepository;

    @Inject
    private SalesTargetReportSettingSalesTargetBlockRepository salesTargetReportSettingSalesTargetBlockRepository;


    @Inject
    private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

    @Inject
    private ProductGroupProductRepository productGroupProductRepository;


    @Override
    public void SaveExecutivetaskExecutionDocument(
            ExecutiveTaskSubmissionTransactionWrapper executiveTaskSubmissionDTO,
            User user) {

        log.debug("Enter ExecutivetaskExecution -> Dashboard Data ");

        Company company = user.getCompany();
        EmployeeProfile employeeProfile = employeeProfileRepository.findEmployeeProfileByUser(user);

        List<InventoryVoucherHeader> inventoryVoucherHeaders = executiveTaskSubmissionDTO.getInventoryVouchers();
        List<DynamicDocumentHeader> dynamicDocumentHeaders = executiveTaskSubmissionDTO.getDynamicDocuments();
        List<AccountingVoucherHeader> accountingVoucherHeaders = executiveTaskSubmissionDTO.getAccountingVouchers();
        ExecutiveTaskExecution executionDTO = executiveTaskSubmissionDTO.getExecutiveTaskExecution();

        savingInventoryDataToDashborad(user, employeeProfile, inventoryVoucherHeaders, executionDTO);
        savingAccountingDataToDashboard(user, employeeProfile, accountingVoucherHeaders, executionDTO);
        savingDynamicDataToDashboard(user, employeeProfile, dynamicDocumentHeaders, executionDTO);
    }



    private void savingDynamicDataToDashboard(
            User user, EmployeeProfile employeeProfile,
            List<DynamicDocumentHeader> dynamicDocumentHeaders,
            ExecutiveTaskExecution executionDTO) {
        if (dynamicDocumentHeaders != null) {
            for (DynamicDocumentHeader docHeader : dynamicDocumentHeaders) {
                        DashboardNew dashboardNew = new DashboardNew();

                        LocationAccountProfile locationAccountProfile = null;

                        List<LocationAccountProfile> locationAccountProfiles = locationAccountProfileRepository.findAllLocationByAccountProfilePids(executionDTO.getAccountProfile().getPid());

                        Optional<LocationAccountProfile> locationAccountProfile1 = locationAccountProfiles.stream().findFirst();

                        if(locationAccountProfile1.isPresent()){
                            locationAccountProfile= locationAccountProfile1.get();
                        }

                        Optional<DashboardNew> dashboardNew1 = dashboardDenormalizedRepository
                                .findByDateUserDocActivityCompanyTerritory(docHeader.getDocumentDate().toLocalDate()
                                , user.getId(), docHeader.getDocument().getId(), executionDTO.getActivity().getId()
                                , executionDTO.getCompany().getId(), locationAccountProfile.getLocation().getId());

                        if (!dashboardNew1.isPresent()) {
                            String pid = DASHBOARD_NEW_PREFIX + RandomUtil.generatePid();
                            dashboardNew.setPid(pid);

                            dashboardNew.setUserId(user.getId());
                            dashboardNew.setUserPid(user.getPid());

                            dashboardNew.setCompanyId(employeeProfile.getCompany().getId());
                            dashboardNew.setCompanyPid(employeeProfile.getCompany().getPid());

                            dashboardNew.setActivityId(executionDTO.getActivity().getId());
                            dashboardNew.setActivityPid(executionDTO.getActivity().getPid());
                            dashboardNew.setActivityName(executionDTO.getActivity().getName());

                            dashboardNew.setDocumentId(docHeader.getDocument().getId());
                            dashboardNew.setDocumentPid(docHeader.getDocument().getPid());
                            dashboardNew.setDocumentName(docHeader.getDocument().getName());
                            dashboardNew.setDocumentType(docHeader.getDocument().getDocumentType().toString());

                            dashboardNew.setLastCustomerLocation(executionDTO.getAccountProfile().getLocation());
                            dashboardNew.setLocation(executionDTO.getLocation());

                            dashboardNew.setTerritoryPid(locationAccountProfile.getLocation().getPid());
                            dashboardNew.setTerritoryId(locationAccountProfile.getLocation().getId());

                            dashboardNew.setCompanyId(executionDTO.getCompany().getId());
                            dashboardNew.setCompanyPid(executionDTO.getCompany().getPid());

                            dashboardNew.setDate(LocalDate.from(docHeader.getDocumentDate()));
                            dashboardNew.setCreatedDate(docHeader.getDocumentDate());
                            dashboardNew.setModifiedDate(docHeader.getDocumentDate());

                            dashboardNew.setLocationType(executionDTO.getLocationType());
                            dashboardNew.setGpsOff(executionDTO.getIsGpsOff());
                            dashboardNew.setMobileDataOff(executionDTO.getIsMobileDataOff());
                            dashboardNew.setMockLocationStatus(executionDTO.getMockLocationStatus());
                            dashboardNew.setExePid(executionDTO.getPid());
                            dashboardNew.setLatitude(executionDTO.getLatitude());

                            dashboardNew.setSubmissionCount(1);
                            dashboardNew.setAmount(0);

                            if(executionDTO.getExecutiveTaskPlan() == null){
                                dashboardNew.setUnplannedCount(1);
                                dashboardNew.setUnplannedAmount(0);
                            }else{
                                dashboardNew.setPlanedCount(1);
                                dashboardNew.setPlannedAmount(0);
                            }

                            dashboardNew.setBatteryPercentage(employeeProfile.getBatteryPercentage());

                            DashboardNew result = dashboardDenormalizedRepository.save(dashboardNew);
                            log.debug(  result != null ? " dynamic Document Result Saved Sucess" : "dynamic Document Result Not Saved "  );
                        } else {
                            try {
                                if(executionDTO.getExecutiveTaskPlan() == null){
                                    dashboardNew = dashboardNew1.get();

                                    dashboardNew.setSubmissionCount(dashboardNew.getSubmissionCount() + 1);
                                    dashboardNew.setUnplannedCount(dashboardNew.getUnplannedCount() + 1);

                                    dashboardNew.setModifiedDate(docHeader.getDocumentDate());
                                    dashboardNew.setLocationType(executionDTO.getLocationType());
                                    dashboardNew.setGpsOff(executionDTO.getIsGpsOff());
                                    dashboardNew.setMobileDataOff(executionDTO.getIsMobileDataOff());
                                    dashboardNew.setMockLocationStatus(executionDTO.getMockLocationStatus());
                                    dashboardNew.setExePid(executionDTO.getPid());
                                    dashboardNew.setLatitude(executionDTO.getLatitude());

                                    dashboardNew.setLastCustomerLocation(executionDTO.getAccountProfile().getLocation());
                                    dashboardNew.setLocation(executionDTO.getLocation());

                                    DashboardNew unplanned = dashboardDenormalizedRepository.save(dashboardNew);
                                    log.debug("dynamic Document" + unplanned != null ? "dynamic Document Result Unplanned Updation Sucess" : "dynamic Document Result Unplanned Not Updated "  );


                                }else {
                                    dashboardNew = dashboardNew1.get();

                                    dashboardNew.setSubmissionCount(dashboardNew.getSubmissionCount() + 1);
                                    dashboardNew.setPlanedCount(dashboardNew.getUnplannedCount() + 1);

                                    dashboardNew.setModifiedDate(docHeader.getDocumentDate());
                                    dashboardNew.setLocationType(executionDTO.getLocationType());
                                    dashboardNew.setGpsOff(executionDTO.getIsGpsOff());
                                    dashboardNew.setMobileDataOff(executionDTO.getIsMobileDataOff());
                                    dashboardNew.setMockLocationStatus(executionDTO.getMockLocationStatus());
                                    dashboardNew.setExePid(executionDTO.getPid());

                                    dashboardNew.setLatitude(executionDTO.getLatitude());
                                    dashboardNew.setLastCustomerLocation(executionDTO.getAccountProfile().getLocation());
                                    dashboardNew.setLocation(executionDTO.getLocation());
                                    DashboardNew planned = dashboardDenormalizedRepository.save(dashboardNew);
                                    log.debug("dynamic Document" + planned != null ? " dynamic Document Result planned Updation Sucess" : "dynamic Document Result planned Not Updated "  );

                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
            }
        }
    }



    private void savingAccountingDataToDashboard(
            User user, EmployeeProfile employeeProfile,
            List<AccountingVoucherHeader> accountingVoucherHeaders,
            ExecutiveTaskExecution executionDTO) {
        if (accountingVoucherHeaders != null) {
            for (AccountingVoucherHeader accountingVoucherHeader : accountingVoucherHeaders) {
                    DashboardNew dashboardNew = new DashboardNew();
                    LocationAccountProfile locationAccountProfile = locationAccountProfileRepository.findByAccountProfile(executionDTO.getAccountProfile());

                    Optional<DashboardNew> dashboardNew1 = dashboardDenormalizedRepository.findByDateUserDocActivityCompanyTerritory(LocalDate.from(accountingVoucherHeader.getDocumentDate())
                            , user.getId(), accountingVoucherHeader.getDocument().getId(), executionDTO.getActivity().getId()
                            , executionDTO.getCompany().getId(), locationAccountProfile.getLocation().getId());

                    if (!dashboardNew1.isPresent()) {
                        String pid = DASHBOARD_NEW_PREFIX + RandomUtil.generatePid();
                        dashboardNew.setPid(pid);

                        dashboardNew.setUserId(user.getId());
                        dashboardNew.setUserPid(user.getPid());

                        dashboardNew.setCompanyId(employeeProfile.getCompany().getId());
                        dashboardNew.setCompanyPid(employeeProfile.getCompany().getPid());

                        dashboardNew.setActivityId(executionDTO.getActivity().getId());
                        dashboardNew.setActivityPid(executionDTO.getActivity().getPid());
                        dashboardNew.setActivityName(executionDTO.getActivity().getName());

                        dashboardNew.setDocumentId(accountingVoucherHeader.getDocument().getId());
                        dashboardNew.setDocumentPid(accountingVoucherHeader.getDocument().getPid());
                        dashboardNew.setDocumentName(accountingVoucherHeader.getDocument().getName());
                        dashboardNew.setDocumentType(accountingVoucherHeader.getDocument().getDocumentType().toString());

                        dashboardNew.setTerritoryPid(locationAccountProfile.getLocation().getPid());
                        dashboardNew.setTerritoryId(locationAccountProfile.getLocation().getId());

                        dashboardNew.setCompanyId(executionDTO.getCompany().getId());
                        dashboardNew.setCompanyPid(executionDTO.getCompany().getPid());

                        dashboardNew.setCreatedDate(accountingVoucherHeader.getDocumentDate());
                        dashboardNew.setModifiedDate(accountingVoucherHeader.getDocumentDate());
                        dashboardNew.setDate(LocalDate.from(accountingVoucherHeader.getDocumentDate()));

                        dashboardNew.setBatteryPercentage(employeeProfile.getBatteryPercentage());

                        dashboardNew.setSubmissionCount(1);
                        dashboardNew.setAmount(accountingVoucherHeader.getTotalAmount());

                        dashboardNew.setLocationType(executionDTO.getLocationType());
                        dashboardNew.setGpsOff(executionDTO.getIsGpsOff());
                        dashboardNew.setMobileDataOff(executionDTO.getIsMobileDataOff());
                        dashboardNew.setMockLocationStatus(executionDTO.getMockLocationStatus());
                        dashboardNew.setExePid(executionDTO.getPid());
                        dashboardNew.setLatitude(executionDTO.getLatitude());

                        dashboardNew.setLastCustomerLocation(accountingVoucherHeader.getAccountProfile().getLocation());
                        dashboardNew.setLocation(executionDTO.getLocation());

                        if(executionDTO.getExecutiveTaskPlan() == null){
                            dashboardNew.setUnplannedCount(1);
                            dashboardNew.setUnplannedAmount(accountingVoucherHeader.getTotalAmount());
                        }else{
                            dashboardNew.setPlanedCount(1);
                            dashboardNew.setPlannedAmount(accountingVoucherHeader.getTotalAmount());
                        }

                        DashboardNew result = dashboardDenormalizedRepository.save(dashboardNew);
                        log.debug( result != null ? "Accounting Voucher Saved" : "Accounting Voucher Not Saved"  );

                    } else {
                        try {
                            if(executionDTO.getExecutiveTaskPlan() == null){
                                dashboardNew = dashboardNew1.get();
                                dashboardNew.setSubmissionCount(dashboardNew.getSubmissionCount() + 1);
                                dashboardNew.setUnplannedCount(dashboardNew.getUnplannedCount() + 1);

                                dashboardNew.setAmount(dashboardNew.getAmount() + accountingVoucherHeader.getTotalAmount());
                                dashboardNew.setUnplannedAmount(dashboardNew.getUnplannedAmount() + accountingVoucherHeader.getTotalAmount());

                                dashboardNew.setModifiedDate(accountingVoucherHeader.getDocumentDate());
                                dashboardNew.setLocationType(executionDTO.getLocationType());
                                dashboardNew.setGpsOff(executionDTO.getIsGpsOff());
                                dashboardNew.setMobileDataOff(executionDTO.getIsMobileDataOff());
                                dashboardNew.setMockLocationStatus(executionDTO.getMockLocationStatus());
                                dashboardNew.setExePid(executionDTO.getPid());
                                dashboardNew.setLatitude(executionDTO.getLatitude());

                                dashboardNew.setLastCustomerLocation(accountingVoucherHeader.getAccountProfile().getLocation());
                                dashboardNew.setLocation(executionDTO.getLocation());

                                DashboardNew unplannedresult = dashboardDenormalizedRepository.save(dashboardNew);

                                log.debug("Accounting Voucher" + unplannedresult != null ? "Result Unplanned Updation Sucess" : " Result Unplanned Not Updated "  );

                            }else{

                                dashboardNew = dashboardNew1.get();
                                dashboardNew.setSubmissionCount(dashboardNew.getSubmissionCount() + 1);
                                dashboardNew.setPlanedCount(dashboardNew.getUnplannedCount() + 1);

                                dashboardNew.setAmount(dashboardNew.getAmount());
                                dashboardNew.setPlannedAmount(dashboardNew.getPlannedAmount() + accountingVoucherHeader.getTotalAmount());

                                dashboardNew.setModifiedDate(accountingVoucherHeader.getDocumentDate());
                                dashboardNew.setLocationType(executionDTO.getLocationType());
                                dashboardNew.setGpsOff(executionDTO.getIsGpsOff());
                                dashboardNew.setMobileDataOff(executionDTO.getIsMobileDataOff());
                                dashboardNew.setMockLocationStatus(executionDTO.getMockLocationStatus());
                                dashboardNew.setExePid(executionDTO.getPid());
                                dashboardNew.setLatitude(executionDTO.getLatitude());

                                dashboardNew.setLastCustomerLocation(accountingVoucherHeader.getAccountProfile().getLocation());
                                dashboardNew.setLocation(executionDTO.getLocation());

                                DashboardNew plannedresult = dashboardDenormalizedRepository.save(dashboardNew);
                                log.debug("Accounting Voucher" + plannedresult != null ? "Result planned Updation Sucess" : " Result planned Not Updated "  );

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
            }
        }
    }



    private void savingInventoryDataToDashborad(
            User user, EmployeeProfile employeeProfile,
            List<InventoryVoucherHeader> inventoryVoucherHeaders,
            ExecutiveTaskExecution executionDTO) {
        if (inventoryVoucherHeaders != null) {
            for (InventoryVoucherHeader inventoryVoucher : inventoryVoucherHeaders) {
                    DashboardNew dashboardNew = new DashboardNew();

                    LocationAccountProfile locationAccountProfile = locationAccountProfileRepository.findByAccountProfile(executionDTO.getAccountProfile());

                    Optional<DashboardNew> dashboardNew1 = dashboardDenormalizedRepository
                            .findByDateUserDocActivityCompanyTerritory(
                              inventoryVoucher.getDocumentDate().toLocalDate()
                            , user.getId()
                            , inventoryVoucher.getDocument().getId()
                            , executionDTO.getActivity().getId()
                            , executionDTO.getCompany().getId()
                            , locationAccountProfile.getLocation().getId());

                    if (!dashboardNew1.isPresent()) {
                        String pid = DASHBOARD_NEW_PREFIX + RandomUtil.generatePid();
                        dashboardNew.setPid(pid);

                        dashboardNew.setUserId(user.getId());
                        dashboardNew.setUserPid(user.getPid());

                        dashboardNew.setCompanyId(employeeProfile.getCompany().getId());
                        dashboardNew.setCompanyPid(employeeProfile.getCompany().getPid());

                        dashboardNew.setActivityId(executionDTO.getActivity().getId());
                        dashboardNew.setActivityPid(executionDTO.getActivity().getPid());
                        dashboardNew.setActivityName(executionDTO.getActivity().getName());

                        dashboardNew.setDocumentId(inventoryVoucher.getDocument().getId());
                        dashboardNew.setDocumentPid(inventoryVoucher.getDocument().getPid());
                        dashboardNew.setDocumentName(inventoryVoucher.getDocument().getName());
                        dashboardNew.setDocumentType(inventoryVoucher.getDocument().getDocumentType().toString());

                        dashboardNew.setTerritoryPid(locationAccountProfile.getLocation().getPid());
                        dashboardNew.setTerritoryId(locationAccountProfile.getLocation().getId());

                        dashboardNew.setCompanyId(executionDTO.getCompany().getId());
                        dashboardNew.setCompanyPid(executionDTO.getCompany().getPid());

                        dashboardNew.setCreatedDate(inventoryVoucher.getDocumentDate());
                        dashboardNew.setModifiedDate(inventoryVoucher.getDocumentDate());
                        dashboardNew.setDate(LocalDate.from(inventoryVoucher.getDocumentDate()));

                        dashboardNew.setBatteryPercentage(employeeProfile.getBatteryPercentage());

                        dashboardNew.setSubmissionCount(1);
                        dashboardNew.setAmount(inventoryVoucher.getDocumentTotal());

                        dashboardNew.setLocationType(executionDTO.getLocationType());
                        dashboardNew.setGpsOff(executionDTO.getIsGpsOff());
                        dashboardNew.setMobileDataOff(executionDTO.getIsMobileDataOff());
                        dashboardNew.setMockLocationStatus(executionDTO.getMockLocationStatus());
                        dashboardNew.setExePid(executionDTO.getPid());
                        dashboardNew.setLatitude(executionDTO.getLatitude());

                        dashboardNew.setLastCustomerLocation(inventoryVoucher.getReceiverAccount().getLocation());
                        dashboardNew.setLocation(executionDTO.getLocation());


                        if(executionDTO.getExecutiveTaskPlan() == null){
                            dashboardNew.setUnplannedCount(1);
                            dashboardNew.setUnplannedAmount(inventoryVoucher.getDocumentTotal());
                        }else{
                            dashboardNew.setPlanedCount(1);
                            dashboardNew.setPlannedAmount(inventoryVoucher.getDocumentTotal());
                        }

                        DashboardNew result = dashboardDenormalizedRepository.save(dashboardNew);
                        log.debug("inventoryVoucher : saved dashboardNew : " + result != null ? "saved" : "notSaved"  );
                    } else {
                        try {
                            if(executionDTO.getExecutiveTaskPlan() == null){
                                dashboardNew = dashboardNew1.get();

                                dashboardNew.setSubmissionCount(dashboardNew.getSubmissionCount() + 1);
                                dashboardNew.setUnplannedCount(dashboardNew.getUnplannedCount() + 1);

                                dashboardNew.setAmount(dashboardNew.getAmount() + inventoryVoucher.getDocumentTotal());
                                dashboardNew.setUnplannedAmount(dashboardNew.getUnplannedAmount() + inventoryVoucher.getDocumentTotal());

                                dashboardNew.setModifiedDate(inventoryVoucher.getDocumentDate());
                                dashboardNew.setLocationType(executionDTO.getLocationType());
                                dashboardNew.setGpsOff(executionDTO.getIsGpsOff());
                                dashboardNew.setMobileDataOff(executionDTO.getIsMobileDataOff());
                                dashboardNew.setMockLocationStatus(executionDTO.getMockLocationStatus());
                                dashboardNew.setExePid(executionDTO.getPid());
                                dashboardNew.setLatitude(executionDTO.getLatitude());

                                dashboardNew.setLastCustomerLocation(inventoryVoucher.getReceiverAccount().getLocation());
                                dashboardNew.setLocation(executionDTO.getLocation());

                                DashboardNew resulPlannedUpdate = dashboardDenormalizedRepository.save(dashboardNew);
                                log.debug("  " + resulPlannedUpdate != null ? "Unplanned Updation Sucess" : "Result unplanned Not Updated "  );
                            }else{
                                dashboardNew = dashboardNew1.get();
                                dashboardNew.setSubmissionCount(dashboardNew.getSubmissionCount() + 1);
                                dashboardNew.setPlanedCount(dashboardNew.getUnplannedCount() + 1);

                                dashboardNew.setAmount(dashboardNew.getAmount());
                                dashboardNew.setPlannedAmount(dashboardNew.getPlannedAmount() + inventoryVoucher.getDocumentTotal());

                                dashboardNew.setModifiedDate(inventoryVoucher.getDocumentDate());
                                dashboardNew.setLocationType(executionDTO.getLocationType());
                                dashboardNew.setGpsOff(executionDTO.getIsGpsOff());
                                dashboardNew.setMobileDataOff(executionDTO.getIsMobileDataOff());
                                dashboardNew.setMockLocationStatus(executionDTO.getMockLocationStatus());
                                dashboardNew.setExePid(executionDTO.getPid());
                                dashboardNew.setLatitude(executionDTO.getLatitude());

                                dashboardNew.setLastCustomerLocation(inventoryVoucher.getReceiverAccount().getLocation());
                                dashboardNew.setLocation(executionDTO.getLocation());

                                DashboardNew resultUnplanned = dashboardDenormalizedRepository.save(dashboardNew);
                                log.debug("  " + resultUnplanned != null ? "Result planned Updation Sucess" : " Result planned Not Updated "  );
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

            }
        }
    }



    @Override
    public DashboardHeaderSummaryDTO loadDashboardHeaderSummary(
            LocalDateTime from, LocalDateTime to) {

        List<DashboardSummaryDTO> daySummaryDatas = new ArrayList<>();
        List<DashboardSummaryDTO> WeekSummaryDatas = new ArrayList<>();
        List<DashboardSummaryDTO> monthSummaryDatas = new ArrayList<>();

        TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
        LocalDateTime weekStartDate = from.with(fieldISO, 1);
        log.debug("week Start : "+String.valueOf(weekStartDate));
        LocalDateTime monthStartDate = from.withDayOfMonth(1);
        log.debug("month Start Date : "+String.valueOf(monthStartDate));

        List<DashboardItem> dashboardItems =
                dashboardItemUserRepository
                        .findDashboardItemByUserLogin(
                                SecurityUtils.getCurrentUserLogin());

        if (dashboardItems.isEmpty()) {
            dashboardItems = dashboardItemRepository.findAllByCompanyId();
        }

        List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();

        List<ExecutiveTaskPlan> scheduledTasksToday = executiveTaskPlanRepository
                .findAllByCompanyIdAndUserIdInAndPlannedDateBetween(
                        SecurityUtils.getCurrentUsersCompanyId(),userIds,from,to);

        List<ExecutiveTaskPlan> scheduledTasksweek = executiveTaskPlanRepository
                .findAllByCompanyIdAndUserIdInAndPlannedDateBetween(
                        SecurityUtils.getCurrentUsersCompanyId(),userIds,weekStartDate,to);

        List<ExecutiveTaskPlan> scheduledTasksMonth = executiveTaskPlanRepository
                .findAllByCompanyIdAndUserIdInAndPlannedDateBetween(
                        SecurityUtils.getCurrentUsersCompanyId(),userIds,monthStartDate,to);

        List<DashboardNew> dashboardDayList = dashboardDenormalizedRepository
                .findAllByCompanyIdAndActivityIdsInAndUserIdsinAndDate(
                        SecurityUtils.getCurrentUsersCompanyId(),userIds,
                        from.toLocalDate(),to.toLocalDate());

        List<DashboardNew> dashboardWeekList = dashboardDenormalizedRepository
                .findAllByCompanyIdAndActivityIdsInAndUserIdsinAndDate(
                        SecurityUtils.getCurrentUsersCompanyId(),userIds,
                        weekStartDate.toLocalDate(),to.toLocalDate());

        List<DashboardNew> dashboardMonthList = dashboardDenormalizedRepository
                .findAllByCompanyIdAndActivityIdsInAndUserIdsinAndDate(
                        SecurityUtils.getCurrentUsersCompanyId(),userIds,
                        monthStartDate.toLocalDate(),to.toLocalDate());

        for (DashboardItem dashboardItem : dashboardItems) {

            if (dashboardItem.getDashboardItemType().equals(DashboardItemType.ACTIVITY)) {
                Long companyId = dashboardItem.getCompany().getId();

                List<Long> activityIds =
                        dashboardItem.getActivities()
                                .parallelStream().map(
                                        p -> p.getId())
                                .collect(Collectors.toList());

                daySummaryDatas.add(activityWiseSummeryFilter(scheduledTasksToday,
                        dashboardDayList, dashboardItem, activityIds));

                WeekSummaryDatas.add(activityWiseSummeryFilter(scheduledTasksweek,
                        dashboardWeekList, dashboardItem, activityIds));

                monthSummaryDatas.add(activityWiseSummeryFilter(scheduledTasksMonth,
                        dashboardMonthList, dashboardItem, activityIds));

            }else if(dashboardItem.getDashboardItemType().equals(DashboardItemType.DOCUMENT)){

                List<Long> documentIds =
                        dashboardItem.getDocuments()
                                .parallelStream().map(
                                        p -> p.getId())
                                .collect(Collectors.toList());

                daySummaryDatas.add(documentWiseDaySummeryFilter(
                        dashboardDayList,documentIds,dashboardItem));

                WeekSummaryDatas.add(documentWiseDaySummeryFilter(
                        dashboardWeekList,documentIds,dashboardItem));

                monthSummaryDatas.add(documentWiseDaySummeryFilter(
                        dashboardWeekList,documentIds,dashboardItem));
            }else if(dashboardItem.getDashboardItemType().equals(DashboardItemType.TARGET)){
                DashboardSummaryDTO dashboardSummaryDto = new DashboardSummaryDTO();
                dashboardSummaryDto.setDashboardItemPid(dashboardItem.getPid());
                dashboardSummaryDto.setLabel(dashboardItem.getName());
                dashboardSummaryDto.setDashboardItemType(dashboardItem.getDashboardItemType());
                dashboardSummaryDto.setTaskPlanType(dashboardItem.getTaskPlanType());
                dashboardSummaryDto.setNumberCircle(false);
                daySummaryDatas.add(assignTargetBlockAchievedAndPlanned(dashboardItem, dashboardSummaryDto, from, to, userIds, null));
                WeekSummaryDatas.add(assignTargetBlockAchievedAndPlanned(dashboardItem, dashboardSummaryDto, weekStartDate, to, userIds, null));
                monthSummaryDatas.add(assignTargetBlockAchievedAndPlanned(dashboardItem, dashboardSummaryDto, monthStartDate, to, userIds, null));
            }else if(dashboardItem.getDashboardItemType().equals(DashboardItemType.PRODUCT)){
                DashboardSummaryDTO dashboardSummaryDto = new DashboardSummaryDTO();
                dashboardSummaryDto.setDashboardItemPid(dashboardItem.getPid());
                dashboardSummaryDto.setLabel(dashboardItem.getName());
                dashboardSummaryDto.setDashboardItemType(dashboardItem.getDashboardItemType());
                dashboardSummaryDto.setTaskPlanType(dashboardItem.getTaskPlanType());
                dashboardSummaryDto.setNumberCircle(false);
                daySummaryDatas.add(assignProductAmountAndVolume(dashboardItem, dashboardSummaryDto, from, to, userIds, null));
                WeekSummaryDatas.add(assignProductAmountAndVolume(dashboardItem, dashboardSummaryDto, weekStartDate, to, userIds, null));
                monthSummaryDatas.add(assignProductAmountAndVolume(dashboardItem, dashboardSummaryDto, monthStartDate, to, userIds, null));
            }
        }
        DashboardHeaderSummaryDTO dashboardHeaderSummaryDTO = new DashboardHeaderSummaryDTO();
        dashboardHeaderSummaryDTO.setDaySummaryDatas(daySummaryDatas);
        dashboardHeaderSummaryDTO.setWeekSummaryDatas(WeekSummaryDatas);
        dashboardHeaderSummaryDTO.setMonthSummaryDatas(monthSummaryDatas);
        return dashboardHeaderSummaryDTO;
    }


    private DashboardSummaryDTO  activityWiseSummeryFilter(
            List<ExecutiveTaskPlan> executiveTaskPlans,
            List<DashboardNew> dashboardNewList, DashboardItem dashboardItem,
            List<Long> activityIds) {

        DashboardSummaryDTO dashboardSummaryDto = getDashboardSummaryDTO(dashboardItem);


        long scheduled = executiveTaskPlans.parallelStream()
                .filter(data-> activityIds.contains(data.getActivity().getId())).count();

        dashboardSummaryDto.setScheduled(scheduled);

        List<DashboardNew> fillteredList = dashboardNewList.parallelStream()
                .filter(data-> activityIds.contains(data.getActivityId()))
                .collect(Collectors.toList());

        if(dashboardItem.getTaskPlanType() == TaskPlanType.PLANNED){
            long sum = fillteredList.parallelStream()
                    .mapToLong(DashboardNew::getPlanedCount)
                    .sum();
            dashboardSummaryDto.setAchieved(sum);
        }else if(dashboardItem.getTaskPlanType() == TaskPlanType.UN_PLANNED){
            long sum = fillteredList.parallelStream()
                    .mapToLong(DashboardNew::getUnplannedCount)
                    .sum();
            dashboardSummaryDto.setAchieved(sum);
        }else if(dashboardItem.getTaskPlanType() == TaskPlanType.BOTH){
            long sum = fillteredList.parallelStream()
                    .mapToLong(DashboardNew::getSubmissionCount)
                    .sum();
            dashboardSummaryDto.setAchieved(sum);
        }
        return dashboardSummaryDto;
    }


    private DashboardSummaryDTO  singleUseractivityWiseSummeryFilter(
            List<ExecutiveTaskPlan> executiveTaskPlans,
            List<DashboardNew> dashboardNewList, DashboardItem dashboardItem,
            List<Long> activityIds,long userId) {


        DashboardSummaryDTO dashboardSummaryDto = getDashboardSummaryDTO(dashboardItem);

        long scheduled = executiveTaskPlans.parallelStream()
                .filter(data-> activityIds.contains(data.getActivity().getId()))
                .filter(data->data.getUser().getId().equals(userId))
                .count();

        dashboardSummaryDto.setScheduled(scheduled);

        List<DashboardNew> fillteredList = dashboardNewList.parallelStream()
                .filter(data-> activityIds.contains(data.getActivityId()))
                .filter(data->data.getUserId().equals(userId))
                .collect(Collectors.toList());

        if(dashboardItem.getTaskPlanType() == TaskPlanType.PLANNED){
            long sum = fillteredList.parallelStream()
                    .mapToLong(DashboardNew::getPlanedCount)
                    .sum();
            dashboardSummaryDto.setAchieved(sum);
        }else if(dashboardItem.getTaskPlanType() == TaskPlanType.UN_PLANNED){
            long sum = fillteredList.parallelStream()
                    .mapToLong(DashboardNew::getUnplannedCount)
                    .sum();
            dashboardSummaryDto.setAchieved(sum);
        }else if(dashboardItem.getTaskPlanType() == TaskPlanType.BOTH){
            long sum = fillteredList.parallelStream()
                    .mapToLong(DashboardNew::getSubmissionCount)
                    .sum();
            dashboardSummaryDto.setAchieved(sum);
        }
        return dashboardSummaryDto;
    }



    private DashboardSummaryDTO  documentWiseDaySummeryFilter(
            List<DashboardNew> dashboardNewList,
            List<Long> documentIds,
            DashboardItem dashboardItem) {

        DashboardSummaryDTO dashboardSummaryDto = getDashboardSummaryDTO(dashboardItem);
        List<DashboardNew> fillteredList = new ArrayList<>();

        if(dashboardItem.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)){
            fillteredList = dashboardNewList.parallelStream()
                    .filter(data->data.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER.toString()))
                    .filter(data-> documentIds.contains(data.getDocumentId()))
                    .collect(Collectors.toList());
        }else if(dashboardItem.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)){
            fillteredList = dashboardNewList.parallelStream()
                    .filter(data-> data.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER.toString()))
                    .filter(data-> documentIds.contains(data.getDocumentId()))
                    .collect(Collectors.toList());
        }else if(dashboardItem.getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT)){
            fillteredList = dashboardNewList.parallelStream()
                    .filter(data-> data.getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT.toString()))
                    .filter(data-> documentIds.contains(data.getDocumentId()))
                    .collect(Collectors.toList());
        }

        if(dashboardItem.getTaskPlanType() == TaskPlanType.PLANNED){
            long sum = fillteredList.parallelStream()
                    .mapToLong(DashboardNew::getPlanedCount)
                    .sum();
            double amount = fillteredList.parallelStream()
                    .mapToDouble(DashboardNew::getPlannedAmount)
                    .sum();
            dashboardSummaryDto.setCount(sum);
            dashboardSummaryDto.setAmount(amount);
        }else if(dashboardItem.getTaskPlanType() == TaskPlanType.UN_PLANNED){
            long sum = fillteredList.parallelStream()
                    .mapToLong(DashboardNew::getUnplannedCount)
                    .sum();
            double amount = fillteredList.parallelStream()
                    .mapToDouble(DashboardNew::getUnplannedAmount)
                    .sum();
            dashboardSummaryDto.setCount(sum);
            dashboardSummaryDto.setAmount(amount);
        }else if(dashboardItem.getTaskPlanType() == TaskPlanType.BOTH){
            long sum = fillteredList.parallelStream()
                    .mapToLong(DashboardNew::getSubmissionCount)
                    .sum();
            double amount = fillteredList.parallelStream()
                    .mapToDouble(DashboardNew::getAmount)
                    .sum();
            dashboardSummaryDto.setAmount(amount);
            dashboardSummaryDto.setCount(sum);
        }
        return dashboardSummaryDto;
    }



    private DashboardSummaryDTO  singleUserDocumentWiseDaySummeryFilter(
            List<DashboardNew> dashboardNewList,
            List<Long> documentIds,
            DashboardItem dashboardItem,long userId) {

        DashboardSummaryDTO dashboardSummaryDto = getDashboardSummaryDTO(dashboardItem);
        List<DashboardNew> fillteredList = new ArrayList<>();

        if(dashboardItem.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)){
            fillteredList = dashboardNewList.parallelStream()
                    .filter(data->data.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER.toString()))
                    .filter(data-> documentIds.contains(data.getDocumentId()))
                    .filter(data->data.getUserId().equals(userId))
                    .collect(Collectors.toList());
        }else if(dashboardItem.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)){
            fillteredList = dashboardNewList.parallelStream()
                    .filter(data-> data.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER.toString()))
                    .filter(data-> documentIds.contains(data.getDocumentId()))
                    .filter(data->data.getUserId().equals(userId))
                    .collect(Collectors.toList());
        }else if(dashboardItem.getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT)){
            fillteredList = dashboardNewList.parallelStream()
                    .filter(data-> data.getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT.toString()))
                    .filter(data-> documentIds.contains(data.getDocumentId()))
                    .filter(data->data.getUserId().equals(userId))
                    .collect(Collectors.toList());
        }


        if(dashboardItem.getTaskPlanType() == TaskPlanType.PLANNED){
            long sum = fillteredList.parallelStream()
                    .mapToLong(DashboardNew::getPlanedCount)
                    .sum();
            double amount = fillteredList.parallelStream()
                    .mapToDouble(DashboardNew::getPlannedAmount)
                    .sum();
            dashboardSummaryDto.setCount(sum);
            dashboardSummaryDto.setAmount(amount);
        }else if(dashboardItem.getTaskPlanType() == TaskPlanType.UN_PLANNED){
            long sum = fillteredList.parallelStream()
                    .mapToLong(DashboardNew::getUnplannedCount)
                    .sum();
            double amount = fillteredList.parallelStream()
                    .mapToDouble(DashboardNew::getUnplannedAmount)
                    .sum();
            dashboardSummaryDto.setCount(sum);
            dashboardSummaryDto.setAmount(amount);
        }else if(dashboardItem.getTaskPlanType() == TaskPlanType.BOTH){
            long sum = fillteredList.parallelStream()
                    .mapToLong(DashboardNew::getSubmissionCount)
                    .sum();
            double amount = fillteredList.parallelStream()
                    .mapToDouble(DashboardNew::getAmount)
                    .sum();
            dashboardSummaryDto.setAmount(amount);
            dashboardSummaryDto.setCount(sum);
        }
        return dashboardSummaryDto;
    }



    private BarChartDTO chartActivityWiseSummeryFilter(
            List<DashboardNew> dashboardNewList, DashboardItem dashboardItem,
            List<Long> activityIds,BarChartDTO barChartDTO) {

        List<DashboardNew> fillteredList = dashboardNewList.parallelStream()
                .filter(data-> activityIds.contains(data.getActivityId()))
                .collect(Collectors.toList());

        if(dashboardItem.getTaskPlanType() == TaskPlanType.PLANNED){
            long sum = fillteredList.parallelStream()
                    .mapToLong(DashboardNew::getPlanedCount)
                    .sum();
            barChartDTO.setValue(String.valueOf(sum));
        }else if(dashboardItem.getTaskPlanType() == TaskPlanType.UN_PLANNED){
            long sum = fillteredList.parallelStream()
                    .mapToLong(DashboardNew::getUnplannedCount)
                    .sum();
            barChartDTO.setValue(String.valueOf(sum));
        }else if(dashboardItem.getTaskPlanType() == TaskPlanType.BOTH){
            long sum = fillteredList.parallelStream()
                    .mapToLong(DashboardNew::getSubmissionCount)
                    .sum();
            barChartDTO.setValue(String.valueOf(sum));
        }
        return barChartDTO;
    }


    private BarChartDTO  chartDocumentWiseDaySummeryFilter(
            List<DashboardNew> dashboardNewList,
            List<Long> documentIds,
            DashboardItem dashboardItem,BarChartDTO barChartDTO) {

        List<DashboardNew> fillteredList = new ArrayList<>();

        if(dashboardItem.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)){
            fillteredList = dashboardNewList.parallelStream()
                    .filter(data->data.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER.toString()))
                    .filter(data-> documentIds.contains(data.getDocumentId()))
                    .collect(Collectors.toList());

            if(dashboardItem.getTaskPlanType() == TaskPlanType.PLANNED){
                double amount = fillteredList.parallelStream()
                        .mapToDouble(DashboardNew::getPlannedAmount)
                        .sum();
                barChartDTO.setValue(String.valueOf(amount));
            }else if(dashboardItem.getTaskPlanType() == TaskPlanType.UN_PLANNED){
                long sum = fillteredList.parallelStream()
                        .mapToLong(DashboardNew::getUnplannedCount)
                        .sum();
                double amount = fillteredList.parallelStream()
                        .mapToDouble(DashboardNew::getUnplannedAmount)
                        .sum();
                barChartDTO.setValue(String.valueOf(amount));
            }else if(dashboardItem.getTaskPlanType() == TaskPlanType.BOTH){
                double amount = fillteredList.parallelStream()
                        .mapToDouble(DashboardNew::getAmount)
                        .sum();
                barChartDTO.setValue(String.valueOf(amount));
            }
        }else if(dashboardItem.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)){
            fillteredList = dashboardNewList.parallelStream()
                    .filter(data-> data.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER.toString()))
                    .filter(data-> documentIds.contains(data.getDocumentId()))
                    .collect(Collectors.toList());

            if(dashboardItem.getTaskPlanType() == TaskPlanType.PLANNED){
                double amount = fillteredList.parallelStream()
                        .mapToDouble(DashboardNew::getPlannedAmount)
                        .sum();
                barChartDTO.setValue(String.valueOf(amount));
            }else if(dashboardItem.getTaskPlanType() == TaskPlanType.UN_PLANNED){
                double amount = fillteredList.parallelStream()
                        .mapToDouble(DashboardNew::getUnplannedAmount)
                        .sum();
                  barChartDTO.setValue(String.valueOf(amount));
            }else if(dashboardItem.getTaskPlanType() == TaskPlanType.BOTH){
                double amount = fillteredList.parallelStream()
                        .mapToDouble(DashboardNew::getAmount)
                        .sum();
                barChartDTO.setValue(String.valueOf(amount));
            }
        }else if(dashboardItem.getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT)){

            fillteredList = dashboardNewList.parallelStream()
                    .filter(data-> data.getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT.toString()))
                    .filter(data-> documentIds.contains(data.getDocumentId()))
                    .collect(Collectors.toList());

            if(dashboardItem.getTaskPlanType() == TaskPlanType.PLANNED){
                long sum = fillteredList.parallelStream()
                        .mapToLong(DashboardNew::getPlanedCount)
                        .sum();
                barChartDTO.setValue(String.valueOf(sum));
            }else if(dashboardItem.getTaskPlanType() == TaskPlanType.UN_PLANNED){
                long sum = fillteredList.parallelStream()
                        .mapToLong(DashboardNew::getUnplannedCount)
                        .sum();
                barChartDTO.setValue(String.valueOf(sum));
            }else if(dashboardItem.getTaskPlanType() == TaskPlanType.BOTH){
                long sum = fillteredList.parallelStream()
                        .mapToLong(DashboardNew::getSubmissionCount)
                        .sum();
                barChartDTO.setValue(String.valueOf(sum));
            }
        }
        return barChartDTO;
    }


    private static DashboardSummaryDTO getDashboardSummaryDTO(
            DashboardItem dashboardItem) {
        DashboardSummaryDTO dashboardSummaryDto = new DashboardSummaryDTO();
        dashboardSummaryDto.setDashboardItemPid(dashboardItem.getPid());
        dashboardSummaryDto.setLabel(dashboardItem.getName());
        dashboardSummaryDto.setDashboardItemType(dashboardItem.getDashboardItemType());
        dashboardSummaryDto.setTaskPlanType(dashboardItem.getTaskPlanType());
        dashboardSummaryDto.setNumberCircle(false);
        return dashboardSummaryDto;
    }



    @Override
    public List<DashboardUserDataDTO<DashboardSummaryDTO>> loadUserSummaryData(
            LocalDateTime from, LocalDateTime to,Long parentLocationId,User user) {

        List<DashboardItem> dashboardItems = dashboardItemUserRepository
                .findDashboardItemByUserLogin(user.getLogin());
        if (dashboardItems.isEmpty()) {
            dashboardItems = dashboardItemRepository.findAllByCompanyId();
        }
        List<DashboardUserDataDTO<DashboardSummaryDTO>> dashboardUserDatas = new ArrayList<>();
        List<UserDTO> dashboardUsers = getDashboardUsers(parentLocationId,user);

        List<EmployeeProfile> employeeProfileList =
                employeeProfileRepository.findAllByCompanyId(user.getCompany().getId());

        List<Attendance> presentUsers = attendanceRepository
                .findAllByCompanyIdAndAttendanceStatusAndPlannedDateBetweenOrderByCreatedDateDesc(
                        user.getCompany().getId(),
                        AttendanceStatus.PRESENT,
                        from,
                        to);

        List<Long> userIds = new ArrayList<>();
        userIds = dashboardUsers.parallelStream().map(UserDTO::getId).collect(Collectors.toList());

        List<ExecutiveTaskPlan> scheduledUserTasksDayWiseList = executiveTaskPlanRepository
                .findAllByCompanyIdAndUserIdInAndPlannedDateBetween(
                        SecurityUtils.getCurrentUsersCompanyId(),userIds,from,to);

        List<DashboardNew> dashboardDataDayWiseList = dashboardDenormalizedRepository
                .findAllByCompanyIdAndActivityIdsInAndUserIdsinAndDate(
                        SecurityUtils.getCurrentUsersCompanyId(),userIds,
                        from.toLocalDate(),to.toLocalDate());

        for(UserDTO userDTO :  dashboardUsers){
            log.debug("user : " + userDTO.getLogin());
            DashboardUserDataDTO<DashboardSummaryDTO> dashboardUserData = new DashboardUserDataDTO();
            Optional<Attendance> attendanceOptional = presentUsers.parallelStream()
                    .filter(data -> data.getUser().getId().equals(userDTO.getId())).findAny();
            if(attendanceOptional.isPresent()){
                dashboardUserData.setAttendanceMarked(true);
                dashboardUserData.setAttendanceStatus("true");
                dashboardUserData.setRemarks(attendanceOptional.get().getRemarks());
                dashboardUserData.setRemarks(attendanceOptional.get().getRemarks());
                dashboardUserData.setPlannedDate(attendanceOptional.get().getPlannedDate());
            }else{
                dashboardUserData.setAttendanceMarked(false);
                dashboardUserData.setRemarks("");
            }

            employeeProfileList.forEach(data->{
                if(data.getUser().getPid().equals(userDTO.getPid())){
                    dashboardUserData.setEmployeePid(data.getPid());
                    dashboardUserData.setEmployeeName(data.getName());
                    dashboardUserData.setUserName(data.getName());
                    dashboardUserData.setProfileImage(data.getProfileImage());
                }
            });

            userIds.add(userDTO.getId());
            List<DashboardSummaryDTO> userSummaryData = new ArrayList<>();
            dashboardUserData.setUserPid(userDTO.getPid());

            userSummaryData = singleUserSummaryDatas(userDTO.getId(), from, to, dashboardItems,
                    scheduledUserTasksDayWiseList,dashboardDataDayWiseList);

            dashboardDataDayWiseList.parallelStream()
                    .filter(data -> data.getUserId().equals(userDTO.getId()))
                    .max(Comparator.comparing(DashboardNew::getModifiedDate)).ifPresent(data->{
                        dashboardUserData.setLastLocation(data.getLocation());
                        dashboardUserData.setLastAccountLocation(data.getLastCustomerLocation());
                        dashboardUserData.setLocationType(data.getLocationType());
                        dashboardUserData.setIsGpsOff(data.getGpsOff());
                        dashboardUserData.setIsMobileDataOff(data.getMobileDataOff());
                        dashboardUserData.setMockLocationStatus(data.getMockLocationStatus());
                        dashboardUserData.setTaskExecutionPid(data.getExePid());
                        dashboardUserData.setLatitude(data.getLatitude());
                        dashboardUserData.setLastTime(data.getModifiedDate());
                    });
            dashboardUserData.setUserSummaryData(userSummaryData);
            dashboardUserDatas.add(dashboardUserData);
        }
        return dashboardUserDatas;
    }



    @Override
    public DashboardChartDTO loadSummaryDenormalisedChart(LocalDateTime from, LocalDateTime to) {
        DashboardChartDTO dashboardChartDTO = new DashboardChartDTO();
        List<BarChartDTO> barChartDtos = new ArrayList<>();
        Optional<DashboardChartItem> opDashboardChartItem = dashboardChartItemRepository
                .findOneByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
        DashboardItem dashboardItem;
        dashboardChartDTO.setChartType(ChartType.BAR);
        if (opDashboardChartItem.isPresent()) {
            dashboardChartDTO.setChartLabel(opDashboardChartItem.get().getName());
            dashboardItem = opDashboardChartItem.get().getDashboardItem();
            log.debug("dashboard ITEM name : " + dashboardItem.getName());
        } else {
            dashboardItem = dashboardItemRepository
                    .findTopByCompanyIdOrderBySortOrderAsc(SecurityUtils.getCurrentUsersCompanyId());
            if (dashboardItem != null) {
                dashboardChartDTO.setChartLabel(dashboardItem.getName());
                log.debug("dashboard ITEM name !null : " + dashboardItem.getName());
            }
        }
        List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
        List<LocalDate> monthDates = getMonthsBetween(from.minusMonths(5).toLocalDate(), to.toLocalDate());
        Collections.sort(monthDates); // Sort in ascending order


        List<DashboardNew> dashboardDataDayWiseList = dashboardDenormalizedRepository
                .findAllByCompanyIdAndActivityIdsInAndUserIdsinAndDate(
                        SecurityUtils.getCurrentUsersCompanyId(),userIds,
                        from.minusMonths(5).toLocalDate(),to.toLocalDate());

        Map<Month, List<DashboardNew>> groupedByDate = dashboardDataDayWiseList.parallelStream()
                .collect(Collectors.groupingBy(data->data.getDate().getMonth()));

        for (LocalDate entry : monthDates) {
            Month date = entry.getMonth();
            List<DashboardNew> entryValue = groupedByDate.get(date);
            log.debug("Date : " + entry);
            log.debug("Enter : " + date.toString());
            log.debug("Month : " + date.name().substring(0, 3));
            BarChartDTO barChartDTO = new BarChartDTO();
            barChartDTO.setName(date.name().substring(0, 3));

            if (dashboardItem != null && entryValue != null && !entryValue.isEmpty()) {
                if (dashboardItem.getDashboardItemType().equals(DashboardItemType.ACTIVITY)) {
                    List<Long> activityIds =
                            dashboardItem.getActivities()
                                    .parallelStream().map(
                                            p -> p.getId())
                                    .collect(Collectors.toList());

                 chartActivityWiseSummeryFilter(entryValue,dashboardItem, activityIds,barChartDTO);
                }else if(dashboardItem.getDashboardItemType().equals(DashboardItemType.DOCUMENT)){
                    List<Long> documentIds =
                            dashboardItem.getDocuments()
                                    .parallelStream().map(
                                            p -> p.getId())
                                    .collect(Collectors.toList());

                 chartDocumentWiseDaySummeryFilter(
                         entryValue,documentIds,
                         dashboardItem,barChartDTO);
                }
            }
            barChartDtos.add(barChartDTO);
        }
        dashboardChartDTO.setBarChartDtos(barChartDtos);
        return dashboardChartDTO;
    }



    private  List<DashboardSummaryDTO> singleUserSummaryDatas(
            long userId,LocalDateTime from, LocalDateTime to,
            List<DashboardItem> dashboardItems ,
            List<ExecutiveTaskPlan> scheduledUserTasksDayWiseList,
            List<DashboardNew> dashboardDataDayWiseList){

        List<DashboardSummaryDTO> daySummaryDatas = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId);

        for (DashboardItem dashboardItem : dashboardItems) {

            if (dashboardItem.getDashboardItemType().equals(DashboardItemType.ACTIVITY)) {

                List<Long> activityIds =
                        dashboardItem.getActivities()
                                .parallelStream().map(
                                        p -> p.getId())
                                .collect(Collectors.toList());


                daySummaryDatas.add(singleUseractivityWiseSummeryFilter(scheduledUserTasksDayWiseList,
                        dashboardDataDayWiseList, dashboardItem, activityIds,userId));
            }else if(dashboardItem.getDashboardItemType().equals(DashboardItemType.DOCUMENT)){

                List<Long> documentIds =
                        dashboardItem.getDocuments()
                                .parallelStream().map(
                                        p -> p.getId())
                                .collect(Collectors.toList());

                daySummaryDatas.add(singleUserDocumentWiseDaySummeryFilter(
                        dashboardDataDayWiseList,documentIds,dashboardItem,userId));
            }
            else if(dashboardItem.getDashboardItemType().equals(DashboardItemType.TARGET)){
                DashboardSummaryDTO dashboardSummaryDto = new DashboardSummaryDTO();
                dashboardSummaryDto.setDashboardItemPid(dashboardItem.getPid());
                dashboardSummaryDto.setLabel(dashboardItem.getName());
                dashboardSummaryDto.setDashboardItemType(dashboardItem.getDashboardItemType());
                dashboardSummaryDto.setTaskPlanType(dashboardItem.getTaskPlanType());
                dashboardSummaryDto.setNumberCircle(false);
                daySummaryDatas.add(assignTargetBlockAchievedAndPlanned(dashboardItem, dashboardSummaryDto, from, to, userIds, null));
            }else if(dashboardItem.getDashboardItemType().equals(DashboardItemType.PRODUCT)){
                DashboardSummaryDTO dashboardSummaryDto = new DashboardSummaryDTO();
                dashboardSummaryDto.setDashboardItemPid(dashboardItem.getPid());
                dashboardSummaryDto.setLabel(dashboardItem.getName());
                dashboardSummaryDto.setDashboardItemType(dashboardItem.getDashboardItemType());
                dashboardSummaryDto.setTaskPlanType(dashboardItem.getTaskPlanType());
                dashboardSummaryDto.setNumberCircle(false);
                daySummaryDatas.add(assignProductAmountAndVolume(dashboardItem, dashboardSummaryDto, from, to, userIds, null));
            }
        }
        return daySummaryDatas;
    }


    private List<UserDTO> getDashboardUsers(Long parentLocationId,User currentUser) {
        List<UserDTO> dashboardUsers = new ArrayList<>();
        if (parentLocationId != null && parentLocationId > 0) {
            List<Long> locationIds = locationHierarchyService.getAllChildrenIdsByParentId(parentLocationId);
            Set<Long> uniqueUserIds = employeeProfileLocationRepository.findEmployeeUserIdsByLocationIdIn(locationIds);
            if (!uniqueUserIds.isEmpty()) {
                dashboardUsers = dashboardUserService.findUsersByUserIdIn(new ArrayList<>(uniqueUserIds));
            }
        } else {
            if (currentUser.getShowAllUsersData()) {
                log.info("get all users");
                dashboardUsers = dashboardUserService.findUsersByCompanyId();
            } else {
                log.info("get employee hierarchy users");
                List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
                dashboardUsers = dashboardUserService.findUsersByUserIdIn(userIds);
            }
        }
        return dashboardUsers;
    }


    @Override
    public void loadDatabetweenTimePeriod(LocalDate from, LocalDate to,long companyId) {

        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.atTime(23,59);

        log.debug("from Date : " + fromDate);
        log.debug("to Date : " + toDate);

        EmployeeProfile employeeProfile = employeeProfileRepository.findEmployeeProfileByUserLogin(SecurityUtils.getCurrentUserLogin());

        List<ExecutiveTaskExecution> executiveTaskExecutions =
                executiveTaskExecutionRepository.findAllByCompanyIdAndCreatedDateBetween(companyId,fromDate,toDate);

        log.debug("executiveTaskExecutions SIZE " + executiveTaskExecutions.size());

        Set<Long> longList =
                executiveTaskExecutions
                        .parallelStream().map(
                                p -> p.getId())
                        .collect(Collectors.toSet());

        log.debug("longList SIZE " + longList.size());

        List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository.findAllByExecutiveTaskExecutionIdIn(longList);
        List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository.findAllByExecutiveTaskExecutionIdIn(longList);
        List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository.findAllByExecutiveTaskExecutionIdIn(longList);


        log.debug("dynamicDocumentHeaders SIZE " + dynamicDocumentHeaders.size());
        log.debug("inventoryVoucherHeaders SIZE " + inventoryVoucherHeaders.size());
        log.debug("accountingVoucherHeaders SIZE " + accountingVoucherHeaders.size());
        int i=1;
        for(ExecutiveTaskExecution executiveTaskExecution :  executiveTaskExecutions){

            List<AccountingVoucherHeader> accountingVoucherHeaderList = accountingVoucherHeaders.parallelStream().filter(data -> executiveTaskExecution.getId().equals(data.getExecutiveTaskExecution().getId())).collect(Collectors.toList());
            List<InventoryVoucherHeader> inventoryVoucherHeaderList = inventoryVoucherHeaders.parallelStream().filter(data -> executiveTaskExecution.getId().equals(data.getExecutiveTaskExecution().getId())).collect(Collectors.toList());
            List<DynamicDocumentHeader> dynamicDocumentHeaderList = dynamicDocumentHeaders.parallelStream().filter(data -> executiveTaskExecution.getId().equals(data.getExecutiveTaskExecution().getId())).collect(Collectors.toList());

            log.debug("Execution  SIZE : " + i +"  : " + executiveTaskExecution.getPid());
            log.debug("current dynamicDocumentHeaders SIZE " + dynamicDocumentHeaderList.size());
            log.debug("current inventoryVoucherHeaders SIZE " + inventoryVoucherHeaderList.size());
            log.debug("current accountingVoucherHeaders SIZE " + accountingVoucherHeaderList.size());

            if(inventoryVoucherHeaderList !=null && inventoryVoucherHeaderList.size() != 0){
                savingInventoryDataToDashborad(executiveTaskExecution.getUser(),employeeProfile,inventoryVoucherHeaderList,executiveTaskExecution);
            }
            if(accountingVoucherHeaderList !=null && accountingVoucherHeaderList.size() != 0){
                savingAccountingDataToDashboard(executiveTaskExecution.getUser(),employeeProfile,accountingVoucherHeaderList,executiveTaskExecution);
            }
            if(dynamicDocumentHeaderList !=null && dynamicDocumentHeaderList.size() != 0){
                savingDynamicDataToDashboard(executiveTaskExecution.getUser(),employeeProfile,dynamicDocumentHeaderList,executiveTaskExecution);
            }
            i++;
        }
    }


    public ObjectMapper getObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        return mapper;
    }

    public <T> void convertToJson(Collection<T> lists) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(lists);
            log.debug("json Data : " + jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void convertToJson(Map<Month, List<DashboardNew>> monthList) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(monthList);
            log.debug("json Data : " + jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public static List<LocalDate> getMonthsBetween(LocalDate startDate, LocalDate endDate) {
        long numOfMonthsBetween = ChronoUnit.MONTHS.between(startDate, endDate.plusMonths(1));
        return IntStream.iterate(0, i -> i + 1).limit(numOfMonthsBetween).mapToObj(i -> startDate.plusMonths(i))
                .collect(Collectors.toList());
    }



//    ====================================================TARGET BASED=======================================================

    private DashboardSummaryDTO assignTargetBlockAchievedAndPlanned(DashboardItem dashboardItem,
                                                     DashboardSummaryDTO dashboardSummaryDto, LocalDateTime from, LocalDateTime to, List<Long> userIds,
                                                     String userPid) {
        List<TargetType> targetTypes = salesTargetReportSettingSalesTargetBlockRepository
                .findTargetTypeBySalesTargetBlockId(dashboardItem.getSalesTargetBlock().getId());
        List<SalesTargetGroupUserTarget> salesTargetGroupUserTargetList = Collections.emptyList();
        if (!targetTypes.isEmpty()) {
            if (userPid == null) {
                if (!userIds.isEmpty()) {
                    salesTargetGroupUserTargetList = salesTargetGroupUserTargetRepository
                            .findBySalesTargetGroupPidAndUserIdInAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
                                    dashboardItem.getSalesTargetGroup().getPid(), userIds, from.toLocalDate(),
                                    to.toLocalDate());
                } else {
                    salesTargetGroupUserTargetList = salesTargetGroupUserTargetRepository
                            .findBySalesTargetGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
                                    dashboardItem.getSalesTargetGroup().getPid(), from.toLocalDate(), to.toLocalDate());
                }
            } else {
                salesTargetGroupUserTargetList = salesTargetGroupUserTargetRepository
                        .findBySalesTargetGroupPidAndUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
                                dashboardItem.getSalesTargetGroup().getPid(), userPid, from.toLocalDate(),
                                to.toLocalDate());
            }
        }
        if (!salesTargetGroupUserTargetList.isEmpty()) {
            TargetType targetType = targetTypes.get(0);
            dashboardSummaryDto.setTargetType(targetTypes.get(0));

            String[] targetAchieved = getTargetBlockAchievedAndPlanned(dashboardItem, from, to,
                    salesTargetGroupUserTargetList, targetType, userPid);

            if (targetType.equals(TargetType.AMOUNT)) {
                double roundedTrgtAmt = new BigDecimal(targetAchieved[0]).setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();
                double roundedAchdAmt = new BigDecimal(targetAchieved[2]).setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();
                dashboardSummaryDto.setTargetAmount(roundedTrgtAmt);
                dashboardSummaryDto.setTargetAchievedAmount(roundedAchdAmt);
            }
            if (targetType.equals(TargetType.VOLUME)) {
                double roundedTrgtVlm = new BigDecimal(targetAchieved[1]).setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();
                double roundedAchdVlm = new BigDecimal(targetAchieved[3]).setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();
                double roundedAverage = new BigDecimal(targetAchieved[4]).setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();
                dashboardSummaryDto.setTargetVolume(roundedTrgtVlm);
                dashboardSummaryDto.setTargetAchievedVolume(roundedAchdVlm);
                dashboardSummaryDto.setTargetAverageVolume(roundedAverage);
            }
        }
        return dashboardSummaryDto;
    }

    private String[] getTargetBlockAchievedAndPlanned(
            DashboardItem dashboardItem, LocalDateTime from, LocalDateTime to,
            List<SalesTargetGroupUserTarget> salesTargetGroupUserTargetList, TargetType targetType, String userPid) {
        Double targetAmount = 0d;
        Double targetVolume = 0d;
        Double achievedAmount = 0d;
        Double achievedVolume = 0d;
        Double targetAvg = 0d;
        // get sales Target Group documents
        Set<Long> documentIds = salesTargetGroupDocumentRepository
                .findDocumentIdsBySalesTargetGroupPid(dashboardItem.getSalesTargetGroup().getPid());
        Set<Long> productProfileIds = salesTargetGroupProductRepository
                .findProductIdBySalesTargetGroupPid(dashboardItem.getSalesTargetGroup().getPid());
        for (SalesTargetGroupUserTarget salesTargetGroupUserTarget : salesTargetGroupUserTargetList) {
            targetAmount += salesTargetGroupUserTarget.getAmount();
            targetVolume += salesTargetGroupUserTarget.getVolume();
            if (!documentIds.isEmpty() && !productProfileIds.isEmpty()) {
                // get achieved amount
                if (targetType.equals(TargetType.AMOUNT)) {
                    Double amount = getAchievedAmount(salesTargetGroupUserTarget.getUser().getId(), documentIds,
                            productProfileIds, from, to, userPid);
                    achievedAmount += amount;
                }
                // get achieved volume
                if (targetType.equals(TargetType.VOLUME)) {
                    Double volume = getAchievedVolume(salesTargetGroupUserTarget.getUser().getId(), documentIds,
                            productProfileIds, from, to, userPid);
                    if (volume != null) {
                        achievedVolume += volume;
                        targetAvg += (volume / salesTargetGroupUserTarget.getVolume());
                    }
                }
            }
        }
        return new String[] { targetAmount.toString(), targetVolume.toString(), achievedAmount.toString(),
                achievedVolume.toString(), targetAvg.toString() };
    }


    private Double getAchievedAmount(Long targetUserId, Set<Long> documentIds, Set<Long> productProfileIds,
                                     LocalDateTime from, LocalDateTime to, String userPid) {
        Double amount;
        if (userPid == null) {
            amount = inventoryVoucherDetailRepository.sumOfAmountByUserIdAndDocumentsAndProductsAndCreatedDateBetween(
                    targetUserId, documentIds, productProfileIds, from, to);
        } else {
            amount = inventoryVoucherDetailRepository.sumOfAmountByUserPidAndDocumentsAndProductsAndCreatedDateBetween(
                    userPid, documentIds, productProfileIds, from, to);
        }
        return amount;
    }

    private Double getAchievedVolume(Long targetUserId, Set<Long> documentIds, Set<Long> productProfileIds,
                                     LocalDateTime from, LocalDateTime to, String userPid) {
        Double volume;
        if (userPid == null) {
            volume = inventoryVoucherDetailRepository.sumOfVolumeByUserIdAndDocumentsAndProductsAndCreatedDateBetween(
                    targetUserId, documentIds, productProfileIds, from, to);
        } else {
            volume = inventoryVoucherDetailRepository.sumOfVolumeByUserPidAndDocumentsAndProductsAndCreatedDateBetween(
                    userPid, documentIds, productProfileIds, from, to);
        }
        return volume;
    }


    //Attendence

     @Override
     public DashboardHeaderSummaryDTO loadDashboardAttendanceHeader(LocalDateTime from, LocalDateTime to) {
        List<DashboardAttendance> dashboardAttendances = dashboardAttendanceUserRepository
                .findDashboardAttendanceByUserLogin(SecurityUtils.getCurrentUserLogin());
        List<DashboardSummaryDTO> daySummaryDatas = new ArrayList<>();
        List<DashboardSummaryDTO> weekSummaryDatas = new ArrayList<>();
        List<DashboardSummaryDTO> monthSummaryDatas = new ArrayList<>();
        TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
        LocalDateTime weekStartDate = from.with(fieldISO, 1);
        LocalDateTime monthStartDate = from.withDayOfMonth(1);
        for (DashboardAttendance dashboardAttendance : dashboardAttendances) {
            DashboardSummaryDTO daySummary = new DashboardSummaryDTO();
            daySummary.setDashboardItemPid(dashboardAttendance.getId() + "");
            daySummary.setLabel(dashboardAttendance.getName());
            if (dashboardAttendance.getAttendanceStatus() != null) {


                daySummary.setCount(attendanceRepository.countByCompanyIdAndDateBetweenAndAttendanceStatus(from, to,
                        dashboardAttendance.getAttendanceStatus()));

            } else if (dashboardAttendance.getAttendanceStatusSubgroup() != null) {

                daySummary.setCount(attendanceRepository.countByCompanyIdAndDateBetweenAndAttendanceSubGroup(from, to,
                        dashboardAttendance.getAttendanceStatusSubgroup()));

            }
            DashboardSummaryDTO weekSummary = new DashboardSummaryDTO();
            weekSummary.setDashboardItemPid(dashboardAttendance.getId() + "");
            weekSummary.setLabel(dashboardAttendance.getName());
            if (dashboardAttendance.getAttendanceStatus() != null) {
                weekSummary.setCount(attendanceRepository.countByCompanyIdAndDateBetweenAndAttendanceStatus(
                        weekStartDate, to, dashboardAttendance.getAttendanceStatus()));
            } else if (dashboardAttendance.getAttendanceStatusSubgroup() != null) {
                weekSummary.setCount(attendanceRepository.countByCompanyIdAndDateBetweenAndAttendanceSubGroup(
                        weekStartDate, to, dashboardAttendance.getAttendanceStatusSubgroup()));
            }
            DashboardSummaryDTO monthSummary = new DashboardSummaryDTO();
            monthSummary.setDashboardItemPid(dashboardAttendance.getId() + "");
            monthSummary.setLabel(dashboardAttendance.getName());
            if (dashboardAttendance.getAttendanceStatus() != null) {
                monthSummary.setCount(attendanceRepository.countByCompanyIdAndDateBetweenAndAttendanceStatus(
                        monthStartDate, to, dashboardAttendance.getAttendanceStatus()));
            } else if (dashboardAttendance.getAttendanceStatusSubgroup() != null) {
                monthSummary.setCount(attendanceRepository.countByCompanyIdAndDateBetweenAndAttendanceSubGroup(
                        monthStartDate, to, dashboardAttendance.getAttendanceStatusSubgroup()));
            }
            daySummaryDatas.add(daySummary);
            weekSummaryDatas.add(weekSummary);
            monthSummaryDatas.add(monthSummary);
        }
        DashboardHeaderSummaryDTO dashboardDTO = new DashboardHeaderSummaryDTO();
        dashboardDTO.setDaySummaryDatas(daySummaryDatas);
        dashboardDTO.setWeekSummaryDatas(weekSummaryDatas);
        dashboardDTO.setMonthSummaryDatas(monthSummaryDatas);
        return dashboardDTO;
    }

    private DashboardSummaryDTO assignProductAmountAndVolume(DashboardItem dashboardItem, DashboardSummaryDTO dashboardSummaryDto,
                                              LocalDateTime from, LocalDateTime to, List<Long> userIds, String userPid) {
        if (!userIds.isEmpty()) {
            Set<Document> userDocuments = inventoryVoucherHeaderService.findDocumentsByUserIdIn(userIds);
            dashboardItem.getDocuments().retainAll(userDocuments);
        }
        List<Document> documents = new ArrayList<>(dashboardItem.getDocuments());
        Collections.sort(documents, (Document s1, Document s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
        if (!documents.isEmpty() && !dashboardItem.getProductGroups().isEmpty()) {
            Set<Long> productIds = productGroupProductRepository
                    .findProductIdByProductGroupIn(dashboardItem.getProductGroups());
            Object[] amountAndVolume = (Object[]) getProductAmountAndVolume(dashboardItem.getTaskPlanType(), documents,
                    productIds, from, to, userIds, userPid);
            if (amountAndVolume != null) {
                dashboardSummaryDto.setAmount(amountAndVolume[0] == null ? 0d : (double) amountAndVolume[0]);
                dashboardSummaryDto.setVolume(amountAndVolume[1] == null ? 0d : (double) amountAndVolume[1]);
            }
        }
        return dashboardSummaryDto;
    }


    private Object getProductAmountAndVolume(TaskPlanType taskPlanType, List<Document> documents, Set<Long> productIds,
                                             LocalDateTime from, LocalDateTime to, List<Long> userIds, String userPid) {
        Object obj = null;
        if (taskPlanType.equals(TaskPlanType.BOTH)) {
            if (userPid == null) {
                if (!userIds.isEmpty()) {
                    obj = inventoryVoucherDetailRepository
                            .getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUserIdIn(documents,
                                    productIds, from, to, userIds);
                } else {
                    obj = inventoryVoucherDetailRepository.getAmountAndVolumeByDocumentsInAndProductsInAndDateBetween(
                            documents, productIds, from, to);
                }
            } else {
                obj = inventoryVoucherDetailRepository
                        .getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUser(documents, productIds, from,
                                to, userPid);
            }
        } else if (taskPlanType.equals(TaskPlanType.PLANNED)) {
            obj = getPlannedProductAmountVolume(documents, productIds, from, to, userIds, userPid);
        } else if (taskPlanType.equals(TaskPlanType.UN_PLANNED)) {
            obj = getUnPlannedProductAmountVolume(documents, productIds, from, to, userIds, userPid);
        }
        return obj;
    }

    private Object getPlannedProductAmountVolume(List<Document> documents, Set<Long> productIds, LocalDateTime from,
                                                 LocalDateTime to, List<Long> userIds, String userPid) {
        Object obj = null;
        if (userPid == null) {
            if (!userIds.isEmpty()) {
                obj = inventoryVoucherDetailRepository
                        .getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUserIdInAndTaskPlanIsNotNull(
                                documents, productIds, from, to, userIds);
            } else {
                obj = inventoryVoucherDetailRepository
                        .getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndTaskPlanIsNotNull(documents,
                                productIds, from, to);
            }
        } else {
            obj = inventoryVoucherDetailRepository
                    .getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUserIdInAndTaskPlanIsNotNull(
                            documents, productIds, from, to, userPid);
        }
        return obj;
    }

    private Object getUnPlannedProductAmountVolume(List<Document> documents, Set<Long> productIds, LocalDateTime from,
                                                   LocalDateTime to, List<Long> userIds, String userPid) {
        Object obj = null;
        if (userPid == null) {
            if (!userIds.isEmpty()) {
                obj = inventoryVoucherDetailRepository
                        .getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUserIdInAndTaskPlanIsNull(
                                documents, productIds, from, to, userIds);
            } else {
                obj = inventoryVoucherDetailRepository
                        .getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndTaskPlanIsNull(documents,
                                productIds, from, to);
            }
        } else {
            obj = inventoryVoucherDetailRepository
                    .getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUserIdInAndTaskPlanIsNull(documents,
                            productIds, from, to, userPid);
        }
        return obj;
    }




}
