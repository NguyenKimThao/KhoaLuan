package org.ext.dep;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.impl.value.PrimitiveTypeValueImpl.IntegerValueImpl;
import org.camunda.bpm.engine.variable.impl.value.PrimitiveTypeValueImpl.StringValueImpl;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.ext.constantutil.ConstantParseUtil;
import org.ext.constantutil.ConstantUtil;
import org.ext.datastorefield.DatastoreField;
import org.ext.dbconnector.DbConnector;
import org.ext.dbconnector.MsSqlConnector;
import org.ext.dbconnector.MySqlConnector;
import org.ext.dbconnector.OracleSqlConnector;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class DataTaskListener implements TaskListener {

	protected Element userTaskElement;
	protected final String databaseType = ConstantUtil.getConstantsMap().get("databaseType");
	protected final String username = ConstantUtil.getConstantsMap().get("username");
	protected final String password = ConstantUtil.getConstantsMap().get("password");
	protected final String server = ConstantUtil.getConstantsMap().get("server");
	protected final String database = ConstantUtil.getConstantsMap().get("database");
	protected final String namespaceUri = ConstantUtil.getConstantsMap().get("namespaceUri");
	protected final String MySQL = "mysql";
	protected final String OracleSQL = "oracle";
	protected final String MsSQL = "mssql";
	protected String datePattern;

	protected DbConnector connector;

	public DataTaskListener(Element userTaskElement) {
		this.userTaskElement = userTaskElement;
	}

	@Override
	public void notify(DelegateTask delegateTask) {
	}

	protected Connection getConnectionFromDatastore(DomElement property, Collection<CamundaFormField> formFields)
			throws Exception {
		switch (property.getAttribute(databaseType)) {
		case MySQL:
			connector = new MySqlConnector(property.getAttribute(server), "", property.getAttribute(username),
					property.getAttribute(password), property.getAttribute(database), formFields);

			break;
		case OracleSQL:
			connector = new OracleSqlConnector(property.getAttribute(server), "", property.getAttribute(username),
					property.getAttribute(password), property.getAttribute(database), formFields);

			break;
		case MsSQL:
			connector = new MsSqlConnector(property.getAttribute(server), "", property.getAttribute(username),
					property.getAttribute(password), property.getAttribute(database), formFields);

			break;
		default:
			break;
		}

		try {
			return connector.connect();

		} catch (Exception e) {
			throw e;
		}

	}

	protected DomElement getDatabaseProperty(DomDocument doc, String Id) {
		DomElement dataStoreElement = doc.getElementById(Id);
		DomElement extensionElements = dataStoreElement.getChildElements().get(0);
		DomElement databaseInformation = extensionElements.getChildElementsByNameNs(namespaceUri, "databaseInformation")
				.get(0);
		return databaseInformation;
	}

	protected Collection<CamundaFormField> getFormfields(UserTask userTaskElement) {
		ExtensionElements extensionElements = userTaskElement.getExtensionElements();
		if (extensionElements != null) {
			CamundaFormData formData = extensionElements.getElementsQuery().filterByType(CamundaFormData.class)
					.singleResult();
			if (formData != null) {
				return formData.getCamundaFormFields();
			}
		}
		return null;
	}

	protected List<String> getVariable(String name) {
		List<String> variableNames = new ArrayList<>();

		Pattern pattern = Pattern.compile("\\$\\{([^}\\s]+)\\}");
		Matcher m = pattern.matcher(name);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String repString = m.group(1);
			if (repString != null) {
				variableNames.add(repString);
			}
		}
		return variableNames;
	}

	protected List<String> getVariableName(Element dataConditionElement, List<DatastoreField> fieldList,
			List<DatastoreField> fieldListProcedure) {

		List<String> variableNames = new ArrayList<>();
		if (fieldList != null) {
			fieldList.forEach(field -> {
				if (variableNames.indexOf(field.getVariableId()) != -1)
					variableNames.add(field.getVariableId());
			});
		}
		if (fieldListProcedure != null) {
			fieldListProcedure.forEach(field -> {
				if (variableNames.indexOf(field.getVariableId()) != -1)
					variableNames.add(field.getVariableId());
			});
		}
		if (dataConditionElement != null) {
			List<String> variableParse = ConstantParseUtil.getConstantsMap();
			variableParse.forEach(var -> {
				String name = dataConditionElement.attribute(ConstantUtil.getConstantsMap().get(var));
				if (name != null && !name.equals("")) {
					List<String> variable = getVariable(name);
					variable.forEach(v -> {
						if (variableNames.indexOf(v) != -1)
							variableNames.add(v);
					});
				}
			});
		}
		return variableNames;
	}

	protected String parseStringQuery(Element dataConditionElement, VariableMap variableMap) {
		String sql = dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("execquery")).trim();
		if (sql == null || sql.isEmpty())
			return "";
		Pattern pattern = Pattern.compile("\\$\\{([^}\\s]+)\\}");
		Matcher m = pattern.matcher(sql);
		StringBuffer sb = new StringBuffer();
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		while (m.find()) {
			String repString = m.group(1);
			if (repString != null) {
				if (variableMap.get(repString) instanceof Date) {
					System.out.println("value: " + df.format(variableMap.get(repString)));
					m.appendReplacement(sb, "'" + df.format(variableMap.get(repString)) + "'");
				} else {
					m.appendReplacement(sb, "'" + variableMap.get(repString) + "'");
				}
			}
		}
		m.appendTail(sb);
		sql = sb.toString().trim();
		return sql;
	}

	protected ArrayList<Map<String, Object>> getResult(ResultSet rs, List<DatastoreField> fieldList)
			throws SQLException {
		ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			Map<String, Object> rows = new HashMap<String, Object>();
			if (fieldList.size() == 0) {
				ResultSetMetaData rsmd = rs.getMetaData();
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					rows.put(rsmd.getColumnName(i + 1), rs.getObject(rsmd.getColumnName(i + 1)));
				}
			} else {
				for (DatastoreField field : fieldList) {
					rows.put(field.getVariableId(), rs.getObject(field.getColumn()));
				}
			}
			results.add(rows);
		}

		rs.close();
		return results;
	}

	protected void setResult(ResultSet rs, List<DatastoreField> fieldList, Element dataConditionElement,
			DelegateTask task, RuntimeService service) throws SQLException {
		ArrayList<Map<String, Object>> results = getResult(rs, fieldList);

		String type = dataConditionElement.attribute("typeOutput");
		if (type == null)
			type = "2";

		service.setVariable(task.getProcessInstanceId(), "count", new IntegerValueImpl(results.size()));
		switch (type) {
		case "0":
			break;
		case "1": {
			if (results.size() < 1)
				break;
			Map<String, Object> rows = results.get(0);
			rows.forEach((k, v) -> {
				TypedValue jsonDataValue = null;
				if (v instanceof String) {
					String val = (String) v;
					if (val != null && val.startsWith("{") && val.endsWith("}")) {
						JSONParser parser = new JSONParser();
						try {
							JSONObject json = (JSONObject) parser.parse(val);
							String processInstanceId = json.getAsString("processInstanceId");
							String valuename = json.getAsString("name");
							List<HistoricVariableInstance> lsithis = task.getProcessEngineServices().getHistoryService()
									.createHistoricVariableInstanceQuery().processInstanceIdIn(processInstanceId)
									.variableName(valuename).list();
							if (lsithis.size() > 0) {
								HistoricVariableInstance varia = lsithis.get(0);
								String str = "history/variable-instance/" + varia.getId() + "/data";
								jsonDataValue = new StringValueImpl(str);
							}
						} catch (ParseException e) {

						}
					}
				}
				if (jsonDataValue == null) {
					if (v instanceof Date) {
						jsonDataValue = Variables.dateValue((Date) v);
					} else {
						jsonDataValue = Variables.objectValue(v)
								.serializationDataFormat(Variables.SerializationDataFormats.JSON).create();
					}
				}
				service.setVariable(task.getProcessInstanceId(), k, jsonDataValue);
			});
			break;
		}
		case "2": {
			ObjectValue jsonDataValue = Variables.objectValue(results)
					.serializationDataFormat(Variables.SerializationDataFormats.JSON).create();
			String nameVariable = dataConditionElement.attribute("nameVariables");
			if (nameVariable == null || nameVariable.equals(""))
				nameVariable = dataConditionElement.attribute("databaseTable");
			if (nameVariable != null && !nameVariable.equals(""))
				service.setVariable(task.getProcessInstanceId(), nameVariable, jsonDataValue);
			break;
		}
		case "3": {
			Map<String, Object> resultsJson = new HashMap<String, Object>();
			String key = dataConditionElement.attribute("keymultirow");
			String text = dataConditionElement.attribute("textmultirow");

			if (key == null || text == null || key.isEmpty() || text.isEmpty() || results.size() == 0)
				break;
			Map<String, Object> rows0 = results.get(0);
			if (!rows0.containsKey(key) || !rows0.containsKey(text))
				break;
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> rows = results.get(i);
				if (rows.get(key) == null)
					resultsJson.put("", rows.get(text));
				else
					resultsJson.put(rows.get(key).toString(), rows.get(text));
			}
			ObjectValue jsonDataValue = Variables.objectValue(resultsJson)
					.serializationDataFormat(Variables.SerializationDataFormats.JSON).create();

			String nameVariable = dataConditionElement.attribute("nameVariables");
			if (nameVariable == null || nameVariable.equals(""))
				nameVariable = dataConditionElement.attribute("databaseTable");
			if (nameVariable != null && !nameVariable.equals(""))
				service.setVariable(task.getProcessInstanceId(), nameVariable, jsonDataValue);

			break;
		}
		default:
			break;
		}
	}
}
