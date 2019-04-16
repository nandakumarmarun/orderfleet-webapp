package com.orderfleet.webapp.web.rest;


import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.UserOnPremise;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.UserOnPremiseRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class UserOnPremiseImportResource {

	private final Logger log = LoggerFactory.getLogger(UserOnPremiseImportResource.class);
	
	@Inject
	private CompanyService companyService;
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private UserOnPremiseRepository userOnPremiseRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@RequestMapping(value = "/userOnPremiseImport", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	@Secured({ AuthoritiesConstants.SITE_ADMIN})
	public String getAllCompaniesUserOnPremiseImport(Model model) throws URISyntaxException{
		log.debug("Web request to get a page of Companies");
		model.addAttribute("companies", companyRepository.findAllByOnPremiseTrue());
		return "site_admin/user-on-premise-import";
	}
	
	@RequestMapping(value = "/userOnPremiseImport/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<CompanyViewDTO> getCompanyUrl(@PathVariable String pid) {
		log.debug("Web request to get Company by pid : {}", pid);
		Optional<CompanyViewDTO> company = companyService.findOneByPid(pid);
		return new ResponseEntity<>(company.get(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/userOnPremiseImport/load-user/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<String>> getUsersForCompany(@PathVariable String pid) {
		log.debug("Web request to get Company Users by pid : {}", pid);
		
		 List<String> userList = userOnPremiseRepository.findAllfullNamesByCompanyPid(pid);
		return new ResponseEntity<>(userList, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/userOnPremiseImport/import-users/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UserOnPremise> importUsersOnpremise(@PathVariable String pid) {
		log.debug("Web request to get Company by pid : {}", pid);
		String apiBaseUrl = companyRepository.findApiUrlByCompanyPid(pid);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		ResponseEntity<UserDTO[]> response = restTemplate.getForEntity(apiBaseUrl + "/api/onpremise-users/" + pid, UserDTO[].class);
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			UserDTO[] userArrays = response.getBody();
			if (userArrays.length > 0) {
				List<UserDTO> userDtos = Arrays.asList(userArrays);
				List<String> userLogins = userDtos.stream().map(user -> user.getLogin()).collect(Collectors.toList());
				List<String> usersExisting = userRepository.findAllUserByLogin(userLogins);
				if(usersExisting.size()==0){
					//insert into db
					userOnPremiseRepository.save(convertUserDTOsToUserOnPremise(userDtos));
				}else{
					return ResponseEntity.badRequest()
							.headers(HeaderUtil.createFailureAlert("company", "Users Already Exist : "+usersExisting, "Users Exist"))
							.body(null);
				}
			}
		}
		return new ResponseEntity<UserOnPremise>(HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/userOnPremiseImport/companyUpdate", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<CompanyViewDTO> updateCompanyApi(
			@Valid @RequestBody CompanyViewDTO companyDTO) {
		log.debug("Web request to update Company with API : {}", companyDTO);
		if (companyDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("company", "idNotexists", "Company must have an ID"))
					.body(null);
		}
		Optional<CompanyViewDTO> company = companyService.findOneByPid(companyDTO.getPid());
		if (company.isPresent() && (!company.get().getPid().equals(companyDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("company", "nameexists", "Company already in use"))
					.body(null);
		}
		companyRepository.updateCompanyUrlByPid(companyDTO.getApiUrl(), companyDTO.getPid());
		/*company.get().setApiUrl(companyDTO.getApiUrl());
		CompanyViewDTO result = companyService.update(company.get());
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("company", "idNotexists", "Invalid Company ID")).body(null);
		}*/
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("company", companyDTO.getPid().toString())).body(companyDTO);
	}
	
	
	private List<UserOnPremise> convertUserDTOsToUserOnPremise(List<UserDTO> userDtos) {
		List<UserOnPremise> userPremiseList = new ArrayList<>();
		for(UserDTO userDto : userDtos) {
			UserOnPremise userOnPremise = new UserOnPremise();
			userOnPremise.setCompanyName(userDto.getCompanyName());
			userOnPremise.setCompanyPid(userDto.getCompanyPid());
			userOnPremise.setCreatedDate(Instant.now());
			userOnPremise.setFullName(userDto.getFirstName()+" "+userDto.getLastName());
			userOnPremise.setLogin(userDto.getLogin());
			userPremiseList.add(userOnPremise);
		}
		return userPremiseList;
	}
}
