package com.orderfleet.webapp.web.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.security.AuthoritiesConstants;

/**
 * Controller for Download Log
 *
 * @author Prashob Sasidharan
 * @since April 13, 2019
 */
@RequestMapping("/web")
@Controller
public class DownloadLogResource {

	private static String FILE_NAME;

	private static String FILE_PATH;

	@GetMapping("/download-log")
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getDownloadLog(Model model) {
		return "site_admin/download-log";
	}

	@RequestMapping(value = "/downloadLog", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<InputStreamResource> downloadLog(@RequestParam("date") String sdate,
			HttpServletRequest request) throws ParseException {

		new SimpleDateFormat("yyyy-MM-dd").parse(sdate);

		FILE_NAME = "logFile." + sdate + ".log";

		FILE_PATH = System.getProperty("user.dir");

		File file = new File(FILE_PATH + "/" + FILE_NAME);
		System.out.println(file.getAbsolutePath() + "\tFile Exists:" + file.exists());
		if (file.exists()) {
			InputStreamResource res = null;
			try {
				res = new InputStreamResource(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + FILE_NAME + "\"").body(res);
		} else {
			return null;
		}
	}
}
