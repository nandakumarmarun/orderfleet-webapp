package com.orderfleet.webapp.web.rest.dto;

import java.util.Objects;

import com.orderfleet.webapp.domain.KnowledgebaseFiles;

/**
 * A DTO for the KnowledgebaseFile entity.
 * 
 * @author Muhammed Riyas T
 * @since August 12, 2016
 */
public class KnowledgebaseFileDTO {

	private String pid;

	private String fileName;

	private String searchTags;

	private String knowledgebasePid;

	private String knowledgebaseName;

	private String filePid;

	public KnowledgebaseFileDTO() {
		super();
	}

	public KnowledgebaseFileDTO(KnowledgebaseFiles knowledgebaseFiles) {
		super();
		this.pid = knowledgebaseFiles.getPid();
		this.fileName = knowledgebaseFiles.getFileName();
		this.searchTags = knowledgebaseFiles.getSearchTags();
		this.knowledgebasePid = knowledgebaseFiles.getKnowledgebase().getPid();
		this.knowledgebaseName = knowledgebaseFiles.getKnowledgebase().getName();
		this.filePid = knowledgebaseFiles.getFile().getPid();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getSearchTags() {
		return searchTags;
	}

	public void setSearchTags(String searchTags) {
		this.searchTags = searchTags;
	}

	public String getKnowledgebasePid() {
		return knowledgebasePid;
	}

	public void setKnowledgebasePid(String knowledgebasePid) {
		this.knowledgebasePid = knowledgebasePid;
	}

	public String getKnowledgebaseName() {
		return knowledgebaseName;
	}

	public void setKnowledgebaseName(String knowledgebaseName) {
		this.knowledgebaseName = knowledgebaseName;
	}

	public String getFilePid() {
		return filePid;
	}

	public void setFilePid(String filePid) {
		this.filePid = filePid;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		KnowledgebaseFileDTO knowledgebaseDTO = (KnowledgebaseFileDTO) o;

		if (!Objects.equals(pid, knowledgebaseDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

}
