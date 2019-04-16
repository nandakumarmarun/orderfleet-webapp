package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.SnrichProduct;
import com.orderfleet.webapp.domain.SnrichProductRate;
import com.orderfleet.webapp.repository.SnrichProductRateRepository;
import com.orderfleet.webapp.repository.SnrichProductRepository;
import com.orderfleet.webapp.service.SnrichProductRateService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.SnrichProductRateDTO;

@Service
@Transactional
public class SnrichProductRateServiceImpl implements SnrichProductRateService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Inject
	private SnrichProductRepository snrichProductRepository;
	
	@Inject
	private SnrichProductRateRepository snrichProductRateRepository;
	
	
	@Override
	public SnrichProductRateDTO save(SnrichProductRateDTO snrichProductRateDTO) {
		log.debug("request to save Product Rate : {}", snrichProductRateDTO);
		SnrichProductRate snrichProductRate = new SnrichProductRate();
		Optional<SnrichProduct> snrichProduct = snrichProductRepository.findOneByPid(snrichProductRateDTO.getSnrichProductPid());
		if(snrichProduct.isPresent()){
			snrichProductRate.setSnrichProduct(snrichProduct.get());
		}
		snrichProductRate.setPid(SnrichProductRateService.PID_PREFIX + RandomUtil.generatePid());
		snrichProductRate.setOrderProPaymentMode(snrichProductRateDTO.getOrderProPaymentMode());
		snrichProductRate.setRate(snrichProductRateDTO.getRate());
		return new SnrichProductRateDTO(snrichProductRateRepository.save(snrichProductRate));
	}

	
	@Override
	public SnrichProductRateDTO update(SnrichProductRateDTO snrichProductRateDTO) {
		return snrichProductRateRepository.findOneByPid(snrichProductRateDTO.getPid()).map(snrichProductRate -> {
			snrichProductRate.setOrderProPaymentMode(snrichProductRateDTO.getOrderProPaymentMode());
			Optional<SnrichProduct> snrichProduct = snrichProductRepository.findOneByPid(snrichProductRateDTO.getSnrichProductPid());
			if(snrichProduct.isPresent()){
				snrichProductRate.setSnrichProduct(snrichProduct.get());
			}
			snrichProductRate.setRate(snrichProductRateDTO.getRate());
			return new SnrichProductRateDTO(snrichProductRateRepository.save(snrichProductRate));
		}).orElse(null);
	}

	
	@Override
	public List<SnrichProductRateDTO> GetAllSnrichProductRate() {
		return snrichProductRateRepository.findAll()
				.stream().map(SnrichProductRateDTO::new).collect(Collectors.toList());
	}

	
	@Override
	public Optional<SnrichProductRateDTO> findOneByPid(String pid) {
		return snrichProductRateRepository.findOneByPid(pid).map(snrichProductRate -> {
			return new SnrichProductRateDTO(snrichProductRate);
		});
	}

	@Override
	public void delete(String pid) {
		log.debug("Request to delete SnrichProductRate : {}", pid);
		snrichProductRateRepository.findOneByPid(pid).ifPresent(snrichProductRate -> {
			snrichProductRateRepository.delete(snrichProductRate.getId());
		});
	}


}
