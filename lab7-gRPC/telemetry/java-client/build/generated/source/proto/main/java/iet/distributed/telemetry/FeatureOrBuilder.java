// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: telemetry.proto

package iet.distributed.telemetry;

public interface FeatureOrBuilder extends
    // @@protoc_insertion_point(interface_extends:iet.distributed.telemetry.Feature)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * The name of the feature.
   * </pre>
   *
   * <code>string name = 1;</code>
   * @return The name.
   */
  java.lang.String getName();
  /**
   * <pre>
   * The name of the feature.
   * </pre>
   *
   * <code>string name = 1;</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <pre>
   * The point where the feature is detected.
   * </pre>
   *
   * <code>.iet.distributed.telemetry.Point location = 2;</code>
   * @return Whether the location field is set.
   */
  boolean hasLocation();
  /**
   * <pre>
   * The point where the feature is detected.
   * </pre>
   *
   * <code>.iet.distributed.telemetry.Point location = 2;</code>
   * @return The location.
   */
  iet.distributed.telemetry.Point getLocation();
  /**
   * <pre>
   * The point where the feature is detected.
   * </pre>
   *
   * <code>.iet.distributed.telemetry.Point location = 2;</code>
   */
  iet.distributed.telemetry.PointOrBuilder getLocationOrBuilder();
}