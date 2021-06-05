package main

import (
	"errors"
	"flag"
	"fmt"
	"io"
	"log"
	"net"
	"time"

	"google.golang.org/grpc"

	ts "github.com/golang/protobuf/ptypes/timestamp"
	pb "github.com/petroniuss/go-server/gen"
)

var (
	port       = flag.Int("port", 10000, "The server port")
)

type resourceMonitorServer struct {
	pb.UnimplementedResourceMonitorServer
}

func (s *resourceMonitorServer) StreamData(stream pb.ResourceMonitor_StreamDataServer) error {
	startTime := time.Now()
	var receivedBatches = 0
	for {
		batchedData, err := stream.Recv()

		// if client ends streaming.
		if err == io.EOF {
			endTime := time.Now()
			fmt.Printf("Client finished streaming data after %v batches. \n\t Start Time: %v, \n\t End Time: %v",
				receivedBatches, startTime, endTime)
			return err
		}
		if err != nil {
			fmt.Printf("Error occurred %v", err)
			return err
		}

		// do something with received data
		err = handleBatch(batchedData)
		var message string
		if err != nil {
			message = err.Error()
		} else {
			message = "Ack"
		}

		err = stream.Send(&pb.Acknowledment{
			Message: message,
		})
		if err != nil {
			return err
		}
	}
}

func handleBatch(data *pb.BatchedData) error {
	fmt.Printf("Recevied batch: \n")
	fmt.Printf("\t Start Time: %v\n", formatTimestamp(data.GetBatchStart()))
	fmt.Printf("\t End Time: %v\n", formatTimestamp(data.GetBatchEnd()))

	for sensorIdx, sensorData := range data.GetSensorData() {
		meta := sensorData.GetMetadata()
		dataType := meta.GetDataType()
		fmt.Printf("  %v. Sensor Metadata \n\t - Sensor Type: %v \n\t - Sensor Owner: %v\n",
			sensorIdx + 1, meta.GetType(), meta.GetOwner())

		var logFunc func(measurement *pb.Measurement)
		if dataType == pb.DataType_TEMPERATURE {
			logFunc = logTemperatureMeasurement
		} else if dataType == pb.DataType_WATER {
			logFunc = logWaterMeasurement
		} else if dataType == pb.DataType_POWER {
			logFunc = logPowerMeasurement
		} else {
			err := "unknown DataType. This server release does not support it yet"
			fmt.Printf(err)

			return errors.New(err)
		}

		fmt.Printf("      Measurements: \n")
		for _, measurement := range sensorData.GetMeasurements() {
			logFunc(measurement)
		}
	}

	fmt.Printf("----------------------------------------------------------------------------\n\n\n")

	return nil
}

func logTemperatureMeasurement(measurement *pb.Measurement) {
	timestamp := measurement.GetCommonData().GetTimestamp()
	temperature := measurement.GetTemperatureData().GetTemperature()

	fmt.Printf("\t Temperature: %v, Timestamp: %v\n", temperature, formatTimestamp(timestamp))
}

func logPowerMeasurement(measurement *pb.Measurement) {
	timestamp := measurement.GetCommonData().GetTimestamp()
	power := measurement.GetPowerConsumptionData().GetPower()

	fmt.Printf("\t Power: %v, Timestamp: %v\n", power, formatTimestamp(timestamp))
}

func logWaterMeasurement(measurement *pb.Measurement) {
	timestamp := measurement.GetCommonData().GetTimestamp()
	water := measurement.GetWaterConsumptionData().GetWater()

	fmt.Printf("\t Water: %v, Timestamp: %v\n", water, formatTimestamp(timestamp))
}

func newResourceMonitorServer() *resourceMonitorServer {
	s := &resourceMonitorServer{}
	return s
}

func formatTimestamp(timestamp *ts.Timestamp) string {
	return timestamp.AsTime().String()
}

func main() {
	flag.Parse()
	lis, err := net.Listen("tcp", fmt.Sprintf("localhost:%d", *port))
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}
	log.Printf("Hello there! Resource Server is listening on port: %d", *port)
	var opts []grpc.ServerOption
	grpcServer := grpc.NewServer(opts...)
	pb.RegisterResourceMonitorServer(grpcServer, newResourceMonitorServer())
	_ = grpcServer.Serve(lis)
}
