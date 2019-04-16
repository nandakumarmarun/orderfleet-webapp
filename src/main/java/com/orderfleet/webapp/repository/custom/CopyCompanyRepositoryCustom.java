package com.orderfleet.webapp.repository.custom;

import com.orderfleet.webapp.web.rest.siteadmin.CCMasterDataDTO;

public interface CopyCompanyRepositoryCustom {

	void copyCompanyMasterData(String defaultSchema, CCMasterDataDTO ccmdDto, String tblName);
	
}
