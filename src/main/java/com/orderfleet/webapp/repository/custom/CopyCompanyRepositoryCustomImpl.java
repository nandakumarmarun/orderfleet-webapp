package com.orderfleet.webapp.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.web.rest.siteadmin.CCMasterDataDTO;

@Component
public final class CopyCompanyRepositoryCustomImpl implements CopyCompanyRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	@Transactional
	public void copyCompanyMasterData(String defaultSchema, CCMasterDataDTO ccmdDto,String tblName) {
		StoredProcedureQuery query = em.createStoredProcedureQuery(defaultSchema + ".copy_master_data")
				.registerStoredProcedureParameter("fromcompanyid", Long.class, ParameterMode.IN)
				.registerStoredProcedureParameter("tocompanyid", Long.class, ParameterMode.IN)
				.registerStoredProcedureParameter("fromschema", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("toschema", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("tblname", String.class, ParameterMode.IN)
				.setParameter("fromcompanyid", ccmdDto.getFromCompanyId())
				.setParameter("tocompanyid", ccmdDto.getToCompanyId())
				.setParameter("fromschema", ccmdDto.getFromSchema())
				.setParameter("toschema", ccmdDto.getToSchema())
				.setParameter("tblname", tblName);
		
		query.getResultList();
		
	}
}
