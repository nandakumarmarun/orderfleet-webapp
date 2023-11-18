package com.orderfleet.webapp.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "tbl_account_profile_attributes")
public class AccountProfileAttributes implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    @Id
    @GenericGenerator(name = "seq_account_profile_attributes_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "seq_account_profile_attributes_id") })
    @GeneratedValue(generator = "seq_account_profile_attributes_id_GEN")
    @Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_account_profile_attributes_id')")
    private Long id;

    @ManyToOne
    private Company company;

    @ManyToOne
    private AccountProfile accountProfile ;

    @Column(name = "attributes_pid", length = 235)
    private String attributesPid;

    @Column(name = "attributes_name", length = 235)
    private String attributesName;


    @Column(name = "answers", length = 235)
    private String answers;

    @Column(name = "sort_order", length = 235)
    private Long sortOrder;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public AccountProfile getAccountProfile() {
        return accountProfile;
    }

    public void setAccountProfile(AccountProfile accountProfile) {
        this.accountProfile = accountProfile;
    }

    public String getAttributesPid() {
        return attributesPid;
    }

    public void setAttributesPid(String attributesPid) {
        this.attributesPid = attributesPid;
    }

    public String getAttributesName() {
        return attributesName;
    }

    public void setAttributesName(String attributesName) {
        this.attributesName = attributesName;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public Long getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Long sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString() {
        return "AccountProfileAttributes{" +
                "id=" + id +
                ", company=" + company +
                ", accountProfile=" + accountProfile +
                ", attributesPid='" + attributesPid + '\'' +
                ", answers='" + answers + '\'' +
                '}';
    }

}
