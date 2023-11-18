package com.orderfleet.webapp.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "tbl_company_attributes")
public class CompanyAttributes implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "seq_company_attributes_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "seq_company_attributes_id") })
    @GeneratedValue(generator = "seq_company_attributes_id_GEN")
    @Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_company_attributes_id')")
    private Long id;

    @NotNull
    @ManyToOne
    private Company company;

    @NotNull
    @ManyToOne
    private Attributes attributes;
    @Column(name="sort_order")
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

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Long getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Long sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString() {
        return "CompanyAttributes{" +
                "id=" + id +
                ", company=" + company +
                ", attributes=" + attributes +
                ", sortOrder=" + sortOrder +
                '}';
    }
}

