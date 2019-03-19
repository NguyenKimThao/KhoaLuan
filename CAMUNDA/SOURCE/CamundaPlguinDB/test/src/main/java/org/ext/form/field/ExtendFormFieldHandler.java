package org.ext.form.field;

import java.util.List;

import org.camunda.bpm.engine.delegate.VariableScope;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.FormFieldValidationConstraint;
import org.camunda.bpm.engine.impl.el.StartProcessVariableScope;
import org.camunda.bpm.engine.impl.form.FormFieldImpl;
import org.camunda.bpm.engine.impl.form.handler.FormFieldHandler;
import org.camunda.bpm.engine.impl.form.handler.FormFieldValidationConstraintHandler;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.ext.form.ManagerFormItem;
import org.ext.form.item.FormItem;

public class ExtendFormFieldHandler extends FormFieldHandler {
	private FormItem formItem;

	@Override
	public FormField createFormField(ExecutionEntity executionEntity) {
		ExtendFormField formField = new ExtendFormField();

		formField.setFormItem(formItem);
		// set id
		formField.setId(id);

		// set label (evaluate expression)
		VariableScope variableScope = executionEntity != null ? executionEntity
				: StartProcessVariableScope.getSharedInstance();
		if (label != null) {
			Object labelValueObject = label.getValue(variableScope);
			if (labelValueObject != null) {
				formField.setLabel(labelValueObject.toString());
			}
		}

		formField.setBusinessKey(businessKey);

		// set type
		formField.setType(type);

		// set default value (evaluate expression)
		Object defaultValue = null;
		if (defaultValueExpression != null) {
			defaultValue = defaultValueExpression.getValue(variableScope);

			if (defaultValue != null) {
				formField.setDefaultValue(type.convertFormValueToModelValue(defaultValue));
			} else {
				formField.setDefaultValue(null);
			}
		}

		// value
		TypedValue value = variableScope.getVariableTyped(id);
		if (value != null) {
			formField.setValue(type.convertToFormValue(value));
		} else {
			// first, need to convert to model value since the default value may be a String
			// Constant specified in the model xml.
			TypedValue typedDefaultValue = type.convertToModelValue(Variables.untypedValue(defaultValue));
			// now convert to form value
			formField.setValue(type.convertToFormValue(typedDefaultValue));
		}

		// properties
		formField.setProperties(properties);

		// validation
		List<FormFieldValidationConstraint> validationConstraints = formField.getValidationConstraints();
		for (FormFieldValidationConstraintHandler validationHandler : validationHandlers) {
			// do not add custom validators
			if (!"validator".equals(validationHandler.getName())) {
				validationConstraints.add(validationHandler.createValidationConstraint(executionEntity));
			}
		}

		return formField;
	}

	public FormItem getFormItem() {
		return formItem;
	}

	public void setFormItem(Element formField) {
		try {
			this.formItem = ManagerFormItem.getFormItem(formField);
		} catch (InstantiationException e) {
			System.out.println("error: setFormItem: "+e.getMessage());
		} catch (IllegalAccessException e) {
			System.out.println("error: setFormItem: " + e.getMessage());
		}

	}

}
