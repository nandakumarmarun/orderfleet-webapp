package com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmDistanceFare;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.KilometerCalculationDenormalised;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlab.KmSlab;
import com.orderfleet.webapp.web.rest.api.AttendanceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KmDistanceFareServiceImpl implements KmDistanceFareService {

    private final Logger log = LoggerFactory.getLogger(AttendanceController.class);
    @Inject
    private KilometerCalculationDenormalisedRepository kilometerCalculationDenormalisedRepository;

    @Inject
    private KmDistanceFareRepository kmDistanceFareRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private KmSlabRepository kmSlabRepository;

    @Inject
    private KmSlabUserRepository kmSlabUserRepository;


    @Inject
    private EmployeeProfileRepository employeeProfileRepository;

    @Override
    public KmDistanceFare getDistanceFare(LocalDate plannedDate, String userPid,
                                          KilometerCalculationDenormalised kcd) {
        Optional<User> user = userRepository.findOneByPid(userPid);

        EmployeeProfile employeeProfile = null;

        log.debug("getDistanceFare");
        List<KmSlab> slabs = new ArrayList<>();

        Optional<KmDistanceFare> distnceKmDistanceFare = null;
        if(user.isPresent()){
            employeeProfile = employeeProfileRepository.findEmployeeProfileByUser(user.get());
            distnceKmDistanceFare = kmDistanceFareRepository
                    .findByCompanyIdAndUserPidAndPlannedDate(
                            user.get().getCompany().getId(),userPid,plannedDate);

         slabs =   kmSlabUserRepository.findAllKmSlabsByCompanyIdAndUserPidInAndSlabPid(user.get().getCompany().getId(),userPid);
        }
        KmDistanceFare kmDistanceFare;
        if(distnceKmDistanceFare != null && distnceKmDistanceFare.isPresent()){
            log.debug("DistanceFare : " + distnceKmDistanceFare.isPresent());
            kmDistanceFare = distnceKmDistanceFare.get();
            kmDistanceFare.setTotalDistance(kmDistanceFare.getTotalDistance() + kcd.getKilometre());
            kmDistanceFare.setEndPoint(kcd.getAccountProfileName());
       }else{
            log.debug("DistanceFare : " + distnceKmDistanceFare.isPresent());
            kmDistanceFare = new KmDistanceFare();
            kmDistanceFare.setPid("KMDF-"+RandomUtil.generatePid());
            kmDistanceFare.setStartingPoint(kcd.getAccountProfileName());
            kmDistanceFare.setTotalDistance(kcd.getKilometre());
            kmDistanceFare.setEmployeeName(kcd.getEmployeeName());
            kmDistanceFare.setEmployeePid(kcd.getEmployeePid());
            if(user.isPresent()){
                kmDistanceFare.setUser(user.get());
               kmDistanceFare.setCompany(user.get().getCompany());
            }
           kmDistanceFare.setPlannedDate(kcd.getPunchingDate().toLocalDate());
           kmDistanceFare.setCreatedDate(LocalDateTime.now());
       }

       Optional<KmSlab> currentSlab =   slabs.stream()
               .filter(data-> kmDistanceFare.getTotalDistance()>=data.getMinKm()
                && kmDistanceFare.getTotalDistance() < data.getMaxKm()).findAny();

        if(currentSlab.isPresent()){
            log.debug("Slab Name  : "+ currentSlab.get().getSlabName());
            log.debug("Slab Total : "+ currentSlab.get().getAmount());
            kmDistanceFare.setSlabName(currentSlab.get().getSlabName());
            kmDistanceFare.setSlabTotal(currentSlab.get().getAmount());
        }

        KmDistanceFare result =  kmDistanceFareRepository.save(kmDistanceFare);
        convertToJson(result,"Saving...");
        return result;
    }

    @Override
    public List<KmDistanceFareDTO> getDistanceFareAmounts(
            LocalDate fromDate, LocalDate toDate, String employeepid) {
        log.debug("Company id  : " + SecurityUtils.getCurrentUsersCompanyId());
        log.debug("employeepid : " + employeepid);
        String userPid = employeeProfileRepository.findUserByEmployeePid(employeepid);
        log.debug("userPid : " + userPid);
        List<KmDistanceFare> userDistanceAmount =
                kmDistanceFareRepository.findAllKmDistanceFaresByCompanyIdAndUserPidAndPlannedDate(
                        SecurityUtils.getCurrentUsersCompanyId(),userPid,fromDate,toDate);
        List<KmDistanceFareDTO> Response = userDistanceAmount.stream().map(ksu -> new KmDistanceFareDTO(ksu))
                .collect(Collectors.toList());
        return Response;
    }

    public <T> void convertToJson(
            Object collection,String messagae) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            log.info(System.lineSeparator());
            String jsonString = objectMapper.writeValueAsString(collection);
            log.debug(messagae  + jsonString);
            log.info(System.lineSeparator());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectMapper getObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        return mapper;
    }
}
