package org.ext.dbconnector;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.PreparedStatement;
import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.value.FileValue;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.ext.datacondition.DataCondition;
import org.ext.datastorefield.DatastoreField;

public abstract class DbConnector {
	private String host;
	private String port;
	private String userName;
	private String password;
	private String database;
	protected String url;
	private Connection connection;

	protected final Logger LOGGER = Logger.getLogger(this.getClass().getName());
	private ProcessEngineServices processEngineService;
	private Collection<CamundaFormField> formFields;

	public static enum dbType {
		mySQL("com.mysql.jdbc.Driver"), oracleSQL("oracle.jdbc.driver.OracleDriver"),
		sqlServer("com.microsoft.sqlserver.jdbc.SQLServerDriver");

		private String dbDriver;

		dbType(String dbDriver) {
			this.dbDriver = dbDriver;
		}

		public String getDbDriver() {
			return this.dbDriver;
		}
	}

	public static enum dbAction {
		INSERT, UPDATE;
	}

	public abstract Connection connect() throws Exception;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public ProcessEngineServices getProcessEngineService() {
		return processEngineService;
	}

	public void setProcessEngineService(ProcessEngineServices processEngineService) {
		this.processEngineService = processEngineService;
	}

	public Connection getConnection() {
		return connection;
	}

	protected void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Collection<CamundaFormField> getFormFields() {
		return formFields;
	}

	public void setFormFields(Collection<CamundaFormField> formFields) {
		this.formFields = formFields;
	}

	public DbConnector(String host, String port, String userName, String password, String database,
			Collection<CamundaFormField> formFields) {
		this.setHost(host);
		this.setPort(port);
		this.setUserName(userName);
		this.setPassword(password);
		this.setDatabase(database);
	}

	public void closeConnection() throws Exception {
		LOGGER.info("Closing connection...");
		this.getConnection().close();
	}

	public PreparedStatement getOutputStatement(Element dataAssociationElement, VariableMap variableMap,
			List<DatastoreField> fieldList) throws SQLException, Exception {
		DataCondition dataCondition = DataCondition.getDataCondition(dataAssociationElement);
		switch (dataCondition.getAction()) {
		case INSERT:
			return createInsertStatement(dataCondition, variableMap, fieldList);
		case UPDATE:
			return createUpdateStatement(dataCondition, variableMap, fieldList);
		case DELETE:
			return createDeleteStatement(dataCondition, variableMap, new ArrayList<>());
		default:
			break;
		}

		return null;
	}

	private PreparedStatement createSelectStatement(DataCondition dataCondition, VariableMap variableMap,
			List<DatastoreField> fieldList) throws Exception {
		int paramIndex = 0;
		Map<String, Object> normalizedVariableMap = normalizeVariables(variableMap, fieldList, formFields);
		StringBuilder builder = new StringBuilder();
		List<Object> expressionValueList = new ArrayList<>();
		builder.append("SELECT ");
		if (fieldList.size() == 0)
			builder.append("*");
		for (int i = 0; i < fieldList.size(); i++) {
			builder.append(fieldList.get(i).getColumn());
			if (i != fieldList.size() - 1) {
				builder.append(",");
			}
		}
		builder.append(" FROM ");
		builder.append(dataCondition.getDataTable() + " ");

		if (!dataCondition.getCondition().isEmpty()) {
			builder.append("WHERE ");
			if (!dataCondition.hasExpression()) {
				builder.append(dataCondition.getCondition());
			} else {
				builder.append(
						parsingCondition(dataCondition.getCondition(), normalizedVariableMap, expressionValueList));
			}
		}

		PreparedStatement sql = this.getConnection().prepareStatement(builder.toString(),
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		for (int i = 0; i < expressionValueList.size(); i++) {
			sql.setObject(paramIndex + i + 1, expressionValueList.get(i));
		}

		return sql;
	}

	private PreparedStatement createProcedureStatement(DataCondition dataCondition, VariableMap variableMap,
			List<DatastoreField> fieldList) throws Exception {
		int paramIndex = 0;
		Map<String, Object> normalizedVariableMap = normalizeVariables(variableMap, fieldList, formFields);
		StringBuilder builder = new StringBuilder();
		builder.append("{ call ");
		builder.append(dataCondition.getQuery());
		builder.append("(");

		List<Object> expressionValueList = new ArrayList<>();
		for (int i = 0; i < fieldList.size(); i++) {
			builder.append("?");
			if (i != fieldList.size() - 1) {
				builder.append(",");
			}
		}
		builder.append(")");
		builder.append("}");
		CallableStatement sql = this.getConnection().prepareCall(builder.toString());
		for (paramIndex = 0; paramIndex < fieldList.size(); paramIndex++) {
			sql.setObject(paramIndex + 1, normalizedVariableMap.get(fieldList.get(paramIndex).getVariableId()));
		}


		return sql;
	}

	private PreparedStatement createQueryStatement(String sqlQuery, DataCondition dataCondition,
			VariableMap variableMap, List<DatastoreField> fieldList) throws Exception {
		int paramIndex = 0;
		Map<String, Object> normalizedVariableMap = normalizeVariables(variableMap, fieldList, formFields);
		Pattern pattern = Pattern.compile("\\$\\{([^}\\s]+)\\}");
		Matcher m = pattern.matcher(sqlQuery);
		StringBuffer sb = new StringBuffer();
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		while (m.find()) {
			String repString = m.group(1);
			if (repString != null) {
				if (normalizedVariableMap.get(repString) instanceof Date) {
					m.appendReplacement(sb, "'" + df.format(normalizedVariableMap) + "'");
				} else {
					m.appendReplacement(sb, "'" + normalizedVariableMap.get(repString) + "'");
				}
			}
		}
		m.appendTail(sb);

		sb.append(dataCondition.getQuery());
		PreparedStatement sql = this.getConnection().prepareStatement(sb.toString().trim(),
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		return sql;
	}

	protected PreparedStatement createDeleteStatement(DataCondition dataCondition, VariableMap variableMap,
			List<DatastoreField> fieldList) throws Exception {
		int paramIndex;
		Map<String, Object> normalizedVariableMap = normalizeVariables(variableMap, fieldList, formFields);
		StringBuilder builder = new StringBuilder();
		List<Object> expressionValueList = new ArrayList<>();
		builder.append("DELETE FROM ");
		builder.append(dataCondition.getDataTable() + " ");

		if (!dataCondition.getCondition().isEmpty()) {
			builder.append("WHERE ");
			if (!dataCondition.hasExpression()) {
				builder.append(dataCondition.getCondition());
			} else {
				builder.append(
						parsingCondition(dataCondition.getCondition(), normalizedVariableMap, expressionValueList));
			}
		}

		PreparedStatement sql = this.getConnection().prepareStatement(builder.toString());
		for (paramIndex = 0; paramIndex < fieldList.size(); paramIndex++) {
			sql.setObject(paramIndex + 1, normalizedVariableMap.get(fieldList.get(paramIndex).getVariableId()));
		}
		for (int i = 0; i < expressionValueList.size(); i++) {
			sql.setObject(paramIndex + i + 1, expressionValueList.get(i));
		}
		return sql;
	}

	protected PreparedStatement createUpdateStatement(DataCondition dataCondition, VariableMap variableMap,
			List<DatastoreField> fieldList) throws Exception {
		int paramIndex;
		Map<String, Object> normalizedVariableMap = normalizeVariables(variableMap, fieldList, formFields);
		StringBuilder builder = new StringBuilder();
		List<Object> expressionValueList = new ArrayList<>();
		builder.append("UPDATE ");
		builder.append(dataCondition.getDataTable() + " ");
		builder.append("SET ");
		for (int i = 0; i < fieldList.size(); i++) {
			builder.append(fieldList.get(i).getColumn() + "= ?");
			if (i != fieldList.size() - 1) {
				builder.append(",");
			}
		}
		builder.append(" ");
		if (!dataCondition.getCondition().isEmpty()) {
			builder.append("WHERE ");
			if (!dataCondition.hasExpression()) {
				builder.append(dataCondition.getCondition());
			} else {
				builder.append(
						parsingCondition(dataCondition.getCondition(), normalizedVariableMap, expressionValueList));
			}
		}
		PreparedStatement sql = this.getConnection().prepareStatement(builder.toString());
		for (paramIndex = 0; paramIndex < fieldList.size(); paramIndex++) {
			sql.setObject(paramIndex + 1, normalizedVariableMap.get(fieldList.get(paramIndex).getVariableId()));
		}

		for (int i = 0; i < expressionValueList.size(); i++) {
			sql.setObject(paramIndex + i + 1, expressionValueList.get(i));
		}
		return sql;
	}

	@SuppressWarnings("rawtypes")
	private String parsingCondition(String condition, Map variableMap, List<Object> expressionValueList) {
		Pattern pattern = Pattern.compile("\\$\\{([^}\\s]+)\\}");
		Matcher m = pattern.matcher(condition);
		StringBuffer sb = new StringBuffer();

		while (m.find()) {
			String repString = m.group(1);
			if (repString != null) {
				expressionValueList.add(variableMap.get(repString));
				m.appendReplacement(sb, "?");
			}
		}
		m.appendTail(sb);
		return sb.toString();
	}

	protected PreparedStatement createInsertStatement(DataCondition dataCondition, VariableMap variableMap,
			List<DatastoreField> fieldList) throws SQLException, Exception {
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO ");
		builder.append(dataCondition.getDataTable() + "(");
		for (int i = 0; i < fieldList.size(); i++) {
			if (i != fieldList.size() - 1) {
				builder.append(fieldList.get(i).getColumn() + ",");
			} else {
				builder.append(fieldList.get(i).getColumn());
			}
		}
		builder.append(") ");
		builder.append("VALUES(");
		for (int i = 0; i < fieldList.size(); i++) {
			if (i != fieldList.size() - 1) {
				builder.append("?,");
			} else {
				builder.append("?");
			}

		}
		builder.append(")");

		PreparedStatement sql = this.getConnection().prepareStatement(builder.toString());
		Map<String, Object> normalizedVariableMap = normalizeVariables(variableMap, fieldList, formFields);
		for (int i = 0; i < fieldList.size(); i++) {
			sql.setObject(i + 1, normalizedVariableMap.get(fieldList.get(i).getVariableId()));
		}
		return sql;

	}

	private Map<String, Object> normalizeVariables(VariableMap variableMap, List<DatastoreField> fieldList,
			Collection<CamundaFormField> formFields) throws Exception {
		Map<String, Object> result = new HashMap<>();
		// for(int i=0; i<fieldList.size(); i++) {
		for (Entry<String, Object> entry : variableMap.entrySet()) {
			// DatastoreField field = fieldList.get(i);
			// String type = field.getVariableType();
			String type = variableMap.getValueTyped(entry.getKey()).getType().getName();
			switch (type) {
			case "string":
			case "long":
			case "boolean":
			case "enum":
			case "file":
				// case "date":
				result.put(entry.getKey(), entry.getValue());
				break;
			case "date":
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				dateFormat.applyLocalizedPattern(dateFormat.toLocalizedPattern());

				if ((variableMap.get(entry.getKey()) instanceof Date)) {
					result.put(entry.getKey(), variableMap.get(entry.getKey()));
					break;
				}
				Date date = dateFormat.parse((String) variableMap.get(entry.getKey()));
				result.put(entry.getKey(), date);
				break;
			case "":
				break;
			default:
//				Exception e = new Exception("Plguin does not support other than Camunda primitive type!");
//				throw e;
			}
		}
		return result;
	}

	public void executeOutputStatement(PreparedStatement statement) throws SQLException, Exception {
		this.getConnection().setAutoCommit(false);
		Savepoint savePoint = this.getConnection().setSavepoint();
		try {
			LOGGER.info("Query: " + statement.toString());
			if (statement.executeUpdate() != 0)
				LOGGER.info("Successfully executed!");
			else
				LOGGER.info("Failed to execute!");

		} catch (Exception e) {
			this.getConnection().rollback(savePoint);
			throw e;
		} finally {
			statement.close();
			// this.getConnection().releaseSavepoint(savePoint);
			this.getConnection().setAutoCommit(true);
		}
	}

	public PreparedStatement getInputStatement(Element dataAssociationElement, VariableMap variableMap,
			List<DatastoreField> fieldList) throws Exception {
		DataCondition dataCondition = DataCondition.getDataCondition(dataAssociationElement);

		switch (dataCondition.getAction()) {
		case SELECT:
			return createSelectStatement(dataCondition, variableMap, fieldList);
		case PROCEDURE:
			return createProcedureStatement(dataCondition, variableMap, fieldList);
		default:
			break;
		}
		return createSelectStatement(dataCondition, variableMap, fieldList);
	}

	public ResultSet executeInputStatement(PreparedStatement statement) throws SQLException {
		this.getConnection().setAutoCommit(false);
		Savepoint savePoint = this.getConnection().setSavepoint();
		try {
			LOGGER.info("Query: " + statement.toString());
			return statement.executeQuery();
		} catch (Exception e) {
			this.getConnection().rollback(savePoint);
			throw e;
		}

	}

	public ResultSet execute(PreparedStatement statement) throws SQLException {
		this.getConnection().setAutoCommit(false);
		Savepoint savePoint = this.getConnection().setSavepoint();
		try {
			LOGGER.info("Query: " + statement.toString());
			boolean res = statement.execute();
			if (res == true) {
				ResultSet rs = statement.getResultSet();
				return rs;
			}
			return null;
		} catch (Exception e) {
			this.getConnection().rollback(savePoint);
			throw e;
		} finally {
			statement.closeOnCompletion();
			if (!(this instanceof MsSqlConnector)) {
				this.getConnection().releaseSavepoint(savePoint);
			}
			this.getConnection().setAutoCommit(true);
		}

	}

	public ResultSet executeInputStatement(java.sql.Statement statement, String query) throws SQLException {

		this.getConnection().setAutoCommit(false);
		Savepoint savePoint = this.getConnection().setSavepoint();
		try {

			Scanner scanner = new Scanner(new ByteArrayInputStream(query.getBytes(StandardCharsets.UTF_8)));
			String delimiter = ";";
			scanner.useDelimiter(delimiter);
			while (scanner.hasNext()) {

				// Get statement
				String rawStatement = scanner.next() + delimiter;
				if (rawStatement != null) {
					rawStatement = rawStatement.trim();
					if (!rawStatement.isEmpty() && !rawStatement.equals(";")) {
						statement.execute(rawStatement);
					}
				}
			}
			scanner.close();
			return statement.getResultSet();
		} catch (Exception e) {
			this.getConnection().rollback(savePoint);
			throw e;
		} finally {
			statement.closeOnCompletion();
			if (!(this instanceof MsSqlConnector)) {
				this.getConnection().releaseSavepoint(savePoint);
			}
			this.getConnection().setAutoCommit(true);
		}

	}

}
