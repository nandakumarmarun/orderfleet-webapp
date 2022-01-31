package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.mapper.PriceLevelMapper;

@Component
public class PriceLevelMapperImpl extends PriceLevelMapper {

	@Override
	public PriceLevelDTO priceLevelToPriceLevelDTO(PriceLevel priceLevel) {
		if (priceLevel == null) {
			return null;
		}

		PriceLevelDTO priceLevelDTO = new PriceLevelDTO();

		priceLevelDTO.setPid(priceLevel.getPid());
		priceLevelDTO.setName(priceLevel.getName());
		priceLevelDTO.setAlias(priceLevel.getAlias());
		priceLevelDTO.setDescription(priceLevel.getDescription());
		priceLevelDTO.setActivated(priceLevel.getActivated());
		priceLevelDTO.setSortOrder(priceLevel.getSortOrder());
		priceLevelDTO.setLastModifiedDate(priceLevel.getLastModifiedDate());

		return priceLevelDTO;
	}

	public PriceLevelDTO priceLevelToPriceLevelDTODescription(PriceLevel priceLevel) {
		if (priceLevel == null) {
			return null;
		}

		PriceLevelDTO priceLevelDTO = new PriceLevelDTO();

		priceLevelDTO.setPid(priceLevel.getPid());
		priceLevelDTO
				.setName(priceLevel.getDescription() != null && !priceLevel.getDescription().equalsIgnoreCase("common")
						? priceLevel.getDescription()
						: priceLevel.getName());
		priceLevelDTO.setAlias(priceLevel.getAlias());
		priceLevelDTO.setDescription(priceLevel.getDescription());
		priceLevelDTO.setActivated(priceLevel.getActivated());
		priceLevelDTO.setSortOrder(priceLevel.getSortOrder());
		priceLevelDTO.setLastModifiedDate(priceLevel.getLastModifiedDate());

		return priceLevelDTO;
	}

	@Override
	public List<PriceLevelDTO> priceLevelsToPriceLevelDTOs(List<PriceLevel> priceLevels) {
		if (priceLevels == null) {
			return null;
		}

		List<PriceLevelDTO> list = new ArrayList<PriceLevelDTO>();
		if (getCompanyCofig()) {
			for (PriceLevel priceLevel : priceLevels) {
				list.add(priceLevelToPriceLevelDTODescription(priceLevel));
			}
		} else {
			for (PriceLevel priceLevel : priceLevels) {
				list.add(priceLevelToPriceLevelDTO(priceLevel));
			}
		}

		return list;
	}

	@Override
	public PriceLevel priceLevelDTOToPriceLevel(PriceLevelDTO priceLevelDTO) {
		if (priceLevelDTO == null) {
			return null;
		}

		PriceLevel priceLevel = new PriceLevel();

		priceLevel.setPid(priceLevelDTO.getPid());
		priceLevel.setName(priceLevelDTO.getName());
		priceLevel.setAlias(priceLevelDTO.getAlias());
		priceLevel.setDescription(priceLevelDTO.getDescription());
		priceLevel.setActivated(priceLevelDTO.getActivated());
		priceLevel.setSortOrder(priceLevelDTO.getSortOrder());

		return priceLevel;
	}

	@Override
	public List<PriceLevel> priceLevelDTOsToPriceLevels(List<PriceLevelDTO> priceLevelDTOs) {
		if (priceLevelDTOs == null) {
			return null;
		}

		List<PriceLevel> list = new ArrayList<PriceLevel>();
		for (PriceLevelDTO priceLevelDTO : priceLevelDTOs) {
			list.add(priceLevelDTOToPriceLevel(priceLevelDTO));
		}

		return list;
	}

	private String priceLevelName(PriceLevel priceLevel) {
		if (priceLevel.getDescription() != null && getCompanyCofig() && !priceLevel.getDescription().equals("common")) {
			return priceLevel.getDescription();
		}

		return priceLevel.getName();
	}
}
