package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.orderfleet.webapp.service.AuditEventService;
import com.orderfleet.webapp.web.util.ResponseUtil;

/**
 * REST controller for getting the audit events.
 */
@Controller
@RequestMapping("/management/audits")
public class AuditResource {

    private final AuditEventService auditEventService;

    public AuditResource(AuditEventService auditEventService) {
        this.auditEventService = auditEventService;
    }

    /**
     * GET  /audits : get a page of AuditEvents.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of AuditEvents in body
     */
    @GetMapping
    public String getAll(Model model) {
        List<AuditEvent> auditEvents = auditEventService.findAll();
        model.addAttribute("audits", auditEvents);
        LocalDate today = LocalDate.now();
		LocalDate previousMonthDate = today.minusMonths(1).minusDays(1);
		model.addAttribute("defaultFromDate", previousMonthDate);
		model.addAttribute("defaultToDate", today);
		return "site_admin/audits";
    }

    /**
     * GET  /audits : get a page of AuditEvents between the fromDate and toDate.
     *
     * @param fromDate the start of the time period of AuditEvents to get
     * @param toDate the end of the time period of AuditEvents to get
     * @return the ResponseEntity with status 200 (OK) and the list of AuditEvents in body
     */
    @GetMapping(params = {"fromDate", "toDate"})
    public String getByDates(
        @RequestParam(value = "fromDate") LocalDate fromDate,
        @RequestParam(value = "toDate") LocalDate toDate,Model model) {

    	List<AuditEvent> auditEvents = auditEventService.findByDates(
            fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
            toDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant());
        model.addAttribute("audits", auditEvents);
        model.addAttribute("defaultFromDate", fromDate);
		model.addAttribute("defaultToDate", toDate);
		return "site_admin/audits";
    }

    /**
     * GET  /audits/:id : get an AuditEvent by id.
     *
     * @param id the id of the entity to get
     * @return the ResponseEntity with status 200 (OK) and the AuditEvent in body, or status 404 (Not Found)
     */
    @GetMapping("/{id:.+}")
    public ResponseEntity<AuditEvent> get(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(auditEventService.find(id));
    }
}