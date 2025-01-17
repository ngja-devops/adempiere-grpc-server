/************************************************************************************
 * Copyright (C) 2018-present E.R.P. Consultores y Asociados, C.A.                  *
 * Contributor(s): Edwin Betancourt, EdwinBetanc0urt@outlook.com                    *
 * This program is free software: you can redistribute it and/or modify             *
 * it under the terms of the GNU General Public License as published by             *
 * the Free Software Foundation, either version 2 of the License, or                *
 * (at your option) any later version.                                              *
 * This program is distributed in the hope that it will be useful,                  *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the                     *
 * GNU General Public License for more details.                                     *
 * You should have received a copy of the GNU General Public License                *
 * along with this program. If not, see <https://www.gnu.org/licenses/>.            *
 ************************************************************************************/
package org.spin.grpc.service.dictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.adempiere.core.domains.models.I_AD_FieldGroup;
import org.adempiere.core.domains.models.I_AD_Table;
import org.adempiere.core.domains.models.X_AD_FieldGroup;
import org.compiere.model.MColumn;
import org.compiere.model.MField;
import org.compiere.model.MFieldCustom;
import org.compiere.model.MLookupInfo;
import org.compiere.model.MMenu;
import org.compiere.model.MProcess;
import org.compiere.model.MRole;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MValRule;
import org.compiere.model.MWindow;
import org.compiere.model.M_Element;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.spin.backend.grpc.dictionary.ContextInfo;
import org.spin.backend.grpc.dictionary.DependentField;
import org.spin.backend.grpc.dictionary.Field;
import org.spin.backend.grpc.dictionary.FieldCondition;
import org.spin.backend.grpc.dictionary.FieldDefinition;
import org.spin.backend.grpc.dictionary.FieldGroup;
import org.spin.backend.grpc.dictionary.Process;
import org.spin.backend.grpc.dictionary.Reference;
import org.spin.backend.grpc.dictionary.Tab;
import org.spin.backend.grpc.dictionary.Window;
import org.spin.base.db.WhereClauseUtil;
import org.spin.base.util.ContextManager;
import org.spin.base.util.ReferenceUtil;
import org.spin.dictionary.custom.FieldCustomUtil;
import org.spin.dictionary.util.WindowUtil;
import org.spin.model.MADFieldCondition;
import org.spin.model.MADFieldDefinition;
import org.spin.service.grpc.util.value.ValueManager;
import org.spin.util.ASPUtil;

public class WindowConvertUtil {

	/**
	 * Convert Window from Window Model
	 * @param window
	 * @param withTabs
	 * @return
	 */
	public static Window.Builder convertWindow(Properties context, MWindow window, boolean withTabs) {
		if (window == null) {
			return Window.newBuilder();
		}
		window = ASPUtil.getInstance(context).getWindow(window.getAD_Window_ID());
		Window.Builder builder = null;
		ContextInfo.Builder contextInfoBuilder = DictionaryConvertUtil.convertContextInfo(
			context,
			window.getAD_ContextInfo_ID()
		);
		//	
		builder = Window.newBuilder()
			.setId(window.getAD_Window_ID())
			.setUuid(
				ValueManager.validateNull(window.getUUID())
			)
			.setName(window.getName())
			.setDescription(
				ValueManager.validateNull(window.getDescription())
			)
			.setHelp(
				ValueManager.validateNull(window.getHelp())
			)
			.setWindowType(
				ValueManager.validateNull(window.getWindowType())
			)
			.setIsSalesTransaction(window.isSOTrx())
			.setIsActive(window.isActive())
		;
		if(contextInfoBuilder != null) {
			builder.setContextInfo(contextInfoBuilder.build());
		}
		//	With Tabs
		if(withTabs) {
			Boolean isShowAcct = MRole.getDefault(context, false).isShowAcct();
//			List<Tab.Builder> tabListForGroup = new ArrayList<>();
			List<MTab> tabs = ASPUtil.getInstance(context).getWindowTabs(window.getAD_Window_ID());
			for(MTab tab : tabs) {
				if(!tab.isActive()) {
					continue;
				}
				// role without permission to accounting
				if (tab.isInfoTab() && !isShowAcct) {
					continue;
				}
				Tab.Builder tabBuilder = WindowConvertUtil.convertTab(
					context,
					tab,
					tabs,
					withTabs
				);
				builder.addTabs(tabBuilder.build());
				//	Get field group
				// int [] fieldGroupIdArray = getFieldGroupIdsFromTab(tab.getAD_Tab_ID());
				// if(fieldGroupIdArray != null) {
				// 	for(int fieldGroupId : fieldGroupIdArray) {
				// 		Tab.Builder tabFieldGroup = convertTab(context, tab, false);
				// 		FieldGroup.Builder fieldGroup = convertFieldGroup(context, fieldGroupId);
				// 		tabFieldGroup.setFieldGroup(fieldGroup);
				// 		tabFieldGroup.setName(fieldGroup.getName());
				// 		tabFieldGroup.setDescription("");
				// 		tabFieldGroup.setUuid(tabFieldGroup.getUuid() + "---");
				// 		//	Add to list
				// 		tabListForGroup.add(tabFieldGroup);
				// 	}
				// }
			}
			//	Add Field Group Tabs
			// for(Tab.Builder tabFieldGroup : tabListForGroup) {
			// 	builder.addTabs(tabFieldGroup.build());
			// }
		}
		//	Add to recent Item
		org.spin.dictionary.util.DictionaryUtil.addToRecentItem(
			MMenu.ACTION_Window,
			window.getAD_Window_ID()
		);
		//	return
		return builder;
	}


	/**
	 * Convert Model tab to builder tab
	 * @param tab
	 * @return
	 */
	public static Tab.Builder convertTab(Properties context, MTab tab, List<MTab> tabs, boolean withFields) {
		if (tab == null) {
			return Tab.newBuilder();
		}

		int tabId = tab.getAD_Tab_ID();
		tab = ASPUtil.getInstance(context).getWindowTab(tab.getAD_Window_ID(), tabId);

		int parentTabId = 0;
		// root tab has no parent
		if (tab.getTabLevel() > 0) {
			parentTabId = WindowUtil.getDirectParentTabId(tab.getAD_Window_ID(), tabId);
		}

		//	Get table attributes
		MTable table = MTable.get(context, tab.getAD_Table_ID());
		boolean isReadOnly = tab.isReadOnly() || table.isView();
		int contextInfoId = tab.getAD_ContextInfo_ID();
		if(contextInfoId <= 0) {
			contextInfoId = table.getAD_ContextInfo_ID();
		}

		// get where clause including link column and parent column
		String whereClause = WhereClauseUtil.getTabWhereClauseFromParentTabs(context, tab, tabs);

		//	create build
		Tab.Builder builder = Tab.newBuilder()
			.setId(tab.getAD_Tab_ID())
			.setUuid(
				ValueManager.validateNull(tab.getUUID())
			)
			.setName(
				ValueManager.validateNull(tab.getName())
			)
			.setDescription(
				ValueManager.validateNull(tab.getDescription())
			)
			.setHelp(ValueManager.validateNull(tab.getHelp()))
			.setAccessLevel(Integer.parseInt(table.getAccessLevel()))
			.setCommitWarning(
				ValueManager.validateNull(tab.getCommitWarning())
			)
			.setSequence(tab.getSeqNo())
			.setDisplayLogic(
				ValueManager.validateNull(tab.getDisplayLogic())
			)
			.setReadOnlyLogic(
				ValueManager.validateNull(tab.getReadOnlyLogic())
			)
			.setIsAdvancedTab(tab.isAdvancedTab())
			.setIsDeleteable(table.isDeleteable())
			.setIsDocument(table.isDocument())
			.setIsHasTree(tab.isHasTree())
			.setIsInfoTab(tab.isInfoTab())
			.setIsInsertRecord(!isReadOnly && tab.isInsertRecord())
			.setIsReadOnly(isReadOnly)
			.setIsSingleRow(tab.isSingleRow())
			.setIsSortTab(tab.isSortTab())
			.setIsTranslationTab(tab.isTranslationTab())
			.setIsView(table.isView())
			.setTabLevel(tab.getTabLevel())
			.setTableName(
				ValueManager.validateNull(table.getTableName())
			)
			.setParentTabId(parentTabId)
			.setIsChangeLog(table.isChangeLog())
			.setIsActive(tab.isActive())
			.addAllContextColumnNames(
				ContextManager.getContextColumnNames(
					Optional.ofNullable(whereClause).orElse("")
					+ Optional.ofNullable(tab.getOrderByClause()).orElse("")
				)
			)
			.addAllKeyColumns(
				Arrays.asList(
					table.getKeyColumns()
				)
			)
		;

		//	For link
		if(contextInfoId > 0) {
			ContextInfo.Builder contextInfoBuilder = DictionaryConvertUtil.convertContextInfo(
				context,
				contextInfoId
			);
			builder.setContextInfo(contextInfoBuilder.build());
		}
		//	Parent Link Column Name
		if(tab.getParent_Column_ID() > 0) {
			MColumn column = MColumn.get(context, tab.getParent_Column_ID());
			builder.setParentColumnName(column.getColumnName());
		}
		//	Link Column Name
		if(tab.getAD_Column_ID() > 0) {
			MColumn column = MColumn.get(context, tab.getAD_Column_ID());
			builder.setLinkColumnName(column.getColumnName());
		}
		if(tab.isSortTab()) {
			//	Sort Column
			if(tab.getAD_ColumnSortOrder_ID() > 0) {
				MColumn column = MColumn.get(context, tab.getAD_ColumnSortOrder_ID());
				builder.setSortOrderColumnName(column.getColumnName());
			}
			//	Sort Yes / No
			if(tab.getAD_ColumnSortYesNo_ID() > 0) {
				MColumn column = MColumn.get(context, tab.getAD_ColumnSortYesNo_ID());
				builder.setSortYesNoColumnName(column.getColumnName());
			}
		}
		//	Process
		List<MProcess> processList = WindowUtil.getProcessActionFromTab(context, tab);
		if(processList != null && processList.size() > 0) {
			for(MProcess process : processList) {
				// get process associated without parameters
				Process.Builder processBuilder = ProcessConvertUtil.convertProcess(
					context,
					process,
					false
				);
				builder.addProcesses(processBuilder.build());
			}
		}
		if(withFields) {
			for(MField field : ASPUtil.getInstance(context).getWindowFields(tab.getAD_Tab_ID())) {
				Field.Builder fieldBuilder = WindowConvertUtil.convertField(
					context,
					field,
					false
				);
				builder.addFields(fieldBuilder.build());
			}
		}
		//	
		return builder;
	}
	
	/**
	 * Convert field to builder
	 * @param field
	 * @param translate
	 * @return
	 */
	public static Field.Builder convertField(Properties context, MField field, boolean translate) {
		if (field == null) {
			return Field.newBuilder();
		}

		// Column reference
		MColumn column = MColumn.get(context, field.getAD_Column_ID());
		M_Element element = new M_Element(context, column.getAD_Element_ID(), null);
		String defaultValue = field.getDefaultValue();
		if(Util.isEmpty(defaultValue)) {
			defaultValue = column.getDefaultValue();
		}
		//	Display Type
		int displayTypeId = column.getAD_Reference_ID();
		if(field.getAD_Reference_ID() > 0) {
			displayTypeId = field.getAD_Reference_ID();
		}
		//	Mandatory Property
		boolean isMandatory = column.isMandatory();
		if(!Util.isEmpty(field.getIsMandatory())) {
			isMandatory = !Util.isEmpty(field.getIsMandatory()) && field.getIsMandatory().equals("Y");
		}
		//	Convert
		Field.Builder builder = Field.newBuilder()
			.setId(field.getAD_Field_ID())
			.setUuid(
				ValueManager.validateNull(field.getUUID())
			)
			.setName(
				ValueManager.validateNull(field.getName())
			)
			.setDescription(
				ValueManager.validateNull(field.getDescription())
			)
			.setHelp(
				ValueManager.validateNull(field.getHelp())
			)
			.setCallout(
				ValueManager.validateNull(column.getCallout())
			)
			.setColumnId(column.getAD_Column_ID())
			.setColumnName(
				ValueManager.validateNull(column.getColumnName())
			)
			.setElementId(element.getAD_Element_ID())
			.setElementName(
				ValueManager.validateNull(element.getColumnName())
			)
			.setColumnSql(
				ValueManager.validateNull(column.getColumnSQL())
			)
			.setDefaultValue(
				ValueManager.validateNull(defaultValue)
			)
			.setDisplayLogic(
				ValueManager.validateNull(field.getDisplayLogic())
			)
			.setDisplayType(displayTypeId)
			.setFormatPattern(
				ValueManager.validateNull(column.getFormatPattern())
			)
			.setIdentifierSequence(column.getSeqNo())
			.setIsAllowCopy(field.isAllowCopy())
			.setIsAllowLogging(column.isAllowLogging())
			.setIsDisplayed(field.isDisplayed())
			.setIsAlwaysUpdateable(column.isAlwaysUpdateable())
			.setIsDisplayedGrid(field.isDisplayedGrid())
			.setIsEncrypted(field.isEncrypted() || column.isEncrypted())
			.setIsFieldOnly(field.isFieldOnly())
			.setIsHeading(field.isHeading())
			.setIsIdentifier(column.isIdentifier())
			.setIsKey(column.isKey())
			.setIsMandatory(isMandatory)
			.setIsParent(column.isParent())
			.setIsQuickEntry(field.isQuickEntry())
			.setIsRange(column.isRange())
			.setIsReadOnly(field.isReadOnly())
			.setIsSameLine(field.isSameLine())
			.setIsSelectionColumn(column.isSelectionColumn())
			.setIsTranslated(column.isTranslated())
			.setIsUpdateable(column.isUpdateable())
			.setMandatoryLogic(
				ValueManager.validateNull(column.getMandatoryLogic())
			)
			.setReadOnlyLogic(
				ValueManager.validateNull(column.getReadOnlyLogic())
			)
			.setSequence(field.getSeqNo())
			.setValueMax(
				ValueManager.validateNull(column.getValueMax())
			)
			.setValueMin(
				ValueManager.validateNull(column.getValueMin())
			)
			.setFieldLength(column.getFieldLength())
			.setIsActive(field.isActive())
			.addAllContextColumnNames(
				ContextManager.getContextColumnNames(
					Optional.ofNullable(field.getDefaultValue()).orElse(
						Optional.ofNullable(column.getDefaultValue()).orElse("")
					)
				)
			)
		;

		//	Context Info
		if(field.getAD_ContextInfo_ID() > 0) {
			ContextInfo.Builder contextInfoBuilder = DictionaryConvertUtil.convertContextInfo(
				context,
				field.getAD_ContextInfo_ID()
			);
			builder.setContextInfo(contextInfoBuilder.build());
		}
		//	Process
		if(column.getAD_Process_ID() > 0) {
			MProcess process = MProcess.get(context, column.getAD_Process_ID());
			Process.Builder processBuilder = ProcessConvertUtil.convertProcess(
				context,
				process,
				false
			);
			builder.setProcess(processBuilder.build());
		}
		//
		if (ReferenceUtil.validateReference(displayTypeId)) {
			//	Reference Value
			int referenceValueId = column.getAD_Reference_Value_ID();
			if(field.getAD_Reference_Value_ID() > 0) {
				referenceValueId = field.getAD_Reference_Value_ID();
			}
			//	Validation Code
			int validationRuleId = column.getAD_Val_Rule_ID();
			if(field.getAD_Val_Rule_ID() > 0) {
				validationRuleId = field.getAD_Val_Rule_ID();
			}

			MLookupInfo info = ReferenceUtil.getReferenceLookupInfo(
				displayTypeId, referenceValueId, column.getColumnName(), validationRuleId
			);
			if (info != null) {
				Reference.Builder referenceBuilder = DictionaryConvertUtil.convertReference(context, info);
				builder.setReference(referenceBuilder.build());
			} else {
				builder.setDisplayType(DisplayType.String);
			}
		} else if (DisplayType.Button == displayTypeId) {
			if (column.getColumnName().equals("Record_ID")) {
				builder.addContextColumnNames(I_AD_Table.COLUMNNAME_AD_Table_ID);
			}
		}

		//	Field Definition
		if(field.getAD_FieldDefinition_ID() > 0) {
			FieldDefinition.Builder fieldDefinitionBuilder = WindowConvertUtil.convertFieldDefinition(
				context,
				field.getAD_FieldDefinition_ID()
			);
			builder.setFieldDefinition(fieldDefinitionBuilder);
		}
		//	Field Group
		if(field.getAD_FieldGroup_ID() > 0) {
			FieldGroup.Builder fieldGroup = WindowConvertUtil.convertFieldGroup(
				context,
				field.getAD_FieldGroup_ID()
			);
			builder.setFieldGroup(fieldGroup.build());
		}

		MFieldCustom fieldCustom = FieldCustomUtil.getFieldCustom(field.getAD_Field_ID());
		if (fieldCustom != null && fieldCustom.isActive()) {
			// ASP default displayed field as panel
			if (fieldCustom.get_ColumnIndex(org.spin.dictionary.util.DictionaryUtil.IS_DISPLAYED_AS_PANEL_COLUMN_NAME) >= 0) {
				builder.setIsDisplayedAsPanel(
					ValueManager.validateNull(
						fieldCustom.get_ValueAsString(
							org.spin.dictionary.util.DictionaryUtil.IS_DISPLAYED_AS_PANEL_COLUMN_NAME
						)
					)
				);
			}
			// ASP default displayed field as table
			if (fieldCustom.get_ColumnIndex(org.spin.dictionary.util.DictionaryUtil.IS_DISPLAYED_AS_TABLE_COLUMN_NAME) >= 0) {
				builder.setIsDisplayedAsTable(
					ValueManager.validateNull(
						fieldCustom.get_ValueAsString(
							org.spin.dictionary.util.DictionaryUtil.IS_DISPLAYED_AS_TABLE_COLUMN_NAME
						)
					)
				);
			}
		}

		List<DependentField> depenentFieldsList = generateDependentFields(field);
		builder.addAllDependentFields(depenentFieldsList);

		return builder;
	}

	public static List<DependentField> generateDependentFields(MField field) {
		List<DependentField> depenentFieldsList = new ArrayList<>();
		if (field == null) {
			return depenentFieldsList;
		}

		int columnId = field.getAD_Column_ID();
		String parentColumnName = MColumn.getColumnName(Env.getCtx(), columnId);

		MTab parentTab = MTab.get(Env.getCtx(), field.getAD_Tab_ID());
		List<MTab> tabsList = ASPUtil.getInstance(Env.getCtx()).getWindowTabs(parentTab.getAD_Window_ID());
		if (tabsList == null) {
			return depenentFieldsList;
		}
		tabsList.stream()
			.filter(currentTab -> {
				// transaltion tab is not rendering on client
				return currentTab.isActive() && !currentTab.isTranslationTab();
			})
			.forEach(tab -> {
				List<MField> fieldsList = ASPUtil.getInstance().getWindowFields(tab.getAD_Tab_ID());
				if (fieldsList == null) {
					return;
				}

				fieldsList.stream()
					.filter(currentField -> {
						if (!currentField.isActive()) {
							return false;
						}
						// Display Logic
						if (ContextManager.isUseParentColumnOnContext(parentColumnName, currentField.getDisplayLogic())) {
							return true;
						}
						// Default Value of Field
						if (ContextManager.isUseParentColumnOnContext(parentColumnName, currentField.getDefaultValue())) {
							return true;
						}
						// Dynamic Validation
						if (currentField.getAD_Val_Rule_ID() > 0) {
							MValRule validationRule = MValRule.get(Env.getCtx(), currentField.getAD_Val_Rule_ID());
							if (ContextManager.isUseParentColumnOnContext(parentColumnName, validationRule.getCode())) {
								return true;
							}
						}

						MColumn currentColumn = MColumn.get(Env.getCtx(), currentField.getAD_Column_ID());
						// Default Value of Column
						if (ContextManager.isUseParentColumnOnContext(parentColumnName, currentColumn.getDefaultValue())) {
							return true;
						}
						// ReadOnly Logic
						if (ContextManager.isUseParentColumnOnContext(parentColumnName, currentColumn.getReadOnlyLogic())) {
							return true;
						}
						// Mandatory Logic
						if (ContextManager.isUseParentColumnOnContext(parentColumnName, currentColumn.getMandatoryLogic())) {
							return true;
						}
						// Dynamic Validation
						if (currentColumn.getAD_Val_Rule_ID() > 0) {
							MValRule validationRule = MValRule.get(Env.getCtx(), currentColumn.getAD_Val_Rule_ID());
							if (ContextManager.isUseParentColumnOnContext(parentColumnName, validationRule.getCode())) {
								return true;
							}
						}
						return false;
					})
					.forEach(currentField -> {
						DependentField.Builder builder = DependentField.newBuilder()
							.setContainerId(
								tab.getAD_Tab_ID()
							)
							.setContainerUuid(
								ValueManager.validateNull(
									tab.getUUID()
								)
							)
							.setContainerName(
								ValueManager.validateNull(
									tab.getName()
								)
							)
							.setId(
								currentField.getAD_Field_ID()
							)
							.setUuid(
								ValueManager.validateNull(
									currentField.getUUID()
								)
							)
						;

						String currentColumnName = MColumn.getColumnName(Env.getCtx(), currentField.getAD_Column_ID());
						builder.setColumnName(
							ValueManager.validateNull(
								currentColumnName
							)
						);

						depenentFieldsList.add(builder.build());
					});
			});

		return depenentFieldsList;
	}


	/**
	 * Convert Field Group to builder
	 * @param fieldGroupId
	 * @return
	 */
	public static FieldGroup.Builder convertFieldGroup(Properties context, int fieldGroupId) {
		FieldGroup.Builder builder = FieldGroup.newBuilder();
		if(fieldGroupId <= 0) {
			return builder;
		}
		X_AD_FieldGroup fieldGroup  = new X_AD_FieldGroup(context, fieldGroupId, null);
		//	Get translation
		String name = null;
		String language = Env.getAD_Language(context);
		if(!Util.isEmpty(language)) {
			name = fieldGroup.get_Translation(I_AD_FieldGroup.COLUMNNAME_Name, language);
		}
		//	Validate for default
		if(Util.isEmpty(name)) {
			name = fieldGroup.getName();
		}
		//	Field Group
		builder = FieldGroup.newBuilder()
			.setId(fieldGroup.getAD_FieldGroup_ID())
			.setUuid(
				ValueManager.validateNull(fieldGroup.getUUID())
			)
			.setName(
				ValueManager.validateNull(name))
			.setFieldGroupType(
				ValueManager.validateNull(fieldGroup.getFieldGroupType())
			)
			.setIsActive(fieldGroup.isActive())
		;
		return builder;
	}


	/**
	 * Convert Field Definition to builder
	 * @param fieldDefinitionId
	 * @return
	 */
	public static FieldDefinition.Builder convertFieldDefinition(Properties context, int fieldDefinitionId) {
		FieldDefinition.Builder builder = null;
		if(fieldDefinitionId <= 0) {
			return builder;
		}
		MADFieldDefinition fieldDefinition  = new MADFieldDefinition(context, fieldDefinitionId, null);
		//	Reference
		builder = FieldDefinition.newBuilder()
			.setId(fieldDefinition.getAD_FieldDefinition_ID())
			.setUuid(
				ValueManager.validateNull(fieldDefinition.getUUID())
			)
			.setValue(
				ValueManager.validateNull(fieldDefinition.getValue())
			)
			.setName(
				ValueManager.validateNull(fieldDefinition.getName())
			)
		;
		//	Get conditions
		for(MADFieldCondition condition : fieldDefinition.getConditions()) {
			if(!condition.isActive()) {
				continue;
			}
			FieldCondition.Builder fieldConditionBuilder = FieldCondition.newBuilder()
				.setId(condition.getAD_FieldCondition_ID())
				.setUuid(
					ValueManager.validateNull(condition.getUUID())
				)
				.setCondition(
					ValueManager.validateNull(condition.getCondition())
				)
				.setStylesheet(
					ValueManager.validateNull(condition.getStylesheet())
				)
				.setIsActive(fieldDefinition.isActive())
			;
			//	Add to parent
			builder.addConditions(fieldConditionBuilder);
		}
		return builder;
	}

}
