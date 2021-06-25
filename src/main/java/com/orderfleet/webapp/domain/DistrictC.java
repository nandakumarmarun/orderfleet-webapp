package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A District.
 * 
 * @author Shaheer
 * @since May 06, 2016
 */
@Entity
@Table(name = "tbl_districtc")
public class DistrictC implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
	@GenericGenerator(name = "seq_district_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_district") })
	@GeneratedValue(generator = "seq_district_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_district')")
    private Long id;

    @NotNull
    @Column(name = "name", length = 100, unique = false, nullable = false)
    private String name;

    

	@ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private StateC state;
   

    public DistrictC() {
		super();
	}
    

	public DistrictC(Long id, String name, StateC state) {
		super();
		this.id = id;
		this.name = name;
		this.state = state;
	}



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stateId", nullable = false)
    public StateC getState() {
        return state;
    }

    public void setState(StateC state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DistrictC district = (DistrictC) o;
        if(district.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, district.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "District{" +
            "id=" + id +
            ", name='" + name + "'" +
           
            '}';
    }
}
