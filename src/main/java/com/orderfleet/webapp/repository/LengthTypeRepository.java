package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.BrandDeva;
import com.orderfleet.webapp.domain.LengthType;
import com.orderfleet.webapp.domain.RouteCode;

public interface LengthTypeRepository extends JpaRepository<LengthType, Long>{
	
}
