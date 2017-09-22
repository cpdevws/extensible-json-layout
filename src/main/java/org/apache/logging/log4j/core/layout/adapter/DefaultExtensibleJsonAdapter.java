package org.apache.logging.log4j.core.layout.adapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultExtensibleJsonAdapter implements ExtensibleJsonAdapter {

    private Map<String, Object> mixinAttributes;

    public DefaultExtensibleJsonAdapter() {
        mixinAttributes = new HashMap<>();
    }

    @Override
    public Map<String, Object> getMixinAttributes() {
        return mixinAttributes;
    }

    public void addMixinAttribute(String key, Object value){
        mixinAttributes.put(key, value);
    }

}
