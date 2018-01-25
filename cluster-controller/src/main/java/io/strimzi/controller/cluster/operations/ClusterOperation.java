package io.strimzi.controller.cluster.operations;

public abstract class ClusterOperation implements Operation {
    protected final String namespace;
    protected final String name;

    protected static final int LOCK_TIMEOUT = 60_000;

    protected ClusterOperation(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    protected abstract String getLockName();
}
