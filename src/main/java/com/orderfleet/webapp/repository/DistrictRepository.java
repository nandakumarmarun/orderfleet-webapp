package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.District;

public interface DistrictRepository extends JpaRepository<District, Long> {

	public District findByCode(String code);

	public District findByName(String name);

	public List<District> findAllByStateId(Long id);
}
