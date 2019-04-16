package com.orderfleet.webapp.web.rest.api;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.repository.FileRepository;
import com.orderfleet.webapp.service.FileManagerService;

@RestController
@RequestMapping("/api")
@Transactional(readOnly = true)
public class FileController {

	private final Logger log = LoggerFactory.getLogger(FileController.class);
	
	@Inject
	private FileRepository fileRepository;
	
	@Inject
	private FileManagerService fileManagerService;

	@RequestMapping(path = "/knowledgebase/{fileId}/download", method = RequestMethod.GET)
	public void downloadFilledFormFile(@PathVariable("fileId") String fileId, HttpServletResponse response)
			throws IOException {
		log.info("@@@@@@@@@@@@@@@@@@@@@@@@@ Mtech downloading knowledgebase file with ID : {}", fileId);
		Optional<File> savedFile = fileRepository.findOneByPid(fileId);
		if(savedFile.isPresent()) {
			File file = savedFile.get();
			java.io.File physicalFile = this.fileManagerService.getPhysicalFileByFile(file);
			if (physicalFile.exists()) {
				response.setContentType(file.getMimeType());
				response.setHeader("Content-Disposition",
						String.format("inline; filename = %s" ,file.getFileName()));
				response.setContentLength((int) physicalFile.length());
				try(InputStream inputStream = new BufferedInputStream(new FileInputStream(physicalFile));){
					FileCopyUtils.copy(inputStream, response.getOutputStream());
					return;
				}
			}
			String errorMessage = "Sorry. The file you are looking for does not exist";
			log.error(errorMessage);
			OutputStream outputStream = response.getOutputStream();
			outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
			outputStream.close();
		}
	}
}
