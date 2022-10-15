package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.orderfleet.webapp.domain.AccountingVoucherUpdateHistory;

public interface AccountingVoucherUpdateHistoryRepository extends JpaRepository<AccountingVoucherUpdateHistory, Long> {

}
