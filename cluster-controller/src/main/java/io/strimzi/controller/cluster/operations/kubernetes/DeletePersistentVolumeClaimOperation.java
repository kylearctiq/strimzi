package io.strimzi.controller.cluster.operations.kubernetes;

import io.strimzi.controller.cluster.K8SUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeletePersistentVolumeClaimOperation extends K8sOperation {

    private static final Logger log = LoggerFactory.getLogger(DeletePersistentVolumeClaimOperation.class.getName());
    private final String namespace;
    private final String name;

    public DeletePersistentVolumeClaimOperation(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    @Override
    public void execute(Vertx vertx, K8SUtils k8s, Handler<AsyncResult<Void>> handler) {
        vertx.createSharedWorkerExecutor("kubernetes-ops-pool").executeBlocking(
                future -> {
                    if (k8s.persistentVolumeClaimExists(namespace, name)) {
                        try {
                            log.info("Deleting persistentvolumeclaim {} in namespace {}", name, namespace);
                            k8s.deletePersistentVolumeClaim(namespace, name);
                            future.complete();
                        } catch (Exception e) {
                            log.error("Caught exception while deleting persistentvolumeclaim", e);
                            future.fail(e);
                        }
                    } else {
                        log.warn("persistentvolumeclaim {} in namespace {} doesn't exists", name, namespace);
                        future.complete();
                    }
                }, false,
                res -> {
                    if (res.succeeded()) {
                        log.info("Persistentvolumeclaim {} in namespace {} has been deleted", name, namespace);
                        handler.handle(Future.succeededFuture());
                    }
                    else {
                        log.error("Persistentvolumeclaim deletion failed: {}", res.result());
                        handler.handle(Future.failedFuture((Exception)res.result()));
                    }
                }
        );
    }
}
