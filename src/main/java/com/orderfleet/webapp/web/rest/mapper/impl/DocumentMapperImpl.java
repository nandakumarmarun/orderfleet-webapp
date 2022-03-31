package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.mapper.DocumentMapper;

@Component
public class DocumentMapperImpl extends DocumentMapper {

	@Override
	public DocumentDTO documentToDocumentDTO(Document document) {
		if (document == null) {
			return null;
		}

		DocumentDTO documentDTO = new DocumentDTO();

		documentDTO.setTermsAndConditions(document.getTermsAndConditions());
		documentDTO.setTermsAndConditionsColumn(document.isTermsAndConditionsColumn());
		documentDTO.setHasTelephonicOrder(document.getHasTelephonicOrder());
		documentDTO.setPid(document.getPid());
		documentDTO.setName(document.getName());
		documentDTO.setAlias(document.getAlias());
		documentDTO.setDescription(document.getDescription());
		documentDTO.setDocumentType(document.getDocumentType());
		documentDTO.setDocumentPrefix(document.getDocumentPrefix());
		documentDTO.setActivityAccount(document.getActivityAccount());
		documentDTO.setSave(document.getSave());
		documentDTO.setEditable(document.getEditable());
		documentDTO.setBatchEnabled(document.getBatchEnabled());
		documentDTO.setPromptStockLocation(document.getPromptStockLocation());
		documentDTO.setPhotoMandatory(document.getPhotoMandatory());
		documentDTO.setSingleVoucherMode(document.getSingleVoucherMode());
		documentDTO.setIsTakeImageFromGallery(document.getIsTakeImageFromGallery());
		documentDTO.setLastModifiedDate(document.getLastModifiedDate());
		documentDTO.setMode(document.getMode());
		documentDTO.setStockFlow(document.getStockFlow());
		documentDTO.setQrCodeEnabled(document.getQrCodeEnabled());
		documentDTO.setOrderNoEnabled(document.getOrderNoEnabled());
		documentDTO.setVoucherNumberGenerationType(document.getVoucherNumberGenerationType());
		documentDTO.setAddNewCustomer(document.getAddNewCustomer());
		if (document.getHeaderImage() != null) {
			byte[] headerImage = document.getHeaderImage();
			documentDTO.setHeaderImage(Arrays.copyOf(headerImage, headerImage.length));
		}
		if (document.getFooterImage() != null) {
			byte[] footerImage = document.getFooterImage();
			documentDTO.setFooterImage(Arrays.copyOf(footerImage, footerImage.length));
		}
		documentDTO.setRateWithTax(document.getRateWithTax());
		documentDTO.setDiscountScaleBar(document.getDiscountScaleBar());
		documentDTO.setHeaderImageContentType(document.getHeaderImageContentType());
		documentDTO.setFooterImageContentType(document.getFooterImageContentType());
        documentDTO.setDiscountPercentage(document.getDiscountPercentage());
        documentDTO.setEnableHeaderPrintOut(document.getEnableHeaderPrintOut());
		return documentDTO;
	}

	public DocumentDTO documentToDocumentDTODescription(Document document) {
		if (document == null) {
			return null;
		}

		DocumentDTO documentDTO = new DocumentDTO();

		documentDTO.setTermsAndConditions(document.getTermsAndConditions());
		documentDTO.setTermsAndConditionsColumn(document.isTermsAndConditionsColumn());
		documentDTO.setHasTelephonicOrder(document.getHasTelephonicOrder());
		documentDTO.setPid(document.getPid());
		documentDTO.setName(document.getDescription() != null && !document.getDescription().equalsIgnoreCase("common")
				? document.getDescription()
				: document.getName());
		documentDTO.setAlias(document.getAlias());
		documentDTO.setDescription(document.getDescription());
		documentDTO.setDocumentType(document.getDocumentType());
		documentDTO.setDocumentPrefix(document.getDocumentPrefix());
		documentDTO.setActivityAccount(document.getActivityAccount());
		documentDTO.setSave(document.getSave());
		documentDTO.setEditable(document.getEditable());
		documentDTO.setBatchEnabled(document.getBatchEnabled());
		documentDTO.setPromptStockLocation(document.getPromptStockLocation());
		documentDTO.setPhotoMandatory(document.getPhotoMandatory());
		documentDTO.setSingleVoucherMode(document.getSingleVoucherMode());
		documentDTO.setIsTakeImageFromGallery(document.getIsTakeImageFromGallery());
		documentDTO.setLastModifiedDate(document.getLastModifiedDate());
		documentDTO.setMode(document.getMode());
		documentDTO.setStockFlow(document.getStockFlow());
		documentDTO.setQrCodeEnabled(document.getQrCodeEnabled());
		documentDTO.setOrderNoEnabled(document.getOrderNoEnabled());
		documentDTO.setVoucherNumberGenerationType(document.getVoucherNumberGenerationType());
		documentDTO.setAddNewCustomer(document.getAddNewCustomer());
		if (document.getHeaderImage() != null) {
			byte[] headerImage = document.getHeaderImage();
			documentDTO.setHeaderImage(Arrays.copyOf(headerImage, headerImage.length));
		}
		if (document.getFooterImage() != null) {
			byte[] footerImage = document.getFooterImage();
			documentDTO.setFooterImage(Arrays.copyOf(footerImage, footerImage.length));
		}
		documentDTO.setRateWithTax(document.getRateWithTax());
		documentDTO.setDiscountScaleBar(document.getDiscountScaleBar());
		documentDTO.setHeaderImageContentType(document.getHeaderImageContentType());
		documentDTO.setFooterImageContentType(document.getFooterImageContentType());
        documentDTO.setDiscountPercentage(document.getDiscountPercentage());
        documentDTO.setEnableHeaderPrintOut(document.getEnableHeaderPrintOut());
		return documentDTO;
	}

	@Override
	public List<DocumentDTO> documentsToDocumentDTOs(List<Document> documents) {
		if (documents == null) {
			return null;
		}

		List<DocumentDTO> list = new ArrayList<DocumentDTO>();
		if (getCompanyCofig()) {
			for (Document document : documents) {
				list.add(documentToDocumentDTODescription(document));
			}
		} else {
			for (Document document : documents) {
				list.add(documentToDocumentDTO(document));
			}
		}

		return list;
	}

	@Override
	public Document documentDTOToDocument(DocumentDTO documentDTO) {
		if (documentDTO == null) {
			return null;
		}

		Document document = new Document();

		document.setPid(documentDTO.getPid());
		document.setName(documentDTO.getName());
		document.setDocumentPrefix(documentDTO.getDocumentPrefix());
		document.setAlias(documentDTO.getAlias());
		document.setDescription(documentDTO.getDescription());
		document.setDocumentType(documentDTO.getDocumentType());
		document.setActivityAccount(documentDTO.getActivityAccount());
		document.setSave(documentDTO.getSave());
		document.setEditable(documentDTO.getEditable());
		document.setBatchEnabled(documentDTO.getBatchEnabled());
		document.setHeaderImageContentType(documentDTO.getHeaderImageContentType());
		document.setFooterImageContentType(documentDTO.getFooterImageContentType());
		document.setPromptStockLocation(documentDTO.isPromptStockLocation());
		document.setSingleVoucherMode(documentDTO.getSingleVoucherMode());
		document.setPhotoMandatory(documentDTO.getPhotoMandatory());
		document.setIsTakeImageFromGallery(documentDTO.getIsTakeImageFromGallery());
		document.setMode(documentDTO.getMode());
		document.setStockFlow(documentDTO.getStockFlow());
		document.setQrCodeEnabled(documentDTO.getQrCodeEnabled());
		document.setOrderNoEnabled(documentDTO.getOrderNoEnabled());
		document.setVoucherNumberGenerationType(documentDTO.getVoucherNumberGenerationType());
		document.setAddNewCustomer(documentDTO.getAddNewCustomer());
		document.setTermsAndConditions(documentDTO.getTermsAndConditions());
		document.setTermsAndConditionsColumn(documentDTO.isTermsAndConditionsColumn());
		document.setHasTelephonicOrder(documentDTO.getHasTelephonicOrder());
		document.setRateWithTax(documentDTO.getRateWithTax());
		document.setDiscountScaleBar(documentDTO.getDiscountScaleBar());
		document.setEnableHeaderPrintOut(documentDTO.getEnableHeaderPrintOut());
		document.setDiscountPercentage(documentDTO.getDiscountPercentage());
		if (documentDTO.getHeaderImage() != null) {
			byte[] headerImage = documentDTO.getHeaderImage();
			document.setHeaderImage(Arrays.copyOf(headerImage, headerImage.length));
		}
		if (documentDTO.getFooterImage() != null) {
			byte[] footerImage = documentDTO.getFooterImage();
			document.setFooterImage(Arrays.copyOf(footerImage, footerImage.length));
		}
		return document;
	}

	@Override
	public List<Document> documentDTOsToDocuments(List<DocumentDTO> documentDTOs) {
		if (documentDTOs == null) {
			return null;
		}

		List<Document> list = new ArrayList<Document>();
		for (DocumentDTO documentDTO : documentDTOs) {
			list.add(documentDTOToDocument(documentDTO));
		}

		return list;
	}

	private String documentName(Document document) {
		if (document.getDescription() != null && getCompanyCofig() && !document.getDescription().equals("common")) {
			return document.getDescription();
		}

		return document.getName();
	}
}
