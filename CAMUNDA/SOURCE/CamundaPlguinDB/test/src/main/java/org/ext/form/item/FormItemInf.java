package org.ext.form.item;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.FormFieldValidationConstraint;
import org.camunda.bpm.engine.form.FormProperty;
import org.camunda.bpm.engine.form.StartFormData;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.form.engine.FormPropertyAdapter;
import org.camunda.bpm.engine.impl.form.engine.HtmlDocumentBuilder;
import org.camunda.bpm.engine.impl.form.engine.HtmlElementWriter;
import org.camunda.bpm.engine.impl.form.type.BooleanFormType;
import org.camunda.bpm.engine.impl.form.type.DateFormType;
import org.camunda.bpm.engine.impl.form.type.EnumFormType;
import org.camunda.bpm.engine.impl.form.type.StringFormType;

public abstract class FormItemInf extends FormItemHandle {

	protected String formItem;
	protected String []itemTable;

	protected FormItemInf(String formItem) {
		this.formItem = formItem;
	}

	public String getFormItem() {

		return this.formItem;
	}

	public String [] getItemTable() {
		return itemTable;
	}

	public void setItemTable(String [] itemTable) {
		this.itemTable = itemTable;
	}
	

}
