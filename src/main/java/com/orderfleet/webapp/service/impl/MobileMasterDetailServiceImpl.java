package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Bank;
import com.orderfleet.webapp.repository.BankRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.BankService;
import com.orderfleet.webapp.service.MobileMasterDetailService;
import com.orderfleet.webapp.service.MobileMasterUpdateService;
import com.orderfleet.webapp.service.util.RandomUtil;

import com.orderfleet.webapp.web.rest.dto.BankDTO;
import com.orderfleet.webapp.web.rest.mapper.BankMapper;

/**
 * Service Implementation for managing Bank.
 * 
 * @author sarath
 * @since July 27, 2016
 */
@Service
@Transactional
public class MobileMasterDetailServiceImpl implements MobileMasterDetailService {

	private final Logger log = LoggerFactory.getLogger(MobileMasterDetailServiceImpl.class);

}
