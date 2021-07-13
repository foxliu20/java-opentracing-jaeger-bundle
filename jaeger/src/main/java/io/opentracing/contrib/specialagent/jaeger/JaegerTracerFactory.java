package io.opentracing.contrib.specialagent.jaeger;

import javax.annotation.Priority;

import io.opentracing.Tracer;
import io.opentracing.contrib.tracerresolver.TracerFactory;

@Priority(1) // Higher priority than the original factory (which we wrap).
public class JaegerTracerFactory implements TracerFactory
{

  public JaegerTracerFactory() {
    TracerParameters.loadParameters();
  }

  @Override
  public Tracer getTracer() {
    return Configuration.fromEnv().getTracer();
  }
}
