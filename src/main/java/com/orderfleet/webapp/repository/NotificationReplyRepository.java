package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orderfleet.webapp.domain.NotificationReply;

@Repository
public interface NotificationReplyRepository extends JpaRepository<NotificationReply, Long> {

	List<NotificationReply> findAllByNotificationPidAndCreatedByPidOrderByCreatedDate(String notificationPid,String userPid);
}
