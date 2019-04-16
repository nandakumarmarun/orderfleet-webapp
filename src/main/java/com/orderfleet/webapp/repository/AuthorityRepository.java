package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.Authority;

/**
 * Spring Data JPA repository for the Authority entity.
 * 
 * @author Shaheer
 * @since May 06, 2016
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
