package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class slabViewDTO {

    private  String legalName;

    private String companyPid;

    private List<slabDTO> slabDTos;

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getCompanyPid() {
        return companyPid;
    }

    public void setCompanyPid(String companyPid) {
        this.companyPid = companyPid;
    }

    public List<slabDTO> getSlabDTos() {
        return slabDTos;
    }

    public void setSlabDTos(List<slabDTO> slabDTos) {
        this.slabDTos = slabDTos;
    }

    @Override
    public String toString() {
        return "slabViewDTO{" +
                "legalName='" + legalName + '\'' +
                ", companyPid='" + companyPid + '\'' +
                ", slabDTos=" + slabDTos +
                '}';
    }
}
