package org.ext.form;

import java.util.ArrayList;

import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParse;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.form.TaskFormDataImpl;
import org.camunda.bpm.engine.impl.form.handler.DefaultTaskFormHandler;
import org.camunda.bpm.engine.impl.form.handler.FormFieldHandler;
import org.camunda.bpm.engine.impl.form.type.AbstractFormFieldType;
import org.camunda.bpm.engine.impl.form.type.FormTypes;
import org.camunda.bpm.engine.impl.form.type.StringFormType;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.ext.dep.FileFormFieldType;
import org.ext.dep.NameFileFieldType;
import org.ext.form.field.ExtendFormFieldHandler;
import org.ext.form.item.FormItem;

public class ExtendTaskFormHandler extends DefaultTaskFormHandler {

	protected String typeSubmit;

	public ExtendTaskFormHandler() {
	}

	public TaskFormData createTaskForm(TaskEntity task) {
		ExtendTaskFormData taskFormData = new ExtendTaskFormData();

		taskFormData.setTypeSubmit(this.typeSubmit);
		Expression formKey = task.getTaskDefinition().getFormKey();
		if (formKey != null) {
			Object formValue = formKey.getValue(task);
			if (formValue != null) {
				taskFormData.setFormKey(formValue.toString());
			}
		}

		taskFormData.setDeploymentId(deploymentId);
		taskFormData.setTask(task);
		initializeFormProperties(taskFormData, task.getExecution());
		initializeFormFields(taskFormData, task.getExecution());
		return taskFormData;
	}

	public void parseConfiguration(Element activityElement, String deploymentId,
			ProcessDefinitionEntity processDefinition, BpmnParse bpmnParse) {
		this.deploymentId = deploymentId;
		this.setTypeSubmit(activityElement.attributeNS(BpmnParse.CAMUNDA_BPMN_EXTENSIONS_NS, "typeSubmit"));

		ExpressionManager expressionManager = Context.getProcessEngineConfiguration().getExpressionManager();

		Element extensionElement = activityElement.element("extensionElements");

		if (extensionElement != null) {

			// provide support for deprecated form properties
			parseFormProperties(bpmnParse, expressionManager, extensionElement);

			// provide support for new form field metadata
			parseFormData(bpmnParse, expressionManager, extensionElement);
		}
	}

	protected void parseFormField(Element formField, BpmnParse bpmnParse, ExpressionManager expressionManager) {

		ExtendFormFieldHandler formFieldHandler = new ExtendFormFieldHandler();
		formFieldHandler.setFormItem(formField);
		// parse Id
		String id = formField.attribute("id");
		if (id == null || id.isEmpty()) {
			bpmnParse.addError("attribute id must be set for FormFieldGroup and must have a non-empty value",
					formField);
		} else {
			formFieldHandler.setId(id);
		}

		if (id.equals(businessKeyFieldId)) {
			formFieldHandler.setBusinessKey(true);
		}

		// parse name
		String name = formField.attribute("label");
		if (name != null) {
			Expression nameExpression = expressionManager.createExpression(name);
			formFieldHandler.setLabel(nameExpression);
		}

		// parse properties
		parseProperties(formField, formFieldHandler, bpmnParse, expressionManager);

		// parse validation
		parseValidation(formField, formFieldHandler, bpmnParse, expressionManager);

		FormTypes formTypes = getFormTypes();
		AbstractFormFieldType formType = formTypes.parseFormPropertyType(formField, bpmnParse);
		FormItem formItem = formFieldHandler.getFormItem();

		if (formItem != null && formItem.getFormItem() != null) {
			switch (formItem.getFormItem()) {
			case "UploadFile":
				formType = new FileFormFieldType();
				break;
			case "LoadImage":
			case "Link":
			case "Button":
				formType = new StringFormType();
				break;
			default:
				break;
			}
		}
		formFieldHandler.setType(formType);

		// parse default value
		String defaultValue = formField.attribute("defaultValue");
		if (defaultValue != null) {
			Expression defaultValueExpression = expressionManager.createExpression(defaultValue);
			formFieldHandler.setDefaultValueExpression(defaultValueExpression);
		}

		formFieldHandlers.add(formFieldHandler);

	}

	public String getTypeSubmit() {
		return typeSubmit;
	}

	public void setTypeSubmit(String typeSubmit) {
		this.typeSubmit = typeSubmit;
	}

}
