package edu.rutmiit.demo.grpc.sla;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.66.0)",
    comments = "Source: sla_calculator.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class SlaCalculatorGrpc {

  private SlaCalculatorGrpc() {}

  public static final java.lang.String SERVICE_NAME = "sla.SlaCalculator";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<edu.rutmiit.demo.grpc.sla.CalculateSlaRequest,
      edu.rutmiit.demo.grpc.sla.CalculateSlaResponse> getCalculateSlaMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CalculateSla",
      requestType = edu.rutmiit.demo.grpc.sla.CalculateSlaRequest.class,
      responseType = edu.rutmiit.demo.grpc.sla.CalculateSlaResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<edu.rutmiit.demo.grpc.sla.CalculateSlaRequest,
      edu.rutmiit.demo.grpc.sla.CalculateSlaResponse> getCalculateSlaMethod() {
    io.grpc.MethodDescriptor<edu.rutmiit.demo.grpc.sla.CalculateSlaRequest, edu.rutmiit.demo.grpc.sla.CalculateSlaResponse> getCalculateSlaMethod;
    if ((getCalculateSlaMethod = SlaCalculatorGrpc.getCalculateSlaMethod) == null) {
      synchronized (SlaCalculatorGrpc.class) {
        if ((getCalculateSlaMethod = SlaCalculatorGrpc.getCalculateSlaMethod) == null) {
          SlaCalculatorGrpc.getCalculateSlaMethod = getCalculateSlaMethod =
              io.grpc.MethodDescriptor.<edu.rutmiit.demo.grpc.sla.CalculateSlaRequest, edu.rutmiit.demo.grpc.sla.CalculateSlaResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CalculateSla"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.rutmiit.demo.grpc.sla.CalculateSlaRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.rutmiit.demo.grpc.sla.CalculateSlaResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SlaCalculatorMethodDescriptorSupplier("CalculateSla"))
              .build();
        }
      }
    }
    return getCalculateSlaMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SlaCalculatorStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SlaCalculatorStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SlaCalculatorStub>() {
        @java.lang.Override
        public SlaCalculatorStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SlaCalculatorStub(channel, callOptions);
        }
      };
    return SlaCalculatorStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SlaCalculatorBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SlaCalculatorBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SlaCalculatorBlockingStub>() {
        @java.lang.Override
        public SlaCalculatorBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SlaCalculatorBlockingStub(channel, callOptions);
        }
      };
    return SlaCalculatorBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SlaCalculatorFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SlaCalculatorFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SlaCalculatorFutureStub>() {
        @java.lang.Override
        public SlaCalculatorFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SlaCalculatorFutureStub(channel, callOptions);
        }
      };
    return SlaCalculatorFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void calculateSla(edu.rutmiit.demo.grpc.sla.CalculateSlaRequest request,
        io.grpc.stub.StreamObserver<edu.rutmiit.demo.grpc.sla.CalculateSlaResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCalculateSlaMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service SlaCalculator.
   */
  public static abstract class SlaCalculatorImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return SlaCalculatorGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service SlaCalculator.
   */
  public static final class SlaCalculatorStub
      extends io.grpc.stub.AbstractAsyncStub<SlaCalculatorStub> {
    private SlaCalculatorStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SlaCalculatorStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SlaCalculatorStub(channel, callOptions);
    }

    /**
     */
    public void calculateSla(edu.rutmiit.demo.grpc.sla.CalculateSlaRequest request,
        io.grpc.stub.StreamObserver<edu.rutmiit.demo.grpc.sla.CalculateSlaResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCalculateSlaMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service SlaCalculator.
   */
  public static final class SlaCalculatorBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<SlaCalculatorBlockingStub> {
    private SlaCalculatorBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SlaCalculatorBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SlaCalculatorBlockingStub(channel, callOptions);
    }

    /**
     */
    public edu.rutmiit.demo.grpc.sla.CalculateSlaResponse calculateSla(edu.rutmiit.demo.grpc.sla.CalculateSlaRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCalculateSlaMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service SlaCalculator.
   */
  public static final class SlaCalculatorFutureStub
      extends io.grpc.stub.AbstractFutureStub<SlaCalculatorFutureStub> {
    private SlaCalculatorFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SlaCalculatorFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SlaCalculatorFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<edu.rutmiit.demo.grpc.sla.CalculateSlaResponse> calculateSla(
        edu.rutmiit.demo.grpc.sla.CalculateSlaRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCalculateSlaMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CALCULATE_SLA = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CALCULATE_SLA:
          serviceImpl.calculateSla((edu.rutmiit.demo.grpc.sla.CalculateSlaRequest) request,
              (io.grpc.stub.StreamObserver<edu.rutmiit.demo.grpc.sla.CalculateSlaResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getCalculateSlaMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              edu.rutmiit.demo.grpc.sla.CalculateSlaRequest,
              edu.rutmiit.demo.grpc.sla.CalculateSlaResponse>(
                service, METHODID_CALCULATE_SLA)))
        .build();
  }

  private static abstract class SlaCalculatorBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SlaCalculatorBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return edu.rutmiit.demo.grpc.sla.SlaCalculatorOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SlaCalculator");
    }
  }

  private static final class SlaCalculatorFileDescriptorSupplier
      extends SlaCalculatorBaseDescriptorSupplier {
    SlaCalculatorFileDescriptorSupplier() {}
  }

  private static final class SlaCalculatorMethodDescriptorSupplier
      extends SlaCalculatorBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    SlaCalculatorMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (SlaCalculatorGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SlaCalculatorFileDescriptorSupplier())
              .addMethod(getCalculateSlaMethod())
              .build();
        }
      }
    }
    return result;
  }
}
