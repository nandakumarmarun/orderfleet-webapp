package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AuthenticationAuditEvent;
import com.orderfleet.webapp.repository.AuthenticationAuditEventRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AuthenticationAuditEventService;

@Service
@Transactional
public class AuthenticationAuditEventServiceImpl implements AuthenticationAuditEventService {
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	private final AuthenticationAuditEventRepository authenticationAuditEventRepository;

	public AuthenticationAuditEventServiceImpl(AuthenticationAuditEventRepository authenticationAuditEventRepository) {
		super();
		this.authenticationAuditEventRepository = authenticationAuditEventRepository;
	}

	@Override
	public Page<AuthenticationAuditEvent> findAll(Pageable pageable) {
		return authenticationAuditEventRepository.findAll(pageable);
	}

	@Override
	public Page<AuthenticationAuditEvent> findByDates(Instant fromDate, Instant toDate, Pageable pageable) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AuthAE_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by audit event date between";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			Page<AuthenticationAuditEvent> aae = authenticationAuditEventRepository.findAllByAuditEventDateBetween(fromDate, toDate, pageable);
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
					return aae;
	}

	@Override
	public Optional<AuthenticationAuditEvent> find(Long id) {
		return Optional.ofNullable(authenticationAuditEventRepository.findOne(id));
	}

	@Override
	public AuthenticationAuditEvent save(String login, String password, String ipAddress) {
		return authenticationAuditEventRepository.save(new AuthenticationAuditEvent(login, password, ipAddress, Instant.now()));
	}
}
