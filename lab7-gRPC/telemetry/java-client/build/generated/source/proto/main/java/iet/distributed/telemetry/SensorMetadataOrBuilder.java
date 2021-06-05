// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: telemetry.proto

package iet.distributed.telemetry;

public interface SensorMetadataOrBuilder extends
    // @@protoc_insertion_point(interface_extends:iet.distributed.telemetry.SensorMetadata)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string owner = 1;</code>
   * @return The owner.
   */
  java.lang.String getOwner();
  /**
   * <code>string owner = 1;</code>
   * @return The bytes for owner.
   */
  com.google.protobuf.ByteString
      getOwnerBytes();

  /**
   * <code>string type = 2;</code>
   * @return The type.
   */
  java.lang.String getType();
  /**
   * <code>string type = 2;</code>
   * @return The bytes for type.
   */
  com.google.protobuf.ByteString
      getTypeBytes();

  /**
   * <code>.iet.distributed.telemetry.DataType data_type = 3;</code>
   * @return The enum numeric value on the wire for dataType.
   */
  int getDataTypeValue();
  /**
   * <code>.iet.distributed.telemetry.DataType data_type = 3;</code>
   * @return The dataType.
   */
  iet.distributed.telemetry.DataType getDataType();
}
