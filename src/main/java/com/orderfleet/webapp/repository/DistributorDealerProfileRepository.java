package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.DistributorDealerAssociation;

public interface DistributorDealerProfileRepository extends JpaRepository<DistributorDealerAssociation,Long>{

	List<DistributorDealerAssociation> findAllByCompanyId(Long companyId);
	
	
	

}
