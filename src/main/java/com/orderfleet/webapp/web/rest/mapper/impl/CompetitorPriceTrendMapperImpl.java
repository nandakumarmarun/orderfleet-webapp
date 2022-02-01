package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompetitorPriceTrend;
import com.orderfleet.webapp.domain.CompetitorProfile;
import com.orderfleet.webapp.domain.PriceTrendProduct;
import com.orderfleet.webapp.domain.PriceTrendProductGroup;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.web.rest.dto.CompetitorPriceTrendDTO;
import com.orderfleet.webapp.web.rest.mapper.CompetitorPriceTrendMapper;

@Component
public class CompetitorPriceTrendMapperImpl extends CompetitorPriceTrendMapper {

	@Override
	public CompetitorPriceTrendDTO competitorPriceTrendToCompetitorPriceTrendDTO(
			CompetitorPriceTrend competitorPriceTrend) {
		if (competitorPriceTrend == null) {
			return null;
		}

		CompetitorPriceTrendDTO competitorPriceTrendDTO = new CompetitorPriceTrendDTO();

		competitorPriceTrendDTO.setCompetitorProfilePid(competitorPriceTrendCompetitorProfilePid(competitorPriceTrend));
		competitorPriceTrendDTO
				.setPriceTrendProductName(competitorPriceTrendPriceTrendProductName(competitorPriceTrend));
		competitorPriceTrendDTO
				.setPriceTrendProductGroupName(competitorPriceTrendPriceTrendProductGroupName(competitorPriceTrend));
		competitorPriceTrendDTO
				.setPriceTrendProductGroupPid(competitorPriceTrendPriceTrendProductGroupPid(competitorPriceTrend));
		competitorPriceTrendDTO.setUserName(competitorPriceTrendUserFirstName(competitorPriceTrend));
		competitorPriceTrendDTO.setPriceTrendProductPid(competitorPriceTrendPriceTrendProductPid(competitorPriceTrend));
		competitorPriceTrendDTO
				.setCompetitorProfileName(competitorPriceTrendCompetitorProfileName(competitorPriceTrend));
		competitorPriceTrendDTO.setPid(competitorPriceTrend.getPid());
		competitorPriceTrendDTO.setPrice1(competitorPriceTrend.getPrice1());
		competitorPriceTrendDTO.setPrice2(competitorPriceTrend.getPrice2());
		competitorPriceTrendDTO.setPrice3(competitorPriceTrend.getPrice3());
		competitorPriceTrendDTO.setPrice4(competitorPriceTrend.getPrice4());
		competitorPriceTrendDTO.setPrice5(competitorPriceTrend.getPrice5());
		competitorPriceTrendDTO.setRemarks(competitorPriceTrend.getRemarks());
		competitorPriceTrendDTO.setCreatedDate(competitorPriceTrend.getCreatedDate());

		return competitorPriceTrendDTO;
	}

	public CompetitorPriceTrendDTO competitorPriceTrendToCompetitorPriceTrendDTODescription(
			CompetitorPriceTrend competitorPriceTrend) {
		if (competitorPriceTrend == null) {
			return null;
		}

		CompetitorPriceTrendDTO competitorPriceTrendDTO = new CompetitorPriceTrendDTO();

		competitorPriceTrendDTO.setCompetitorProfilePid(competitorPriceTrendCompetitorProfilePid(competitorPriceTrend));
		competitorPriceTrendDTO
				.setPriceTrendProductName(competitorPriceTrendPriceTrendProductDescription(competitorPriceTrend));
		competitorPriceTrendDTO.setPriceTrendProductGroupName(
				competitorPriceTrendPriceTrendProductGroupDescription(competitorPriceTrend));
		competitorPriceTrendDTO
				.setPriceTrendProductGroupPid(competitorPriceTrendPriceTrendProductGroupPid(competitorPriceTrend));
		competitorPriceTrendDTO.setUserName(competitorPriceTrendUserFirstName(competitorPriceTrend));
		competitorPriceTrendDTO.setPriceTrendProductPid(competitorPriceTrendPriceTrendProductPid(competitorPriceTrend));
		competitorPriceTrendDTO
				.setCompetitorProfileName(competitorPriceTrendCompetitorProfileDescription(competitorPriceTrend));
		competitorPriceTrendDTO.setPid(competitorPriceTrend.getPid());
		competitorPriceTrendDTO.setPrice1(competitorPriceTrend.getPrice1());
		competitorPriceTrendDTO.setPrice2(competitorPriceTrend.getPrice2());
		competitorPriceTrendDTO.setPrice3(competitorPriceTrend.getPrice3());
		competitorPriceTrendDTO.setPrice4(competitorPriceTrend.getPrice4());
		competitorPriceTrendDTO.setPrice5(competitorPriceTrend.getPrice5());
		competitorPriceTrendDTO.setRemarks(competitorPriceTrend.getRemarks());
		competitorPriceTrendDTO.setCreatedDate(competitorPriceTrend.getCreatedDate());

		return competitorPriceTrendDTO;
	}

	@Override
	public List<CompetitorPriceTrendDTO> competitorPriceTrendsToCompetitorPriceTrendDTOs(
			List<CompetitorPriceTrend> competitorPriceTrends) {
		if (competitorPriceTrends == null) {
			return null;
		}
		List<CompetitorPriceTrendDTO> list = new ArrayList<CompetitorPriceTrendDTO>();
		if (getCompanyCofig()) {
			for (CompetitorPriceTrend competitorPriceTrend : competitorPriceTrends) {
				list.add(competitorPriceTrendToCompetitorPriceTrendDTODescription(competitorPriceTrend));
			}
		} else {
			for (CompetitorPriceTrend competitorPriceTrend : competitorPriceTrends) {
				list.add(competitorPriceTrendToCompetitorPriceTrendDTO(competitorPriceTrend));
			}
		}
		return list;
	}

	@Override
	public CompetitorPriceTrend competitorPriceTrendDTOToCompetitorPriceTrend(
			CompetitorPriceTrendDTO competitorPriceTrendDTO) {
		if (competitorPriceTrendDTO == null) {
			return null;
		}

		CompetitorPriceTrend competitorPriceTrend = new CompetitorPriceTrend();

		competitorPriceTrend.setPid(competitorPriceTrendDTO.getPid());
		competitorPriceTrend.setPrice1(competitorPriceTrendDTO.getPrice1());
		competitorPriceTrend.setPrice2(competitorPriceTrendDTO.getPrice2());
		competitorPriceTrend.setPrice3(competitorPriceTrendDTO.getPrice3());
		competitorPriceTrend.setPrice4(competitorPriceTrendDTO.getPrice4());
		competitorPriceTrend.setPrice5(competitorPriceTrendDTO.getPrice5());
		competitorPriceTrend.setRemarks(competitorPriceTrendDTO.getRemarks());
		competitorPriceTrend.setCreatedDate(competitorPriceTrendDTO.getCreatedDate());

		return competitorPriceTrend;
	}

	@Override
	public List<CompetitorPriceTrend> competitorPriceTrendDTOsToCompetitorPriceTrends(
			List<CompetitorPriceTrendDTO> competitorPriceTrendDTOs) {
		if (competitorPriceTrendDTOs == null) {
			return null;
		}

		List<CompetitorPriceTrend> list = new ArrayList<CompetitorPriceTrend>();
		for (CompetitorPriceTrendDTO competitorPriceTrendDTO : competitorPriceTrendDTOs) {
			list.add(competitorPriceTrendDTOToCompetitorPriceTrend(competitorPriceTrendDTO));
		}

		return list;
	}

	private String competitorPriceTrendCompetitorProfilePid(CompetitorPriceTrend competitorPriceTrend) {

		if (competitorPriceTrend == null) {
			return null;
		}
		CompetitorProfile competitorProfile = competitorPriceTrend.getCompetitorProfile();
		if (competitorProfile == null) {
			return null;
		}
		String pid = competitorProfile.getPid();
		if (pid == null) {
			return null;
		}
		return pid;
	}

	private String competitorPriceTrendPriceTrendProductName(CompetitorPriceTrend competitorPriceTrend) {

		if (competitorPriceTrend == null) {
			return null;
		}
		PriceTrendProduct priceTrendProduct = competitorPriceTrend.getPriceTrendProduct();
		if (priceTrendProduct == null) {
			return null;
		}
		String name = priceTrendProduct.getName();
		if (name == null) {
			return null;
		}
//	        if(priceTrendProduct.getDescription()!=null && getCompanyCofig() && !priceTrendProduct.getDescription().equals("common")) {
//		        return priceTrendProduct.getDescription();
//		        }
		return name;
	}

	private String competitorPriceTrendPriceTrendProductDescription(CompetitorPriceTrend competitorPriceTrend) {

		if (competitorPriceTrend == null) {
			return null;
		}
		PriceTrendProduct priceTrendProduct = competitorPriceTrend.getPriceTrendProduct();
		if (priceTrendProduct == null) {
			return null;
		}
		String name = priceTrendProduct.getName();
		if (name == null) {
			return null;
		}
		if (priceTrendProduct.getDescription() != null && !priceTrendProduct.getDescription().equals("common")) {
			return priceTrendProduct.getDescription();
		}
		return name;
	}

	private String competitorPriceTrendPriceTrendProductGroupName(CompetitorPriceTrend competitorPriceTrend) {

		if (competitorPriceTrend == null) {
			return null;
		}
		PriceTrendProductGroup priceTrendProductGroup = competitorPriceTrend.getPriceTrendProductGroup();
		if (priceTrendProductGroup == null) {
			return null;
		}
		String name = priceTrendProductGroup.getName();
		if (name == null) {
			return null;
		}
//	        if(priceTrendProductGroup.getDescription()!=null && getCompanyCofig() && !priceTrendProductGroup.getDescription().equals("common")) {
//		        return priceTrendProductGroup.getDescription();
//		        }
		return name;
	}

	private String competitorPriceTrendPriceTrendProductGroupDescription(CompetitorPriceTrend competitorPriceTrend) {

		if (competitorPriceTrend == null) {
			return null;
		}
		PriceTrendProductGroup priceTrendProductGroup = competitorPriceTrend.getPriceTrendProductGroup();
		if (priceTrendProductGroup == null) {
			return null;
		}
		String name = priceTrendProductGroup.getName();
		if (name == null) {
			return null;
		}
		if (priceTrendProductGroup.getDescription() != null
				&& !priceTrendProductGroup.getDescription().equals("common")) {
			return priceTrendProductGroup.getDescription();
		}
		return name;
	}

	private String competitorPriceTrendPriceTrendProductGroupPid(CompetitorPriceTrend competitorPriceTrend) {

		if (competitorPriceTrend == null) {
			return null;
		}
		PriceTrendProductGroup priceTrendProductGroup = competitorPriceTrend.getPriceTrendProductGroup();
		if (priceTrendProductGroup == null) {
			return null;
		}
		String pid = priceTrendProductGroup.getPid();
		if (pid == null) {
			return null;
		}
		return pid;
	}

	private String competitorPriceTrendUserFirstName(CompetitorPriceTrend competitorPriceTrend) {

		if (competitorPriceTrend == null) {
			return null;
		}
		User user = competitorPriceTrend.getUser();
		if (user == null) {
			return null;
		}
		String firstName = user.getFirstName();
		if (firstName == null) {
			return null;
		}
		return firstName;
	}

	private String competitorPriceTrendPriceTrendProductPid(CompetitorPriceTrend competitorPriceTrend) {

		if (competitorPriceTrend == null) {
			return null;
		}
		PriceTrendProduct priceTrendProduct = competitorPriceTrend.getPriceTrendProduct();
		if (priceTrendProduct == null) {
			return null;
		}
		String pid = priceTrendProduct.getPid();
		if (pid == null) {
			return null;
		}
		return pid;
	}

	private String competitorPriceTrendCompetitorProfileName(CompetitorPriceTrend competitorPriceTrend) {

		if (competitorPriceTrend == null) {
			return null;
		}
		CompetitorProfile competitorProfile = competitorPriceTrend.getCompetitorProfile();
		if (competitorProfile == null) {
			return null;
		}
		String name = competitorProfile.getName();
		if (name == null) {
			return null;
		}
//	        if(competitorProfile.getDescription()!=null && getCompanyCofig() && !competitorProfile.getDescription().equals("common")) {
//		        return competitorProfile.getDescription();
//		        }
		return name;
	}

	private String competitorPriceTrendCompetitorProfileDescription(CompetitorPriceTrend competitorPriceTrend) {

		if (competitorPriceTrend == null) {
			return null;
		}
		CompetitorProfile competitorProfile = competitorPriceTrend.getCompetitorProfile();
		if (competitorProfile == null) {
			return null;
		}
		String name = competitorProfile.getName();
		if (name == null) {
			return null;
		}
		if (competitorProfile.getDescription() != null && !competitorProfile.getDescription().equals("common")) {
			return competitorProfile.getDescription();
		}
		return name;
	}
}
