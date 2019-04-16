package com.orderfleet.webapp.repository;

import com.orderfleet.webapp.domain.AuthenticationAuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data JPA repository for the AuthenticationAuditEvent entity.
 */
public interface AuthenticationAuditEventRepository extends JpaRepository<AuthenticationAuditEvent, Long> {

    List<AuthenticationAuditEvent> findByLogin(String login);

    List<AuthenticationAuditEvent> findByAuditEventDateAfter(Instant after);

    List<AuthenticationAuditEvent> findByLoginAndAuditEventDateAfter(String login, Instant after);

    List<AuthenticationAuditEvent> findByIpAddress(String login, String ipAddress);

    Page<AuthenticationAuditEvent> findAllByAuditEventDateBetween(Instant fromDate, Instant toDate, Pageable pageable);
}