package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.BrandDeva;
import com.orderfleet.webapp.domain.StateFocus;

public interface StateFocusRepository extends JpaRepository<StateFocus, Long> {

}
