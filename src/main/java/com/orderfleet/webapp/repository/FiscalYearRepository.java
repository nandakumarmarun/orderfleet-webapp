package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.FiscalYear;

public interface FiscalYearRepository extends JpaRepository<FiscalYear, Long>   {

}
