package com.orderfleet.webapp.web.rest.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.impl.FileManagerException;
//import com.orderfleet.webapp.web.rest.api.dto.MenuDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * used to upload db of mobile.
 *
 * @author Sarath
 * @since Oct 18, 2017
 *
 */
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class MobileBackUpFileUploadController {

	private final Logger log = LoggerFactory.getLogger(MobileBackUpFileUploadController.class);

	private final FileManagerService fileManagerService;

	public MobileBackUpFileUploadController(FileManagerService fileManagerService) {
		super();
		this.fileManagerService = fileManagerService;
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/mobile-user/upload/db", method = RequestMethod.POST)
	public ResponseEntity<List<String>> uploadGeoLocationImage(@RequestParam("file") MultipartFile file) {
		log.debug("Rest request to upload mobile database  {}");
		if (file.isEmpty()) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("fileUpload", "Nocontent", "Invalid file upload: No content"))
					.body(null);
		}

		List<String> filePid = new ArrayList<>();
		try {
			File uploadedFile = this.fileManagerService.processFileUpload(file.getBytes(), file.getOriginalFilename(),
					file.getContentType());
			// update account profile with file
			Set<File> files = new HashSet<>();
			files.add(uploadedFile);
			filePid.add(uploadedFile.getPid());
			return new ResponseEntity<>(filePid, HttpStatus.OK);
		} catch (FileManagerException | IOException ex) {
			log.debug("File upload exception : {}", ex);
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("fileUpload", "exception", ex.getMessage())).body(null);
		}

	}

//	@Inject
//	private MenuItemRepository menuItemRepository;
//
//	@GetMapping("/menu-items")
//	@ResponseBody
//	@Timed
//	public Map<Long, MenuDTO> getMenuItems() {
//		log.debug("REST request to get api MenuItems");
//		List<MenuItem> menuItems = menuItemRepository.findByActivatedTrue();
//
//		/* This is the list of Root Menus (Menus which have no parent) */
//		Map<Long, MenuDTO> rootMenus = menuItems.stream().filter(m -> Objects.isNull(m.getParent()))
//				.sorted(Comparator.comparing(MenuItem::getSortOrder))
//				.collect(Collectors.toMap(MenuItem::getId, MenuDTO::new));
//		
//		System.out.println("===================================================");
//		System.out.println(rootMenus.keySet());
//		
//		menuItems.stream().filter(mi -> null != mi.getParent())
//				.collect(Collectors.groupingBy(m -> m.getParent().getId(), Collectors.toList()))
//				// now we have a map of parent Ids to list of MenuItems for that
//				// parent
//				.forEach((parentId, children) -> {
//					System.out.println(rootMenus.get(parentId));
//				}/*rootMenus.get(parentId)
//						.setChildMenus(children.stream().sorted(Comparator.comparing(MenuItem::getSortOrder))
//								.map(MenuDTO::new).collect(Collectors.toList())*/);
//		return rootMenus;
//	}
}
