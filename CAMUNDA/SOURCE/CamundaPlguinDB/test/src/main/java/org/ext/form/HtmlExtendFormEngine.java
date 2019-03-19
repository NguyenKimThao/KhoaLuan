package org.ext.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.FormProperty;
import org.camunda.bpm.engine.form.StartFormData;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.form.engine.FormPropertyAdapter;
import org.camunda.bpm.engine.impl.form.engine.HtmlDocumentBuilder;
import org.camunda.bpm.engine.impl.form.engine.HtmlElementWriter;
import org.camunda.bpm.engine.impl.form.engine.HtmlFormEngine;
import org.camunda.bpm.engine.impl.form.type.EnumFormType;
import org.ext.form.field.ExtendFormField;
import org.ext.form.item.ButtonFormItem;
import org.ext.form.submit.ButtonSumit;

public class HtmlExtendFormEngine extends HtmlFormEngine {

	protected static final String CAM_CHOICES_ATTRIBUTE = "cam-choices";
	protected static final String CAM_VALUE_ATTRIBUTE = "cam-value";
	protected static final String CAM_TEXT_ATTRIBUTE = "cam-text";

	public String getName() {
		return null;
	}

	public Object renderStartForm(StartFormData startForm) {
		return renderFormData(startForm);
	}

	public Object renderTaskForm(TaskFormData taskForm) {
		return renderFormData(taskForm);
	}

	protected String renderFormData(FormData formData) {

		if (formData == null || (formData.getFormFields() == null || formData.getFormFields().isEmpty())
				&& (formData.getFormProperties() == null || formData.getFormProperties().isEmpty())) {
			return null;

		} else {
			HtmlElementWriter formElement = new HtmlElementWriter(FORM_ELEMENT)
					.attribute(NAME_ATTRIBUTE, GENERATED_FORM_NAME).attribute(ROLE_ATTRIBUTE, FORM_ROLE);

			HtmlDocumentBuilder documentBuilder = new HtmlDocumentBuilder(formElement);

			// render fields
			for (FormField formField : formData.getFormFields()) {
				if (formField instanceof ExtendFormField) {
					ExtendFormField ex = (ExtendFormField) formField;
					if (ex.getFormItem() != null)
						ex.getFormItem().renderExtendFormField(ex, documentBuilder, formData);
					else
						renderFormField(formField, documentBuilder);
				} else
					renderFormField(formField, documentBuilder);
			}
			if (formData instanceof ExtendTaskFormData) {
				ExtendTaskFormData extendTaskForm = (ExtendTaskFormData) formData;
				renderSumit(documentBuilder, extendTaskForm);
			}

			// render deprecated form properties
			for (FormProperty formProperty : formData.getFormProperties()) {
				renderFormField(new FormPropertyAdapter(formProperty), documentBuilder);
			}

			// end document element
			documentBuilder.endElement();

			return documentBuilder.getHtmlString();

		}
	}

	protected void renderSumit(HtmlDocumentBuilder documentBuilder, ExtendTaskFormData extendTaskForm) {
		try {
			String typeSubmit=extendTaskForm.getTypeSubmit();
			if(typeSubmit==null||typeSubmit.isEmpty())
				typeSubmit=ButtonSumit.itemName;
			ButtonFormItem buttonSubmit = ManagerTypeSubmit.getFormItem(typeSubmit);
			if (buttonSubmit != null) {
				buttonSubmit.renderExtendFormField(null, documentBuilder, extendTaskForm);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

//	protected void renderExtendFormField(ExtendFormField formField, HtmlDocumentBuilder documentBuilder,
//			FormData formData) {
//
//		// start group
//		HtmlElementWriter divElement = new HtmlElementWriter(DIV_ELEMENT).attribute(CLASS_ATTRIBUTE, FORM_GROUP_CLASS);
//
//		documentBuilder.startElement(divElement);
//
//		String formFieldId = formField.getId();
//		String formFieldLabel = formField.getLabel();
//
//		// write label
//		if (formFieldLabel != null && !formFieldLabel.isEmpty()) {
//
//			HtmlElementWriter labelElement = new HtmlElementWriter(LABEL_ELEMENT).attribute(FOR_ATTRIBUTE, formFieldId)
//					.textContent(formFieldLabel);
//
//			// <label for="...">...</label>
//			documentBuilder.startElement(labelElement).endElement();
//		}
//		formField.getFormItem().renderExtendFormField(formField, documentBuilder, formData);
//		renderInvalidMessageElement(formField, documentBuilder);
//
//		// end group
//		documentBuilder.endElement();
//	}

//	protected void renderSelectJsonBox(ExtendFormField formField, HtmlDocumentBuilder documentBuilder,
//			FormData formData) {
//		HtmlElementWriter selectBox = new HtmlElementWriter(SELECT_ELEMENT, false);
//		System.out.println("vao day la renderSelectJsonBox:" + formField.getId());
//		addCommonFormFieldAttributes(formField, selectBox);
//		if (!formField.isBusinessKey()) {
//			selectBox.attribute(CAM_CHOICES_ATTRIBUTE, formField.getFormItemChoices())
//					.attribute(CAM_TEXT_ATTRIBUTE, formField.getFormItemText())
//					.attribute(CAM_VALUE_ATTRIBUTE, formField.getFormItemChoices());
//		}
//
//		// <select ...>
//		documentBuilder.startElement(selectBox);
//
//		// </select>
//		documentBuilder.endElement();
//	}
//
//	protected void renderSelectJsonOptions(FormField formField, HtmlDocumentBuilder documentBuilder,
//			FormData formData) {
//		Object val = formField.getValue().getValue();
//		if (val == null)
//			return;
//		if (val instanceof ArrayList || val.getClass().isArray()) {
//			if (val instanceof Map) {
//				ArrayList<Map<String, String>> value = (ArrayList<Map<String, String>>) val;
//				for (int i = 0; i < value.size(); i++) {
//					Map<String, String> map = value.get(i);
//					StringBuilder htmlValue = new StringBuilder("{");
//					for (Entry<String, String> vk : map.entrySet()) {
//						htmlValue.append(vk.getKey() + " : " + vk.getValue() + ",");
//					}
//					htmlValue.substring(0, htmlValue.length() - 1);
//					htmlValue.setCharAt(htmlValue.length() - 1, '}');
//
//					HtmlElementWriter option = new HtmlElementWriter(OPTION_ELEMENT, false)
//							.attribute(VALUE_ATTRIBUTE, String.valueOf(i)).textContent(htmlValue.toString());
//					documentBuilder.startElement(option).endElement();
//				}
//			} else {
//				List<Object> value = Arrays.asList(val);
//				for (int i = 0; i < value.size(); i++) {
//					Object obj = value.get(i);
//					String htmlValue = (obj == null) ? "" : obj.toString();
//					HtmlElementWriter option = new HtmlElementWriter(OPTION_ELEMENT, false)
//							.attribute(VALUE_ATTRIBUTE, htmlValue).textContent(htmlValue);
//					documentBuilder.startElement(option).endElement();
//				}
//
//			}
//		} else if (val instanceof Map) {
//			Map<String, String> values = (Map<String, String>) formField.getValue().getValue();
//
//			for (Entry<String, String> value : values.entrySet()) {
//				// <option>
//				HtmlElementWriter option = new HtmlElementWriter(OPTION_ELEMENT, false)
//						.attribute(VALUE_ATTRIBUTE, value.getKey()).textContent(value.getValue());
//
//				documentBuilder.startElement(option).endElement();
//			}
//		}
//
//	}

}
