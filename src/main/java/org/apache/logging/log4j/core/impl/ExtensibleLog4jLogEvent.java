package org.apache.logging.log4j.core.impl;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

import java.io.Serializable;
import java.util.Map;

/**
 * Implementation of an Extensible Log4j Event.
 */
@JsonSerialize(using = ExtensibleLog4jLogEventSerializer.class)
public class ExtensibleLog4jLogEvent implements Serializable {

    private Log4jLogEvent event;
    private Map<String, Object> appContext;

    public ExtensibleLog4jLogEvent(Log4jLogEvent event, Map<String, Object> appContext) {
        this.appContext = appContext;
        this.event = event;
    }

    public Log4jLogEvent getEvent() {
        return event;
    }

    public void setEvent(Log4jLogEvent event) {
        this.event = event;
    }

    public Map<String, Object> getAppContext() {
        return appContext;
    }

    public void setAppContext(Map<String, Object> appContext) {
        this.appContext = appContext;
    }
}