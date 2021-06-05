// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: telemetry.proto

package iet.distributed.telemetry;

public final class Telemetry {
  private Telemetry() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_iet_distributed_telemetry_Acknowledment_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_iet_distributed_telemetry_Acknowledment_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_iet_distributed_telemetry_BatchedData_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_iet_distributed_telemetry_BatchedData_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_iet_distributed_telemetry_SensorData_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_iet_distributed_telemetry_SensorData_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_iet_distributed_telemetry_SensorMetadata_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_iet_distributed_telemetry_SensorMetadata_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_iet_distributed_telemetry_Measurement_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_iet_distributed_telemetry_Measurement_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_iet_distributed_telemetry_CommonData_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_iet_distributed_telemetry_CommonData_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_iet_distributed_telemetry_TemperatureData_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_iet_distributed_telemetry_TemperatureData_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_iet_distributed_telemetry_WaterConsumptionData_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_iet_distributed_telemetry_WaterConsumptionData_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_iet_distributed_telemetry_PowerConsumptionData_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_iet_distributed_telemetry_PowerConsumptionData_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_iet_distributed_telemetry_Point_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_iet_distributed_telemetry_Point_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_iet_distributed_telemetry_Rectangle_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_iet_distributed_telemetry_Rectangle_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_iet_distributed_telemetry_Feature_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_iet_distributed_telemetry_Feature_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_iet_distributed_telemetry_FeatureDatabase_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_iet_distributed_telemetry_FeatureDatabase_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_iet_distributed_telemetry_RouteNote_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_iet_distributed_telemetry_RouteNote_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_iet_distributed_telemetry_RouteSummary_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_iet_distributed_telemetry_RouteSummary_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\017telemetry.proto\022\031iet.distributed.telem" +
      "etry\032\036google/protobuf/duration.proto\032\037go" +
      "ogle/protobuf/timestamp.proto\" \n\rAcknowl" +
      "edment\022\017\n\007message\030\001 \001(\t\"\251\001\n\013BatchedData\022" +
      "/\n\013batch_start\030\001 \001(\0132\032.google.protobuf.T" +
      "imestamp\022-\n\tbatch_end\030\002 \001(\0132\032.google.pro" +
      "tobuf.Timestamp\022:\n\013sensor_data\030\003 \003(\0132%.i" +
      "et.distributed.telemetry.SensorData\"\277\001\n\n" +
      "SensorData\022;\n\010metadata\030\001 \001(\0132).iet.distr" +
      "ibuted.telemetry.SensorMetadata\0226\n\tdata_" +
      "type\030\002 \001(\0162#.iet.distributed.telemetry.D" +
      "ataType\022<\n\014measurements\030\003 \003(\0132&.iet.dist" +
      "ributed.telemetry.Measurement\"-\n\016SensorM" +
      "etadata\022\r\n\005owner\030\001 \001(\t\022\014\n\004type\030\002 \001(\t\"\306\002\n" +
      "\013Measurement\022:\n\013common_data\030\001 \001(\0132%.iet." +
      "distributed.telemetry.CommonData\022F\n\020temp" +
      "erature_data\030\002 \001(\0132*.iet.distributed.tel" +
      "emetry.TemperatureDataH\000\022Q\n\026water_consum" +
      "ption_data\030\003 \001(\0132/.iet.distributed.telem" +
      "etry.WaterConsumptionDataH\000\022Q\n\026power_con" +
      "sumption_data\030\004 \001(\0132/.iet.distributed.te" +
      "lemetry.PowerConsumptionDataH\000B\r\n\013one_of" +
      "_data\";\n\nCommonData\022-\n\ttimestamp\030\001 \001(\0132\032" +
      ".google.protobuf.Timestamp\"&\n\017Temperatur" +
      "eData\022\023\n\013temperature\030\001 \001(\001\"%\n\024WaterConsu" +
      "mptionData\022\r\n\005water\030\001 \001(\001\"%\n\024PowerConsum" +
      "ptionData\022\r\n\005power\030\001 \001(\001\",\n\005Point\022\020\n\010lat" +
      "itude\030\001 \001(\005\022\021\n\tlongitude\030\002 \001(\005\"g\n\tRectan" +
      "gle\022,\n\002lo\030\001 \001(\0132 .iet.distributed.teleme" +
      "try.Point\022,\n\002hi\030\002 \001(\0132 .iet.distributed." +
      "telemetry.Point\"K\n\007Feature\022\014\n\004name\030\001 \001(\t" +
      "\0222\n\010location\030\002 \001(\0132 .iet.distributed.tel" +
      "emetry.Point\"F\n\017FeatureDatabase\0223\n\007featu" +
      "re\030\001 \003(\0132\".iet.distributed.telemetry.Fea" +
      "ture\"P\n\tRouteNote\0222\n\010location\030\001 \001(\0132 .ie" +
      "t.distributed.telemetry.Point\022\017\n\007message" +
      "\030\002 \001(\t\"}\n\014RouteSummary\022\023\n\013point_count\030\001 " +
      "\001(\005\022\025\n\rfeature_count\030\002 \001(\005\022\020\n\010distance\030\003" +
      " \001(\005\022/\n\014elapsed_time\030\004 \001(\0132\031.google.prot" +
      "obuf.Duration*1\n\010DataType\022\017\n\013TEMPERATURE" +
      "\020\000\022\t\n\005WATER\020\001\022\t\n\005POWER\020\0022t\n\017ResourceMoni" +
      "tor\022a\n\tRouteChat\022&.iet.distributed.telem" +
      "etry.BatchedData\032(.iet.distributed.telem" +
      "etry.Acknowledment(\0010\0012\365\002\n\nRouteGuide\022R\n" +
      "\nGetFeature\022 .iet.distributed.telemetry." +
      "Point\032\".iet.distributed.telemetry.Featur" +
      "e\022Z\n\014ListFeatures\022$.iet.distributed.tele" +
      "metry.Rectangle\032\".iet.distributed.teleme" +
      "try.Feature0\001\022Z\n\013RecordRoute\022 .iet.distr" +
      "ibuted.telemetry.Point\032\'.iet.distributed" +
      ".telemetry.RouteSummary(\001\022[\n\tRouteChat\022$" +
      ".iet.distributed.telemetry.RouteNote\032$.i" +
      "et.distributed.telemetry.RouteNote(\0010\001B\014" +
      "P\001Z\010gen/;genb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.protobuf.DurationProto.getDescriptor(),
          com.google.protobuf.TimestampProto.getDescriptor(),
        });
    internal_static_iet_distributed_telemetry_Acknowledment_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_iet_distributed_telemetry_Acknowledment_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_iet_distributed_telemetry_Acknowledment_descriptor,
        new java.lang.String[] { "Message", });
    internal_static_iet_distributed_telemetry_BatchedData_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_iet_distributed_telemetry_BatchedData_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_iet_distributed_telemetry_BatchedData_descriptor,
        new java.lang.String[] { "BatchStart", "BatchEnd", "SensorData", });
    internal_static_iet_distributed_telemetry_SensorData_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_iet_distributed_telemetry_SensorData_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_iet_distributed_telemetry_SensorData_descriptor,
        new java.lang.String[] { "Metadata", "DataType", "Measurements", });
    internal_static_iet_distributed_telemetry_SensorMetadata_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_iet_distributed_telemetry_SensorMetadata_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_iet_distributed_telemetry_SensorMetadata_descriptor,
        new java.lang.String[] { "Owner", "Type", });
    internal_static_iet_distributed_telemetry_Measurement_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_iet_distributed_telemetry_Measurement_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_iet_distributed_telemetry_Measurement_descriptor,
        new java.lang.String[] { "CommonData", "TemperatureData", "WaterConsumptionData", "PowerConsumptionData", "OneOfData", });
    internal_static_iet_distributed_telemetry_CommonData_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_iet_distributed_telemetry_CommonData_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_iet_distributed_telemetry_CommonData_descriptor,
        new java.lang.String[] { "Timestamp", });
    internal_static_iet_distributed_telemetry_TemperatureData_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_iet_distributed_telemetry_TemperatureData_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_iet_distributed_telemetry_TemperatureData_descriptor,
        new java.lang.String[] { "Temperature", });
    internal_static_iet_distributed_telemetry_WaterConsumptionData_descriptor =
      getDescriptor().getMessageTypes().get(7);
    internal_static_iet_distributed_telemetry_WaterConsumptionData_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_iet_distributed_telemetry_WaterConsumptionData_descriptor,
        new java.lang.String[] { "Water", });
    internal_static_iet_distributed_telemetry_PowerConsumptionData_descriptor =
      getDescriptor().getMessageTypes().get(8);
    internal_static_iet_distributed_telemetry_PowerConsumptionData_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_iet_distributed_telemetry_PowerConsumptionData_descriptor,
        new java.lang.String[] { "Power", });
    internal_static_iet_distributed_telemetry_Point_descriptor =
      getDescriptor().getMessageTypes().get(9);
    internal_static_iet_distributed_telemetry_Point_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_iet_distributed_telemetry_Point_descriptor,
        new java.lang.String[] { "Latitude", "Longitude", });
    internal_static_iet_distributed_telemetry_Rectangle_descriptor =
      getDescriptor().getMessageTypes().get(10);
    internal_static_iet_distributed_telemetry_Rectangle_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_iet_distributed_telemetry_Rectangle_descriptor,
        new java.lang.String[] { "Lo", "Hi", });
    internal_static_iet_distributed_telemetry_Feature_descriptor =
      getDescriptor().getMessageTypes().get(11);
    internal_static_iet_distributed_telemetry_Feature_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_iet_distributed_telemetry_Feature_descriptor,
        new java.lang.String[] { "Name", "Location", });
    internal_static_iet_distributed_telemetry_FeatureDatabase_descriptor =
      getDescriptor().getMessageTypes().get(12);
    internal_static_iet_distributed_telemetry_FeatureDatabase_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_iet_distributed_telemetry_FeatureDatabase_descriptor,
        new java.lang.String[] { "Feature", });
    internal_static_iet_distributed_telemetry_RouteNote_descriptor =
      getDescriptor().getMessageTypes().get(13);
    internal_static_iet_distributed_telemetry_RouteNote_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_iet_distributed_telemetry_RouteNote_descriptor,
        new java.lang.String[] { "Location", "Message", });
    internal_static_iet_distributed_telemetry_RouteSummary_descriptor =
      getDescriptor().getMessageTypes().get(14);
    internal_static_iet_distributed_telemetry_RouteSummary_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_iet_distributed_telemetry_RouteSummary_descriptor,
        new java.lang.String[] { "PointCount", "FeatureCount", "Distance", "ElapsedTime", });
    com.google.protobuf.DurationProto.getDescriptor();
    com.google.protobuf.TimestampProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
