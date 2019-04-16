package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A UserActivity.
 * 
 * @author Muhammed Riyas T
 * @since June 29, 2016
 */
@Entity
@Table(name = "tbl_user_activity")
public class UserActivity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_activity_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_activity_id") })
	@GeneratedValue(generator = "seq_user_activity_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_activity_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private User user;

	@NotNull
	@ManyToOne
	private Activity activity;

	@Column(name = "plan_throuch_only", nullable = false, columnDefinition = "boolean DEFAULT 'False' ")
	private boolean planThrouchOnly;

	@Column(name = "exclude_accounts_in_plan", nullable = false, columnDefinition = "boolean DEFAULT 'False' ")
	private boolean excludeAccountsInPlan;

	@Column(name = "save_activity_duration", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean saveActivityDuration;
	
	@NotNull
	@ManyToOne
	private Company company;
	
	@Column(name = "interim_save", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean interimSave;

	public UserActivity() {
		super();
	}

	public UserActivity(User user, Activity activity, Company company, boolean planThrouchOnly,
			boolean excludeAccountsInPlan,boolean saveActivityDuration,boolean interimSave) {
		super();
		this.user = user;
		this.activity = activity;
		this.company = company;
		this.planThrouchOnly = planThrouchOnly;
		this.excludeAccountsInPlan = excludeAccountsInPlan;
		this.saveActivityDuration=saveActivityDuration;
		this.interimSave=interimSave;
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

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public boolean getPlanThrouchOnly() {
		return planThrouchOnly;
	}

	public void setPlanThrouchOnly(boolean planThrouchOnly) {
		this.planThrouchOnly = planThrouchOnly;
	}

	public boolean getExcludeAccountsInPlan() {
		return excludeAccountsInPlan;
	}

	public void setExcludeAccountsInPlan(boolean excludeAccountsInPlan) {
		this.excludeAccountsInPlan = excludeAccountsInPlan;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	
	public boolean getSaveActivityDuration() {
		return saveActivityDuration;
	}

	public void setSaveActivityDuration(boolean saveActivityDuration) {
		this.saveActivityDuration = saveActivityDuration;
	}

	public boolean getInterimSave() {
		return interimSave;
	}

	public void setInterimSave(boolean interimSave) {
		this.interimSave = interimSave;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UserActivity userActivity = (UserActivity) o;
		if (userActivity.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, userActivity.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "UserActivity [id=" + id + ", user=" + user + ", activity=" + activity + ", planThrouchOnly="
				+ planThrouchOnly + ", excludeAccountsInPlan=" + excludeAccountsInPlan + ", saveActivityDuration="
				+ saveActivityDuration + ", company=" + company + "]";
	}

	
}
