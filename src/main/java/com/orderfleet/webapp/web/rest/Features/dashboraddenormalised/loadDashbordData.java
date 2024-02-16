package com.orderfleet.webapp.web.rest.Features.dashboraddenormalised;

import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.DashboardHeaderSummaryDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.LocalDate;

@RestController
@RequestMapping(value = "/api")
public class loadDashbordData {

    @Inject
    private DashboardDenormalizedService dashboardDenormalizedService;

    @RequestMapping(value = "/dashboard/load-summary-denormalised", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DashboardHeaderSummaryDTO> LoadDataDenormlaised(
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate, @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        System.out.println("from Date : " + fromDate);
        System.out.println("to Date : "   + toDate);
        System.out.println("CompanyId : " + SecurityUtils.getCurrentUsersCompanyId());

        dashboardDenormalizedService.loadDatabetweenTimePeriod(fromDate,toDate, SecurityUtils.getCurrentUsersCompanyId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
