
package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.IntegrationModule;

/**
 * @author Anish
 * @since Aug 23, 2018
 */
public interface IntegrationModuleRepository extends JpaRepository<IntegrationModule, String>{

}
