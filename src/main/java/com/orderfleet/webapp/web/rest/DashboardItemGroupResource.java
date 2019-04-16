package com.orderfleet.webapp.web.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.DashboardGroupDashboardItem;
import com.orderfleet.webapp.domain.DashboardItem;
import com.orderfleet.webapp.domain.DashboardItemGroup;
import com.orderfleet.webapp.domain.DashboardItemGroupUser;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DashboardGroupDashboardItemRepository;
import com.orderfleet.webapp.repository.DashboardItemGroupRepository;
import com.orderfleet.webapp.repository.DashboardItemGroupUserRepository;
import com.orderfleet.webapp.repository.DashboardItemRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DashboardItemService;
import com.orderfleet.webapp.web.rest.dto.DashboardItemDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class DashboardItemGroupResource {

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DashboardItemGroupRepository dashboardItemGroupRepository;

	@Inject
	private DashboardItemService dashboardItemService;

	@Inject
	private DashboardItemRepository dashboardItemRepository;

	@Inject
	private DashboardGroupDashboardItemRepository dashboardGroupDashboardItemRepository;
	
	@Inject
	private DashboardItemGroupUserRepository dashboardItemGroupUserRepository;
	
	@Inject
	private UserRepository userRepository;

	@RequestMapping(value = "/dashboard-item-groups", method = RequestMethod.GET)
	public String getAllDashboardItems(Model model) {
		model.addAttribute("dashboardItemGroups", dashboardItemGroupRepository.findAllByCompanyId());
		model.addAttribute("dashboardItems", dashboardItemService.findAllByCompany());
		model.addAttribute("users", userRepository.findByUserPropertyCompanyId());
		return "company/dashboard/dashboard-item-group";
	}

	@RequestMapping(value = "/dashboard-item-groups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<DashboardItemGroup> createDashboardItemGroup(@RequestParam("name") String name,
			@RequestParam("sortOrder") int sortOrder, @RequestParam boolean appearInDbSummary) {
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		if (dashboardItemGroupRepository.findByCompanyIdAndNameIgnoreCase(companyId, name).isPresent()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dashboardItemGroup", "nameexists",
					"Dashboard Item Group Name already in use")).body(null);
		}
		DashboardItemGroup dbItemGroup = new DashboardItemGroup();
		dbItemGroup.setName(name);
		dbItemGroup.setSortOrder(sortOrder);
		dbItemGroup.setAppearInDashboardSummary(appearInDbSummary);
		// set company
		dbItemGroup.setCompany(companyRepository.findOne(companyId));
		DashboardItemGroup result = dashboardItemGroupRepository.save(dbItemGroup);
		return ResponseEntity.ok().body(result);
	}

	@RequestMapping(value = "/dashboard-item-groups", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DashboardItemGroup> updateDashboardItemGroup(@RequestParam Long id, @RequestParam String name,
			@RequestParam int sortOrder, @RequestParam boolean appearInDbSummary) {
		Optional<DashboardItemGroup> existingDashboardItemGroup = dashboardItemGroupRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name);
		if (existingDashboardItemGroup.isPresent() && (existingDashboardItemGroup.get().getId() != id)) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dashboardItemGroup", "nameexists",
					"Dashboard Item Group Name already in use")).body(null);
		}
		DashboardItemGroup dbItemGroup = dashboardItemGroupRepository.findOne(id);
		if (dbItemGroup != null) {
			dbItemGroup.setName(name);
			dbItemGroup.setSortOrder(sortOrder);
			dbItemGroup.setAppearInDashboardSummary(appearInDbSummary);
			dbItemGroup = dashboardItemGroupRepository.save(dbItemGroup);
		}
		return ResponseEntity.ok().body(dbItemGroup);
	}

	@RequestMapping(value = "/dashboard-item-groups/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DashboardItemGroup> getDashboardItemGroup(@PathVariable Long id) {
		DashboardItemGroup dashboardItemGroup = dashboardItemGroupRepository.findOne(id);
		if (dashboardItemGroup == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(dashboardItemGroup, HttpStatus.OK);
	}

	@RequestMapping(value = "/dashboard-item-groups/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseEntity<Void> deleteDashboardItemGroup(@PathVariable Long id) {
		dashboardItemGroupRepository.delete(id);
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = "/dashboard-item-groups/findDashboardItems/{groupId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DashboardItemDTO>> getDashboardItems(@PathVariable Long groupId) {
		List<DashboardItem> dashboardItems = dashboardGroupDashboardItemRepository
				.findDashboardItemByDashboardGroupId(groupId);
		List<DashboardItemDTO> dashboardItemDTOs = dashboardItems.stream().map(di -> {
			DashboardItemDTO dashboardItemDTO = new DashboardItemDTO();
			dashboardItemDTO.setPid(di.getPid());
			dashboardItemDTO.setName(di.getName());
			return dashboardItemDTO;
		}).collect(Collectors.toList());
		return new ResponseEntity<>(dashboardItemDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/dashboard-item-groups/assign-dashboard-items", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<Void> saveAssignedDashboardItems(@RequestParam Long id,
			@RequestParam String[] assignedDashboardItemPids) {
		DashboardItemGroup dbItemGroup = dashboardItemGroupRepository.findOne(id);
		List<DashboardItem> dashboardItems = dashboardItemRepository
				.findByPidIn(Arrays.asList(assignedDashboardItemPids));
		if (dbItemGroup != null) {
			dashboardGroupDashboardItemRepository.deleteByDashboardItemGroupId(dbItemGroup.getId());
			if (!dashboardItems.isEmpty()) {
				Long companyId = SecurityUtils.getCurrentUsersCompanyId();
				List<DashboardGroupDashboardItem> dgDashboardItems = new ArrayList<>();
				for (String diPid : assignedDashboardItemPids) {
					DashboardGroupDashboardItem dgdi = new DashboardGroupDashboardItem();
					Optional<DashboardItem> opDashboardItem = dashboardItems.stream()
							.filter(s -> s.getPid().equals(diPid)).findAny();
					if (opDashboardItem.isPresent()) {
						dgdi.setDashboardItem(opDashboardItem.get());
						dgdi.setDashboardItemGroup(dbItemGroup);
						dgdi.setCompanyId(companyId);
						dgDashboardItems.add(dgdi);
					}
				}
				dashboardGroupDashboardItemRepository.save(dgDashboardItems);
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/dashboard-item-groups/users/{id}", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<List<String>> getAssignedUsers(@PathVariable Long id) {
		List<String> users = dashboardItemGroupUserRepository.findUserPidsByDashboardItemGroupId(id);
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	@RequestMapping(value = "/dashboard-item-groups/assign-users", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<Void> saveAssignedUsers(@RequestParam Long id, String[] assignedUserPids) {
		DashboardItemGroup dbItemGroup = dashboardItemGroupRepository.findOne(id);
		List<User> users = userRepository.findByPidIn(Arrays.asList(assignedUserPids));
		if (dbItemGroup != null) {
			Company company = dbItemGroup.getCompany();
			dashboardItemGroupUserRepository.deleteByDashboardItemGroupId(dbItemGroup.getId());
			if (!users.isEmpty()) {
				List<DashboardItemGroupUser> digUsers = new ArrayList<>();
				for (String userPid : assignedUserPids) {
					DashboardItemGroupUser digUser = new DashboardItemGroupUser();
					Optional<User> opUser = users.stream()
							.filter(u -> u.getPid().equals(userPid)).findAny();
					if (opUser.isPresent()) {
						digUser.setDashboardItemGroup(dbItemGroup);
						digUser.setUser(opUser.get());
						digUser.setCompany(company);
						digUsers.add(digUser);
					}
				}
				dashboardItemGroupUserRepository.save(digUsers);
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
