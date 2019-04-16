package com.orderfleet.webapp.web.rest.api.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.orderfleet.webapp.domain.KnowledgebaseFiles;
import com.orderfleet.webapp.domain.Knowledgebase;
import com.orderfleet.webapp.web.rest.dto.KnowledgebaseDTO;

/**
 * KnowledgeBaseFilesDTO.
 * 
 * @author Shaheer
 * @since August 11, 2016
 */
public class KnowledgeBaseFilesDTO {

	private KnowledgebaseDTO knowledgebaseDTO;

	private List<FileSearchTag> fileSearchTags;

	public KnowledgeBaseFilesDTO() {
		super();
	}

	public KnowledgeBaseFilesDTO(Knowledgebase knowledgebase, List<KnowledgebaseFiles> knowledgeBaseFiles) {
		this.knowledgebaseDTO = new KnowledgebaseDTO(knowledgebase);
		this.fileSearchTags = knowledgeBaseFiles.stream().map(FileSearchTag::new).collect(Collectors.toList());
	}

	public KnowledgebaseDTO getKnowledgebaseDTO() {
		return knowledgebaseDTO;
	}

	public void setKnowledgebaseDTO(KnowledgebaseDTO knowledgebaseDTO) {
		this.knowledgebaseDTO = knowledgebaseDTO;
	}

	public List<FileSearchTag> getFileSearchTags() {
		return fileSearchTags;
	}

	public void setFileSearchTags(List<FileSearchTag> fileSearchTags) {
		this.fileSearchTags = fileSearchTags;
	}

	class FileSearchTag {

		private String searchTags;
		private String filePid;
		private String fileName;
		private String mimeType;
		private String description;

		public FileSearchTag() {
		}

		public FileSearchTag(KnowledgebaseFiles knowledgeBaseFiles) {
			this(knowledgeBaseFiles.getSearchTags(), knowledgeBaseFiles.getFile().getPid(),
					knowledgeBaseFiles.getFile().getFileName(), knowledgeBaseFiles.getFile().getMimeType(),
					knowledgeBaseFiles.getFile().getDescription());
		}

		public FileSearchTag(String searchTags, String filePid, String fileName, String mimeType, String description) {
			super();
			this.searchTags = searchTags;
			this.filePid = filePid;
			this.fileName = fileName;
			this.mimeType = mimeType;
			this.description = description;
		}

		public String getSearchTags() {
			return searchTags;
		}

		public String getFilePid() {
			return filePid;
		}

		public String getFileName() {
			return fileName;
		}

		public String getMimeType() {
			return mimeType;
		}

		public String getDescription() {
			return description;
		}

	}

}
