package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.MobileMenuItem;
import com.orderfleet.webapp.repository.MobileMenuItemRepository;
import com.orderfleet.webapp.service.MobileMenuItemService;
import com.orderfleet.webapp.web.rest.dto.MobileMenuItemDTO;

/**
 *Service Implementation for managing MobileMenuItem.
 *
 * @author fahad
 * @since Jun 12, 2017
 */
@Transactional
@Service
public class MobileMenuItemServiceImpl implements MobileMenuItemService{

	@Inject
	private MobileMenuItemRepository mobileMenuItemRepository;
	
	@Override
	public MobileMenuItemDTO save(MobileMenuItemDTO mobileMenuItemDTO) {
		MobileMenuItem mobileMenuItem=new MobileMenuItem();
		mobileMenuItem.setLabel(mobileMenuItemDTO.getLabel());
		mobileMenuItem.setName(mobileMenuItemDTO.getName());
		mobileMenuItemRepository.save(mobileMenuItem);
		MobileMenuItemDTO mobileMenuItemDTO1=new MobileMenuItemDTO(mobileMenuItem);
		return mobileMenuItemDTO1;
	}

	@Override
	public MobileMenuItemDTO update(MobileMenuItemDTO mobileMenuItemDTO) {
		MobileMenuItem mobileMenuItem=new MobileMenuItem();
		mobileMenuItem.setLabel(mobileMenuItemDTO.getLabel());
		mobileMenuItem.setName(mobileMenuItemDTO.getName());
		mobileMenuItem.setId(mobileMenuItemDTO.getId());
		mobileMenuItemRepository.save(mobileMenuItem);
		MobileMenuItemDTO mobileMenuItemDTO1=new MobileMenuItemDTO(mobileMenuItem);
		return mobileMenuItemDTO1;
	}

	@Override
	public MobileMenuItemDTO findOne(Long id) {
		MobileMenuItem mobileMenuItem=mobileMenuItemRepository.findOne(id);
		MobileMenuItemDTO mobileMenuItemDTO=new MobileMenuItemDTO(mobileMenuItem);
		return mobileMenuItemDTO;
	}

	@Override
	public void delete(Long id) {
		MobileMenuItem mobileMenuItem=mobileMenuItemRepository.findOne(id);
		mobileMenuItemRepository.delete(mobileMenuItem.getId());
	}

	@Override
	public List<MobileMenuItemDTO> findAllMobileMenuItem() {
		List<MobileMenuItemDTO> mobileMenuItemDTOs=new ArrayList<>();
		List<MobileMenuItem> mobileMenuItems=mobileMenuItemRepository.findAll();
		for(MobileMenuItem mobileMenuItem:mobileMenuItems){
			MobileMenuItemDTO mobileMenuItemDTO=new MobileMenuItemDTO(mobileMenuItem);
			mobileMenuItemDTOs.add(mobileMenuItemDTO);
		}
		return mobileMenuItemDTOs;
	}

	@Override
	public Optional<MobileMenuItemDTO> findByName(String name) {
		return mobileMenuItemRepository.findByNameIgnoreCase(name)
				.map(mobileMenuItem -> {
					MobileMenuItemDTO mobileMenuItemDTO=new MobileMenuItemDTO(mobileMenuItem);
					return mobileMenuItemDTO;
				});
	}

	@Override
	public Optional<MobileMenuItemDTO> findByLabel(String label) {
		return mobileMenuItemRepository.findByLabelIgnoreCase(label)
				.map(mobileMenuItem -> {
					MobileMenuItemDTO mobileMenuItemDTO=new MobileMenuItemDTO(mobileMenuItem);
					return mobileMenuItemDTO;
				});	}

	
}
