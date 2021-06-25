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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A Country.
 * 
 * @author Shaheer
 * @since May 06, 2016
 */
@Entity
@Table(name = "tbl_countryc")
public class CountryC implements Serializable {
	
	/* @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE) */

    private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "SEQ_COUNTRY_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SEQ_COUNTRY") })
	@GeneratedValue(generator = "SEQ_COUNTRY_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('SEQ_COUNTRY')")
    private Long id;

    @NotNull
    @Pattern(regexp ="^[a-zA-Z ]*$")
    @Column(name = "name", length = 100, unique = true, nullable = false, updatable = false)
    private String name;

    @NotNull
    @Column(name = "code_iso_2", length = 2, unique = true, nullable = false, updatable = false)
    private String code;
    
   
    

   

	public CountryC() {
		super();
	}

	public CountryC(Long id, String name, String code) {
		super();
		this.id = id;
		this.name = name;
		this.code = code;
	}

	public long getId() {
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
	


	
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Country{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", code='" + code + "'" +
            '}';
    }
}
