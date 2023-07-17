package com.orderfleet.webapp.web.rest.DownloadLog;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/web")
public class DownloadLog {

  @RequestMapping(value = "/get-log", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public String  initializeView(Model model){
    return "print/downloadlog";
  }

  @RequestMapping(value = "/get-log/logs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Resource> fetchLogs(@RequestParam ("date") LocalDate date){

    Resource resource = null;
    try {
      resource = loadFileAsResource(date);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    String contentType = "application/octet-stream";

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(resource);
  };

  private Resource loadFileAsResource(LocalDate date) throws IOException {
    String fileName = "logFile." + date + ".log";
    String userDirectory = System.getProperty("user.dir");
    Path filePath = Paths.get(userDirectory +"\\"+ fileName);
    System.out.println("Path : " + userDirectory);
    Resource resource = new UrlResource(filePath.toUri());

    if (resource.exists()) {
      return resource;
    } else {
      throw new FileNotFoundException("File not found: " + fileName);
    }
  }



}
