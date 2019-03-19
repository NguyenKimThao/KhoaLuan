package org.ext.form.item;

import org.camunda.bpm.engine.form.FormData;
import org.camunda.bpm.engine.impl.form.engine.HtmlDocumentBuilder;
import org.camunda.bpm.engine.impl.form.engine.HtmlElementWriter;
import org.ext.form.ManagerFormItem;
import org.ext.form.field.ExtendFormField;

public class TableFormItem extends ChoiceFormItem {

	public static String itemName = "Table";

	public TableFormItem() {
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

		HtmlElementWriter divpanel = new HtmlElementWriter(DIV_ELEMENT)
				.attribute(CLASS_ATTRIBUTE,
						"panel panel-default templatemo-content-widget white-bg no-padding templatemo-overflow-hidden")
				.attribute(STYLE_ATTRIBUTE, "width:100%");
		documentBuilder.startElement(divpanel);
		documentBuilder.startElement(new HtmlElementWriter(DIV_ELEMENT).attribute(CLASS_ATTRIBUTE, "table-responsive"));

		HtmlElementWriter table = new HtmlElementWriter(TABLE_ELEMENT).attribute(CLASS_ATTRIBUTE,
				"table table-striped table-bordered");

		if (formItemChoices != null && !formItemChoices.isEmpty())
			table.attribute(CAM_CHOICES_ATTRIBUTE, formItemChoices);

		documentBuilder.startElement(table);

		documentBuilder.startElement(new HtmlElementWriter(THEAD_ELEMENT));
		documentBuilder.startElement(new HtmlElementWriter(TR_ELEMENT));

		if (formItemText != null && !formItemText.isEmpty()) {
			String[] th = formItemText.split(";");
			for (int i = 0; i < th.length; i++) {
				documentBuilder.startElement(new HtmlElementWriter(TH_ELEMENT).textContent(th[i]));
				documentBuilder.endElement();
			}
		}

		documentBuilder.endElement();
		documentBuilder.endElement();

		documentBuilder.startElement(new HtmlElementWriter(TBODY_ELEMENT));
		documentBuilder.startElement(new HtmlElementWriter(TR_ELEMENT));

		if (formItemValue != null && !formItemValue.isEmpty()) {
			String[] th = formItemValue.split(";");
			for (int i = 0; i < th.length; i++) {
				String val = th[i];
				if (val.startsWith("{")) {
					if (val.endsWith("}")) {
						documentBuilder.startElement(new HtmlElementWriter(TD_ELEMENT));
						documentBuilder.startElement(new HtmlElementWriter(PARAGRAPH_ELEMENT).textContent(val))
								.endElement();
						documentBuilder.endElement();
					} else {
						documentBuilder.startElement(new HtmlElementWriter(TD_ELEMENT).textContent(""));
						documentBuilder.endElement();
					}

				} else if (val.startsWith("[")) {
					if (val.endsWith("]")) {
						val = val.substring(1, val.length() - 1);
						String[] keys = val.split(",");
						if (keys == null || keys.length == 0) {
							documentBuilder.startElement(new HtmlElementWriter(TD_ELEMENT).textContent(""));
							documentBuilder.endElement();
							continue;
						}
						HtmlElementWriter tr = new HtmlElementWriter(TD_ELEMENT);
						documentBuilder.startElement(tr);
						FormItem item = ManagerFormItem.getFormItemByTable(keys);
						if (item != null)
							item.renderInTableField(formField, documentBuilder, tr, formData);
						documentBuilder.endElement();

					} else {
						documentBuilder.startElement(new HtmlElementWriter(TD_ELEMENT).textContent(""));
						documentBuilder.endElement();
					}
				}

			}
		}

		documentBuilder.endElement();

		documentBuilder.endElement();

		documentBuilder.endElement();
		documentBuilder.endElement();
		documentBuilder.endElement();

		// end group
		documentBuilder.endElement();

	}

}
