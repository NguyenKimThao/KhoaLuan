package org.ext.form.submit;

import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.impl.form.engine.HtmlDocumentBuilder;
import org.camunda.bpm.engine.impl.form.engine.HtmlElementWriter;
import org.ext.form.field.ExtendFormField;
import org.ext.form.item.ButtonFormItem;

public class ButtonSumit extends ButtonFormItem {

	public static String itemName = "button";

	public ButtonSumit() {
		super(itemName);
	}

	public void renderExtendFormField(ExtendFormField formField, HtmlDocumentBuilder documentBuilder,
			FormData formData) {
		HtmlElementWriter divElement = new HtmlElementWriter(DIV_ELEMENT).attribute(CLASS_ATTRIBUTE, FORM_GROUP_CLASS);
		documentBuilder.startElement(divElement);

		HtmlElementWriter labelElement = new HtmlElementWriter(LABEL_ELEMENT)
				.attribute(CLASS_ATTRIBUTE, "hidden type-label").textContent("Complete");

		documentBuilder.startElement(labelElement).endElement();

		HtmlElementWriter button = new HtmlElementWriter(BUTTON_ELEMENT, false);

		button.attribute(CLASS_ATTRIBUTE, "button templatemo-blue-button pull-right")
				.attribute(TYPE_ATTRIBUTE, "submit").textContent("Complete");
		button.attribute(NG_CLICK_ATTRIBUTE, "camForm.submit()");

		documentBuilder.startElement(button);

		documentBuilder.endElement();

		documentBuilder.endElement();

		// TODO Auto-generated method stub

	}

}
