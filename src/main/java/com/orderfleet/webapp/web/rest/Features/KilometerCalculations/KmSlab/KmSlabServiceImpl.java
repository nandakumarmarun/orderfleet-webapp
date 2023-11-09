package com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlab;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.KmSlabRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlabUserAssociation.KmSlabUserDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class KmSlabServiceImpl implements KmSlabsService {

    @Inject
    private KmSlabRepository kmSlabRepository;

    @Inject
    private CompanyRepository companyRepository;

    @Override
    public List<KmSlabDTO> getAllSlabs() {
        Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
        List<KmSlab> kmslabs = kmSlabRepository.findAllByCompanyId(company.getId());
        List<KmSlabDTO> Response = kmslabs.stream().map(ksu -> new KmSlabDTO(ksu))
                .collect(Collectors.toList());
        return Response;
    }

    @Override
    public KmSlabDTO saveKmSlab(KmSlabDTO kmSlabDTO) {
        System.out.println("Kilometer Slab : " + kmSlabDTO );
        Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
        KmSlab kmSlab = new KmSlab();
        kmSlab.setSlabName(kmSlabDTO.getMinKm() + "-" +kmSlabDTO.getMaxKm());
        kmSlab.setPid(PID_PREFIX + RandomUtil.generatePid());
        kmSlab.setAmount(kmSlabDTO.getSlabRate());
        kmSlab.setMinKm(kmSlabDTO.getMinKm());
        kmSlab.setMaxKm(kmSlabDTO.getMaxKm());
        kmSlab.setCompany(company);
        KmSlab result = kmSlabRepository.save(kmSlab);
        return new KmSlabDTO(result);
    }

    @Override
    public void DeleteKmSlab(String pid) {
        System.out.println("Pid : "+ pid );
        kmSlabRepository.deleteByPid(pid);
    }
}
