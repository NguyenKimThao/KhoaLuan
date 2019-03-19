package org.ext.form.submit;

import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.impl.form.engine.HtmlDocumentBuilder;
import org.camunda.bpm.engine.impl.form.engine.HtmlElementWriter;
import org.ext.form.field.ExtendFormField;
import org.ext.form.item.ButtonFormItem;

public class AutoSubmit extends ButtonFormItem {

	public static String itemName = "auto";

	public AutoSubmit() {
		super(itemName);
	}

	@Override
	public void renderExtendFormField(ExtendFormField formField, HtmlDocumentBuilder documentBuilder,
			FormData formData) {
		documentBuilder.startElement(new HtmlElementWriter(DIV_ELEMENT).attribute(STYLE_ATTRIBUTE, "display: none;")
				.attribute(CLASS_ATTRIBUTE, "autosubmit")).endElement();


	}
}
