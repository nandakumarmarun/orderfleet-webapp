package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.SnrichProduct;
import com.orderfleet.webapp.domain.SnrichProductRate;
import com.orderfleet.webapp.domain.enums.OrderProPaymentMode;

public interface SnrichProductRateRepository extends JpaRepository<SnrichProductRate, Long> {

	@Query("select spr.rate from SnrichProductRate spr where spr.snrichProduct.pid = ?1 and spr.orderProPaymentMode = ?2")
	Optional<Double> findRateBySnrichProductPidAndOrderProPaymentMode(String snrichProdctPid, 
			OrderProPaymentMode orderProPaymentMode);
	
	Optional<SnrichProductRate> findOneByPid(String pid); 
}
