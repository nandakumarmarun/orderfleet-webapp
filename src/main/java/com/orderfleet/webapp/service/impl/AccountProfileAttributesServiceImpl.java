package com.orderfleet.webapp.service.impl;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountProfileAttributes;
import com.orderfleet.webapp.domain.Attributes;
import com.orderfleet.webapp.domain.CompanyAttributes;
import com.orderfleet.webapp.repository.AccountProfileAttributesRepository;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AttributesRepository;
import com.orderfleet.webapp.repository.CompanyAttributesRepository;

import com.orderfleet.webapp.service.AccountProfileAttributesService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileAttributesDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountProfileAttributesServiceImpl implements AccountProfileAttributesService {

    private final Logger log = LoggerFactory.getLogger(AccountProfileAttributesServiceImpl.class);


    @Autowired
    private AccountProfileRepository accountProfileRepository;

    @Autowired
    private AccountProfileAttributesRepository accountProfileAttributesRepository;

    @Autowired
    private AttributesRepository attributesRepository;
    @Inject
    private CompanyAttributesRepository companyAttributesRepository;


    public List<AccountProfileAttributesDTO> getAccountProfileAttributes(AccountProfileAttributesDTO accountProfileAttributesDTO) {
        String accountProfilePid = accountProfileAttributesDTO.getAccountProfilePid();
        List<AccountProfileAttributesDTO> savedAccountProfileAttributes = new ArrayList<>();
        AccountProfileAttributesDTO savedDTO = new AccountProfileAttributesDTO();
        Map<String, String> questionPidAnswerMap = accountProfileAttributesDTO.getQuestionAndAnswers();

        Set<String> qstnPid = questionPidAnswerMap.keySet();
        List<Attributes> attributes = attributesRepository.findByAttributePidIn(qstnPid);
        List<CompanyAttributes> companyAttributes = companyAttributesRepository.findCompanyAttributesByAttributesPidIn(qstnPid);
        if (accountProfilePid != null) {
            // Fetch AccountProfile based on the accountProfilePid
            Optional<AccountProfile> optionalAccountProfile = accountProfileRepository.findOneByPid(accountProfilePid);

            if (optionalAccountProfile.isPresent()) {
                AccountProfile accountProfile = optionalAccountProfile.get();

                savedDTO.setAccountProfilePid(accountProfilePid);
                savedDTO.setQuestionAndAnswers(questionPidAnswerMap);
                savedAccountProfileAttributes.add(savedDTO);


                for (Map.Entry<String, String> entry : questionPidAnswerMap.entrySet()) {
                    String questionPid = entry.getKey();
                    String answer = entry.getValue();
                    System.out.println("Question Pid: " + questionPid);
                    System.out.println("Answer: " + answer);
                    if (questionPid != null && answer != null) {
                        for (Attributes attribute : attributes) {
                            if (attribute.getPid().equals(questionPid)) {
                                for (CompanyAttributes compAttr : companyAttributes) {
                                    if (compAttr.getAttributes().getPid().equals(questionPid)) {
                                        AccountProfileAttributes accountProfileAttributes = new AccountProfileAttributes();
                                        accountProfileAttributes.setAccountProfile(accountProfile);
                                        accountProfileAttributes.setCompany(accountProfile.getCompany());
                                        accountProfileAttributes.setAttributesPid(questionPid);
                                        accountProfileAttributes.setAttributesName(attribute.getQuestions());
                                        accountProfileAttributes.setAnswers(answer);
                                        accountProfileAttributes.setSortOrder(compAttr.getSortOrder());
                                        accountProfileAttributesRepository.save(accountProfileAttributes);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return savedAccountProfileAttributes;
    }

    @Override
    public List<AccountProfileAttributesDTO> getAccountProfileAttributesByAccountProfilePid(String accountProfilePid) {

        // Retrieve AccountProfileAttributes for the given accountProfilePid
        List<AccountProfileAttributes> accountProfileAttributesList =
                accountProfileAttributesRepository.findAccountProfileAttributesByAccountProfilePid(accountProfilePid);
        List<String> QuestionPid = new ArrayList<>();
        if(!accountProfileAttributesList.isEmpty()) {
            QuestionPid = accountProfileAttributesList.stream().map(qn -> qn.getAttributesPid()).collect(Collectors.toList());

            List<Attributes> attributesList = attributesRepository.findByAttributePidIn(QuestionPid);
            List<AccountProfileAttributesDTO> accountProfileAttributesDTOList = new ArrayList<>();
            for (AccountProfileAttributes attributes : accountProfileAttributesList) {
                for (Attributes attr : attributesList) {
                    if (attributes.getAttributesPid().equals(attr.getPid())) {
                        AccountProfileAttributesDTO accountProfileAttributesDTO = new AccountProfileAttributesDTO();
                        accountProfileAttributesDTO.setAttributePid(attr.getQuestions());
                        accountProfileAttributesDTO.setAnswerPid(attributes.getAnswers());
                        accountProfileAttributesDTOList.add(accountProfileAttributesDTO);
                    }
                }

            }
            return accountProfileAttributesDTOList;
        }
        return null;
    }



}





