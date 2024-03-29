package com.orderfleet.webapp.web.rest.api;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.repository.EcomProductProfileRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.service.FileManagerService;

/**
 * REST controller for managing image-apis.
 * 
 * @author Sarath
 * @since Mar 7, 2018
 */
@RestController
@RequestMapping(value = "/image")
public class PublicImageApis {

	private final Logger log = LoggerFactory.getLogger(PublicImageApis.class);

	@Inject
	private FileManagerService fileManagerService;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private EcomProductProfileRepository ecomProductProfileRepository;

	@RequestMapping(value = "/product-group-image/{productGroupPid}", method = RequestMethod.GET)
	public @ResponseBody void getproductGroupImage(@PathVariable String productGroupPid, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.info("show ProductGroup image................." + productGroupPid);

		String errorMessage = "";
		Optional<ProductGroup> opProductGroup = productGroupRepository.findOneByPid(productGroupPid);
		if (opProductGroup.isPresent()) {
			ProductGroup productGroup = opProductGroup.get();
			if (productGroup.getImage() != null) {

				response.setContentType(productGroup.getImageContentType());
				response.setHeader("Content-Disposition",
						String.format("inline; filename=\"" + productGroup.getName() + "\""));

				InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(productGroup.getImage()));
				FileCopyUtils.copy(inputStream, response.getOutputStream());
				return;
			} else {
				errorMessage = "Sorry. image not available";
			}
		} else {
			errorMessage = "Sorry. The file you are looking for does not exist";
		}
		System.out.println(errorMessage);
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
		outputStream.close();
	}

	@RequestMapping(value = "/product-image/{filePid}", method = RequestMethod.GET)
	public @ResponseBody void getPdf(@PathVariable String filePid, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.info("show product image................." + filePid);
		Optional<File> file = fileManagerService.findOneByPid(filePid);
		if (file.isPresent()) {
			File file2 = file.get();
			java.io.File physicalFile = this.fileManagerService.getPhysicalFileByFile(file2);
			if (physicalFile.exists()) {
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

	@RequestMapping(value = "/ecom-product-profile-image/{ecomProductPid}", method = RequestMethod.GET)
	public @ResponseBody void getEcomProductProfileImage(@PathVariable String ecomProductPid,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.info("show ecom-Product image................." + ecomProductPid);

		String errorMessage = "";
		Optional<EcomProductProfile> opEcomProductProfile = ecomProductProfileRepository.findOneByPid(ecomProductPid);
		if (opEcomProductProfile.isPresent()) {
			EcomProductProfile ecomProductProfile = opEcomProductProfile.get();
			if (ecomProductProfile.getImage() != null) {

				response.setContentType(ecomProductProfile.getImageContentType());
				response.setHeader("Content-Disposition",
						String.format("inline; filename=\"" + ecomProductProfile.getName() + "\""));

				InputStream inputStream = new BufferedInputStream(
						new ByteArrayInputStream(ecomProductProfile.getImage()));
				FileCopyUtils.copy(inputStream, response.getOutputStream());
				return;
			} else {
				errorMessage = "Sorry. image not available";
			}
		} else {
			errorMessage = "Sorry. The file you are looking for does not exist";
		}
		System.out.println(errorMessage);
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
		outputStream.close();
	}
}
