package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.OrderStatus;
import com.orderfleet.webapp.domain.enums.DocumentType;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long>{
	
	Optional<OrderStatus> findByCompanyIdAndNameIgnoreCase(Long id, String name);
	
	@Query("select orderStatus from OrderStatus orderStatus where orderStatus.company.id = ?#{principal.companyId} Order By orderStatus.id asc")
	List<OrderStatus> findAllByCompanyId();
	
	@Query("select orderStatus from OrderStatus orderStatus where orderStatus.company.id = ?#{principal.companyId} and orderStatus.documentType = ?1")
	List<OrderStatus> findAllByDocumentType(DocumentType documentType);
	
}
