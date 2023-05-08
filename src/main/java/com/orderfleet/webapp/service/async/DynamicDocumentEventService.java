package com.orderfleet.webapp.service.async;

import com.orderfleet.webapp.async.event.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DynamicDocumentEventService {
    @Autowired
    private EventProducer EventProducer;

    public void DynamicDocumentChooser(String content) {
        if(!content.isEmpty()) {
            EventProducer.documentContentPublishedEventPublished(content);
        }

    }
}
