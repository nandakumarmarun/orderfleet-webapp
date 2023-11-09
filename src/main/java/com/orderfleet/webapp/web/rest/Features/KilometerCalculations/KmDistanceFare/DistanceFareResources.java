package com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmDistanceFare;

import com.orderfleet.webapp.domain.KilometerCalculationDenormalised;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.AccountGroupResource;
import com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlab.KmSlabsService;
import com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlabUserAssociation.KmSlabUserResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/web")
public class DistanceFareResources {

    private final Logger log = LoggerFactory.getLogger(AccountGroupResource.class);
    private final Logger logger = LoggerFactory.getLogger("QueryFormatting");

    @Inject
    private KmSlabsService kmSlabsService;

    @Inject
    private KmSlabUserResources kmSlabUserResources;

    @Inject
    private CompanyService companyService;

    @Inject
    private UserService userService;

    @Inject
    private KmDistanceFareService kmDistanceFareService;


    @RequestMapping(value = "/km-distance-fare", method = RequestMethod.GET)
    public String getAllActivities(Model model) throws URISyntaxException {
        model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
        model.addAttribute("users", userService.findAllByCompany());
        log.debug("Web request to get a page of Activities");
        return "company/kmdistancefare";
    }


    @RequestMapping(value = "/km-distance-fare/load", method = RequestMethod.GET)
    public ResponseEntity<List<KmDistanceFareDTO>> Fillter(@RequestParam LocalDate fromDate, @RequestParam String userPid){
        log.debug("from Date : " + fromDate );
        log.debug("User Pid  : " + userPid );
        Month month =  fromDate.getMonth();
        YearMonth yearMonth = YearMonth.from(fromDate);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        log.debug("Last Date : " + lastDayOfMonth);
        List<KmDistanceFareDTO> kiloKmDistanceFareDTOS = kmDistanceFareService
                .getDistanceFareAmounts(fromDate,lastDayOfMonth,userPid);
        return ResponseEntity.ok().body(kiloKmDistanceFareDTOS);
    }


}
