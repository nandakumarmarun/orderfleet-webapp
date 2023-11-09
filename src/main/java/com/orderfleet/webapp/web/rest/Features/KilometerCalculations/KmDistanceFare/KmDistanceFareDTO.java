package com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmDistanceFare;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.User;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class KmDistanceFareDTO {
    private String pid;
    private String StartingPoint;
    private String endPoint;
    private double TotalDistance;
    private String StartingPointLocation;
    private String EndPointLocation;
    private LocalDateTime createdDate;
    private LocalDate PlannedDate;
    private String EmployeeName;
    private long companyId;
    private long userId;
    private String userName;
    private String slabName;
    private double slabRate;

    public KmDistanceFareDTO(KmDistanceFare kmDistanceFare) {
        this.pid = kmDistanceFare.getPid();
        this.userId= kmDistanceFare.getUser().getId();
        this.companyId=kmDistanceFare.getCompany().getId();
        this.createdDate= LocalDateTime.now();
        this.endPoint= kmDistanceFare.getEndPoint();
        this.EndPointLocation= kmDistanceFare.getEndLocation();
        this.StartingPoint = kmDistanceFare.getStartingPoint();
        this.StartingPointLocation= kmDistanceFare.getStartingLocation();
        this.PlannedDate = kmDistanceFare.getPlannedDate();
        this.TotalDistance = kmDistanceFare.getTotalDistance();
        this.userName= kmDistanceFare.getUser().getFirstName();
        this.slabName= kmDistanceFare.getSlabName();
        this.slabRate = kmDistanceFare.getSlabTotal();
        this.EmployeeName = kmDistanceFare.getEmployeeName();
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

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getStartingPointLocation() {
        return StartingPointLocation;
    }

    public void setStartingPointLocation(String startingPointLocation) {
        StartingPointLocation = startingPointLocation;
    }

    public String getEndPointLocation() {
        return EndPointLocation;
    }

    public void setEndPointLocation(String endPointLocation) {
        EndPointLocation = endPointLocation;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSlabName() {
        return slabName;
    }

    public void setSlabName(String slabName) {
        this.slabName = slabName;
    }

    public double getSlabRate() {
        return slabRate;
    }

    public void setSlabRate(double slabRate) {
        this.slabRate = slabRate;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    @Override
    public String toString() {
        return "KmDistanceFare{" +
                ", pid='" + pid + '\'' +
                ", StartingPoint='" + StartingPoint + '\'' +
                ", endPoint='" + endPoint + '\'' +
                ", TotalDistance=" + TotalDistance +
                ", createdDate=" + createdDate +
                ", PlannedDate=" + PlannedDate +
                ", company=" + companyId +
                ", user=" + userId +
                '}';
    }
}
