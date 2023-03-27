package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.SlabRepository;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.SlabService;
import com.orderfleet.webapp.web.rest.dto.slabDTO;
import com.orderfleet.webapp.web.rest.dto.slabViewDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web")
public class BillingSlabResources {

    @Inject
    private CompanyRepository companyRepository;

    @Inject
    private CompanyService companyService;

    @Inject
    private SlabRepository slabRepository;

    @Inject
    private SlabService slabService;

    private final Logger log = LoggerFactory.getLogger(UserManagementResource.class);

    @RequestMapping(value = "/billing-slabs", method = RequestMethod.GET)
    @Timed
    public String getSlabManagementPage(Pageable pageable, Model model) {
        log.debug("Web request to get user-management page with user");
        model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
        model.addAttribute("slabs",slabRepository.findAll());
        return "site_admin/billing-slabs";
    }

    @RequestMapping(value = "/billing-slabs/save-slab", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<slabDTO> createSlab(@RequestBody slabDTO slabDTO, HttpServletRequest request)
            throws URISyntaxException {
        log.debug("REST request to save slab : {}", slabDTO);
        Optional<Company> company = companyRepository.findOneByPid(slabDTO.getCompanyPid());
        slabDTO result = slabService.save(slabDTO);
         return new ResponseEntity<>(slabDTO, HttpStatus.OK);
    }


    @RequestMapping(value = "/billing-slabs/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<slabViewDTO>> filterCompanySlabs() {
        log.debug("Web request to get slab page: {}");

        List<slabDTO> slabDTOList = new ArrayList<slabDTO>();
        List<slabViewDTO> slabViewDTOList = new ArrayList<>();

        slabDTOList = slabService.findAll();
        List<Company> companies = companyRepository.findAll();

        Map<String, List<slabDTO>> slabDTOMap = slabDTOList.stream()
                .collect(Collectors.groupingBy(slab -> slab.getCompanyPid()));


        for (Map.Entry<String, List<slabDTO>> entry : slabDTOMap.entrySet()) {
            String companyPid = entry.getKey();
            List<slabDTO> slabDTOs = entry.getValue();

            Optional<Company> Company = companies.stream()
                    .filter(company -> company.getPid().equals(companyPid)).findAny();

            slabViewDTO slabView = new slabViewDTO();
            slabView.setCompanyPid(companyPid);
            if(Company.isPresent()){
                slabView.setLegalName(Company.get().getLegalName());
            }
            slabView.setSlabDTos(slabDTOs);
            slabViewDTOList.add(slabView);
        }

          log.debug(slabDTOMap.toString());
        return new ResponseEntity<>(slabViewDTOList, HttpStatus.OK);
    }

    @RequestMapping(value = "/billing-slabs/delete-slab/{Pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<slabDTO> deleteSlab(@PathVariable String Pid)
            throws URISyntaxException {
        log.debug("REST request to Delete slab : {}", Pid);
        slabService.DeleteByPid(Pid);
        log.debug("Slab Deleted Sucessfully");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/billing-slabs/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<slabViewDTO>> searchByCompanyName(@RequestParam("companyName") String companyName )
            throws URISyntaxException {
        log.debug("REST request to search slab : {}", companyName);
        List<slabViewDTO> slabViewDTOList = new ArrayList<>();

        List<slabDTO> slabDTOList = slabService.findByLeagalName(companyName);
        List<Company> companies = companyRepository.findAll();

        Map<String, List<slabDTO>> slabDTOMap = slabDTOList.stream()
                .collect(Collectors.groupingBy(slab -> slab.getCompanyPid()));

        for (Map.Entry<String, List<slabDTO>> entry : slabDTOMap.entrySet()) {
            String companyPid = entry.getKey();
            List<slabDTO> slabDTOs = entry.getValue();

            Optional<Company> Company = companies.stream()
                    .filter(company -> company.getPid().equals(companyPid)).findAny();

            slabViewDTO slabView = new slabViewDTO();
            slabView.setCompanyPid(companyPid);
            if(Company.isPresent()){
                slabView.setLegalName(Company.get().getLegalName());
            }
            slabView.setSlabDTos(slabDTOs);
            slabViewDTOList.add(slabView);
        }

        return new ResponseEntity<>(slabViewDTOList, HttpStatus.OK);
    }
}
