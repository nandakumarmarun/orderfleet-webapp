package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.ReceivablePayableColumn;

public interface ReceivablePayableColumnRepository extends JpaRepository<ReceivablePayableColumn, Long> {
	
}
