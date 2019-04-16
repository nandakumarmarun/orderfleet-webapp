package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfileDynamicDocumentAccountprofile;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.repository.AccountProfileDynamicDocumentAccountprofileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.FormElementRepository;
import com.orderfleet.webapp.repository.FormRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileDynamicDocumentAccountprofileService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDynamicDocumentAccountprofileDTO;

/**
 * Service Implementation for managing
 * AccountProfileDynamicDocumentAccountprofile.
 *
 * @author Sarath
 * @since Feb 5, 2018
 *
 */
@Service
@Transactional
public class AccountProfileDynamicDocumentAccountprofileServiceImpl
		implements AccountProfileDynamicDocumentAccountprofileService {

	private final Logger log = LoggerFactory.getLogger(AccountProfileDynamicDocumentAccountprofileServiceImpl.class);
	@Inject
	private AccountProfileDynamicDocumentAccountprofileRepository accountProfileDynamicDocumentAccountprofileRepository;

	@Inject
	private FormElementRepository formElementRepository;

	@Inject
	private FormRepository formRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public List<AccountProfileDynamicDocumentAccountprofileDTO> save(
			List<AccountProfileDynamicDocumentAccountprofileDTO> accountProfileDynamicDocumentAccountprofileDTOs) {
		log.debug("Request to save accountProfileDynamicDocumentAccountprofile : {}",
				accountProfileDynamicDocumentAccountprofileDTOs.size());
		List<AccountProfileDynamicDocumentAccountprofileDTO> resullt = new ArrayList<>();

		if (accountProfileDynamicDocumentAccountprofileDTOs.size() > 0) {
			List<AccountProfileDynamicDocumentAccountprofile> list = accountProfileDynamicDocumentAccountprofileRepository
					.findAllByDocumentPidAndFormPid(
							accountProfileDynamicDocumentAccountprofileDTOs.get(0).getDocumentPid(),
							accountProfileDynamicDocumentAccountprofileDTOs.get(0).getFormPid());
			accountProfileDynamicDocumentAccountprofileRepository.delete(list);

			for (AccountProfileDynamicDocumentAccountprofileDTO accountProfileDynamicDocumentAccountprofileDTO : accountProfileDynamicDocumentAccountprofileDTOs) {
				AccountProfileDynamicDocumentAccountprofileDTO documentAccountprofileDTO = new AccountProfileDynamicDocumentAccountprofileDTO();
				Optional<FormElement> opFormElement = formElementRepository
						.findOneByPid(accountProfileDynamicDocumentAccountprofileDTO.getFormElementPid());

				Optional<Form> opForm = formRepository
						.findOneByPid(accountProfileDynamicDocumentAccountprofileDTO.getFormPid());

				Optional<Document> opDocument = documentRepository
						.findOneByPid(accountProfileDynamicDocumentAccountprofileDTO.getDocumentPid());

				if (opFormElement.isPresent() && opForm.isPresent() && opDocument.isPresent()) {
					AccountProfileDynamicDocumentAccountprofile accountProfileDynamicDocumentAccountprofile = new AccountProfileDynamicDocumentAccountprofile();
					accountProfileDynamicDocumentAccountprofile.setFormElement(opFormElement.get());
					accountProfileDynamicDocumentAccountprofile.setForm(opForm.get());
					accountProfileDynamicDocumentAccountprofile.setDocument(opDocument.get());
					accountProfileDynamicDocumentAccountprofile.setAccountProfleField(
							accountProfileDynamicDocumentAccountprofileDTO.getAccountProfleField());
					accountProfileDynamicDocumentAccountprofile
							.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
					accountProfileDynamicDocumentAccountprofile = accountProfileDynamicDocumentAccountprofileRepository
							.save(accountProfileDynamicDocumentAccountprofile);
					documentAccountprofileDTO = new AccountProfileDynamicDocumentAccountprofileDTO(
							accountProfileDynamicDocumentAccountprofile);
					resullt.add(documentAccountprofileDTO);
				}
			}
		}
		return resullt;
	}

	@Override
	public AccountProfileDynamicDocumentAccountprofileDTO update(
			AccountProfileDynamicDocumentAccountprofileDTO accountProfileDynamicDocumentAccountprofileDTO) {
		log.debug("Request to Update accountProfileDynamicDocumentAccountprofile : {}",
				accountProfileDynamicDocumentAccountprofileDTO);

		return accountProfileDynamicDocumentAccountprofileDTO;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountProfileDynamicDocumentAccountprofileDTO> findAllByCompany() {
		log.debug("Request to get all by company accountProfileDynamicDocumentAccountprofile : {}");
		List<AccountProfileDynamicDocumentAccountprofile> accountProfileDynamicDocumentAccountprofiles = accountProfileDynamicDocumentAccountprofileRepository
				.findAllByCompany();
		List<AccountProfileDynamicDocumentAccountprofileDTO> result = accountProfileDynamicDocumentAccountprofiles
				.stream().map(AccountProfileDynamicDocumentAccountprofileDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountProfileDynamicDocumentAccountprofileDTO> findAllByCompanyMapped() {
		log.debug("Request to get all by company accountProfileDynamicDocumentAccountprofile : {}");
		List<AccountProfileDynamicDocumentAccountprofile> accountProfileDynamicDocumentAccountprofiles = accountProfileDynamicDocumentAccountprofileRepository
				.findAllByCompany();
		Set<AccountProfileDynamicDocumentAccountprofileDTO> result = accountProfileDynamicDocumentAccountprofiles
				.stream().map(AccountProfileDynamicDocumentAccountprofileDTO::new).collect(Collectors.toSet());
		return new ArrayList<>(result);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountProfileDynamicDocumentAccountprofileDTO> findAllByDocumentPidAndFormPid(String documentPid,
			String formPid) {
		log.debug("Request to Update accountProfileDynamicDocumentAccountprofile : {}");
		List<AccountProfileDynamicDocumentAccountprofile> accountProfileDynamicDocumentAccountprofiles = accountProfileDynamicDocumentAccountprofileRepository
				.findAllByDocumentPidAndFormPid(documentPid, formPid);
		List<AccountProfileDynamicDocumentAccountprofileDTO> result = accountProfileDynamicDocumentAccountprofiles
				.stream().map(AccountProfileDynamicDocumentAccountprofileDTO::new).collect(Collectors.toList());
		return result;
	}
}
