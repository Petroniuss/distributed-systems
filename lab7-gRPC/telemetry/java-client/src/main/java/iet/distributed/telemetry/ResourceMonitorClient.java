package iet.distributed.telemetry;

import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceMonitorClient {
    private static final Logger logger = Logger.getLogger(ResourceMonitorClient.class.getName());

    private final StreamObserver<BatchedData> requestObserver;

    volatile boolean done;

    public ResourceMonitorClient(Channel channel) {
        final var asyncStub = ResourceMonitorGrpc.newStub(channel);
        final var responseObserver = new ResponseObserver();
        this.requestObserver = asyncStub.streamData(responseObserver);
        this.done = false;
    }

    public void sendBatch(BatchedData batch) {
        this.requestObserver.onNext(batch);
    }

    public boolean isConnectionActive() {
        return !done;
    }

    class ResponseObserver implements StreamObserver<Acknowledment> {
        @Override
        public void onNext(Acknowledment value) {
            logger.info("Received ack from server: " + value.getMessage());
        }

        @Override
        public void onError(Throwable t) {
            done = true;
            logger.log(Level.WARNING, "Streaming completed");
        }

        @Override
        public void onCompleted() {
            done = true;
            logger.info("Streaming completed");
        }
    }


}
