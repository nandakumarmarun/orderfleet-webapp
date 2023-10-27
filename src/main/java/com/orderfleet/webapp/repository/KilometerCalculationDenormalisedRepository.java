package com.orderfleet.webapp.repository;

import com.orderfleet.webapp.domain.InwardOutwardStockLocation;
import com.orderfleet.webapp.domain.KilometerCalculationDenormalised;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface KilometerCalculationDenormalisedRepository extends JpaRepository<KilometerCalculationDenormalised, Long> {

    @Query("select kilometerCalculationDenormalised from KilometerCalculationDenormalised kilometerCalculationDenormalised where kilometerCalculationDenormalised.companyId=?1 and kilometerCalculationDenormalised.createdDate Between ?2 and ?3 and kilometerCalculationDenormalised.UserPid = ?4")
    List<KilometerCalculationDenormalised>  findAllByDateBetweenAndCompanyId(long companyid, LocalDateTime fromDate , LocalDateTime toDate,String userPid);

    @Query("select kilometerCalculationDenormalised from KilometerCalculationDenormalised kilometerCalculationDenormalised where kilometerCalculationDenormalised.companyId=?1 and kilometerCalculationDenormalised.taskExecutionPid = ?2")
    KilometerCalculationDenormalised findByTaskExecutionPid(long companyid , String taskExePid);

    @Query(value= "select * from tbl_kilometer_calculation_denormalised where company_id =?1 and punch_in_date between ?2 and ?3 and user_pid=?4 and is_calculated='true' ORDER BY punch_in_date DESC Limit 1",nativeQuery = true)
    Optional<KilometerCalculationDenormalised> findTop1ByTaskExecutionPid(long companyid, LocalDateTime fromDate , LocalDateTime toDate, String userPid);

}
