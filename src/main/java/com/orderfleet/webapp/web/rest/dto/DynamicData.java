package com.orderfleet.webapp.web.rest.dto;

public class DynamicData {
    private String documentName;
    private String formElementName;
    private String value;

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getFormElementName() {
        return formElementName;
    }

    public void setFormElementName(String formElementName) {
        this.formElementName = formElementName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
