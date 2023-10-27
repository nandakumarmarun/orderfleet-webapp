package com.orderfleet.webapp.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "tbl_kilometer_calculation_denormalised")
public class KilometerCalculationDenormalised {

    @Id
    @GenericGenerator(name = "seq_kilometer_calculation_denormalised_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "seq_kilometer_calculation_denormalised_id") })
    @GeneratedValue(generator = "seq_kilometer_calculation_denormalised_id_GEN")
    @Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_kilometer_calculation_denormalised_id')")
    private Long id;

    @Column(name = "START_LOCATION")
    private String startLocation;

    @Column(name = "END_LOCATION")
    private String endLocation;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_pid")
    private String UserPid;

    @Column(name = "exe_created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime exeCreatedDate;

    @Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @Column(name = "punch_in_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime punchingDate;

    @Column(name = "employee_pid")
    private String employeePid;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "executivetaskexecution_pid")
    private String taskExecutionPid;

    @Column(name = "accountprofile_pid")
    private String accountProfilePid;

    @Column(name = "accountprofile_name")
    private String accountProfileName;

    @Column(name = "KILOMETRE", nullable = false)
    private double kilometre;

    @Column(name = "METRES")
    private double metres;

    @Column(name = "company_id")
    private long companyId;

    @Column(name = "location")
    private String Location;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    private Boolean isCalculated;

    public KilometerCalculationDenormalised() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPid() {
        return UserPid;
    }

    public void setUserPid(String userPid) {
        UserPid = userPid;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getPunchingDate() {
        return punchingDate;
    }

    public void setPunchingDate(LocalDateTime punchingDate) {
        this.punchingDate = punchingDate;
    }

    public String getEmployeePid() {
        return employeePid;
    }

    public void setEmployeePid(String employeePid) {
        this.employeePid = employeePid;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getTaskExecutionPid() {
        return taskExecutionPid;
    }

    public void setTaskExecutionPid(String taskExecutionPid) {
        this.taskExecutionPid = taskExecutionPid;
    }

    public String getAccountProfilePid() {
        return accountProfilePid;
    }

    public void setAccountProfilePid(String accountProfilePid) {
        this.accountProfilePid = accountProfilePid;
    }

    public String getAccountProfileName() {
        return accountProfileName;
    }

    public void setAccountProfileName(String accountProfileName) {
        this.accountProfileName = accountProfileName;
    }

    public double getKilometre() {
        return kilometre;
    }

    public void setKilometre(double kilometre) {
        this.kilometre = kilometre;
    }

    public double getMetres() {
        return metres;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setMetres(double metres) {
        this.metres = metres;
    }

    public LocalDateTime getExeCreatedDate() {
        return exeCreatedDate;
    }

    public void setExeCreatedDate(LocalDateTime exeCreatedDate) {
        this.exeCreatedDate = exeCreatedDate;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Boolean getCalculatd() {
        return isCalculated;
    }

    public void setCalculatd(Boolean calculatd) {
        isCalculated = calculatd;
    }

    @Override
    public String toString() {
        return "KilometerCalculationDenormalised{" +
                "id=" + id +
                ", startLocation='" + startLocation + '\'' +
                ", endLocation='" + endLocation + '\'' +
                ", userName='" + userName + '\'' +
                ", UserPid='" + UserPid + '\'' +
                ", createdDate=" + createdDate +
                ", punchingDate=" + punchingDate +
                ", employeePid='" + employeePid + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", taskExecutionPid='" + taskExecutionPid + '\'' +
                ", accountProfilePid='" + accountProfilePid + '\'' +
                ", accountProfileName='" + accountProfileName + '\'' +
                ", kilometre=" + kilometre +
                ", metres=" + metres +
                '}';
    }
}
