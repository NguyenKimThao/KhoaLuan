'use strict';

var electron = require('electron'),
  app = electron.app,
  BrowserWindow = electron.BrowserWindow;

var path = require('path');


var forEach = require('lodash/collection/forEach'),
  got = require('got'),
  fs = require('fs'),
  FormData = require('form-data');
 
/**
 * automatically report crash reports
 *
 * @see http://electron.atom.io/docs/v0.34.0/api/crash-reporter/
 */
// TODO(nikku): do we want to do this?
// require('crash-reporter').start();

var Platform = require('./platform'),
  Config = require('./config'),
  ClientConfig = require('./client-config'),
  FileSystem = require('./file-system'),
  Workspace = require('./workspace'),
  Dialog = require('./dialog'),
  Menu = require('./menu'),
  Cli = require('./cli'),
  Plugins = require('./plugins'),
  deploy = require('./createDeployer')({ got, fs, FormData });

var browserOpen = require('./util/browser-open'),
  renderer = require('./util/renderer');

var config = Config.load(path.join(app.getPath('userData'), 'config.json'));

Platform.create(process.platform, app, config);

// variable for developing (reloading and devtools toggling)
app.developmentMode = false;

app.version = require('../../package').version;
app.name = 'Camunda Modeler';

// this is shared variable between main and renderer processes
global.metaData = {
  version: app.version,
  name: app.name
};

// get directory of executable
var appPath = path.dirname(app.getPath('exe'));

var plugins = app.plugins = new Plugins({
  paths: [
    app.getPath('userData'),
    appPath
  ]
});

// set global modeler directory
global.modelerDirectory = appPath;

// bootstrap the application's menus
//
// TODO(nikku): remove app.menu binding when development
// mode bootstrap issue is fixed in electron-connect
app.menu = new Menu(
  process.platform,
  plugins.getPlugins()
    .map(p => {
      return {
        menu: p.menu,
        name: p.name,
        error: p.error
      };
    })
);

// bootstrap workspace behavior
new Workspace(config);

// bootstrap client config behavior
var clientConfig = new ClientConfig(app);

// bootstrap dialog
var dialog = new Dialog({
  dialog: electron.dialog,
  config: config,
  userDesktopPath: app.getPath('userDesktop')
});

// bootstrap filesystem
var fileSystem = new FileSystem({
  dialog: dialog
});


// make app a singleton
if (config.get('single-instance', true)) {

  var shouldQuit = app.makeSingleInstance(function (commandLine, workingDirectory) {

    app.emit('app:parse-cmd', commandLine, workingDirectory);

    // focus existing running instance window
    if (app.mainWindow) {
      if (app.mainWindow.isMinimized()) {
        app.mainWindow.restore();
      }

      app.mainWindow.focus();
    }
  });

  if (shouldQuit) {
    app.quit();
  }
}

// client life-cycle //////////////////

renderer.on('dialog:unrecognized-file', function (file, done) {
  dialog.showDialog('unrecognizedFile', { name: file.name });

  done(null);
});



renderer.on('dialog:reimport-warning', function (done) {

  dialog.showDialog('reimportWarning', done);
});

renderer.on('dialog:convert-namespace', function (type, done) {
  dialog.showDialog('namespace', { type: type }, done);
});

renderer.on('dialog:import-error', function (filename, errorDetails, done) {

  dialog.showDialog('importError', { name: filename, errorDetails: errorDetails }, function (err, answer) {
    if (answer === 'ask-forum') {
      browserOpen('https://forum.camunda.org/c/modeler');
    }

    // the answer is irrelevant for the client
    done(null);
  });
});

renderer.on('dialog:close-tab', function (diagramFile, done) {
  dialog.showDialog('close', { name: diagramFile.name }, done);
});

renderer.on('dialog:saving-denied', function (done) {
  dialog.showDialog('savingDenied', done);
});

renderer.on('dialog:content-changed', function (done) {
  dialog.showDialog('contentChanged', done);
});

renderer.on('deploy', function (data, done) {
  var workspaceConfig = config.get('workspace', { endpoints: [] });

  var endpointUrl = (workspaceConfig.endpoints || [])[0];

  if (!endpointUrl) {

    let err = new Error('no deploy endpoint configured');

    console.error('failed to deploy', err);
    return done(err.message);
  }

  deploy(endpointUrl, data, function (err, result) {

    if (err) {
      console.error('failed to deploy', err);

      return done(err.message);
    }

    done(null, result);
  });

});


function saveCallback(saveAction, diagramFile, done) {
  saveAction.apply(fileSystem, [diagramFile, (err, updatedDiagram) => {
    if (err) {
      return done(err);
    }

    if (updatedDiagram && updatedDiagram !== 'cancel') {
      app.emit('app:add-recent-file', updatedDiagram.path);
    }

    done(null, updatedDiagram);
  }]);
}

renderer.on('client-config:get', function () {

  var args = Array.prototype.slice.call(arguments);

  var done = args[args.length - 1];

  try {
    clientConfig.get.apply(clientConfig, arguments);
  } catch (e) {
    if (typeof done === 'function') {
      done(e);
    }
  }
});

renderer.on('file:save-as', function (diagramFile, done) {
  saveCallback(fileSystem.saveAs, diagramFile, done);
});

renderer.on('file:save', function (diagramFile, done) {
  saveCallback(fileSystem.save, diagramFile, done);
});

renderer.on('file:read', function (diagramFile, done) {
  done(null, fileSystem.readFile(diagramFile.path));
});

renderer.on('file:read-stats', function (diagramFile, done) {
  done(null, fileSystem.readFileStats(diagramFile));
});

renderer.on('file:open', function (filePath, done) {
  fileSystem.open(filePath, function (err, diagramFiles) {
    if (err) {
      return done(err);
    }

    if (diagramFiles && diagramFiles !== 'cancel') {
      diagramFiles.forEach(file => {
        app.emit('app:add-recent-file', file.path);
      });
    }

    done(null, diagramFiles);
  });
});


// open file handling //////////////////

// list of files that should be opened by the editor
app.openFiles = [];

app.on('app:parse-cmd', function (argv, cwd) {
  console.log('app:parse-cmd', argv.join(' '), cwd);

  var files = Cli.extractFiles(argv, cwd);

  files.forEach(function (file) {
    app.emit('app:open-file', file);
  });
});

app.on('app:open-file', function (filePath) {
  var file;

  console.log('app:open-file', filePath);

  if (!app.clientReady) {
    // defer file open
    return app.openFiles.push(filePath);
  }

  try {
    file = fileSystem.readFile(filePath);
  } catch (e) {
    return dialog.showDialog('unrecognizedFile', { name: path.basename(filePath) });
  }

  // open file immediately
  renderer.send('client:open-files', [file]);
});

app.on('app:client-ready', function () {
  var files = [];

  console.log('app:client-ready');

  forEach(app.openFiles, function (filePath) {
    try {
      files.push(fileSystem.readFile(filePath));
    } catch (e) {
      dialog.showDialog('unrecognizedFile', { name: path.basename(filePath) });
    }
  });

  renderer.send('client:open-files', files);
});

renderer.on('client:ready', function () {
  app.clientReady = true;

  app.emit('app:client-ready');
});


/**
 * Create the main window that represents the editor.
 *
 * @return {BrowserWindow}
 */
app.createEditorWindow = function () {

  var mainWindow = app.mainWindow = new BrowserWindow({
    resizable: true,
    title: 'Camunda Modeler'
  });
  mainWindow.maximize();

  mainWindow.loadURL('file://' + path.resolve(__dirname + '/../../public/index.html'));

  mainWindow.webContents.on('new-window', function (event, url) {
    event.preventDefault();

    browserOpen(url);
  });

  // handling case when user clicks on window close button
  mainWindow.on('close', function (e) {
    console.log('Initiating close of main window');

    if (app.quitAllowed) {
      // dereferencing main window and resetting client state
      app.mainWindow = null;
      app.clientReady = false;

      return console.log('Main window closed');
    }

    // preventing window from closing until client allows to do so
    e.preventDefault();

    console.log('Asking client to allow application quit');

    app.emit('app:quit-denied');

    renderer.send('menu:action', 'quit');
  });

  mainWindow.on('focus', function () {
    console.log('Window focused');
    renderer.send('client:window-focused');
  });

  app.emit('app:window-created', mainWindow);

  // only set by client, when it is ok to exit
  app.quitAllowed = false;
};


/**
 * Application entry point
 * Emitted when Electron has finished initialization.
 */
app.on('ready', function () {

  // quit command from menu/shortcut
  app.on('app:quit', function () {
    console.log('Initiating termination of the application');

    renderer.send('menu:action', 'quit');
  });

  // client quit verification event
  renderer.on('app:quit-allowed', function () {
    console.log('Quit allowed');

    app.quitAllowed = true;

    app.mainWindow.close();
  });

  app.createEditorWindow();

  app.emit('app:parse-cmd', process.argv, process.cwd());
});


var ipcMain = require('electron').ipcMain;
var database = require('./database/Connection');
ipcMain.on('client:ConnectDatabase', function (event, props) {
  database.Connect(props.databaseType, props.server, props.username, props.password, props.database, true, function (err) {
    event.returnValue = err;
  });
});


ipcMain.on('client:ExecuteQuery', function (event, obj, arg) {
  // database.ExecuteQuery(obj,arg,function (data) {
  //   event.returnValue=data;
  // });
  //var abc=require('./service/list');
  //abc(app.mainWindow);
});
ipcMain.on('client:GetDatabaseName', function (event, boInfo) {
  switch (boInfo.databaseType) {
    case 'mysql':
      database.ExecuteQuery(boInfo, 'show databases', function (data) {
        if (data.err) {  
          event.returnValue = data;
          return;
        }
        var val = [];
        for (var i = 0; i < data.data.length; i++) {
          val.push(data.data[i].Database);
        }
        data.data = val;
        event.returnValue = data;
      });
      break;
    case 'mssql':
      database.ExecuteQuery(boInfo, 'select name from sys.sysdatabases', function (data) {
        if (data.err) {
          event.returnValue = data;
          return;
        }
        var val = [];
        console.log(data);
        for (var i = 0; i < data.data.recordset.length; i++) {
          val.push(data.data.recordset[i].name);
        }
        data.data = val;
        event.returnValue = data;
      });
      break;
  }
});

ipcMain.on('client:GetTableName', function (event, boInfo) {
  switch (boInfo.databaseType) {
    case 'mysql':
      database.ExecuteQuery(boInfo, 'show tables from ' + boInfo.database, function (data) {
        if (data.err) {
          event.returnValue = data;
          return;
        }
        var val = [];
        for (var i = 0; i < data.data.length; i++) {
          for (var obj in data.data[i]) {
            val.push(data.data[i][obj]);
            break;
          }
        }
        data.data = val;
        event.returnValue = data;
      });
      break;
    case 'mssql':
      database.ExecuteQuery(boInfo, 'SELECT TABLE_NAME FROM ' + boInfo.database + '.INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = \'BASE TABLE\'', function (data) {
        if (data.err) {
          event.returnValue = data;
          return;
        }
        var val = [];
        for (var i = 0; i < data.data.recordset.length; i++) {
          val.push(data.data.recordset[i].TABLE_NAME);
        }
        data.data = val;
        event.returnValue = data;
      });
      break;
    case 'oracle':
      database.ExecuteQuery(boInfo, 'select table_name from user_tables', function (data) {
        if (data.err) {
          event.returnValue = data;
          return;
        }
        var val = [];
        for (var i = 0; i < data.data.rows.length; i++) {
          val.push(data.data.rows[i][0]);
        }
        data.data = val;
        event.returnValue = data;
      });
  }
});
ipcMain.on('client:GetColumnName', function (event, boInfo, tableName) {
  switch (boInfo.databaseType) {
    case 'mysql':
      database.ExecuteQuery(boInfo, 'show columns from ' + tableName + ' from ' + boInfo.database, function (data) {
        if (data.err) {
          event.returnValue = data;
          return;
        }
        var val = [];
        for (var i = 0; i < data.data.length; i++) {
          var obj = {};
          obj.name = data.data[i].Field;
          obj.type = data.data[i].Type;
          val.push(obj);
        }
        data.data = val;
        event.returnValue = data;
      });
      break;
    case 'mssql':
      database.ExecuteQuery(boInfo, 'SELECT* FROM ' + boInfo.database + '.INFORMATION_SCHEMA.COLUMNS WHere TABLE_NAME=\'' + tableName + '\'', function (data) {
        if (data.err) {
          event.returnValue = data;
          return;
        }
        var val = [];
        for (var i = 0; i < data.data.recordset.length; i++) {
          var obj = {};
          obj.name = data.data.recordset[i].COLUMN_NAME;
          obj.type = data.data.recordset[i].DATA_TYPE;
          val.push(obj);
        }
        data.data = val;
        event.returnValue = data;
      });
      break;
    case 'oracle':
      database.ExecuteQuery(boInfo, 'SELECT column_name, data_type FROM USER_TAB_COLUMNS WHERE table_name = \'' + tableName + '\'', function (data) {
        if (data.err) {
          event.returnValue = data;
          return;
        }
        var val = [];
        for (var i = 0; i < data.data.rows.length; i++) {
          var obj = {};
          obj.name = data.data.rows[i][0];
          obj.type = data.data.rows[i][1];
          val.push(obj);
        }
        data.data = val;
        event.returnValue = data;


      });
      break;
  }
});

ipcMain.on('client:GetParameterProcedure', function (event, boInfo, procedureName) {
  switch (boInfo.databaseType) {
    case 'mysql':
      database.ExecuteQuery(boInfo, "SELECT PARAMETER_NAME as Field ,DTD_IDENTIFIER as Type FROM information_schema.parameters  WHERE SPECIFIC_NAME = '" + procedureName + "'", function (data) {
        if (data.err) {
          event.returnValue = data;
          return;
        }
        var val = [];
        for (var i = 0; i < data.data.length; i++) {
          var obj = {};
          obj.name = data.data[i].Field;
          obj.type = data.data[i].Type;
          val.push(obj);
        }
        data.data = val;
        event.returnValue = data;
      });
      break;
    case 'mssql':
      database.ExecuteQuery(boInfo, "select parameters.name as PROCEDURE_NAME,types.name  as DATA_TYPE  from sys.parameters "
        + " inner join sys.procedures on parameters.object_id = procedures.object_id "
        + " inner join sys.types on parameters.system_type_id = types.system_type_id AND parameters.user_type_id = types.user_type_id "
        + " where procedures.name ='" + procedureName + "'"
        , function (data) {
          if (data.err) {
            event.returnValue = data;
            return;
          }
          var val = [];
          for (var i = 0; i < data.data.recordset.length; i++) {
            var obj = {};
            obj.name = data.data.recordset[i].PROCEDURE_NAME;
            obj.type = data.data.recordset[i].DATA_TYPE;
            val.push(obj);
          }
          data.data = val;
          event.returnValue = data;
        });
      break;
    case 'oracle':
      // database.ExecuteQuery(boInfo, 'SELECT column_name, data_type FROM USER_TAB_COLUMNS WHERE table_name = \'' + tableName + '\'', function (data) {
      //   if (data.err) {
      //     event.returnValue = data;
      //     return;
      //   }
      //   var val = [];
      //   for (var i = 0; i < data.data.rows.length; i++) {
      //     var obj = {};
      //     obj.name = data.data.rows[i][0];
      //     obj.type = data.data.rows[i][1];
      //     val.push(obj);
      //   }
      //   data.data = val;
      //   event.returnValue = data;


      // });
      break;
  }
});

ipcMain.on('client:GetProcudeName', function (event, boInfo) {
  switch (boInfo.databaseType) {
    case 'mysql':
      database.ExecuteQuery(boInfo, "SHOW PROCEDURE STATUS  WHERE Db = DATABASE() AND Type = 'PROCEDURE'", function (data) {
        if (data.err) {
          event.returnValue = data;
          return;
        }
        var val = [];
        for (var i = 0; i < data.data.length; i++) {
          val.push(data.data[i].Name);
        }

        data.data = val;
        event.returnValue = data;
      });
      break;
    case 'mssql':
      database.ExecuteQuery(boInfo, "SELECT name FROM sysobjects WHERE (type = 'P')", function (data) {
        if (data.err) {
          event.returnValue = data;
          return;
        }
        var val = [];
        for (var i = 0; i < data.data.recordset.length; i++) {
          val.push(data.data.recordset[i].name);
        }
        data.data = val;
        event.returnValue = data;
      });
      break;
    case 'oracle':
      // database.ExecuteQuery(boInfo, 'select table_name from user_tables', function (data) {
      //   if (data.err) {
      //     event.returnValue = data;
      //     return;
      //   }
      //   var val = [];
      //   for (var i = 0; i < data.data.rows.length; i++) {
      //     val.push(data.data.rows[i][0]);
      //   }
      //   data.data = val;
      //   event.returnValue = data;
      // });
  }
});

ipcMain.on('client:WriteHeader', function (event, attr, sheetName, values) {
  var service = require('./service/service');
  service.writeHeader(attr.token, attr.fileid, sheetName, values, function (err, result) {
    var rs = {};
    rs.err = err;
    rs.data = result;
    event.returnValue = rs;
  });
});

ipcMain.on('client:GetFileName', function (event, attr) {
  var service = require('./service/service');

  service.getFileName(app.mainWindow, attr, function (err, data) {
    var result = {};
    result.err = err;
    result.data = data;
    event.returnValue = result;

  })
});
module.exports = app;
