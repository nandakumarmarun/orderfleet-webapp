package com.orderfleet.webapp.web.rest;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.KnowledgebaseFilesService;
import com.orderfleet.webapp.service.KnowledgebaseService;
import com.orderfleet.webapp.service.UserKnowledgebaseFileService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.impl.FileManagerException;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.KnowledgebaseFileDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing KnowledgebaseFile.
 * 
 * @author Muhammed Riyas T
 * @since August 12, 2016
 */
@Controller
@RequestMapping("/web")
public class KnowledgebaseFileResource {

	private final Logger log = LoggerFactory.getLogger(KnowledgebaseFileResource.class);

	@Inject
	private KnowledgebaseFilesService knowledgebaseFilesService;

	@Inject
	private KnowledgebaseService knowledgebaseService;

	@Inject
	private UserService userService;

	@Inject
	private FileManagerService fileManagerService;

	@Inject
	private UserKnowledgebaseFileService userKnowledgebaseFileService;

	@Transactional
	@RequestMapping(value = "/knowledgebase-files", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> uploadFile(@RequestParam(name = "file", required = false) MultipartFile file,
			KnowledgebaseFileDTO knowledgebaseFileDTO) {
		log.debug("Request to upload a file : {}", file);
		if (file != null && file.isEmpty()) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("fileUpload", "Nocontent", "Invalid file upload: No content"))
					.body(null);
		}
		try {
			if (knowledgebaseFileDTO.getPid() == null || knowledgebaseFileDTO.getPid().isEmpty()) {
				knowledgebaseFilesService.saveKnowledgebaseFile(knowledgebaseFileDTO, file);
			} else {
				knowledgebaseFilesService.update(knowledgebaseFileDTO, file);
			}
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (FileManagerException | IOException ex) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("fileUpload", "exception", ex.getMessage())).body(null);
		}
	}

	/**
	 * GET /knowledgebase-files : get all the knowledgebase files.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         knowledgebases in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/knowledgebase-files", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllKnowledgebaseFiles(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of KnowledgebaseFiles");
		List<KnowledgebaseFileDTO> pageKnowledgebaseFile = knowledgebaseFilesService.findAllByCompanyId();
		model.addAttribute("knowledgebaseFiles", pageKnowledgebaseFile);

		model.addAttribute("knowledgebases", knowledgebaseService.findAllByCompany());

		model.addAttribute("users", userService.findAllByCompany());
		return "company/knowledgebaseFiles";
	}

	/**
	 * GET /knowledgebase-files/:pid : get the "pid" knowledgebase file.
	 *
	 * @param pid
	 *            the pid of the KnowledgebaseFileDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         KnowledgebaseFileDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/knowledgebase-files/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<KnowledgebaseFileDTO> getKnowledgebaseFile(@PathVariable String pid) {
		log.debug("Web request to get KnowledgebaseFile by pid : {}", pid);
		return knowledgebaseFilesService.findOneByPid(pid)
				.map(knowledgebaseDTO -> new ResponseEntity<>(knowledgebaseDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /knowledgebase-files/:id : delete the "id" knowledgebase.
	 *
	 * @param id
	 *            the id of the knowledgebaseDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/knowledgebase-files/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteKnowledgebaseFile(@PathVariable String pid) {
		log.debug("REST request to delete KnowledgebaseFile : {}", pid);
		knowledgebaseFilesService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("knowledgebase", pid.toString()))
				.build();
	}

	/**
	 * GET /knowledgebase-files/searchTags : get searchTags.
	 *
	 * @return the ResponseEntity with status 200 (OK) and with body the List or
	 *         string, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/knowledgebase-files/searchTags", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Set<String>> getSearchTags() {
		log.debug("Web request to get SearchTags");
		Set<String> searchTags = knowledgebaseFilesService.findSearchTagsByCompany();
		Set<String> result = new HashSet<>();
		for (String searchTag : searchTags) {
			String[] searchTagArray = searchTag.split(",");
			result.addAll(Arrays.asList(searchTagArray));
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/knowledgebase-files/view-file/{filePid}", method = RequestMethod.GET)
	public @ResponseBody void getPdf(@PathVariable String filePid, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.info("show knowledgebase file................." + filePid);
		Optional<File> file = fileManagerService.findOneByPid(filePid);
		if (file.isPresent()) {
			File file2 = file.get();
			java.io.File physicalFile = this.fileManagerService.getPhysicalFileByFile(file2);
			if (physicalFile.exists()) {
				// String mimeType =
				// URLConnection.guessContentTypeFromName(physicalFile.getName());
				response.setContentType(file2.getMimeType());
				response.setHeader("Content-Disposition",
						String.format("inline; filename=\"" + file2.getFileName() + "\""));
				response.setContentLength((int) physicalFile.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(physicalFile));

				FileCopyUtils.copy(inputStream, response.getOutputStream());
				return;
			}
		}
		String errorMessage = "Sorry. The file you are looking for does not exist";
		System.out.println(errorMessage);
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
		outputStream.close();
	}

	/**
	 * DELETE /knowledgebase-files/users/:id : delete the "id" knowledgebase.
	 *
	 * @param id
	 *            the id of the knowledgebaseDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/knowledgebase-files/users/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<UserDTO>> getKnowledgebaseFileUsers(@PathVariable String pid) {
		log.debug("REST request to get KnowledgebaseFile Users : {}", pid);
		return new ResponseEntity<>(userKnowledgebaseFileService.findUsersByKnowledgebaseFilePid(pid), HttpStatus.OK);
	}

	@RequestMapping(value = "/knowledgebase-files/assignUsers", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> assignUsers(@RequestParam String pid, @RequestParam String assignedUsers) {
		log.debug("REST request to save assigned Users: {}", pid);
		knowledgebaseFilesService.saveAssignedUsers(pid, assignedUsers);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
