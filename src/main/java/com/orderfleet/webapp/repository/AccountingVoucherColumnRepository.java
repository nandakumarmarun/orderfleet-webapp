package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.AccountingVoucherColumn;

/**
 * Spring Data JPA repository for the AccountingVoucherColumn entity.
 * 
 * @author Muhammed Riyas T
 * @since July 26, 2016
 */
public interface AccountingVoucherColumnRepository extends JpaRepository<AccountingVoucherColumn, Long> {

	AccountingVoucherColumn findOneByName(String name);

}
