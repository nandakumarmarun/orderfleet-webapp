package com.orderfleet.webapp.web.rest.api.dto;

import com.orderfleet.webapp.web.rest.dto.FilledFormDetailDTO;

import java.util.List;

public class LeadsTrackerDTO {
    private String accountPid;
    private String documentPid;
    private String dynamicDocHeaderPid;
    private String createdDate;
    private List<FilledFormDetailDTO> filledFormDetailDTO;

    public LeadsTrackerDTO() {
    }

    public LeadsTrackerDTO(String accountPid, String documentPid, String dynamicDocHeaderPid, String createdDate, List<FilledFormDetailDTO> filledFormDetailDTO) {
        this.accountPid = accountPid;
        this.documentPid = documentPid;
        this.dynamicDocHeaderPid = dynamicDocHeaderPid;
        this.createdDate = createdDate;
        this.filledFormDetailDTO = filledFormDetailDTO;
    }

    public String getAccountPid() {
        return accountPid;
    }

    public void setAccountPid(String accountPid) {
        this.accountPid = accountPid;
    }

    public String getDocumentPid() {
        return documentPid;
    }

    public void setDocumentPid(String documentPid) {
        this.documentPid = documentPid;
    }

    public String getDynamicDocHeaderPid() {
        return dynamicDocHeaderPid;
    }

    public void setDynamicDocHeaderPid(String dynamicDocHeaderPid) {
        this.dynamicDocHeaderPid = dynamicDocHeaderPid;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public List<FilledFormDetailDTO> getFilledFormDetailDTO() {
        return filledFormDetailDTO;
    }

    public void setFilledFormDetailDTO(List<FilledFormDetailDTO> filledFormDetailDTO) {
        this.filledFormDetailDTO = filledFormDetailDTO;
    }

    @Override
    public String toString() {
        return "LeadsTrackerDTO{" +
                "accountPid='" + accountPid + '\'' +
                ", documentPid='" + documentPid + '\'' +
                ", dynamicDocHeaderPid='" + dynamicDocHeaderPid + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", filledFormDetailDTO=" + filledFormDetailDTO +
                '}';
    }
}
