package org.ext.form;

import java.util.HashMap;

import org.camunda.bpm.engine.impl.util.xml.Element;
import org.ext.form.item.ButtonFormItem;
import org.ext.form.item.FormItem;
import org.ext.form.item.InputFormItem;
import org.ext.form.item.ParagraphFormItem;
import org.ext.form.item.SelectFormItem;
import org.ext.form.item.TableFormItem;
import org.ext.form.submit.AutoSubmit;
import org.ext.form.submit.ButtonSumit;
import org.ext.form.submit.NormalSubmit;

public class ManagerTypeSubmit {
	private static HashMap<String, Class<?>> map = getInstance();

	private static HashMap<String, Class<?>> getInstance() {
		HashMap<String, Class<?>> mapRes = new HashMap<>();
		mapRes.put(NormalSubmit.itemName, NormalSubmit.class);
		mapRes.put(AutoSubmit.itemName, AutoSubmit.class);
		mapRes.put(ButtonSumit.itemName, ButtonSumit.class);

		return mapRes;
	}

	public static void registerFormItem(String item, Class<?> clasItem) {
		if (item == null)
			return;
		map.put(item, clasItem);
	}

	public static ButtonFormItem getFormItem(String itemname) throws InstantiationException, IllegalAccessException {
		if (itemname == null || itemname.isEmpty())
			return null;
		if (!map.containsKey(itemname))
			return null;
		return (ButtonFormItem) map.get(itemname).newInstance();
	}

}
