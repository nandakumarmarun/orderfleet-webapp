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
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.domain.enums.TargetFrequency;
import com.orderfleet.webapp.domain.enums.TargetType;

/**
 * A SalesTargetReportSetting.
 *
 * @author Sarath
 * @since Feb 17, 2017
 */
@Entity
@Table(name = "tbl_sales_target_report_setting")
public class SalesTargetReportSetting implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_sales_target_report_setting_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_sales_target_report_setting_id") })
	@GeneratedValue(generator = "seq_sales_target_report_setting_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_sales_target_report_setting_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name", length = 255, nullable = false)
	private String name;

	@Column(name = "account_wise_target", columnDefinition = "boolean DEFAULT 'FALSE' ")
	private boolean accountWiseTarget;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "target_period", nullable = false)
	private TargetFrequency targetPeriod = TargetFrequency.MONTH;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "target_type", nullable = false)
	private TargetType targetType = TargetType.NONE;

	@ManyToOne
	@NotNull
	private Company company;

	@NotNull
	@Column(name = "monthly_average_wise", columnDefinition = "boolean DEFAULT 'FALSE' ")
	private boolean monthlyAverageWise;

	@Column(name = "target_setting_type", nullable = false, columnDefinition = "character varying DEFAULT 'SALES'")
	@Enumerated(EnumType.STRING)
	private BestPerformanceType targetSettingType;

	public SalesTargetReportSetting() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getAccountWiseTarget() {
		return accountWiseTarget;
	}

	public void setAccountWiseTarget(boolean accountWiseTarget) {
		this.accountWiseTarget = accountWiseTarget;
	}

	public TargetFrequency getTargetPeriod() {
		return targetPeriod;
	}

	public void setTargetPeriod(TargetFrequency targetPeriod) {
		this.targetPeriod = targetPeriod;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public boolean getMonthlyAverageWise() {
		return monthlyAverageWise;
	}

	public void setMonthlyAverageWise(boolean monthlyAverageWise) {
		this.monthlyAverageWise = monthlyAverageWise;
	}

	public BestPerformanceType getTargetSettingType() {
		return targetSettingType;
	}

	public void setTargetSettingType(BestPerformanceType targetSettingType) {
		this.targetSettingType = targetSettingType;
	}

}
