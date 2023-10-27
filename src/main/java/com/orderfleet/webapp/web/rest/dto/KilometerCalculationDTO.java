
package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.orderfleet.webapp.domain.KilometreCalculation;

/**
 * @author Anish
 *
 */
public class KilometerCalculationDTO {

	private double kilometre;
	private double metres;
	private LocalDate date;
	private String startLocation;
	private String endLocation;
	private String userName;
	private String UserPid;
	private LocalDateTime createdDate;
	private LocalDateTime ExecreatedDate;
	private String punchingDate;
	private String punchingTime;
	private LocalDateTime lastModifiedDate;
	private String employeePid;
	private String employeeName;
	private String taskExecutionPid;
	private String accountProfileName;
	private String location;
	private boolean isAttendence;
	private  boolean isPunchOut;

	public KilometerCalculationDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public KilometerCalculationDTO(KilometreCalculation kilometreCalculation) {
		super();
		this.kilometre = kilometreCalculation.getKilometre();
		this.metres = kilometreCalculation.getMetres();
		this.date = kilometreCalculation.getDate();
		this.startLocation = kilometreCalculation.getStartLocation();
		this.endLocation = kilometreCalculation.getEndLocation();
		this.userName = kilometreCalculation.getUser().getFirstName() + " "
				+ kilometreCalculation.getUser().getLastName();
		this.UserPid = kilometreCalculation.getUser().getPid();
		this.createdDate = kilometreCalculation.getCreatedDate();
		this.lastModifiedDate = kilometreCalculation.getLastModifiedDate();
		this.employeePid = kilometreCalculation.getEmployee().getPid();
		this.employeeName = kilometreCalculation.getEmployee().getName();
		this.taskExecutionPid = kilometreCalculation.getExecutiveTaskExecution() == null ? null
				: kilometreCalculation.getExecutiveTaskExecution().getPid();
		this.accountProfileName = kilometreCalculation.getExecutiveTaskExecution() == null ? null
				: kilometreCalculation.getExecutiveTaskExecution().getAccountProfile().getName();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		this.punchingDate = dateFormatter.format(kilometreCalculation.getCreatedDate());
		DateTimeFormatter dateFormatter1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		this.punchingTime = dateFormatter1.format(kilometreCalculation.getCreatedDate());
		this.location = kilometreCalculation.getExecutiveTaskExecution() == null ? null
				: kilometreCalculation.getExecutiveTaskExecution().getLocation();
		this.ExecreatedDate = kilometreCalculation.getCreatedDate();

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

	public void setMetres(double metres) {
		this.metres = metres;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
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

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
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

	public String getPunchingDate() {
		return punchingDate;
	}

	public void setPunchingDate(String punchingDate) {
		this.punchingDate = punchingDate;
	}

	public String getAccountProfileName() {
		return accountProfileName;
	}

	public void setAccountProfileName(String accountProfileName) {
		this.accountProfileName = accountProfileName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPunchingTime() {
		return punchingTime;
	}

	public void setPunchingTime(String punchingTime) {
		this.punchingTime = punchingTime;
	}

	public boolean isAttendence() {
		return isAttendence;
	}

	public void setAttendence(boolean attendence) {
		isAttendence = attendence;
	}

	public boolean isPunchOut() {
		return isPunchOut;
	}

	public void setPunchOut(boolean punchOut) {
		isPunchOut = punchOut;
	}

	public LocalDateTime getExecreatedDate() {
		return ExecreatedDate;
	}

	public void setExecreatedDate(LocalDateTime execreatedDate) {
		ExecreatedDate = execreatedDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		KilometerCalculationDTO myObject = (KilometerCalculationDTO) o;
		return Objects.equals(punchingDate, myObject.punchingDate) &&
				Objects.equals(taskExecutionPid, myObject.taskExecutionPid);
	}

	@Override
	public String toString() {
		return "KilometerCalculationDTO{" +
				"kilometre=" + kilometre +
				", metres=" + metres +
				", date=" + date +
				", startLocation='" + startLocation + '\'' +
				", endLocation='" + endLocation + '\'' +
				", userName='" + userName + '\'' +
				", UserPid='" + UserPid + '\'' +
				", createdDate=" + createdDate +
				", punchingDate='" + punchingDate + '\'' +
				", punchingTime='" + punchingTime + '\'' +
				", lastModifiedDate=" + lastModifiedDate +
				", employeePid='" + employeePid + '\'' +
				", employeeName='" + employeeName + '\'' +
				", taskExecutionPid='" + taskExecutionPid + '\'' +
				", accountProfileName='" + accountProfileName + '\'' +
				", location='" + location + '\'' +
				", isAttendence=" + isAttendence +
				", isPunchOut=" + isPunchOut +
				'}';
	}
}
