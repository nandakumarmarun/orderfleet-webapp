package com.orderfleet.webapp.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "tbl_billing_slab")
public class Slab {
    @Id
    @GenericGenerator(name = "seq_billing_slab_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "seq_billing_slab_id") })
    @GeneratedValue(generator = "seq_billing_slab_id_GEN")
    @Column(name = "id", nullable = false)
    private Long id;

    private String pid;

    private int minimumUser;

    private int maximumUser;

    private double slabRate;

    @ManyToOne
    private Company company;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMinimumUser() {
        return minimumUser;
    }

    public void setMinimumUser(int minimumUser) {
        this.minimumUser = minimumUser;
    }

    public int getMaximumUser() {
        return maximumUser;
    }

    public void setMaximumUser(int maximumUser) {
        this.maximumUser = maximumUser;
    }

    public double getSlabRate() {
        return slabRate;
    }

    public void setSlabRate(double slabRate) {
        this.slabRate = slabRate;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getPid() {return pid;}

    public void setPid(String pid) {this.pid = pid;}

    @Override
    public String toString() {
        return "slab{" +
                "id=" + id +
                ", minimumUser=" + minimumUser +
                ", maximumUser=" + maximumUser +
                ", slabRate=" + slabRate +
                '}';
    }
}
