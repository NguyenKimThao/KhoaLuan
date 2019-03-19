package org.ext.form.field;

import org.camunda.bpm.engine.impl.form.FormFieldImpl;
import org.ext.form.item.FormItem;

public class ExtendFormField extends FormFieldImpl {
	private FormItem formItem;

	public FormItem getFormItem() {
		return formItem;
	}

	public void setFormItem(FormItem formItem) {
		this.formItem = formItem;
	}

}
