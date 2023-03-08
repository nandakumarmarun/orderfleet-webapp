package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.enums.ContactManagement;

/**
 * A DTO for the Activity entity.
 * 
 * @author Muhammed Riyas T
 * @since May 19, 2016
 */
public class ActivityDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	private boolean hasDefaultAccount;

	private boolean hasSecondarySales;

	private Set<AccountTypeDTO> activityAccountTypes = new HashSet<AccountTypeDTO>();

	private Set<DocumentDTO> documents = new HashSet<DocumentDTO>();

	private boolean planThrouchOnly;

	private boolean excludeAccountsInPlan;

	private boolean activated;

	private boolean completePlans;

	private boolean saveActivityDuration;

	private boolean targetDisplayOnDayplan;
	
	private boolean emailTocomplaint;

	private int sortOrder;

	private LocalDateTime lastModifiedDate;

	private boolean interimSave;

	private String companyPid;

	private String companyName;
	
	private boolean geoFencing;
	
	private boolean hasTelephonicOrder;
	
	private Double locationRadius;



	private ContactManagement contactManagement;

	public ActivityDTO() {
		super();
	}

	public ActivityDTO(Activity activity) {
		super();
		this.pid = activity.getPid();
		this.name = activity.getName();
		this.alias = activity.getAlias();
		this.description = activity.getDescription();
		this.hasDefaultAccount = activity.getHasDefaultAccount();
		this.completePlans = activity.getCompletePlans();
		this.lastModifiedDate = activity.getLastModifiedDate();
		this.targetDisplayOnDayplan = activity.getTargetDisplayOnDayplan();
		this.hasSecondarySales = activity.getHasSecondarySales();
		this.geoFencing = activity.getGeoFencing();
		this.hasTelephonicOrder = activity.getHasTelephonicOrder();
		this.emailTocomplaint = activity.getEmailTocomplaint();


		// eagerly load the association
		this.activityAccountTypes = activity.getActivityAccountTypes().stream().map(AccountTypeDTO::new)
				.collect(Collectors.toSet());
		this.companyPid = activity.getCompany().getPid();
		this.companyName = activity.getCompany().getLegalName();
		this.contactManagement = activity.getContactManagement();
		this.locationRadius = activity.getLocationRadius();
	}

	public ActivityDTO(Activity activity, boolean saveActivityDuration, boolean planThrouchOnly,
			boolean excludeAccountsInPlan, boolean interimSave) {
		super();
		this.pid = activity.getPid();
		this.name = activity.getName();
		this.alias = activity.getAlias();
		this.description = activity.getDescription();
		this.hasDefaultAccount = activity.getHasDefaultAccount();
		this.targetDisplayOnDayplan = activity.getTargetDisplayOnDayplan();
		this.completePlans = activity.getCompletePlans();
		this.lastModifiedDate = activity.getLastModifiedDate();
		this.hasSecondarySales = activity.getHasSecondarySales();
		this.geoFencing = activity.getGeoFencing();
		this.hasTelephonicOrder = activity.getHasTelephonicOrder();
		this.locationRadius = activity.getLocationRadius();


		
		// eagerly load the
		// association
		this.activityAccountTypes = activity.getActivityAccountTypes().stream().map(AccountTypeDTO::new)
				.collect(Collectors.toSet());
		this.saveActivityDuration = saveActivityDuration;
		this.planThrouchOnly = planThrouchOnly;
		this.excludeAccountsInPlan = excludeAccountsInPlan;
		this.interimSave = interimSave;
		this.companyPid = activity.getCompany().getPid();
		this.companyName = activity.getCompany().getLegalName();
		this.contactManagement = activity.getContactManagement();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean getHasDefaultAccount() {
		return hasDefaultAccount;
	}

	public void setHasDefaultAccount(boolean hasDefaultAccount) {
		this.hasDefaultAccount = hasDefaultAccount;
	}

	public Set<AccountTypeDTO> getActivityAccountTypes() {
		return activityAccountTypes;
	}

	public void setActivityAccountTypes(Set<AccountTypeDTO> accountAccountTypes) {
		this.activityAccountTypes = accountAccountTypes;
	}

	public Set<DocumentDTO> getDocuments() {
		return documents;
	}

	public void setDocuments(Set<DocumentDTO> documents) {
		this.documents = documents;
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

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public boolean getCompletePlans() {
		return completePlans;
	}

	public void setCompletePlans(boolean completePlans) {
		this.completePlans = completePlans;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public boolean getSaveActivityDuration() {
		return saveActivityDuration;
	}

	public void setSaveActivityDuration(boolean saveActivityDuration) {
		this.saveActivityDuration = saveActivityDuration;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public boolean getTargetDisplayOnDayplan() {
		return targetDisplayOnDayplan;
	}

	public void setTargetDisplayOnDayplan(boolean targetDisplayOnDayplan) {
		this.targetDisplayOnDayplan = targetDisplayOnDayplan;
	}

	public boolean getInterimSave() {
		return interimSave;
	}

	public void setInterimSave(boolean interimSave) {
		this.interimSave = interimSave;
	}

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public ContactManagement getContactManagement() {
		return contactManagement;
	}

	public void setContactManagement(ContactManagement contactManagement) {
		this.contactManagement = contactManagement;
	}

	public boolean getHasSecondarySales() {
		return hasSecondarySales;
	}

	public void setHasSecondarySales(boolean hasSecondarySales) {
		this.hasSecondarySales = hasSecondarySales;
	}
	

	public boolean getGeoFencing() {
		return geoFencing;
	}

	public void setGeoFencing(boolean geoFencing) {
		this.geoFencing = geoFencing;
	}

	public boolean getHasTelephonicOrder() {
		return hasTelephonicOrder;
	}

	public void setHasTelephonicOrder(boolean hasTelephonicOrder) {
		this.hasTelephonicOrder = hasTelephonicOrder;
	}
	
	

	public boolean getEmailTocomplaint() {
		return emailTocomplaint;
	}

	public void setEmailTocomplaint(boolean emailTocomplaint) {
		this.emailTocomplaint = emailTocomplaint;
	}

	public Double getLocationRadius() {
		return locationRadius;
	}

	public void setLocationRadius(Double locationRadius) {
		this.locationRadius = locationRadius;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ActivityDTO activityDTO = (ActivityDTO) o;

		if (!Objects.equals(pid, activityDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "ActivityDTO [pid=" + pid + ", name=" + name + ", alias=" + alias + ", description=" + description
				+ ", hasDefaultAccount=" + hasDefaultAccount + ", hasSecondarySales=" + hasSecondarySales
				+ ", activityAccountTypes=" + activityAccountTypes + ", documents=" + documents + ", planThrouchOnly="
				+ planThrouchOnly + ", excludeAccountsInPlan=" + excludeAccountsInPlan + ", activated=" + activated
				+ ", completePlans=" + completePlans + ", saveActivityDuration=" + saveActivityDuration
				+ ", targetDisplayOnDayplan=" + targetDisplayOnDayplan + ", sortOrder=" + sortOrder
				+ ", lastModifiedDate=" + lastModifiedDate + ", interimSave=" + interimSave + ", companyPid="
				+ companyPid + ", companyName=" + companyName + ", geoFencing=" + geoFencing + ", hasTelephonicOrder="
				+ hasTelephonicOrder + ", contactManagement=" + contactManagement + "]";
	}

	

}
