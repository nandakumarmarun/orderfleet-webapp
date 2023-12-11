package com.orderfleet.webapp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.KilometerCalculationsDenormalisationService;
import com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmDistanceFare.KmDistanceFare;
import com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmDistanceFare.KmDistanceFareDTO;
import com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmDistanceFare.KmDistanceFareService;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.orderfleet.webapp.web.rest.dto.KilometerCalculationDTO;
import com.orderfleet.webapp.web.rest.dto.MapDistanceApiDTO;
import com.orderfleet.webapp.web.rest.dto.MapDistanceDTO;
import com.orderfleet.webapp.web.rest.dto.PunchOutDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class KilometerCalculationsDenormalisedServiceImpl implements KilometerCalculationsDenormalisationService {

    private final Logger log = LoggerFactory.getLogger(KilometerCalculationsDenormalisedServiceImpl.class);

    @Inject
    private KilometerCalculationDenormalisedRepository  KilometerCalculationDenormalisedRepository;

    @Inject
    private GeoLocationService geoLocationService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private AttendanceRepository attendanceRepository;

    @Inject
    private CompanyConfigurationRepository companyConfigurationRepository;

    @Inject
    private EmployeeProfileRepository employeeProfileRepository;

    @Inject
    private KmDistanceFareService kmDistanceFareService;


    @Override
    @Async
    public void CalculateDistance(
            List<KilometerCalculationDTO> KiloCalDTOs,
            long companyid) {
        List<KilometerCalculationDenormalised> routeData = new ArrayList<>();
        for(KilometerCalculationDTO KiloCalDTO : KiloCalDTOs){
            KilometerCalculationDenormalised KilometerCalculationDenormalised = new  KilometerCalculationDenormalised();
            KilometerCalculationDenormalised.setKilometre(KiloCalDTO.getKilometre());
            KilometerCalculationDenormalised.setMetres(KiloCalDTO.getMetres());
            KilometerCalculationDenormalised.setUserPid(KiloCalDTO.getUserPid());
            KilometerCalculationDenormalised.setUserName(KiloCalDTO.getUserName());
            KilometerCalculationDenormalised.setStartLocation(KiloCalDTO.getLocation());
            KilometerCalculationDenormalised.setEndLocation(KiloCalDTO.getEndLocation());
            KilometerCalculationDenormalised.setTaskExecutionPid(KiloCalDTO.getTaskExecutionPid());
            KilometerCalculationDenormalised.setEmployeeName(KiloCalDTO.getEmployeeName());
            KilometerCalculationDenormalised.setEmployeePid(KiloCalDTO.getEmployeePid());
            KilometerCalculationDenormalised.setCreatedDate(LocalDate.parse(KiloCalDTO.getPunchingDate()).atTime(LocalTime.parse(KiloCalDTO.getPunchingTime())));
            KilometerCalculationDenormalised.setPunchingDate(LocalDate.parse(KiloCalDTO.getPunchingDate()).atTime(LocalTime.parse(KiloCalDTO.getPunchingTime())));
            KilometerCalculationDenormalised.setLocation(KiloCalDTO.getLocation());
            KilometerCalculationDenormalised.setAccountProfileName(KiloCalDTO.getAccountProfileName());
            KilometerCalculationDenormalised.setExeCreatedDate(KiloCalDTO.getExecreatedDate());
            KilometerCalculationDenormalised.setCompanyId(companyid);
            routeData.add(KilometerCalculationDenormalised);
        }
        log.debug(" Saveing  "+ routeData.size()  + " distances ");
        List<KilometerCalculationDenormalised> result =
                KilometerCalculationDenormalisedRepository.save(routeData);
        log.debug(" Saved Compeleted " +  result.size());
    }


    /**
     *
     * @param tsTransactionWrapper
     * @param user
     */
    @Async
    @Override
    public void calculateDistanceOnCall(
            ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper,
            User user) {
        LocalDateTime fromDate = LocalDate.now().atTime(0, 0);
        LocalDateTime toDate = LocalDate.now().atTime(23, 59);
        log.debug("from Date :  " + fromDate );
        log.debug("To Date   :  " + toDate );
        log.debug("user.getCompany().getPid()");
        MapDistanceApiDTO distanceApiJson = null;
        MapDistanceDTO distance = null;
        ExecutiveTaskExecution executiveTaskExecution = null;
        KilometerCalculationDenormalised  KilometerCalculationDenormalised = new  KilometerCalculationDenormalised();

        Optional<EmployeeProfile> employee =
                employeeProfileRepository.findByUserPid(user.getPid());

        Optional<CompanyConfiguration> optDistanceTraveled =
                companyConfigurationRepository
                        .findByCompanyPidAndName(
                                user.getCompany().getPid(), CompanyConfig.KILO_CALC);

            if (optDistanceTraveled.isPresent() ) {
                if (Boolean.valueOf(optDistanceTraveled.get().getValue())) {
                    log.info("Update Distance travelled : " );

                    if(tsTransactionWrapper != null
                            && tsTransactionWrapper.getExecutiveTaskExecution() != null
                            && !tsTransactionWrapper.getExecutiveTaskExecution()
                            .getActivity().getKmCalculationDisabled())
                    {
                        log.info("Activity Kilometer Enabled : " + tsTransactionWrapper.getExecutiveTaskExecution()
                                .getActivity().getKmCalculationDisabled());
                        Optional<KilometerCalculationDenormalised> OptionalOrigin =
                                KilometerCalculationDenormalisedRepository
                                        .findTop1ByTaskExecutionPid(
                                                user.getCompany().getId(),fromDate,toDate,user.getPid());

                        executiveTaskExecution= tsTransactionWrapper.getExecutiveTaskExecution();

                        if(OptionalOrigin.isPresent()){
                            String Origin = OptionalOrigin.get().getLatitude() +","+OptionalOrigin.get().getLongitude();
                            String destination = tsTransactionWrapper.getExecutiveTaskExecution().getLatitude() +","+tsTransactionWrapper.getExecutiveTaskExecution().getLongitude();

                            log.debug("Origin : " + OptionalOrigin.get().getAccountProfileName() + " : " + Origin );
                            log.debug("destination : " + tsTransactionWrapper.getExecutiveTaskExecution().getAccountProfile().getName() + " : " + destination );

                            distanceApiJson = geoLocationService.findDistance(Origin, destination);
                            if (distanceApiJson != null && !distanceApiJson.getRows().isEmpty()) {
                                distance = distanceApiJson.getRows().get(0).getElements().get(0).getDistance();
                                if (distance != null) {
                                    log.debug("Attendance latitude and longitudes are accurate ");
                                    KilometerCalculationDenormalised.setKilometre(distance.getValue() * 0.001);
                                    KilometerCalculationDenormalised.setMetres(distance.getValue());
                                    KilometerCalculationDenormalised.setUserPid(executiveTaskExecution.getUser().getPid());
                                    KilometerCalculationDenormalised.setUserName(executiveTaskExecution.getUser().getFirstName());
                                    KilometerCalculationDenormalised.setStartLocation(executiveTaskExecution.getLocation());
                                    KilometerCalculationDenormalised.setEndLocation(executiveTaskExecution.getLocation());
                                    KilometerCalculationDenormalised.setTaskExecutionPid(executiveTaskExecution.getPid());
                                    KilometerCalculationDenormalised.setExeCreatedDate(executiveTaskExecution.getCreatedDate());
                                    if (employee.isPresent()) {
                                        KilometerCalculationDenormalised.setEmployeeName(employee.get().getName());
                                        KilometerCalculationDenormalised.setEmployeePid(employee.get().getPid());
                                    }
                                    KilometerCalculationDenormalised.setAccountProfileName(executiveTaskExecution.getAccountProfile().getName());
                                    KilometerCalculationDenormalised.setAccountProfilePid(executiveTaskExecution.getAccountProfile().getPid());
                                    KilometerCalculationDenormalised.setLatitude(executiveTaskExecution.getLatitude());
                                    KilometerCalculationDenormalised.setLongitude(executiveTaskExecution.getLongitude());
                                    KilometerCalculationDenormalised.setLocation(executiveTaskExecution.getLocation());
                                    KilometerCalculationDenormalised.setCreatedDate(executiveTaskExecution.getCreatedDate());
                                    KilometerCalculationDenormalised.setPunchingDate(executiveTaskExecution.getSendDate());
                                    KilometerCalculationDenormalised.setCompanyId(executiveTaskExecution.getCompany().getId());
                                    KilometerCalculationDenormalised.setCalculatd(true);
                                } else{
                                    log.debug("Attendance latitude and longitudes are not accurate");
                                    KilometerCalculationDenormalised = saveDefaultKilometer(executiveTaskExecution,employee);
                                }
                            }else{
                                log.debug("Api Response Null " + distanceApiJson.getStatus());
                                KilometerCalculationDenormalised = saveDefaultKilometer(executiveTaskExecution,employee);
                            }
                        }else{
                            log.debug("Adding Default Value");
                            KilometerCalculationDenormalised = saveDefaultKilometer(executiveTaskExecution, employee);
                        }
                    }

                    if(!tsTransactionWrapper.getExecutiveTaskExecution().getActivity().getKmCalculationDisabled()){
                        if(executiveTaskExecution.getLongitude() != null
                                && executiveTaskExecution.getLongitude().doubleValue() != 0
                                && executiveTaskExecution.getLatitude() != null
                                && executiveTaskExecution.getLatitude().doubleValue() != 0){

                            log.debug("Coordinates : " + executiveTaskExecution.getLatitude()
                                    +","+executiveTaskExecution.getLongitude());
                            log.debug(" Calculating  kilometer from  Executive task ");

                            Optional<CompanyConfiguration> optSalbdistnceCalculations = getCompanyConfiguration(user);

                            if (optSalbdistnceCalculations.isPresent()) {
                                if (Boolean.valueOf(optSalbdistnceCalculations.get().getValue())) {
                                    getDistanceFare(executiveTaskExecution.getSendDate().toLocalDate(),
                                            executiveTaskExecution.getUser().getPid(),KilometerCalculationDenormalised);
                                }
                            }
                            KilometerCalculationDenormalised.setCalculatd(true);
                        }else{
                            log.debug("Not Calculatable " + executiveTaskExecution.getLongitude());
                            KilometerCalculationDenormalised.setCalculatd(false);
                        }
                        KilometerCalculationDenormalisedRepository.save(KilometerCalculationDenormalised);
                    }else{
                        log.debug("Kilometer calculation Property not Supported For this Visit (Activity) "
                                + tsTransactionWrapper.getExecutiveTaskExecution().getActivity().getName());
                    }
                }
            }




    }

    private Optional<CompanyConfiguration> getCompanyConfiguration(User user) {
        Optional<CompanyConfiguration> optSalbdistnceCalculations =
                companyConfigurationRepository
                        .findByCompanyPidAndName(
                                user.getCompany().getPid(), CompanyConfig.ENABLE_DISATNCE_SLAB_CALC);
        return optSalbdistnceCalculations;
    }


    public KilometerCalculationDenormalised saveDefaultKilometer(
            ExecutiveTaskExecution executiveTaskExecution,Optional<EmployeeProfile> employee) {
        log.debug("Adding Default Value");
        KilometerCalculationDenormalised  KilometerCalculationDenormalised = new  KilometerCalculationDenormalised();
        KilometerCalculationDenormalised.setKilometre(0.0);
        KilometerCalculationDenormalised.setMetres(0.0);
        KilometerCalculationDenormalised.setUserPid(executiveTaskExecution.getUser().getPid());
        KilometerCalculationDenormalised.setUserName(executiveTaskExecution.getUser().getFirstName());
        KilometerCalculationDenormalised.setAccountProfileName(executiveTaskExecution.getAccountProfile().getName());
        KilometerCalculationDenormalised.setAccountProfilePid(executiveTaskExecution.getAccountProfile().getPid());
        KilometerCalculationDenormalised.setPunchingDate(executiveTaskExecution.getSendDate());
        KilometerCalculationDenormalised.setExeCreatedDate(executiveTaskExecution.getCreatedDate());
        KilometerCalculationDenormalised.setCreatedDate(executiveTaskExecution.getCreatedDate());
        KilometerCalculationDenormalised.setLocation(executiveTaskExecution.getLocation());
        if (employee.isPresent()) {
            KilometerCalculationDenormalised.setEmployeeName(employee.get().getName());
            KilometerCalculationDenormalised.setEmployeePid(employee.get().getPid());
        }
        KilometerCalculationDenormalised.setLatitude(executiveTaskExecution.getLatitude());
        KilometerCalculationDenormalised.setLongitude(executiveTaskExecution.getLongitude());
        KilometerCalculationDenormalised.setTaskExecutionPid(executiveTaskExecution.getPid());
        KilometerCalculationDenormalised.setLatitude(executiveTaskExecution.getLatitude());
        KilometerCalculationDenormalised.setLongitude(executiveTaskExecution.getLongitude());
        KilometerCalculationDenormalised.setCompanyId(executiveTaskExecution.getCompany().getId());
        return KilometerCalculationDenormalised;
    }



    @Override
    @Async
    public void saveAttendenceKilometer(
            Attendance attendance,User user ) {
        log.debug("Saving Attendence in kilometer Calculaions");
        MapDistanceApiDTO distanceApiJson = null;
        MapDistanceDTO distance = null;
        LocalDateTime fromDate = LocalDate.now().atTime(0, 0);
        LocalDateTime toDate = LocalDate.now().atTime(23, 59);
        log.debug(" login : " + user);

        Optional<EmployeeProfile> employee =
                employeeProfileRepository.findByUserPid(user.getPid());

        Optional<KilometerCalculationDenormalised> OptionalOrigin =
                KilometerCalculationDenormalisedRepository
                        .findTop1ByTaskExecutionPid(
                                user.getCompany().getId(),fromDate,toDate,user.getPid());

        KilometerCalculationDenormalised  KilometerCalculationDenormalised = new  KilometerCalculationDenormalised();

        if(OptionalOrigin.isPresent()){
            log.debug("OptionalOrigin : " + OptionalOrigin.isPresent());
            String origin = OptionalOrigin.get().getLatitude() +","+OptionalOrigin.get().getLongitude();
            String destination = attendance.getLatitude() +","+attendance.getLongitude();
            log.debug("Origin : " + OptionalOrigin.get().getAccountProfileName() + " : " + origin );
            log.debug("destination : " + "Attendence " + " : " + destination );
            distanceApiJson = geoLocationService.findDistance(origin, destination);
            if (distanceApiJson != null && !distanceApiJson.getRows().isEmpty()) {
                distance = distanceApiJson.getRows().get(0).getElements().get(0).getDistance();
                if (distance != null) {
                    log.debug("distance not null ");
                    KilometerCalculationDenormalised.setKilometre(0.0);
                    KilometerCalculationDenormalised.setMetres(0.0);
                    KilometerCalculationDenormalised.setUserPid(attendance.getUser().getPid());
                    KilometerCalculationDenormalised.setUserName(attendance.getUser().getFirstName());
                    KilometerCalculationDenormalised.setStartLocation(attendance.getLocation());
                    KilometerCalculationDenormalised.setEndLocation(attendance.getLocation());
                    KilometerCalculationDenormalised.setTaskExecutionPid(attendance.getPid());
                    KilometerCalculationDenormalised.setAccountProfileName("Attendence");
                    KilometerCalculationDenormalised.setAccountProfilePid(attendance.getPid());
                    KilometerCalculationDenormalised.setExeCreatedDate(attendance.getCreatedDate());
                    KilometerCalculationDenormalised.setLatitude(attendance.getLatitude());
                    KilometerCalculationDenormalised.setLocation("No Location");
                    if (employee.isPresent()) {KilometerCalculationDenormalised.setEmployeeName(employee.get().getName());}
                    KilometerCalculationDenormalised.setLongitude(attendance.getLongitude());
                    KilometerCalculationDenormalised.setCreatedDate(attendance.getCreatedDate());
                    KilometerCalculationDenormalised.setPunchingDate(attendance.getPlannedDate());
                    KilometerCalculationDenormalised.setCompanyId(attendance.getCompany().getId());
                    KilometerCalculationDenormalised.setCalculatd(true);
                }else{
                    log.debug("Adding default Attendence : Not Accurate ");
                    KilometerCalculationDenormalised = defaultAttendence(attendance,employee);
                }
            }else{
                log.debug("Adding default Attendence : Not Accurate Distance Value ");
                KilometerCalculationDenormalised = defaultAttendence(attendance,employee);
            }
        }else{
            log.debug("Adding default Attendence : No Previous Value ");
            KilometerCalculationDenormalised = defaultAttendence(attendance,employee);
        }

        if(attendance.getLongitude() != null
                && attendance.getLongitude().doubleValue() != 0
                && attendance.getLatitude() != null
                && attendance.getLatitude().doubleValue() != 0){
            KilometerCalculationDenormalised.setCalculatd(true);
            log.debug(" Calculating  kilometer from  attandence ");

            Optional<CompanyConfiguration> optSalbdistnceCalculations = getCompanyConfiguration(user);

            if (optSalbdistnceCalculations.isPresent()) {
                if (Boolean.valueOf(optSalbdistnceCalculations.get().getValue())) {
                    getDistanceFare(attendance.getPlannedDate().toLocalDate(),
                            attendance.getUser().getPid(),KilometerCalculationDenormalised);
                }
            }
        }else{
            KilometerCalculationDenormalised.setCalculatd(false);
        }
        convertToJson(KilometerCalculationDenormalised,"saving Attendence kilometer");
        KilometerCalculationDenormalisedRepository.save(KilometerCalculationDenormalised);
    }


    private KilometerCalculationDenormalised
    defaultAttendence(Attendance attendance,Optional<EmployeeProfile> employee) {
        log.debug("Adding Default value ");
        KilometerCalculationDenormalised KilometerCalculationDenormalised = new KilometerCalculationDenormalised();
        KilometerCalculationDenormalised.setKilometre(0.0);
        KilometerCalculationDenormalised.setMetres(0.0);
        KilometerCalculationDenormalised.setUserPid(attendance.getUser().getPid());
        KilometerCalculationDenormalised.setUserName(attendance.getUser().getFirstName());
        KilometerCalculationDenormalised.setStartLocation(attendance.getLocation());
        KilometerCalculationDenormalised.setEndLocation(attendance.getLocation());
        KilometerCalculationDenormalised.setTaskExecutionPid(attendance.getPid());
        KilometerCalculationDenormalised.setAccountProfileName("Attendence");
        if (employee.isPresent()) {
            KilometerCalculationDenormalised.setEmployeeName(employee.get().getName());
            KilometerCalculationDenormalised.setEmployeePid(employee.get().getPid());
        }
        KilometerCalculationDenormalised.setLocation(attendance.getLocation());
        KilometerCalculationDenormalised.setAccountProfilePid(attendance.getPid());
        KilometerCalculationDenormalised.setExeCreatedDate(attendance.getCreatedDate());
        KilometerCalculationDenormalised.setLatitude(attendance.getLatitude());
        KilometerCalculationDenormalised.setLongitude(attendance.getLongitude());
        KilometerCalculationDenormalised.setCreatedDate(attendance.getCreatedDate());
        KilometerCalculationDenormalised.setPunchingDate(attendance.getPlannedDate());
        KilometerCalculationDenormalised.setCompanyId(attendance.getCompany().getId());
        return KilometerCalculationDenormalised;
    }

    @Override
    @Async
    public void savePunchoutKilometer(PunchOutDTO punchOut,User user) {
        LocalDateTime fromDate = LocalDate.now().atTime(0, 0);
        LocalDateTime toDate = LocalDate.now().atTime(23, 59);
        MapDistanceApiDTO distanceApiJson = null;
        MapDistanceDTO distance = null;
        KilometerCalculationDenormalised  KilometerCalculationDenormalised = new  KilometerCalculationDenormalised();

        Optional<EmployeeProfile> employee =
                employeeProfileRepository.findByUserPid(user.getPid());

        Optional<KilometerCalculationDenormalised> OptionalOrigin =
                KilometerCalculationDenormalisedRepository
                        .findTop1ByTaskExecutionPid(
                                user.getCompany().getId(),fromDate,toDate,user.getPid());

        if(OptionalOrigin.isPresent()){
            log.debug("OptionalOrigin : " + OptionalOrigin.isPresent());
            String origin = OptionalOrigin.get().getLatitude() +","+OptionalOrigin.get().getLongitude();
            String destination = punchOut.getLatitude() +","+punchOut.getLongitude();
            log.debug("Origin : " + OptionalOrigin.get().getAccountProfileName() + " : " + origin );
            log.debug("destination : " + " punchOut " + " : " + destination );
            distanceApiJson = geoLocationService.findDistance(origin, destination);
            if (distanceApiJson != null && !distanceApiJson.getRows().isEmpty()) {
                distance = distanceApiJson.getRows().get(0).getElements().get(0).getDistance();
                if (distance != null) {
                    KilometerCalculationDenormalised.setKilometre(distance.getValue() * 0.001);
                    KilometerCalculationDenormalised.setMetres(distance.getValue());
                    KilometerCalculationDenormalised.setUserPid(punchOut.getUserPid());
                    KilometerCalculationDenormalised.setUserName(punchOut.getUserName().getFirstName());
                    KilometerCalculationDenormalised.setStartLocation(punchOut.getLocation());
                    KilometerCalculationDenormalised.setEndLocation(punchOut.getLocation());
                    KilometerCalculationDenormalised.setTaskExecutionPid(punchOut.getPid());
                    KilometerCalculationDenormalised.setAccountProfileName("PUNCH OUT");
                    KilometerCalculationDenormalised.setLocation(punchOut.getLocation());
                    KilometerCalculationDenormalised.setExeCreatedDate(punchOut.getCreatedDate());
                    KilometerCalculationDenormalised.setLatitude(punchOut.getLatitude());
                    if (employee.isPresent()){KilometerCalculationDenormalised.setEmployeeName(employee.get().getName());}
                    KilometerCalculationDenormalised.setLongitude(punchOut.getLongitude());
                    KilometerCalculationDenormalised.setCreatedDate(punchOut.getCreatedDate());
                    KilometerCalculationDenormalised.setPunchingDate(punchOut.getPunchOutDate());
                    KilometerCalculationDenormalised.setCompanyId(punchOut.getCompanyId());
                    KilometerCalculationDenormalised.setCalculatd(true);
                } else{
                    log.debug("adding default punchout : not Accurate ");
                    KilometerCalculationDenormalised =  defaultPunchout(punchOut,employee);
                }
            }else{
                log.debug("adding default punchout : not Accurate Distance Value ");
                KilometerCalculationDenormalised = defaultPunchout(punchOut,employee);
            }
        }else{
            log.debug("adding default punchout : No Previous Value ");
            KilometerCalculationDenormalised = defaultPunchout(punchOut,employee);
        }

        if(punchOut.getLongitude() != null
                && punchOut.getLongitude().doubleValue() != 0
                && punchOut.getLatitude() != null
                && punchOut.getLatitude().doubleValue() != 0){
               KilometerCalculationDenormalised.setCalculatd(true);
            log.debug(" Calculating  kilometer from  Executive task punchout ");

            Optional<CompanyConfiguration> optSalbdistnceCalculations = getCompanyConfiguration(user);

            if (optSalbdistnceCalculations.isPresent()) {
                if (Boolean.valueOf(optSalbdistnceCalculations.get().getValue())) {
                    getDistanceFare(punchOut.getPunchOutDate().toLocalDate(),
                            punchOut.getUserPid(),KilometerCalculationDenormalised);
                }
            }
        }else{
            KilometerCalculationDenormalised.setCalculatd(false);
        }

        convertToJson(KilometerCalculationDenormalised," Saving Punchout");
        KilometerCalculationDenormalisedRepository.save(KilometerCalculationDenormalised);
    }

    private KilometerCalculationDenormalised
    defaultPunchout(PunchOutDTO punchOut ,Optional<EmployeeProfile> employee) {
        log.debug("Adding Default Punchout ");
        KilometerCalculationDenormalised KilometerCalculationDenormalised = new KilometerCalculationDenormalised();
        KilometerCalculationDenormalised.setKilometre(0.0);
        KilometerCalculationDenormalised.setMetres(0.0);
        KilometerCalculationDenormalised.setUserPid(punchOut.getUserPid());
        KilometerCalculationDenormalised.setUserName(punchOut.getUserName().getFirstName());
        KilometerCalculationDenormalised.setStartLocation(punchOut.getLocation());
        KilometerCalculationDenormalised.setEndLocation(punchOut.getLocation());
        KilometerCalculationDenormalised.setTaskExecutionPid(punchOut.getPid());
        if (employee.isPresent()) {
            KilometerCalculationDenormalised.setEmployeeName(employee.get().getName());
            KilometerCalculationDenormalised.setEmployeePid(employee.get().getPid());
        }
        KilometerCalculationDenormalised.setAccountProfileName("PUNCH OUT");
        KilometerCalculationDenormalised.setLocation(punchOut.getLocation());
        KilometerCalculationDenormalised.setAccountProfilePid(punchOut.getPid());
        KilometerCalculationDenormalised.setLatitude(punchOut.getLatitude());
        KilometerCalculationDenormalised.setLongitude(punchOut.getLongitude());
        KilometerCalculationDenormalised.setCreatedDate(punchOut.getCreatedDate());
        KilometerCalculationDenormalised.setPunchingDate(punchOut.getPunchOutDate());
        KilometerCalculationDenormalised.setExeCreatedDate(punchOut.getCreatedDate());
        KilometerCalculationDenormalised.setCompanyId(punchOut.getCompanyId());
        return KilometerCalculationDenormalised;
    }


    @Override
    public List<KilometerCalculationDTO>
    findAllByDateBetweenAndCompanyId(
            long companyid, LocalDateTime fromDate,
            LocalDateTime toDate, String userPid) {
        List<KilometerCalculationDTO> KiloCalDTOs = new ArrayList<>();
        List<KilometerCalculationDenormalised> kilometerCalculationDenormaliseds = new ArrayList<>();
        kilometerCalculationDenormaliseds =
                KilometerCalculationDenormalisedRepository
                        .findAllByDateBetweenAndCompanyId(companyid, fromDate, toDate,userPid);
        for(KilometerCalculationDenormalised kcd : kilometerCalculationDenormaliseds){
            KilometerCalculationDTO kilometerCalculationDTO =  new KilometerCalculationDTO();
            kilometerCalculationDTO.setKilometre(kcd.getKilometre());
            kilometerCalculationDTO.setMetres(kcd.getMetres());
            kilometerCalculationDTO.setUserPid(kcd.getUserPid());
            kilometerCalculationDTO.setUserName(kcd.getUserName());
            kilometerCalculationDTO.setStartLocation(kcd.getStartLocation());
            kilometerCalculationDTO.setEndLocation(kcd.getEndLocation());
            kilometerCalculationDTO.setTaskExecutionPid(kcd.getTaskExecutionPid());
            kilometerCalculationDTO.setEmployeeName(kcd.getEmployeeName());
            kilometerCalculationDTO.setEmployeePid(kcd.getEmployeePid());
            kilometerCalculationDTO.setLocation(kcd.getLocation());
            kilometerCalculationDTO.setPunchingDate(kcd.getCreatedDate().toLocalDate().toString());
            kilometerCalculationDTO.setPunchingTime(kcd.getCreatedDate().toLocalTime().toString());
            kilometerCalculationDTO.setCreatedDate(kcd.getCreatedDate());
            kilometerCalculationDTO.setDate(kcd.getExeCreatedDate().toLocalDate());
            kilometerCalculationDTO.setAccountProfileName(kcd.getAccountProfileName());
            kilometerCalculationDTO.setExecreatedDate(kcd.getExeCreatedDate());
            KiloCalDTOs.add(kilometerCalculationDTO);
        }
        return KiloCalDTOs;
    }




    @Override
    public KilometerCalculationDenormalised findbyExePid(
            String pid) {
        KilometerCalculationDenormalised kcd =
                KilometerCalculationDenormalisedRepository
                        .findByTaskExecutionPid(
                                SecurityUtils.getCurrentUsersCompanyId(),pid);
        return kcd;
    }


    @Override
    public void saveLocation(
            KilometerCalculationDenormalised kcd) {
        KilometerCalculationDenormalisedRepository.save(kcd);
    }


    @Override
    public KilometerCalculationDenormalised testQuery(
            String userPid, LocalDateTime fromDate ,
            LocalDateTime toDate , long companyId) {
        return KilometerCalculationDenormalisedRepository
                .findTop1ByTaskExecutionPid(
                        companyId,fromDate,toDate,userPid).get();
    }

    @Override
    @Async
    public KmDistanceFare getDistanceFare(LocalDate plannedDate, String userPid,
                                          KilometerCalculationDenormalised KilometerCalculationDenormalised) {
        log.debug("Saving Distance Fare Data");
        kmDistanceFareService.getDistanceFare(plannedDate,userPid,KilometerCalculationDenormalised);
        return null;
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
