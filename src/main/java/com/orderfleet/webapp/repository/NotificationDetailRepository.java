package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.NotificationDetail;
import com.orderfleet.webapp.domain.enums.MessageStatus;


public interface NotificationDetailRepository extends JpaRepository<NotificationDetail, Long> {

	@Query("select nd from NotificationDetail nd where nd.company.id = ?#{principal.companyId} and nd.notification.id = ?1")
	List<NotificationDetail> findByCompanyIdAndNotificationId(Long notificationId);
	
	@Query("select nd from NotificationDetail nd where nd.company.id = ?#{principal.companyId} and nd.userId in ?1 and nd.messageStatus in ?2 and nd.createdDate between ?3 and ?4 order by nd.createdDate DESC")
	List<NotificationDetail> findByCompanyIdAndUserIdInAndMessageStatusInAndDateBetweenOrderByCreatedDateDesc(List<Long> userIds, List<MessageStatus> status, LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query("select nd from NotificationDetail nd where nd.company.id = ?#{principal.companyId} and nd.createdDate between ?1 and ?2 order by nd.createdDate DESC")
	List<NotificationDetail> findByCompanyIdAndDateBetweenOrderByCreatedDateDesc(LocalDateTime fromDate, LocalDateTime toDate);
}
