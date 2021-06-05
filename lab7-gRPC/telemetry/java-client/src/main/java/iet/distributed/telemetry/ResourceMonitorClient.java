package iet.distributed.telemetry;

import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceMonitorClient {
    private static final Logger logger = Logger.getLogger(ResourceMonitorClient.class.getName());

    private final StreamObserver<BatchedData> requestObserver;

    public ResourceMonitorClient(Channel channel) {
        final var asyncStub = ResourceMonitorGrpc.newStub(channel);
        final var responseObserver = new ResponseObserver();
        this.requestObserver = asyncStub.streamData(responseObserver);
    }

    public void sendBatch(BatchedData batch) {
        this.requestObserver.onNext(batch);
    }

    static class ResponseObserver implements StreamObserver<Acknowledment> {
        @Override
        public void onNext(Acknowledment value) {
            logger.info("Received ack from server: " + value.getMessage());
        }

        @Override
        public void onError(Throwable t) {
            logger.log(Level.WARNING, "Streaming completed", t);
        }

        @Override
        public void onCompleted() {
            logger.info("Streaming completed");
        }
    }


}
