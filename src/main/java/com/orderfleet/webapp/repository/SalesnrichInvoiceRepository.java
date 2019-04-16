package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.SalesnrichInvoiceHeader;

/**
 * Spring Data JPA repository for the SalesnrichInvoiceHeader entity.
 *
 * @author Sarath
 * @since Mar 15, 2018
 *
 */
public interface SalesnrichInvoiceRepository extends JpaRepository<SalesnrichInvoiceHeader, Long> {

	Optional<SalesnrichInvoiceHeader> findOneById(Long id);

	@Query("select salesNrichInvoice from SalesnrichInvoiceHeader salesNrichInvoice where salesNrichInvoice.company.pid = ?1")
	List<SalesnrichInvoiceHeader> findAllByCompanyPid(String companyPid);

	Optional<SalesnrichInvoiceHeader> getTop1SalesnrichInvoiceByInvoiceDateBetweenOrderByCreatedDateDesc(
			LocalDateTime fromDate, LocalDateTime toDate);

	Optional<SalesnrichInvoiceHeader> findByInvoiceNumber(Long invoiceNumber);

	@Query("select count(salesNrichInvoice) from SalesnrichInvoiceHeader salesNrichInvoice")
	Long getCountOfSalesnrichInvoiceHeader();

}
