package org.ext.datacondition;

import org.camunda.bpm.engine.impl.util.xml.Element;
import org.ext.constantutil.ConstantUtil;

public class DataCondition {
	public static enum Action {
		INSERT, UPDATE, DELETE, SELECT, PROCEDURE, QUERY;
	};

	private Action action;
	private String dataTable;
	private String condition;
	private boolean hasExpression;
	private String query;

	protected DataCondition(Action action, String dataTable, String condition) {
		this.setAction(action);
		this.setDataTable(dataTable);
		if (condition != null) {
			this.setCondition(condition);
			if (condition.contains("$"))
				this.setHasExpression(true);
		} else
			this.setCondition("");
	}

	protected DataCondition(Action action, String query) {
		this.setAction(action);
		this.setQuery(query);
		this.setCondition("");
	}

	public String getDataTable() {
		return dataTable;
	}

	protected void setDataTable(String dataTable) {
		this.dataTable = dataTable;
	}

	public Action getAction() {
		return action;
	}

	protected void setAction(Action action) {
		this.action = action;
	}

	public String getCondition() {
		return condition;
	}

	protected void setCondition(String condition) {
		this.condition = condition;
	}

	public boolean hasExpression() {
		return hasExpression;
	}

	protected void setHasExpression(boolean hasExpression) {
		this.hasExpression = hasExpression;
	}

	public static DataCondition getDataCondition(Element dataAssociation) {
		Element extensionElements = dataAssociation.element(ConstantUtil.getConstantsMap().get("extensionElements"));
		Element dataConditionElement = extensionElements.element(ConstantUtil.getConstantsMap().get("dataCondition"));

		if (dataConditionElement != null) {
			if ("insert".equals(dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("action")))) {
				return new DataCondition(Action.INSERT,
						dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("databaseTable")),
						dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("condition")));
			}

			if ("update".equals(dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("action"))))
				return new DataCondition(Action.UPDATE,
						dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("databaseTable")),
						dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("condition")));

			if ("delete".equals(dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("action"))))
				return new DataCondition(Action.DELETE,
						dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("databaseTable")),
						dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("condition")));
			if ("select".equals(dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("action"))))
				return new DataCondition(Action.SELECT,
						dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("databaseTable")),
						dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("condition")));
			if ("procedure".equals(dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("action"))))
				return new DataCondition(Action.PROCEDURE,
						dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("nameprocedure")));
			if ("query".equals(dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("action"))))
				return new DataCondition(Action.QUERY,
						dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("execquery")));
			return new DataCondition(Action.SELECT,
					dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("databaseTable")),
					dataConditionElement.attribute(ConstantUtil.getConstantsMap().get("condition")));
		}

		return null;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

}
