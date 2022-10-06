package com.orderfleet.webapp.service;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.web.rest.api.dto.ReleaseInfo;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

@Service
public class ReleaseInfoService {
	
	private final Logger log = LoggerFactory.getLogger(ReleaseInfoService.class);
	
	public ReleaseInfo getReleaseInfoDetails() {
		log.info("get Release Info Details");
		JSONParser parser = new JSONParser();
		 ReleaseInfo releaseInfo = new ReleaseInfo();
		try {
			Object obj = parser.parse(new FileReader(System.getProperty("user.dir")+"/src/main/resources/release-info.json"));
			JSONObject jsonObj = (JSONObject) obj;
			 String releaseNumber = (String)jsonObj.get("release-number");
	         String releaseDate = (String)jsonObj.get("release-date");
	         JSONArray features = (JSONArray)jsonObj.get("features");
	         List<String> featurs =  new ArrayList<>();
	         for (Object f : features)
	         {
	        	 String featurObj = new String();
	           featurObj = (String) f;
	           featurs.add(featurObj);   
	         }
	         releaseInfo.setReleaseNumber(releaseNumber);
	         releaseInfo.setReleaseDate(releaseDate);
	         releaseInfo.setFeatures(featurs);
	         
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return releaseInfo;
	}

}
