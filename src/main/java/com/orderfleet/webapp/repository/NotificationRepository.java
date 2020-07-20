package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

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
	
	@Query("select notification from Notification notification where notification.pid= ?1 Order By notification.createdDate desc")
	Notification findByPidOrderByCreatedDateDesc(String notificationPid);
	
	@Query("select notification.id from Notification notification where notification.pid IN ?1")
	List<Long> findNotificationIdByPids(List<String> notificationPids);

	@Transactional
	@Modifying
	@Query("UPDATE Notification notification SET notification.lastModifiedDate =?1 where notification.id = ?2")
	int updateNotificationLastModifiedDate(LocalDateTime now,Long id);

}
