package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.NotificationDetail;
import com.orderfleet.webapp.domain.enums.MessageStatus;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

public interface NotificationDetailRepository extends JpaRepository<NotificationDetail, Long> {

	@Query("select nd from NotificationDetail nd where nd.company.id = ?#{principal.companyId} and nd.notification.id = ?1")
	List<NotificationDetail> findByCompanyIdAndNotificationId(Long notificationId);

	@Query("select nd from NotificationDetail nd where nd.company.id = ?#{principal.companyId} and nd.userId in ?1 and nd.messageStatus in ?2 and nd.createdDate between ?3 and ?4 order by nd.createdDate DESC")
	List<NotificationDetail> findByCompanyIdAndUserIdInAndMessageStatusInAndDateBetweenOrderByCreatedDateDesc(
			List<Long> userIds, List<MessageStatus> status, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select nd from NotificationDetail nd where nd.company.id = ?#{principal.companyId} and nd.createdDate between ?1 and ?2 order by nd.createdDate DESC")
	List<NotificationDetail> findByCompanyIdAndDateBetweenOrderByCreatedDateDesc(LocalDateTime fromDate,
			LocalDateTime toDate);

	@Transactional
	@Modifying
	@Query("UPDATE NotificationDetail notificationDetail SET notificationDetail.messageStatus = ?1,notificationDetail.lastModifiedDate =?3 where  notificationDetail.notification.id IN ?2")
	int updateNotificationReadStatus(MessageStatus messageStatus, List<Long> notificationIds,
			LocalDateTime localDateTime);

	@Transactional
	@Modifying
	@Query("UPDATE NotificationDetail notificationDetail SET notificationDetail.messageStatus = ?1,notificationDetail.lastModifiedDate =?3 where  notificationDetail.notification.id IN ?2 and notificationDetail.userPid = ?4")
	int updateNotificationReadStatusAndUser(MessageStatus read, List<Long> notificationIds, LocalDateTime now,
			String userPid);

}
