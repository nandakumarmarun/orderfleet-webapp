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

import com.orderfleet.webapp.domain.BestPerformer;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.BestPerformerRepository;
import com.orderfleet.webapp.repository.EcomProductProfileRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FileManagerService;

/**
 * REST controller for managing Attendance.
 * 
 * @author Muhammed Riyas T
 * @since October 18, 2016
 */
@RestController
@RequestMapping(value = "/api")
public class DownloadImageController {

	private final Logger log = LoggerFactory.getLogger(DownloadImageController.class);

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private EcomProductProfileRepository ecomProductProfileRepository;

	@Inject
	private FileManagerService fileManagerService;

	@Inject
	private UserRepository userRepository;	

	@Inject
	private BestPerformerRepository bestPerformerRepository;

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

	@RequestMapping(value = "/product-image/{filePid}", method = RequestMethod.GET)
	public @ResponseBody void getPdf(@PathVariable String filePid, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Optional<File> file = fileManagerService.findOneByPid(filePid);
		if (file.isPresent()) {
			File file2 = file.get();
			java.io.File physicalFile = this.fileManagerService.getPhysicalFileByFile(file2);
			if (physicalFile.exists()) {
				response.setContentType(file2.getMimeType());
				response.setHeader("Content-Disposition", "inline; filename=\"" + file2.getFileName() + "\"");
				response.setContentLength((int) physicalFile.length());
				try(InputStream inputStream = new BufferedInputStream(new FileInputStream(physicalFile));){
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				}
				return;
			}
		}
		String errorMessage = "Sorry. The file you are looking for does not exist";
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
		outputStream.close();
	}

	@RequestMapping(value = "/company-image", method = RequestMethod.GET)
	public @ResponseBody void getCompanyImageCurrentUser(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String errorMessage = "";
		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if (opUser.isPresent()) {
			User user = opUser.get();
			Company company = user.getCompany();
			if (company.getLogo() != null) {
				response.setContentType(company.getLogoContentType());
				response.setHeader("Content-Disposition",
						String.format("inline; filename=\"" + company.getLegalName() + "\""));

				InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(company.getLogo()));
				FileCopyUtils.copy(inputStream, response.getOutputStream());
				return;
			} else {
				errorMessage = "Sorry. image not available";
			}
		}
		errorMessage = "Sorry. The file you are looking for does not exist";
		System.out.println(errorMessage);
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
		outputStream.close();
	}
	
	@RequestMapping(value = "/best-performer-upload", method = RequestMethod.GET)
	public @ResponseBody void getBestPerformerImageCurrentUser(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String errorMessage = "";
		BestPerformer cd = bestPerformerRepository.findOneByCompanyId();
		if (cd != null) {
			if (cd.getLogo() != null) {
				response.setContentType(cd.getLogoContentType());
//				response.setHeader("Content-Disposition",
//						String.format("inline; filename=\"" + company.getLegalName() + "\""));

				InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(cd.getLogo()));
				FileCopyUtils.copy(inputStream, response.getOutputStream());
				return;
			} else {
				errorMessage = "Sorry. image not available";
			}
		}
		errorMessage = "Sorry. The file you are looking for does not exist";
		System.out.println(errorMessage);
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
		outputStream.close();
	}

}
