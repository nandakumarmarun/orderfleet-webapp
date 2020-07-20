package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.NewlyEditedAccountProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountNameType;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.StageNameType;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LeadManagementDTO;
import com.orderfleet.webapp.web.rest.dto.NewlyEditedAccountProfileDTO;

/**
 * Service Interface for managing AccountProfile.
 *
 * @author Muhammed Riyas T
 * @since June 02, 2016
 */
public interface NewlyEditedAccountProfileService {

	String PID_PREFIX = "NEWACCP-";

	NewlyEditedAccountProfile accountProfileToNewlyEditedAccountProfile(AccountProfileDTO accountProfileDTO, Optional<AccountProfile> exisitingAccountProfile);

	NewlyEditedAccountProfileDTO findOneByPid(String pid);

	List<NewlyEditedAccountProfileDTO> findByCompanyIdAndUserIdInAndCreatedDateBetweenOrderAndAccountStatusByCreatedDateDesc(
			Long companyId, List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate, List<AccountStatus> status);

	AccountProfileDTO newlyEditedAccountProfileToAccountProfileDTO(NewlyEditedAccountProfile newlyEditedAccountProfile);


}
