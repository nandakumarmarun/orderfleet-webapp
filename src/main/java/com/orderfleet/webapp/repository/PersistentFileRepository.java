package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.PersistentFile;

/**
 * Spring Data JPA repository for the PersistentFile entity.
 * 
 * @author Shaheer
 * @since August 01, 2016
 */
public interface PersistentFileRepository extends JpaRepository<PersistentFile, Long> {
	PersistentFile findOneByMd5(String md5);
}
