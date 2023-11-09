package com.orderfleet.webapp.repository;


import com.orderfleet.webapp.domain.UserDistance;
import com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmDistanceFare.KmDistanceFare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface KmDistanceFareRepository extends JpaRepository<KmDistanceFare, Long> {

    @Query("select kdf from KmDistanceFare kdf where kdf.company.id=?1 and user.pid=?2 and kdf.PlannedDate=?3")
    Optional<KmDistanceFare> findByCompanyIdAndUserPidAndPlannedDate(Long companyId, String userPid, LocalDate date);

    @Query("select kdf from KmDistanceFare kdf where kdf.company.id=?1 and user.pid=?2 and kdf.PlannedDate between ?3 and ?4")
    List<KmDistanceFare> findAllKmDistanceFaresByCompanyIdAndUserPidAndPlannedDate(Long companyId, String userPid, LocalDate date,LocalDate toDate);

}
