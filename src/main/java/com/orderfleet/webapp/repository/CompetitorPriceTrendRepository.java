package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.CompetitorPriceTrend;
import com.orderfleet.webapp.domain.CompetitorProfile;
import com.orderfleet.webapp.domain.User;

/**
 * Spring Data JPA repository for the CompetitorPriceTrend entity.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
public interface CompetitorPriceTrendRepository extends JpaRepository<CompetitorPriceTrend, Long> {

	Optional<CompetitorPriceTrend> findOneByPid(String pid);

	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where competitorPriceTrend.company.id = ?#{principal.companyId}")
	List<CompetitorPriceTrend> findAllByCompanyId();
	
	@Query("select cpt from CompetitorPriceTrend cpt where cpt.user.login = ?1 and cpt.competitorProfile.pid = ?2 and cpt.priceTrendProduct.pid = ?3 and cpt.createdDate between ?4 and ?5")
	List<CompetitorPriceTrend> findByLoginCompetitorPidProductPidAndDateBetween(String login, String competitorPid, String productPid, LocalDateTime start,LocalDateTime end);

	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where competitorPriceTrend.company.id = ?#{principal.companyId} and competitorPriceTrend.competitorProfile.pid=?1 and competitorPriceTrend.createdDate between ?2 and ?3 Order By competitorPriceTrend.createdDate asc")
	List<CompetitorPriceTrend> findByCompanyIdCompitatorPidAndDateBetween(String compitatorPid,LocalDateTime startDateTime, LocalDateTime endDateTime);

	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where competitorPriceTrend.company.id = ?#{principal.companyId} order by competitorPriceTrend.id desc")
	Page<CompetitorPriceTrend> findAllByCompanyId(Pageable pageable);

	CompetitorPriceTrend findTop1ByPriceTrendProductPidAndCompetitorProfilePidAndCreatedDateBetweenOrderByCreatedDateDesc(
			String priceTrendProductPid, String competitorPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where competitorPriceTrend.priceTrendProduct.pid in ?1 and competitorPriceTrend.priceTrendProductGroup.pid in ?2 and competitorPriceTrend.competitorProfile.pid in ?3 Order By competitorPriceTrend.createdDate desc")
	List<CompetitorPriceTrend> findProductByProductPidInAndProductGroupPidInAndCompetitorPidIn(List<String> productPids,
			List<String> productGroupPids, List<String> competitorPids);

	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where competitorPriceTrend.priceTrendProduct.pid in ?1 and competitorPriceTrend.priceTrendProductGroup.pid in ?2  Order By competitorPriceTrend.createdDate desc")
	List<CompetitorPriceTrend> findProductByProductPidInAndProductGroupPidIn(List<String> productPids,
			List<String> productGroupPids);

	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where  competitorPriceTrend.priceTrendProductGroup.pid in ?1 and competitorPriceTrend.competitorProfile.pid in ?2 Order By competitorPriceTrend.createdDate desc")
	List<CompetitorPriceTrend> findProductByProductGroupPidInAndCompetitorPidIn(List<String> productGroupPids,
			List<String> competitorPids);

	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where competitorPriceTrend.priceTrendProduct.pid in ?1 and  competitorPriceTrend.competitorProfile.pid in ?2 Order By competitorPriceTrend.createdDate desc")
	List<CompetitorPriceTrend> findProductByProductPidInAndCompetitorPidIn(List<String> productPids,
			List<String> competitorPids);

	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where competitorPriceTrend.priceTrendProduct.pid in ?1  Order By competitorPriceTrend.createdDate desc")
	List<CompetitorPriceTrend> findProductByProductPidIn(List<String> productPids);

	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where  competitorPriceTrend.priceTrendProductGroup.pid in ?1  Order By competitorPriceTrend.createdDate desc")
	List<CompetitorPriceTrend> findProductByProductGroupPidIn(List<String> productGroupPids);

	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where competitorPriceTrend.competitorProfile.pid in ?1 Order By competitorPriceTrend.createdDate desc")
	List<CompetitorPriceTrend> findProductByCompetitorPidIn(List<String> competitorPids);
	
	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where competitorPriceTrend.user.pid = ?1 and competitorPriceTrend.createdDate between ?2 and ?3 Order By competitorPriceTrend.createdDate desc")
	List<CompetitorPriceTrend> findProductByUserAndCreatedDateBetWeen(String userPid,LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where competitorPriceTrend.priceTrendProduct.pid in ?1 and competitorPriceTrend.priceTrendProductGroup.pid in ?2 and competitorPriceTrend.competitorProfile.pid in ?3 and competitorPriceTrend.user.pid = ?4 and competitorPriceTrend.createdDate between ?5 and ?6 Order By competitorPriceTrend.createdDate desc")
	List<CompetitorPriceTrend> findProductByProductPidInAndProductGroupPidInAndCompetitorPidInAndUserAndCreatedDateBetween(List<String> productPids,
			List<String> productGroupPids, List<String> competitorPids ,String userPid,LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where competitorPriceTrend.priceTrendProduct.pid in ?1 and competitorPriceTrend.priceTrendProductGroup.pid in ?2 and competitorPriceTrend.user.pid = ?3 and competitorPriceTrend.createdDate between ?4 and ?5  Order By competitorPriceTrend.createdDate desc")
	List<CompetitorPriceTrend> findProductByProductPidInAndProductGroupPidInAndUserAndCreatedDateBetween(List<String> productPids,
			List<String> productGroupPids,String userPid,LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where  competitorPriceTrend.priceTrendProductGroup.pid in ?1 and competitorPriceTrend.competitorProfile.pid in ?2 and competitorPriceTrend.user.pid = ?3 and competitorPriceTrend.createdDate between ?4 and ?5 Order By competitorPriceTrend.createdDate desc")
	List<CompetitorPriceTrend> findProductByProductGroupPidInAndCompetitorPidInAndUserAndCreatedDateBetween(List<String> productGroupPids,
			List<String> competitorPids,String userPid,LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where competitorPriceTrend.priceTrendProduct.pid in ?1 and  competitorPriceTrend.competitorProfile.pid in ?2 and competitorPriceTrend.user.pid = ?3 and competitorPriceTrend.createdDate between ?4 and ?5 Order By competitorPriceTrend.createdDate desc")
	List<CompetitorPriceTrend> findProductByProductPidInAndCompetitorPidInAndUserAndCreatedDateBetween(List<String> productPids,
			List<String> competitorPids,String userPid,LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where competitorPriceTrend.priceTrendProduct.pid in ?1 and  competitorPriceTrend.user.pid = ?2 and competitorPriceTrend.createdDate between ?3 and ?4  Order By competitorPriceTrend.createdDate desc")
	List<CompetitorPriceTrend> findProductByProductPidInAndUserAndCreatedDateBetween(List<String> productPids,String userPid,LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where  competitorPriceTrend.priceTrendProductGroup.pid in ?1 and competitorPriceTrend.user.pid = ?2 and competitorPriceTrend.createdDate between ?3 and ?4 Order By competitorPriceTrend.createdDate desc")
	List<CompetitorPriceTrend> findProductByProductGroupPidInAndUserAndCreatedDateBetween(List<String> productGroupPids,String userPid,LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select competitorPriceTrend from CompetitorPriceTrend competitorPriceTrend where competitorPriceTrend.competitorProfile.pid in ?1 and competitorPriceTrend.user.pid = ?2 and competitorPriceTrend.createdDate between ?3 and ?4 Order By competitorPriceTrend.createdDate desc")
	List<CompetitorPriceTrend> findProductByCompetitorPidInAndUserAndCreatedDateBetween(List<String> competitorPids,String userPid,LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query("select competitorPriceTrend.competitorProfile from CompetitorPriceTrend competitorPriceTrend where competitorPriceTrend.priceTrendProduct.id = ?1 and competitorPriceTrend.user.pid = ?2 and competitorPriceTrend.createdDate between ?3 and ?4 Order By competitorPriceTrend.createdDate desc")
	List<CompetitorProfile> findCompetitorProfileByProductIdAndCreatedDateBetween(Long productId,String userPid, LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query("select competitorPriceTrend.user from CompetitorPriceTrend competitorPriceTrend where competitorPriceTrend.priceTrendProduct.id = ?1 and competitorPriceTrend.competitorProfile.pid = ?2 and competitorPriceTrend.createdDate between ?3 and ?4 Order By competitorPriceTrend.createdDate desc")
	List<User> findUserByProductIdAndCompetitorProfilePidAndCreatedDateBetween(Long productId,String competitorPid, LocalDateTime fromDate, LocalDateTime toDate);
	
	
}
