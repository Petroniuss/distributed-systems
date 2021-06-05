package iet.distributed.telemetry;

import com.google.protobuf.Timestamp;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static com.google.protobuf.util.Timestamps.fromMillis;
import static java.lang.System.currentTimeMillis;

public class BatchManager {

    private final ConcurrentLinkedQueue<MeasurementData> batchQueue;
    private final ResourceMonitorClient client;

    private Timestamp batchStart;

    public BatchManager(ResourceMonitorClient client, Duration period) {
        this.batchQueue = new ConcurrentLinkedQueue<>();
        this.client = client;

        final var periodLong = period.getSeconds() * 1000;
        this.batchStart = fromMillis(currentTimeMillis());

        final var timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendBatch();
            }
        }, periodLong, periodLong);
    }

    // enqueue data
    public void enqueue(MeasurementData data) {
        this.batchQueue.add(data);
    }

    // periodically aggregate and send data in a single batch
    private void sendBatch() {
        final var batch = getBatch();

        Map<SensorMetadata, List<Measurement>> map = new HashMap<>();
        batch.forEach(data -> {
            map.putIfAbsent(data.metadata, new ArrayList<>());
            map.get(data.metadata).add(data.measurement);
        });

        final var sensorData = map.entrySet().stream().map(entry -> {
            final var meta = entry.getKey();
            final var measurements = entry.getValue();
            return SensorData.newBuilder()
                    .setMetadata(meta)
                    .addAllMeasurements(measurements)
                    .build();
        }).collect(Collectors.toList());

        final var now = fromMillis(currentTimeMillis());
        final var batchedData = BatchedData.newBuilder()
                .setBatchStart(this.batchStart)
                .setBatchEnd(now)
                .addAllSensorData(sensorData)
                .build();

        this.batchStart = now;
        this.client.sendBatch(batchedData);
    }

    private List<MeasurementData> getBatch() {
        final var batch = new ArrayList<MeasurementData>();
        while (true) {
            final var measurement = batchQueue.poll();

            if (measurement != null) {
                batch.add(measurement);
            } else {
                break;
            }
        }

        return batch;
    }

}
