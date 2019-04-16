package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Notification;

/**
 * Spring Data JPA repository for the Notification entity.
 * 
 * @author Sarath
 * @since Sep 7, 2016
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> getAllNotificationsByIsImportantTrueAndResendTime(Long count);

	Page<Notification> findAll(Pageable pageable); 

	@Query("select notification from Notification notification where notification.company.id = ?#{principal.companyId} and notification.createdDate between ?1 and ?2 Order By notification.createdDate desc")
	List<Notification> findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(LocalDateTime fromDate,
			LocalDateTime toDate);
	
	@Query("select notification from Notification notification where notification.id in ?1 Order By notification.createdDate desc")
	List<Notification> findByIdInOrderByCreatedDateDesc(Set<Long> notificationIds);

}
