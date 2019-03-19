package org.ext.form.item;

import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.impl.form.engine.HtmlDocumentBuilder;
import org.camunda.bpm.engine.impl.form.engine.HtmlElementWriter;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.camunda.bpm.engine.variable.impl.value.PrimitiveTypeValueImpl.StringValueImpl;
import org.camunda.bpm.engine.variable.value.StringValue;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.ext.dep.NameFileFieldType;
import org.ext.form.field.ExtendFormField;

public class LoadImageFormItem extends FormItemInf {

	public static String itemName = "LoadImage";

	public LoadImageFormItem() {
		super(itemName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void renderExtendFormField(ExtendFormField formField, HtmlDocumentBuilder documentBuilder,
			FormData formData) {
		// start group
		HtmlElementWriter divElement = new HtmlElementWriter(DIV_ELEMENT).attribute(CLASS_ATTRIBUTE, FORM_GROUP_CLASS);

		documentBuilder.startElement(divElement);

		String formFieldId = formField.getId();
		String formFieldLabel = formField.getLabel();

		TypedValue value = formField.getValue();

		if (value != null) {
			HtmlElementWriter image = new HtmlElementWriter("img", true);
			image.attribute("src","/engine-rest/"+ value.getValue().toString());
			image.attribute("width", "100%");
			documentBuilder.startElement(image);

			// </select>
			documentBuilder.endElement();

		} else {
			HtmlElementWriter selectBox = new HtmlElementWriter(INPUT_ELEMENT, true);
			selectBox.attribute(CLASS_ATTRIBUTE, "hidden");
			selectBox.attribute(CAM_VARIABLE_NAME_ATTRIBUTE, formFieldId);
			selectBox.attribute(CAM_VARIABLE_TYPE_ATTRIBUTE, "File");
			selectBox.attribute("cam-variable-type-file", "LoadImage");
			// <select ...>
			documentBuilder.startElement(selectBox);
			// </select>
			documentBuilder.endElement();
			HtmlElementWriter image = new HtmlElementWriter("img", true);
			image.attribute("src", "{{ camForm.variableManager.variable('" + formField.getId() + "').contentUrl }}");
			image.attribute("width", "100%");
			documentBuilder.startElement(image);
			// </select>
			documentBuilder.endElement();

		}

		// <select ...>

		// write label
//		if (formFieldLabel != null && !formFieldLabel.isEmpty()) {
//
//			HtmlElementWriter labelElement = new HtmlElementWriter(LABEL_ELEMENT).attribute(FOR_ATTRIBUTE, formFieldId)
//					.textContent(formFieldLabel);
//
//			// <label for="...">...</label>
//			documentBuilder.startElement(labelElement).endElement();
//		}

		// end group
		documentBuilder.endElement();
	}

	@Override
	public void renderInTableField(ExtendFormField formField, HtmlDocumentBuilder documentBuilder, HtmlElementWriter tr,
			FormData formData) {
		// TODO Auto-generated method stub
		TypedValue value = formField.getValue();
		HtmlElementWriter image = new HtmlElementWriter("img", true);
		documentBuilder.startElement(image);
		image.attribute("src","/engine-rest/"+ value.getValue().toString());
		image.attribute("width", "100%");

		// </select>
		documentBuilder.endElement();

	}

	@Override
	public void setElement(Element formField) {
		// TODO Auto-generated method stub

	}

}