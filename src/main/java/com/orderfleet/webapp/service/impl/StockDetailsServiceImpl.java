package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.service.StockDetailsService;
import com.orderfleet.webapp.web.rest.dto.StockDetailsDTO;

@Service
@Transactional
public class StockDetailsServiceImpl implements StockDetailsService{

	@Inject
	private UserStockLocationRepository userStockLocationRepository;
	
	@Inject
	private OpeningStockRepository openingStockRepository;
	
	@Override
	public List<StockDetailsDTO> findOtherStockItems(User user) {
		List<StockDetailsDTO> stockDetails = new ArrayList<StockDetailsDTO>();
		List<UserStockLocation> userStockLocation = new ArrayList<>();
		userStockLocation = userStockLocationRepository.findByUserPid(user.getPid());
		if(!userStockLocation.isEmpty()) {
			List<OpeningStock> openingStockList = openingStockRepository.findByStockLocationIn(userStockLocation.stream()
					.map(us -> us.getStockLocation()).collect(Collectors.toList()));
			for(OpeningStock op : openingStockList) {
				List<StockDetailsDTO> stockDetailsProduct = stockDetails.stream().filter(
						sd -> sd.getProductName().equals(
								op.getProductProfile().getName())).collect(Collectors.toList());
				if(stockDetailsProduct.isEmpty()) {
					StockDetailsDTO stockDetailDto = 
							new StockDetailsDTO(
								op.getQuantity(), op.getProductProfile().getName(), 0.0, op.getQuantity()) ;
							stockDetails.add(stockDetailDto);
				}
			}
		}
		return stockDetails;
	}

}
