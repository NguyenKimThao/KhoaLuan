package org.ext.form;

import java.util.HashMap;

import javax.print.attribute.standard.MediaSize.Other;

import org.camunda.bpm.engine.impl.util.xml.Element;
import org.ext.form.item.ButtonFormItem;
import org.ext.form.item.FormItem;
import org.ext.form.item.InputFormItem;
import org.ext.form.item.LinkFormItem;
import org.ext.form.item.LoadImageFormItem;
import org.ext.form.item.ParagraphFormItem;
import org.ext.form.item.SelectFormItem;
import org.ext.form.item.TableFormItem;
import org.ext.form.item.UploadFileItem;

public class ManagerFormItem {
	private static HashMap<String, Class<?>> map = getInstance();

	private static HashMap<String, Class<?>> getInstance() {
		HashMap<String, Class<?>> mapRes = new HashMap<>();
		mapRes.put(InputFormItem.itemName, InputFormItem.class);
		mapRes.put(SelectFormItem.itemName, SelectFormItem.class);
		mapRes.put(ParagraphFormItem.itemName, ParagraphFormItem.class);
		mapRes.put(ButtonFormItem.itemName, ButtonFormItem.class);
		mapRes.put(TableFormItem.itemName, TableFormItem.class);
		mapRes.put(LinkFormItem.itemName, LinkFormItem.class);
		mapRes.put(UploadFileItem.itemName, UploadFileItem.class);
		mapRes.put(LoadImageFormItem.itemName, LoadImageFormItem.class);
		mapRes.put("Label", ParagraphFormItem.class);
		return mapRes;
	}

	public static void registerFormItem(String item, Class<?> clasItem) {
		if (item == null || item.isEmpty())
			return;
		map.put(item, clasItem);
	}

	public static FormItem getFormItem(String itemname) throws InstantiationException, IllegalAccessException {
		if (itemname == null || itemname.isEmpty())
			return null;
		if (!map.containsKey(itemname))
			return null;
		return (FormItem) map.get(itemname).newInstance();
	}

	public static FormItem getFormItem(Element formField) throws InstantiationException, IllegalAccessException {
		String itemname = formField.attribute("formItem");
		FormItem formItem = getFormItem(itemname);
		if (formItem == null)
			return null;
		formItem.setElement(formField);
		return formItem;
	}

	public static FormItem getFormItemByTable(String[] formField) {
		try {
			FormItem formItem = getFormItem(formField[0]);
			if (formItem == null)
				return null;
			formItem.setItemTable(formField);
			return formItem;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}

}
