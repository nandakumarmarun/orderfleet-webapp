package com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmDistanceFare;

import com.orderfleet.webapp.domain.KilometerCalculationDenormalised;

import java.time.LocalDate;
import java.util.List;

public interface KmDistanceFareService {


    KmDistanceFare getDistanceFare(LocalDate plannedDate, String userPid, KilometerCalculationDenormalised KilometerCalculationDenormalised);

    List<KmDistanceFareDTO> getDistanceFareAmounts(LocalDate fromDate, LocalDate toDate, String userPid);


}