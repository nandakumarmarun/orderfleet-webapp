package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.orderfleet.webapp.domain.DistrictC;
import com.orderfleet.webapp.domain.StateC;
import com.orderfleet.webapp.web.rest.dto.DistrictCDTO;
import com.orderfleet.webapp.web.rest.dto.StateCDTO;

public interface DistrictCRepository extends JpaRepository<DistrictC, Long> {
	
	
	
	@Query("select districtc from DistrictC  districtc  where districtc.state.id=?1")
	List<DistrictC> findAllByStateId(Long id);
	
	@Query("select districtc from DistrictC districtc order by districtc.name")
	List<DistrictC> findAllDistricts();


}
