package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.UserFavouriteDocument;

/**
 * Spring Data JPA repository for the UserFavouriteDocument entity.
 * 
 * @author Muhammed Riyas T
 * @since Novembor 01, 2016
 */
public interface UserFavouriteDocumentRepository extends JpaRepository<UserFavouriteDocument, Long> {

	@Query("select userFavouriteDocument from UserFavouriteDocument userFavouriteDocument where userFavouriteDocument.user.pid = ?1 ")
	List<UserFavouriteDocument> findFavouriteDocumentsByUserPid(String userPid);

	@Query("select userFavouriteDocument from UserFavouriteDocument userFavouriteDocument where userFavouriteDocument.user.login = ?#{principal.username} order by id")
	List<UserFavouriteDocument> findFavouriteDocumentsByUserIsCurrentUser();

	void deleteByUserPid(String userPid);

	@Query("select userFavouriteDocument from UserFavouriteDocument userFavouriteDocument where userFavouriteDocument.user.login = ?#{principal.username} and userFavouriteDocument.lastModifiedDate > ?1")
	List<UserFavouriteDocument> findFavouriteDocumentsByUserIsCurrentUserAndLastModifiedDate(LocalDateTime lastModifiedDate);

	void deleteByUserPidIn(List<String> userPids);
	
	List<String> findUserPidByCompanyPid(String pid);

}
