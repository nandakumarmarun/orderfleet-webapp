package com.orderfleet.webapp.repository;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.License;

/**
 * Spring Data JPA repository for the License entity.
 * 
 * @author Shaheer
 * @since May 18, 2016
 */
public interface LicenseRepository extends JpaRepository<License,Long> {
	
	Optional<License> findOneByLicenseKey(String licenseKey);
	
	Optional<License> findOneByLicenseKeyAndExpireDateAfterAndActivatedFalse(String licenseKey,ZonedDateTime expireDate);
	
	Optional<License> findOneByLicenseKeyAndCompanyIdAndExpireDateAfterAndActivatedFalse(String licenseKey, Long companyId, ZonedDateTime expireDate);

}
