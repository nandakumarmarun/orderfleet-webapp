package com.orderfleet.webapp.web.rest;


import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.DashboardItemGroup;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/web")
public class EnableDashboardResource {


    @Inject
    private CompanyConfigurationRepository companyConfigurationRepository;

    private final Logger log = LoggerFactory.getLogger(DashboardResource.class);


    @RequestMapping(value = {"/load-dashboard" }, method = RequestMethod.GET)
    public String dashboard(Model model) throws JSONException {
        log.info("Web request to get dashboard common page");
        Long companyId = SecurityUtils.getCurrentUsersCompanyId();

      Optional<CompanyConfiguration> enableNewDashbord = companyConfigurationRepository
              .findByCompanyIdAndName(companyId, CompanyConfig.ENABLE_NEW_DASH_BOARD);

        if (enableNewDashbord.isPresent()) {
            System.out.println("dashbord present ");
            if(enableNewDashbord.get().getValue().equals("true")){
                System.out.println("loading dashbord....");
                return "redirect:/web/new-dashboard";
            }else{
                System.out.println("loading old dASHBORA");
                return "redirect:/web/home";
            }
        } else {
            return "redirect:/web/home";
        }
    }

}
