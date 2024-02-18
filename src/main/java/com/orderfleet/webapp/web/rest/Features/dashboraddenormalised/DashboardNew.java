package com.orderfleet.webapp.web.rest.Features.dashboraddenormalised;

import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.domain.enums.TaskPlanType;
import org.apache.poi.hssf.record.InterfaceEndRecord;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_dashboard_new")
public class DashboardNew {
    @Id
    @GenericGenerator(name = "seq_dashboard_new_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "seq_dashboard_new_id") })
    @GeneratedValue(generator = "seq_dashboard_new_id_GEN")
    @Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_dashboard_new_id')")
    private Long id;

    @NotNull
    @Column(name = "pid", unique = true, nullable = false, updatable = false)
    private String pid;

    @NotNull
    @Column(name = "document_pid" , nullable = false)
    private String documentPid;

    @NotNull
    @Column(name = "document_id", nullable = false)
    private Long documentId;

    private String documentName;

    @Column(name = "document_type")
    private String documentType;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "submission_count" , nullable = false, columnDefinition = "double precision DEFAULT 0")
    private long submissionCount;

    @NotNull
    @Column(name = "planned_count" , nullable = false, columnDefinition = "double precision DEFAULT 0")
    private long planedCount;

    @NotNull
    @Column(name = "unplanned_count" , nullable = false, columnDefinition = "double precision DEFAULT 0")
    private long unplannedCount;

    @NotNull
    @Column(name = "submission_count_activity" , nullable = false, columnDefinition = "double precision DEFAULT 0")
    private long submissionCountAcitivity;

    @NotNull
    @Column(name = "planned_count_activity" , nullable = false, columnDefinition = "double precision DEFAULT 0")
    private long planedCountAcitivity;

    @NotNull
    @Column(name = "unplanned_count_activity" , nullable = false, columnDefinition = "double precision DEFAULT 0")
    private long unplannedCountAcitivity;

    @NotNull
    @Column(name = "sheduled_count" , nullable = false, columnDefinition = "double precision DEFAULT 0")
    private double sheduled;

    @NotNull
    @Column(name = "amount" , nullable = false, columnDefinition = "double precision DEFAULT 0")
    private double amount;

    @NotNull
    @Column(name = "planned_amount" , nullable = false, columnDefinition = "double precision DEFAULT 0")
    private double plannedAmount;


    @NotNull
    @Column(name = "unplanned_amount" , nullable = false, columnDefinition = "double precision DEFAULT 0")
    private double unplannedAmount;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_pid")
    private String userPid;

    @Column(name = "territory_id")
    private Long territoryId;

    @Column(name = "territory_pid")
    private String territoryPid;

    private Long activityId;

    private String activityPid;

    private String activityName;
    private Long companyId;

    private String companyPid;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    private String location;
    private String LastCustomerLocation;

    private Integer batteryPercentage = 0;

    private LocationType locationType;

    @Column(name = "is_gps_off", columnDefinition = "boolean DEFAULT 'FALSE'")
    private boolean isGpsOff;

    private boolean isMobileDataOff;


    @Column(name = "mock_location_status", columnDefinition = "boolean DEFAULT 'FALSE'")
    private boolean mockLocationStatus;


    private String exePid;


    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    private Long customerTimeSpentTime;

//    public DashboardNew(Long id, String pid, String documentPid, LocalDate date, long submissionCount, double amount, Long userId
//            , String userPid, Long territoryId, String territoryPid, Long documentId, Long activityId, String activityPid, String activityName
//            , Long companyId, String companyPid, LocalDate createdDate, LocalDateTime modifiedDate, String location, String documentName, Integer batteryPercentage,long unplannedCount ,long planedCount) {
//        this.id = id;
//        this.pid = pid;
//        this.documentId = documentId;
//        this.documentPid = documentPid;
//        this.documentName = documentName;
//        this.date = date;
//        this.submissionCount = submissionCount;
//        this.amount = amount;
//        this.userId = userId;
//        this.userPid = userPid;
//        this.territoryId = territoryId;
//        this.territoryPid = territoryPid;
//        this.activityId = activityId;
//        this.activityPid = activityPid;
//        this.activityName = activityName;
//        this.companyId = companyId;
//        this.companyPid = companyPid;
//        this.createdDate = createdDate;
//        this.modifiedDate = modifiedDate;
//        this.location = location;
//        this.batteryPercentage = batteryPercentage;
//        this.planedCount = planedCount;
//        this.unplannedCount = unplannedCount;
//    }

    public DashboardNew() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDocumentPid() {
        return documentPid;
    }

    public void setDocumentPid(String documentPid) {
        this.documentPid = documentPid;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getSubmissionCount() {
        return submissionCount;
    }

    public void setSubmissionCount(long submissionCount) {
        this.submissionCount = submissionCount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserPid() {
        return userPid;
    }

    public void setUserPid(String userPid) {
        this.userPid = userPid;
    }

    public Long getTerritoryId() {
        return territoryId;
    }

    public void setTerritoryId(Long territoryId) {
        this.territoryId = territoryId;
    }

    public String getTerritoryPid() {
        return territoryPid;
    }

    public void setTerritoryPid(String territoryPid) {
        this.territoryPid = territoryPid;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getActivityPid() {
        return activityPid;
    }

    public void setActivityPid(String activityPid) {
        this.activityPid = activityPid;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyPid() {
        return companyPid;
    }

    public void setCompanyPid(String companyPid) {
        this.companyPid = companyPid;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Integer getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(Integer batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public long getPlanedCount() {
        return planedCount;
    }

    public void setPlanedCount(long planedCount) {
        this.planedCount = planedCount;
    }

    public long getUnplannedCount() {
        return unplannedCount;
    }

    public void setUnplannedCount(long unplannedCount) {
        this.unplannedCount = unplannedCount;
    }

    public double getSheduled() {
        return sheduled;
    }

    public void setSheduled(double sheduled) {
        this.sheduled = sheduled;
    }

    public double getPlannedAmount() {
        return plannedAmount;
    }

    public void setPlannedAmount(double plannedAmount) {
        this.plannedAmount = plannedAmount;
    }

    public double getUnplannedAmount() {
        return unplannedAmount;
    }

    public void setUnplannedAmount(double unplannedAmount) {
        this.unplannedAmount = unplannedAmount;
    }


    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }


    public boolean getGpsOff() {
        return isGpsOff;
    }

    public void setGpsOff(boolean gpsOff) {
        isGpsOff = gpsOff;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public boolean getMockLocationStatus() {
        return mockLocationStatus;
    }

    public void setMockLocationStatus(boolean mockLocationStatus) {
        this.mockLocationStatus = mockLocationStatus;
    }

    public String getExePid() {
        return exePid;
    }

    public void setExePid(String exePid) {
        this.exePid = exePid;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public boolean getMobileDataOff() {
        return isMobileDataOff;
    }

    public void setMobileDataOff(boolean mobileDataOff) {
        isMobileDataOff = mobileDataOff;
    }

    public Long getCustomerTimeSpentTime() {
        return customerTimeSpentTime;
    }

    public void setCustomerTimeSpentTime(Long customerTimeSpentTime) {
        this.customerTimeSpentTime = customerTimeSpentTime;
    }

    public String getLastCustomerLocation() {
        return LastCustomerLocation;
    }

    public void setLastCustomerLocation(String lastCustomerLocation) {
        LastCustomerLocation = lastCustomerLocation;
    }

    public long getPlanedCountAcitivity() {
        return planedCountAcitivity;
    }

    public void setPlanedCountAcitivity(long planedCountAcitivity) {
        this.planedCountAcitivity = planedCountAcitivity;
    }

    public long getUnplannedCountAcitivity() {
        return unplannedCountAcitivity;
    }

    public void setUnplannedCountAcitivity(long unplannedCountAcitivity) {
        this.unplannedCountAcitivity = unplannedCountAcitivity;
    }


    public long getSubmissionCountAcitivity() {
        return submissionCountAcitivity;
    }

    public void setSubmissionCountAcitivity(long submissionCountAcitivity) {
        this.submissionCountAcitivity = submissionCountAcitivity;
    }
}
