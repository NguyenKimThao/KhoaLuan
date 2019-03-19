package org.ext.form.item;

import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.FormFieldValidationConstraint;
import org.camunda.bpm.engine.impl.form.engine.HtmlDocumentBuilder;
import org.camunda.bpm.engine.impl.form.engine.HtmlElementWriter;
import org.camunda.bpm.engine.impl.form.engine.HtmlFormEngine;
import org.camunda.bpm.engine.impl.form.type.StringFormType;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.ext.form.field.ExtendFormField;

public interface FormItem {

	public String getFormItem();

	public void renderExtendFormField(ExtendFormField formField, HtmlDocumentBuilder documentBuilder,
			FormData formData);

	public void setElement(Element formField);

	public String[] getItemTable();

	public void setItemTable(String[] itemTable);

	public void renderInTableField(ExtendFormField formField, HtmlDocumentBuilder documentBuilder, HtmlElementWriter tr,
			FormData formData);
}
