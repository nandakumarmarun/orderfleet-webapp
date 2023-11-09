package com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmDistanceFare;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.User;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_km_distance_fare")
public class KmDistanceFare {
    @Id
    @GenericGenerator(name = "seq_tbl_km_distance_fare_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "seq_tbl_km_distance_fare_id") })
    @GeneratedValue(generator = "seq_tbl_km_distance_fare_id_GEN")
    @Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_tbl_km_distance_fare_id')")
    private Long id;
    @Column(name = "pid")
    private String pid;

    @Column(name = "starting_point")
    private String StartingPoint;

    @Column(name = "starting_location")
    private String startingLocation;

    @Column(name = "destination_point")
    private String endPoint;

    @Column(name = "end_location")
    private String endLocation;

    @Column(name = "total_distance")
    private double TotalDistance;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "planned_date")
    private LocalDate PlannedDate;
    @ManyToOne
    @NotNull
    private Company company;
    @ManyToOne
    private User user;

    @Column(name = "slab_name")
    private String slabName;

    @Column(name = "slab_total")
    private double slabTotal;

    private String EmployeeName;

    private String EmployeePid;


    public KmDistanceFare() {
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

    public String getStartingPoint() {
        return StartingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        StartingPoint = startingPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public double getTotalDistance() {
        return TotalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        TotalDistance = totalDistance;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getPlannedDate() {
        return PlannedDate;
    }

    public void setPlannedDate(LocalDate plannedDate) {
        PlannedDate = plannedDate;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(String startingLocation) {
        this.startingLocation = startingLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getSlabName() {
        return slabName;
    }

    public void setSlabName(String slabName) {
        this.slabName = slabName;
    }

    public double getSlabTotal() {
        return slabTotal;
    }

    public void setSlabTotal(double slabTotal) {
        this.slabTotal = slabTotal;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public String getEmployeePid() {
        return EmployeePid;
    }

    public void setEmployeePid(String employeePid) {
        EmployeePid = employeePid;
    }

    @Override
    public String toString() {
        return "KmDistanceFare{" +
                "id=" + id +
                ", pid='" + pid + '\'' +
                ", StartingPoint='" + StartingPoint + '\'' +
                ", startingLocation='" + startingLocation + '\'' +
                ", endPoint='" + endPoint + '\'' +
                ", endLocation='" + endLocation + '\'' +
                ", TotalDistance=" + TotalDistance +
                ", createdDate=" + createdDate +
                ", PlannedDate=" + PlannedDate +
                ", company=" + company +
                ", user=" + user +
                ", slabName='" + slabName + '\'' +
                ", slabTotal=" + slabTotal +
                '}';
    }
}
