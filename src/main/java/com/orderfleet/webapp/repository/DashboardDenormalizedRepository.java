package com.orderfleet.webapp.repository;

import com.orderfleet.webapp.web.rest.Features.dashboraddenormalised.DashboardNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DashboardDenormalizedRepository extends JpaRepository<DashboardNew, Long> {

    @Query("select dashboardNew from DashboardNew  dashboardNew where date = ?1 and userId = ?2 and documentId = ?3 and activityId = ?4 and companyId = ?5 and territoryId = ?6")
    Optional<DashboardNew> findByDateUserDocActivityCompanyTerritory(LocalDate date, Long userId, Long DocId, Long activityId, Long companyId, Long territoryId);

    @Transactional
    @Modifying
    @Query("update DashboardNew dashboardNew set dashboardNew.submissionCount = ?1 , dashboardNew.amount = ?2 , dashboardNew.modifiedDate = ?3 where dashboardNew.id = ?4 ")
    void updateBySubmissionCountAmountDate(double subCnt, double amnt, LocalDate mdate, Long id);


    @Transactional
    @Modifying
    @Query("update DashboardNew dashboardNew set dashboardNew.submissionCount = ?1 , dashboardNew.amount = ?2 , dashboardNew.modifiedDate = ?3 , dashboardNew.planedCount = ?5 , dashboardNew.plannedAmount=?6 where dashboardNew.id = ?4 ")
    void updateBySubmissionCountAmountDatePlanned(long subCnt, double amnt, LocalDate mdate, Long id,long planedCount,double plannedAmount);

    @Transactional
    @Modifying
    @Query("update DashboardNew dashboardNew set dashboardNew.submissionCount = ?1 , dashboardNew.amount = ?2 , dashboardNew.modifiedDate = ?3,dashboardNew.unplannedCount = ?5 ,dashboardNew.unplannedAmount=?6 where dashboardNew.id = ?4 ")
    void updateBySubmissionCountAmountDateUnplanned(long subCnt, double amnt, LocalDate mdate, Long id,long unplannedCount,double unplannedAmount);


    @Query("select dashboardNew from DashboardNew  dashboardNew where companyId = ?1 and dashboardNew.userId in ?2 and dashboardNew.date between ?3 and ?4")
    List<DashboardNew> findAllByCompanyIdAndActivityIdsInAndUserIdsinAndDate(long companyId,List<Long>userIds,LocalDate from,LocalDate to);

    List<DashboardNew> findAllByExePid(String exepid) ;

}
