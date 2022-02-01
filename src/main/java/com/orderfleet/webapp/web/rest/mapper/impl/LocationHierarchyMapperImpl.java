package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationHierarchy;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;
import com.orderfleet.webapp.web.rest.mapper.LocationHierarchyMapper;
@Component
public class LocationHierarchyMapperImpl extends LocationHierarchyMapper{

	 @Override
	    public LocationHierarchyDTO locationHierarchyToLocationHierarchyDTO(LocationHierarchy locationHierarchy) {
	        if ( locationHierarchy == null ) {
	            return null;
	        }

	        LocationHierarchyDTO locationHierarchyDTO = new LocationHierarchyDTO();

	        locationHierarchyDTO.setParentName( locationHierarchyParentName( locationHierarchy ) );
	        locationHierarchyDTO.setLocationName( locationHierarchyLocationName( locationHierarchy ) );
	        locationHierarchyDTO.setLocationId( locationHierarchyLocationId( locationHierarchy ) );
	        locationHierarchyDTO.setParentId( locationHierarchyParentId( locationHierarchy ) );
	        locationHierarchyDTO.setCustom( locationHierarchy.getCustom() );

	        return locationHierarchyDTO;
	    }
	  public LocationHierarchyDTO locationHierarchyToLocationHierarchyDTODescription(LocationHierarchy locationHierarchy) {
	        if ( locationHierarchy == null ) {
	            return null;
	        }

	        LocationHierarchyDTO locationHierarchyDTO = new LocationHierarchyDTO();

	        locationHierarchyDTO.setParentName( locationHierarchyParentName( locationHierarchy ) );
	        locationHierarchyDTO.setLocationName( locationHierarchyLocationDescription( locationHierarchy ) );
	        locationHierarchyDTO.setLocationId( locationHierarchyLocationId( locationHierarchy ) );
	        locationHierarchyDTO.setParentId( locationHierarchyParentId( locationHierarchy ) );
	        locationHierarchyDTO.setCustom( locationHierarchy.getCustom() );

	        return locationHierarchyDTO;
	    }
	    @Override
	    public List<LocationHierarchyDTO> locationHierarchiesToLocationHierarchyDTOs(List<LocationHierarchy> locationHierarchies) {
	        if ( locationHierarchies == null ) {
	            return null;
	        }

	        List<LocationHierarchyDTO> list = new ArrayList<LocationHierarchyDTO>();
	        if(getCompanyCofig())
	        {
	        for ( LocationHierarchy locationHierarchy : locationHierarchies ) {
	            list.add( locationHierarchyToLocationHierarchyDTODescription( locationHierarchy ) );
	        }
	        }
	        else
	        {
	        	 for ( LocationHierarchy locationHierarchy : locationHierarchies ) {
	 	            list.add( locationHierarchyToLocationHierarchyDTO( locationHierarchy ) );
	 	        }
	        	
	        }
	        return list;
	    }

	    @Override
	    public LocationHierarchy locationHierarchyDTOToLocationHierarchy(LocationHierarchyDTO locationHierarchyDTO) {
	        if ( locationHierarchyDTO == null ) {
	            return null;
	        }

	        LocationHierarchy locationHierarchy = new LocationHierarchy();

	        locationHierarchy.setParent( locationFromId( locationHierarchyDTO.getParentId() ) );
	        locationHierarchy.setLocation( locationFromId( locationHierarchyDTO.getLocationId() ) );
	        locationHierarchy.setCustom( locationHierarchyDTO.getCustom() );

	        return locationHierarchy;
	    }

	    @Override
	    public List<LocationHierarchy> locationHierarchyDTOsToLocationHierarchies(List<LocationHierarchyDTO> locationHierarchyDTOs) {
	        if ( locationHierarchyDTOs == null ) {
	            return null;
	        }

	        List<LocationHierarchy> list = new ArrayList<LocationHierarchy>();
	        for ( LocationHierarchyDTO locationHierarchyDTO : locationHierarchyDTOs ) {
	            list.add( locationHierarchyDTOToLocationHierarchy( locationHierarchyDTO ) );
	        }

	        return list;
	    }

	    private String locationHierarchyParentName(LocationHierarchy locationHierarchy) {

	        if ( locationHierarchy == null ) {
	            return null;
	        }
	        Location parent = locationHierarchy.getParent();
	        if ( parent == null ) {
	            return null;
	        }
	        String name = parent.getName();
	        if ( name == null ) {
	            return null;
	        }
	        return name;
	    }

	    private String locationHierarchyLocationName(LocationHierarchy locationHierarchy) {

	        if ( locationHierarchy == null ) {
	            return null;
	        }
	        Location location = locationHierarchy.getLocation();
	        if ( location == null ) {
	            return null;
	        }
	        String name = location.getName();
	        if ( name == null ) {
	            return null;
	        }
	        
//		        if(location.getDescription()!=null && getCompanyCofig() && !location.getDescription().equals("common")) {
//		        return location.getDescription();
//		        }
		       
		    
	        return name;
	    }
	    private String locationHierarchyLocationDescription(LocationHierarchy locationHierarchy) {

	        if ( locationHierarchy == null ) {
	            return null;
	        }
	        Location location = locationHierarchy.getLocation();
	        if ( location == null ) {
	            return null;
	        }
	        String name = location.getName();
	        if ( name == null ) {
	            return null;
	        }
	        
		        if(location.getDescription()!=null && !location.getDescription().equals("common")) {
		        return location.getDescription();
		        }
		       
		    
	        return name;
	    }

	    private Long locationHierarchyLocationId(LocationHierarchy locationHierarchy) {

	        if ( locationHierarchy == null ) {
	            return null;
	        }
	        Location location = locationHierarchy.getLocation();
	        if ( location == null ) {
	            return null;
	        }
	        Long id = location.getId();
	        if ( id == null ) {
	            return null;
	        }
	        return id;
	    }

	    private Long locationHierarchyParentId(LocationHierarchy locationHierarchy) {

	        if ( locationHierarchy == null ) {
	            return null;
	        }
	        Location parent = locationHierarchy.getParent();
	        if ( parent == null ) {
	            return null;
	        }
	        Long id = parent.getId();
	        if ( id == null ) {
	            return null;
	        }
	        return id;
	    }
	    
}
