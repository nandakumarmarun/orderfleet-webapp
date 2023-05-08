package com.orderfleet.webapp.async.event;


import java.text.SimpleDateFormat;
import java.util.List;

import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.AttendanceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;

@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final String topic="events";


    public void snrichUserSynk(List<UserDTO> userDTOS) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            String userJson = objectMapper.writeValueAsString(userDTOS);
            String jsonSnrichUsers = objectMapper.writeValueAsString(new Events("snrichUser",userJson));
            kafkaTemplate.send(topic,jsonSnrichUsers);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void snrichUserDisable(List<UserDTO> userDTOS) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            String userJson = objectMapper.writeValueAsString(userDTOS);
            String jsonSnrichUsers = objectMapper.writeValueAsString(new Events("snrichUserDisable",userJson));
            kafkaTemplate.send(topic,jsonSnrichUsers);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void dynamicDocumentPublish(DynamicDocumentHeaderDTO documentheader) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            String jsonDynamicDocument = objectMapper.writeValueAsString(documentheader);
            System.out.println("Dynamic Document Json*******************"+jsonDynamicDocument);
            String jsonSnrichDynamicDocumentIntegrationEvent = objectMapper.writeValueAsString(new Events("SnrichDynamicDocument",jsonDynamicDocument));
            kafkaTemplate.send(topic,jsonSnrichDynamicDocumentIntegrationEvent);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void attendancePublish(AttendanceDTO attendanceDTO) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            String jsonAttendance = objectMapper.writeValueAsString(attendanceDTO);
            System.out.println("Attendance Json*******************"+attendanceDTO);
            String jsonSnrichAttendanceEvent = objectMapper.writeValueAsString(new Events("SnrichAttendance",jsonAttendance));
            kafkaTemplate.send(topic,jsonSnrichAttendanceEvent);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void documentContentPublishedEventPublished(String content) {
        ObjectMapper objectMapper = getObjectMapper();
        try {
            String jsonlead = objectMapper.writeValueAsString(new Events("documentContentPublished",content));
            kafkaTemplate.send(topic,jsonlead);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
//  public void InfluencerVisitEventPublished(String content) {
//	  ObjectMapper objectMapper = getObjectMapper();
//	  try {
//		 String jsonlead = objectMapper.writeValueAsString(new Events("influencerVisitPublished",content));
//		 kafkaTemplate.send(topic,jsonlead);
//	} catch (JsonProcessingException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//  }

    public ObjectMapper getObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        return mapper;
    }
}
