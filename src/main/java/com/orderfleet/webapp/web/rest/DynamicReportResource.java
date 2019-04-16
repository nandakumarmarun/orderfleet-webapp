package com.orderfleet.webapp.web.rest;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.DynamicReportDetail;
import com.orderfleet.webapp.domain.DynamicReportHeader;
import com.orderfleet.webapp.repository.DynamicReportDetailRepository;
import com.orderfleet.webapp.repository.DynamicReportHeaderRepository;

/**
 * Web controller for DynamicReportResource.
 */
@Controller
@RequestMapping("/web")
public class DynamicReportResource {

	private final Logger log = LoggerFactory.getLogger(DynamicReportResource.class);

	@Inject
	private DynamicReportHeaderRepository dynamicReportHeaderRepository;

	@Inject
	private DynamicReportDetailRepository dynamicReportDetailRepository;

	@GetMapping("/dynamic-report/{reportName}")
	@Timed
	public String getDynamicReportData(@PathVariable(name = "reportName") String reportName, Model model) {
		log.debug("Web request to get Dynamic report");
		Optional<DynamicReportHeader> optionalDRH = dynamicReportHeaderRepository
				.findOneByDynamicReportNameName(reportName);
		if (optionalDRH.isPresent()) {
			List<DynamicReportDetail> dynamicReportDetails = dynamicReportDetailRepository
					.findByDynamicReportHeaderId(optionalDRH.get().getId());
			List<List<String>> reportDetails = new ArrayList<>();
			int colCount = optionalDRH.get().getColumnCount();
			if (!dynamicReportDetails.isEmpty()) {
				try {
					// first row is heading
					model.addAttribute("colHeaders", getColumnValues(dynamicReportDetails.get(0), colCount));
					for (int i = 1; i < dynamicReportDetails.size(); i++) {
						reportDetails.add(getColumnValues(dynamicReportDetails.get(i), colCount));
					}
				} catch (Exception ex) {
					log.debug("Error reading excel data from database {}", ex);
				}
			}
			model.addAttribute("dynamicReportDetails", reportDetails);
		}
		return "company/dynamic-report";
	}

	private List<String> getColumnValues(DynamicReportDetail reportDetail, int colCount)
			throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<String> values = new ArrayList<>();
		for (int i = 1; i <= colCount; i++) {
			String field = "col" + i;
			PropertyDescriptor pd = new PropertyDescriptor(field, DynamicReportDetail.class);
			Method getter = pd.getReadMethod();
			String value = (String) getter.invoke(reportDetail);
			if (value != null && value.trim().length() > 0) {
				values.add(value);
			} else {
				values.add("."); //for UI tweak
			}
		}
		return values;
	}

}
