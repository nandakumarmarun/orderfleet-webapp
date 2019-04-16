package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.NotificationMessage;
import com.orderfleet.webapp.domain.enums.NotificationMessageType;

public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, Long> {

	@Query("select nm from NotificationMessage nm where nm.company.id = ?#{principal.companyId}")
	List<NotificationMessage> findByCompanyIsCurrentCompany();
	
	@Query("select nm from NotificationMessage nm where nm.company.id = ?#{principal.companyId} and notificationType = ?1")
	List<NotificationMessage> findByNotificationType(NotificationMessageType notificationMessageType);
}
