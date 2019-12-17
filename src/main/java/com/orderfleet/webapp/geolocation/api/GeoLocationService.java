package com.orderfleet.webapp.geolocation.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.orderfleet.webapp.geolocation.model.CellTower;
import com.orderfleet.webapp.geolocation.model.GTowerRequest;
import com.orderfleet.webapp.geolocation.model.GTowerResponse;
import com.orderfleet.webapp.geolocation.model.GoogleLocation;
import com.orderfleet.webapp.geolocation.model.TowerLocation;
import com.orderfleet.webapp.web.rest.dto.MapDistanceApiDTO;
import com.orderfleet.webapp.web.rest.util.RestUtil;

/**
 * A location service that using Google geocoding API return the location
 * coordinates.
 * 
 * @author Shaheer
 */
@Service("geoLocationService")
public class GeoLocationService {
	private final Logger log = LoggerFactory.getLogger(GeoLocationService.class);
	
	// google api constants
	public static final String STATUS_OK = "OK";
	public static final String STATUS_ZERO_RESULTS = "ZERO_RESULTS";
	public static final String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
	public static final String STATUS_REQUEST_DENIED = "REQUEST_DENIED";
	public static final String STATUS_INVALID_REQUEST = "INVALID_REQUEST";

	// google maps api endpoint
	//private static final String GEOCODE_API_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng={lat_lng}&key={api_key}";
	private static final String GEOCODE_API_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng={lat_lng}";
	private static final String GEO_LOCATION_API_URL = "https://www.googleapis.com/geolocation/v1/geolocate?key={api_key}";
	private static final String DISTANCE_MATRIX_API_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins={source}&destinations={dest}&key={api_key}";

	@Value("${google.api.key}")
	String apiKey;
	
	private static final String OPENCAGE_API_ENDPOINT = "https://api.opencagedata.com/geocode/v1/json";
	
//	private static final String OPENCAGE_API_KEY = "af60f0d7e182487dbdb9638da38b551d";
	private static final String OPENCAGE_API_KEY = "a73de03502fc4e06b14a25277d58c839";
	
	private static final String UNABLE_FIND_LOCATION = "Unable to find location";
	
	/**
	 * Perform a reverse geocode request to find the address for a valid
	 * coordinates.
	 * 
	 * @param latLng
	 *            the coordinates that you want to reverse geocode.
	 * @return the address.
	 * @throws RestClientException
	 * @throws UnsupportedEncodingException
	 */
	public String findAddressFromLatLng(String latLng) {
		log.info("Reverse geocode using lalLan {} " , latLng);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<GoogleLocation> results = restTemplate.getForEntity(GEOCODE_API_URL, GoogleLocation.class,
				latLng);
		if (results.getStatusCode().equals(HttpStatus.OK)) {
			GoogleLocation response = results.getBody();
			String status = response.getStatus();
			if (STATUS_OK.equals(status) && !response.getResults().isEmpty()) {
				return response.getResults().get(0).getFormatted_address();
			}
			//return buildLocationErrorMessage(status);
			return findAddressFromLatLngWithOpenCage(latLng);
		}
		throw new GeoLocationServiceException(
				"API response status:" + results.getStatusCode() + " - API response object:" + results);
	}
	
	private String findAddressFromLatLngWithOpenCage(String latLng) {
		URI serviceUrl = UriComponentsBuilder.fromUriString(OPENCAGE_API_ENDPOINT)
				.queryParam("key", OPENCAGE_API_KEY)
				.queryParam("q", latLng)
				.queryParam("abbrv", 1)
				.queryParam("no_annotations", 1)
				.build().toUri();
		RestOperations template = new RestTemplate();
		ResponseEntity<String> response = template.getForEntity(serviceUrl, String.class);
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode array = mapper.readValue(response.getBody(), JsonNode.class);
			JsonNode object = array.get("results").get(0);
			return object.get("formatted").textValue();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return UNABLE_FIND_LOCATION;
	}

	/**
	 * Perform a geocode request to find the coordinates for a valid cell tower
	 * details.
	 * 
	 * @param mcc
	 * @param mnc
	 * @param cellId
	 * @param lac
	 * @return TowerLocation object contains lat,lon and address.
	 */
	public TowerLocation findAddressFromCellTower(String mcc, String mnc, String cellId, String lac) {
		log.info("Cell tower details using mcc {} , mnc {}, cellId {} , lac {} ", mcc , mnc, cellId, lac);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new CustomErrorHandler());
		GTowerRequest request = new GTowerRequest();
		request.getCellTowers().add(new CellTower(cellId, lac, mcc, mnc));
		ResponseEntity<String> response = restTemplate.postForEntity(GEO_LOCATION_API_URL, request, String.class,
				apiKey);
		String responseBody = response.getBody();
		try {
			if (RestUtil.isError(response.getStatusCode())) {
				return buildTowerErrorMessage(responseBody);
			} else {
				ObjectMapper objectMapper = new ObjectMapper();
				GTowerResponse gTowerResponse = objectMapper.readValue(responseBody, GTowerResponse.class);
				BigDecimal lat = gTowerResponse.getLocation().getLat();
				BigDecimal lan = gTowerResponse.getLocation().getLng();
				// get location from lat/lan using google api
				String location = findAddressFromLatLng(lat + "," + lan);
				return new TowerLocation(location, lat, lan);
			}
		} catch (Exception e) {
			throw new GeoLocationServiceException(
					"Google Cell Tower API error:" + e.getMessage() + " - API response object:" + responseBody);
		}
	}

	public MapDistanceApiDTO findDistance(String origin, String destination) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<MapDistanceApiDTO> response = restTemplate.getForEntity(DISTANCE_MATRIX_API_URL,
					MapDistanceApiDTO.class, origin, destination, apiKey);
			if (response.getStatusCode().equals(HttpStatus.OK)) {
				return response.getBody();
			} else {
				throw new GeoLocationServiceException(
						"Distance Matrix error : Different status : " + response.getStatusCode());
			}
		} catch (Exception e) {
			throw new GeoLocationServiceException("Distance Matrix error:" + e.getMessage());
		}
	}

	private TowerLocation buildTowerErrorMessage(String responseBody) {
		int code = JsonPath.read(responseBody, "$.error.code");
		String reason = JsonPath.read(responseBody, "$.error.errors[0].reason");
		if (403 == code && "dailyLimitExceeded".equals(reason) || "userRateLimitExceeded".equals(reason)) {
			return new TowerLocation("Unable to find location", null, null);
		} else if (404 == code) {
			return new TowerLocation("Not Found", null, null);
		} else {
			throw new GeoLocationServiceException("Google Tower API : " + responseBody);
		}
	}

	/**
	 * Calculates the distance in km between two lat/long points using the haversine
	 * formula
	 */
	public double computeDistanceBetween(double lat1, double lng1, double lat2, double lng2) {
		int r = 6371; // average radius of the earth in km
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return r * c;
	}
	
}

class CustomErrorHandler implements ResponseErrorHandler {
	
	private final Logger log = LoggerFactory.getLogger(CustomErrorHandler.class);
	
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
    	log.error("Response error: {} {}", response.getStatusCode(), response.getStatusText());
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return RestUtil.isError(response.getStatusCode());
    }
}
