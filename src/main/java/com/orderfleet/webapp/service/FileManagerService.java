package com.orderfleet.webapp.service;

import java.util.Optional;

import org.springframework.core.io.Resource;

import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.service.impl.FileManagerException;

/**
 * Service Interface for managing Files.
 * 
 * @author Shaheer
 * @since August 01, 2016
 */
public interface FileManagerService {
	
	public File processFileUpload(final byte[] fileBytes, String originalFileName, String mimeType) throws FileManagerException;

	public java.io.File getPhysicalFileByFile(File file);
	
	Optional<File> findOneByPid(String pid);
	
	void deleteFile(File file);
	
	Resource loadAsResource(String filePid) throws FileManagerException;
}
