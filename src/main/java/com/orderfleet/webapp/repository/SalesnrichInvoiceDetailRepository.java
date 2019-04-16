package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.SalesnrichInvoiceDetail;

/**
 * Spring Data JPA repository for the SalesnrichInvoiceDetail entity.
 *
 * @author Sarath
 * @since Apr 5, 2018
 *
 */
public interface SalesnrichInvoiceDetailRepository extends JpaRepository<SalesnrichInvoiceDetail, Long> {

}
