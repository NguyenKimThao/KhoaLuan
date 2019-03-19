package org.ext.datastorefield;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.util.xml.Element;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.impl.value.PrimitiveTypeValueImpl.LongValueImpl;
import org.camunda.bpm.engine.variable.type.ValueType;
import org.camunda.bpm.engine.variable.value.LongValue;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.ext.constantutil.ConstantUtil;

public class DatastoreField {
	private String column;
	private String variableId;
	private String variableType;
	private String datePattern;

	private final static String extensionElementsString = ConstantUtil.getConstantsMap().get("extensionElements");
	private final static String datastoreFieldsString = ConstantUtil.getConstantsMap().get("dataStoreFields");
	private final static String serviceDataFieldsString = ConstantUtil.getConstantsMap().get("serviceDataFields");
	private final static String fieldString = ConstantUtil.getConstantsMap().get("field");
	private final static String servicefieldString = ConstantUtil.getConstantsMap().get("serviceField");
	private final static String columnString = ConstantUtil.getConstantsMap().get("column");
	private final static String variableString = ConstantUtil.getConstantsMap().get("variable");

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getVariableId() {
		return variableId;
	}

	public void setVariableId(String variableId) {
		this.variableId = variableId;
	}

	public DatastoreField(String column, String variableId, String variableType) {
		this.column = column;
		this.variableId = variableId;
		this.variableType = variableType;
	}

	public static List<DatastoreField> getDatastoreFieldsList(Element dataAssociationElement,
			Collection<CamundaFormField> formFields) {
		Element extensionElement = dataAssociationElement.element(extensionElementsString);
		if (extensionElement == null) {
			return new ArrayList<>();
		}
		String fieldStr = servicefieldString;
		Element datastoreField = null;

		if (extensionElement.element(ConstantUtil.getConstantsMap().get("dataCondition")).attribute("action") != null) {
			datastoreField = extensionElement.element(serviceDataFieldsString);
		}

		if (datastoreField == null)
			return new ArrayList<>();
		ArrayList<DatastoreField> result = new ArrayList<>();
		Map<String, String> vcMap = new HashMap<String, String>();

		datastoreField.elements(fieldStr).forEach(f -> {
			if (f.attribute(variableString) != null && f.attribute(columnString) != null)
				vcMap.put(f.attribute(variableString), f.attribute(columnString));
		});

		if (formFields != null) {
			formFields.forEach(field -> {
				String fieldId = field.getCamundaId();
				DatastoreField dataStoreField = new DatastoreField(vcMap.get(fieldId), fieldId, "");
				if (vcMap.containsKey(fieldId)) {
					dataStoreField.setVariableType(field.getCamundaType());
					if (field.getCamundaType() == "date") {
						dataStoreField.setDatePattern(field.getCamundaDatePattern());
					}
					result.add(dataStoreField);
				}

			});
		} else {
			datastoreField.elements(fieldStr).forEach(f -> {
				if (f.attribute(variableString) != null && f.attribute(columnString) != null)
					result.add(new DatastoreField(f.attribute(columnString), f.attribute(variableString),
							f.attribute("type")));
			});
		}

		return result;
	}

	public static List<DatastoreField> getDatastoreFieldsParamInput(Element dataAssociationElement,
			Collection<CamundaFormField> formFields) {

		Element extensionElement = dataAssociationElement.element(extensionElementsString);
		if (extensionElement == null) {
			return new ArrayList<>();
		}
		String fieldStr = fieldString;
		Element datastoreField = extensionElement.element(datastoreFieldsString);
		if (datastoreField == null)
			return new ArrayList<>();
		ArrayList<DatastoreField> result = new ArrayList<>();
		Map<String, String> vcMap = new HashMap<String, String>();

		datastoreField.elements(fieldStr).forEach(f -> {
			if (f.attribute(variableString) != null && f.attribute(columnString) != null)
				vcMap.put(f.attribute(variableString), f.attribute(columnString));
		});

		if (formFields != null) {
			formFields.forEach(field -> {
				String fieldId = field.getCamundaId();
				DatastoreField dataStoreField = new DatastoreField(vcMap.get(fieldId), fieldId, "");
				if (vcMap.containsKey(fieldId)) {
					dataStoreField.setVariableType(field.getCamundaType());
					if (field.getCamundaType() == "date") {
						dataStoreField.setDatePattern(field.getCamundaDatePattern());
					}
					result.add(dataStoreField);
				}

			});
		} else {
			datastoreField.elements(fieldStr).forEach(f -> {
				if (f.attribute(variableString) != null && f.attribute(columnString) != null)
					result.add(new DatastoreField(f.attribute(columnString), f.attribute(variableString),
							f.attribute("type")));
			});
		}

		return result;
	}

	public String getVariableType() {
		return variableType;
	}

	public void setVariableType(String variableType) {
		this.variableType = variableType;
	}

	public String getDatePattern() {
		return datePattern;
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

	public TypedValue getValue(ResultSet rs) throws SQLException {
		if (this.variableType.indexOf("date") != -1 || this.variableType.indexOf("time") != -1)
			return Variables.dateValue(rs.getDate(this.getColumn()));
		return Variables.objectValue(rs.getObject(this.getColumn())).create();
	}
}
