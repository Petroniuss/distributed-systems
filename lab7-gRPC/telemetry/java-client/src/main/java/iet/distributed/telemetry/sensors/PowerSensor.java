package iet.distributed.telemetry.sensors;

import iet.distributed.telemetry.DataType;
import iet.distributed.telemetry.Measurement;
import iet.distributed.telemetry.PowerConsumptionData;

public class PowerSensor extends Sensor {

    public PowerSensor(String owner, String type) {
        super(owner, type, DataType.POWER);
    }

    @Override
    Measurement getMeasurement() {
        final var value = 1 + random.nextDouble();
        final var data = PowerConsumptionData.newBuilder()
                .setPower(value)
                .build();
        return Measurement.newBuilder()
                .setCommonData(this.commonData())
                .setPowerConsumptionData(data)
                .build();
    }

}
