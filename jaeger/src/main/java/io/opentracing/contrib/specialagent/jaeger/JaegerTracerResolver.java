package io.opentracing.contrib.specialagent.jaeger;

import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.contrib.tracerresolver.TracerResolver;

import javax.annotation.Priority;

@Priority(1) // Higher priority than the original resolver (which we wrap).
public class JaegerTracerResolver extends TracerResolver
{
  @Override
  protected JaegerTracer resolve() {
    return new JaegerTracerFactory().getTracer();
  }
}
