
package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.api.dto.PostDatedVoucherDTO;

/**
 * @author Anish
 * @since September 3, 2018
 *
 */
public interface PostDatedVoucherService {

	String PID_PREFIX = "PDC-";
	
	PostDatedVoucherDTO  save(PostDatedVoucherDTO postDatedVoucher);
	List<PostDatedVoucherDTO>  saveAll(List<PostDatedVoucherDTO> postDatedVoucherDtos);
	List<PostDatedVoucherDTO> findAllPostDatedVouchers();
	List<PostDatedVoucherDTO> findAllPostDatedVoucherByAccountProfilePid(String accountPid);
	
}
