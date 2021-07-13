package io.opentracing.contrib.specialagent.jaeger;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.opentracing.ScopeManager;
import io.opentracing.Span;

/**
 * @author liuxueyun
 * @date 2021-05-17 21:05
 **/
public class TTLScopeManager implements ScopeManager {
    final TransmittableThreadLocal<TTLScope> ttlScope = new TransmittableThreadLocal();

    public TTLScopeManager() {
    }


    @Override
    public TTLScope activate(Span span) {
        return new TTLScope(this, span);
    }

    @Override
    public Span activeSpan() {
        TTLScope scope = (TTLScope)this.ttlScope.get();
        return scope == null ? null : scope.span();
    }
}