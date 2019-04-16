package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.enums.LicenseType;

/**
 * A License.
 * 
 * @author Shaheer
 * @since May 18, 2016
 */
@Entity
@Table(name = "tbl_license")
public class License implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @ManyToOne
    private Company company;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "license_type", nullable = false)
    private LicenseType licenseType;
    
    @NotNull
    @Size(min = 10, max = 10)
    @Column(name = "license_key", length = 10, unique = true, nullable = false)
    private String licenseKey;
    
    @NotNull
    @Column(name = "issued_date", nullable = false)
	private ZonedDateTime issuedDate;
    
    @NotNull
    @Column(name = "expire_date", nullable = false)
	private ZonedDateTime expireDate;
    
    @NotNull
	@Column(nullable = false)
	private boolean activated = false;
    
    @Column(name = "activated_date", nullable = true)
	private ZonedDateTime activatedDate = null;

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

	public LicenseType getLicenseType() {
		return licenseType;
	}

	public void setLicenseType(LicenseType licenseType) {
		this.licenseType = licenseType;
	}

	public String getLicenseKey() {
		return licenseKey;
	}

	public void setLicenseKey(String licenseKey) {
		this.licenseKey = licenseKey;
	}

	public ZonedDateTime getIssuedDate() {
		return issuedDate;
	}

	public void setIssuedDate(ZonedDateTime issuedDate) {
		this.issuedDate = issuedDate;
	}

	public ZonedDateTime getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(ZonedDateTime expireDate) {
		this.expireDate = expireDate;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public ZonedDateTime getActivatedDate() {
		return activatedDate;
	}

	public void setActivatedDate(ZonedDateTime activatedDate) {
		this.activatedDate = activatedDate;
	}
    
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        License license = (License) o;
        if(license.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, license.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
    
    @Override
    public String toString() {
        return "Company{" +
            "id=" + id +
            ", licenseType='" + licenseType + "'" +
            ", licenseKey='" + licenseKey + "'" +
            ", issuedDate='" + issuedDate + "'" +
            ", expireDate='" + expireDate + "'" +
            ", activated='" + activated + "'" +
            ", activatedDate='" + activatedDate + "'" +
            '}';
    }
    

}
