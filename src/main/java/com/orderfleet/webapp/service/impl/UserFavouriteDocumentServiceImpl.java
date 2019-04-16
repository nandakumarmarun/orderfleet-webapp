package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserFavouriteDocument;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.UserFavouriteDocumentRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.service.UserFavouriteDocumentService;
import com.orderfleet.webapp.web.rest.dto.UserFavouriteDocumentDTO;

@Service
@Transactional
public class UserFavouriteDocumentServiceImpl implements UserFavouriteDocumentService {

	private final Logger log = LoggerFactory.getLogger(UserFavouriteDocumentServiceImpl.class);

	@Inject
	private UserFavouriteDocumentRepository userFavouriteDocumentRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private ActivityRepository activityRepository;

	@Override
	public void save(List<UserFavouriteDocumentDTO> favouriteDocumentDTOs) {
		log.debug("Request to save User Favourite Documents");

		String userPid = favouriteDocumentDTOs.get(0).getUserPid();
		User user = userRepository.findOneByPid(userPid).get();
		List<UserFavouriteDocument> userFavouriteDocuments = new ArrayList<>();
		for (UserFavouriteDocumentDTO userFavouriteDocumentDTO : favouriteDocumentDTOs) {
			Document document = documentRepository.findOneByPid(userFavouriteDocumentDTO.getDocumentPid()).get();
			Activity activity = activityRepository.findOneByPid(userFavouriteDocumentDTO.getActivityPid()).get();
			userFavouriteDocuments.add(new UserFavouriteDocument(activity, document, user, user.getCompany(),
					userFavouriteDocumentDTO.getSortOrder()));
		}
		userFavouriteDocumentRepository.deleteByUserPid(userPid);
		userFavouriteDocumentRepository.save(userFavouriteDocuments);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserFavouriteDocumentDTO> findFavouriteDocumentsByUserPid(String userPid) {
		log.debug("Request to get all User Favourite Documents by user pid");
		List<UserFavouriteDocument> userFavouriteDocuments = userFavouriteDocumentRepository
				.findFavouriteDocumentsByUserPid(userPid);
		return userFavouriteDocuments.stream().map(UserFavouriteDocumentDTO::new)
				.collect(Collectors.toList());
	}

	@Override
	public List<UserFavouriteDocumentDTO> findFavouriteDocumentsByUserIsCurrentUser() {
		log.debug("Request to get all User Favourite Documents");
		List<UserFavouriteDocument> userFavouriteDocuments = userFavouriteDocumentRepository
				.findFavouriteDocumentsByUserIsCurrentUser();
		return userFavouriteDocuments.stream().map(UserFavouriteDocumentDTO::new).collect(Collectors.toList());
	}

	@Override
	public List<UserFavouriteDocumentDTO> findFavouriteDocumentsByUserIsCurrentUserAndLastModifiedDate(
			LocalDateTime lastModifiedDate) {
		log.debug("Request to get all User Favourite Documents");
		List<UserFavouriteDocument> userFavouriteDocuments = userFavouriteDocumentRepository
				.findFavouriteDocumentsByUserIsCurrentUserAndLastModifiedDate(lastModifiedDate);
		return userFavouriteDocuments.stream().map(UserFavouriteDocumentDTO::new)
				.collect(Collectors.toList());
	}

	@Override
	public void copyUserFavouriteDocuments(String fromUserPid, List<String> toUserPids) {
		// delete association first
		userFavouriteDocumentRepository.deleteByUserPidIn(toUserPids);
		List<UserFavouriteDocument> userFavouriteDocuments = userFavouriteDocumentRepository.findFavouriteDocumentsByUserPid(fromUserPid);
		if (userFavouriteDocuments != null && !userFavouriteDocuments.isEmpty()) {
			List<User> users = userRepository.findByUserPidIn(toUserPids);
			for (User user : users) {
				List<UserFavouriteDocument> newUserFaouriteDocuments = userFavouriteDocuments.stream()
						.map(ufd -> new UserFavouriteDocument(ufd.getActivity(),ufd.getDocument(), user,ufd.getCompany(),ufd.getSortOrder()))
						.collect(Collectors.toList());
				userFavouriteDocumentRepository.save(newUserFaouriteDocuments);
			}
		}
	}

}
