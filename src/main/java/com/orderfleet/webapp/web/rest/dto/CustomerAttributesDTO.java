package com.orderfleet.webapp.web.rest.dto;

public class CustomerAttributesDTO {

    private Long id;
    private String attributePid;

    private Long attributedId;
    private String question;
    private String companyPid;
    private String companyName;
    private Long sortOrder;
    private String type;
    private String customerDocumentPid;
    private String customerDocumentName;

    public String getCustomerDocumentPid() {
        return customerDocumentPid;
    }

    public void setCustomerDocumentPid(String customerDocumentPid) {
        this.customerDocumentPid = customerDocumentPid;
    }

    public String getCustomerDocumentName() {
        return customerDocumentName;
    }

    public void setCustomerDocumentName(String customerDocumentName) {
        this.customerDocumentName = customerDocumentName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAttributePid() {
        return attributePid;
    }

    public void setAttributePid(String attributePid) {
        this.attributePid = attributePid;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCompanyPid() {
        return companyPid;
    }

    public void setCompanyPid(String companyPid) {
        this.companyPid = companyPid;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Long sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getAttributedId() {
        return attributedId;
    }

    public void setAttributedId(Long attributedId) {
        this.attributedId = attributedId;
    }

    @Override
    public String toString() {
        return "CustomerAttributesDTO{" +
                "id=" + id +
                ", attributePid='" + attributePid + '\'' +
                ", question='" + question + '\'' +
                ", companyPid='" + companyPid + '\'' +
                ", companyName='" + companyName + '\'' +
                ", sortOrder=" + sortOrder +
                ", type='" + type + '\'' +
                '}';
    }
}
