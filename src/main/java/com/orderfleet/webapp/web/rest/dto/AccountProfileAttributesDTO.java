package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.CompanyAttributes;

import java.util.List;
import java.util.Map;

public class AccountProfileAttributesDTO {
    private String accountProfilePid;
    private Map<String,String> questionAndAnswers;

    private String attributePid;

    private String answerPid;

    private String attributeName;

    private List<CompanyAttributes> companyAttributes;

    public String getAccountProfilePid() {
        return accountProfilePid;
    }

    public void setAccountProfilePid(String accountProfilePid) {
        this.accountProfilePid = accountProfilePid;
    }

    public Map<String, String> getQuestionAndAnswers() {
        return questionAndAnswers;
    }

    public void setQuestionAndAnswers(Map<String, String> questionAndAnswers) {
        this.questionAndAnswers = questionAndAnswers;
    }

    public String getAttributePid() {
        return attributePid;
    }

    public void setAttributePid(String attributePid) {
        this.attributePid = attributePid;
    }

    public String getAnswerPid() {
        return answerPid;
    }

    public void setAnswerPid(String answerPid) {
        this.answerPid = answerPid;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public List<CompanyAttributes> getCompanyAttributes() {
        return companyAttributes;
    }

    public void setCompanyAttributes(List<CompanyAttributes> companyAttributes) {
        this.companyAttributes = companyAttributes;
    }

    @Override
    public String toString() {
        return "AccountProfileAttributesDTO{" +
                "questionAndAnswers=" + questionAndAnswers +
                ", accountProfilePid='" + accountProfilePid + '\'' +
                '}';
    }
}
