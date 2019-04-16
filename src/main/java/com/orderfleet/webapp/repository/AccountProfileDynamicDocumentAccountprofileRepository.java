package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.AccountProfileDynamicDocumentAccountprofile;

/**
 * repository for AccountProfileDynamicDocumentAccountprofile.
 *
 * @author Sarath
 * @since Feb 5, 2018
 *
 */
public interface AccountProfileDynamicDocumentAccountprofileRepository
		extends JpaRepository<AccountProfileDynamicDocumentAccountprofile, Long> {

	@Query("select accDocAcc from AccountProfileDynamicDocumentAccountprofile accDocAcc where accDocAcc.company.id = ?#{principal.companyId}")
	List<AccountProfileDynamicDocumentAccountprofile> findAllByCompany();

	@Query("select accDocAcc from AccountProfileDynamicDocumentAccountprofile accDocAcc where accDocAcc.company.id = ?#{principal.companyId}")
	List<AccountProfileDynamicDocumentAccountprofile> findUniqueByCompany();

	@Query("select accDocAcc from AccountProfileDynamicDocumentAccountprofile accDocAcc where accDocAcc.company.id = ?#{principal.companyId} and accDocAcc.document.pid=?1 and accDocAcc.form.pid=?2")
	List<AccountProfileDynamicDocumentAccountprofile> findAllByDocumentPidAndFormPid(String documentPid,
			String formPid);
}
