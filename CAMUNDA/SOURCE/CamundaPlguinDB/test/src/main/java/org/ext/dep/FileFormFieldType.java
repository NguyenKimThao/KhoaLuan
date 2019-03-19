package org.ext.dep;

import java.io.File;

import org.camunda.bpm.engine.impl.form.type.AbstractFormFieldType;
import org.camunda.bpm.engine.impl.form.type.SimpleFormFieldType;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.FileValue;
import org.camunda.bpm.engine.variable.value.TypedValue;

public class FileFormFieldType extends AbstractFormFieldType {

	public final static String TYPE_NAME = "uploadfile";

	@Override
	public String getName() {
		return TYPE_NAME;
	}

	public TypedValue convertValue(TypedValue propertyValue) {
		if (propertyValue instanceof FileValue) {
			return propertyValue;
		} else {
			Object value = propertyValue.getValue();
			if (value == null) {
				return Variables.fileValue("null").create();
			} else {
				return Variables.fileValue((File) value);
			}
		}
	}

	@Override
	public Object convertFormValueToModelValue(Object propertyValue) {
		return null;
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		return null;
	}

	@Override
	public TypedValue convertToFormValue(TypedValue propertyValue) {
		 return convertValue(propertyValue);
	}

	@Override
	public TypedValue convertToModelValue(TypedValue propertyValue) {
		 return convertValue(propertyValue);
	}
}


