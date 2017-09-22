package org.apache.logging.log4j.core.layout.adapter;

import java.util.HashMap;
import java.util.Map;

public class TestAdapterClass implements ExtensibleJsonAdapter {

    @Override
    public Map<String, Object> getMixinAttributes() {
        return new HashMap<>();
    }
}
