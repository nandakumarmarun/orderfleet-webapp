
package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.PostDatedVoucher;

/**
 * @author Anish
 * @since September 3, 2018
 *
 */
public interface PostDatedVoucherRepository extends JpaRepository<PostDatedVoucher, Long> {

	@Query("select postDatedVoucher from PostDatedVoucher postDatedVoucher where postDatedVoucher.company.id = ?#{principal.companyId}")
	List<PostDatedVoucher> findAllByCompanyId();
	
	List<PostDatedVoucher> findAllByAccountProfilePid(String accountPid);

	List<PostDatedVoucher> findAllByCompanyIdAndReferenceDocumentNumberIn(long companyId ,List<String> referenceDocumentNumbers);
	
	@Transactional
	Long deleteByCompanyId(Long companyId);
	
	
}
