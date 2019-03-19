package org.ext.dep;

import java.util.Map;

import org.camunda.bpm.engine.impl.form.type.AbstractFormFieldType;
import org.camunda.bpm.engine.variable.value.TypedValue;

public class SelectFormFieldType extends AbstractFormFieldType {

	public final static String TYPE_NAME = "select";

	@Override
	public String getName() {
		return TYPE_NAME;
	}

	@Override
	public TypedValue convertToFormValue(TypedValue propertyValue) {
		// TODO Auto-generated method stub
		return propertyValue;
	}

	@Override
	public TypedValue convertToModelValue(TypedValue propertyValue) {
		// TODO Auto-generated method stub
		return propertyValue;
	}

	@Override
	public Object convertFormValueToModelValue(Object propertyValue) {
		// TODO Auto-generated method stub
		return propertyValue;
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		// TODO Auto-generated method stub
		return modelValue.toString();
	}

}
