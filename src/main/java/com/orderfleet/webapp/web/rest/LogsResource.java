package com.orderfleet.webapp.web.rest;

import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.web.rest.dto.LoggerDTO;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

/**
 * Controller for view and managing Log Level at runtime.
 */
@Controller
@RequestMapping("/management/logs")
@Secured(AuthoritiesConstants.SITE_ADMIN)
public class LogsResource {

	@GetMapping
	@Timed
	public String getAll(Model model) {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		model.addAttribute("loggers",
				context.getLoggerList().stream().map(LoggerDTO::new).collect(Collectors.toList()));
		return "site_admin/logs";
	}

	/*@GetMapping
	@Timed
	@ResponseBody
	public List<LoggerDTO> getList() {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		return context.getLoggerList().stream().map(LoggerDTO::new).collect(Collectors.toList());
	}*/

	@PutMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Timed
	@ResponseBody
	public void changeLevel(@RequestBody LoggerDTO jsonLogger) {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		context.getLogger(jsonLogger.getName()).setLevel(Level.valueOf(jsonLogger.getLevel()));
	}
}