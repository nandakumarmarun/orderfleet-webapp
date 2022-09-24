package com.orderfleet.webapp.service.async;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.Attendance;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.web.rest.integration.dto.AttendanceLogDTO;

@Service
public class AttendanceLogService {
	@Inject
	private AttendanceRepository attendanceRepository;
	
	@Inject
	private UserRepository userRepository;
	
	public List<AttendanceLogDTO> getattendaceLog (String Date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate fDate = LocalDate.parse(Date,formatter);
		LocalDate todate = LocalDate.parse(Date,formatter);
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = todate.atTime(23, 59);
//		List<Long> userid = userRepository.findAllUserIdsByCompanyId();
		System.out.println("fromm date ==="+ fromDate);
		System.out.println("todate date ==="+ toDate);
		List<AttendanceLogDTO> attendanceLogList = new ArrayList<>();
		List<Object[]> attendanceLogs = attendanceRepository.findByCompanyanddateBtween(toDate);
		
		if(!attendanceLogs.isEmpty()) {
			attendanceLogs.forEach(data -> {
				AttendanceLogDTO attendancelog = new AttendanceLogDTO();
				LocalDateTime attTime = LocalDateTime.parse(data[0].toString());
				attendancelog.setPid(data[2].toString());
				attendancelog.setAttendanceDate(attTime);
				attendancelog.setEmployeeName(data[1].toString());
				attendancelog.setOrgEmpId(data[3] == null ? "" : data[3].toString());
				if(data[4] != null) {
					attendancelog.setPunchOutDate(LocalDateTime.parse(data[4].toString()));
				}
				attendanceLogList.add(attendancelog);
			});
		}
//		if(!attendanceLogList.isEmpty()) {
//			List<String> pids = attendanceLogList.stream().map(data -> data.getPid()).collect(Collectors.toList());
//			
//		}
		System.out.println("attenstelogs sizeeee==="+attendanceLogs.size());
		attendanceLogs.forEach(data -> System.out.println("attenstelogs==="+ data[0] + data[1]));
		return attendanceLogList;
	}

}
