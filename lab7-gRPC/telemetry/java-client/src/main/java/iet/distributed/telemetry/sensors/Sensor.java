package iet.distributed.telemetry.sensors;

import iet.distributed.telemetry.*;

import java.util.Random;

import static com.google.protobuf.util.Timestamps.fromMillis;
import static java.lang.System.currentTimeMillis;

public abstract class Sensor {

    protected final SensorMetadata metadata;
    protected final Random random;

    public Sensor(String owner, String type, DataType dataType) {
        this.metadata = SensorMetadata.newBuilder()
                .setOwner(owner)
                .setType(type)
                .setDataType(dataType)
                .build();

        this.random = new Random();
    }

    abstract Measurement getMeasurement();

    public MeasurementData getMeasurementData() {
        final var measurement = getMeasurement();
        return new MeasurementData(metadata, measurement);
    }

    protected CommonData commonData() {
        return CommonData.newBuilder()
                .setTimestamp(fromMillis(currentTimeMillis()))
                .build();
    }

}
