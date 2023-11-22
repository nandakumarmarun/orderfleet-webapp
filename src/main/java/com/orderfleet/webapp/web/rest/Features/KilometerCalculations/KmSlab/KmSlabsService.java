package com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlab;

import java.util.List;

public interface KmSlabsService {

    String PID_PREFIX = "KMSL-";

    public List<KmSlabDTO> getAllSlabs();

    public KmSlabDTO saveKmSlab(KmSlabDTO kmSlabDTO);

    public KmSlabDTO updateKmSlab(KmSlabDTO kmSlabDTO);

    public KmSlabDTO findByKmSlab(String KmSlabPid);

    public void DeleteKmSlab(String pid);

}
