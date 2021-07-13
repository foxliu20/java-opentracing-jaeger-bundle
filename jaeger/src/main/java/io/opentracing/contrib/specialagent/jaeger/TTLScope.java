package io.opentracing.contrib.specialagent.jaeger;

import io.opentracing.Scope;
import io.opentracing.Span;

/**
 * @author liuxueyun
 * @date 2021-05-17 21:08
 **/
public class TTLScope implements Scope {

    private final TTLScopeManager scopeManager;
    private final Span wrapped;
    private final TTLScope toRestore;

    TTLScope(TTLScopeManager scopeManager, Span wrapped) {
        this.scopeManager = scopeManager;
        this.wrapped = wrapped;
        this.toRestore = (TTLScope) scopeManager.ttlScope.get();
        scopeManager.ttlScope.set(this);
    }

    @Override
    public void close() {
        if (this.scopeManager.ttlScope.get() == this) {
            this.scopeManager.ttlScope.set(this.toRestore);
        }
    }

    Span span() {
        return this.wrapped;
    }
}