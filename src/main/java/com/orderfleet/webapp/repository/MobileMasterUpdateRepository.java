package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Department;
import com.orderfleet.webapp.domain.MobileMasterDetail;
import com.orderfleet.webapp.domain.MobileMasterUpdate;

/**
 * Spring Data JPA repository for the Department entity.
 * 
 * @author Prashob
 * @since Dec 18, 2019
 */
public interface MobileMasterUpdateRepository extends JpaRepository<MobileMasterUpdate, Long> {

}
