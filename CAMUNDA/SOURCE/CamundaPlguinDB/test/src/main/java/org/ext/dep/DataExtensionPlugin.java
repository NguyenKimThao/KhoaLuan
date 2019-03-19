package org.ext.dep;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.form.engine.FormEngine;
import org.camunda.bpm.engine.impl.form.type.AbstractFormFieldType;
import org.ext.constantutil.ConstantUtil;
import org.ext.form.HtmlExtendFormEngine;
import org.ext.constantutil.ConstantParseUtil;

public class DataExtensionPlugin implements ProcessEnginePlugin {
	private final Logger LOGGER = Logger.getLogger(ConstantUtil.class.getName());

	@Override
	public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
		
		List<BpmnParseListener> postParseListeners = processEngineConfiguration.getCustomPostBPMNParseListeners();
		try {
			ConstantUtil.init();
			ConstantParseUtil.init();
		} catch (Exception e) {
			LOGGER.severe("Cannot init constants file!");
			return;
		}
		try {
			LOGGER.info("Initiate Spring Boot Application...");
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			LOGGER.severe("Cannot Initiate Spring Boot Application!");
			return;
		}
		if (postParseListeners == null) {
			postParseListeners = new ArrayList<BpmnParseListener>();
			processEngineConfiguration.setCustomPostBPMNParseListeners(postParseListeners);
		}
		postParseListeners.add(new ParseListener());
		List<AbstractFormFieldType> formTypes = processEngineConfiguration.getCustomFormTypes();
		if (formTypes == null)
			formTypes = new ArrayList<AbstractFormFieldType>();
		formTypes.add(new SelectFormFieldType());
		processEngineConfiguration.setCustomFormTypes(formTypes);
		List<FormEngine> customForm = processEngineConfiguration.getCustomFormEngines();
		if (customForm == null)
			customForm = new ArrayList<FormEngine>();
		customForm.add(new HtmlExtendFormEngine());
		processEngineConfiguration.setCustomFormEngines(customForm);

		LOGGER.info("Successfully initiate plugin!");
	}

	@Override
	public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {

	}

	@Override
	public void postProcessEngineBuild(ProcessEngine processEngine) {

	}

}
