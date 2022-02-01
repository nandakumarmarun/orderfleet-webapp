package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityGroup;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.ActivityGroupMapper;
import com.orderfleet.webapp.web.rest.mapper.ActivityMapper;

@Component
public class ActivityGroupMapperImpl extends ActivityGroupMapper{
	
	 @Autowired
	    private ActivityMapper activityMapper;

	    @Override
	    public ActivityGroupDTO activityGroupToActivityGroupDTO(ActivityGroup activityGroup) {
	        if ( activityGroup == null ) {
	            return null;
	        }

	        ActivityGroupDTO activityGroupDTO = new ActivityGroupDTO();

	        activityGroupDTO.setPid( activityGroup.getPid() );
	        activityGroupDTO.setName( activityGroup.getName());
	        activityGroupDTO.setAlias( activityGroup.getAlias() );
	        activityGroupDTO.setDescription( activityGroup.getDescription() );
	        Set<ActivityDTO> set = activitySetToActivityDTOSet( activityGroup.getActivities() );
	        if ( set != null ) {
	            activityGroupDTO.setActivities( set );
	        }
	        activityGroupDTO.setActivated( activityGroup.getActivated() );
	        activityGroupDTO.setLastModifiedDate( activityGroup.getLastModifiedDate() );

	        return activityGroupDTO;
	    }

	
	    public ActivityGroupDTO activityGroupToActivityGroupDTODescription(ActivityGroup activityGroup) {
	        if ( activityGroup == null ) {
	            return null;
	        }

	        ActivityGroupDTO activityGroupDTO = new ActivityGroupDTO();

	        activityGroupDTO.setPid( activityGroup.getPid() );
	        activityGroupDTO.setName(activityGroup.getDescription() != null && !activityGroup.getDescription().equalsIgnoreCase("common")
					? activityGroup.getDescription()
					: activityGroup.getName());
	        activityGroupDTO.setAlias( activityGroup.getAlias() );
	        activityGroupDTO.setDescription( activityGroup.getDescription() );
	        Set<ActivityDTO> set = activitySetToActivityDTOSet( activityGroup.getActivities() );
	        if ( set != null ) {
	            activityGroupDTO.setActivities( set );
	        }
	        activityGroupDTO.setActivated( activityGroup.getActivated() );
	        activityGroupDTO.setLastModifiedDate( activityGroup.getLastModifiedDate() );

	        return activityGroupDTO;
	    }
	    @Override
	    public List<ActivityGroupDTO> activityGroupsToActivityGroupDTOs(List<ActivityGroup> activityGroups) {
	        if ( activityGroups == null ) {
	            return null;
	        }
	        List<ActivityGroupDTO> list = new ArrayList<ActivityGroupDTO>();
	        if (getCompanyCofig()) {
	        
	        for ( ActivityGroup activityGroup : activityGroups ) {
	            list.add( activityGroupToActivityGroupDTODescription( activityGroup ) );
	        }
	        }else
	        {
	        	for ( ActivityGroup activityGroup : activityGroups ) {
		            list.add( activityGroupToActivityGroupDTO( activityGroup ) );
	        }
	        }
	        return list;
	    }

	    @Override
	    public ActivityGroup activityGroupDTOToActivityGroup(ActivityGroupDTO activityGroupDTO) {
	        if ( activityGroupDTO == null ) {
	            return null;
	        }

	        ActivityGroup activityGroup = new ActivityGroup();

	        activityGroup.setPid( activityGroupDTO.getPid() );
	        activityGroup.setName( activityGroupDTO.getName() );
	        activityGroup.setAlias( activityGroupDTO.getAlias() );
	        activityGroup.setDescription( activityGroupDTO.getDescription() );
	        activityGroup.setActivated( activityGroupDTO.getActivated() );

	        return activityGroup;
	    }

	    @Override
	    public List<ActivityGroup> activityGroupDTOsToActivityGroups(List<ActivityGroupDTO> activityGroupDTOs) {
	        if ( activityGroupDTOs == null ) {
	            return null;
	        }

	        List<ActivityGroup> list = new ArrayList<ActivityGroup>();
	        for ( ActivityGroupDTO activityGroupDTO : activityGroupDTOs ) {
	            list.add( activityGroupDTOToActivityGroup( activityGroupDTO ) );
	        }

	        return list;
	    }

	    protected Set<ActivityDTO> activitySetToActivityDTOSet(Set<Activity> set) {
	        if ( set == null ) {
	            return null;
	        }

	        Set<ActivityDTO> set_ = new HashSet<ActivityDTO>();
	        for ( Activity activity : set ) {
	            set_.add( activityMapper.activityToActivityDTO( activity ) );
	        }

	        return set_;
	    }
	    private String  activityGroupName(ActivityGroup activityGroup) {
	        if(activityGroup.getDescription()!=null && getCompanyCofig() && !activityGroup.getDescription().equals("common")) {
	        return activityGroup.getDescription();
	        }
	       
	    return activityGroup.getName();
	    }
}
