package com.orderfleet.webapp.web.rest.tallypartner;

import java.net.URISyntaxException;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.TallyCompany;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.TallyCompanyService;

/**
 * a xml request resource.
 *
 * @author Sarath
 * @since Feb 13, 2018
 *
 */
@RestController
@RequestMapping(value = "/api/tp/v1")
public class TallyPartnerXmlRequestResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Inject
	private TallyCompanyService tallyCompanyService;

	@RequestMapping(value = "/get-tally-company-xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String getCompanyXML() throws URISyntaxException {
		log.debug("REST request to get companies running in tally xml : {}");
		Optional<TallyCompany> opTallyCompany = tallyCompanyService
				.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if (opTallyCompany.isPresent()) {
			String resultXml = TallyMastersRequestXml.getCompanyNamesXml();
			return resultXml;
		}
		return null;
	}

	@RequestMapping(value = "/get-product-group-xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String getProductGroupXML() throws URISyntaxException {
		log.debug("REST request to get product group xml : {}");

		Optional<TallyCompany> opTallyCompany = tallyCompanyService
				.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if (opTallyCompany.isPresent()) {
			String resultXml = TallyMastersRequestXml
					.getCompanyStockGroupsXml(opTallyCompany.get().getTallyCompanyName());
			return resultXml;
		}
		return null;
	}

	@RequestMapping(value = "/get-product-category-xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String getProductCategoryXML() throws URISyntaxException {
		log.debug("REST request to get product category xml : {}");

		Optional<TallyCompany> opTallyCompany = tallyCompanyService
				.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if (opTallyCompany.isPresent()) {
			String resultXml = TallyMastersRequestXml
					.getCompanyStockCategoryXml(opTallyCompany.get().getTallyCompanyName());
			return resultXml;
		}
		return null;
	}

	@RequestMapping(value = "/get-product-profile-xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String getProductProfileXML() throws URISyntaxException {
		log.debug("REST request to get product profile xml : {}");
		Optional<TallyCompany> opTallyCompany = tallyCompanyService
				.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if (opTallyCompany.isPresent()) {
			String resultXml = TallyMastersRequestXml
					.getCompanyStockItemsXml(opTallyCompany.get().getTallyCompanyName());
			return resultXml;
		}
		return null;
	}

	@RequestMapping(value = "/get-opening-stock-xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String getOpeningStockXML() throws URISyntaxException {
		log.debug("REST request to get opening stock xml : {}");
		Optional<TallyCompany> opTallyCompany = tallyCompanyService
				.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if (opTallyCompany.isPresent()) {
			String resultXml = TallyMastersRequestXml
					.getCompanyStockSummaryBatchWiseXml(opTallyCompany.get().getTallyCompanyName());
			return resultXml;
		}
		return null;
	}

	@RequestMapping(value = "/get-price-level-xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String getPriceLevelXML() throws URISyntaxException {
		log.debug("REST request to get price level xml : {}");
		Optional<TallyCompany> opTallyCompany = tallyCompanyService
				.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if (opTallyCompany.isPresent()) {
			String resultXml = TallyMastersRequestXml
					.getCompanyPriceLevelsXml(opTallyCompany.get().getTallyCompanyName());
			return resultXml;
		}
		return null;
	}

	@RequestMapping(value = "/get-group-wise-gst-xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String getGroupWiseGSTXML() throws URISyntaxException {
		log.debug("REST request to get group wise gst xml : {}");
		Optional<TallyCompany> opTallyCompany = tallyCompanyService
				.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if (opTallyCompany.isPresent()) {
			String resultXml = TallyMastersRequestXml
					.getCompanyGSTProductGroupWiseXml(opTallyCompany.get().getTallyCompanyName());
			return resultXml;
		}
		return null;
	}

	@RequestMapping(value = "/get-master-gst-xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String getGstMasterXml() throws URISyntaxException {
		log.debug("REST request to get gst master xml : {}");
		Optional<TallyCompany> opTallyCompany = tallyCompanyService
				.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if (opTallyCompany.isPresent()) {
			String resultXml = TallyMastersRequestXml
					.getCompanyVatLedgersXml(opTallyCompany.get().getTallyCompanyName());
			return resultXml;
		}
		return null;
	}

	@RequestMapping(value = "/get-location-xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String getLocationXml() throws URISyntaxException {
		log.debug("REST request to get location xml : {}");
		Optional<TallyCompany> opTallyCompany = tallyCompanyService
				.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if (opTallyCompany.isPresent()) {
			String resultXml = TallyMastersRequestXml
					.getCompanyAccountGroupsXml(opTallyCompany.get().getTallyCompanyName());
			return resultXml;
		}
		return null;
	}

	@RequestMapping(value = "/get-account-profile-xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String getAccountProfileXml() throws URISyntaxException {
		log.debug("REST request to get account profile xml : {}");
		Optional<TallyCompany> opTallyCompany = tallyCompanyService
				.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if (opTallyCompany.isPresent()) {
			String resultXml = TallyMastersRequestXml
					.getCompanyLedgersXmlUsingFetchWithFullAddress(opTallyCompany.get().getTallyCompanyName());
			return resultXml;
		}
		return null;
	}

	@RequestMapping(value = "/get-receivables-xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String getReceivableXml() throws URISyntaxException {
		log.debug("REST request to get receivables xml : {}");
		Optional<TallyCompany> opTallyCompany = tallyCompanyService
				.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if (opTallyCompany.isPresent()) {
			String resultXml = TallyMastersRequestXml
					.getCompanyBillReceivableXml(opTallyCompany.get().getTallyCompanyName());
			return resultXml;
		}
		return null;
	}

	@RequestMapping(value = "/get-payables-xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String getPayableXml() throws URISyntaxException {
		log.debug("REST request to get payables xml : {}");
		Optional<TallyCompany> opTallyCompany = tallyCompanyService
				.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if (opTallyCompany.isPresent()) {
			String resultXml = TallyMastersRequestXml
					.getCompanyBillPayablesXml(opTallyCompany.get().getTallyCompanyName());
			return resultXml;
		}
		return null;
	}

	@RequestMapping(value = "/get-closing-balance-account-profiles-xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String getClosingBalanceAccountProfileXml() throws URISyntaxException {
		log.debug("REST request to get closing balance account profile xml : {}");
		Optional<TallyCompany> opTallyCompany = tallyCompanyService
				.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if (opTallyCompany.isPresent()) {
			String resultXml = TallyMastersRequestXml
					.getCompanyTrialBalanceXml(opTallyCompany.get().getTallyCompanyName());
			return resultXml;
		}
		return null;
	}

	@RequestMapping(value = "/get-stock-item-gst-rate-xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String getCompanyStockItemGSTRateXml() throws URISyntaxException {
		log.debug("REST request to get company stock item gst rate xml : {}");
		Optional<TallyCompany> opTallyCompany = tallyCompanyService
				.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if (opTallyCompany.isPresent()) {
			String resultXml = TallyMastersRequestXml
					.getCompanyStockItemGSTRateXml(opTallyCompany.get().getTallyCompanyName());
			return resultXml;
		}
		return null;
	}
	
	@RequestMapping(value = "/get-stockitem-with-vat-xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String getCompanyStockItemsWithVatXml() throws URISyntaxException {
		log.debug("REST request to get company stock item with vat xml : {}");
		Optional<TallyCompany> opTallyCompany = tallyCompanyService
				.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if (opTallyCompany.isPresent()) {
			String resultXml = TallyMastersRequestXml
					.getCompanyStockItemsWithVatXml(opTallyCompany.get().getTallyCompanyName());
			return resultXml;
		}
		return null;
	}
	
	
}
