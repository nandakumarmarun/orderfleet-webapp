package com.orderfleet.webapp.web.vendor.ujtirunelveli.controller;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orderfleet.webapp.web.rest.UploadUncleJohnResource;
import com.orderfleet.webapp.web.vendor.ujtirunelveli.service.UjTirunelveliAccountService;
import com.orderfleet.webapp.web.vendor.ujtirunelveli.service.UjTirunelveliProductService;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.AccountProfileResponseUj;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.DealerResponseUJ;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.ProductProfileResponceUJ;
import net.minidev.json.parser.ParseException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller handling Uncle John Trichy data uploads for product and account profiles.
 * This controller interacts with external APIs to fetch data and updates the local database.
 */
@Controller
@RequestMapping("/web")
public class UjTirunelveliResource {

    private final Logger log = LoggerFactory.getLogger(UploadUncleJohnResource.class);

    @Inject
    private UjTirunelveliProductService ujTirunelveliProductService;

    @Inject
    private UjTirunelveliAccountService ujTirunelveliAccountService;

    /**
     * Displays the upload page for Uncle John Trichy Masters.
     *
     * @param model The Spring MVC model.
     * @return The name of the HTML template for the upload page.
     */
    @RequestMapping(value = "/upload-uncleJhon-tirunelveli", method = RequestMethod.GET)
    @Timed
    @Transactional(readOnly = true)
    public String uploadXls(Model model) throws URISyntaxException {
        log.debug("Web request to get a page of Upload Uncle Jhon Masters");
        return "company/uploadUncleJhonTirunelveli";
    }


    /**
     * Handles the upload of Uncle John Tirunelveli product profiles.
     * Fetches data from an external API and updates the local database.
     *
     *
     * @return ResponseEntity indicating the success of the operation.
     * @throws IOException       If an I/O error occurs during the upload process.
     * @throws JSONException     If there is an issue with JSON processing.
     * @throws ParseException    If there is an issue parsing the response.
     */
    @RequestMapping(value = "/upload-uncleJhon-tirunelveli/upload-product-profiles",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> uploadProductProfiles()
            throws IOException, JSONException, ParseException {
        log.debug("Web request to upload Product Profiles...");
        RestTemplate restTemplate = new RestTemplate();

        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("endPoint", "ret_itemmast");
        requestBody.put("apiKey", 11111);
        Map<String, String> payload = new HashMap<>();
        payload.put("factory", "TI");
        requestBody.put("payload", payload);

        //Creating Request Entity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        log.info("RequestEntity :" + requestEntity);

        //Fetching Data From their product data
        ResponseEntity<ProductProfileResponceUJ> productProfileResponse = restTemplate.exchange(
                "http://192.168.2.54/?request=apiNtrich", HttpMethod.POST, requestEntity,
                ProductProfileResponceUJ.class);

        log.debug("Response created");
        log.debug("Size :" + productProfileResponse.getBody().getProductProfileUJ().getProductUJ().size());
        log.debug("Size :" + productProfileResponse.getBody().getProductProfileUJ().getProductUJ().size());

        if (productProfileResponse.getBody().getProductProfileUJ().getProductUJ() != null) {
            ujTirunelveliProductService
                    .saveUpdateProductProfiles(productProfileResponse
                            .getBody().getProductProfileUJ().getProductUJ());
            ujTirunelveliProductService
                    .saveProductGroupProduct(productProfileResponse
                            .getBody().getProductProfileUJ().getProductUJ());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Handles the upload of Uncle John Tirunelveli account profiles.
     * Fetches data from an external API and updates the local database.
     *
     * @return ResponseEntity indicating the success of the operation.
     * @throws IOException       If an I/O error occurs during the upload process.
     * @throws JSONException     If there is an issue with JSON processing.
     * @throws ParseException    If there is an issue parsing the response.
     */
    @RequestMapping(value = "/upload-uncleJhon-tirunelveli/upload-account-profiles",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> uploadAccountProfiles()
            throws IOException, JSONException, ParseException {
        log.debug("Web request to upload Account Profiles");
        RestTemplate restTemplate = new RestTemplate();

        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("endPoint", "ret_custmast");
        requestBody.put("apiKey", 11111);
        Map<String, String> payload = new HashMap<>();
        payload.put("factory", "TI");
        requestBody.put("payload", payload);

        //Creating Request Entity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        log.debug("RequestEntity :" + requestEntity);

        //Fetching Data From their customer data
        ResponseEntity<AccountProfileResponseUj> accountProfileResponse =
                restTemplate.exchange("http://192.168.2.54/?request=apiNtrich",
                        HttpMethod.POST, requestEntity,AccountProfileResponseUj.class);

        log.debug("Web request to upload Dealer Profiles");
        Map<String, Object> dealerBody = new HashMap<>();
        dealerBody.put("endPoint", "ret_dealmast");
        dealerBody.put("apiKey", 11111);
        payload.put("factory", "TI");
        dealerBody.put("payload", payload);

        HttpEntity<Map<String, Object>> dealerEntity = new HttpEntity<>(dealerBody, headers);
        log.debug("dealerEntity : " + dealerEntity);

        //Fetching Data From their dealer data
        ResponseEntity<DealerResponseUJ> dealerResponse =
                restTemplate.exchange("http://192.168.2.54/?request=apiNtrich",
                HttpMethod.POST, dealerEntity, DealerResponseUJ.class);

        log.debug("Response created");
        log.debug(" Customer Size : " + accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ().size());
        convertToJson(accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ(), " Customer Data ");
        log.debug(" Dealer Size : " + dealerResponse.getBody().getDealerUJ().getDealer());
        convertToJson(accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ(), " Dealers Data ");

        if (accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ() != null
                && dealerResponse.getBody().getDealerUJ().getDealer() != null) {

            ujTirunelveliAccountService.saveUpdateLocations(accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ(),
                    dealerResponse.getBody().getDealerUJ().getDealer());

            ujTirunelveliAccountService
                    .saveUpdateAccounts(accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ());

            ujTirunelveliAccountService.saveAccountProfileGeoLocation(
                    accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ());

            ujTirunelveliAccountService
                    .saveDealer(dealerResponse.getBody().getDealerUJ().getDealer());

            ujTirunelveliAccountService
                    .saveDistributorDealerAssociation(dealerResponse.getBody().getDealerUJ().getDealer());

            ujTirunelveliAccountService
                    .saveUpdateLocationAccounts(accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ(),
                    dealerResponse.getBody().getDealerUJ().getDealer());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    public <T> void convertToJson(
            Object collection,String messagae) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            log.info(System.lineSeparator());
            String jsonString = objectMapper.writeValueAsString(collection);
            log.debug(messagae  + jsonString);
            log.info(System.lineSeparator());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectMapper getObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        return mapper;
    }

}
