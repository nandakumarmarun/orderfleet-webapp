package com.orderfleet.webapp.repository.custom;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.DashboardItem;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.web.rest.dto.DashboardGraphDTO;

@Component
public class DashboardGraphRepositoryCustomImpl implements DashboardGraphRepositoryCustom {

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public DashboardGraphDTO getPerformanceChartData(List<DashboardItem> dashboardChartItems,
			List<AccountProfile> accountProfiles, Long companyId, LocalDateTime from, LocalDateTime to) {
		List<String> labels = new ArrayList<>();
		StringBuilder queryString = new StringBuilder("WITH performance_chart AS (");
		StringBuilder selectQueryString = new StringBuilder(" select document_date ");
		for (DashboardItem dashboardItem : dashboardChartItems) {
			//create labels for graph
			labels.add(dashboardItem.getName());
			String documentIds = dashboardItem.getDocuments().stream().map(di -> di.getId()+"").collect(Collectors.joining(","));
			StringBuilder subQueryString = new StringBuilder("SELECT cast(document_date as date)");
			String docName = dashboardItem.getName().replaceAll("[^a-zA-Z0-9]","");
			selectQueryString.append(" ,sum("+ docName +") as " + docName);
			for (int i = 0; i < dashboardChartItems.size(); i++) {
				if (dashboardChartItems.get(i).getName().equals(dashboardItem.getName())) {
					// for graph we need inventory/accounting only
					if (dashboardItem.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)) {
						subQueryString.append(" ,sum(document_total) as " + docName);
					} else if (dashboardItem.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)) {
						subQueryString.append(" ,sum(total_amount) as " + dashboardItem.getName());
					}
				} else {
					String documentName = dashboardChartItems.get(i).getName().replaceAll("[^a-zA-Z0-9]","");
					subQueryString.append(" ,0 as " + documentName);
				}
			}
			if (dashboardItem.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)) {
				subQueryString.append(",document_id,company_id FROM tbl_inventory_voucher_header where company_id = :companyId and document_date between :fromDate and :toDate and document_id in ("+ documentIds +") and receiver_account_id in :accountProfileIds group by cast(document_date as date), document_id, company_id");

			} else if (dashboardItem.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)) {
				subQueryString.append(",document_id,company_id FROM tbl_accounting_voucher_header where company_id = :companyId and document_date between :fromDate and :toDate and document_id in ("+ documentIds +") and account_profile_id in :accountProfileIds group by cast(document_date as date), document_id, company_id");
			}
			queryString.append(subQueryString + " UNION ");
		}
		//remove last union
		int lastIdx = queryString.lastIndexOf("UNION");
		queryString.replace(lastIdx, lastIdx + "UNION".length(), "");
		queryString.append(")" + selectQueryString + " from performance_chart group by document_date order by document_date");
		
		List<Object[]> results = new ArrayList<>();
		if(!accountProfiles.isEmpty()) {
			List<Long> accountProfileIds = accountProfiles.stream().map(AccountProfile::getId).collect(Collectors.toList());
			Query q = em.createNativeQuery(queryString.toString());
			q.setParameter("companyId", companyId);
			q.setParameter("fromDate", from.toLocalDate());
			q.setParameter("toDate", to.toLocalDate());
			q.setParameter("accountProfileIds", accountProfileIds);
			results = q.getResultList();
		}
		DashboardGraphDTO dashboardGraphDTO = new DashboardGraphDTO();
		dashboardGraphDTO.setLabels(labels);
		dashboardGraphDTO.setLineGraphDatas(results);
		return dashboardGraphDTO;
	}

}
