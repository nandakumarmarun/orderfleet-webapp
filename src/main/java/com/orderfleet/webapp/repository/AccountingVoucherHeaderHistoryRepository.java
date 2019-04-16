package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.AccountingVoucherHeaderHistory;

/**
 * Spring Data JPA repository for the AccountingVoucherHeaderHistory entity.
 * 
 * @author Muhammed Riyas T
 * @since October 24, 2016
 */
public interface AccountingVoucherHeaderHistoryRepository extends JpaRepository<AccountingVoucherHeaderHistory, Long> {

	List<AccountingVoucherHeaderHistory> findByPidOrderByIdDesc(String pid);

}
