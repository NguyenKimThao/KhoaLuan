package org.ext.form;

import org.camunda.bpm.engine.impl.form.TaskFormDataImpl;

public class ExtendTaskFormData extends TaskFormDataImpl {

	protected String typeSubmit;

	public String getTypeSubmit() {
		return typeSubmit;
	}

	public void setTypeSubmit(String typeSubmit) {
		this.typeSubmit = typeSubmit;
	}
}
