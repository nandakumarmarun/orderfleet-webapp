package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.ExpenseVoucherHeader;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

/**
 * Spring Data JPA repository for the AccountingVoucherHeader entity.
 * 
 * @author Muhammed Riyas T
 * @since July 28, 2016
 */
/*
 * public interface ExpenseHeaderRepository extends
 * JpaRepository<ExpenseVoucherHeader, Long> {
 * 
 * 
 * 
 * }
 */