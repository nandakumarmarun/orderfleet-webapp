package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.CustomerTimeSpent;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.enums.AttendanceStatus;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.repository.CustomerTimeSpentRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AttendanceService;

/**
 * A DTO for the DashboardUserData.
 * 
 * @author Muhammed Riyas T
 * @since August 24, 2016
 */
public class DashboardUserDataDTO<T> {

	private String userPid;

	private String userName;

	private String employeePid;

	private String employeeName;

	private byte[] profileImage;

	private boolean attendanceMarked;

	private String attendanceStatus;

	private String remarks;

	private List<T> userSummaryData;

	private String lastLocation;

	private String lastAccountLocation;

	private LocalDateTime lastTime;

	private LocalDateTime plannedDate;

	private int notificationCount;

	private LocationType locationType;
	private boolean isGpsOff;
	private boolean isMobileDataOff;

	private boolean mockLocationStatus;

	private String attendanceSubGroupName;
	private String attendanceSubGroupCode;
	private String taskExecutionPid;
	private BigDecimal latitude;

	// Set Customer Time Spent for Dashboard
	private Long customerTimeSpentTime;

	private boolean customerTimeSpentBoolean;

	@JsonIgnore
	private AttendanceService attendanceService;
	@JsonIgnore
	private EmployeeProfileRepository employeeProfileRepository;
	@JsonIgnore
	private CustomerTimeSpentRepository customerTimeSpentRepository;

	public DashboardUserDataDTO() {
	}

	public DashboardUserDataDTO(AttendanceService attendanceService,
			EmployeeProfileRepository employeeProfileRepository,
			CustomerTimeSpentRepository customerTimeSpentRepository) {
		this.attendanceService = attendanceService;
		this.employeeProfileRepository = employeeProfileRepository;
		this.customerTimeSpentRepository = customerTimeSpentRepository;
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public byte[] getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(byte[] profileImage) {
		this.profileImage = profileImage;
	}

	public boolean getAttendanceMarked() {
		return attendanceMarked;
	}

	public void setAttendanceMarked(boolean attendanceMarked) {
		this.attendanceMarked = attendanceMarked;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<T> getUserSummaryData() {
		return userSummaryData;
	}

	public void setUserSummaryData(List<T> userSummaryData) {
		this.userSummaryData = userSummaryData;
	}

	public String getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(String lastLocation) {
		this.lastLocation = lastLocation;
	}

	public String getLastAccountLocation() {
		return lastAccountLocation;
	}

	public void setLastAccountLocation(String lastAccountLocation) {
		this.lastAccountLocation = lastAccountLocation;
	}

	public LocalDateTime getLastTime() {
		return lastTime;
	}

	public void setLastTime(LocalDateTime lastTime) {
		this.lastTime = lastTime;
	}

	public int getNotificationCount() {
		return notificationCount;
	}

	public void setNotificationCount(int notificationCount) {
		this.notificationCount = notificationCount;
	}

	public LocationType getLocationType() {
		return locationType;
	}

	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	public boolean getIsGpsOff() {
		return isGpsOff;
	}

	public void setIsGpsOff(boolean isGpsOff) {
		this.isGpsOff = isGpsOff;
	}

	public boolean getIsMobileDataOff() {
		return isMobileDataOff;
	}

	public void setIsMobileDataOff(boolean isMobileDataOff) {
		this.isMobileDataOff = isMobileDataOff;
	}

	public boolean getMockLocationStatus() {
		return mockLocationStatus;
	}

	public void setMockLocationStatus(boolean mockLocationStatus) {
		this.mockLocationStatus = mockLocationStatus;
	}

	public String getAttendanceStatus() {
		return attendanceStatus;
	}

	public void setAttendanceStatus(String attendanceStatus) {
		this.attendanceStatus = attendanceStatus;
	}

	public LocalDateTime getPlannedDate() {
		return plannedDate;
	}

	public void setPlannedDate(LocalDateTime createdDate) {
		this.plannedDate = createdDate;
	}

	public String getAttendanceSubGroupName() {
		return attendanceSubGroupName;
	}

	public void setAttendanceSubGroupName(String attendanceSubGroupName) {
		this.attendanceSubGroupName = attendanceSubGroupName;
	}

	public String getAttendanceSubGroupCode() {
		return attendanceSubGroupCode;
	}

	public void setAttendanceSubGroupCode(String attendanceSubGroupCode) {
		this.attendanceSubGroupCode = attendanceSubGroupCode;
	}

	public Long getCustomerTimeSpentTime() {
		return customerTimeSpentTime;
	}

	public void setCustomerTimeSpentTime(Long customerTimeSpentTime) {
		this.customerTimeSpentTime = customerTimeSpentTime;
	}

	public boolean getCustomerTimeSpentBoolean() {
		return customerTimeSpentBoolean;
	}

	public void setCustomerTimeSpentBoolean(boolean customerTimeSpentBoolean) {
		this.customerTimeSpentBoolean = customerTimeSpentBoolean;
	}

	public String getTaskExecutionPid() {
		return taskExecutionPid;
	}

	public void setTaskExecutionPid(String taskExecutionPid) {
		this.taskExecutionPid = taskExecutionPid;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public void setEmployeeData(DashboardUserDataDTO<DashboardSummaryDTO> dashboardUserData, String userPid) {
		// set employee profile name and image
		employeeProfileRepository.findByUserPid(userPid).ifPresent(employeeProfile -> {
			dashboardUserData.setEmployeePid(employeeProfile.getPid());
			dashboardUserData.setUserName(employeeProfile.getName());
			dashboardUserData.setProfileImage(employeeProfile.getProfileImage());
		});
	}

	public void setAttendanceStatus(DashboardUserDataDTO<DashboardSummaryDTO> dashboardUserData, String userPid,
			LocalDate date) {
		// set attendance status
		List<AttendanceDTO> attendanceDTOs = attendanceService.findAllByCompanyIdUserPidAndDateBetween(userPid,
				date.atTime(0, 0), date.atTime(23, 59));
		if (attendanceDTOs.isEmpty()) {
			dashboardUserData.setAttendanceStatus("");
			dashboardUserData.setRemarks("");
			dashboardUserData.setAttendanceSubGroupName("");
			dashboardUserData.setAttendanceSubGroupCode("");
		} else {
			if (attendanceDTOs.get(0).getAttendanceStatus().equals(AttendanceStatus.PRESENT)
					|| attendanceDTOs.get(0).getAttendanceStatus().equals(AttendanceStatus.Present)) {
				dashboardUserData.setAttendanceMarked(true);
				dashboardUserData.setAttendanceStatus("True");
				dashboardUserData.setRemarks(attendanceDTOs.get(0).getRemarks());
				dashboardUserData.setPlannedDate(attendanceDTOs.get(0).getPlannedDate());
			} else {
				dashboardUserData.setAttendanceStatus("False");
				dashboardUserData.setAttendanceMarked(false);
				dashboardUserData.setRemarks(attendanceDTOs.get(0).getRemarks());
				dashboardUserData.setPlannedDate(attendanceDTOs.get(0).getPlannedDate());
			}
			dashboardUserData.setAttendanceSubGroupName(attendanceDTOs.get(0).getAttendanceSubGroupName());
			dashboardUserData.setAttendanceSubGroupCode(attendanceDTOs.get(0).getAttendanceSubGroupCode());
		}
	}

	public void setCustomerTimeSpent(DashboardUserDataDTO<DashboardSummaryDTO> dashboardUserData, String userPid) {
		// set customer time spent
		Optional<CustomerTimeSpent> customerTimeSpent = customerTimeSpentRepository
				.findTop1ByCompanyIdAndUserPidAndActiveOrderByStartTimeAsc(SecurityUtils.getCurrentUsersCompanyId(),
						userPid, true);
		if (customerTimeSpent.isPresent()) {
			long millis = customerTimeSpent.get().getStartTime().until(LocalDateTime.now(), ChronoUnit.SECONDS);
			dashboardUserData.setCustomerTimeSpentTime(millis);
			dashboardUserData.setCustomerTimeSpentBoolean(true);
		} else {
			dashboardUserData.setCustomerTimeSpentBoolean(false);
		}
	}

	public void setLastExecutionDetails(DashboardUserDataDTO<DashboardSummaryDTO> dashboardUserData,
			ExecutiveTaskExecution executiveTaskExecution) {
		if (executiveTaskExecution != null) {
			dashboardUserData.setLastAccountLocation(executiveTaskExecution.getAccountProfile().getLocation());
			dashboardUserData.setLastTime(executiveTaskExecution.getDate());

			if (executiveTaskExecution.getLocation() != null
					&& !executiveTaskExecution.getLocation().equals("No Location")) {
				dashboardUserData.setLastLocation(executiveTaskExecution.getLocation());
			} else if (executiveTaskExecution.getTowerLocation() != null
					&& !executiveTaskExecution.getTowerLocation().equals("Not Found")) {
				dashboardUserData.setLastLocation(executiveTaskExecution.getTowerLocation());
			} else {
				dashboardUserData.setLastLocation("No Location");
			}
			dashboardUserData.setLocationType(executiveTaskExecution.getLocationType());
			dashboardUserData.setIsGpsOff(executiveTaskExecution.getIsGpsOff());
			dashboardUserData.setIsMobileDataOff(executiveTaskExecution.getIsMobileDataOff());
			dashboardUserData.setMockLocationStatus(executiveTaskExecution.getMockLocationStatus());
			dashboardUserData.setTaskExecutionPid(executiveTaskExecution.getPid());
			dashboardUserData.setLatitude(executiveTaskExecution.getLatitude());
		}
	}

}
