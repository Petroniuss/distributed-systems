package iet.distributed.telemetry.sensors;

import iet.distributed.telemetry.DataType;
import iet.distributed.telemetry.Measurement;
import iet.distributed.telemetry.TemperatureData;

public class TemperatureSensor extends Sensor {

    public TemperatureSensor(String owner, String type) {
        super(owner, type, DataType.TEMPERATURE);
    }

    @Override
    Measurement getMeasurement() {
        final var value = 20 + (5 * random.nextDouble());
        final var data = TemperatureData.newBuilder()
                .setTemperature(value)
                .build();
        return Measurement.newBuilder()
                .setCommonData(this.commonData())
                .setTemperatureData(data)
                .build();
    }

}
