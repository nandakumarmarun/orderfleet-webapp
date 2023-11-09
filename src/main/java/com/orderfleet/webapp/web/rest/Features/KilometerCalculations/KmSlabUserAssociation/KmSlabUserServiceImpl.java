package com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlabUserAssociation;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.KmSlabRepository;
import com.orderfleet.webapp.repository.KmSlabUserRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlab.KmSlab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KmSlabUserServiceImpl implements KmSlabUserService{

    private final Logger log = LoggerFactory.getLogger(KmSlabUserServiceImpl.class);

    @Inject
    private KmSlabUserRepository kmSlabUserRepository;

    @Inject
    private KmSlabRepository kmSlabRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private CompanyRepository companyRepository;


    @Override
    public List<KmSlabUserDTO> findAllByCompanyIdUserPidInKmSlabPid(String kmSlabUser) {
        log.debug("kilometre Slabs");
        Optional<Company> optCompany = companyRepository.findById(SecurityUtils.getCurrentUsersCompanyId());
        List<KmSlabUser> kmslabUser  =  new ArrayList<>();
        if(optCompany.isPresent()){
            log.debug("CompanyId : " + optCompany.get());
            kmslabUser = kmSlabUserRepository.findAllByCompanyId(optCompany.get().getId(),kmSlabUser);
        }
        List<KmSlabUserDTO> Response = kmslabUser.stream().map(ksu -> new KmSlabUserDTO(ksu))
                .collect(Collectors.toList());
        return Response;
    }

    /**
     *
     * @param kmSlabUserPid
     * @param userPids
     * @return
     */
    @Override
    public List<KmSlabUserDTO> AssociateUserSlab(String kmSlabUserPid, List<String> userPids) {
        log.debug("Associate User And Slab - { userPids : " + userPids+", kmSlabs : "+ kmSlabUserPid + "}" );
        List<KmSlabUser> kmSlabUsersList = new ArrayList<>();

        List<User> users = userRepository.findAllUserByUserPids(userPids);
        log.debug("Users : "+ users.size());
        Optional<KmSlab> optKmSlab = kmSlabRepository.findByPid(kmSlabUserPid);
        log.debug("optKmSlab : "+ optKmSlab.isPresent());
        Optional<Company> optCompany = companyRepository.findById(SecurityUtils.getCurrentUsersCompanyId());
        log.debug("optCompany : "+ optCompany.isPresent());
        if(optKmSlab.isPresent() && optCompany.isPresent()){
            log.debug("detaching Existing Associations ");

            List<Long> kmUsId =
                   kmSlabUserRepository
                           .findAllByCompanyIdAndUserPidInAndSlabPid(
                                   optCompany.get().getId(),kmSlabUserPid);

            if(kmUsId.size() != 0){
                kmSlabUserRepository.deleteAllByCompanyIdAndIdIn(optCompany.get().getId(),kmUsId);
            }

            log.debug("userIds : " + kmUsId);
            log.debug("CompanyId : " + optCompany.get().getId());
            for (User user : users){
                KmSlabUser kmSlabUser = new KmSlabUser();
                kmSlabUser.setKmSlab(optKmSlab.get());
                kmSlabUser.setUser(user);
                kmSlabUser.setCompany(optCompany.get());
                kmSlabUsersList.add(kmSlabUser);
            }
        }
        log.debug("Saving : " + kmSlabUsersList.size() );
        List<KmSlabUser> KmSlabUserResult = kmSlabUserRepository.save(kmSlabUsersList);

        List<KmSlabUserDTO> Response = KmSlabUserResult.stream().map(ksu -> new KmSlabUserDTO(ksu))
                .collect(Collectors.toList());
        return Response;
    }

}
