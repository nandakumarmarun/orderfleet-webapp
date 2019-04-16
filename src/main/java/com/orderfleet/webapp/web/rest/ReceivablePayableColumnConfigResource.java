package com.orderfleet.webapp.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.ReceivablePayableColumn;
import com.orderfleet.webapp.domain.ReceivablePayableColumnConfig;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ReceivablePayableColumnConfigRepository;
import com.orderfleet.webapp.repository.ReceivablePayableColumnRepository;
import com.orderfleet.webapp.security.SecurityUtils;

@Controller
@RequestMapping("/web")
public class ReceivablePayableColumnConfigResource {

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ReceivablePayableColumnRepository receivablePayableColumnRepository;

	@Inject
	private ReceivablePayableColumnConfigRepository receivablePayableColumnConfigRepository;

	@RequestMapping(value = "/receivable-payable-column-config", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public String getReceivablePayableColumnConfig(Model model) {
		List<ReceivablePayableColumn> rpColumns = receivablePayableColumnRepository.findAll();
		List<ReceivablePayableColumnConfig> rpColumnConfigs = receivablePayableColumnConfigRepository
				.findAllByCompanyId();
		model.addAttribute("rpColumns", rpColumns);
		model.addAttribute("rpColumnConfigs", rpColumnConfigs);
		return "company/receivable-payable-column-config";
	}

	@RequestMapping(value = "/receivable-payable-column-config/save", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<Void> save(@RequestParam String[] configColumns) {
		if (configColumns.length > 0) {
			Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
			List<ReceivablePayableColumn> rpColumns = receivablePayableColumnRepository.findAll();

			List<ReceivablePayableColumnConfig> columnConfigs = new ArrayList<>();
			for (String column : configColumns) {
				Optional<ReceivablePayableColumn> optionalColumn = rpColumns.stream()
						.filter(col -> column.equals(col.getName())).findAny();
				if (optionalColumn.isPresent()) {
					ReceivablePayableColumnConfig columnConfig = new ReceivablePayableColumnConfig();
					columnConfig.setReceivablePayableColumn(optionalColumn.get());
					columnConfig.setEnabled(true);
					columnConfig.setCompany(company);
					columnConfigs.add(columnConfig);
				}
			}
			if (!columnConfigs.isEmpty()) {
				// delete
				receivablePayableColumnConfigRepository.deleteByCompanyId(company.getId());
				receivablePayableColumnConfigRepository.flush();
				receivablePayableColumnConfigRepository.save(columnConfigs);
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
