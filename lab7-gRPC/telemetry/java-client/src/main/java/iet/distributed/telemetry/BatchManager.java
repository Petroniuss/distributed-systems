package iet.distributed.telemetry;

import com.google.protobuf.Timestamp;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.google.protobuf.util.Timestamps.fromMillis;
import static java.lang.System.currentTimeMillis;

public class BatchManager {

    private static final Logger logger = Logger.getLogger(BatchManager.class.getName());

    private final ConcurrentLinkedQueue<MeasurementData> batchQueue;
    private final ResourceMonitorClient client;
    private final Timer timer;

    private Timestamp batchStart;

    public BatchManager(ResourceMonitorClient client, Duration period) {
        this.batchQueue = new ConcurrentLinkedQueue<>();
        this.client = client;

        final var periodLong = period.getSeconds() * 1000;
        this.batchStart = fromMillis(currentTimeMillis());

        this.timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendBatch();
            }
        }, periodLong, periodLong);
    }

    // enqueue data
    public void enqueue(MeasurementData data) {
        final var now = fromMillis(currentTimeMillis());
        final var nowFormatted = formatTimestamp(now);
        final var logMsg = MessageFormat.format("Measurement; owner: {0}, type: {1}, at: {2}",
                data.metadata.getType(), data.metadata.getOwner(), nowFormatted);

        logger.info(logMsg);
        this.batchQueue.add(data);
    }

    public boolean isConnectionAlive() {
        return this.client.isConnectionActive();
    }

    // periodically aggregate and send data in a single batch
    private void sendBatch() {
        if (!isConnectionAlive()) {
            this.timer.cancel();
            return;
        }

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

        final var msg = MessageFormat.format("Sending a batch - {0}" +
                "\n--------------------------------------------------------------------", formatTimestamp(now));
        logger.info(msg);
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

    public static String formatTimestamp(Timestamp timestamp) {
        final var instant = Instant.ofEpochSecond(
                timestamp.getSeconds(), timestamp.getNanos());

        final var zoned = instant.atZone(ZoneId.systemDefault());
        return zoned.format(DateTimeFormatter.ISO_DATE_TIME);
    }

}
