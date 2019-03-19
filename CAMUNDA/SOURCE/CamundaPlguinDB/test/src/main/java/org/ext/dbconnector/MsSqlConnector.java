package org.ext.dbconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;

public class MsSqlConnector extends DbConnector {

	public MsSqlConnector(String host, String port, String userName, String password, String database, Collection<CamundaFormField> formFields) {
		super(host, port, userName, password, database, formFields);
		if (null == port || "".equals(port)) {
			this.setPort("1433");
		}


		StringBuilder url = new StringBuilder();
		url.append("jdbc:sqlserver://")
			.append(getHost())
			.append(":")
			.append(getPort())
			.append(";databaseName=")
			.append(getDatabase());
		
		this.url = url.toString();
		LOGGER.info("Database type: SQLServer");
	}

	@Override
	public Connection connect() throws Exception {
		LOGGER.info("Establishing a connection...");
		Class.forName(DbConnector.dbType.sqlServer.getDbDriver()).newInstance();
		setConnection(DriverManager.getConnection(this.url.toString(), getUserName(), getPassword()));
		LOGGER.info("Successfully connected!");
		return getConnection();
	}

}
