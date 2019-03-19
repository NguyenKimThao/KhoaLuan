package org.ext.form.item;

import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.impl.form.engine.HtmlDocumentBuilder;
import org.camunda.bpm.engine.impl.form.engine.HtmlElementWriter;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.ext.form.field.ExtendFormField;

public class UploadFileItem extends FormItemInf {

	public static String itemName = "UploadFile";
	
	public UploadFileItem() {
		super(itemName);
		// TODO Auto-generated constructor stub
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
					.textContent(formFieldLabel);

			// <label for="...">...</label>
			documentBuilder.startElement(labelElement).endElement();
		}

		HtmlElementWriter selectBox = new HtmlElementWriter(INPUT_ELEMENT, true);
		addCommonFormFieldAttributes(formField, selectBox);
		selectBox.attribute("type", "file");
		selectBox.attribute(CAM_VARIABLE_TYPE_ATTRIBUTE, "File");
		selectBox.attribute("cam-variable-type-file", "UploadFile");

		// <select ...>
		documentBuilder.startElement(selectBox);

		// </select>
		documentBuilder.endElement();

//		renderInvalidMessageElement(formField, documentBuilder);

		// end group
		documentBuilder.endElement();
		
	}

	@Override
	public void setElement(Element formField) {
		// TODO Auto-generated method stub
		
	}

}
