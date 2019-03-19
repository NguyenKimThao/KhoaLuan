package org.ext.dbconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;

public class OracleSqlConnector extends DbConnector {

	public OracleSqlConnector(String host, String port, String userName, String password, String database, Collection<CamundaFormField> formFields) {
		super(host, port, userName, password, database, formFields);
		if (null == port || "".equals(port)) {
			this.setPort("1521");
		}


		StringBuilder url = new StringBuilder();
		url.append("jdbc:oracle:thin:@")
			.append(getHost())
			.append(":")
			.append(getPort())
			.append(":")
			.append(getDatabase());
		
		this.url = url.toString();
		LOGGER.info("Database type: OracleSQL");
	}

	@Override
	public Connection connect() throws Exception {
		LOGGER.info("Establishing a connection...");
		Class.forName(DbConnector.dbType.oracleSQL.getDbDriver()).newInstance();
		setConnection(DriverManager.getConnection(this.url.toString(), getUserName(), getPassword()));
		LOGGER.info("Successfully connected!");
		return getConnection();
	}

	@Override
	public void closeConnection() throws Exception {
		LOGGER.info("Closing a connection...");
		getConnection().close();
	}


	

}
