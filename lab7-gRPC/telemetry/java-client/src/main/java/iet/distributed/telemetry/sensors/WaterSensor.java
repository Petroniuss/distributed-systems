package iet.distributed.telemetry.sensors;

import iet.distributed.telemetry.DataType;
import iet.distributed.telemetry.Measurement;
import iet.distributed.telemetry.WaterConsumptionData;
import iet.distributed.telemetry.sensors.Sensor;

public class WaterSensor extends Sensor {

    public WaterSensor(String owner, String type) {
        super(owner, type, DataType.WATER);
    }

    @Override
    Measurement getMeasurement() {
        final var value = 5 + 3 * random.nextDouble();
        final var data = WaterConsumptionData.newBuilder()
                .setWater(value)
                .build();
        return Measurement.newBuilder()
                .setCommonData(this.commonData())
                .setWaterConsumptionData(data)
                .build();
    }

}
