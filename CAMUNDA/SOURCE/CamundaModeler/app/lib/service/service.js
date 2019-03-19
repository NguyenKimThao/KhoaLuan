// Copyright 2018, Google, LLC.
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

'use strict';

const {google} = require('googleapis');
const express = require('express');
const opn = require('opn');
const path = require('path');
const fs = require('fs');
var electron = require('electron'),
  BrowserWindow = electron.BrowserWindow;


const keyfile = path.join(__dirname, 'secret.json');
const keys = JSON.parse(fs.readFileSync(keyfile));
const scopes = ['https://www.googleapis.com/auth/drive','https://www.googleapis.com/auth/spreadsheets'];

// Create an oAuth2 client to authorize the API call
const client = new google.auth.OAuth2(
  keys.installed.client_id,
  keys.installed.client_secret,
  keys.installed.redirect_uris[1]
);
// Generate the url that will be used for authorization
var authorizeUrl = client.generateAuthUrl({
  access_type: 'offline',
  scope: scopes
});

// Open an http server to accept the oauth callback. In this
// simple example, the only request to our webserver is to
// /oauth2callback?code=<code>
const app = express();
app.get('/', function (req, res) {
  const code = req.query.code;
  client.getToken(code, function (err, tokens) {
    if (err) {
      console.error('Error getting oAuth tokens:');
      resultFunction(err,null);
      throw err;
      return;
    }
    res.send('Authentication successful! Please return to the console.');
    resultFunction(null, tokens);
	resultFunction=null;
    childWindow.close();
    server.close();
  });
});

var childWindow;
var server;
var resultFunction;
module.exports.getFileName = function (mainWindow, attr, result) {
  if(attr.token==null){
    getTokens(mainWindow, function (err, token) {
      if(err){
        result(err);
        return;
      }
      client.credentials=token;
      getFileNames(client,function(err,files){
        var data={};
        data.token=token;
        data.files=files;
        result(err,data);
      });
    });
  }else{
    console.log(attr.token);
    client.setCredentials(JSON.parse(attr.token));
    getFileNames(client,function (err, files) {
      var data={};
      data.files=files;
      result(err,data);
    });
  }
}

module.exports.writeHeader=function(token,fileId,sheetName,rows, callback){
  client.setCredentials(JSON.parse(token));
  addSheet(client,fileId,sheetName,function (err,sheetId) {
    if(err){
      getSheet(client,fileId,sheetName,function (err, id) {
        if(err){
          callback(err);
          return;
        }
        readRow(client,rows,fileId,id,sheetName,callback);
      });

    }else{
      var val=[];

      val.push(rows);
      writeRows(client,val,fileId,sheetName+'!'+'A1:'+String.fromCharCode(65+rows.length-1)+'1',sheetId,callback);
    }
  });
}

function getTokens(mainWindow, callback){
  resultFunction=callback;
  childWindow = new BrowserWindow({
    resizable: false,
    title: 'Login',
    parent: mainWindow,
    show: false,
    modal: true
  });
  childWindow.loadURL(authorizeUrl);
  childWindow.focus();
  childWindow.setMenu(null);
  childWindow.show();
  // childWindow.webContents.session.clearStorageData();
  server = app.listen(3000, function () {
     childWindow.on('close', function (event) {
       resultFunction('windowclosed',null);
       server.close();
    });

  });
}

function getFileNames(author, callback){
  const drive = google.drive({version: 'v3'});
  drive.files.list({
    auth:author,
    pageSize: 10,
	q: "mimeType='application/vnd.google-apps.spreadsheet'",
    fields: 'nextPageToken, files(id, name)'
  },function (err,{data}) {
    if (err) return console.log('The API returned an error: ' + err);
    const files = data.files;
      console.log('Files:');
      callback(null,files);
  });
}


function addSheet(auth,spreadsheetId, name, callback){
  var requests = [];
  requests.push({
    "addSheet": {
      "properties": {
        "title": name,
        "gridProperties": {
          "rowCount": 20,
          "columnCount": 12
        },
        "tabColor": {
          "red": 1.0,
          "green": 0.3,
          "blue": 0.4
        }
      }
    }
  });
  var batchUpdateRequest = {requests: requests};
  var sheets = google.sheets('v4');
  sheets.spreadsheets.batchUpdate({
    spreadsheetId: spreadsheetId,
    resource: batchUpdateRequest,
    auth:auth
  },function(err, response) {
    if(err) {
      console.log(err);
      callback(err.errors[0].message);
    } else {
      console.log('add sheet');
      console.log(response.data.replies[0].addSheet.properties.sheetId);
      callback(null,response.data.replies[0].addSheet.properties.sheetId);
    }
  });
}

function writeRows(auth,values,spreadsheetId, range,sheetId,callback){
  var body = {
    values: values
  };

  var sheets = google.sheets('v4');
  sheets.spreadsheets.values.update({
    spreadsheetId: spreadsheetId,
    range: range,
    valueInputOption: 'RAW',
    resource: body,
    auth:auth
  }, function(err, result) {
    if(err) {
      console.log('fail addrow');
      console.log(err);
      callback(err);
    } else {
      console.log('add rows');
      console.log(result);
      if(result.status!=200){
        callback('fail');
        return;
      }

      callback(null,sheetId);

    }
  });
}

function readRow(auth,values,spreadsheetId,sheetId, sheetName,callback){
  var range=sheetName+'!A1:Z1';
  var sheets = google.sheets('v4');
  sheets.spreadsheets.values.get({
    spreadsheetId: spreadsheetId,
    range: range,
    auth:auth
  }, function(err, result) {
    if(err) {
      callback(err);
    } else {
      console.log('read rows');
      console.log(result.data.values[0]);
      console.log(values);
      var rows=result.data.values[0];
      var count=0;
      var dontHaveFiel=[];
      for(var i=0;i< values.length;i++){
       for(var j=0;j<rows.length;j++) {
         if (values[i]==rows[j]) {
           count++;
           break;
         }
       }
       if(count==0){
         dontHaveFiel.push(values[i]);
       }
       count=0;
      }
      if(dontHaveFiel.length==0){
        callback(null,sheetId);
        return;
      }else{
        rows=rows.concat(dontHaveFiel);
        var val=[];
        val.push(rows);
        writeRows(auth,val,spreadsheetId,sheetName+'!'+'A1:'+String.fromCharCode(65+rows.length-1)+'1',sheetId,callback);

      }


    }
  });
}


function getSheet(auth,spreadsheetId,title, callback){
  var sheets = google.sheets('v4');
  sheets.spreadsheets.get({
    spreadsheetId: spreadsheetId,
    auth:auth
  },function(err, response) {
    if(err) {
      console.log(err);
      callback(err.errors[0].message);
    } else {
      for(var i=0;i< response.data.sheets.length;i++){
        if(response.data.sheets[i].properties.title==title){
          callback(null,response.data.sheets[i].properties.sheetId);
          return;
        }
      }
      callback(null,null);

    }
  });
}







