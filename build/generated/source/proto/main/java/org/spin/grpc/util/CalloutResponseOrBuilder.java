// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: data.proto

package org.spin.grpc.util;

public interface CalloutResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:data.CalloutResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string result = 1;</code>
   */
  java.lang.String getResult();
  /**
   * <code>string result = 1;</code>
   */
  com.google.protobuf.ByteString
      getResultBytes();

  /**
   * <code>.data.ValueObject values = 2;</code>
   */
  boolean hasValues();
  /**
   * <code>.data.ValueObject values = 2;</code>
   */
  org.spin.grpc.util.ValueObject getValues();
  /**
   * <code>.data.ValueObject values = 2;</code>
   */
  org.spin.grpc.util.ValueObjectOrBuilder getValuesOrBuilder();
}