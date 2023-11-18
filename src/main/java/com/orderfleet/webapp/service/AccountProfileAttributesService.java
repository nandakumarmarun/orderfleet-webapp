package com.orderfleet.webapp.service;

import com.orderfleet.webapp.web.rest.dto.AccountProfileAttributesDTO;

import java.util.List;

public interface AccountProfileAttributesService {
    List<AccountProfileAttributesDTO>getAccountProfileAttributes(AccountProfileAttributesDTO accountProfileAttributesDTO);
    List<AccountProfileAttributesDTO>getAccountProfileAttributesByAccountProfilePid(String accountProfilePid);


}
