package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.SalesnrichInvoiceDetail;
import com.orderfleet.webapp.domain.SalesnrichInvoiceHeader;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.SalesnrichInvoiceRepository;
import com.orderfleet.webapp.service.SalesnrichInvoiceHeaderService;
import com.orderfleet.webapp.web.rest.dto.SalesnrichInvoiceHeaderDTO;

/**
 * Service Implementation for managing Salesnrich Invoice Header.
 *
 * @author Sarath
 * @since Mar 15, 2018
 *
 */
@Service
@Transactional
public class SalesnrichInvoiceHeaderServiceImpl implements SalesnrichInvoiceHeaderService {

	private final Logger log = LoggerFactory.getLogger(SalesnrichInvoiceHeaderServiceImpl.class);

	@Inject
	private SalesnrichInvoiceRepository salesnrichInvoiceRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public SalesnrichInvoiceHeaderDTO save(SalesnrichInvoiceHeaderDTO salesnrichInvoiceHeaderDTO) {
		log.debug("Request to save SalesnrichInvoice : {}", salesnrichInvoiceHeaderDTO);
		Optional<Company> opCompany = companyRepository.findOneByPid(salesnrichInvoiceHeaderDTO.getCompanyPid());
		if (opCompany.isPresent()) {
			SalesnrichInvoiceHeader salesnrichInvoice = new SalesnrichInvoiceHeader();

			salesnrichInvoice.setActiveUserCount(salesnrichInvoiceHeaderDTO.getActiveUserCount());
			salesnrichInvoice.setBillingFrom(salesnrichInvoiceHeaderDTO.getBillingFrom());
			salesnrichInvoice.setBillingTo(salesnrichInvoiceHeaderDTO.getBillingTo());
			salesnrichInvoice.setCheckedUserCount(salesnrichInvoiceHeaderDTO.getCheckedUserCount());
			salesnrichInvoice.setGstAmount(salesnrichInvoiceHeaderDTO.getGstAmount());
			salesnrichInvoice.setGstPercentage(salesnrichInvoiceHeaderDTO.getGstPercentage());
			salesnrichInvoice.setInvoiceDate(salesnrichInvoiceHeaderDTO.getInvoiceDate());
			salesnrichInvoice.setInvoiceNumber(salesnrichInvoiceHeaderDTO.getInvoiceNumber());
			salesnrichInvoice.setSubTotal(salesnrichInvoiceHeaderDTO.getSubTotal());
			salesnrichInvoice.setTotalAmount(salesnrichInvoiceHeaderDTO.getTotalAmount());
			salesnrichInvoice.setTotalUserCount(salesnrichInvoiceHeaderDTO.getTotalUserCount());

			// set company
			salesnrichInvoice.setCompany(opCompany.get());

			List<SalesnrichInvoiceDetail> salesnrichInvoiceDetails = new ArrayList<>();
			if (!salesnrichInvoiceHeaderDTO.getSalesnrichInvoiceDetailDTOs().isEmpty()) {
				salesnrichInvoiceHeaderDTO.getSalesnrichInvoiceDetailDTOs().forEach(salesnrichInvoiceDetailDTO -> {
					SalesnrichInvoiceDetail salesnrichInvoiceDetail = new SalesnrichInvoiceDetail();

					salesnrichInvoiceDetail.setParticulars(salesnrichInvoiceDetailDTO.getParticulars());
					salesnrichInvoiceDetail.setPrice(salesnrichInvoiceDetailDTO.getPrice());
					salesnrichInvoiceDetail.setQuantity(salesnrichInvoiceDetailDTO.getQuantity());
					salesnrichInvoiceDetail.setTotal(salesnrichInvoiceDetailDTO.getTotal());
					salesnrichInvoiceDetail.setCompany(opCompany.get());

					salesnrichInvoiceDetails.add(salesnrichInvoiceDetail);
				});
			}
			salesnrichInvoice.setSalesnrichInvoiceDetail(salesnrichInvoiceDetails);

			salesnrichInvoice = salesnrichInvoiceRepository.save(salesnrichInvoice);

			SalesnrichInvoiceHeaderDTO result = new SalesnrichInvoiceHeaderDTO(salesnrichInvoice);
			return result;
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SalesnrichInvoiceHeaderDTO> findAllByCompanyPid(String companyPid) {
		log.debug("Request to get all SalesnrichInvoices");
		List<SalesnrichInvoiceHeader> salesnrichInvoices = salesnrichInvoiceRepository.findAllByCompanyPid(companyPid);
		List<SalesnrichInvoiceHeaderDTO> result = salesnrichInvoices.stream().map(SalesnrichInvoiceHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<SalesnrichInvoiceHeaderDTO> getTop1SalesnrichInvoiceByInvoiceDateBetween(LocalDateTime fromDate,
			LocalDateTime toDate) {
		log.debug("Request to get last salesnrich billings using invoiceDate between", fromDate, "  ~  ", toDate);
		return salesnrichInvoiceRepository
				.getTop1SalesnrichInvoiceByInvoiceDateBetweenOrderByCreatedDateDesc(fromDate, toDate)
				.map(salesnrichInvoice -> {
					SalesnrichInvoiceHeaderDTO billingDTO = new SalesnrichInvoiceHeaderDTO(salesnrichInvoice);
					return billingDTO;
				});
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<SalesnrichInvoiceHeaderDTO> findByInvoiceNumber(Long invoiceNumber) {
		log.debug("Request to get  salesnrich billings using invoice number", invoiceNumber);
		return salesnrichInvoiceRepository.findByInvoiceNumber(invoiceNumber).map(salesnrichInvoice -> {
			SalesnrichInvoiceHeaderDTO billingDTO = new SalesnrichInvoiceHeaderDTO(salesnrichInvoice);
			return billingDTO;
		});
	}

	@Override
	@Transactional(readOnly = true)
	public Long getCountOfSalesnrichInvoiceHeader() {
		log.debug("request to get count of salesnrich invoiceheader");
		return salesnrichInvoiceRepository.getCountOfSalesnrichInvoiceHeader();

	}

	@Override
	@Transactional(readOnly = true)
	public List<SalesnrichInvoiceHeaderDTO> findAll() {
		List<SalesnrichInvoiceHeader> invoiceHeaders = salesnrichInvoiceRepository.findAll();
		List<SalesnrichInvoiceHeaderDTO> headerDTOs = invoiceHeaders.stream().map(SalesnrichInvoiceHeaderDTO::new)
				.collect(Collectors.toList());
		return headerDTOs;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<SalesnrichInvoiceHeaderDTO> findOneById(Long pid) {
		return salesnrichInvoiceRepository.findOneById(pid).map(salesnrichInvoice -> {
			SalesnrichInvoiceHeaderDTO salesnrichInvoiceHeaderDTO = new SalesnrichInvoiceHeaderDTO(salesnrichInvoice);
			return salesnrichInvoiceHeaderDTO;
		});
	}

}
