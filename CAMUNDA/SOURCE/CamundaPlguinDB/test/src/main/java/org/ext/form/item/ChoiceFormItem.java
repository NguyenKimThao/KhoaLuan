package org.ext.form.item;

import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.impl.form.engine.HtmlDocumentBuilder;
import org.camunda.bpm.engine.impl.util.xml.Element;

public abstract class ChoiceFormItem extends FormItemInf {
	protected ChoiceFormItem(String formItem) {
		super(formItem);
	}

	protected String formItemChoices;
	protected String formItemValue;
	protected String formItemText;

	public String getFormItemChoices() {
		return formItemChoices;
	}

	public void setFormItemChoices(String formItemChoices) {
		this.formItemChoices = formItemChoices;
	}

	public String getFormItemValue() {
		return formItemValue;
	}

	public void setFormItemValue(String formItemValue) {
		this.formItemValue = formItemValue;
	}

	public String getFormItemText() {
		return formItemText;
	}

	public void setFormItemText(String formItemText) {
		this.formItemText = formItemText;
	}

	@Override
	public void setElement(Element formField) {
		setFormItemChoices(formField.attribute("formItemChoices"));
		setFormItemText(formField.attribute("formItemText"));
		setFormItemValue(formField.attribute("formItemValue"));

	}
}
