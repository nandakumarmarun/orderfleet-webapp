package com.orderfleet.webapp.service;

import com.orderfleet.webapp.web.rest.dto.slabDTO;

import java.util.List;

public interface SlabService {
    String PID_PREFIX = "SLB-";

    public slabDTO save(slabDTO slab);

    List<slabDTO> findAllByCompanyId(Long companyid);

    public List<slabDTO> findAll();

    public void DeleteByPid(String Pid);

    public List<slabDTO> findByLeagalName(String legalName);



}
