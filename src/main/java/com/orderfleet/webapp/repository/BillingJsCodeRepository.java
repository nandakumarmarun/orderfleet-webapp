package com.orderfleet.webapp.repository;

import com.orderfleet.webapp.domain.BillingJSCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BillingJsCodeRepository extends JpaRepository<BillingJSCode, Long> {

    @Query("select billingJSCode from BillingJSCode billingJSCode where billingJSCode.company.id = ?1")
    BillingJSCode findByCompanyId(Long CompanyId);
}
