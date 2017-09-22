package org.apache.logging.log4j.core.layout;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.impl.ExtensibleLog4jLogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.core.layout.adapter.ExtensibleJsonAdapter;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Plugin(name = "ExtensibleJsonLayout", category = Node.CATEGORY, elementType = Layout.ELEMENT_TYPE, printObject = true)
public class ExtensibleJsonLayout extends AbstractJacksonLayout  {

    private static final String DEFAULT_FOOTER = "]";
    private static final String DEFAULT_HEADER = "[";
    protected static final String DEFAULT_ADAPTER_CLASS_NAME = "org.apache.logging.log4j.core.layout.adapter.DefaultExtensibleJsonAdapter";
    static final String CONTENT_TYPE = "application/json";

    private String adapterClassName;
    private ExtensibleJsonAdapter adapter;

    public static class Builder<B extends Builder<B>> extends AbstractJacksonLayout.Builder<B>
            implements org.apache.logging.log4j.core.util.Builder<ExtensibleJsonLayout> {

        @PluginBuilderAttribute
        private boolean propertiesAsList;

        @PluginBuilderAttribute
        private String adapter = DEFAULT_ADAPTER_CLASS_NAME;

        public Builder() {
            super();
            setCharset(StandardCharsets.UTF_8);
        }

        @Override
        public ExtensibleJsonLayout build() {
            final boolean encodeThreadContextAsList = isProperties() && propertiesAsList;
            final String headerPattern = toStringOrNull(getHeader());
            final String footerPattern = toStringOrNull(getFooter());
            try {
                return new ExtensibleJsonLayout(getConfiguration(),
                                                isLocationInfo(),
                                                isProperties(),
                                                encodeThreadContextAsList,
                                                isComplete(),
                                                isCompact(),
                                                getEventEol(),
                                                headerPattern,
                                                footerPattern,
                                                getCharset(),
                                                isIncludeStacktrace(),
                                                isStacktraceAsString(),
                                                isIncludeNullDelimiter(),
                                                getAdapterClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public boolean isPropertiesAsList() {
            return propertiesAsList;
        }

        public B setPropertiesAsList(final boolean propertiesAsList) {
            this.propertiesAsList = propertiesAsList;
            return asBuilder();
        }

        public String getAdapterClassName() {
            return adapter;
        }

        public B setAdapterClassName(String adapterClassName) {
            this.adapter = (adapterClassName == null || adapterClassName.trim().equals(""))
                                        ?  DEFAULT_ADAPTER_CLASS_NAME
                                        : adapterClassName;
            return asBuilder();
        }
    }


    private ExtensibleJsonLayout(final Configuration config, final boolean locationInfo, final boolean properties,
                                 final boolean encodeThreadContextAsList, final boolean complete,
                                 final boolean compact, final boolean eventEol, final String headerPattern,
                                 final String footerPattern, final Charset charset, final boolean includeStacktrace,
                                 final boolean stacktraceAsString, final boolean includeNullDelimiter,
                                 final String adapterClassName) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        super(config,
                new JacksonFactory
                        .JSON(encodeThreadContextAsList, includeStacktrace, stacktraceAsString)
                        .newWriter(locationInfo, properties, compact),
                charset, compact, complete, eventEol,
                PatternLayout.newSerializerBuilder().setConfiguration(config).setPattern(headerPattern).setDefaultPattern(DEFAULT_HEADER).build(),
                PatternLayout.newSerializerBuilder().setConfiguration(config).setPattern(footerPattern).setDefaultPattern(DEFAULT_FOOTER).build(),
                includeNullDelimiter);
        configureAdapter(adapterClassName);
    }



    protected ExtensibleJsonLayout(Configuration config, ObjectWriter objectWriter, Charset charset, boolean compact,
                                   boolean complete, boolean eventEol, Serializer headerSerializer,
                                   Serializer footerSerializer, boolean includeNullDelimiter, String adapterClassName) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        super(config, objectWriter, charset, compact, complete, eventEol, headerSerializer, footerSerializer, includeNullDelimiter);
        configureAdapter(adapterClassName);
    }

    /**
     * Returns appropriate JSON header.
     *
     * @return a byte array containing the header, opening the JSON array.
     */
    @Override
    public byte[] getHeader() {
        if (!this.complete) {
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        final String str = serializeToString(getHeaderSerializer());
        if (str != null) {
            buf.append(str);
        }
        buf.append(this.eol);
        return getBytes(buf.toString());
    }

    /**
     * Returns appropriate JSON footer.
     *
     * @return a byte array containing the footer, closing the JSON array.
     */
    @Override
    public byte[] getFooter() {
        if (!this.complete) {
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        buf.append(this.eol);
        final String str = serializeToString(getFooterSerializer());
        if (str != null) {
            buf.append(str);
        }
        buf.append(this.eol);
        return getBytes(buf.toString());
    }

    @Override
    public Map<String, String> getContentFormat() {
        final Map<String, String> result = new HashMap<>();
        result.put("version", "2.0");
        return result;
    }

    /**
     * @return The content type.
     */
    @Override
    public String getContentType() {
        return CONTENT_TYPE + "; charset=" + this.getCharset();
    }

    public String getAdapterClassName() {
        return adapterClassName;
    }

    protected void initializeAdapter() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        adapter =  (ExtensibleJsonAdapter) Class.forName(getAdapterClassName()).getConstructor().newInstance();
    }

    private void configureAdapter(String adapterClassName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.adapterClassName = (adapterClassName == null || adapterClassName.trim().equals(""))
                                    ?  DEFAULT_ADAPTER_CLASS_NAME
                                    : adapterClassName;
        initializeAdapter();

    }

    @PluginBuilderFactory
    public static <B extends Builder<B>> B newBuilder() {
        return new Builder<B>().asBuilder();
    }

    /**
     * Creates an Extensible JSON Layout using the default settings. Useful for testing.
     *
     * @return Extensible JSON Layout.
     *
     * @throws ClassNotFoundException When invalid class name is specified
     * @throws NoSuchMethodException When no constructor is present in the adapter class
     * @throws InvocationTargetException When constructor of the adapter can not be called
     * @throws InstantiationException When adapter could not be instantiated
     * @throws IllegalAccessException When adapter could not be created
     */
    public static ExtensibleJsonLayout createDefaultLayout() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return new ExtensibleJsonLayout(new DefaultConfiguration(), false, false,
                                        false, false, false,
                                        false, DEFAULT_HEADER, DEFAULT_FOOTER, StandardCharsets.UTF_8,
                                        true, false, false, null);
    }

    @Override
    public void toSerializable(final LogEvent event, final Writer writer) throws IOException {
        if (complete && eventCount > 0) {
            writer.append(", ");
        }

        serialize(event, writer);
    }

    private static ExtensibleLog4jLogEvent convertMutableToExtensibleLog4jLogEvent(final LogEvent event, Map<String, Object> appContext) {
        // TODO Jackson-based layouts have certain filters set up for Log4jLogEvent.
        // TODO Need to set up the same filters for MutableLogEvent but don't know how...
        // This is a workaround.
        Log4jLogEvent logEvent = event instanceof MutableLogEvent
                                    ? ((MutableLogEvent) event).createMemento()
                                    : (Log4jLogEvent) event;

        return new ExtensibleLog4jLogEvent(logEvent, appContext);
    }

    public void serialize(final LogEvent event, final Writer writer) throws JsonGenerationException, JsonMappingException, IOException {
        objectWriter.writeValue(writer, convertMutableToExtensibleLog4jLogEvent(event, adapter.getMixinAttributes()));
        writer.write(eol);
        if (includeNullDelimiter) {
            writer.write('\0');
        }
        markEvent();
    }
}
