package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.BestPerformer;
import com.orderfleet.webapp.repository.BestPerformerRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.BestPerformerService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.BestPerformerUploadDTO;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;


@Service
@Transactional
public class BestPerformerServiceImpl  implements BestPerformerService {


	private final Logger log = LoggerFactory.getLogger(BestPerformerServiceImpl.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");

	@Inject
	private BestPerformerRepository companyDetailRepository;

	@Override
	@Transactional(readOnly = true)
	public Optional<BestPerformerUploadDTO> findOneByPid(String pid) {
		log.debug("Request to get Company by pid : {}", pid);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "BP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		 Optional<BestPerformerUploadDTO> bpuDTO= companyDetailRepository.findOneByPid(pid).map(company -> {
			BestPerformerUploadDTO bestPerformerDTO = new BestPerformerUploadDTO(company);
			return bestPerformerDTO;
		});
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
					return bpuDTO;

	}
	
	@Override
	public BestPerformerUploadDTO update(BestPerformerUploadDTO bestPerformerDTO) {
		log.debug("Request to Update BestPerformer : {}", bestPerformerDTO);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "BP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		BestPerformerUploadDTO bpuDTO= companyDetailRepository.findOneByPid(bestPerformerDTO.getPid()).map(company -> {
			company.setLogo(bestPerformerDTO.getLogo());
			company.setLogoContentType(bestPerformerDTO.getLogoContentType());
			company = companyDetailRepository.save(company);
			BestPerformerUploadDTO result = new BestPerformerUploadDTO(company);
			return result;
		}).orElse(null);
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
					return bpuDTO;
	}

}
