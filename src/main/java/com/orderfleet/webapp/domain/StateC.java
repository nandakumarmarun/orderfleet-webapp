package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A State.
 * 
 * @author Shaheer
 * @since May 06, 2016
 */
@Entity
@Table(name = "tbl_statec")

public class StateC implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
	@GenericGenerator(name = "seq_state_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_state_id") })
	@GeneratedValue(generator = "seq_state_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_state_id')")
	private Long id;

    @NotNull
    @Pattern(regexp ="^[a-zA-Z ]*$")
    @Column(name = "name", length = 100, unique = false, nullable = false, updatable = false)
    private String name;
	/*
	 * @NotNull
	 * 
	 * @Column(name = "code_iso_2", length = 2, unique = true, nullable = false,
	 * updatable = false) private String code;
	 */
    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false, updatable = false)
    private CountryC country;
    
  // private Set<CountryC> countryc = new HashSet<CountryC>(0);
   
  // private Set<DistrictC> district = new HashSet<DistrictC>(0);
    
    

    public StateC() {
		super();
	}
    

	

    public StateC(CountryC country, String name) {
		this.country = country;
		this.name = name;
	}

	
	
	

	
	public void setCountry(CountryC country) {
		this.country = country;
	}

	

	public void setName(String name) {
		this.name = name;
	}
	
	

	/*
	 * @OneToMany(fetch = FetchType.LAZY, mappedBy = "statec") public Set<DistrictC>
	 * getDistrict() { return this.district; }
	 */
	/*
	 * public void setDistrict(Set<DistrictC> district) { this.district = district;
	 * }
	 */

    


	
	  


	public Long getId() {
		return id;
	}




	public void setId(Long id) {
		this.id = id;
	}




	public String getName() {
		return name;
	}




	public CountryC getCountry() {
		return country;
	}




	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StateC state = (StateC) o;
        if(state.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, state.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "State{" +
            "id=" + id +
            ", name='" + name + "'" +
           
            '}';
    }
}
