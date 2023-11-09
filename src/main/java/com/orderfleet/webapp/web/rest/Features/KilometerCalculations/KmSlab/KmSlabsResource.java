package com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlab;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.AccountGroupResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/web")
//@RestController
//@RequestMapping("/api")
public class KmSlabsResource {

    private final Logger log = LoggerFactory.getLogger(AccountGroupResource.class);
    private final Logger logger = LoggerFactory.getLogger("QueryFormatting");

    @Inject
    private KmSlabsService kmSlabsService;

    @Inject
    private CompanyService companyService;

    @Inject
    private UserService userService;

    /**
     *
     * @param model
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/km-slab", method = RequestMethod.GET)
    public String getAllActivities(Model model) throws URISyntaxException {
        model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
        model.addAttribute("users", userService.findAllByCompany());
        log.debug("Web request to get a page of Activities");
        return "company/km-slabs";
    }

    /**
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/km-slab/salbs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Timed
    public ResponseEntity<?> getAllKmSlabs(HttpServletRequest request){
        log.debug("Request to Save Kilometer Slabs....");
        List<KmSlabDTO> Response = new ArrayList<>();
        try{
           Response =  kmSlabsService.getAllSlabs();
        }catch (Exception e){
            return new ResponseEntity<>(Response, HttpStatus.OK);
        }
        return new ResponseEntity<>(Response, HttpStatus.OK);
    }

    /**
     *
     * @param kmSlabDTO
     */
    @RequestMapping(value = "/km-slab/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Timed
    public void saveKmSlabs(@Valid @RequestBody KmSlabDTO kmSlabDTO){
        log.debug(" Request to Save Kilometer Slabs.... " + kmSlabDTO.toString());
        kmSlabsService.saveKmSlab(kmSlabDTO);
    }

    /**
     *
     * @param slabPid
     */

    @RequestMapping(value = "/km-slab/delete/{slabPid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteKmSlabs(@PathVariable String slabPid){
        log.debug(" Request to Save Kilometer Slabs" + slabPid);
        kmSlabsService.DeleteKmSlab(slabPid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
