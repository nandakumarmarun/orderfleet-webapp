package com.orderfleet.webapp.service.impl;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.PersistentFile;
import com.orderfleet.webapp.repository.FileRepository;
import com.orderfleet.webapp.repository.PersistentFileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.util.FileUtil;
import com.orderfleet.webapp.service.util.RandomUtil;

/**
 * Service Implementation for managing File.
 * 
 * @author Shaheer
 * @since August 01, 2016
 */
@Service
@Transactional
public class FileManagerServiceImpl implements FileManagerService {

	private final Logger log = LoggerFactory.getLogger(FileManagerServiceImpl.class);

	private static final String FILE_PID_PREFIX = "file";
	private static final String FILE_SAVE_DIRECTORY = "C:/orderfleet/files/";
	// private static final String FILE_EXTENSION = ".of";

	private final PersistentFileRepository persistentFileRepository;
	private final FileRepository fileRepository;

	@Inject
	public FileManagerServiceImpl(PersistentFileRepository persistentFileRepository, FileRepository fileRepository) {
		this.persistentFileRepository = persistentFileRepository;
		this.fileRepository = fileRepository;
	}

	@Override
	public File processFileUpload(final byte[] fileBytes, String originalFileName, String mimeType)
			throws FileManagerException {
		if (mimeType.split("/")[0].equalsIgnoreCase("image")) {
			return processImageUpload(fileBytes, originalFileName, mimeType);
		}
		final String md5 = getMD5(fileBytes) + Instant.now().toEpochMilli();
//		String fileLocation = getFileSaveDirectory() + "/" + LocalDate.now().getMonthValue() + "-"
//				+ LocalDate.now().getYear() + "/" + md5 + "_" + originalFileName;
		
		String fileLocation = getFileSaveDirectory() + "/" + md5 + "_" + originalFileName;
		// write to disk
		writeToDisk(fileBytes, fileLocation);
		PersistentFile persistentFile = new PersistentFile(fileLocation, md5);
		// save to persistent-file table
		PersistentFile savedPersistentFile = this.persistentFileRepository.save(persistentFile);
		return this.saveToFileTable(savedPersistentFile, originalFileName, mimeType);
	}

	private File processImageUpload(final byte[] fileBytes, String originalFileName, String mimeType)
			throws FileManagerException {
		try {
			InputStream in = new ByteArrayInputStream(fileBytes);
			BufferedImage biNewImg = ImageIO.read(in);
			DataBuffer dbNewImg = biNewImg.getData().getDataBuffer();
			int sizeNewImg = dbNewImg.getSize();

			final String fileNameExtension = getFileNameExtension(originalFileName);
			final String md5 = getMD5(fileBytes) + Instant.now().toEpochMilli();

			log.debug("--------------------------------------------------------------------------------");
			log.debug("File Name : " + originalFileName);
			log.debug("MIME Type : " + mimeType);
			log.debug("File Extension : " + fileNameExtension);
			log.debug("MD5 : " + md5);
			log.debug("File save Directory : " + getFileSaveDirectory());
			log.debug("--------------------------------------------------------------------------------");

			// Check MD5 already exist.
			PersistentFile persistentFile = persistentFileRepository.findOneByMd5(md5);
			if (persistentFile == null) {
				// String fileLocation = getFileSaveDirectory() + "/" + md5 +
				// FILE_EXTENSION;
				String fileLocation = getFileSaveDirectory() + "/" + originalFileName;
				// write to disk
				writeToDisk(fileBytes, fileLocation);
				persistentFile = new PersistentFile(fileLocation, md5);
			} else {
				BufferedImage biExistImg = ImageIO.read(new java.io.File(persistentFile.getLocation()));
				DataBuffer dbExistImg = biExistImg.getData().getDataBuffer();
				int sizeExistImg = dbExistImg.getSize();
				if (sizeNewImg != sizeExistImg) {
					// FILE_EXTENSION;
					String fileLocation = getFileSaveDirectory() + "/" + originalFileName;
					// write to disk
					writeToDisk(fileBytes, fileLocation);
					persistentFile = new PersistentFile(fileLocation, md5);
				}
			}
			// save to persistent-file table
			PersistentFile savedPersistentFile = this.persistentFileRepository.save(persistentFile);
			return this.saveToFileTable(savedPersistentFile, originalFileName, mimeType);
		} catch (IOException ioe) {
			throw new FileManagerException(ioe.getMessage());
		}
	}

	private File saveToFileTable(PersistentFile savedPersistentFile, String originalFileName, String mimeType) {
		// save to file table
		File newFile = new File();
		newFile.setPid(FILE_PID_PREFIX + RandomUtil.generatePid());
		newFile.setPersistentFile(savedPersistentFile);
		newFile.setFileName(originalFileName);
		newFile.setMimeType(mimeType);
		newFile.setDescription("Test file upload");
		newFile.setUploadedDate(LocalDateTime.now());
		return this.fileRepository.save(newFile);
	}

	private void writeToDisk(byte[] fileBytes, String fileLocation) throws FileManagerException {
		try {
			java.io.File fileDir = new java.io.File(getFileSaveDirectory());
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			FileCopyUtils.copy(fileBytes, new java.io.File(fileLocation));
		} catch (IOException ioe) {
			throw new FileManagerException(ioe.getMessage());
		}
	}

	@Override
	public java.io.File getPhysicalFileByFile(File file) {
		String fileSaveDirectory = file.getPersistentFile().getLocation();
		java.io.File physicalFile = new java.io.File(fileSaveDirectory);
		return physicalFile;
	}

	private String getMD5(final byte[] fileBytes) {
		try {
			return FileUtil.getMd5(fileBytes);
		} catch (NoSuchAlgorithmException ex) {
			return null;
		}
	}

	private String getFileNameExtension(final String fileName) {
		int dotIndex = fileName.lastIndexOf('.');
		return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
	}

	private String getFileSaveDirectory() {
		return FILE_SAVE_DIRECTORY + SecurityUtils.getCurrentUsersCompanyId();
	}

	@Override
	public Optional<File> findOneByPid(String pid) {
		return fileRepository.findOneByPid(pid);
	}

	@Override
	public void deleteFile(File file) {
		fileRepository.delete(file);
	}

	@Override
	public Resource loadAsResource(String filePid) throws FileManagerException {
		Optional<File> optionalFile = this.findOneByPid(filePid);
		if (!optionalFile.isPresent()) {
			throw new FileManagerException("Could not find file with pid : " + filePid);
		}
		File savedFile = optionalFile.get();
		try {
			String fileSaveDirectory = optionalFile.get().getPersistentFile().getLocation();
			Path file = Paths.get(fileSaveDirectory);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new FileManagerException("Could not read file: " + savedFile.getFileName());
			}
		} catch (MalformedURLException e) {
			throw new FileManagerException("Could not read file: " + savedFile.getFileName() + " => " + e.getMessage());
		}
	}
}
