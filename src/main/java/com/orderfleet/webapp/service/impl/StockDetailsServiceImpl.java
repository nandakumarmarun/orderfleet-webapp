package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductNameTextSettings;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.ProductNameTextSettingsRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.StockDetailsService;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.StockDetailsDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductProfileMapper;

@Service
@Transactional
public class StockDetailsServiceImpl implements StockDetailsService {

	@Inject
	private UserStockLocationRepository userStockLocationRepository;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private ProductProfileMapper productProfileMapper;

	@Inject
	private ProductNameTextSettingsRepository productNameTextSettingsRepository;

	private final Logger log = LoggerFactory.getLogger(InventoryVoucherHeaderServiceImpl.class);

	@Override
	public List<StockDetailsDTO> findOtherStockItems(User user) {
		List<StockDetailsDTO> stockDetails = new ArrayList<StockDetailsDTO>();
		List<UserStockLocation> userStockLocation = new ArrayList<>();
		userStockLocation = userStockLocationRepository.findByUserPid(user.getPid());
		if (!userStockLocation.isEmpty()) {
			List<OpeningStock> openingStockList = openingStockRepository.findByStockLocationIn(
					userStockLocation.stream().map(us -> us.getStockLocation()).collect(Collectors.toList()));
			for (OpeningStock op : openingStockList) {
				List<StockDetailsDTO> stockDetailsProduct = stockDetails.stream()
						.filter(sd -> sd.getProductName().equals(op.getProductProfile().getName()))
						.collect(Collectors.toList());
				if (stockDetailsProduct.isEmpty()) {
					StockDetailsDTO stockDetailDto = new StockDetailsDTO(op.getQuantity(),
							op.getProductProfile().getName(), 0.0, op.getQuantity());
					stockDetails.add(stockDetailDto);
				}
			}
		}

		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyIdActivatedTrue();
		List<ProductProfileDTO> productProfileDtos = productProfileMapper
				.productProfilesToProductProfileDTOs(productProfiles);
		List<ProductNameTextSettings> productNameTextSettings = productNameTextSettingsRepository
				.findAllByCompanyIdAndEnabledTrue(SecurityUtils.getCurrentUsersCompanyId());

		for (StockDetailsDTO stockDetailsDTO : stockDetails) {

			Optional<ProductProfileDTO> opProductProfile = productProfileDtos.stream()
					.filter(ppDto -> ppDto.getName().equalsIgnoreCase(stockDetailsDTO.getProductName())).findAny();

			if (opProductProfile.isPresent()) {
				ProductProfileDTO productProfileDTO=opProductProfile.get();
				if (productNameTextSettings.size() > 0) {
						String name = " (";
						for (ProductNameTextSettings productNameText : productNameTextSettings) {
							if (productNameText.getName().equals("DESCRIPTION")) {
								if (productProfileDTO.getDescription() != null
										&& !productProfileDTO.getDescription().isEmpty())
									name += productProfileDTO.getDescription() + ",";
							} else if (productNameText.getName().equals("MRP")) {
								name += productProfileDTO.getMrp() + ",";
							} else if (productNameText.getName().equals("SELLING RATE")) {
								name += productProfileDTO.getPrice() + ",";
							} else if (productNameText.getName().equals("STOCK")) {
								name += stockDetailsDTO.getOpeningStock() + ",";
							} else if (productNameText.getName().equals("PRODUCT DESCRIPTION")) {
								if (productProfileDTO.getProductDescription() != null
										&& !productProfileDTO.getProductDescription().isEmpty())
									name += productProfileDTO.getProductDescription() + ",";
							} else if (productNameText.getName().equals("BARCODE")) {
								if (productProfileDTO.getBarcode() != null && !productProfileDTO.getBarcode().isEmpty())
									name += productProfileDTO.getBarcode() + ",";
							} else if (productNameText.getName().equals("REMARKS")) {
								if (productProfileDTO.getRemarks() != null && !productProfileDTO.getRemarks().isEmpty())
									name += productProfileDTO.getRemarks() + ",";
							}
						}
						name = name.substring(0, name.length() - 1);
						if (name.length() > 1) {
							name += ")";
						}
						stockDetailsDTO.setProductName(stockDetailsDTO.getProductName() + name);
						
					
				} else {
					log.info("Product Profile Size < 0");
					stockDetailsDTO.setProductName(stockDetailsDTO.getProductName());
				}
			} else {
				log.info("Optional Product Profile not present");
				stockDetailsDTO.setProductName(stockDetailsDTO.getProductName());
			}
		}

		return stockDetails;
	}

}
