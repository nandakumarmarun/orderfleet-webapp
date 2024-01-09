package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.TransferAccountDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web")
public class TransferAccountResource {

    private final Logger log = LoggerFactory.getLogger(TransferAccountResource.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    private AccountProfileRepository accountProfileRepository;

    @Autowired
    private AccountProfileService accountProfileService;

    @Autowired
    LocationAccountProfileService locationAccountProfileService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationAccountProfileRepository locationAccountProfileRepository;

    @GetMapping("/transferAccounts")
    @Timed
    public ModelAndView getAllTransferAccounts(Model model, Pageable pageable) {
        log.debug("Web request to get a page of Account Transfer");

        // Fetch all users
        List<User> users = userRepository.findAllByCompanyId();

        // Fetch account profiles (assuming this is part of your use case)
        Page<AccountProfileDTO> accountProfiles = accountProfileService.findAllByCompany(pageable);

        List<Location> locations = locationRepository.findAllByCompanyIdAndActivated();

        // Add the combined data to the model
        model.addAttribute("locations", locations);
        model.addAttribute("users", users);
        model.addAttribute("accountProfiles", accountProfiles.getContent());

        return new ModelAndView("company/TransferAccount");
    }

    @GetMapping("/transferAccounts/userChange/{userPid}")
    @Timed
    public ResponseEntity<List<TransferAccountDTO>> userChange(@PathVariable String userPid) {
        log.debug("Web request to get AccountProfiles by user Pid: {}", userPid);

        List<LocationAccountProfile> locationAccountProfiles = locationAccountProfileService.findByAccountUser(userPid);

        // Map LocationAccountProfile entities to TransferAccountDTO
        List<TransferAccountDTO> transferAccountDTOs = locationAccountProfiles.stream()
                .map(locationAccountProfile -> new TransferAccountDTO(locationAccountProfile))
                .collect(Collectors.toList());

        return new ResponseEntity<>(transferAccountDTOs, HttpStatus.OK);
    }

    @GetMapping("/transferAccounts/getLocationAccountProfilesSortedByUserAndLocation/{locationPid}/{userPid}")
    @Timed
    public ResponseEntity<List<TransferAccountDTO>> getLocationAccountProfilesSortedByUserAndLocation(
            @PathVariable(name = "userPid") String userPid,
            @PathVariable(name = "locationPid") String locationPid) {

        List<LocationAccountProfile> locationAccountProfiles = locationAccountProfileRepository
                .findLocationAccountProfilesSortedByUserAndLocation(userPid, locationPid);

        // Map LocationAccountProfile entities to TransferAccountDTO
        List<TransferAccountDTO> transferAccountDTOs = locationAccountProfiles.stream()
                .map(locationAccountProfile -> {
                    TransferAccountDTO dto = new TransferAccountDTO();
                    dto.setAccountProfilePid(locationAccountProfile.getAccountProfile().getPid());
                    dto.setAccountProfileName(locationAccountProfile.getAccountProfile().getName());
                    dto.setLocationPid(locationAccountProfile.getLocation().getPid());
                    dto.setLocationName(locationAccountProfile.getLocation().getName());
                    dto.setAccountProfileAddress(locationAccountProfile.getAccountProfile().getAddress());
                    dto.setUserPid(locationAccountProfile.getAccountProfile().getUser().getPid());
                    dto.setUsername(locationAccountProfile.getAccountProfile().getUser().getFirstName());
                    return dto;
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(transferAccountDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/transferAccounts/transferAccountProfiles", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<String> transferData(
            @RequestParam(name = "currentLocationPid") String currentLocationPid,
            @RequestParam(name = "newLocation") String newLocationPid,
            @RequestParam(name = "selectedAccountProfiles") List<String> selectedAccountProfiles) {

        try {
            // Validate inputs (similar to your existing code)
            if (currentLocationPid == null || currentLocationPid.isEmpty() || newLocationPid == null || newLocationPid.isEmpty()) {
                return new ResponseEntity<>("Please select both current and new locations.", HttpStatus.BAD_REQUEST);
            }

            if (selectedAccountProfiles == null || selectedAccountProfiles.isEmpty()) {
                return new ResponseEntity<>("Please select one or more account profiles to transfer.", HttpStatus.BAD_REQUEST);
            }

            // Fetch the current and new locations, and the company
            Location currentLocation = locationRepository.findOneByPid(currentLocationPid).orElse(null);
            Location newLocation = locationRepository.findOneByPid(newLocationPid).orElse(null);
            Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

            if (currentLocation == null || newLocation == null) {
                return new ResponseEntity<>("Invalid current or new location.", HttpStatus.BAD_REQUEST);
            }
//            if(currentLocation==newLocation){
//                return new ResponseEntity<>("Same location selected", HttpStatus.BAD_REQUEST);
//            }

            // Iterate through selected account profiles and transfer them to the new location
            for (String accountProfilePid : selectedAccountProfiles) {
                AccountProfile accountProfile = accountProfileRepository.findOneByPid(accountProfilePid).orElse(null);

                if (accountProfile != null) {
                    // Create a new LocationAccountProfile for the new location
                    LocationAccountProfile locationAccountProfile = new LocationAccountProfile();
                    locationAccountProfile.setAccountProfile(accountProfile);
                    locationAccountProfile.setLocation(newLocation);
                    locationAccountProfile.setCompany(company);

                    // Save the LocationAccountProfile to the new location
                    locationAccountProfileRepository.save(locationAccountProfile);

                    // Delete the old LocationAccountProfile for the current location
                    locationAccountProfileRepository.deleteByAccountProfileIdAndLocationId(accountProfile.getId(), currentLocation.getId());
                }
            }

            return new ResponseEntity<>("Data transfer completed.", HttpStatus.OK);
        } catch (Exception e) {
            // Handle any errors or exceptions
            return new ResponseEntity<>("Data transfer failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/transferAccounts/getLocationByUser")
    @Timed
    public ResponseEntity<List<Location>> getLocationByUser(@RequestParam String userPid) {
        log.debug("Web request to get locations by userPid: {}", userPid);

        // Fetch locations based on the selected user
        List<Location> locations = locationAccountProfileRepository.findLocationByAccountProfileUser(userPid);

        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

}
