package com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlabUserAssociation;

import java.util.List;

public interface KmSlabUserService {

  public List<KmSlabUserDTO> findAllByCompanyIdUserPidInKmSlabPid(String kmSlabUser);


  public List<KmSlabUserDTO> AssociateUserSlab(String kmSlabUser, List<String> userPids);

}
