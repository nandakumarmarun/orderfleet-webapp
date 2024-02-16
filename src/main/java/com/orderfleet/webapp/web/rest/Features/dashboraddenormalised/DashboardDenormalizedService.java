package com.orderfleet.webapp.web.rest.Features.dashboraddenormalised;

import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.orderfleet.webapp.web.rest.dto.DashboardChartDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardHeaderSummaryDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardSummaryDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardUserDataDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DashboardDenormalizedService {

    String PID_PREFIX = "DASHB-";
    void SaveExecutivetaskExecutionDocument(ExecutiveTaskSubmissionTransactionWrapper executiveTaskSubmissionDTO, User user);

    DashboardHeaderSummaryDTO loadDashboardHeaderSummary(LocalDateTime from, LocalDateTime to);

    List<DashboardUserDataDTO<DashboardSummaryDTO>> loadUserSummaryData(LocalDateTime from, LocalDateTime to,Long parentLocationId,User user);

    DashboardChartDTO loadSummaryDenormalisedChart(LocalDateTime from, LocalDateTime to);

    void loadDatabetweenTimePeriod(LocalDate from, LocalDate to,long companyId);

    DashboardHeaderSummaryDTO loadDashboardAttendanceHeader(LocalDateTime from, LocalDateTime to);

}
