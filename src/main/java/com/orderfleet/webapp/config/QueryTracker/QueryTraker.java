package com.orderfleet.webapp.config.QueryTracker;

import com.orderfleet.webapp.web.rest.ItemWiseSaleResourceJonarin;
import com.orderfleet.webapp.web.rest.dto.QueryTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class QueryTraker {

  private final Logger log = LoggerFactory.getLogger(ItemWiseSaleResourceJonarin.class);
  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");

  public QueryTraker() {
  }

  public QueryTime queryRunStatusIntialize(String id, String description) {
    DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
    DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDateTime startLCTime = LocalDateTime.now();
    String startTime = startLCTime.format(DATE_TIME_FORMAT);
    String startDate = startLCTime.format(DATE_FORMAT);
    QueryTime queryTime = new QueryTime(startLCTime,startTime,startDate);
    logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
    return queryTime;
  }


  public void queryRunStatus(String id, String description, LocalDateTime startLCTime, String startTime) {
    DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
    DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    String flag = "Normal";
    LocalDateTime endLCTime = LocalDateTime.now();
    String endTime = endLCTime.format(DATE_TIME_FORMAT);
    String endDate = startLCTime.format(DATE_FORMAT);
    Duration duration = Duration.between(startLCTime, endLCTime);
    long minutes = duration.toMinutes();
    if (minutes <= 1 && minutes >= 0) {
      flag = "Fast";
    }
    if (minutes > 1 && minutes <= 2) {
      flag = "Normal";
    }
    if (minutes > 2 && minutes <= 10) {
      flag = "Slow";
    }
    if (minutes > 10) {
      flag = "Dead Slow";
    }
    logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
        + description);
  }



}
