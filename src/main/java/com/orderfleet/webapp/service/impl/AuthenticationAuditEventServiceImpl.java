package com.orderfleet.webapp.service.impl;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AuthenticationAuditEvent;
import com.orderfleet.webapp.repository.AuthenticationAuditEventRepository;
import com.orderfleet.webapp.service.AuthenticationAuditEventService;

@Service
@Transactional
public class AuthenticationAuditEventServiceImpl implements AuthenticationAuditEventService {

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
		return authenticationAuditEventRepository.findAllByAuditEventDateBetween(fromDate, toDate, pageable);
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
