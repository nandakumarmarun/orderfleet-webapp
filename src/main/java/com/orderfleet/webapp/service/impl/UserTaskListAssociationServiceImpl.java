package com.orderfleet.webapp.service.impl;

import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserTaskListAssociation;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserTaskListAssociationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.UserTaskListTaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserTaskListAssociationServiceImpl implements UserTaskListAssociationService {

	private final Logger log = LoggerFactory.getLogger(UserTaskListAssociationServiceImpl.class);

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private TaskRepository taskRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private TaskListRepository taskListRepository;

	@Inject
	private UserTaskListAssociationRepository userTaskListAssociationRepository;

	@Override
	public UserTaskListTaskDTO save(UserTaskListTaskDTO userTaskListAssignmentDTO) {
		// TODO Auto-generated method stub

		userTaskListAssignmentDTO.setPid(UserTaskListAssociationService.PID_PREFIX + RandomUtil.generatePid());

		UserTaskListAssociation userTaskList = new UserTaskListAssociation();
		Optional<User> users = userRepository.findOneByPid(userTaskListAssignmentDTO.getUserPid());
		Optional<TaskList> taskList = taskListRepository.findOneByPid(userTaskListAssignmentDTO.getTaskListPid());

		userTaskList.setUser(users.get());
		userTaskList.setTaskListId(taskList.get().getPid());
		userTaskList.setTaskListName(taskList.get().getName());

		userTaskList.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		userTaskList.setPid(userTaskListAssignmentDTO.getPid());

		UserTaskListAssociation result = userTaskListAssociationRepository.save(userTaskList);

		log.info("Result :" + result);

		UserTaskListTaskDTO taskListTaskDTO = new UserTaskListTaskDTO();

		taskListTaskDTO.setPid(userTaskList.getPid());
		taskListTaskDTO.setUserName(userTaskList.getUser().getFirstName());
		taskListTaskDTO.setUserPid(userTaskList.getUser().getPid());
		taskListTaskDTO.setTaskListName(userTaskList.getTaskListName());
		taskListTaskDTO.setTaskListPid(userTaskList.getTaskListId());

		return taskListTaskDTO;
	}

	@Override
	public List<UserTaskListTaskDTO> getAllTasksByEmployee(String userPid) {
		// TODO Auto-generated method stub

		Optional<User> users = userRepository.findOneByPid(userPid);

		List<UserTaskListAssociation> userTaskList = userTaskListAssociationRepository
				.findAllByUserId(users.get().getId());

		List<TaskList> tasklist = taskListRepository.findAllByCompanyId();

//		List<Long> taskListId = userTaskList.stream().map(ust -> ust.getTaskList().getId())
//				.collect(Collectors.toList());
//
//		List<TaskList> tasklist = taskListRepository.findAllByTaskListIdIn(taskListId);

		System.out.println("Size :" + userTaskList.size());
		userTaskList.stream().forEach(a -> System.out.println("Pid :" + a.getPid()));

		List<UserTaskListTaskDTO> userTaskDTO = new ArrayList<>();
		for (UserTaskListAssociation ust : userTaskList) {
			UserTaskListTaskDTO userTaskListDTO = new UserTaskListTaskDTO();

			userTaskListDTO.setPid(ust.getPid());
			userTaskListDTO.setUserName(ust.getUser().getFirstName());
			userTaskListDTO.setUserPid(ust.getUser().getPid());
			Optional<TaskList> taskList = tasklist.stream().filter(tl -> tl.getPid().equals(ust.getTaskListId()))
					.findAny();
			if (taskList.isPresent()) {
				userTaskListDTO.setTaskList(taskList.get());
			}
			
			userTaskDTO.add(userTaskListDTO);

		}

		userTaskDTO.stream().forEach(abc -> log.info("usertask****************:" + abc.getTaskList().getTasks()));
		return userTaskDTO;
	}

	@Override
	public void delete(String pid) {

		log.debug("Request to delete UserTaskListAssignment : {}", pid);
		userTaskListAssociationRepository.findOneByPid(pid).ifPresent(userTaskListAssociation -> {
			userTaskListAssociationRepository.delete(userTaskListAssociation.getId());
		});

	}

}
