package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.SalesLedger;
import com.orderfleet.webapp.domain.SalesLedgerWiseTarget;
import com.orderfleet.webapp.repository.SalesLedgerRepository;
import com.orderfleet.webapp.repository.SalesLedgerWiseTargetRepository;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.SalesLedgerWiseTargetService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.SalesLedgerDTO;
import com.orderfleet.webapp.web.rest.dto.SalesLedgerWiseMonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.dto.SalesLedgerWiseTargetDTO;

/**
 * Web controller for managing SalesLedgerWiseTarget.
 * 
 * @author Muhammed Riyas T
 * @since June 16, 2016
 */
@Controller
@RequestMapping("/web")
public class SalesLedgerWiseTargetResource {

	private final Logger log = LoggerFactory.getLogger(SalesLedgerWiseTargetResource.class);

	@Inject
	private SalesLedgerRepository salesLedgerRepository;

	@Inject
	private SalesLedgerWiseTargetService salesLedgerWiseTargetService;

	@Inject
	private SalesLedgerWiseTargetRepository salesLedgerWiseTargetRepository;

	@Timed
	@RequestMapping(value = "/sales-ledger-wise-monthly-targets", method = RequestMethod.GET)
	public String setMonthlySalesTargets(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of  set Sales Ledger Wise Targets");

		model.addAttribute("salesLedgers", convertSalesLedgerToSalesLedgerDtoList(
				salesLedgerRepository.findAllCompanyAndSalesLedgerActivated(true)));
		return "company/set-sales-ledger-wise-monthly-target";
	}

	@RequestMapping(value = "/sales-ledger-wise-monthly-targets/monthly-sales-ledger-wise-targets", method = RequestMethod.GET)
	public @ResponseBody List<SalesLedgerWiseMonthlyTargetDTO> monthlySalesTargets(@RequestParam String monthAndYear) {
		log.debug("Web request to get monthly  Sales Ledger Wise Targets");

		// List<EmployeeProfileDTO> employees =
		// employeeProfileService.findAllByCompanyAndDeactivatedEmployeeProfile(true);\

		List<SalesLedgerDTO> salesLedgers = convertSalesLedgerToSalesLedgerDtoList(
				salesLedgerRepository.findAllCompanyAndSalesLedgerActivated(true));

		if (salesLedgers.size() > 0) {

			String[] monthAndYearArray = monthAndYear.split("/");
			int month = Integer.valueOf(monthAndYearArray[0]);
			int year = Integer.valueOf(monthAndYearArray[1]);
			YearMonth yearMonth = YearMonth.of(year, month);
			LocalDate firstDateMonth = yearMonth.atDay(1);
			LocalDate lastDateMonth = yearMonth.atEndOfMonth();
			List<SalesLedgerWiseMonthlyTargetDTO> monthlyTargetDTOs = new ArrayList<>();

			for (SalesLedgerDTO salesLedger : salesLedgers) {
				SalesLedgerWiseMonthlyTargetDTO monthlyTargetDTO = new SalesLedgerWiseMonthlyTargetDTO();
				monthlyTargetDTO.setSalesLedgerPid(salesLedger.getPid());
				monthlyTargetDTO.setSalesLedgerName(salesLedger.getName());

				List<SalesLedgerWiseTarget> salesLedgerWiseTargets = salesLedgerWiseTargetRepository
						.findBySalesLedgerPidAndFromDateGreaterThanEqualAndToDateLessThanEqual(salesLedger.getPid(),
								firstDateMonth, lastDateMonth);
				if (salesLedgerWiseTargets.size() > 0) {
					// monthlyTargetDTO.setTarget(salesTargetGroupUserTargets.get(0).getTargetNumber());
					// monthlyTargetDTO.setUserActivityTragetPid(activityUserTargets.get(0).getPid());
					double totalAmount = 0;
					double totalVolume = 0;
					for (SalesLedgerWiseTarget salesLedgerWiseTarget : salesLedgerWiseTargets) {
						totalAmount = totalAmount + salesLedgerWiseTarget.getAmount();
						monthlyTargetDTO.setAmount(totalAmount);
						monthlyTargetDTO.setSalesLedgerWiseTargetPid(salesLedgerWiseTargets.get(0).getPid());
					}
				} else {
					monthlyTargetDTO.setAmount(0);
				}
				monthlyTargetDTOs.add(monthlyTargetDTO);
			}
			return monthlyTargetDTOs;
		}
		return null;
	}

	@RequestMapping(value = "/sales-ledger-wise-monthly-targets/monthly-sales-ledger-wise-targets", method = RequestMethod.POST)
	public @ResponseBody SalesLedgerWiseMonthlyTargetDTO saveMonthlyActivityTargets(
			@RequestBody SalesLedgerWiseMonthlyTargetDTO monthlyTargetDTO) {
		log.debug("Web request to save monthly Sales Ledger Targets {}" + monthlyTargetDTO);

		Optional<SalesLedger> salesLedgerOp = salesLedgerRepository.findOneByPid(monthlyTargetDTO.getSalesLedgerPid());

		if (salesLedgerOp.isPresent()) {
			monthlyTargetDTO.setSalesLedgerPid(salesLedgerOp.get().getPid());
			if (monthlyTargetDTO.getSalesLedgerWiseTargetPid().equals("null")) {
				String[] monthAndYearArray = monthlyTargetDTO.getMonthAndYear().split("/");
				int month = Integer.valueOf(monthAndYearArray[0]);
				int year = Integer.valueOf(monthAndYearArray[1]);
				YearMonth yearMonth = YearMonth.of(year, month);

				LocalDate firstDateMonth = yearMonth.atDay(1);
				LocalDate lastDateMonth = yearMonth.atEndOfMonth();
				SalesLedgerWiseTargetDTO result = salesLedgerWiseTargetService.saveMonthlyTarget(monthlyTargetDTO,
						firstDateMonth, lastDateMonth);
				monthlyTargetDTO.setSalesLedgerWiseTargetPid(result.getPid());
			} else {
				salesLedgerWiseTargetRepository.findOneByPid(monthlyTargetDTO.getSalesLedgerWiseTargetPid())
						.ifPresent(activityUserTarget -> {
							activityUserTarget.setAmount(monthlyTargetDTO.getAmount());
							salesLedgerWiseTargetRepository.save(activityUserTarget);
						});
			}
		}
		return monthlyTargetDTO;
	}

	private List<SalesLedgerDTO> convertSalesLedgerToSalesLedgerDtoList(List<SalesLedger> salesLedgers) {
		List<SalesLedgerDTO> salesLedgerDTOs = new ArrayList<>();

		for (SalesLedger salesLedger : salesLedgers) {
			salesLedgerDTOs.add(salesLedgerToSalesLedgerDTO(salesLedger));
		}
		return salesLedgerDTOs;
	}

	private SalesLedgerDTO salesLedgerToSalesLedgerDTO(SalesLedger salesLedger) {
		SalesLedgerDTO salesLedgerDTO = new SalesLedgerDTO();
		salesLedgerDTO.setPid(salesLedger.getPid());
		salesLedgerDTO.setActivated(salesLedger.getActivated());
		salesLedgerDTO.setName(salesLedger.getName());
		salesLedgerDTO.setAlias(salesLedger.getAlias());
		salesLedgerDTO.setDescription(salesLedger.getDescription());

		return salesLedgerDTO;
	}

}