package com.orderfleet.webapp.service.impl;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Slab;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.SlabRepository;
import com.orderfleet.webapp.service.SlabService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.slabDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SlabServiceImpl implements SlabService {

    @Inject
    SlabRepository slabRepository;

    @Inject
    private CompanyRepository companyRepository;

    public Slab slabDTOToSlab(slabDTO slabDTO) {
        if ( slabDTO == null ) {
            return null;
        }
        Slab slab = new Slab();
        slab.setMaximumUser( slabDTO.getMaximumUser() );
        slab.setMinimumUser(slabDTO.getMinimumUser());
        slab.setSlabRate(slabDTO.getSlabRate());
        return slab;
    }


    public slabDTO slabToSlabDTO(Slab slab) {
        if ( slab == null ) {
            return null;
        }
        slabDTO slabDTO = new slabDTO();
        slabDTO.setMaximumUser(slab.getMaximumUser());
        slabDTO.setMinimumUser(slab.getMinimumUser());
        slabDTO.setSlabRate(slab.getSlabRate());
        slabDTO.setCompanyLegalName(slab.getCompany().getLegalName());
        slabDTO.setPid(slab.getPid());
        slabDTO.setCompanyPid(slab.getCompany().getPid());
        return slabDTO;
    }


    @Override
    public slabDTO save(slabDTO slabDTO) {
        Optional<Company> company = companyRepository.findOneByPid(slabDTO.getCompanyPid());
        Slab slab = slabDTOToSlab(slabDTO);
        slab.setPid(SlabService.PID_PREFIX + RandomUtil.generatePid());
        slab.setCompany(company.get());
        slabDTO result = slabToSlabDTO(slabRepository.save(slab));
        return result;
    }

    @Override
    public List<slabDTO> findAllByCompanyId(Long companyid) {
        List<slabDTO> slabDTOS = new ArrayList<slabDTO>();
        List<Slab> slabs = slabRepository.findAllByCompanyId(companyid);
        slabs.forEach(data->{
                slabDTO slabDTO = slabToSlabDTO(data);
                slabDTOS.add(slabDTO);
        });
        return slabDTOS;
    }

    @Override
    public List<slabDTO> findAll() {
        List<slabDTO> slabDTOS = new ArrayList<slabDTO>();
        List<Slab> slabs = slabRepository.findAll();
        slabs.forEach(data->{
            slabDTO slabDTO = slabToSlabDTO(data);
            slabDTOS.add(slabDTO);
        });
        return slabDTOS;
    }

    @Override
    public void DeleteByPid(String Pid) {
        slabRepository.deleteByPid(Pid);
    }

    @Override
    public List<slabDTO> findByLeagalName(String legalName) {
        List<slabDTO> slabDTOS = new ArrayList<slabDTO>();
        List<Slab> slabs = slabRepository.findByCompanyName(legalName);
        slabs.forEach(data->{
            slabDTO slabDTO = slabToSlabDTO(data);
            slabDTOS.add(slabDTO);
        });
        return slabDTOS;
    }
}
