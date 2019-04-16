package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.InventoryClosingHeader;

public interface InventoryClosingHeaderRepository extends JpaRepository<InventoryClosingHeader, Long>{
	
	Optional<InventoryClosingHeader> findTop1ByUserPidOrderByIdDesc(String userPid);

}
