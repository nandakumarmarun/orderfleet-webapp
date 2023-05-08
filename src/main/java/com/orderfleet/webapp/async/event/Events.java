package com.orderfleet.webapp.async.event;

public class Events {
    private String eventType;
    private String metaData;


    public Events() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Events(String eventType, String metaData) {
        super();
        this.eventType = eventType;
        this.metaData = metaData;
    }

    public String getEventType() {
        return eventType;
    }
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    public String getMetaData() {
        return metaData;
    }
    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

}
