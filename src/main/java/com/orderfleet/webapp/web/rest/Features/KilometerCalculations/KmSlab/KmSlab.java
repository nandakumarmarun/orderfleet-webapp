package com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlab;

import com.orderfleet.webapp.domain.Company;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tbl_km_slab")
public class KmSlab {

    @Id
    @GenericGenerator(name = "seq_tbl_km_slab_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "seq_tbl_km_slab_id") })
    @GeneratedValue(generator = "seq_tbl_km_slab_id_GEN")
    @Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_tbl_km_slab_id')")
    private Long id;

    @Column(name = "pid")
    private String pid;

    @Column(name = "name")
    private String slabName;

    @Column(name = "minKm", columnDefinition = "double precision DEFAULT 0" )
    private double minKm;

    @Column(name = "maxKm", columnDefinition = "double precision DEFAULT 0" )
    private double maxKm;

    @Column(name = "amount", columnDefinition = "double precision DEFAULT 0" )
    private double Amount;


    public KmSlab(Long id, String pid, String slabName, double minKm, double maxKm, double amount, Company company) {
        this.id = id;
        this.pid = pid;
        this.slabName = slabName;
        this.minKm = minKm;
        this.maxKm = maxKm;
        Amount = amount;
        this.company = company;
    }

    @ManyToOne
    @NotNull
    private Company company;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public KmSlab() {}

    public String getSlabName() {
        return slabName;
    }

    public void setSlabName(String slabName) {
        this.slabName = slabName;
    }

    public double getMinKm() {
        return minKm;
    }

    public void setMinKm(double minKm) {
        this.minKm = minKm;
    }

    public double getMaxKm() {
        return maxKm;
    }
    public void setMaxKm(double maxKm) {
        this.maxKm = maxKm;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "'KmSlab' :{" +
                "'id'" + id +
                ", 'slabName':'" + slabName + '\'' +
                ", 'minKm':" + minKm +
                ", 'maxKm':" + maxKm +
                ", 'Amount':" + Amount +
                ", 'company':" + company +
                '}';
    }
}
