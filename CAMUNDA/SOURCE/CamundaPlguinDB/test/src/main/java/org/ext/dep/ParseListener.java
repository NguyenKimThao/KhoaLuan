package org.ext.dep;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParser;
import org.camunda.bpm.engine.impl.cfg.DefaultBpmnParseFactory;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.form.handler.DefaultTaskFormHandler;
import org.camunda.bpm.engine.impl.form.handler.DelegateTaskFormHandler;
import org.camunda.bpm.engine.impl.form.handler.TaskFormHandler;
import org.camunda.bpm.engine.impl.form.type.AbstractFormFieldType;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.impl.history.parser.HistoryParseListener;
import org.camunda.bpm.engine.impl.metrics.parser.MetricsBpmnParseListener;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.task.TaskDecorator;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.camunda.bpm.engine.impl.util.ReflectUtil;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.ext.dbconnector.DbConnector;
import org.ext.form.ExtendBpmnParse;
import org.ext.form.ExtendTaskFormHandler;

public class ParseListener extends AbstractBpmnParseListener implements BpmnParseListener {

	public DbConnector dbConnector;

	@Override
	public void parseUserTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {

		ProcessEngineConfigurationImpl processEngine = Context.getProcessEngineConfiguration();

		ProcessDefinitionEntity process = (ProcessDefinitionEntity) activity.getProcessDefinition();

		DefaultBpmnParseFactory bpmnParseFactory = new DefaultBpmnParseFactory();
		BpmnParser bpmnParser = new BpmnParser(processEngine.getExpressionManager(), bpmnParseFactory);
		bpmnParser.getParseListeners().addAll(processEngine.getPostParseListeners());
		bpmnParser.getParseListeners().addAll(processEngine.getPreParseListeners());
		ExtendBpmnParse bpmn = new ExtendBpmnParse(bpmnParser);

		TaskDefinition taskDefinition = bpmn.parseTaskDefinition(userTaskElement, activity.getId(), process);
		taskDefinition.addTaskListener(TaskListener.EVENTNAME_CREATE, new InputTaskListener(userTaskElement));
		taskDefinition.addTaskListener(TaskListener.EVENTNAME_COMPLETE, new OutputTaskListener(userTaskElement));
		((ProcessDefinitionEntity) activity.getProcessDefinition()).getTaskDefinitions().put(activity.getId(),
				taskDefinition);

		ActivityBehavior behavior = activity.getActivityBehavior();
		if (behavior instanceof UserTaskActivityBehavior) {
			if (!userTaskElement.elements("dataInputAssociation").isEmpty()) {
				((UserTaskActivityBehavior) behavior).getTaskDefinition().addTaskListener(TaskListener.EVENTNAME_CREATE,
						new InputTaskListener(userTaskElement));
			}
			if (!userTaskElement.elements("dataOutputAssociation").isEmpty()) {
				((UserTaskActivityBehavior) behavior).getTaskDefinition()
						.addTaskListener(TaskListener.EVENTNAME_COMPLETE, new OutputTaskListener(userTaskElement));
			}
		}
		if (processEngine.getCustomFormTypes() == null) {
			processEngine.setCustomFormTypes(new ArrayList<AbstractFormFieldType>());
		}

		List<AbstractFormFieldType> formTypes = processEngine.getCustomFormTypes();
		formTypes.add(new FileFormFieldType());
	}
}
