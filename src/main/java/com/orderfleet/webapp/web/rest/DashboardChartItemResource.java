package com.orderfleet.webapp.web.rest;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.orderfleet.webapp.domain.DashboardChartItem;
import com.orderfleet.webapp.domain.DashboardItem;
import com.orderfleet.webapp.domain.enums.ChartType;
import com.orderfleet.webapp.repository.DashboardChartItemRepository;
import com.orderfleet.webapp.repository.DashboardItemRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DashboardItemService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class DashboardChartItemResource {
	
	private static final String PID_PREFIX = "DCI-";

	@Inject
	private DashboardChartItemRepository dashboardChartItemRepository;
	
	@Inject
	private DashboardItemService dashboardItemService;
	
	@Inject
	private DashboardItemRepository dashboardItemRepository;
	
	@RequestMapping(value = "/dashboard-chart-items", method = RequestMethod.GET)
	@Transactional
	public String getAllDashaboardChartItems(Model model) {
		model.addAttribute("dashboardItems", dashboardItemService.findAllByCompany());
		List<DashboardChartItem> dashboardChartItems = dashboardChartItemRepository.findAllByCompanyId();
		model.addAttribute("dashboardChartItems", dashboardChartItems);
		return "company/dashboard-chart-item";
	}
	
	@RequestMapping(value = "/dashboard-chart-items", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Transactional
	public ResponseEntity<DashboardChartItem> createDashaboardChartItem(@RequestParam("name") String name, @RequestParam String dashboardItemPid) {
		if (dashboardChartItemRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("dashboardItem", "nameexists", "DashboardItem Name already in use"))
					.body(null);
		}
		Optional<DashboardItem> opDashboardItem = dashboardItemRepository.findOneByPid(dashboardItemPid);
		if(opDashboardItem.isPresent()) {
			DashboardChartItem newDashboardChartItem = new DashboardChartItem();
			newDashboardChartItem.setName(name);
			newDashboardChartItem.setDashboardItem(opDashboardItem.get());
			newDashboardChartItem.setPid(PID_PREFIX + RandomUtil.generatePid());
			newDashboardChartItem.setChartType(ChartType.BAR);
			newDashboardChartItem.setCompany(opDashboardItem.get().getCompany());
			DashboardChartItem result = dashboardChartItemRepository.save(newDashboardChartItem);
			return ResponseEntity.ok().body(result);
		} else {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("updateDashboardItem", "DashboardItemNotExist", "DashboardItem not exist")).body(null);
		}
	}
	
	@RequestMapping(value = "/dashboard-chart-items", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseEntity<DashboardChartItem> updateDashboardChartItem(@RequestParam String id, @RequestParam String name, @RequestParam String dashboardItemPid) {
		Optional<DashboardChartItem> existingDashboardChartItem = dashboardChartItemRepository.findOneByPid(id);
		if (existingDashboardChartItem.isPresent() && !(existingDashboardChartItem.get().getPid().equals(id))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("dashboardItem", "nameexists", "DashboardItem Name already in use"))
					.body(null);
		}
		Optional<DashboardItem> opDashboardItem = dashboardItemRepository.findOneByPid(dashboardItemPid);
		if(opDashboardItem.isPresent()) {
			DashboardChartItem chartItem = existingDashboardChartItem.get();
			chartItem.setName(name);
			chartItem.setDashboardItem(opDashboardItem.get());
			DashboardChartItem result = dashboardChartItemRepository.save(chartItem);
			return ResponseEntity.ok().body(result);
		} else {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dashboardItem", "DashboardItemNotExist", "DashboardItem not exist")).body(null);
		}
	}
	
	@RequestMapping(value = "/dashboard-chart-items/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseEntity<DashboardChartItem> dashboardChartItemRepository(@PathVariable String id) {
		Optional<DashboardChartItem> opDashboardChartItem = dashboardChartItemRepository.findOneByPid(id);
		if(opDashboardChartItem.isPresent()) {
			DashboardChartItem chartItem = opDashboardChartItem.get();
			chartItem.getDashboardItem().getName();
			return new ResponseEntity<>(chartItem, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);		
	}
	
	@RequestMapping(value = "/dashboard-chart-items/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseEntity<Void> deleteDashboardChartItem(@PathVariable Long id) {
		dashboardChartItemRepository.delete(id);
		return ResponseEntity.ok().build();
	}

}
