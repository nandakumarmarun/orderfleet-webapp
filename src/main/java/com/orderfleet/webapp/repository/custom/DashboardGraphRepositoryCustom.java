package com.orderfleet.webapp.repository.custom;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.DashboardItem;
import com.orderfleet.webapp.web.rest.dto.DashboardGraphDTO;

public interface DashboardGraphRepositoryCustom {

	DashboardGraphDTO getPerformanceChartData(List<DashboardItem> dashboardChartItems, List<AccountProfile> accountProfiles, Long companyId, LocalDateTime from, LocalDateTime to);
	
}
