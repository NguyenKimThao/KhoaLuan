function ConnectDatabase(type, server, username, password, database, closeAfterConnect, result) {
  switch (type){
    case 'mysql': ConnectMySql( server, username, password, database, result); break;
    case 'mssql': ConnectMsSql(server,username,password,database,closeAfterConnect,result);break;
    case 'oracle': ConnectOracle( server, username, password, database, result); break;
  }
}

function ExecuteQuery(information,query, result) {
  var resultValue={};
  ConnectDatabase(information.databaseType,information.server,information.username,information.password,information.database, false,function (err,con) {
    if(err){
      resultValue.err=err;
	  console.log(err);
      result(resultValue);
      return;
    }
    switch (information.databaseType){
      case 'mysql': ExecuteMySqlQuery(con, query,result);break;
      case 'mssql': ExecuteSqlServerQuery(con,query,result);break;
      case 'oracle': ExecuteOracle(con,query,result);break;
    }

  });

}
function ConnectMySql(server, username, password, database, result){
  var mysql=require('mysql');
  if(database=="") {
    var con = mysql.createConnection({
      host: server,
      user: username,
      password: password,
      port: '3306'
    });
  }else {
    var con = mysql.createConnection({
      host: server,
      user: username,
      password: password,
      database: database,
      port: '3306'
    });
  }

  con.connect(function(err) {
    result(err,con);
  });
}

function ConnectOracle(server, username, password, database, result){
  var oracledb = require('oracledb');
  oracledb.getConnection(
    {
      user          : username,
      password      : password,
      connectString : server+"/"+database
    },
    function(err, connection) {
      result(err,connection);
    });
}

function ExecuteOracle(con,query,result){
  con.execute(query,
    function(err, data) {
      var resultValue={};
      if(err){
        resultValue.err=err;
        result(resultValue);
        return;
      }
      resultValue.data=data;
      return result(resultValue);
    });
}

function ConnectMsSql(server, username, password, database , closeAfterConnected, result) {
  var mssql=require('mssql');
  var config;

  if(database!='') {
    config = {
      user: username,
      password: password,
      server: server,
      database: database
    };
  }else {
    config = {
      user: username,
      password: password,
      server: server
    };
  }
  mssql.connect(config,function (err) {
	if(err){
		mssql.close();
	}
    result(err,mssql);
    if(closeAfterConnected){
      mssql.close();
    }
  });

}

function ExecuteMySqlQuery(con,query,result){
  var resultValue={};
    con.query(query, function (err, data) {
      if (err) {
        resultValue.err=err;
        result(resultValue);
        return;
      }
      resultValue.data=data;
      return result(resultValue);
    });
}

function ExecuteSqlServerQuery(con,query,result){
  var request = new con.Request();
  var resultValue={};
  // query to the database and get the records
  request.query(query, function (err, data) {
    if (err) {
      resultValue.err=err;
      result(resultValue);
      return;
    }
    resultValue.data=data;
    con.close();
    return result(resultValue);
  });
}
module.exports.Connect=ConnectDatabase;
module.exports.ExecuteQuery=ExecuteQuery;

