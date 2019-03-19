package org.ext.form.item;

import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.impl.form.engine.HtmlDocumentBuilder;
import org.camunda.bpm.engine.impl.form.engine.HtmlElementWriter;
import org.camunda.bpm.engine.impl.form.engine.HtmlFormEngine;
import org.camunda.bpm.engine.impl.form.type.DateFormType;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.ext.dep.FileFormFieldType;
import org.ext.form.field.ExtendFormField;

public class InputFormItem extends FormItemInf {

	public static String itemName = "Input";

	public InputFormItem() {
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

		if (isEnum(formField)) {
			// <select ...>
			renderSelectBox(formField, documentBuilder);

		} else if (isDate(formField)) {

			renderDatePicker(formField, documentBuilder);

		} else if (isFile(formField)) {
			renderUploadFileField(formField, documentBuilder);
		} else {
			// <input ...>
			renderInputField(formField, documentBuilder);

		}

		renderInvalidMessageElement(formField, documentBuilder);

		// end group
		documentBuilder.endElement();
	}
	
	protected void renderUploadFileField(FormField formField, HtmlDocumentBuilder documentBuilder) {
		HtmlElementWriter inputField = new HtmlElementWriter(INPUT_ELEMENT, true);
		addCommonFormFieldAttributes(formField, inputField);
		inputField.attribute(TYPE_ATTRIBUTE, "file");
		// <input ... />
		documentBuilder.startElement(inputField).endElement();
	}

	

	protected boolean isFile(FormField formField) {
		return FileFormFieldType.TYPE_NAME.equals(formField.getTypeName());
	}

	@Override
	public void setElement(Element formField) {

	}

}
