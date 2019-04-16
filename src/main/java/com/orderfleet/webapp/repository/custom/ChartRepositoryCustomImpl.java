package com.orderfleet.webapp.repository.custom;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompetitorProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompetitorPriceTrendRepository;
import com.orderfleet.webapp.repository.CompetitorProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.chart.dto.BarCharrtDTO;

@Component
public class ChartRepositoryCustomImpl implements ChartRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private CompetitorPriceTrendRepository competitorPriceTrendRepository;

	@Inject
	private UserRepository userRepository;
	
	@Inject
	private CompetitorProfileRepository competitorProfileRepository;

	@SuppressWarnings("unchecked")
	@Override
	public BarCharrtDTO getCompetitorPriceTrendByUser(Long productId, String userPid, LocalDateTime from, LocalDateTime to,
			String priceTrendName) {
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		List<CompetitorProfile> competitorProfiles = competitorPriceTrendRepository
				.findCompetitorProfileByProductIdAndCreatedDateBetween(productId, userPid, from, to);
		if (competitorProfiles.isEmpty()) {
			return null;
		} else {
			competitorProfiles = competitorProfiles.stream().filter(distinctByKey(cpt -> cpt.getName())).collect(Collectors.toList());
			List<String> labels = new ArrayList<>();
			List<String> colors = new ArrayList<>();
			
			StringBuilder queryString = new StringBuilder("WITH competitor_pricetrend_chart AS (");
			StringBuilder selectQueryString = new StringBuilder(" select date ");
			for (CompetitorProfile competitorProfile : competitorProfiles) {
				// create labels for graph
				labels.add(competitorProfile.getName());
				colors.add(competitorProfile.getChartColor());
				
				StringBuilder subQueryString = new StringBuilder("SELECT cast(created_date as date) date");
				String cProfileName = competitorProfile.getName().replaceAll("[^a-zA-Z0-9]", "");

				selectQueryString.append(" ,sum(" + cProfileName + ") as " + cProfileName);
				for (int i = 0; i < competitorProfiles.size(); i++) {
					if (competitorProfiles.get(i).getName().equals(competitorProfile.getName())) {
						subQueryString.append(" ," + priceTrendName + " as " + cProfileName);
					} else {
						String cpName = competitorProfiles.get(i).getName().replaceAll("[^a-zA-Z0-9]", "");
						subQueryString.append(" ,0 as " + cpName);
					}
				}
				subQueryString
						.append(" FROM tbl_competitor_price_trend where company_id = :companyId and price_trend_product_id = :priceTrendProductId and competitor_profile_id = "
								+ competitorProfile.getId() + " and created_date between :fromDate and :toDate");
				userRepository.findOneByPid(userPid).ifPresent(u -> subQueryString.append(" and user_id=" + u.getId()));
				queryString.append(subQueryString + " UNION ");
			}
			// remove last union
			int lastIdx = queryString.lastIndexOf("UNION");
			queryString.replace(lastIdx, lastIdx + "UNION".length(), "");
			queryString
					.append(")" + selectQueryString + " from competitor_pricetrend_chart group by date order by date");

			BarCharrtDTO barChartDTO = new BarCharrtDTO();
			barChartDTO.setLabels(labels);
			barChartDTO.setColors(colors);
			
			Query q = em.createNativeQuery(queryString.toString());
			q.setParameter("companyId", companyId);
			q.setParameter("priceTrendProductId", productId);
			q.setParameter("fromDate", from);
			q.setParameter("toDate", to);
			barChartDTO.setBarChartDatas(q.getResultList());
			return barChartDTO;
		}

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public BarCharrtDTO getCompetitorPriceTrendByCompetitorProfile(Long productId, String competitorPid,
			LocalDateTime from, LocalDateTime to, String priceTrendName) {
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		List<User> users = competitorPriceTrendRepository
				.findUserByProductIdAndCompetitorProfilePidAndCreatedDateBetween(productId, competitorPid, from, to);
		if (users.isEmpty()) {
			return null;
		} else {
			users = users.stream().filter(distinctByKey(cpt -> cpt.getFirstName())).collect(Collectors.toList());
			List<String> labels = new ArrayList<>();
			List<String> colors = new ArrayList<>();
			
			StringBuilder queryString = new StringBuilder("WITH competitor_pricetrend_chart AS (");
			StringBuilder selectQueryString = new StringBuilder(" select date ");
			for (User user : users) {
				// create labels for graph
				labels.add(user.getFirstName());
				colors.add(user.getChartColor());
				
				StringBuilder subQueryString = new StringBuilder("SELECT cast(created_date as date) date");
				String cProfileName = user.getFirstName().replaceAll("[^a-zA-Z0-9]", "");

				selectQueryString.append(" ,sum(" + cProfileName + ") as " + cProfileName);
				for (int i = 0; i < users.size(); i++) {
					if (users.get(i).getFirstName().equals(user.getFirstName())) {
						subQueryString.append(" ," + priceTrendName + " as " + cProfileName);
					} else {
						String cpName = users.get(i).getFirstName().replaceAll("[^a-zA-Z0-9]", "");
						subQueryString.append(" ,0 as " + cpName);
					}
				}
				subQueryString
						.append(" FROM tbl_competitor_price_trend where company_id = :companyId and price_trend_product_id = :priceTrendProductId and user_id = "
								+ user.getId() + " and created_date between :fromDate and :toDate");
				
				competitorProfileRepository.findOneByPid(competitorPid).ifPresent(u -> subQueryString.append(" and competitor_profile_id=" + u.getId()));
				queryString.append(subQueryString + " UNION ");
			}
			// remove last union
			int lastIdx = queryString.lastIndexOf("UNION");
			queryString.replace(lastIdx, lastIdx + "UNION".length(), "");
			queryString
					.append(")" + selectQueryString + " from competitor_pricetrend_chart group by date order by date");

			BarCharrtDTO barChartDTO = new BarCharrtDTO();
			barChartDTO.setLabels(labels);
			barChartDTO.setColors(colors);
			
			Query q = em.createNativeQuery(queryString.toString());
			q.setParameter("companyId", companyId);
			q.setParameter("priceTrendProductId", productId);
			q.setParameter("fromDate", from);
			q.setParameter("toDate", to);
			barChartDTO.setBarChartDatas(q.getResultList());
			return barChartDTO;
		}
	}
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Map<Object,Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

}
