package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.mapper.LocationMapper;

@Component
public class LocationMapperImpl extends LocationMapper {

	@Override
	public LocationDTO locationToLocationDTO(Location location) {
		if (location == null) {
			return null;
		}

		LocationDTO locationDTO = new LocationDTO();

		locationDTO.setActivated(location.getActivated());
		if (location.getId() != null) {
			locationDTO.setId(String.valueOf(location.getId()));
		}
		locationDTO.setPid(location.getPid());
		locationDTO.setName(location.getName());
		locationDTO.setAlias(location.getAlias());
		locationDTO.setDescription(location.getDescription());
		locationDTO.setLatitude(location.getLatitude());
		locationDTO.setLongitude(location.getLongitude());
		locationDTO.setLastModifiedDate(location.getLastModifiedDate());
		locationDTO.setLocationId(location.getLocationId());

		return locationDTO;
	}

	public LocationDTO locationToLocationDTODescription(Location location) {
		if (location == null) {
			return null;
		}

		LocationDTO locationDTO = new LocationDTO();

		locationDTO.setActivated(location.getActivated());
		if (location.getId() != null) {
			locationDTO.setId(String.valueOf(location.getId()));
		}
		locationDTO.setPid(location.getPid());
		locationDTO.setName(location.getDescription() != null && !location.getDescription().equalsIgnoreCase("common")
				? location.getDescription()
				: location.getName());
		locationDTO.setAlias(location.getAlias());
		locationDTO.setDescription(location.getDescription());
		locationDTO.setLatitude(location.getLatitude());
		locationDTO.setLongitude(location.getLongitude());
		locationDTO.setLastModifiedDate(location.getLastModifiedDate());
		locationDTO.setLocationId(location.getLocationId());

		return locationDTO;
	}

	@Override
	public List<LocationDTO> locationsToLocationDTOs(List<Location> locations) {
		if (locations == null) {
			return null;
		}

		List<LocationDTO> list = new ArrayList<LocationDTO>();

		if (getCompanyCofig()) {
			for (Location location : locations) {
				list.add(locationToLocationDTODescription(location));
			}
		} else {
			for (Location location : locations) {
				list.add(locationToLocationDTO(location));
			}
		}

		return list;
	}

	@Override
	public Location locationDTOToLocation(LocationDTO locationDTO) {
		if (locationDTO == null) {
			return null;
		}

		Location location = new Location();

		location.setActivated(locationDTO.getActivated());
		if (locationDTO.getId() != null) {
			location.setId(Long.parseLong(locationDTO.getId()));
		}
		location.setPid(locationDTO.getPid());
		location.setName(locationDTO.getName());
		location.setAlias(locationDTO.getAlias());
		location.setLatitude(locationDTO.getLatitude());
		location.setLongitude(locationDTO.getLongitude());
		location.setDescription(locationDTO.getDescription());
		location.setLocationId(locationDTO.getLocationId());

		return location;
	}

	@Override
	public List<Location> locationDTOsToLocations(List<LocationDTO> locationDTOs) {
		if (locationDTOs == null) {
			return null;
		}

		List<Location> list = new ArrayList<Location>();
		for (LocationDTO locationDTO : locationDTOs) {
			list.add(locationDTOToLocation(locationDTO));
		}

		return list;
	}

	private String locationName(Location location) {
		if (location.getDescription() != null && getCompanyCofig() && !location.getDescription().equals("common")) {
			return location.getDescription();
		}

		return location.getName();
	}
}
