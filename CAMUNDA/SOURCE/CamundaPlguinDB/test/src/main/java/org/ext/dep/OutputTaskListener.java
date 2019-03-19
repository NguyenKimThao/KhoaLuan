package org.ext.dep;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.PreparedStatement;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.impl.form.type.StringFormType;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.value.PrimitiveTypeValueImpl.StringValueImpl;
import org.camunda.bpm.engine.variable.value.FileValue;
import org.camunda.bpm.engine.variable.value.StringValue;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.ext.constantutil.ConstantUtil;
import org.ext.datastorefield.DatastoreField;
import org.ext.ssconnector.SpreadsheetConnector;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class OutputTaskListener extends DataTaskListener {
	private final Logger LOGGER = Logger.getLogger(OutputTaskListener.class.getName());
	private final String namespaceUri = ConstantUtil.getConstantsMap().get("namespaceUri");
	private final String bpmnNamespaceUri = ConstantUtil.getConstantsMap().get("bpmnNamespaceUri");

	public OutputTaskListener(Element userTaskElement) {
		super(userTaskElement);
	}

	@Override
	public void notify(DelegateTask task) {
		RepositoryService repositoryService = task.getProcessEngineServices().getRepositoryService();
		DomDocument doc = repositoryService.getBpmnModelInstance(task.getProcessDefinitionId()).getDocument();
		RuntimeService runtimeService = task.getProcessEngineServices().getRuntimeService();

		if (!userTaskElement.elements("dataOutputAssociation").isEmpty()) {
			userTaskElement.elements("dataOutputAssociation").forEach(element -> {
				Collection<CamundaFormField> formFields = getFormfields(task.getBpmnModelElementInstance());

				Element extensionElements = element.element(ConstantUtil.getConstantsMap().get("extensionElements"));
				Element dataConditionElement = extensionElements
						.element(ConstantUtil.getConstantsMap().get("dataCondition"));

				List<DatastoreField> inputParamer = DatastoreField.getDatastoreFieldsParamInput(element, null);

				List<DatastoreField> fieldList = DatastoreField.getDatastoreFieldsList(element, null);

				List<String> variableNames = getVariableName(dataConditionElement, inputParamer, fieldList);

				VariableMap variableMap = runtimeService.getVariablesLocalTyped(task.getProcessInstanceId());

				for (Entry<String, Object> entry : variableMap.entrySet()) {
					// DatastoreField field = inputParamer.get(i);
					// String type = field.getVariableType();
					String type = variableMap.getValueTyped(entry.getKey()).getType().getName();
					if ("file".equals(type)) {
						String str = "{processInstanceId:" + task.getProcessInstanceId() + ",name:" + entry.getKey()
								+ "}";
						StringValue stringValue = new StringValueImpl(str);
						variableMap.put(entry.getKey(), stringValue);
					}
				}

				if (formFields.isEmpty()) {
					for (DatastoreField field : inputParamer) {
						field.setVariableType(variableMap.getValueTyped(field.getVariableId()).getType().getName());
					}
				} else {
					for (CamundaFormField formField : formFields) {
						String id = formField.getCamundaId();
						for (DatastoreField field : inputParamer) {
							if (id.equals(field.getVariableId())) {
								field.setVariableType(formField.getCamundaType());
							}
						}
					}
				}

				if ("dataStoreReference"
						.equals(doc.getElementById(element.element("targetRef").getText()).getLocalName())) {
					DomElement property = this.getDatabaseProperty(doc, element.element("targetRef").getText());

					try {
						this.getConnectionFromDatastore(property, formFields);
						connector.setProcessEngineService(task.getProcessEngineServices());
						ResultSet rs = null;
						boolean isResult = false;
						if ("query".equals(dataConditionElement.attribute("action"))) {
							String query = parseStringQuery(dataConditionElement, variableMap);
							Statement statement = connector.getConnection().createStatement();
							rs = connector.executeInputStatement(statement, query);
							isResult = true;
						} else if ("procedure".equals(dataConditionElement.attribute("action"))) {

							PreparedStatement statement = connector.getInputStatement(element, variableMap,
									inputParamer);
							rs = connector.execute(statement);
							isResult = true;
						} else {
							PreparedStatement statement = connector.getOutputStatement(element, variableMap,
									inputParamer);
							connector.executeOutputStatement(statement);
						}
						if (isResult) {
							setResult(rs, fieldList, dataConditionElement, task, runtimeService);
						}
						connector.closeConnection();
					} catch (Exception e) {
						LOGGER.severe(e.getMessage());
						return;
					}
				}

				DomElement dataObjectReference = doc.getElementById(element.element("targetRef").getText());

				if ("dataObjectReference".equals(dataObjectReference.getLocalName())) {
					SpreadsheetConnector service;
					try {
						service = this.connectToService(dataObjectReference);
						if (!service.checkIfSheetExists()) {
							LOGGER.severe("File does not exist!");
							return;
						}
						if (!service.checkingSheetFile(formFields)) {
							LOGGER.severe("Wrong format!");
							return;
						}

						service.batchUpdate(variableMap);
					} catch (ParseException e) {
						LOGGER.log(Level.SEVERE, "Cannot parse file bpmn", e.getCause());
					} catch (IOException e) {
						LOGGER.log(Level.SEVERE, "Cannot Access sheet file", e.getCause());
					}
				}
			});
		}

	}

	protected SpreadsheetConnector connectToService(DomElement dataObjectReference) throws ParseException, IOException {
		DomElement extensionElements = dataObjectReference
				.getChildElementsByNameNs(bpmnNamespaceUri, "extensionElements").get(0);
		DomElement serviceElement = extensionElements.getChildElementsByNameNs(namespaceUri, "service").get(0);
		String token = serviceElement.getAttribute("token");
		JSONParser parser = new JSONParser();
		JSONObject object = (JSONObject) parser.parse(token);
		String accessToken = (String) object.get("access_token");
		String refreshToken = (String) object.get("refresh_token");
		String tokenType = (String) object.get("token_type");
		String spreadsheetId = serviceElement.getAttribute("fileid");
		Integer sheetId = Integer.valueOf(serviceElement.getAttribute("sheetId"));

		return new SpreadsheetConnector(accessToken, refreshToken, tokenType, spreadsheetId, sheetId);

	}

}
