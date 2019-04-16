package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A EmployeeHierarchy.
 * 
 * @author Shaheer
 * @since June 02, 2016
 */
@Entity
@Table(name = "tbl_employee_hierarchy")
public class EmployeeHierarchy implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "seq_employeehierarchy_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_employeehierarchy_id") })
	@GeneratedValue(generator = "seq_employeehierarchy_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_employeehierarchy_id')")
	private Long id;
	
	@ManyToOne
    @NotNull
    private Company company;
	
	@ManyToOne
    @NotNull
    @JoinColumn(name = "employee_id")
    private EmployeeProfile employee;

	/** The parent employee, can be null if this is the root employee. */
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private EmployeeProfile parent;

	@NotNull
    @Column(name = "activated", nullable = false)
    private Boolean activated = true;
	
	@NotNull
    @Column(name = "activated_date", nullable = false)
    private ZonedDateTime activatedDate = ZonedDateTime.now();
	
	@Column(name = "inactivated_date", nullable = true)
	private ZonedDateTime inactivatedDate = null;
	
	public EmployeeHierarchy() {
		super();
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public ZonedDateTime getActivatedDate() {
        return activatedDate;
    }

    public void setActivatedDate(ZonedDateTime activatedDate) {
        this.activatedDate = activatedDate;
    }

    public ZonedDateTime getInactivatedDate() {
        return inactivatedDate;
    }

    public void setInactivatedDate(ZonedDateTime inactivatedDate) {
        this.inactivatedDate = inactivatedDate;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public EmployeeProfile getEmployee() {
		return employee;
	}

	public void setEmployee(EmployeeProfile employee) {
		this.employee = employee;
	}

	public EmployeeProfile getParent() {
		return parent;
	}

	public void setParent(EmployeeProfile parent) {
		this.parent = parent;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmployeeHierarchy employeeHierarchy = (EmployeeHierarchy) o;
        if(employeeHierarchy.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, employeeHierarchy.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "EmployeeHierarchy{" +
            "id=" + id +
            ", employee='" + employee + "'" +
            ", parent='" + parent + "'" +
            ", activated='" + activated + "'" +
            ", activatedDate='" + activatedDate + "'" +
            ", inactivatedDate='" + inactivatedDate + "'" +
            '}';
    }
}
