package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.web.rest.api.dto.MBLocationHierarchyDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;

/**
 * Service Interface for managing LocationHierarchy.
 * 
 * @author Shaheer
 * @since May 27, 2016
 */
public interface LocationHierarchyService {

    /**
     * Save a locationHierarchy.
     * 
     * @param locationHierarchyDTO the entity to save
     * @return the persisted entity
     */
    LocationHierarchyDTO save(LocationHierarchyDTO locationHierarchyDTO);
    
    /**
     * Save a locationHierarchies.
     * 
     * @param locationHierarchyDTOs the entities to save
     */
    void save(List<LocationHierarchyDTO> locationHierarchyDTOs);
    
    /**
     * Save a root location in Hierarchy.
     * 
     * @param locationId the locationId to save
     */
   void saveRootLocation(Long locationId);

    /**
     *  Get all the locationHierarchies.
     *  
     *  @return the list of entities
     */
    List<LocationHierarchyDTO> findAllByCompanyAndActivatedTrue();

    /**
     *  Get the "id" locationHierarchy.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    LocationHierarchyDTO findOne(Long id);

    /**
     *  Delete the "id" locationHierarchy.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);
    
    void saveRootLocationWithCompanyId(Long locationId, Long companyId);
    
    List<LocationHierarchyDTO> findByLocationsInAndActivatedTrue(List<Location> locations);
    
    List<Long> getAllChildrenIdsByParentId(Long parentLocId);
    
    List<LocationDTO> findChildLocationsByParentId(Long parentLocId);
    
    LocationDTO findParentLocation(Long locationId);
    
    List<LocationHierarchyDTO> findByLocationPidInAndActivatedTrue(List<String> locationPids);
    
    List<MBLocationHierarchyDTO> findByCompanyAndActivatedTrue();
    
    List<MBLocationHierarchyDTO> findByUserAndActivatedTrue();
}
