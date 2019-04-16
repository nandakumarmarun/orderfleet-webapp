package com.orderfleet.webapp.web.rest.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.orderfleet.webapp.domain.DynamicDocumentSettingsHeader;

/**
 * A DTO For DynamicDocumentSettingsHeader.
 *
 * @author Sarath
 * @since Aug 29, 2017
 *
 */
public class DynamicDocumentSettingsHeaderDTO {

	private String pid;

	private String name;

	private String title;

	private List<DynamicDocumentSettingsColumnsDTO> documentSettingsColumnsDTOs = new ArrayList<>();

	private List<DynamicDocumentSettingsRowColourDTO> documentSettingsRowColourDTOs = new ArrayList<>();;

	private List<DynamicDocumentReportDetailDTO> dynamicDocumentReportDetailDTOs = new ArrayList<>();;

	private String documentPid;

	private String documentName;

	public DynamicDocumentSettingsHeaderDTO() {
		super();
	}

	public DynamicDocumentSettingsHeaderDTO(DynamicDocumentSettingsHeader documentSettingsHeader) {
		super();
		this.pid = documentSettingsHeader.getPid();
		this.name = documentSettingsHeader.getName();
		this.title = documentSettingsHeader.getTitle();
		this.documentPid = documentSettingsHeader.getDocument().getPid();
		this.documentName = documentSettingsHeader.getDocument().getName();
		if (documentSettingsHeader.getDocumentSettingsColumns() != null
				&& documentSettingsHeader.getDocumentSettingsColumns().size() > 0) {
			this.documentSettingsColumnsDTOs = documentSettingsHeader.getDocumentSettingsColumns().stream()
					.map(DynamicDocumentSettingsColumnsDTO::new).collect(Collectors.toList());
		}
		if (documentSettingsHeader.getDocumentSettingsRowColours() != null
				&& documentSettingsHeader.getDocumentSettingsRowColours().size() > 0) {
			this.documentSettingsRowColourDTOs = documentSettingsHeader.getDocumentSettingsRowColours().stream()
					.map(DynamicDocumentSettingsRowColourDTO::new).collect(Collectors.toList());
		}

		if (documentSettingsHeader.getDynamicDocumentReportDetails() != null
				&& documentSettingsHeader.getDynamicDocumentReportDetails().size() > 0) {
			List<DynamicDocumentReportDetailDTO> detailDTOs = documentSettingsHeader.getDynamicDocumentReportDetails()
					.stream().map(DynamicDocumentReportDetailDTO::new).collect(Collectors.toList());
			detailDTOs.sort(Comparator.comparing(DynamicDocumentReportDetailDTO::getSortOrder));
			this.dynamicDocumentReportDetailDTOs = detailDTOs;
		}
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<DynamicDocumentSettingsColumnsDTO> getDocumentSettingsColumnsDTOs() {
		return documentSettingsColumnsDTOs;
	}

	public void setDocumentSettingsColumnsDTOs(List<DynamicDocumentSettingsColumnsDTO> documentSettingsColumnsDTOs) {
		this.documentSettingsColumnsDTOs = documentSettingsColumnsDTOs;
	}

	public List<DynamicDocumentSettingsRowColourDTO> getDocumentSettingsRowColourDTOs() {
		return documentSettingsRowColourDTOs;
	}

	public void setDocumentSettingsRowColourDTOs(
			List<DynamicDocumentSettingsRowColourDTO> documentSettingsRowColourDTOs) {
		this.documentSettingsRowColourDTOs = documentSettingsRowColourDTOs;
	}

	public String getDocumentPid() {
		return documentPid;
	}

	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public List<DynamicDocumentReportDetailDTO> getDynamicDocumentReportDetailDTOs() {
		return dynamicDocumentReportDetailDTOs;
	}

	public void setDynamicDocumentReportDetailDTOs(
			List<DynamicDocumentReportDetailDTO> dynamicDocumentReportDetailDTOs) {
		this.dynamicDocumentReportDetailDTOs = dynamicDocumentReportDetailDTOs;
	}

	@Override
	public String toString() {
		return "DynamicDocumentSettingsHeaderDTO [pid=" + pid + ", name=" + name + ", title=" + title
				+ ", documentSettingsColumnsDTOs=" + documentSettingsColumnsDTOs + ", documentPid=" + documentPid
				+ ", documentName=" + documentName + "]";
	}

}
