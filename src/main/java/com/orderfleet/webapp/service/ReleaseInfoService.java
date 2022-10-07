package com.orderfleet.webapp.service;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

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
			ClassPathResource classPathResource = new ClassPathResource("release-info.json");
			byte[] binaryData = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());
            String strJson = new String(binaryData, StandardCharsets.UTF_8);
//			File file = ResourceUtils.getFile("classpath:release-info.json");
//			Object obj = parser.parse(new FileReader(file));
            
			JSONObject jsonObj = (JSONObject) parser.parse(strJson);
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
