
package com.orderfleet.webapp.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.web.rest.dto.KilometerCalculationDTO;


/**
 * @author Anish
 *
 */
public interface KilometreCalculationService {

	Optional<KilometerCalculationDTO> findByCompanyIdAndUserIdAndDate(Long companyId, String userId, LocalDate date);

	KilometerCalculationDTO save(KilometerCalculationDTO kilometreCalculationDTO, Long companyId);

	List<KilometerCalculationDTO> findAllByCompany();

	Page<KilometerCalculationDTO> findAllByCompanyIdOrderByDateDesc(Pageable pageable);

	List<KilometerCalculationDTO> findAllByCompanyIdAndUserPidAndDateBetweenOrderByCreatedDateDesc(String userPid,
			LocalDate fromDate, LocalDate toDate);

	List<KilometerCalculationDTO> findAllByCompanyIdAndUserPidAndDateOrderByCreatedDateDesc(String userPid, LocalDate fromDate);

}
