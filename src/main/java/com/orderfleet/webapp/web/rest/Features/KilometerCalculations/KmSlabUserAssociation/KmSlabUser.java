package com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlabUserAssociation;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlab.KmSlab;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="tbl_km_slab_user")
public class KmSlabUser {

    @Id
    @GenericGenerator(name = "seq_tbl_km_slab_user_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "seq_tbl_km_slab_user_id") })
    @GeneratedValue(generator = "seq_tbl_km_slab_user_id_GEN")
    @Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_tbl_km_slab_user_id')")
    private Long id;

    @Column(name="pid")
    private String pid;

    @ManyToOne
    private KmSlab kmSlab;
    @ManyToOne
    private User user;
    @NotNull
    @ManyToOne
    private Company company;

    public KmSlabUser() {
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public KmSlab getKmSlab() {
        return kmSlab;
    }

    public void setKmSlab(KmSlab kmSlab) {
        this.kmSlab = kmSlab;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "KmSlabUser{" +
                "id=" + id +
                ", kmSlab=" + kmSlab +
                ", user=" + user +
                ", company=" + company +
                '}';
    }
}
