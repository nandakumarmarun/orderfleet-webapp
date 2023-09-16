package com.orderfleet.webapp.web.rest;

import com.orderfleet.webapp.domain.Stock;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.StockCalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import java.util.List;

@Controller
@RequestMapping("/web")
public class LiveStockData {

    private final Logger log = LoggerFactory.getLogger(LiveStockData.class);

    @Inject
    private StockCalculationService stockCalculationService;


    @RequestMapping(value = "/live-stocks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String executives(Model model) {
        log.info("live Stock...");
        model.addAttribute("companyId", SecurityUtils.getCurrentUsersCompanyId());
        return "company/liveStock";
    }

    @RequestMapping(value = "/live-stocks/load-live-stocks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Stock>> getStockData(){
        List<Stock> liveStocks =
                stockCalculationService
                        .findAllLiveStocksByCompanyId(
                                SecurityUtils.getCurrentUsersCompanyId());
        return new ResponseEntity<>(liveStocks, HttpStatus.OK);
    }

}
