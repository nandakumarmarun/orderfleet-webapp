package com.orderfleet.webapp.web.rest;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.YtdReportDay;
import com.orderfleet.webapp.web.rest.dto.YtdReportMonth;
import com.orderfleet.webapp.web.rest.dto.YtdReportWeek;

/**
 * Web controller for managing YTDReport.
 * 
 * @author Muhammed Riyas T
 * @since October 15, 2016
 */
@Controller
@RequestMapping("/web")
public class YTDReportResource {

	private final Logger log = LoggerFactory.getLogger(YTDReportResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFinding");
	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@RequestMapping("/ytd-report")
	public String ytdReport() {
		log.debug("Web request to ytd report.......");
		return "company/ytdReport";
	}

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@RequestMapping(value = "/ytd-report/getReport", method = RequestMethod.GET)
	public @ResponseBody List<YtdReportMonth> getReport() {

		log.info("load ytd report data.................");

		int year = LocalDate.now().getYear();

		List<YtdReportMonth> reports = new ArrayList<>();
		for (int i = 1; i < 12; i++) {

			YearMonth yearMonth = YearMonth.of(year, i);
			LocalDateTime start = yearMonth.atDay(1).atTime(0, 0);
			LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59);

			YtdReportMonth ytdReportMonth = new YtdReportMonth();

			// set month name
			ytdReportMonth.setMonth(getMonth(i));

			// set total count(TC) in a month
			ytdReportMonth.setTc(executiveTaskExecutionRepository.countByDateBetween(start, end));

			// set producive count(PC), Amount and Volume in a month
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			
			String id = "INV_QUERY_117" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get amount count and volume by date between ";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			Object obj = inventoryVoucherHeaderRepository.getCountAndAmountAndVolumeByDateBetween(start, end);
			String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	      logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
			Object[] countKgAndAmount = (Object[]) obj;

			ytdReportMonth.setPc((long) countKgAndAmount[0]);
			if (countKgAndAmount[1] != null) {
				ytdReportMonth.setAmount((Double) (countKgAndAmount[1]));
			}
			if (countKgAndAmount[2] != null) {
				ytdReportMonth.setVolume((Double) (countKgAndAmount[2]));
			}

			// set Efficiency in a month
			try {
				ytdReportMonth.setEfficiency(ytdReportMonth.getPc() * 100 / ytdReportMonth.getTc());
			} catch (ArithmeticException e) {

			}

			// get no of weeks in this month
			int noOfWeeksInMonth = getNoOfWeekInaMonth(i);

			// create week report
			for (int j = 1; j <= noOfWeeksInMonth; j++) {

				List<Date> dates = getDatesInWeek(i, j);

				YtdReportWeek ytdReportWeek = new YtdReportWeek();
				ytdReportWeek.setWeek("Week " + j);
				if (ytdReportMonth.getTc() > 0) {
					Instant instant = Instant.ofEpochMilli(dates.get(0).getTime());
					LocalDateTime weekStart = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
					weekStart.with(LocalTime.MIN);

					instant = Instant.ofEpochMilli(dates.get(dates.size() - 1).getTime());
					LocalDateTime weekEnd = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
					weekEnd.with(LocalTime.MAX);

					// set total count(TC) in a week
					ytdReportWeek.setTc(executiveTaskExecutionRepository.countByDateBetween(weekStart, weekEnd));

					// set producive count(PC), Amount and Volume in a week
					Object objWeek = inventoryVoucherHeaderRepository.getCountAndAmountAndVolumeByDateBetween(weekStart,
							weekEnd);
					Object[] countKgAndAmountWeek = (Object[]) objWeek;

					ytdReportWeek.setPc((long) countKgAndAmountWeek[0]);
					if (countKgAndAmountWeek[1] != null) {
						ytdReportWeek.setAmount((Double) (countKgAndAmountWeek[1]));
					}
					if (countKgAndAmountWeek[2] != null) {
						ytdReportWeek.setVolume((Double) (countKgAndAmountWeek[2]));
					}

					// set Efficiency in a week
					try {
						ytdReportWeek.setEfficiency(ytdReportWeek.getPc() * 100 / ytdReportWeek.getTc());
					} catch (ArithmeticException e) {
					}
				}
				// create day report
				for (Date date : dates) {
					YtdReportDay reportDay = new YtdReportDay();
					reportDay.setDay(dateFormat.format(date));

					if (ytdReportWeek.getTc() > 0) {
						Instant instant = Instant.ofEpochMilli(date.getTime());
						LocalDateTime day = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

						// set total count(TC) in a day
						reportDay.setTc(executiveTaskExecutionRepository
								.countByDateBetween(day.with(LocalTime.MIN), day.with(LocalTime.MAX)));

						// set producive count(PC), Amount and Volume in a day
						Object objDay = inventoryVoucherHeaderRepository.getCountAndAmountAndVolumeByDateBetween(
								day.with(LocalTime.MIN), day.with(LocalTime.MAX));
						Object[] countKgAndAmountDay = (Object[]) objDay;

						reportDay.setPc((long) countKgAndAmountDay[0]);
						if (countKgAndAmountDay[1] != null) {
							reportDay.setAmount((Double) (countKgAndAmountDay[1]));
						}
						if (countKgAndAmountDay[2] != null) {
							reportDay.setVolume((Double) (countKgAndAmountDay[2]));
						}

						// set Efficiency in a day
						try {
							reportDay.setEfficiency(reportDay.getPc() * 100 / reportDay.getTc());
						} catch (ArithmeticException e) {
						}
					}
					ytdReportWeek.getDays().add(reportDay);
				}
				ytdReportMonth.getWeeks().add(ytdReportWeek);
			}
			reports.add(ytdReportMonth);
		}
		return reports;
	}

	public String getMonth(int month) {
		return new DateFormatSymbols().getMonths()[month - 1];
	}

	public static int getNoOfWeekInaMonth(int i) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, i - 1);
		int maxWeeknumber = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
		return maxWeeknumber;
	}

	public static List<Date> getDatesInWeek(int month, int week) {
		month = month - 1;
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
