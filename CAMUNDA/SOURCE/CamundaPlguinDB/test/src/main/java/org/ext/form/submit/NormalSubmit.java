package org.ext.form.submit;

import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.impl.form.engine.HtmlDocumentBuilder;
import org.camunda.bpm.engine.impl.form.engine.HtmlElementWriter;
import org.ext.form.field.ExtendFormField;
import org.ext.form.item.ButtonFormItem;
import org.ext.form.item.FormItemInf;

public class NormalSubmit extends ButtonFormItem {

	public static String itemName = "normal";

	public NormalSubmit() {
		super(itemName);
	}

	@Override
	public void renderExtendFormField(ExtendFormField formField, HtmlDocumentBuilder documentBuilder,
			FormData formData) {
		
	}
}
