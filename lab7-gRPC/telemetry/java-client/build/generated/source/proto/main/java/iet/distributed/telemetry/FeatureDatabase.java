// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: telemetry.proto

package iet.distributed.telemetry;

/**
 * <pre>
 * Not used in the RPC.  Instead, this is here for the form serialized to disk.
 * </pre>
 *
 * Protobuf type {@code iet.distributed.telemetry.FeatureDatabase}
 */
public final class FeatureDatabase extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:iet.distributed.telemetry.FeatureDatabase)
    FeatureDatabaseOrBuilder {
private static final long serialVersionUID = 0L;
  // Use FeatureDatabase.newBuilder() to construct.
  private FeatureDatabase(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private FeatureDatabase() {
    feature_ = java.util.Collections.emptyList();
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new FeatureDatabase();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private FeatureDatabase(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            if (!((mutable_bitField0_ & 0x00000001) != 0)) {
              feature_ = new java.util.ArrayList<iet.distributed.telemetry.Feature>();
              mutable_bitField0_ |= 0x00000001;
            }
            feature_.add(
                input.readMessage(iet.distributed.telemetry.Feature.parser(), extensionRegistry));
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      if (((mutable_bitField0_ & 0x00000001) != 0)) {
        feature_ = java.util.Collections.unmodifiableList(feature_);
      }
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return iet.distributed.telemetry.Telemetry.internal_static_iet_distributed_telemetry_FeatureDatabase_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return iet.distributed.telemetry.Telemetry.internal_static_iet_distributed_telemetry_FeatureDatabase_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            iet.distributed.telemetry.FeatureDatabase.class, iet.distributed.telemetry.FeatureDatabase.Builder.class);
  }

  public static final int FEATURE_FIELD_NUMBER = 1;
  private java.util.List<iet.distributed.telemetry.Feature> feature_;
  /**
   * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
   */
  @java.lang.Override
  public java.util.List<iet.distributed.telemetry.Feature> getFeatureList() {
    return feature_;
  }
  /**
   * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
   */
  @java.lang.Override
  public java.util.List<? extends iet.distributed.telemetry.FeatureOrBuilder> 
      getFeatureOrBuilderList() {
    return feature_;
  }
  /**
   * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
   */
  @java.lang.Override
  public int getFeatureCount() {
    return feature_.size();
  }
  /**
   * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
   */
  @java.lang.Override
  public iet.distributed.telemetry.Feature getFeature(int index) {
    return feature_.get(index);
  }
  /**
   * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
   */
  @java.lang.Override
  public iet.distributed.telemetry.FeatureOrBuilder getFeatureOrBuilder(
      int index) {
    return feature_.get(index);
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    for (int i = 0; i < feature_.size(); i++) {
      output.writeMessage(1, feature_.get(i));
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    for (int i = 0; i < feature_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, feature_.get(i));
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof iet.distributed.telemetry.FeatureDatabase)) {
      return super.equals(obj);
    }
    iet.distributed.telemetry.FeatureDatabase other = (iet.distributed.telemetry.FeatureDatabase) obj;

    if (!getFeatureList()
        .equals(other.getFeatureList())) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (getFeatureCount() > 0) {
      hash = (37 * hash) + FEATURE_FIELD_NUMBER;
      hash = (53 * hash) + getFeatureList().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static iet.distributed.telemetry.FeatureDatabase parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static iet.distributed.telemetry.FeatureDatabase parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static iet.distributed.telemetry.FeatureDatabase parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static iet.distributed.telemetry.FeatureDatabase parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static iet.distributed.telemetry.FeatureDatabase parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static iet.distributed.telemetry.FeatureDatabase parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static iet.distributed.telemetry.FeatureDatabase parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static iet.distributed.telemetry.FeatureDatabase parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static iet.distributed.telemetry.FeatureDatabase parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static iet.distributed.telemetry.FeatureDatabase parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static iet.distributed.telemetry.FeatureDatabase parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static iet.distributed.telemetry.FeatureDatabase parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(iet.distributed.telemetry.FeatureDatabase prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * <pre>
   * Not used in the RPC.  Instead, this is here for the form serialized to disk.
   * </pre>
   *
   * Protobuf type {@code iet.distributed.telemetry.FeatureDatabase}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:iet.distributed.telemetry.FeatureDatabase)
      iet.distributed.telemetry.FeatureDatabaseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return iet.distributed.telemetry.Telemetry.internal_static_iet_distributed_telemetry_FeatureDatabase_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return iet.distributed.telemetry.Telemetry.internal_static_iet_distributed_telemetry_FeatureDatabase_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              iet.distributed.telemetry.FeatureDatabase.class, iet.distributed.telemetry.FeatureDatabase.Builder.class);
    }

    // Construct using iet.distributed.telemetry.FeatureDatabase.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
        getFeatureFieldBuilder();
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      if (featureBuilder_ == null) {
        feature_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
      } else {
        featureBuilder_.clear();
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return iet.distributed.telemetry.Telemetry.internal_static_iet_distributed_telemetry_FeatureDatabase_descriptor;
    }

    @java.lang.Override
    public iet.distributed.telemetry.FeatureDatabase getDefaultInstanceForType() {
      return iet.distributed.telemetry.FeatureDatabase.getDefaultInstance();
    }

    @java.lang.Override
    public iet.distributed.telemetry.FeatureDatabase build() {
      iet.distributed.telemetry.FeatureDatabase result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public iet.distributed.telemetry.FeatureDatabase buildPartial() {
      iet.distributed.telemetry.FeatureDatabase result = new iet.distributed.telemetry.FeatureDatabase(this);
      int from_bitField0_ = bitField0_;
      if (featureBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0)) {
          feature_ = java.util.Collections.unmodifiableList(feature_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.feature_ = feature_;
      } else {
        result.feature_ = featureBuilder_.build();
      }
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof iet.distributed.telemetry.FeatureDatabase) {
        return mergeFrom((iet.distributed.telemetry.FeatureDatabase)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(iet.distributed.telemetry.FeatureDatabase other) {
      if (other == iet.distributed.telemetry.FeatureDatabase.getDefaultInstance()) return this;
      if (featureBuilder_ == null) {
        if (!other.feature_.isEmpty()) {
          if (feature_.isEmpty()) {
            feature_ = other.feature_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureFeatureIsMutable();
            feature_.addAll(other.feature_);
          }
          onChanged();
        }
      } else {
        if (!other.feature_.isEmpty()) {
          if (featureBuilder_.isEmpty()) {
            featureBuilder_.dispose();
            featureBuilder_ = null;
            feature_ = other.feature_;
            bitField0_ = (bitField0_ & ~0x00000001);
            featureBuilder_ = 
              com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                 getFeatureFieldBuilder() : null;
          } else {
            featureBuilder_.addAllMessages(other.feature_);
          }
        }
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      iet.distributed.telemetry.FeatureDatabase parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (iet.distributed.telemetry.FeatureDatabase) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private java.util.List<iet.distributed.telemetry.Feature> feature_ =
      java.util.Collections.emptyList();
    private void ensureFeatureIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        feature_ = new java.util.ArrayList<iet.distributed.telemetry.Feature>(feature_);
        bitField0_ |= 0x00000001;
       }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
        iet.distributed.telemetry.Feature, iet.distributed.telemetry.Feature.Builder, iet.distributed.telemetry.FeatureOrBuilder> featureBuilder_;

    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public java.util.List<iet.distributed.telemetry.Feature> getFeatureList() {
      if (featureBuilder_ == null) {
        return java.util.Collections.unmodifiableList(feature_);
      } else {
        return featureBuilder_.getMessageList();
      }
    }
    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public int getFeatureCount() {
      if (featureBuilder_ == null) {
        return feature_.size();
      } else {
        return featureBuilder_.getCount();
      }
    }
    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public iet.distributed.telemetry.Feature getFeature(int index) {
      if (featureBuilder_ == null) {
        return feature_.get(index);
      } else {
        return featureBuilder_.getMessage(index);
      }
    }
    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public Builder setFeature(
        int index, iet.distributed.telemetry.Feature value) {
      if (featureBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureFeatureIsMutable();
        feature_.set(index, value);
        onChanged();
      } else {
        featureBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public Builder setFeature(
        int index, iet.distributed.telemetry.Feature.Builder builderForValue) {
      if (featureBuilder_ == null) {
        ensureFeatureIsMutable();
        feature_.set(index, builderForValue.build());
        onChanged();
      } else {
        featureBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public Builder addFeature(iet.distributed.telemetry.Feature value) {
      if (featureBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureFeatureIsMutable();
        feature_.add(value);
        onChanged();
      } else {
        featureBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public Builder addFeature(
        int index, iet.distributed.telemetry.Feature value) {
      if (featureBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureFeatureIsMutable();
        feature_.add(index, value);
        onChanged();
      } else {
        featureBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public Builder addFeature(
        iet.distributed.telemetry.Feature.Builder builderForValue) {
      if (featureBuilder_ == null) {
        ensureFeatureIsMutable();
        feature_.add(builderForValue.build());
        onChanged();
      } else {
        featureBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public Builder addFeature(
        int index, iet.distributed.telemetry.Feature.Builder builderForValue) {
      if (featureBuilder_ == null) {
        ensureFeatureIsMutable();
        feature_.add(index, builderForValue.build());
        onChanged();
      } else {
        featureBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public Builder addAllFeature(
        java.lang.Iterable<? extends iet.distributed.telemetry.Feature> values) {
      if (featureBuilder_ == null) {
        ensureFeatureIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, feature_);
        onChanged();
      } else {
        featureBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public Builder clearFeature() {
      if (featureBuilder_ == null) {
        feature_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
      } else {
        featureBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public Builder removeFeature(int index) {
      if (featureBuilder_ == null) {
        ensureFeatureIsMutable();
        feature_.remove(index);
        onChanged();
      } else {
        featureBuilder_.remove(index);
      }
      return this;
    }
    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public iet.distributed.telemetry.Feature.Builder getFeatureBuilder(
        int index) {
      return getFeatureFieldBuilder().getBuilder(index);
    }
    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public iet.distributed.telemetry.FeatureOrBuilder getFeatureOrBuilder(
        int index) {
      if (featureBuilder_ == null) {
        return feature_.get(index);  } else {
        return featureBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public java.util.List<? extends iet.distributed.telemetry.FeatureOrBuilder> 
         getFeatureOrBuilderList() {
      if (featureBuilder_ != null) {
        return featureBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(feature_);
      }
    }
    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public iet.distributed.telemetry.Feature.Builder addFeatureBuilder() {
      return getFeatureFieldBuilder().addBuilder(
          iet.distributed.telemetry.Feature.getDefaultInstance());
    }
    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public iet.distributed.telemetry.Feature.Builder addFeatureBuilder(
        int index) {
      return getFeatureFieldBuilder().addBuilder(
          index, iet.distributed.telemetry.Feature.getDefaultInstance());
    }
    /**
     * <code>repeated .iet.distributed.telemetry.Feature feature = 1;</code>
     */
    public java.util.List<iet.distributed.telemetry.Feature.Builder> 
         getFeatureBuilderList() {
      return getFeatureFieldBuilder().getBuilderList();
    }
    private com.google.protobuf.RepeatedFieldBuilderV3<
        iet.distributed.telemetry.Feature, iet.distributed.telemetry.Feature.Builder, iet.distributed.telemetry.FeatureOrBuilder> 
        getFeatureFieldBuilder() {
      if (featureBuilder_ == null) {
        featureBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
            iet.distributed.telemetry.Feature, iet.distributed.telemetry.Feature.Builder, iet.distributed.telemetry.FeatureOrBuilder>(
                feature_,
                ((bitField0_ & 0x00000001) != 0),
                getParentForChildren(),
                isClean());
        feature_ = null;
      }
      return featureBuilder_;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:iet.distributed.telemetry.FeatureDatabase)
  }

  // @@protoc_insertion_point(class_scope:iet.distributed.telemetry.FeatureDatabase)
  private static final iet.distributed.telemetry.FeatureDatabase DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new iet.distributed.telemetry.FeatureDatabase();
  }

  public static iet.distributed.telemetry.FeatureDatabase getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<FeatureDatabase>
      PARSER = new com.google.protobuf.AbstractParser<FeatureDatabase>() {
    @java.lang.Override
    public FeatureDatabase parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new FeatureDatabase(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<FeatureDatabase> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<FeatureDatabase> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public iet.distributed.telemetry.FeatureDatabase getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

