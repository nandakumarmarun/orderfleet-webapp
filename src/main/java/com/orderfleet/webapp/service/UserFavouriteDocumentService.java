package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.UserFavouriteDocumentDTO;

/**
 * Service Interface for managing UserFavouriteDocument.
 * 
 * @author Muhammed Riyas T
 * @since Novembor 01, 2016
 */
public interface UserFavouriteDocumentService {

	void save(List<UserFavouriteDocumentDTO> favouriteDocumentDTOs);

	List<UserFavouriteDocumentDTO> findFavouriteDocumentsByUserPid(String userPid);

	List<UserFavouriteDocumentDTO> findFavouriteDocumentsByUserIsCurrentUser();

	List<UserFavouriteDocumentDTO> findFavouriteDocumentsByUserIsCurrentUserAndLastModifiedDate(
			LocalDateTime lastModifiedDate);
	
	void copyUserFavouriteDocuments(String fromUserPid, List<String> toUserPids);

}
