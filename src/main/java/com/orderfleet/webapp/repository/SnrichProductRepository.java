package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.SnrichProduct;

public interface SnrichProductRepository extends JpaRepository<SnrichProduct, Long> {
	
	Optional<SnrichProduct> findOneByPid(String pid);

}
