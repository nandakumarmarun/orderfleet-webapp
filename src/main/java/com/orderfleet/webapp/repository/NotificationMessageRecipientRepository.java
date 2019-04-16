package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.NotificationMessageRecipient;

public interface NotificationMessageRecipientRepository extends JpaRepository<NotificationMessageRecipient, Long> {

	@Query("select nmr from NotificationMessageRecipient nmr where nmr.userDevice.user.login = ?#{principal.username}")
	List<NotificationMessageRecipient> findByUserIsCurrentUser();
	
	Optional<NotificationMessageRecipient> findOneByNotificationMessagePidAndUserDeviceId(String notificatinMessagePid, Long userDeviceId);

}
