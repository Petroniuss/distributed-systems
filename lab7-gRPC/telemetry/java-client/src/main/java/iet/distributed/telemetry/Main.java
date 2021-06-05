package iet.distributed.telemetry;

import iet.distributed.telemetry.sensors.PowerSensor;
import iet.distributed.telemetry.sensors.Sensor;
import iet.distributed.telemetry.sensors.TemperatureSensor;
import iet.distributed.telemetry.sensors.WaterSensor;
import io.grpc.ManagedChannelBuilder;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;

public class Main {

    private static final long BASE_SLEEP_TIME = 5000;

    public static void main(String[] args) {
        // create channel
        final var target = "localhost:10000";
        final var channel = ManagedChannelBuilder
                .forTarget(target)
                .usePlaintext().build();

        // create client and batch manager
        final var client = new ResourceMonitorClient(channel);
        final var manager = new BatchManager(client, Duration.of(15, ChronoUnit.SECONDS));

        // spawn sensor threads
        final var threadPool = Executors.newCachedThreadPool();
        createSensors().forEach(sensor -> {
            threadPool.submit(() -> sensorJob(sensor, manager));
        });
    }

    private static void sensorJob(Sensor sensor, BatchManager batchManager) {
        while (true) {
            try {
                final var measurement = sensor.getMeasurementData();
                batchManager.enqueue(measurement);

                // sleep for 2-5 seconds.
                Thread.sleep((long) (BASE_SLEEP_TIME - (3 * Math.random() * 1000)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private static List<Sensor> createSensors() {
        final var powerSensor = new PowerSensor("George Washington", "Power-Sv1");
        final var temperatureSensor = new TemperatureSensor("Abraham Lincoln", "Temp-Sensor-v1");
        final var waterSensor = new WaterSensor("Thomas Jefferson", "Water-Sensor-v1");

        return List.of(powerSensor, temperatureSensor, waterSensor);
    }
}
