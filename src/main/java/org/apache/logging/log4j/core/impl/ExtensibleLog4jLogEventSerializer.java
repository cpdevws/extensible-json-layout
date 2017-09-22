package org.apache.logging.log4j.core.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.type.SimpleType;

import java.io.IOException;
import java.util.Map;

public class ExtensibleLog4jLogEventSerializer extends BeanSerializer {

    public ExtensibleLog4jLogEventSerializer() {
        super(SimpleType.constructUnsafe(ExtensibleLog4jLogEvent.class), null, new BeanPropertyWriter[0], new BeanPropertyWriter[0]);
    }

    protected ExtensibleLog4jLogEventSerializer(BeanSerializerBase src) {
        super(src);
    }

    @Override
    protected void serializeFields(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException
    {
        if(bean instanceof ExtensibleLog4jLogEvent) {
            ExtensibleLog4jLogEvent log4jLogEvent = (ExtensibleLog4jLogEvent) bean;
            BeanSerializer log4jEventSerializer = (BeanSerializer) provider.findValueSerializer(log4jLogEvent.getEvent().getClass());
            new ExtensibleLog4jLogEventSerializer(log4jEventSerializer).serializeFieldsFiltered(log4jLogEvent.getEvent(), gen, provider);
            if(log4jLogEvent.getAppContext() == null) {
                return;
            }
            for(Map.Entry<String, Object> entry : log4jLogEvent.getAppContext().entrySet()) {
                gen.writeObjectField(entry.getKey(), entry.getValue());
            }
        }
        else {
            super.serializeFields(bean, gen, provider);
        }
     }


}
