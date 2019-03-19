package org.ext.dep;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.conn.EofSensorWatcher;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.impl.value.PrimitiveTypeValueImpl.IntegerValueImpl;
import org.camunda.bpm.engine.variable.impl.value.PrimitiveTypeValueImpl.LongValueImpl;
import org.camunda.bpm.engine.variable.impl.value.PrimitiveTypeValueImpl.StringValueImpl;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.spin.plugin.variable.SpinValues;
import org.camunda.spin.plugin.variable.value.JsonValue;
import org.ext.constantutil.ConstantParseUtil;
import org.ext.constantutil.ConstantUtil;
import org.ext.datacondition.DataCondition;
import org.ext.datastorefield.DatastoreField;

import com.google.gson.Gson;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class InputTaskListener extends DataTaskListener {
	public InputTaskListener(Element userTaskElement) {
		super(userTaskElement);
	}

	private final Logger LOGGER = Logger.getLogger(InputTaskListener.class.getName());

	@Override
	public void notify(DelegateTask task) {
		RepositoryService repositoryService = task.getProcessEngineServices().getRepositoryService();
		FormService formService = task.getProcessEngineServices().getFormService();

		DomDocument doc = repositoryService.getBpmnModelInstance(task.getProcessDefinitionId()).getDocument();
		RuntimeService service = task.getProcessEngineServices().getRuntimeService();
		Collection<CamundaFormField> formFields = getFormfields(task.getBpmnModelElementInstance());

		if (!userTaskElement.elements("dataInputAssociation").isEmpty()) {
			userTaskElement.elements("dataInputAssociation").forEach(element -> {

				Element extensionElements = element.element(ConstantUtil.getConstantsMap().get("extensionElements"));
				Element dataConditionElement = extensionElements
						.element(ConstantUtil.getConstantsMap().get("dataCondition"));

				DomElement property = this.getDatabaseProperty(doc, element.element("sourceRef").getText());
				List<DatastoreField> fieldList = DatastoreField.getDatastoreFieldsList(element, null);
				List<DatastoreField> inputParamerList = DatastoreField.getDatastoreFieldsParamInput(element, null);

				List<String> variableNames = getVariableName(dataConditionElement, fieldList, inputParamerList);

				VariableMap variableMap = formService.getTaskFormVariables(task.getId(), variableNames, true);

				try {
					this.getConnectionFromDatastore(property, formFields);
					connector.setProcessEngineService(task.getProcessEngineServices());
					ResultSet rs = null;
					if ("query".equals(dataConditionElement.attribute("action"))) {
						String query = parseStringQuery(dataConditionElement, variableMap);
						Statement statement = connector.getConnection().createStatement();
						rs = connector.executeInputStatement(statement, query);
					} else if ("procedure".equals(dataConditionElement.attribute("action"))) {

						PreparedStatement statement = connector.getInputStatement(element, variableMap,
								inputParamerList);
						rs = connector.execute(statement);
					} else {
						PreparedStatement statement = connector.getInputStatement(element, variableMap, inputParamerList);
						rs = connector.executeInputStatement(statement);
					}

					if (rs == null) {
						connector.closeConnection();
						return;
					}

					setResult(rs, fieldList, dataConditionElement, task, service);

					connector.closeConnection();
				} catch (Exception e) {
					service.setVariable(task.getProcessInstanceId(), "error", new StringValueImpl(e.getMessage()));
					LOGGER.severe(e.getMessage());
					return;
				}
			});
		}
	}

}
