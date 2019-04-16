package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.UserForm;

/**
 * Spring Data JPA repository for the UserForm entity.
 *
 * @author Sarath
 * @since Apr 19, 2017
 *
 */
public interface UserFormRepository extends JpaRepository<UserForm, Long> {

	@Query("select userForm from UserForm userForm where userForm.company.id = ?#{principal.companyId}")
	List<UserForm> findAllByCompanyId();
	
	@Query("select userForm from UserForm userForm where userForm.company.id = ?#{principal.companyId}")
	Page<UserForm> findAllByCompanyId(Pageable pageable);

	@Query("select userForm from UserForm userForm where userForm.company.id = ?#{principal.companyId} and userForm.user.pid = ?1")
	List<UserForm> findFormByUserPid(String userPid);
	
	@Query("select userForm from UserForm userForm where userForm.user.login = ?#{principal.username} Order By userForm.sortOrder asc")
	List<UserForm> findByUserIsCurrentUser();
	
	@Query("select userForm from UserForm userForm where userForm.user.login = ?#{principal.username} and userForm.form in ?1 Order By userForm.sortOrder asc")
	List<UserForm> findByUserIsCurrentUserAndFormsIn(List<Form> forms);

	@Query("select userForm from UserForm userForm where userForm.company.id = ?#{principal.companyId} and userForm.form.pid = ?1")
	List<UserForm> findAllByFormPid(String formPid);

	@Transactional
	void deleteByUserPid(String userPid);

}
