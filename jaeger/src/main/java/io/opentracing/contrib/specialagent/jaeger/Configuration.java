package io.opentracing.contrib.specialagent.jaeger;

import io.jaegertracing.internal.JaegerTracer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author liuxueyun
 * @date 2021-07-13 15:51
 **/
public class Configuration extends io.jaegertracing.Configuration {
    private final static Logger logger = Logger.getLogger(Configuration.class.getName());

    /**
     * 是否使用TTLScope.
     */
    public static final String JAEGER_TTL_SCOPE = JAEGER_PREFIX + "TTL_SCOPE";

    /**
     * lazy singleton JaegerTracer initialized in getTracer() method.
     */
    private JaegerTracer tracer;

    public Configuration(String serviceName) {
        super(serviceName);
    }

    /**
     * @return Configuration object from environmental variables
     */
    public static Configuration fromEnv() {
        return Configuration.fromEnv(getProperty(JAEGER_SERVICE_NAME));
    }

    public static Configuration fromEnv(String serviceName) {
        Configuration configuration = new Configuration(serviceName);
        configuration
            .withTracerTags(tracerTagsFromEnv())
            .withTraceId128Bit(getPropertyAsBool(JAEGER_TRACEID_128BIT))
            .withReporter(ReporterConfiguration.fromEnv())
            .withSampler(SamplerConfiguration.fromEnv())
            .withCodec(CodecConfiguration.fromEnv());
        return configuration;
    }

    private static Map<String, String> tracerTagsFromEnv() {
        Map<String, String> tracerTagMaps = null;
        String tracerTags = getProperty(JAEGER_TAGS);
        if (tracerTags != null) {
            String[] tags = tracerTags.split("\\s*,\\s*");
            for (String tag : tags) {
                String[] tagValue = tag.split("\\s*=\\s*");
                if (tagValue.length == 2) {
                    if (tracerTagMaps == null) {
                        tracerTagMaps = new HashMap<String, String>();
                    }
                    tracerTagMaps.put(tagValue[0], resolveValue(tagValue[1]));
                } else {
                    logger.log(Level.WARNING, "Tracer tag incorrectly formatted: " + tag);
                }
            }
        }
        return tracerTagMaps;
    }

    private static String resolveValue(String value) {
        if (value.startsWith("${") && value.endsWith("}")) {
            String[] ref = value.substring(2, value.length() - 1).split("\\s*:\\s*");
            if (ref.length > 0) {
                String propertyValue = getProperty(ref[0]);
                if (propertyValue == null && ref.length > 1) {
                    propertyValue = ref[1];
                }
                return propertyValue;
            }
        }
        return value;
    }

    @Override
    public synchronized JaegerTracer getTracer() {
        if (tracer != null) {
            return tracer;
        }

        JaegerTracer.Builder builder = getTracerBuilder();
        if (getPropertyAsBool(JAEGER_TTL_SCOPE)) {
            logger.log(Level.FINE, "with ttl scope manager");
            builder.withScopeManager(new TTLScopeManager());
        }
        tracer = builder.build();
        logger.log(Level.INFO, "Initialized tracer=" + tracer);

        return tracer;
    }

    @Override
    public synchronized void closeTracer() {
        if (tracer != null) {
            tracer.close();
        }
    }

    private static boolean getPropertyAsBool(String name) {
        return Boolean.valueOf(getProperty(name));
    }

    private static String getProperty(String name) {
        return System.getProperty(name, System.getenv(name));
    }
}
