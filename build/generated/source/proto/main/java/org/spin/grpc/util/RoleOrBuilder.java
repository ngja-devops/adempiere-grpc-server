// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: access.proto

package org.spin.grpc.util;

public interface RoleOrBuilder extends
    // @@protoc_insertion_point(interface_extends:access.Role)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 id = 1;</code>
   */
  int getId();

  /**
   * <code>string uuid = 2;</code>
   */
  java.lang.String getUuid();
  /**
   * <code>string uuid = 2;</code>
   */
  com.google.protobuf.ByteString
      getUuidBytes();

  /**
   * <code>string name = 3;</code>
   */
  java.lang.String getName();
  /**
   * <code>string name = 3;</code>
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <code>string description = 4;</code>
   */
  java.lang.String getDescription();
  /**
   * <code>string description = 4;</code>
   */
  com.google.protobuf.ByteString
      getDescriptionBytes();

  /**
   * <code>int32 clientId = 5;</code>
   */
  int getClientId();

  /**
   * <pre>
   * Entity Access
   * </pre>
   *
   * <code>repeated .access.Access organizations = 6;</code>
   */
  java.util.List<org.spin.grpc.util.Access> 
      getOrganizationsList();
  /**
   * <pre>
   * Entity Access
   * </pre>
   *
   * <code>repeated .access.Access organizations = 6;</code>
   */
  org.spin.grpc.util.Access getOrganizations(int index);
  /**
   * <pre>
   * Entity Access
   * </pre>
   *
   * <code>repeated .access.Access organizations = 6;</code>
   */
  int getOrganizationsCount();
  /**
   * <pre>
   * Entity Access
   * </pre>
   *
   * <code>repeated .access.Access organizations = 6;</code>
   */
  java.util.List<? extends org.spin.grpc.util.AccessOrBuilder> 
      getOrganizationsOrBuilderList();
  /**
   * <pre>
   * Entity Access
   * </pre>
   *
   * <code>repeated .access.Access organizations = 6;</code>
   */
  org.spin.grpc.util.AccessOrBuilder getOrganizationsOrBuilder(
      int index);

  /**
   * <code>repeated .access.Access windows = 7;</code>
   */
  java.util.List<org.spin.grpc.util.Access> 
      getWindowsList();
  /**
   * <code>repeated .access.Access windows = 7;</code>
   */
  org.spin.grpc.util.Access getWindows(int index);
  /**
   * <code>repeated .access.Access windows = 7;</code>
   */
  int getWindowsCount();
  /**
   * <code>repeated .access.Access windows = 7;</code>
   */
  java.util.List<? extends org.spin.grpc.util.AccessOrBuilder> 
      getWindowsOrBuilderList();
  /**
   * <code>repeated .access.Access windows = 7;</code>
   */
  org.spin.grpc.util.AccessOrBuilder getWindowsOrBuilder(
      int index);

  /**
   * <code>repeated .access.Access process = 8;</code>
   */
  java.util.List<org.spin.grpc.util.Access> 
      getProcessList();
  /**
   * <code>repeated .access.Access process = 8;</code>
   */
  org.spin.grpc.util.Access getProcess(int index);
  /**
   * <code>repeated .access.Access process = 8;</code>
   */
  int getProcessCount();
  /**
   * <code>repeated .access.Access process = 8;</code>
   */
  java.util.List<? extends org.spin.grpc.util.AccessOrBuilder> 
      getProcessOrBuilderList();
  /**
   * <code>repeated .access.Access process = 8;</code>
   */
  org.spin.grpc.util.AccessOrBuilder getProcessOrBuilder(
      int index);

  /**
   * <code>repeated .access.Access forms = 9;</code>
   */
  java.util.List<org.spin.grpc.util.Access> 
      getFormsList();
  /**
   * <code>repeated .access.Access forms = 9;</code>
   */
  org.spin.grpc.util.Access getForms(int index);
  /**
   * <code>repeated .access.Access forms = 9;</code>
   */
  int getFormsCount();
  /**
   * <code>repeated .access.Access forms = 9;</code>
   */
  java.util.List<? extends org.spin.grpc.util.AccessOrBuilder> 
      getFormsOrBuilderList();
  /**
   * <code>repeated .access.Access forms = 9;</code>
   */
  org.spin.grpc.util.AccessOrBuilder getFormsOrBuilder(
      int index);

  /**
   * <code>repeated .access.Access browsers = 10;</code>
   */
  java.util.List<org.spin.grpc.util.Access> 
      getBrowsersList();
  /**
   * <code>repeated .access.Access browsers = 10;</code>
   */
  org.spin.grpc.util.Access getBrowsers(int index);
  /**
   * <code>repeated .access.Access browsers = 10;</code>
   */
  int getBrowsersCount();
  /**
   * <code>repeated .access.Access browsers = 10;</code>
   */
  java.util.List<? extends org.spin.grpc.util.AccessOrBuilder> 
      getBrowsersOrBuilderList();
  /**
   * <code>repeated .access.Access browsers = 10;</code>
   */
  org.spin.grpc.util.AccessOrBuilder getBrowsersOrBuilder(
      int index);

  /**
   * <code>repeated .access.Access workflows = 11;</code>
   */
  java.util.List<org.spin.grpc.util.Access> 
      getWorkflowsList();
  /**
   * <code>repeated .access.Access workflows = 11;</code>
   */
  org.spin.grpc.util.Access getWorkflows(int index);
  /**
   * <code>repeated .access.Access workflows = 11;</code>
   */
  int getWorkflowsCount();
  /**
   * <code>repeated .access.Access workflows = 11;</code>
   */
  java.util.List<? extends org.spin.grpc.util.AccessOrBuilder> 
      getWorkflowsOrBuilderList();
  /**
   * <code>repeated .access.Access workflows = 11;</code>
   */
  org.spin.grpc.util.AccessOrBuilder getWorkflowsOrBuilder(
      int index);

  /**
   * <code>repeated .access.Access tasks = 12;</code>
   */
  java.util.List<org.spin.grpc.util.Access> 
      getTasksList();
  /**
   * <code>repeated .access.Access tasks = 12;</code>
   */
  org.spin.grpc.util.Access getTasks(int index);
  /**
   * <code>repeated .access.Access tasks = 12;</code>
   */
  int getTasksCount();
  /**
   * <code>repeated .access.Access tasks = 12;</code>
   */
  java.util.List<? extends org.spin.grpc.util.AccessOrBuilder> 
      getTasksOrBuilderList();
  /**
   * <code>repeated .access.Access tasks = 12;</code>
   */
  org.spin.grpc.util.AccessOrBuilder getTasksOrBuilder(
      int index);

  /**
   * <code>repeated .access.Access dashboards = 13;</code>
   */
  java.util.List<org.spin.grpc.util.Access> 
      getDashboardsList();
  /**
   * <code>repeated .access.Access dashboards = 13;</code>
   */
  org.spin.grpc.util.Access getDashboards(int index);
  /**
   * <code>repeated .access.Access dashboards = 13;</code>
   */
  int getDashboardsCount();
  /**
   * <code>repeated .access.Access dashboards = 13;</code>
   */
  java.util.List<? extends org.spin.grpc.util.AccessOrBuilder> 
      getDashboardsOrBuilderList();
  /**
   * <code>repeated .access.Access dashboards = 13;</code>
   */
  org.spin.grpc.util.AccessOrBuilder getDashboardsOrBuilder(
      int index);

  /**
   * <code>repeated .access.Access documentActions = 14;</code>
   */
  java.util.List<org.spin.grpc.util.Access> 
      getDocumentActionsList();
  /**
   * <code>repeated .access.Access documentActions = 14;</code>
   */
  org.spin.grpc.util.Access getDocumentActions(int index);
  /**
   * <code>repeated .access.Access documentActions = 14;</code>
   */
  int getDocumentActionsCount();
  /**
   * <code>repeated .access.Access documentActions = 14;</code>
   */
  java.util.List<? extends org.spin.grpc.util.AccessOrBuilder> 
      getDocumentActionsOrBuilderList();
  /**
   * <code>repeated .access.Access documentActions = 14;</code>
   */
  org.spin.grpc.util.AccessOrBuilder getDocumentActionsOrBuilder(
      int index);

  /**
   * <code>repeated .access.TableAccess tables = 15;</code>
   */
  java.util.List<org.spin.grpc.util.TableAccess> 
      getTablesList();
  /**
   * <code>repeated .access.TableAccess tables = 15;</code>
   */
  org.spin.grpc.util.TableAccess getTables(int index);
  /**
   * <code>repeated .access.TableAccess tables = 15;</code>
   */
  int getTablesCount();
  /**
   * <code>repeated .access.TableAccess tables = 15;</code>
   */
  java.util.List<? extends org.spin.grpc.util.TableAccessOrBuilder> 
      getTablesOrBuilderList();
  /**
   * <code>repeated .access.TableAccess tables = 15;</code>
   */
  org.spin.grpc.util.TableAccessOrBuilder getTablesOrBuilder(
      int index);

  /**
   * <code>repeated .access.ColumnAccess columns = 16;</code>
   */
  java.util.List<org.spin.grpc.util.ColumnAccess> 
      getColumnsList();
  /**
   * <code>repeated .access.ColumnAccess columns = 16;</code>
   */
  org.spin.grpc.util.ColumnAccess getColumns(int index);
  /**
   * <code>repeated .access.ColumnAccess columns = 16;</code>
   */
  int getColumnsCount();
  /**
   * <code>repeated .access.ColumnAccess columns = 16;</code>
   */
  java.util.List<? extends org.spin.grpc.util.ColumnAccessOrBuilder> 
      getColumnsOrBuilderList();
  /**
   * <code>repeated .access.ColumnAccess columns = 16;</code>
   */
  org.spin.grpc.util.ColumnAccessOrBuilder getColumnsOrBuilder(
      int index);

  /**
   * <code>repeated .access.RecordAccess records = 17;</code>
   */
  java.util.List<org.spin.grpc.util.RecordAccess> 
      getRecordsList();
  /**
   * <code>repeated .access.RecordAccess records = 17;</code>
   */
  org.spin.grpc.util.RecordAccess getRecords(int index);
  /**
   * <code>repeated .access.RecordAccess records = 17;</code>
   */
  int getRecordsCount();
  /**
   * <code>repeated .access.RecordAccess records = 17;</code>
   */
  java.util.List<? extends org.spin.grpc.util.RecordAccessOrBuilder> 
      getRecordsOrBuilderList();
  /**
   * <code>repeated .access.RecordAccess records = 17;</code>
   */
  org.spin.grpc.util.RecordAccessOrBuilder getRecordsOrBuilder(
      int index);
}