package org.ext.form.item;

import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.impl.form.engine.HtmlDocumentBuilder;
import org.camunda.bpm.engine.impl.form.engine.HtmlElementWriter;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.ext.form.field.ExtendFormField;

public class LinkFormItem extends FormItemInf {

	public static String itemName = "Link";

	public LinkFormItem() {
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

		HtmlElementWriter link = new HtmlElementWriter("a").attribute("cam-file-download", formFieldId)
				.textContent(formFieldLabel);
		if (formField.getDefaultValue() != null || formField.getDefaultValue().toString().isEmpty()) {
			link.attribute("src", formField.getDefaultValue().toString());
		}
		documentBuilder.startElement(link);
		// </select>
		documentBuilder.endElement();

		// end group
		documentBuilder.endElement();
	}

	@Override
	public void setElement(Element formField) {
		// TODO Auto-generated method stub

	}

}