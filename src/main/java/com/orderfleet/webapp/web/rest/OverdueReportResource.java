package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationHierarchy;
import com.orderfleet.webapp.domain.OverduePeriod;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.OverduePeriodRepository;
import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.web.rest.dto.OverdueReportAccountDTO;
import com.orderfleet.webapp.web.rest.dto.OverdueReportBillDTO;
import com.orderfleet.webapp.web.rest.dto.OverdueReportLocationDTO;
import com.orderfleet.webapp.web.rest.dto.OverdueReportLocationTreeDTO;
import com.orderfleet.webapp.web.rest.dto.OverdueReportPeriod;

/**
 * Web controller for managing OverdueReport.
 * 
 * @author Muhammed Riyas T
 * @since Mar 07, 2017
 */
@Controller
@RequestMapping("/web")
public class OverdueReportResource {

	private final Logger log = LoggerFactory.getLogger(OverdueReportResource.class);

	private final OverduePeriodRepository overduePeriodRepository;

	private final ReceivablePayableRepository receivablePayableRepository;

	private final EmployeeProfileLocationRepository employeeProfileLocationRepository;

	private final LocationHierarchyRepository locationHierarchyRepository;

	private final LocationAccountProfileRepository locationAccountProfileRepository;

	public OverdueReportResource(OverduePeriodRepository overduePeriodRepository,
			ReceivablePayableRepository receivablePayableRepository,
			EmployeeProfileLocationRepository employeeProfileLocationRepository,
			LocationHierarchyRepository locationHierarchyRepository,
			LocationAccountProfileRepository locationAccountProfileRepository) {
		super();
		this.overduePeriodRepository = overduePeriodRepository;
		this.receivablePayableRepository = receivablePayableRepository;
		this.employeeProfileLocationRepository = employeeProfileLocationRepository;
		this.locationHierarchyRepository = locationHierarchyRepository;
		this.locationAccountProfileRepository = locationAccountProfileRepository;
	}

	/**
	 * GET /overdue-report : get overdueReport.
	 *
	 * @param pageable
	 *            the pagination information
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/overdue-report", method = RequestMethod.GET)
	public String getOverdueReport(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of overdue report");
		return "company/overdueReport";
	}

	@Timed
	@GetMapping(value = "/overdue-report/filter", produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional(readOnly = true)
	@ResponseBody
	public ResponseEntity<List<OverdueReportLocationDTO>> filterOverdueReport() {
		log.debug("Web request to filter overdue");
		return new ResponseEntity<>(getFilterData(), HttpStatus.OK);
	}

	private List<OverdueReportLocationDTO> getFilterData() {
		List<Location> locations = employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();
		List<LocationHierarchy> locationsHierarchies = locationHierarchyRepository
				.findByLocationInAndActivatedTrue(locations);
		List<OverduePeriod> overduePeriods = overduePeriodRepository.findAllByCompanyId();
		List<OverdueReportLocationDTO> overdueReportLocationDTOs = new ArrayList<>();
		if (overduePeriods != null && overduePeriods.size() > 0) {
			List<OverdueReportLocationTreeDTO> overdueLocationTree = getLocationTree(locationsHierarchies);
			for (OverdueReportLocationTreeDTO overdueReportLocationTreeDTO : overdueLocationTree) {
				overdueReportLocationDTOs
						.addAll(createOverdueReportLocationDTO(overduePeriods, overdueReportLocationTreeDTO));
			}
		}
		return overdueReportLocationDTOs;
	}

	private List<OverdueReportLocationDTO> createOverdueReportLocationDTO(List<OverduePeriod> overduePeriods,
			OverdueReportLocationTreeDTO overdueReportLocationTreeDTO) {
		List<OverdueReportLocationDTO> overdueReportLocationDTOs = new ArrayList<>();
		OverdueReportLocationDTO odlDto = new OverdueReportLocationDTO();
		odlDto.setPid(overdueReportLocationTreeDTO.getPid());
		odlDto.setName(overdueReportLocationTreeDTO.getName());
		List<OverdueReportAccountDTO> overdueReportAccDtos = createOverdueReportAccountDTO(overduePeriods,
				overdueReportLocationTreeDTO.getPid());
		odlDto.setOverdueReportAccounts(overdueReportAccDtos);

		// periods
		List<OverdueReportPeriod> locOverdueReportPeriods = new ArrayList<>();
		for (OverduePeriod overduePeriod : overduePeriods) {
			OverdueReportPeriod reportPeriod = new OverdueReportPeriod();
			reportPeriod.setName(overduePeriod.getName());
			double sum = 0;
			for (OverdueReportAccountDTO odAccDto : overdueReportAccDtos) {
				sum += odAccDto.getOverdueReportPeriods().stream()
						.filter(p -> p.getName().equals(overduePeriod.getName()))
						.collect(Collectors.summingDouble(bp -> bp.getAmount()));
			}
			reportPeriod.setAmount(sum);
			locOverdueReportPeriods.add(reportPeriod);
		}
		odlDto.setOverdueReportPeriods(locOverdueReportPeriods);

		// recursive
		if (overdueReportLocationTreeDTO.getChildren() != null
				&& overdueReportLocationTreeDTO.getChildren().size() > 0) {
			List<OverdueReportLocationDTO> childrens = new ArrayList<>();
			for (OverdueReportLocationTreeDTO odrLocTreeDto : overdueReportLocationTreeDTO.getChildren()) {
				childrens.addAll(createOverdueReportLocationDTO(overduePeriods, odrLocTreeDto));
			}
			odlDto.setOverdueReportLocations(childrens);
			// periods sum
			for (OverdueReportPeriod overdueReportPeriod : odlDto.getOverdueReportPeriods()) {
				double sum = 0;
				for (OverdueReportLocationDTO overdueReportLocationDTO : odlDto.getOverdueReportLocations()) {
					// account-wise
					if (overdueReportLocationDTO.getOverdueReportAccounts() != null
							&& overdueReportLocationDTO.getOverdueReportAccounts().size() > 0) {
						for (OverdueReportAccountDTO odAccDto : overdueReportLocationDTO.getOverdueReportAccounts()) {
							sum += odAccDto.getOverdueReportPeriods().stream()
									.filter(p -> p.getName().equals(overdueReportPeriod.getName()))
									.collect(Collectors.summingDouble(bp -> bp.getAmount()));
						}
					}
					// location-wise
					if (overdueReportLocationDTO.getOverdueReportLocations() != null
							&& overdueReportLocationDTO.getOverdueReportLocations().size() > 0) {
						for (OverdueReportLocationDTO overdueLoc : overdueReportLocationDTO
								.getOverdueReportLocations()) {
							sum += overdueLoc.getOverdueReportPeriods().stream()
									.filter(p -> p.getName().equals(overdueReportPeriod.getName()))
									.collect(Collectors.summingDouble(bp -> bp.getAmount()));
						}
					}
				}
				overdueReportPeriod.setAmount(overdueReportPeriod.getAmount() + sum);
			}
		}
		overdueReportLocationDTOs.add(odlDto);
		return overdueReportLocationDTOs;
	}

	private List<OverdueReportAccountDTO> createOverdueReportAccountDTO(List<OverduePeriod> overduePeriods,
			String locationPid) {
		List<OverdueReportAccountDTO> overdueReportAccDtos = new ArrayList<>();
		List<AccountProfile> accountProfiles = locationAccountProfileRepository
				.findAccountProfileByLocationPid(locationPid);
		for (AccountProfile accountProfile : accountProfiles) {
			OverdueReportAccountDTO overdueReportAccountDTO = new OverdueReportAccountDTO();
			overdueReportAccountDTO.setPid(accountProfile.getPid());
			overdueReportAccountDTO.setName(accountProfile.getName());
			List<ReceivablePayable> receivables = receivablePayableRepository
					.findAllByAccountProfilePidAndReceivablePayableType(accountProfile.getPid(),
							ReceivablePayableType.Receivable);
			List<OverdueReportPeriod> accOverdueReportPeriods = new ArrayList<>();
			// periods
			for (OverduePeriod overduePeriod : overduePeriods) {
				double sum = receivables.stream()
						.filter(r -> overduePeriod.getOverdueFrom() <= r.getBillOverDue()
								&& overduePeriod.getOverdueTo() >= r.getBillOverDue())
						.collect(Collectors.summingDouble(rv -> rv.getReferenceDocumentBalanceAmount()));
				accOverdueReportPeriods.add(new OverdueReportPeriod(overduePeriod.getName(), sum));
			}
			// bill
			List<OverdueReportBillDTO> overdueReportBills = receivables.stream()
					.map(r -> new OverdueReportBillDTO(r.getPid(), r.getReferenceDocumentNumber(),
							r.getReferenceDocumentDate(), r.getReferenceDocumentBalanceAmount()))
					.collect(Collectors.toList());
			overdueReportAccountDTO.setOverdueReportBills(overdueReportBills);
			overdueReportAccountDTO.setOverdueReportPeriods(accOverdueReportPeriods);
			overdueReportAccDtos.add(overdueReportAccountDTO);
		}
		return overdueReportAccDtos;
	}

	private List<OverdueReportLocationTreeDTO> getLocationTree(List<LocationHierarchy> locationHierarchies) {
		// Arrange String corresponds to the Id
		Map<String, OverdueReportLocationTreeDTO> hm = new HashMap<>();
		locationHierarchies.parallelStream().forEach(lh -> {
			// ----- Child -----
			OverdueReportLocationTreeDTO mmdChild;
			if (hm.containsKey(lh.getLocation().getPid())) {
				mmdChild = hm.get(lh.getLocation().getPid());
			} else {
				mmdChild = new OverdueReportLocationTreeDTO();
				hm.put(lh.getLocation().getPid(), mmdChild);
			}
			mmdChild.setPid(lh.getLocation().getPid());
			mmdChild.setName(lh.getLocation().getName());
			if (lh.getParent() != null) {
				mmdChild.setParentPid(lh.getParent().getPid());
			} else {
				mmdChild.setParentPid(null);
			}
			// ------ Parent ----
			if (lh.getParent() != null) {
				OverdueReportLocationTreeDTO mmdParent;
				if (hm.containsKey(lh.getParent().getPid())) {
					mmdParent = hm.get(lh.getParent().getPid());
				} else {
					mmdParent = new OverdueReportLocationTreeDTO();
					mmdParent.setPid(lh.getParent().getPid());
					mmdParent.setParentPid(lh.getParent().getPid());
					hm.put(lh.getParent().getPid(), mmdParent);
				}
				mmdParent.addChildrenItem(mmdChild);
			}
		});
		// Get the root
		List<OverdueReportLocationTreeDTO> roots = new ArrayList<>();
		for (OverdueReportLocationTreeDTO mmd : hm.values()) {
			if (mmd.getParentPid() == null)
				roots.add(mmd);
		}
		return roots;
	}

}
