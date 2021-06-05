package iet.distributed.telemetry;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.29.0)",
    comments = "Source: telemetry.proto")
public final class ResourceMonitorGrpc {

  private ResourceMonitorGrpc() {}

  public static final String SERVICE_NAME = "iet.distributed.telemetry.ResourceMonitor";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<iet.distributed.telemetry.BatchedData,
      iet.distributed.telemetry.Acknowledment> getStreamDataMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StreamData",
      requestType = iet.distributed.telemetry.BatchedData.class,
      responseType = iet.distributed.telemetry.Acknowledment.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<iet.distributed.telemetry.BatchedData,
      iet.distributed.telemetry.Acknowledment> getStreamDataMethod() {
    io.grpc.MethodDescriptor<iet.distributed.telemetry.BatchedData, iet.distributed.telemetry.Acknowledment> getStreamDataMethod;
    if ((getStreamDataMethod = ResourceMonitorGrpc.getStreamDataMethod) == null) {
      synchronized (ResourceMonitorGrpc.class) {
        if ((getStreamDataMethod = ResourceMonitorGrpc.getStreamDataMethod) == null) {
          ResourceMonitorGrpc.getStreamDataMethod = getStreamDataMethod =
              io.grpc.MethodDescriptor.<iet.distributed.telemetry.BatchedData, iet.distributed.telemetry.Acknowledment>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "StreamData"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  iet.distributed.telemetry.BatchedData.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  iet.distributed.telemetry.Acknowledment.getDefaultInstance()))
              .setSchemaDescriptor(new ResourceMonitorMethodDescriptorSupplier("StreamData"))
              .build();
        }
      }
    }
    return getStreamDataMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ResourceMonitorStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ResourceMonitorStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ResourceMonitorStub>() {
        @java.lang.Override
        public ResourceMonitorStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ResourceMonitorStub(channel, callOptions);
        }
      };
    return ResourceMonitorStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ResourceMonitorBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ResourceMonitorBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ResourceMonitorBlockingStub>() {
        @java.lang.Override
        public ResourceMonitorBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ResourceMonitorBlockingStub(channel, callOptions);
        }
      };
    return ResourceMonitorBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ResourceMonitorFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ResourceMonitorFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ResourceMonitorFutureStub>() {
        @java.lang.Override
        public ResourceMonitorFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ResourceMonitorFutureStub(channel, callOptions);
        }
      };
    return ResourceMonitorFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class ResourceMonitorImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * client communicates with server using bidirectional async communication.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<iet.distributed.telemetry.BatchedData> streamData(
        io.grpc.stub.StreamObserver<iet.distributed.telemetry.Acknowledment> responseObserver) {
      return asyncUnimplementedStreamingCall(getStreamDataMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getStreamDataMethod(),
            asyncBidiStreamingCall(
              new MethodHandlers<
                iet.distributed.telemetry.BatchedData,
                iet.distributed.telemetry.Acknowledment>(
                  this, METHODID_STREAM_DATA)))
          .build();
    }
  }

  /**
   */
  public static final class ResourceMonitorStub extends io.grpc.stub.AbstractAsyncStub<ResourceMonitorStub> {
    private ResourceMonitorStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ResourceMonitorStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ResourceMonitorStub(channel, callOptions);
    }

    /**
     * <pre>
     * client communicates with server using bidirectional async communication.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<iet.distributed.telemetry.BatchedData> streamData(
        io.grpc.stub.StreamObserver<iet.distributed.telemetry.Acknowledment> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getStreamDataMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   */
  public static final class ResourceMonitorBlockingStub extends io.grpc.stub.AbstractBlockingStub<ResourceMonitorBlockingStub> {
    private ResourceMonitorBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ResourceMonitorBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ResourceMonitorBlockingStub(channel, callOptions);
    }
  }

  /**
   */
  public static final class ResourceMonitorFutureStub extends io.grpc.stub.AbstractFutureStub<ResourceMonitorFutureStub> {
    private ResourceMonitorFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ResourceMonitorFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ResourceMonitorFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_STREAM_DATA = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ResourceMonitorImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ResourceMonitorImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_STREAM_DATA:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.streamData(
              (io.grpc.stub.StreamObserver<iet.distributed.telemetry.Acknowledment>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class ResourceMonitorBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ResourceMonitorBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return iet.distributed.telemetry.Telemetry.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ResourceMonitor");
    }
  }

  private static final class ResourceMonitorFileDescriptorSupplier
      extends ResourceMonitorBaseDescriptorSupplier {
    ResourceMonitorFileDescriptorSupplier() {}
  }

  private static final class ResourceMonitorMethodDescriptorSupplier
      extends ResourceMonitorBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ResourceMonitorMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ResourceMonitorGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ResourceMonitorFileDescriptorSupplier())
              .addMethod(getStreamDataMethod())
              .build();
        }
      }
    }
    return result;
  }
}
