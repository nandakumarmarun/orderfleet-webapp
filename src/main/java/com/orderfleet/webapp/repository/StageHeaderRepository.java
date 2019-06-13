package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.StageHeader;
import com.orderfleet.webapp.domain.enums.StageNameType;

public interface StageHeaderRepository extends JpaRepository<StageHeader, Long> {
	
	public static final String FUNNEL_QUERY = "WITH Final_stages AS (SELECT max(a.created_date) as created_date, a.account_profile_id from tbl_stage_header a where a.company_id = ?#{principal.companyId} and a.created_date BETWEEN ?1 and ?2 group by a.account_profile_id), funnel AS ( select a.account_profile_id, a.stage_id, c.funnel_id, sum(value) as value from tbl_stage_header a, Final_stages b, tbl_funnel_stage c where a.company_id = ?#{principal.companyId} and a.account_profile_id = b.account_profile_id and a.created_date=b.created_date and c.stage_id=a.stage_id group by a.account_profile_id, a.stage_id, c.funnel_id) select b.name,count(a.funnel_id), sum(a.value) as amount from funnel a, tbl_funnel b where b.id=a.funnel_id group by b.name";
	
	public static final String FUNNEL_DETAIL_QUERY = "WITH Final_stages AS (SELECT max(a.created_date) as created_date, a.account_profile_id from tbl_stage_header a where a.company_id = ?#{principal.companyId} and a.created_date BETWEEN ?1 and ?2 group by a.account_profile_id), funnel AS ( select a.account_profile_id, a.stage_id, c.funnel_id, sum(value) as value from tbl_stage_header a, Final_stages b, tbl_funnel_stage c where a.company_id = ?#{principal.companyId} and a.account_profile_id = b.account_profile_id and a.created_date=b.created_date and c.stage_id=a.stage_id group by a.account_profile_id, a.stage_id, c.funnel_id) select b.name as StageGroup, c.name as Stage, count(a.stage_id), sum(a.value) as amount, c.sort_order from funnel a, tbl_funnel b, tbl_stage c where b.id=a.funnel_id and c.id=a.stage_id group by b.name, c.name, c.sort_order Order by c.sort_order";
	
	public static final String STAGE_REPORT_QUERY = "WITH Final_stages AS (SELECT max(a.created_date) as created_date, a.account_profile_id from tbl_stage_header a where a.company_id = ?#{principal.companyId} and a.created_date BETWEEN ?1 and ?2 group by a.account_profile_id), funnel AS ( select a.account_profile_id, a.stage_id, c.funnel_id, sum(value) as value from tbl_stage_header a, Final_stages b, tbl_funnel_stage c where a.company_id = ?#{principal.companyId} and a.account_profile_id = b.account_profile_id and a.created_date=b.created_date and c.stage_id=a.stage_id group by a.account_profile_id, a.stage_id, c.funnel_id) select a.account_profile_id as accountProfile, c.name as Stage, count(a.stage_id), sum(a.value) as amount, c.sort_order from funnel a, tbl_funnel b, tbl_stage c where b.id=a.funnel_id and c.id=a.stage_id group by a.account_profile_id, c.name, c.sort_order Order by c.sort_order";
	
	public static final String STAGE_COUNT_REPORT_QUERY= "select s.account_profile_id from tbl_stage_header s where s.stage_id = ?1 and s.account_profile_id IN (?2) and s.created_date IN (select max(ss.created_date) from tbl_stage_header ss where ss.stage_id = ?1 and ss.account_profile_id IN (?2) and ss.created_date between ?3 and ?4  group by ss.account_profile_id ) group by s.account_profile_id";
	
	@Query("select stageHeader from StageHeader stageHeader where stageHeader.createdBy.id in ?1 and stageHeader.stage.pid in ?2 and stageHeader.accountProfile.pid in ?3 and stageHeader.createdDate between ?4 and ?5 order by stageHeader.createdDate DESC")
	List<StageHeader> findByUserIdInAndStagePidInAndAccountPidInAndDateBetween(List<Long> userIds, List<String> stagePids, Set<String> accountPids, LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query("select stageHeader from StageHeader stageHeader where stageHeader.createdBy.id in ?1 and stageHeader.stage.pid in ?2 and stageHeader.accountProfile.pid in ?3 and stageHeader.id in ?4 order by stageHeader.createdDate DESC")
	List<StageHeader> findByUserIdInAndStagePidInAndAccountPidInAndDateBetween(List<Long> userIds, List<String> stagePids, Set<String> accountPids, Set<Long> sHeaderIds);

	@Query("select stageHeader from StageHeader stageHeader where stageHeader.accountProfile.pid in ?1 order by stageHeader.createdDate DESC")
	List<StageHeader> findByAccountPidIn(List<String> accountPids);

	@Query("select stageHeader.id, stageHeader.stage.id, stageHeader.stage.name, stageHeader.accountProfile.pid from StageHeader stageHeader where stageHeader.stage.id in ?1 and stageHeader.createdDate between ?2 and ?3")
	List<Object[]> findByStageIdInAndDateBetween(Set<Long> stageIds, LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query(value = FUNNEL_QUERY, nativeQuery = true)
	List<Object[]> findFunnelByDateBetween(LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query(value = FUNNEL_DETAIL_QUERY, nativeQuery = true)
	List<Object[]> findFunnelDetails(LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query("select s.accountProfile.pid , s.stage.name ,s.createdDate from StageHeader s where s.createdDate ="
			+ " (select max(sh.createdDate) From  StageHeader sh where s.accountProfile.pid = sh.accountProfile.pid )")
	List<Object[]> findByDateIfPresent();
	
	@Query("select s.accountProfile.pid from StageHeader s where s.stage.stageNameType = ?1")
	List<String> findByStageStageNameType(StageNameType stageNameType);
	
	@Query("select s from StageHeader s where s.stage.stageNameType = ?1 and s.accountProfile.pid in ?2 and s.createdDate between ?3 and ?4 order by s.createdDate DESC")
	List<StageHeader> findByUserIdInAndStagePidInAndAccountPidInAndDateBetween(StageNameType stageNameType,Set<String> accountPids, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select s.employeeProfile.name, s.accountProfile.id, s.createdDate from StageHeader s where s.employeeProfile.pid in ?1 and s.stage.id = ?2 and s.createdDate between ?3 and ?4 order by s.employeeProfile.name ASC")
	List<Object[]> findEmployeeNameAndAccountIdByEmployeePidInAndStageIdAndDateBetween(List<String> employeePids, Long stageId, LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query("select s.accountProfile.id, s.createdDate from StageHeader s where s.stage.id = ?1 and s.accountProfile.id in ?2 and s.createdDate between ?3 and ?4")
	List<Object[]> findByStageIdAndAccountIdInAndDateBetween(Long stageId,Set<Long> accountIds, LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query("select count(s.id) from StageHeader s where s.stage.id = ?1 and s.accountProfile.id in ?2 and s.createdDate between ?3 and ?4")
	long findCountByStageIdAndAccountIdInAndDateBetween(Long stageId,Set<Long> accountIds, LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query(value = STAGE_COUNT_REPORT_QUERY,nativeQuery = true)
	List<Long> findCountByStageIdAndDistinctAccountIdInAndDateBetween(Long stageId,Set<Long> accountIds, LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query(value = "SELECT sh.id from StageHeader sh where sh.createdDate IN (SELECT max(a.createdDate) from StageHeader a where a.company.id = ?#{principal.companyId} and a.createdDate BETWEEN ?1 and ?2 group by a.accountProfile.id)")
	Set<Long> findIdByCreatedDateBetween(LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query(value = "SELECT sh.id from StageHeader sh where sh.company.id = ?#{principal.companyId} and sh.createdDate BETWEEN ?1 and ?2 ")
	Set<Long> findCustomerGroupIdByCreatedDateBetween(LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select stageHeader from StageHeader stageHeader where stageHeader.employeeProfile.pid = ?1 and stageHeader.id in ?2")
	List<StageHeader> findByUserIdIn(String employeePid,List<Long> stageHeaderId);

	@Query("select stageHeader from StageHeader stageHeader where stageHeader.accountProfile.pid in ?1 and stageHeader.stage.pid in ?2 order by stageHeader.createdDate DESC")
	List<StageHeader> findByAccountPidInAndStagePidIn(List<String> accountPids,List<String> stagePid);
	
	@Query("select stageHeader from StageHeader stageHeader where stageHeader.accountProfile.id in ?1 order by stageHeader.createdDate DESC")
	List<StageHeader> findByAccountIdIn(List<Long> accountPids);
	
	@Query("select stageHeader.id from StageHeader stageHeader where stageHeader.company.id = ?#{principal.companyId} and stageHeader.createdDate BETWEEN ?1 and ?2")
	Set<Long> findIdByCompanyAndCreatedDateBetween(LocalDateTime fromDate, LocalDateTime toDate);
	
	//for displaying the lead details of employees
	@Query("select s.employeeProfile.name, s.accountProfile.id, s.createdDate ,s.employeeProfile.pid from StageHeader s where s.employeeProfile.pid in ?1 and s.stage.id = ?2 and s.createdDate between ?3 and ?4 order by s.employeeProfile.name ASC")
	List<Object[]> findEmployeeNameAndAccountIdByEmployeePidInAndStageIdAndDateBetweenWithEmpPid(List<String> employeePids, Long stageId, LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query("select s.accountProfile.id from StageHeader s where s.employeeProfile.pid = ?1 and s.stage.id = ?2 and s.createdDate between ?3 and ?4 group by s.accountProfile.id")
	Set<Long> findCountAccountIdByEmployeePidInAndStagePidInAndDateBetween(String employeePids, Long stageIds, LocalDateTime fromDate, LocalDateTime toDate);
	
}
