package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.StageHeaderRca;
import com.orderfleet.webapp.domain.enums.StageNameType;

public interface StageHeaderRcaRepository extends JpaRepository<StageHeaderRca, Long>{

}
