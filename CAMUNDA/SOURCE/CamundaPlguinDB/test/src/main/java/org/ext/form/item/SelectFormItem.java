package org.ext.form.item;

import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.impl.form.engine.HtmlDocumentBuilder;
import org.camunda.bpm.engine.impl.form.engine.HtmlElementWriter;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.ext.form.field.ExtendFormField;

public class SelectFormItem extends ChoiceFormItem {
	public static String itemName = "Select";

	public SelectFormItem() {
		super(itemName);
	}

	@Override
	public void renderExtendFormField(ExtendFormField formField, HtmlDocumentBuilder documentBuilder,
			FormData formData) {
		// start group
		HtmlElementWriter divElement = new HtmlElementWriter(DIV_ELEMENT).attribute(CLASS_ATTRIBUTE, FORM_GROUP_CLASS);

		documentBuilder.startElement(divElement);

		String formFieldId = formField.getId();
		String formFieldLabel = formField.getLabel();

		// write label
		if (formFieldLabel != null && !formFieldLabel.isEmpty()) {

			HtmlElementWriter labelElement = new HtmlElementWriter(LABEL_ELEMENT).attribute(FOR_ATTRIBUTE, formFieldId)
					.textContent(formFieldLabel);

			// <label for="...">...</label>
			documentBuilder.startElement(labelElement).endElement();
		}

		HtmlElementWriter selectBox = new HtmlElementWriter(SELECT_ELEMENT, false);
		addCommonFormFieldAttributes(formField, selectBox);
		if (!formField.isBusinessKey() && formItemChoices != null) {
			selectBox.attribute(CAM_CHOICES_ATTRIBUTE, formItemChoices).attribute(CAM_TEXT_ATTRIBUTE, formItemText)
					.attribute(CAM_VALUE_ATTRIBUTE, formItemValue);
		}

		// <select ...>
		documentBuilder.startElement(selectBox);

		// </select>
		documentBuilder.endElement();

		renderInvalidMessageElement(formField, documentBuilder);

		// end group
		documentBuilder.endElement();
	}


}
