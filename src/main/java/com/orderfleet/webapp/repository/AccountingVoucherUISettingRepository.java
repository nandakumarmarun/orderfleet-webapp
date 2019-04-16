package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.AccountingVoucherUISetting;

public interface AccountingVoucherUISettingRepository extends JpaRepository<AccountingVoucherUISetting, Long>{
	
	@Query("select accountingVoucherUISetting from AccountingVoucherUISetting accountingVoucherUISetting where accountingVoucherUISetting.company.id = ?#{principal.companyId}")
	List<AccountingVoucherUISetting> findAllByCompanyId();
	
	Optional<AccountingVoucherUISetting> findByCompanyIdAndNameIgnoreCase(Long id, String name);
	
}
