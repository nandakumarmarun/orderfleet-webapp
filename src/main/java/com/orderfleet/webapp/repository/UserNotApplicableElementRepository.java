package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserNotApplicableElement;

/**
 * Spring Data JPA repository for the UserNotApplicableElement entity.
 * 
 * @author Muhammed Riyas T
 * @since Jan 18, 2017
 */
public interface UserNotApplicableElementRepository extends JpaRepository<UserNotApplicableElement, Long> {

	@Query("select userNotApplicableElement from UserNotApplicableElement userNotApplicableElement where userNotApplicableElement.company.id = ?#{principal.companyId}")
	List<UserNotApplicableElement> findAllByCompanyId();

	@Query("select userNotApplicableElement.formElement from UserNotApplicableElement userNotApplicableElement where userNotApplicableElement.company.id = ?#{principal.companyId} and userNotApplicableElement.document.pid = ?1 and userNotApplicableElement.form.pid = ?2 ")
	List<FormElement> findFormElementsByDocumentAndForm(String documentPid, String formPid);
	
	@Query("select userNotApplicableElement.formElement from UserNotApplicableElement userNotApplicableElement where userNotApplicableElement.company.id = ?#{principal.companyId} and userNotApplicableElement.document.pid = ?1 and userNotApplicableElement.form.pid = ?2 and userNotApplicableElement.user.pid = ?3 ")
	List<FormElement> findFormElementsByDocumentAndFormAndUser(String documentPid, String formPid, String userPid);

	@Query("select userNotApplicableElement.user from UserNotApplicableElement userNotApplicableElement where userNotApplicableElement.company.id = ?#{principal.companyId} and userNotApplicableElement.document.pid = ?1 and userNotApplicableElement.form.pid = ?2 and userNotApplicableElement.formElement.pid = ?3 ")
	List<User> findUsersByDocumentAndFormAndFormElement(String documentPid, String formPid, String formElementPid);

	@Query("select userNotApplicableElement from UserNotApplicableElement userNotApplicableElement where userNotApplicableElement.company.id = ?#{principal.companyId} and userNotApplicableElement.user.login = ?#{principal.username} ")
	List<UserNotApplicableElement> findByUserIsCurrentUser();

}
