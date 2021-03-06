// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: telemetry.proto

package iet.distributed.telemetry;

public interface RectangleOrBuilder extends
    // @@protoc_insertion_point(interface_extends:iet.distributed.telemetry.Rectangle)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * One corner of the rectangle.
   * </pre>
   *
   * <code>.iet.distributed.telemetry.Point lo = 1;</code>
   * @return Whether the lo field is set.
   */
  boolean hasLo();
  /**
   * <pre>
   * One corner of the rectangle.
   * </pre>
   *
   * <code>.iet.distributed.telemetry.Point lo = 1;</code>
   * @return The lo.
   */
  iet.distributed.telemetry.Point getLo();
  /**
   * <pre>
   * One corner of the rectangle.
   * </pre>
   *
   * <code>.iet.distributed.telemetry.Point lo = 1;</code>
   */
  iet.distributed.telemetry.PointOrBuilder getLoOrBuilder();

  /**
   * <pre>
   * The other corner of the rectangle.
   * </pre>
   *
   * <code>.iet.distributed.telemetry.Point hi = 2;</code>
   * @return Whether the hi field is set.
   */
  boolean hasHi();
  /**
   * <pre>
   * The other corner of the rectangle.
   * </pre>
   *
   * <code>.iet.distributed.telemetry.Point hi = 2;</code>
   * @return The hi.
   */
  iet.distributed.telemetry.Point getHi();
  /**
   * <pre>
   * The other corner of the rectangle.
   * </pre>
   *
   * <code>.iet.distributed.telemetry.Point hi = 2;</code>
   */
  iet.distributed.telemetry.PointOrBuilder getHiOrBuilder();
}
