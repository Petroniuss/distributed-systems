package iet.distributed.telemetry;

public class MeasurementData {
    final SensorMetadata metadata;
    final Measurement measurement;

    public MeasurementData(SensorMetadata metadata, Measurement measurement) {
        this.metadata = metadata;
        this.measurement = measurement;
    }

}

