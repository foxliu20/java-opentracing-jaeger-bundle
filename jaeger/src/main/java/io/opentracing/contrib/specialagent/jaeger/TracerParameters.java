package io.opentracing.contrib.specialagent.jaeger;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.opentracing.contrib.specialagent.common.Configuration;

import static io.opentracing.contrib.specialagent.jaeger.Configuration.JAEGER_TTL_SCOPE;

public final class TracerParameters {
  private TracerParameters() {}

  final static String JAEGER_PREFIX = "JAEGER_";

  private final static Logger logger = Logger.getLogger(TracerParameters.class.getName());

  public static void loadParameters() {
    defaultParameters();
    Properties props = Configuration.loadConfigurationFile();
    loadParametersIntoSystemProperties(props);
  }

  private static void defaultParameters() {
    System.setProperty(JAEGER_TTL_SCOPE, "true");
  }

  static void loadParametersIntoSystemProperties(Properties props) {
    for (String propName: props.stringPropertyNames()) {
      // Only load the parameter if it is not *already* defined as a System property.
      if (!propName.startsWith(JAEGER_PREFIX) || System.getProperty(propName) != null)
        continue;

      String propValue = props.getProperty(propName);
      System.setProperty(propName, propValue);
      logger.log(Level.INFO, "Set System property " + propName + "=" + propValue + " from Tracer configuration file");
    }
  }
}
