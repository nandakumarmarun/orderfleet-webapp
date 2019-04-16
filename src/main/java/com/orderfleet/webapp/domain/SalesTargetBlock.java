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

/**
 * A SalesTargetBlock
 *
 * @author Sarath
 * @since Feb 17, 2017
 */
@Entity
@Table(name = "tbl_sales_target_block")
public class SalesTargetBlock implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_sales_target_block_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_sales_target_block_id") })
	@GeneratedValue(generator = "seq_sales_target_block_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_sales_target_block_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name", length = 255, nullable = false)
	private String name;

	@Column(name = "start_month")
	private int startMonth;

	@Column(name = "start_month_name")
	private String startMonthName;

	@Column(name = "start_month_minus")
	private int startMonthMinus;

	@Column(name = "start_month_year_minus")
	private int startMonthYearMinus;

	@Column(name = "end_month")
	private int endMonth;

	@Column(name = "end_month_name")
	private String endMonthName;

	@Column(name = "end_month_minus")
	private int endMonthMinus;

	@Column(name = "end_month_year_minus")
	private int endMonthYearMinus;

	@Column(name = "createDynamicLabel")
	private boolean createDynamicLabel;

	@Column(name = "description")
	private String description;

	@ManyToOne
	@NotNull
	private Company company;

	@Column(name = "target_setting_type", nullable = false, columnDefinition = "character varying DEFAULT 'SALES'")
	@Enumerated(EnumType.STRING)
	private BestPerformanceType targetSettingType;
	
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

	public int getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(int startMonth) {
		this.startMonth = startMonth;
	}

	public String getStartMonthName() {
		return startMonthName;
	}

	public void setStartMonthName(String startMonthName) {
		this.startMonthName = startMonthName;
	}

	public int getStartMonthMinus() {
		return startMonthMinus;
	}

	public void setStartMonthMinus(int startMonthMinus) {
		this.startMonthMinus = startMonthMinus;
	}

	public int getStartMonthYearMinus() {
		return startMonthYearMinus;
	}

	public void setStartMonthYearMinus(int startMonthYearMinus) {
		this.startMonthYearMinus = startMonthYearMinus;
	}

	public int getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(int endMonth) {
		this.endMonth = endMonth;
	}

	public String getEndMonthName() {
		return endMonthName;
	}

	public void setEndMonthName(String endMonthName) {
		this.endMonthName = endMonthName;
	}

	public int getEndMonthMinus() {
		return endMonthMinus;
	}

	public void setEndMonthMinus(int endMonthMinus) {
		this.endMonthMinus = endMonthMinus;
	}

	public int getEndMonthYearMinus() {
		return endMonthYearMinus;
	}

	public void setEndMonthYearMinus(int endMonthYearMinus) {
		this.endMonthYearMinus = endMonthYearMinus;
	}

	public boolean getCreateDynamicLabel() {
		return createDynamicLabel;
	}

	public void setCreateDynamicLabel(boolean createDynamicLabel) {
		this.createDynamicLabel = createDynamicLabel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public BestPerformanceType getTargetSettingType() {
		return targetSettingType;
	}

	public void setTargetSettingType(BestPerformanceType targetSettingType) {
		this.targetSettingType = targetSettingType;
	}

}
