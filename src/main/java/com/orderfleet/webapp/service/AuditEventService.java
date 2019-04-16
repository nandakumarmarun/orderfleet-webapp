package com.orderfleet.webapp.service;

import com.orderfleet.webapp.config.audit.AuditEventConverter;
import com.orderfleet.webapp.domain.PersistentAuditEvent;
import com.orderfleet.webapp.repository.PersistenceAuditEventRepository;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing audit events.
 * <p>
 * This is the default implementation to support SpringBoot Actuator AuditEventRepository
 * </p>
 */
@Service
@Transactional
public class AuditEventService {

    private final PersistenceAuditEventRepository persistenceAuditEventRepository;

    private final AuditEventConverter auditEventConverter;

    public AuditEventService(
        PersistenceAuditEventRepository persistenceAuditEventRepository,
        AuditEventConverter auditEventConverter) {

        this.persistenceAuditEventRepository = persistenceAuditEventRepository;
        this.auditEventConverter = auditEventConverter;
    }
    
    public List<AuditEvent> findAll() {
    	List<PersistentAuditEvent> auditEvents = persistenceAuditEventRepository.findAll();
    	return auditEvents.stream().map(auditEventConverter::convertToAuditEvent).collect(Collectors.toList());
    }

    public Page<AuditEvent> findAll(Pageable pageable) {
        return persistenceAuditEventRepository.findAll(pageable)
            .map(auditEventConverter::convertToAuditEvent);
    }
    
    public List<AuditEvent> findByDates(Instant fromDate, Instant toDate) {
        return persistenceAuditEventRepository.findAllByAuditEventDateBetween(fromDate, toDate).stream()
            .map(auditEventConverter::convertToAuditEvent).collect(Collectors.toList());
    }

    public Page<AuditEvent> findByDates(Instant fromDate, Instant toDate, Pageable pageable) {
        return persistenceAuditEventRepository.findAllByAuditEventDateBetween(fromDate, toDate, pageable)
            .map(auditEventConverter::convertToAuditEvent);
    }

    public Optional<AuditEvent> find(Long id) {
        return Optional.ofNullable(persistenceAuditEventRepository.findOne(id)).map
            (auditEventConverter::convertToAuditEvent);
    }
}