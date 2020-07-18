package com.orderfleet.webapp.web.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/web")
public class ProfitCalculatorResource {

	@RequestMapping(value = "/profit-calculator", method = RequestMethod.GET)
	public String loadPrintCalculator(){
		
		return "company/profitCalculator";
	}
}
