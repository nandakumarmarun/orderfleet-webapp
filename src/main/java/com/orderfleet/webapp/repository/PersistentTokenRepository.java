package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.PersistentToken;
import com.orderfleet.webapp.domain.User;

/**
 * Spring Data JPA repository for the PersistentToken entity.
 * 
 * @author Shaheer
 * @since May 06, 2016
 */
public interface PersistentTokenRepository extends JpaRepository<PersistentToken, String> {

    List<PersistentToken> findByUser(User user);

    List<PersistentToken> findByTokenDateBefore(LocalDate localDate);

}
