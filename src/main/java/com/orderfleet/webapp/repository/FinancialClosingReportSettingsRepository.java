package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.FinancialClosingReportSettings;
import com.orderfleet.webapp.domain.enums.PaymentMode;

public interface FinancialClosingReportSettingsRepository extends JpaRepository<FinancialClosingReportSettings, Long>{
	
	Optional<FinancialClosingReportSettings> findByCompanyIdAndDocumentPid(Long id, String documentPid);
	
	@Query("select financialClosingReportSettings from FinancialClosingReportSettings financialClosingReportSettings where financialClosingReportSettings.company.id = ?#{principal.companyId}")
	List<FinancialClosingReportSettings> findAllByCompanyId();
	
	@Query("select financialClosingReportSettings from FinancialClosingReportSettings financialClosingReportSettings where financialClosingReportSettings.company.id = ?#{principal.companyId} and financialClosingReportSettings.paymentMode = ?1")
	List<FinancialClosingReportSettings> findAllByPaymentMode(PaymentMode paymentMode);
	
	@Query("select financialClosingReportSettings from FinancialClosingReportSettings financialClosingReportSettings where financialClosingReportSettings.company.id = ?#{principal.companyId} and financialClosingReportSettings.paymentMode in ?1")
	List<FinancialClosingReportSettings> findAllByPaymentModeExcludePettyCash(List<PaymentMode>paymentModes);
	
}
