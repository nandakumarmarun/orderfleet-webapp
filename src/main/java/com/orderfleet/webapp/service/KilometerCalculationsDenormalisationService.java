package com.orderfleet.webapp.service;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.orderfleet.webapp.web.rest.dto.KilometerCalculationDTO;
import com.orderfleet.webapp.web.rest.dto.PunchOutDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface KilometerCalculationsDenormalisationService {

    public void CalculateDistance(List<KilometerCalculationDTO> KiloCalDTOs, long companyid);

    public void calculateDistanceOnCall(ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper,User user);

    public void saveAttendenceKilometer(Attendance attendance, User user);

    public void savePunchoutKilometer(PunchOutDTO punchOut,User user );

    public List<KilometerCalculationDTO> findAllByDateBetweenAndCompanyId(long companyid, LocalDateTime fromDate, LocalDateTime toDate,String userPid);

    public KilometerCalculationDenormalised findbyExePid(String pid);

    public void saveLocation(KilometerCalculationDenormalised kcd);

    public KilometerCalculationDenormalised testQuery(String userPid, LocalDateTime fromDate , LocalDateTime toDate , long companyId);

    }
