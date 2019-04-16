
package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentPrint;

/**
 * Spring Data JPA repository for the DocumentPrint entity.
 *
 * @author Sarath
 * @since Aug 12, 2017
 *
 */
public interface DocumentPrintRepository extends JpaRepository<DocumentPrint, Long> {

	Optional<DocumentPrint> findOneByPid(String pid);

	@Query("select documentPrint from DocumentPrint documentPrint where documentPrint.company.id = ?#{principal.companyId}")
	List<DocumentPrint> findAllByCompanyId();

	@Query("select documentPrint from DocumentPrint documentPrint where documentPrint.company.id = ?#{principal.companyId}")
	Page<DocumentPrint> findAllByCompanyId(Pageable pageable);

	Optional<DocumentPrint> findOneByUserPidAndActivityPidAndDocumentPid(String userpid, String activityPid,
			String documentPid);

	List<DocumentPrint> findAllByUserPidAndActivityPid(String userpid, String activityPid);

	@Query("select documentPrint.document from DocumentPrint documentPrint where documentPrint.company.id = ?#{principal.companyId} and  documentPrint.activity.pid=?1 and documentPrint.user.pid=?2 order by documentPrint.document.name asc")
	List<Document> findAllDocumentsByActivityPid(String activityPid, String userpid);

	@Query("select documentPrint from DocumentPrint documentPrint where documentPrint.company.id = ?#{principal.companyId} and documentPrint.user.login=?1")
	List<DocumentPrint> findAllByUserLogin(String userLogin);
}
