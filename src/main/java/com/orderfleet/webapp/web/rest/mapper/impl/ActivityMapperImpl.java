package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountTypeMapper;
import com.orderfleet.webapp.web.rest.mapper.ActivityMapper;

@Component
public class ActivityMapperImpl extends ActivityMapper {

	@Autowired
	private AccountTypeMapper accountTypeMapper;

	@Override
	public ActivityDTO activityToActivityDTO(Activity activity) {
		if (activity == null) {
			return null;
		}

		ActivityDTO activityDTO = new ActivityDTO();

		activityDTO.setActivated(activity.getActivated());
		Set<AccountTypeDTO> set = accountTypeSetToAccountTypeDTOSet(activity.getActivityAccountTypes());
		if (set != null) {
			activityDTO.setActivityAccountTypes(set);
		}
		activityDTO.setAlias(activity.getAlias());
		activityDTO.setCompletePlans(activity.getCompletePlans());
		activityDTO.setContactManagement(activity.getContactManagement());
		activityDTO.setDescription(activity.getDescription());
		activityDTO.setGeoFencing(activity.getGeoFencing());
		activityDTO.setHasDefaultAccount(activity.getHasDefaultAccount());
		activityDTO.setHasSecondarySales(activity.getHasSecondarySales());
		activityDTO.setHasTelephonicOrder(activity.getHasTelephonicOrder());
		activityDTO.setLastModifiedDate(activity.getLastModifiedDate());
		activityDTO.setName(activity.getName());
		activityDTO.setPid(activity.getPid());
		activityDTO.setTargetDisplayOnDayplan(activity.getTargetDisplayOnDayplan());
		activityDTO.setEmailTocomplaint(activity.getEmailTocomplaint());
		activityDTO.setLocationRadius(activity.getLocationRadius());
		activityDTO.setSecondarySalesThroughApi(activity.getSecondarySalesThroughApi());
		activityDTO.setKmCalculationDisabled(activity.getKmCalculationDisabled());
		activityDTO.setAutoTaskCreation(activity.getAutoTaskCreation());
		return activityDTO;
	}

	public ActivityDTO activityToActivityDTODescription(Activity activity) {
		if (activity == null) {
			return null;
		}

		ActivityDTO activityDTO = new ActivityDTO();

		activityDTO.setActivated(activity.getActivated());
		Set<AccountTypeDTO> set = accountTypeSetToAccountTypeDTOSet(activity.getActivityAccountTypes());
		if (set != null) {
			activityDTO.setActivityAccountTypes(set);
		}
		activityDTO.setAlias(activity.getAlias());
		activityDTO.setCompletePlans(activity.getCompletePlans());
		activityDTO.setContactManagement(activity.getContactManagement());
		activityDTO.setDescription(activity.getDescription());
		activityDTO.setGeoFencing(activity.getGeoFencing());
		activityDTO.setHasDefaultAccount(activity.getHasDefaultAccount());
		activityDTO.setHasSecondarySales(activity.getHasSecondarySales());
		activityDTO.setHasTelephonicOrder(activity.getHasTelephonicOrder());
		activityDTO.setLastModifiedDate(activity.getLastModifiedDate());
		activityDTO.setName(activity.getDescription() != null && !activity.getDescription().equalsIgnoreCase("common")
				? activity.getDescription()
				: activity.getName());
		activityDTO.setPid(activity.getPid());
		activityDTO.setTargetDisplayOnDayplan(activity.getTargetDisplayOnDayplan());
		activityDTO.setEmailTocomplaint(activity.getEmailTocomplaint());
		activityDTO.setLocationRadius(activity.getLocationRadius());
        activityDTO.setSecondarySalesThroughApi(activity.getSecondarySalesThroughApi());
		activityDTO.setKmCalculationDisabled(activity.getKmCalculationDisabled());
		
		return activityDTO;
	}

	@Override
	public List<ActivityDTO> activitiesToActivityDTOs(List<Activity> activities) {
		if (activities == null) {
			return null;
		}

		List<ActivityDTO> list = new ArrayList<ActivityDTO>();

		if (getCompanyCofig()) {
			for (Activity activity : activities) {
				list.add(activityToActivityDTODescription(activity));
			}
		} else {
			for (Activity activity : activities) {
				list.add(activityToActivityDTO(activity));
			}
		}

		return list;
	}

	@Override
	public Activity activityDTOToActivity(ActivityDTO activityDTO) {
		if (activityDTO == null) {
			return null;
		}

		Activity activity = new Activity();

		activity.setActivated(activityDTO.getActivated());
		Set<AccountType> set = accountTypeDTOSetToAccountTypeSet(activityDTO.getActivityAccountTypes());
		if (set != null) {
			activity.setActivityAccountTypes(set);
		}
		activity.setAlias(activityDTO.getAlias());
		activity.setCompletePlans(activityDTO.getCompletePlans());
		activity.setContactManagement(activityDTO.getContactManagement());
		activity.setDescription(activityDTO.getDescription());
		activity.setGeoFencing(activityDTO.getGeoFencing());
		activity.setHasDefaultAccount(activityDTO.getHasDefaultAccount());
		activity.setHasSecondarySales(activityDTO.getHasSecondarySales());
		activity.setHasTelephonicOrder(activityDTO.getHasTelephonicOrder());
		activity.setName(activityDTO.getName());
		activity.setPid(activityDTO.getPid());
		activity.setTargetDisplayOnDayplan(activityDTO.getTargetDisplayOnDayplan());
		activity.setEmailTocomplaint(activityDTO.getEmailTocomplaint());
		activity.setKmCalculationDisabled(activityDTO.getKmCalculationDisabled());
		activity.setAutoTaskCreation(activityDTO.getAutoTaskCreation());
		if(activityDTO.getLocationRadius()!= null)
		{
        activity.setLocationRadius(activityDTO.getLocationRadius());
		}
		activity.setSecondarySalesThroughApi(activityDTO.getSecondarySalesThroughApi());
		return activity;
	}

	@Override
	public List<Activity> activityDTOsToActivities(List<ActivityDTO> activityDTOs) {
		if (activityDTOs == null) {
			return null;
		}

		List<Activity> list = new ArrayList<Activity>();
		for (ActivityDTO activityDTO : activityDTOs) {
			list.add(activityDTOToActivity(activityDTO));
		}

		return list;
	}

	protected Set<AccountTypeDTO> accountTypeSetToAccountTypeDTOSet(Set<AccountType> set) {
		if (set == null) {
			return null;
		}

		Set<AccountTypeDTO> set_ = new HashSet<AccountTypeDTO>();
		for (AccountType accountType : set) {
			set_.add(accountTypeMapper.accountTypeToAccountTypeDTO(accountType));
		}

		return set_;
	}

	protected Set<AccountType> accountTypeDTOSetToAccountTypeSet(Set<AccountTypeDTO> set) {
		if (set == null) {
			return null;
		}

		Set<AccountType> set_ = new HashSet<AccountType>();
		for (AccountTypeDTO accountTypeDTO : set) {
			set_.add(accountTypeMapper.accountTypeDTOToAccountType(accountTypeDTO));
		}

		return set_;
	}

	private String activityName(Activity activity) {
		if (activity.getDescription() != null && getCompanyCofig() && !activity.getDescription().equals("common")) {
			return activity.getDescription();
		}

		return activity.getName();
	}
}
