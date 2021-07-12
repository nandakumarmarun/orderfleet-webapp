package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.web.rest.dto.BankDTO;

/**
 * Service Interface for managing Bank.
 * 
 * @author Sarath
 * @since July 27, 2016
 */

public interface SalesLedgerService {

	String PID_PREFIX = "SALLEDGER-";

}
