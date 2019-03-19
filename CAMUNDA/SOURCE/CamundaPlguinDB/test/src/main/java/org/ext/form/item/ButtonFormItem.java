package org.ext.form.item;

import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.impl.form.engine.HtmlDocumentBuilder;
import org.camunda.bpm.engine.impl.form.engine.HtmlElementWriter;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.ext.form.field.ExtendFormField;

public class ButtonFormItem extends ChoiceFormItem {

	public static String itemName = "Button";

	public ButtonFormItem() {
		super(itemName);
	}

	public ButtonFormItem(String item) {
		super(item);
	}

	@Override
	public void renderExtendFormField(ExtendFormField formField, HtmlDocumentBuilder documentBuilder,
			FormData formData) {
		HtmlElementWriter divElement = new HtmlElementWriter(DIV_ELEMENT).attribute(CLASS_ATTRIBUTE, FORM_GROUP_CLASS);
		documentBuilder.startElement(divElement);

		String formFieldId = formField.getId();
		String formFieldLabel = formField.getLabel();

		// write label
		if (formFieldLabel != null && !formFieldLabel.isEmpty()) {

			HtmlElementWriter labelElement = new HtmlElementWriter(LABEL_ELEMENT).attribute(FOR_ATTRIBUTE, formFieldId)
					.attribute(CLASS_ATTRIBUTE, "hidden type-label").textContent(formFieldLabel);

			// <label for="...">...</label>
			documentBuilder.startElement(labelElement).endElement();
		}
		HtmlElementWriter button = new HtmlElementWriter(BUTTON_ELEMENT, false);
		if (!formField.isBusinessKey()) {
			button.attribute(CAM_VARIABLE_TYPE_ATTRIBUTE, formField.getTypeName())
					.attribute(CAM_VARIABLE_NAME_ATTRIBUTE, formFieldId).textContent(formField.getLabel());
			if (formField.getDefaultValue() != null)
				button.attribute(VALUE_ATTRIBUTE, formField.getDefaultValue().toString());

			button.attribute(CLASS_ATTRIBUTE, "button templatemo-blue-button").attribute(TYPE_ATTRIBUTE, "submit")
					.attribute(NAME_ATTRIBUTE, formFieldId);

		}
		documentBuilder.startElement(button);

		// </select>
		documentBuilder.endElement();

		documentBuilder.endElement();

		// TODO Auto-generated method stub

	}

	@Override
	public void renderInTableField(ExtendFormField formField, HtmlDocumentBuilder documentBuilder, HtmlElementWriter tr,
			FormData formData) {
		// TODO Auto-generated method stub
		HtmlElementWriter button = new HtmlElementWriter(BUTTON_ELEMENT)
				.attribute(CLASS_ATTRIBUTE, "templatemo-blue-button").attribute(TYPE_ATTRIBUTE, "button")
				.textContent(itemTable[1]).attribute(VALUE_ATTRIBUTE, itemTable[3]);
		button.attribute("ng-click", "actionForm(camForm,item,'" + itemTable[2] + "','" + itemTable[3] + "')");
		documentBuilder.startElement(button);
		documentBuilder.endElement();

	}

	@Override
	public void setElement(Element formField) {
		// TODO Auto-generated method stub

	}

}
