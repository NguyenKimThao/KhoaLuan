package org.ext.ssconnector;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.type.ValueType;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SpreadsheetConnector {
	private final Logger LOGGER = Logger.getLogger(SpreadsheetConnector.class.getName());
	protected Sheets service; 
	protected String clientId;
	protected String clientSecrect;
	protected GoogleCredential credential;
	protected String spreadsheetId;
	protected Integer sheetId;
	protected List<Object> header;
	protected String sheetTitle;
	protected final String secret = "secret.json";
	
	private HttpRequestInitializer setHttpTimeOut(final HttpRequestInitializer requestInitializer) {
		return new HttpRequestInitializer() {
			
			@Override
			public void initialize(HttpRequest request) throws IOException {
				requestInitializer.initialize(request);
				request.setConnectTimeout(3*60000);
				request.setReadTimeout(3*60000);
				
			}
		};
	}
	
	public SpreadsheetConnector(String accessToken,
								String refreshToken,
								String tokenType,
								String spreadsheetId,
								Integer sheetId) throws IOException, ParseException {
		LOGGER.info("Connecting to Google...");
		InputStreamReader in = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(secret), "UTF-8");
		JSONParser parser = new JSONParser();
		JSONObject objectInstalled = (JSONObject) parser.parse(in);
		JSONObject object = (JSONObject) objectInstalled.get("installed");
		clientId = (String) object.get("client_id");
		clientSecrect = (String) object.get("client_secret");
		HttpTransport httpTransport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();
		credential = new GoogleCredential.Builder()
						.setJsonFactory(jsonFactory)
						.setTransport(httpTransport)
						.setClientSecrets(clientId, clientSecrect)
						.build()
						.setAccessToken(accessToken)
						.setRefreshToken(refreshToken);
		
		service = new Sheets.Builder(httpTransport, jsonFactory, setHttpTimeOut(credential))
				.build();
		
		this.spreadsheetId = spreadsheetId;
		this.sheetId = sheetId;
	}
	
	public boolean checkIfSheetExists() {
		LOGGER.info("Check if spreadsheet exists ...");
		try {
			service.spreadsheets().get(spreadsheetId);
			return true;
		} catch (Exception e) {
			LOGGER.severe("Cannot find correct file!");
			LOGGER.log(Level.SEVERE, e.getMessage(), e.getCause());
		}
		
		return false;
	}

	public boolean checkingSheetFile(Collection<CamundaFormField> formFields) throws IOException {
		LOGGER.info("Checking format of the sheet file");
		Spreadsheet spreadsheet = service.spreadsheets().get(spreadsheetId).execute();
		Optional<Sheet> sheet = spreadsheet.getSheets().stream().filter((s) -> {
			return sheetId.equals(s.getProperties().getSheetId());
		}).findFirst();
		if (sheet.isPresent()) {
			sheetTitle = sheet.get().getProperties().getTitle();
			ValueRange firstRow = service.spreadsheets().values().get(spreadsheetId, sheetTitle+"!1:1").execute();
			if (!firstRow.getValues().isEmpty()) {
				for (CamundaFormField field : formFields) {
					if (!firstRow.getValues().get(0).contains(field.getCamundaId()))
					{
						return false;
					}
				}
				header = firstRow.getValues().get(0);
				return true;
			}
		}
		return false;
	}

	public void batchUpdate(VariableMap variableMap) {
		LOGGER.info("Start updating the sheet");
		String valueInputOption = "RAW";
		String insertDataOption = "INSERT_ROWS";
		List<Object> values = new ArrayList<>();
		ValueRange body = new ValueRange();
		
		for (Object cell : header) {
			TypedValue typedValue = variableMap.asVariableContext().resolve((String) cell);
			if (variableMap.containsKey(cell)) {
					if(typedValue.getValue() != null) {
						/*
						if (typedValue.getType().isPrimitiveValueType()) {
							values.add(typedValue.getValue());
						}else {
							if (typedValue.getType().equals(ValueType.DATE)) {
								
							}*/
						
						switch (typedValue.getType().getName()) {
						case "date":
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
							Date date = (Date) typedValue.getValue();
							values.add(dateFormat.format(date));
							break;
						case "string":
							values.add(typedValue.getValue().toString());
							break;

						default:
							values.add(typedValue.getValue());
							break;
						}
					}
				}
				else {
					values.add("");
				}
			}
		
		List<List<Object>> valuesArray = Arrays.asList(values);
		body.setValues(valuesArray);
		
		try {
			service.spreadsheets().values().append(spreadsheetId, sheetTitle, body)
			        .setValueInputOption(valueInputOption)
			        .setInsertDataOption(insertDataOption)
			        .execute();
			LOGGER.info("Row appended.");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e.getCause());
		}
		
		
	}

	
}
