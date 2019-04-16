package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.orderfleet.webapp.domain.AuthenticationAuditEvent;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.AuthenticationAuditEventService;
import com.orderfleet.webapp.web.util.ResponseUtil;

import io.swagger.annotations.ApiParam;

/**
 * REST controller for getting the Authentication audit events.
 */
@Controller
@RequestMapping("/management/authentication/audits")
@Secured(AuthoritiesConstants.SITE_ADMIN)
public class AuthenticationAuditEventResource {

    private final AuthenticationAuditEventService authenticationAuditEventService;

    public AuthenticationAuditEventResource(AuthenticationAuditEventService authenticationAuditEventService) {
		super();
		this.authenticationAuditEventService = authenticationAuditEventService;
	}

    /**
     * GET  /audits : get a page of AuditEvents.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of AuditEvents in body
     */
    @GetMapping
    public String getAll(@ApiParam Pageable pageable, Model model) {
        Page<AuthenticationAuditEvent> page = authenticationAuditEventService.findAll(pageable);
        model.addAttribute("authenticationAudits", page);
        LocalDate today = LocalDate.now();
		LocalDate previousMonthDate = today.minusMonths(1).minusDays(1);
		model.addAttribute("defaultFromDate", previousMonthDate);
		model.addAttribute("defaultToDate", today);
		return "site_admin/authentication-audits";
    }

    /**
     * GET  /audits : get a page of AuditEvents between the fromDate and toDate.
     *
     * @param fromDate the start of the time period of AuditEvents to get
     * @param toDate the end of the time period of AuditEvents to get
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of AuditEvents in body
     */
    @GetMapping(params = {"fromDate", "toDate"})
    public String getByDates(
        @RequestParam(value = "fromDate") LocalDate fromDate,
        @RequestParam(value = "toDate") LocalDate toDate,
        @ApiParam Pageable pageable, Model model) {

        Page<AuthenticationAuditEvent> page = authenticationAuditEventService.findByDates(
            fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
            toDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant(),
            pageable);
        model.addAttribute("authenticationAudits", page);
        model.addAttribute("defaultFromDate", fromDate);
		model.addAttribute("defaultToDate", toDate);
		return "site_admin/authentication-audits";
    }

    /**
     * GET  /audits/:id : get an AuditEvent by id.
     *
     * @param id the id of the entity to get
     * @return the ResponseEntity with status 200 (OK) and the AuditEvent in body, or status 404 (Not Found)
     */
    @GetMapping("/{id:.+}")
    public ResponseEntity<AuthenticationAuditEvent> get(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(authenticationAuditEventService.find(id));
    }
}