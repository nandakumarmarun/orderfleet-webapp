package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.DashboardNotification;;

public interface DashboardNotificationRepository extends JpaRepository<DashboardNotification, Long> {

	Long countByCreatedByAndReadFalse(String login);

	Long countByCreatedByAndReadFalseAndCreatedDateBetween(String login,LocalDateTime fromDate, LocalDateTime toDate);

}
