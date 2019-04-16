package com.orderfleet.webapp.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.AuthenticationAuditEvent;

public interface AuthenticationAuditEventService {
	
	public Page<AuthenticationAuditEvent> findAll(Pageable pageable);
	
	public Page<AuthenticationAuditEvent> findByDates(Instant fromDate, Instant toDate, Pageable pageable);
	
	public Optional<AuthenticationAuditEvent> find(Long id);
	
	public AuthenticationAuditEvent save(String login, String password, String ipAddress);

}
