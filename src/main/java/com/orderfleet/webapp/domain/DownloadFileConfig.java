package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.DownloadConfig;
import com.orderfleet.webapp.domain.enums.DownloadFileColumns;


/**
 * A Configuration for downloading pdf and excel columns.
 * 
 * @author Anish
 * @since March 11, 2020
 */
@Entity
@Table(name = "tbl_download_file_config")
public class DownloadFileConfig implements Serializable {

    private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "SEQ_DOWNLOAD_FILE_CONFIG_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SEQ_DOWNLOAD_FILE_CONFIG") })
	@GeneratedValue(generator = "SEQ_DOWNLOAD_FILE_CONFIG_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('SEQ_DOWNLOAD_FILE_CONFIG')")
    private Long id;
	
	@Column(name = "heading_name" ,nullable = false)
	private String headingName;
	
	@ManyToOne
	private Company company;
	
	@Column(name = "sort_order")
	private Long sortOrder;
	
	@Column(name = "download_file_columns")
	private DownloadFileColumns downloadFileColumns;
	
	@Column(name = "download_config")
	private DownloadConfig downloadConfig;

	
	public Company getCompany() {
		return company;
	}

	public DownloadConfig getDownloadConfig() {
		return downloadConfig;
	}

	public DownloadFileColumns getDownloadFileColumns() {
		return downloadFileColumns;
	}

	public String getHeadingName() {
		return headingName;
	}

	public Long getId() {
		return id;
	}

	public Long getSortOrder() {
		return sortOrder;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public void setDownloadConfig(DownloadConfig downloadConfig) {
		this.downloadConfig = downloadConfig;
	}

	public void setDownloadFileColumns(DownloadFileColumns downloadFileColumns) {
		this.downloadFileColumns = downloadFileColumns;
	}

	public void setHeadingName(String headingName) {
		this.headingName = headingName;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setSortOrder(Long sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	
	
	
}
