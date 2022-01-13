package com.orderfleet.webapp.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.google.common.util.concurrent.Service.State;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.CountryC;
import com.orderfleet.webapp.domain.DistrictC;
import com.orderfleet.webapp.domain.StateC;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CounrtyCRepository;
import com.orderfleet.webapp.repository.DistrictCRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.StateCRepository;
import com.orderfleet.webapp.repository.StateRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.CountryCDTO;
import com.orderfleet.webapp.web.rest.dto.DistrictCDTO;
import com.orderfleet.webapp.web.rest.dto.StateCDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class AccountProfile_CSD_ManagementResource {
	private final Logger log = LoggerFactory.getLogger(AccountProfileResource.class);
	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private CounrtyCRepository countryCRepository;

	@Inject
	private DistrictCRepository districtCRepository;

	@Inject
	private StateCRepository stateCRepository;
	

	@RequestMapping(value = "/accountProfile_CSD_Management/loadStates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<StateCDTO>> loadStates(@RequestParam("countryId") String countryId) {
		log.debug("Web request to load states by country*  ");

		List<StateC> statesList = stateCRepository.findAllByCountryId(Long.valueOf(countryId));
		List<StateCDTO> states = converttostatedto(statesList);
		return new ResponseEntity<>(states, HttpStatus.OK);

	}

	@RequestMapping(value = "/accountProfile_CSD_Management/loadDistricts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DistrictCDTO>> loadDistricts(@RequestParam("stateId") String stateId) {
		log.debug("Web request to load districts by states*  ");

		List<DistrictC> districtsList = districtCRepository.findAllByStateId(Long.valueOf(stateId));
		List<DistrictCDTO> districts = converttodistrictdto(districtsList);
		return new ResponseEntity<>(districts, HttpStatus.OK);

	}

	@RequestMapping(value="/accountProfile_CSD_Management/loadCountries",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CountryCDTO>> loadCountries()
    {    log.debug("web request to load Countries");
		List<CountryC> countriesList = countryCRepository.findAllCountries();
		List<CountryCDTO> countries = converttodto(countriesList);
		
		return new ResponseEntity<>(countries,HttpStatus.OK);
		
    }
	
	@RequestMapping(value="/accountProfile_CSD_Management/loadStateList",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<StateCDTO>> loadStates()
    {   log.debug("Web request to load states");
		List<StateC> statesList = stateCRepository.findAll();
		List<StateCDTO> states = converttostatedto(statesList);
		return new ResponseEntity<>(states, HttpStatus.OK);

    }
	
	@RequestMapping(value = "/accountProfile_CSD_Management/loadDistrictList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DistrictCDTO>> loadDistricts() {
		log.debug("Web request to load districts   ");

		List<DistrictC> districtsList = districtCRepository.findAll();
		List<DistrictCDTO> districts = converttodistrictdto(districtsList);
		return new ResponseEntity<>(districts, HttpStatus.OK);

	}
	@RequestMapping(value = "/accountProfile_CSD_Management", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountProfiles(Model model) {

		model.addAttribute("AccountProfiles", accountProfileService.findAllByCompanyAndActivated(true));

//		List<CountryC> countriesList = countryCRepository.findAllCountries();
//		List<CountryCDTO> countries = converttodto(countriesList);
//		model.addAttribute("countries", countries);
//
//		List<StateC> statesList = stateCRepository.findAllStates();
//		List<StateCDTO> states = converttostatedto(statesList);
//
//		states = states.stream().filter((s) -> s.getCountry_id() == (101)).collect(Collectors.toList());
//
//		model.addAttribute("states", states);
//		List<Long> statecdto = new ArrayList<>();
//
//		List<DistrictC> districtsList = districtCRepository.findAllDistricts();
//		List<DistrictCDTO> districts = converttodistrictdto(districtsList);
//        List<DistrictCDTO> district = new ArrayList<>();
//		for (Long stc : statecdto) {
//			for(DistrictCDTO dst:districts)
//			{
//				if(dst.getState_id()!=null && dst.getState_id().equals(stc))
//				{
//					district.add(dst);
//				}
//			}
//			}
//		districts= districts.stream().filter((s) -> s.getState_id() == (19)).collect(Collectors.toList());
//	model.addAttribute("districts",districts);

	return"company/accountProfile_CSD_Management";

	}

	private List<DistrictCDTO> converttodistrictdto(List<DistrictC> districtsList) {
		List<DistrictCDTO> districtdtos = new ArrayList<>();

		for (DistrictC district : districtsList) {
			DistrictCDTO districtcdto = new DistrictCDTO(district);
			districtdtos.add(districtcdto);

		}

		return districtdtos;
	}

	private List<StateCDTO> converttostatedto(List<StateC> statesList) {
		List<StateCDTO> statecdtos = new ArrayList<>();

		for (StateC state : statesList) {
			StateCDTO statecdto = new StateCDTO(state);
			statecdtos.add(statecdto);

		}
		return statecdtos;
	}

	private List<CountryCDTO> converttodto(List<CountryC> countriesList) {
		List<CountryCDTO> countrycdtos = new ArrayList<>();

		for (CountryC country : countriesList) {
			CountryCDTO counrtycdto = new CountryCDTO(country);
			countrycdtos.add(counrtycdto);

		}
		return countrycdtos;
	}

	@RequestMapping(value = "/accountProfile_CSD_Management/save-csd", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveCSD(@RequestParam String countryId, @RequestParam String stateId,
			@RequestParam String districtId, @RequestParam String accountPid) {

		log.debug("Rest request to save AccountProfiles with CSD:{}", accountPid);

		AccountProfile accountProfile = accountProfileRepository.findOneByPid(accountPid).get();
		accountProfile.setCountryc(countryCRepository.findOne(Long.valueOf(countryId)));

		accountProfile.setStatec(stateCRepository.findOne(Long.valueOf(stateId)));

		accountProfile.setDistrictc(districtCRepository.findOne(Long.valueOf(districtId)));
		log.info("List of account Profile***********************" + accountProfile.toString());
		accountProfile = accountProfileRepository.save(accountProfile);

		return new ResponseEntity<>(HttpStatus.OK);

	}
}
//	
