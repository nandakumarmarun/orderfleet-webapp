package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.MobileUINames;

/**
 * A PerformanceReportMobile.
 * 
 */
@Entity
@Table(name = "tbl_performance_report_mobile")
public class PerformanceReportMobile implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_performance_report_mobile_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_performance_report_mobile_id") })
	@GeneratedValue(generator = "seq_performance_report_mobile_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_performance_report_mobile_id')")
	private Long id;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "mobile_ui_name", nullable = false)
	private MobileUINames mobileUINames;

	@ManyToOne
	@NotNull
	private Company company;

	@ManyToOne
	@NotNull
	private SalesTargetReportSetting salesTargetReportSetting;

	public MobileUINames getMobileUINames() {
		return mobileUINames;
	}

	public void setMobileUINames(MobileUINames mobileUINames) {
		this.mobileUINames = mobileUINames;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public SalesTargetReportSetting getSalesTargetReportSetting() {
		return salesTargetReportSetting;
	}

	public void setSalesTargetReportSetting(SalesTargetReportSetting salesTargetReportSetting) {
		this.salesTargetReportSetting = salesTargetReportSetting;
	}

}
