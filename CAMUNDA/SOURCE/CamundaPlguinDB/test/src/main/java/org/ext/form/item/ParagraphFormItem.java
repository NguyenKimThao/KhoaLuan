package org.ext.form.item;

import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.impl.form.engine.HtmlDocumentBuilder;
import org.camunda.bpm.engine.impl.form.engine.HtmlElementWriter;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.ext.form.field.ExtendFormField;

public class ParagraphFormItem extends ChoiceFormItem {

	public static String itemName = "Paragraph";

	public ParagraphFormItem() {
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

		HtmlElementWriter selectBox = new HtmlElementWriter(PARAGRAPH_ELEMENT, false);
		addCommonFormFieldAttributes(formField, selectBox);
		if (!formField.isBusinessKey() && formItemChoices != null) {
			selectBox.attribute(CAM_CHOICES_ATTRIBUTE, formItemChoices).attribute(CAM_TEXT_ATTRIBUTE, formItemText)
					.attribute(CAM_VALUE_ATTRIBUTE, formItemValue);
		}
		if (formField.getValue() != null && formField.getValue().getValue() != null) {
			selectBox.attribute(VALUE_ATTRIBUTE, formField.getValue().getValue().toString());
			selectBox.textContent(formField.getValue().getValue().toString());
		}

		else if (formField.getDefaultValue() != null) {
			selectBox.attribute(VALUE_ATTRIBUTE, formField.getDefaultValue().toString());
			selectBox.textContent(formField.getDefaultValue().toString());
		}

		// <select ...>
		documentBuilder.startElement(selectBox);

		// </select>
		documentBuilder.endElement();

		renderInvalidMessageElement(formField, documentBuilder);

		// end group
		documentBuilder.endElement();
	}

	@Override
	public void renderInTableField(ExtendFormField formField, HtmlDocumentBuilder documentBuilder,HtmlElementWriter tr, FormData formData) {
		// TODO Auto-generated method stub
		if (itemTable == null || itemTable.length == 1) {
			documentBuilder.startElement(new HtmlElementWriter(PARAGRAPH_ELEMENT));
			documentBuilder.endElement();
		}
		if (itemTable.length == 2) {
			documentBuilder.startElement(new HtmlElementWriter(PARAGRAPH_ELEMENT).textContent("{" + itemTable[1] + "}"))
					.endElement();
		}
		if (itemTable.length == 3) {
			HtmlElementWriter paragraph = new HtmlElementWriter(PARAGRAPH_ELEMENT)
					.attribute(CAM_CHOICES_ATTRIBUTE, itemTable[2])
					.textContent("{" + itemTable[2] + "[" + itemTable[1] + "]}");
			documentBuilder.startElement(paragraph);
			documentBuilder.endElement();
		}

	}

}
