package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.ExpenseVoucherDetail;
import com.orderfleet.webapp.domain.enums.PaymentMode;

/**
 * Spring Data JPA repository for the AccountingVoucherDetail entity.
 * 
 * @author Prashob Sasidharan
 * @since October 29, 2019
 */
/*
 * public interface ExpenseVoucherDetailRepository extends
 * JpaRepository<ExpenseVoucherDetail, Long> {
 * 
 * 
 * 
 * }
 */