package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.StageDetail;

public interface StageDetailRepository extends JpaRepository<StageDetail, Long> {

}
