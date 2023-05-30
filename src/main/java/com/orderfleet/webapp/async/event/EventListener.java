package com.orderfleet.webapp.async.event;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import com.orderfleet.webapp.web.rest.CrmApplicationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orderfleet.webapp.service.async.DynamicDocumentEventService;

@Component
public class EventListener {
    @Autowired
    private DynamicDocumentEventService dynamicDocumentEventService;

    @Autowired
    private CrmApplicationResource crmApplicationResource;

    @KafkaListener(topics = "events")
    public void eventListener(String events) throws IOException {
        ObjectMapper objectMapper = getObjectMapper();
        if(!events.isEmpty()){
            Events event = objectMapper.readValue(events, Events.class);
            if(event != null && event.getEventType().toString().equals("dynamicDocumentACK")){
                System.out.println("ackk++++++++"+event.getMetaData());
                JsonNode rootNode = objectMapper.readTree(event.getMetaData());
                dynamicDocumentEventService.DynamicDocumentChooser( event.getMetaData());
            }
            if(event != null && event.getEventType().toString().equals("inventoryVoucherACK")){
                System.out.println("ackk++++++++"+event.getMetaData());
//                JsonNode rootNode = objectMapper.readTree(event.getMetaData());
                dynamicDocumentEventService.InventoryChooser(event.getMetaData());
            }
            if(event != null && event.getEventType().toString().equals("accountingVoucherACK")){
                System.out.println("ackk++++++++"+event.getMetaData());
//                JsonNode rootNode = objectMapper.readTree(event.getMetaData());
                dynamicDocumentEventService.AccountingChooser(event.getMetaData());
            }
            if(event != null && event.getEventType().toString().equals("SnrichCompanyUserApprovedAck")){
                System.out.println("ackk++++++++"+event.getMetaData());
                String[] userPid = objectMapper.readValue(event.getMetaData(), String[].class);
                List<String> userPis = Arrays.asList(userPid);
                crmApplicationResource.updateUserStatus(userPis,true);
            }
            if(event != null && event.getEventType().toString().equals("SnrichCompanyUserDisableAck")){
                System.out.println("ackk++++++++"+event.getMetaData());
                String[] userPid = objectMapper.readValue(event.getMetaData(), String[].class);
                List<String> userPis = Arrays.asList(userPid);
                crmApplicationResource.updateUserStatus(userPis,false);
            }
        }
    }

    public ObjectMapper getObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        return mapper;
    }
}
