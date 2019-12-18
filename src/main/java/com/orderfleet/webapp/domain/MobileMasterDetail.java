package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.MobileMasterItem;

@Entity
@Table(name = "tbl_mobile_master_detail")
public class MobileMasterDetail implements Serializable, Cloneable{

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_mobile_master_detail_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_mobile_master_detail_id") })
	@GeneratedValue(generator = "seq_mobile_master_detail_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_mobile_master_detail_id')")
	private Long id;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "mobile_master_item", nullable = false, columnDefinition = "character varying DEFAULT 'DEFAULT'")
	private MobileMasterItem mobileMasterItem;
	private String operationTime;
	private Long count;
}
