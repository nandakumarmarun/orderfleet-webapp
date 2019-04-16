package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.text.DateFormatSymbols;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.CompetitorPriceTrend;
import com.orderfleet.webapp.service.CompetitorPriceTrendService;
import com.orderfleet.webapp.service.PriceTrendConfigurationService;
import com.orderfleet.webapp.service.PriceTrendProductCompetitorService;
import com.orderfleet.webapp.service.PriceTrendProductGroupService;
import com.orderfleet.webapp.web.rest.dto.CompetitorProfileDTO;
import com.orderfleet.webapp.web.rest.dto.CompitatorView;
import com.orderfleet.webapp.web.rest.dto.FullMonth;
import com.orderfleet.webapp.web.rest.dto.FullWeek;
import com.orderfleet.webapp.web.rest.dto.PriceTrendConfigurationDTO;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductDTO;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductGroupView;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductView;
import com.orderfleet.webapp.web.rest.dto.PriceView;

/**
 * Web controller for managing Activity Hour Reporting.
 * 
 * @author Muhammed Riyas T
 * @since October 07, 2016
 */
@Controller
@RequestMapping("/web")
public class ActivityHourReportingResource {

	private final Logger log = LoggerFactory.getLogger(ActivityHourReportingResource.class);

	@Inject
	private PriceTrendProductGroupService priceTrendProductGroupService;

	@Inject
	private PriceTrendProductCompetitorService PriceTrendProductCompetitorService;

	@Inject
	private PriceTrendConfigurationService priceTrendConfigurationService;

	@Inject
	private CompetitorPriceTrendService competitorPriceTrendService;

	/**
	 * GET /marketing-activity-performance
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of users in
	 *         body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/activity-hour-reporting", method = RequestMethod.GET)
	public String activityHourReporting(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Marketing Activity Performance");
		model.addAttribute("priceTrendProductGroups", priceTrendProductGroupService.findAllByCompany());
		return "company/activityHourReporting";
	}

	@RequestMapping(value = "/activity-hour-reporting/load-data", method = RequestMethod.GET)
	public @ResponseBody PriceTrendProductGroupView getSingleProductGroup(
			@RequestParam String priceTrendProductGroupPid) {
		PriceTrendProductGroupDTO priceTrendProductGroup = priceTrendProductGroupService
				.findOneByPid(priceTrendProductGroupPid).get();
		PriceTrendProductGroupView priceTrendProductGroupView = toProductGroupView(priceTrendProductGroup);
		return priceTrendProductGroupView;
	}

	private PriceTrendProductGroupView toProductGroupView(PriceTrendProductGroupDTO productGroup) {
		PriceTrendProductGroupView productGroupView = new PriceTrendProductGroupView();

		List<PriceTrendConfigurationDTO> configurationDTOs = priceTrendConfigurationService.findAllByCompany();
		productGroupView.setPid(productGroup.getPid());
		productGroupView.setName(productGroup.getName());
		List<PriceTrendProductView> priceTrendProductViews = new ArrayList<PriceTrendProductView>();
		for (PriceTrendProductDTO priceTrendProduct : productGroup.getPriceTrendProducts()) {
			PriceTrendProductView priceTrendProductView = new PriceTrendProductView();
			priceTrendProductView.setPid(priceTrendProduct.getPid());
			priceTrendProductView.setName(priceTrendProduct.getName());

			List<CompetitorProfileDTO> competitors = PriceTrendProductCompetitorService
					.findCompetitorsByPriceTrendProductPid(priceTrendProduct.getPid());
			List<CompitatorView> compitataorViews = new ArrayList<CompitatorView>();
			for (CompetitorProfileDTO competitor : competitors) {
				CompitatorView compitatorView = new CompitatorView();
				compitatorView.setPid(competitor.getPid());
				compitatorView.setName(competitor.getName());

				List<FullMonth> months = new ArrayList<FullMonth>();
				for (int i = 0; i < 12; i++) {

					FullMonth month = new FullMonth();
					int noOfWeeksInMonth = getNoOfWeekInaMonth(i);
					month.setName(getMonth(i));
					month.setNoOfWeeks(noOfWeeksInMonth);
					List<FullWeek> weeks = new ArrayList<FullWeek>();
					for (int j = 1; j <= noOfWeeksInMonth; j++) {

						List<Date> dates = getDatesInWeek(i, j);

						Instant instant = Instant.ofEpochMilli(dates.get(0).getTime());
						LocalDateTime weekStart = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
						weekStart.with(LocalTime.MIN);

						instant = Instant.ofEpochMilli(dates.get(dates.size() - 1).getTime());
						LocalDateTime weekEnd = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
						weekEnd.with(LocalTime.MIDNIGHT);

						CompetitorPriceTrend competitorPriceTrend = competitorPriceTrendService
								.findByProductPidAndCompetitorPidAndDateBetween(priceTrendProduct.getPid(),
										competitor.getPid(), weekStart, weekEnd);

						FullWeek week = new FullWeek();
						List<PriceView> prices = new ArrayList<>();
						week.setWeek("Week " + j);

						for (PriceTrendConfigurationDTO configuration : configurationDTOs) {
							if (!configuration.getName().equals("remarks")) {
								double price = 0;
								if (competitorPriceTrend != null) {
									if (!configuration.getName().equals("price1")) {
										price = competitorPriceTrend.getPrice1();
									} else if (!configuration.getName().equals("price2")) {
										price = competitorPriceTrend.getPrice2();
									} else if (!configuration.getName().equals("price3")) {
										price = competitorPriceTrend.getPrice3();
									} else if (!configuration.getName().equals("price4")) {
										price = competitorPriceTrend.getPrice4();
									} else if (!configuration.getName().equals("price5")) {
										price = competitorPriceTrend.getPrice5();
									}
								}
								prices.add(new PriceView(configuration.getValue(), price));
							}
						}
						week.setPrices(prices);
						weeks.add(week);
					}
					month.setWeeks(weeks);
					months.add(month);
				}
				compitatorView.setMonths(months);
				compitataorViews.add(compitatorView);
			}
			priceTrendProductView.setCompitators(compitataorViews);
			priceTrendProductViews.add(priceTrendProductView);
		}
		productGroupView.setProductCategories(priceTrendProductViews);

		List<FullMonth> months = toMonthAndWeeks();

		productGroupView.setMonths(months);

		return productGroupView;
	}

	private List<FullMonth> toMonthAndWeeks() {
		List<FullMonth> months = new ArrayList<FullMonth>();
		for (int i = 0; i < 12; i++) {

			FullMonth month = new FullMonth();

			int noOfWeeksInMonth = getNoOfWeekInaMonth(i);

			month.setName(getMonth(i));

			month.setNoOfWeeks(noOfWeeksInMonth);
			List<FullWeek> weeks = new ArrayList<FullWeek>();
			for (int j = 1; j <= noOfWeeksInMonth; j++) {

				FullWeek week = new FullWeek();

				week.setWeek("Week " + j);
				weeks.add(week);
			}
			month.setWeeks(weeks);
			months.add(month);
		}

		return months;
	}

	public String getMonth(int month) {
		return new DateFormatSymbols().getMonths()[month];
	}

	public static int getNoOfWeekInaMonth(int i) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, i);
		int maxWeeknumber = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
		return maxWeeknumber;
	}

	public static List<Date> getDatesInWeek(int month, int week) {
		List<Date> dates = new ArrayList<>();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, month);
		c.set(Calendar.WEEK_OF_MONTH, week);
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = 0; i < 7; i++) {
			Date date = c.getTime();

			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int year1 = cal.get(Calendar.YEAR);
			int month1 = cal.get(Calendar.MONTH);

			if (year1 == year && month1 == month) {
				dates.add(date);
			}
			c.add(Calendar.DATE, 1);
		}
		return dates;
	}
}
