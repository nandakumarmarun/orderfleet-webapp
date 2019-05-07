package com.orderfleet.webapp.web.rest;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.orderfleet.webapp.service.StageService;

@Controller
@RequestMapping("/web")
public class StageTargetReportResource {

	@Inject
	private StageService stageService;

	@RequestMapping(value = "/stage-target-report", method = RequestMethod.GET)
	public String getAllStages(Model model) {

		model.addAttribute("stageTargetReports", stageService.findAllStageTargetReports());
		return "company/stage/stage-target-report";
	}

}
