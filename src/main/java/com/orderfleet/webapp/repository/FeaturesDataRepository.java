package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.FeaturesData;
import com.orderfleet.webapp.domain.enums.Feature;

public interface FeaturesDataRepository extends JpaRepository<FeaturesData, Long> {

	@Query("select featuresData from FeaturesData featuresData where featuresData.company.id = ?#{principal.companyId}")
	List<FeaturesData> findAllByCompanyId();
	
	@Query("select featuresData.feature from FeaturesData featuresData where featuresData.company.pid = ?1")
	Set<Feature> findFeatureByCompanyPid(String sompanyPid);

	Optional<FeaturesData> findByCompanyIdAndFeature(Long companyId, Feature feature);
}
