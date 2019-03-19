package org.ext.form;

import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParse;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParser;
import org.camunda.bpm.engine.impl.form.handler.DefaultTaskFormHandler;
import org.camunda.bpm.engine.impl.form.handler.DelegateTaskFormHandler;
import org.camunda.bpm.engine.impl.form.handler.TaskFormHandler;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.camunda.bpm.engine.impl.util.ReflectUtil;
import org.camunda.bpm.engine.impl.util.xml.Element;

public class ExtendBpmnParse  extends BpmnParse{

	public ExtendBpmnParse(BpmnParser parser) {
		super(parser);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public TaskDefinition parseTaskDefinition(Element taskElement, String taskDefinitionKey, ProcessDefinitionEntity processDefinition) {
		ExtendTaskFormHandler taskFormHandler = new ExtendTaskFormHandler();
	  
	    taskFormHandler.parseConfiguration(taskElement, processDefinition.getDeploymentId(), processDefinition, this);
	    deployment =new DeploymentEntity();
	    deployment.setId(processDefinition.getDeploymentId());
	    TaskDefinition taskDefinition = new TaskDefinition(new DelegateTaskFormHandler(taskFormHandler, deployment));

	    taskDefinition.setKey(taskDefinitionKey);
	    processDefinition.getTaskDefinitions().put(taskDefinitionKey, taskDefinition);

	    String formKeyAttribute = taskElement.attributeNS(CAMUNDA_BPMN_EXTENSIONS_NS, "formKey");

	    if (formKeyAttribute != null) {
	      taskDefinition.setFormKey(expressionManager.createExpression(formKeyAttribute));
	    }

	    String name = taskElement.attribute("name");
	    if (name != null) {
	      taskDefinition.setNameExpression(expressionManager.createExpression(name));
	    }

	    String descriptionStr = parseDocumentation(taskElement);
	    if (descriptionStr != null) {
	      taskDefinition.setDescriptionExpression(expressionManager.createExpression(descriptionStr));
	    }

	    parseHumanPerformer(taskElement, taskDefinition);
	    parsePotentialOwner(taskElement, taskDefinition);

	    // Activiti custom extension
	    parseUserTaskCustomExtensions(taskElement, taskDefinition);

	    return taskDefinition;
	  }
}
